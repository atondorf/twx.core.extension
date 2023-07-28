package twx.core.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.thingworx.common.utils.MonitoredThreadPoolExecutor;
import com.thingworx.data.util.InfoTableInstanceFactory;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.notifications.SubscriberReference;
import com.thingworx.resources.Resource;
import com.thingworx.system.subsystems.eventprocessing.EventProcessingSubsystem;
import com.thingworx.system.subsystems.eventprocessing.EventRouter;
import com.thingworx.things.events.ThingworxEvent;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.primitives.StringPrimitive;

public class SysUtilServices extends Resource {
    private static final long serialVersionUID = -8659764339606932258L;

    private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(SysUtilServices.class);

    // region queue Thingworx Event Queue - Utils

    protected EventRouter getEventRouter() {
        // query the queue of EventProcessingSubsystem ...
        EventProcessingSubsystem evSubsys = EventProcessingSubsystem.getSubsystemInstance();
        EventRouter evRouter = evSubsys.getEventRouter();
        return evRouter;
    }

    protected MonitoredThreadPoolExecutor getEventExecutor() {
        // query the queue of EventProcessingSubsystem ...
        EventProcessingSubsystem evSubsys = EventProcessingSubsystem.getSubsystemInstance();
        EventRouter evRouter = evSubsys.getEventRouter();
        MonitoredThreadPoolExecutor evPoolExecutor = evRouter.getWorkerPool();
        return evPoolExecutor;
    }

    protected BlockingQueue<Runnable> getEventQueue() {
        MonitoredThreadPoolExecutor evPoolExecutor = this.getEventExecutor();
        return evPoolExecutor.getQueue();
    }

    protected BlockingQueue<Runnable> getEventQueue(ThreadPoolExecutor executor) {
        return executor.getQueue();
    }

    @ThingworxServiceDefinition(name = "GetEventQueueMetrics", description = "Returns all entries of the event queue as Infotable", category = "TWX.EventQueue", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "JSON", aspects = {})
    public JSONObject GetEventQueueMetrics() throws Exception {
        JSONObject json = new JSONObject();
        ThreadPoolExecutor evex = this.getEventExecutor();
        BlockingQueue<Runnable> evq = evex.getQueue();

        json.put("ActiveCount", evex.getActiveCount());
        json.put("CompletedTaskCount", evex.getCompletedTaskCount());
        json.put("CorePoolSize", evex.getCorePoolSize());
        json.put("LargestPoolSize", evex.getLargestPoolSize());
        json.put("MaximumPoolSize", evex.getMaximumPoolSize());
        json.put("PoolSize", evex.getPoolSize());
        json.put("TaskCount", evex.getTaskCount());

        json.put("QueueSize", evq.size());
        json.put("QueueRemainCapacity", evq.remainingCapacity());

        return json;
    }

    @ThingworxServiceDefinition(name = "GetEventQueueContent", description = "Returns all entries of the event queue as Infotable", category = "TWX.EventQueue", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" })
    public InfoTable GetEventQueueContent() throws Exception {
        _logger.trace("Entering Service: GetEventQueueContent");
        // create the result Infotable ...
        InfoTable tab = InfoTableInstanceFactory.createInfoTableFromDataShape(ThingworxEvent.getDataShape());
        tab.addField(new FieldDefinition("SubscriberReference", BaseTypes.STRING));

        // query the queue of EventProcessingSubsystem ...
        BlockingQueue<Runnable> evq = this.getEventQueue();
        for (Runnable evInstance : evq) {
            // queue contains <EventInstance> which implements runnable ...
            // but, we want the "private final ThingworxEvent event;"
            // only way to get it is reflection ...
            Class<?> evInstanceClass = evInstance.getClass();
            Field eventField = evInstanceClass.getDeclaredField("event");
            eventField.setAccessible(true);
            Field handlerField = evInstanceClass.getDeclaredField("handler");
            handlerField.setAccessible(true);

            ThingworxEvent event = (ThingworxEvent) eventField.get(evInstance);
            SubscriberReference handler = (SubscriberReference) handlerField.get(evInstance);

            ValueCollection evVal = event.toValueCollection();
            evVal.put("SubscriberReference", new StringPrimitive(handler.toString()));
            tab.addRow(evVal);
        }

        _logger.trace("Exiting Service: GetEventQueueContent");
        return tab;
    }

    @ThingworxServiceDefinition(name = "DrainEventQueueContent", description = "Drains(Removes) all entries of the event queue to an Infotable", category = "TWX.EventQueue", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" })
    public InfoTable DrainEventQueueContent() throws Exception {
        _logger.trace("Entering Service: DrainEventQueueContent");

        // create the result Infotable ...
        InfoTable resulTab = InfoTableInstanceFactory.createInfoTableFromDataShape(ThingworxEvent.getDataShape());

        BlockingQueue<Runnable> queue = this.getEventQueue();
        List<Runnable> list = new ArrayList<Runnable>();
        queue.drainTo(list);

        // query the queue of EventProcessingSubsystem ...
        for (Runnable evInstance : list) {
            // queue contains <EventInstance> which implements runnable ...
            // but, we want the "private final ThingworxEvent event;"
            // only way to get it is reflection ...
            Class<?> evInstCls = evInstance.getClass();
            Field evFiled = evInstCls.getDeclaredField("event");
            evFiled.setAccessible(true);

            ThingworxEvent event = (ThingworxEvent) evFiled.get(evInstance);
            resulTab.addRow(event.toValueCollection());
        }

        _logger.trace("Exiting Service: DrainEventQueueContent");
        return resulTab;
    }

    @ThingworxServiceDefinition(name = "PurgeEventQueueContent", description = "Removes all events from the EventQueue", category = "TWX.EventQueue", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "Number of Removed Events", baseType = "INTEGER", aspects = {})
    public Integer PurgeEventQueueContent() throws Exception {
        // query the queue of EventProcessingSubsystem ...
        BlockingQueue<Runnable> evq = this.getEventQueue();
        Integer size = evq.size();
        evq.clear();
        return size;
    }

    @ThingworxServiceDefinition(name = "RestartEventThreadPool", description = "Tries to restart the EventRouter", category = "TWX.EventQueue", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void RestartEventThreadPool() throws Exception {
        _logger.trace("Entering Service: RestartEventThreadPool");
        EventRouter evRouter = this.getEventRouter();
        evRouter.shutdown();
        evRouter.start();
        _logger.trace("Exiting Service: RestartEventThreadPool");
    }
}
