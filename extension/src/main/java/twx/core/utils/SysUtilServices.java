package twx.core.utils;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;

import com.thingworx.common.utils.MonitoredThreadPoolExecutor;
import com.thingworx.data.util.InfoTableInstanceFactory;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.resources.Resource;
import com.thingworx.system.subsystems.eventprocessing.EventProcessingSubsystem;
import com.thingworx.system.subsystems.eventprocessing.EventRouter;
import com.thingworx.things.events.ThingworxEvent;
import com.thingworx.types.InfoTable;

public class SysUtilServices extends Resource {
    private static final long serialVersionUID = -8659764339606932258L;

    private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(SysUtilServices.class);

    // region queue Thingworx Event Queue - Utils 

    protected MonitoredThreadPoolExecutor getEventExecutor()  {
        // query the queue of EventProcessingSubsystem ... 
        EventProcessingSubsystem evSubsys = EventProcessingSubsystem.getSubsystemInstance();
        EventRouter evRouter = evSubsys.getEventRouter();
        MonitoredThreadPoolExecutor evPoolExecutor = evRouter.getWorkerPool(); 
        return evPoolExecutor;
    }

    protected BlockingQueue<Runnable> getEventQueue()  {
        MonitoredThreadPoolExecutor evPoolExecutor = this.getEventExecutor(); 
        return evPoolExecutor.getQueue();
    }

    protected BlockingQueue<Runnable> getEventQueue(ThreadPoolExecutor executor)  {
        return executor.getQueue();
    }

    @ThingworxServiceDefinition(name = "GetEventQueueMetrics", description = "Returns all entries of the event queue as Infotable", category = "TWX.EventQueue", isAllowOverride = false, aspects = {"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" })
	public InfoTable GetEventQueueMetrics() throws Exception {
        MonitoredThreadPoolExecutor evPoolExecutor = this.getEventExecutor();
        return evPoolExecutor.getThingworxMetrics();
	}

    @ThingworxServiceDefinition(name = "GetEventQueueContent", description = "Returns all entries of the event queue as Infotable", category = "TWX.EventQueue", isAllowOverride = false, aspects = {"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = { "isEntityDataShape:true" })
	public InfoTable GetEventQueueContent() throws Exception {
        // create the result Infotable ... 
        InfoTable   resulTab = InfoTableInstanceFactory.createInfoTableFromDataShape( ThingworxEvent.getDataShape() );
        
        // query the queue of EventProcessingSubsystem ... 
        BlockingQueue<Runnable> evQueue = this.getEventQueue();
        for (Runnable evInstance : evQueue) {
            // queue contains <EventInstance> which implements runnable ... 
            // but, we want the "private final ThingworxEvent event;"
            // only way to get it is reflection ...
            Class<?> evInstCls = evInstance.getClass();
            Field evFiled = evInstCls.getDeclaredField("event");
            evFiled.setAccessible(true);
            
            ThingworxEvent event = (ThingworxEvent)evFiled.get(evInstance);
            resulTab.addRow( event.toValueCollection() );
        }
		return resulTab;
	}

	@ThingworxServiceDefinition(name = "PurgeEventQueueContent", description = "Removes all events from the EventQueue", category = "TWX.EventQueue", isAllowOverride = false, aspects = { "isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "NUmber of Removed Events", baseType = "LONG", aspects = {})
	public Long PurgeEventQueueContent() {
        Long ret = (long) 0;
        // query the queue of EventProcessingSubsystem ... 
        BlockingQueue<Runnable> evq = this.getEventQueue();
        Iterator<Runnable> it = evq.iterator();
        while ( it.hasNext() ) {
            Runnable r = it.next();
            if ( r.getClass().getSimpleName() == "EventInstance" ) {
                it.remove();
                ret++;           
            }
        }
		return ret;
	}

    // endregion

}
