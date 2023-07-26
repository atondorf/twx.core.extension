package twx.core.utils;

import java.lang.reflect.Field;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;

import com.thingworx.common.utils.MonitoredThreadPoolExecutor;
import com.thingworx.data.util.InfoTableInstanceFactory;
import com.thingworx.entities.utils.EntityUtilities;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.relationships.RelationshipTypes.ThingworxRelationshipTypes;
import com.thingworx.resources.Resource;
import com.thingworx.system.subsystems.Subsystem;
import com.thingworx.system.subsystems.eventprocessing.EventProcessingSubsystem;
import com.thingworx.system.subsystems.eventprocessing.EventRouter;
import com.thingworx.things.events.ThingworxEvent;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.primitives.StringPrimitive;

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
        BlockingQueue<Runnable> evq = this.getEventQueue();
        for (Runnable evInstance : evq) {
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
        try {
            Iterator<Runnable> it = evq.iterator();
            while (it.hasNext()) {
                Runnable r = it.next();
                if ( r.getClass().getSimpleName().equals("EventInstance") ) {
                    it.remove();
                    ret++;
                }
            }
        } catch (ConcurrentModificationException fallThrough) {
            // Take slow path if we encounter interference during traversal.
            // Make copy for traversal and call remove for cancelled entries.
            // The slow path is more likely to be O(N*N).
            for (Object r : evq.toArray())
                if ( r.getClass().getSimpleName().equals("EventInstance") ) {
                    evq.remove(r);
                    ret++;
                }
        }
		return ret;
	}

    /*
    @ThingworxServiceDefinition(name = "GetEventQueueContent", description = "", category = "", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "INFOTABLE", aspects = {
			"isEntityDataShape:true", "dataShape:EventProcessingSubsystemQueue" })
	public InfoTable GetEventQueueContent() throws Exception {
		_logger.trace("Entering Service: GetEventQueueContent");
		Subsystem evproc = (Subsystem) EntityUtilities.findEntity("EventProcessingSubsystem",
				ThingworxRelationshipTypes.Subsystem);
		EventProcessingSubsystem ev = EventProcessingSubsystem.getSubsystemInstance();
		Class cls = ev.getClass();
		Field workerQueue = cls.getDeclaredField("eventRouter");
		workerQueue.setAccessible(true);
		EventRouter eventRouter = (EventRouter)workerQueue.get(ev);
		Class erClass = eventRouter.getClass();
		Field mtp = erClass.getDeclaredField("workerPool");
		mtp.setAccessible(true);
		MonitoredThreadPoolExecutor mtpe = (MonitoredThreadPoolExecutor)mtp.get(eventRouter);
		BlockingQueue<Runnable> br = mtpe.getQueue();
		
		InfoTable iftbl_Status = InfoTableInstanceFactory
				.createInfoTableFromDataShape("EventProcessingSubsystemQueue");
		for (Object obj: br)
		{
			Class cls2 = obj.getClass();
			Field fld = cls2.getDeclaredField("event");
			fld.setAccessible(true);
			ThingworxEvent tve = (ThingworxEvent)fld.get(obj);
		
			ValueCollection vc = new ValueCollection();
			vc.put("eventName", new StringPrimitive(tve.getEventName()));
			vc.put("eventSource", new StringPrimitive(tve.getSource()));
			vc.put("eventSourceProperty", new StringPrimitive(tve.getSourceProperty()));
			
			iftbl_Status.addRow(vc);
		}
		
		_logger.trace("Exiting Service: GetEventQueueContent");
		return iftbl_Status;
	}

	@ThingworxServiceDefinition(name = "ClearEventQueue", description = "", category = "", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "STRING", aspects = {})
	public String ClearEventQueue() throws Exception {
		_logger.trace("Entering Service: ClearEventQueue");
//		
//		Subsystem evproc = (Subsystem) EntityUtilities.findEntity("EventProcessingSubsystem",
//				ThingworxRelationshipTypes.Subsystem);
//		EventProcessingSubsystem ev = EventProcessingSubsystem.getSubsystemInstance();
//		Class cls = ev.getClass();
//		Field workerQueue = cls.getDeclaredField("eventRouter");
//		workerQueue.setAccessible(true);
//		EventRouter eventRouter = (EventRouter)workerQueue.get(ev);
//		Class erClass = eventRouter.getClass();
//		Field mtp = erClass.getDeclaredField("workerPool");
//		mtp.setAccessible(true);
//		MonitoredThreadPoolExecutor mtpe = (MonitoredThreadPoolExecutor)mtp.get(eventRouter);
//		BlockingQueue<Runnable> br = mtpe.getQueue();
//		br.clear();
		
	
		
		_logger.trace("Exiting Service: GetEventQueueContent");
	
		_logger.trace("Exiting Service: ClearEventQueue");
		//return Integer.toString(br.size());
		return "";

	}

    // endregion

*/

}
