package twx.core.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.thingworx.common.utils.MonitoredThreadPoolExecutor;
import com.thingworx.data.util.InfoTableInstanceFactory;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
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
        EventProcessingSubsystem evSubSys = EventProcessingSubsystem.getSubsystemInstance();
        EventRouter evRouter = evSubSys.getEventRouter();
        return evRouter;
    }

    protected MonitoredThreadPoolExecutor getEventExecutor() {
        EventRouter evRouter = this.getEventRouter();
        MonitoredThreadPoolExecutor evPoolExecutor = evRouter.getWorkerPool();
        return evPoolExecutor;
    }

    protected BlockingQueue<Runnable> getEventQueue() {
        MonitoredThreadPoolExecutor evPoolExecutor = this.getEventExecutor();
        return evPoolExecutor.getQueue();
    }

    protected InfoTable getInfotableFromEvents(Collection<Runnable> events) throws Exception {
        // create the result Infotable ...
        InfoTable evTab = InfoTableInstanceFactory.createInfoTableFromDataShape(ThingworxEvent.getDataShape());
        evTab.addField(new FieldDefinition("SubscriberReference", BaseTypes.STRING));

        for (Runnable evInstance : events) {
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
            evTab.addRow(evVal);
        }
        return evTab;
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

        BlockingQueue<Runnable> evQueue = this.getEventQueue();
        InfoTable evTab = this.getInfotableFromEvents(evQueue);

        _logger.trace("Exiting Service: GetEventQueueContent");
        return evTab;
    }

    @ThingworxServiceDefinition(name = "DrainEventQueueContent", description = "Drains(Removes) all entries of the event queue to an Infotable", category = "TWX.EventQueue", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "Infotable of Drainerd Events", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" })
    public InfoTable DrainEventQueueContent() throws Exception {
        _logger.trace("Entering Service: DrainEventQueueContent");

        // query the queue of EventProcessingSubsystem ...
        BlockingQueue<Runnable> evQueue = this.getEventQueue();
        List<Runnable> evList = new ArrayList<Runnable>();
        evQueue.drainTo(evList);
        InfoTable evTab = this.getInfotableFromEvents(evList);

        _logger.trace("Exiting Service: DrainEventQueueContent");
        return evTab;
    }

    @ThingworxServiceDefinition(name = "PurgeEventQueueContent", description = "Removes all events from the EventQueue", category = "TWX.EventQueue", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "Number of Removed Events", baseType = "INTEGER", aspects = {})
    public Integer PurgeEventQueueContent() throws Exception {
        _logger.trace("Entering Service: PurgeEventQueueContent");
        // query the queue of EventProcessingSubsystem ...
        BlockingQueue<Runnable> evQueue = this.getEventQueue();
        List<Runnable> evList = new ArrayList<Runnable>();
        evQueue.drainTo(evList);
        _logger.trace("Exiting Service: PurgeEventQueueContent");
        return evList.size();
    }

    @ThingworxServiceDefinition(name = "FilterEventQueueByEventName", description = "Removes all events of specified type from the EventQueue", category = "TWX.EventQueue", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "Number of Removed Events", baseType = "INTEGER", aspects = {})
    public Integer FilterEventQueueByEventName(
            @ThingworxServiceParameter(name = "eventName", description = "", baseType = "STRING") String eventName) throws Exception {
        _logger.trace("Entering Service: FilterEventQueueContent");
        Integer cnt = 0;

        BlockingQueue<Runnable> evQueue = this.getEventQueue();
        List<Runnable> evList = new ArrayList<Runnable>();
        evQueue.drainTo(evList);

        // iterate Runnable List and check for matching Events ...
        Iterator<Runnable> it = evList.iterator();
        while (it.hasNext()) {
            Runnable evInstance = it.next();

            // queue contains <EventInstance> which implements runnable ...
            // but, we want the "private final ThingworxEvent event;"
            // only way to get it is reflection ...
            Class<?> evInstanceClass = evInstance.getClass();
            Field eventField = evInstanceClass.getDeclaredField("event");
            eventField.setAccessible(true);
            ThingworxEvent event = (ThingworxEvent) eventField.get(evInstance);
            if (event != null && event.getEventName().equals(eventName)) {
                it.remove();
                cnt++;
            }
        }

        // added the remaining events back to Worker Pool ... 
        MonitoredThreadPoolExecutor evExec = this.getEventExecutor();
        for (Runnable evInstance : evList) {
            evExec.execute(evInstance);
        }

        _logger.trace("Exiting Service: FilterEventQueueContent");
        return cnt;
    }

    @ThingworxServiceDefinition(name = "FilterEventQueueBySourceName", description = "Removes all events of specified type from the EventQueue", category = "TWX.EventQueue", isAllowOverride = false, aspects = { "isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "Number of Removed Events", baseType = "INTEGER", aspects = {})
    public Integer FilterEventQueueBySourceName(
            @ThingworxServiceParameter(name = "sourceName", description = "", baseType = "STRING") String sourceName) throws Exception {
        _logger.trace("Entering Service: FilterEventQueueContent");
        Integer cnt = 0;

        BlockingQueue<Runnable> evQueue = this.getEventQueue();
        List<Runnable> evList = new ArrayList<Runnable>();
        evQueue.drainTo(evList);

        // iterate Runnable List and check for matching Events ...
        Iterator<Runnable> it = evList.iterator();
        while (it.hasNext()) {
            Runnable evInstance = it.next();

            // queue contains <EventInstance> which implements runnable ...
            // but, we want the "private final ThingworxEvent event;"
            // only way to get it is reflection ...
            Class<?> evInstanceClass = evInstance.getClass();
            Field eventField = evInstanceClass.getDeclaredField("event");
            eventField.setAccessible(true);
            ThingworxEvent event = (ThingworxEvent) eventField.get(evInstance);
            if (event != null && event.getSource().equals(sourceName)) {
                it.remove();
                cnt++;
            }
        }

        // added the remaining events back to Worker Pool ... 
        MonitoredThreadPoolExecutor evExec = this.getEventExecutor();
        for (Runnable evInstance : evList) {
            evExec.execute(evInstance);
        }

        _logger.trace("Exiting Service: FilterEventQueueContent");
        return cnt;
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
