package twx.core.concurrency.imp;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

interface IActorManager {
	
	void shutdown() throws InterruptedException;
	
}

public class ActorManager implements IActorManager {
    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(ActorManager.class);
    /**
     * executor service for scheduling tasks
     */
    private final ExecutorService executorService;
    /**
     * collection of all actors present in this system
     */
    private final Map<String, AbstractActor> actorMap;
    
    /**
     * Creates a new instance.
     */
    public ActorManager(final int numThreads) {
    	actorMap = Collections.synchronizedMap(new HashMap<>() );
        executorService = Executors.newScheduledThreadPool(numThreads);
    }
    //!	Returns an anonymous actor handle for sending message from "nowhere".
    public AbstractActor getNobody() {
        return null;
    }
    //! return instance of named actor ... 
    public AbstractActor getActor(String name) {
    	AbstractActor actor = this.actorMap.get(name);
    	return actor;
    }
    
    
    //!
    public void tell(String actorName, Message message) {
    	AbstractActor actor = this.actorMap.get(actorName);
    	if( actor != null ) {
    		actor.addMessage(message);
    	}
    }
    //!	Adds an Actor
    public void addActor(AbstractActor actor) {
    	logger.debug("Adding actor: {}", actor);
    	synchronized (actorMap) {
    		actorMap.put(actor.getActorID(), actor );
    	}
    }
   //!	removes an Actor
   public void removeActor(final AbstractActor actor) {
	   logger.debug("Removing actor {}.", actor);
	   synchronized (actorMap) {
		   actorMap.remove( actor.getActorID() );
		   actorMap.notifyAll();
	   }
   }
   
   public void scheduleActorProcessing(final AbstractActor actor) {
	   executorService.submit(actor);
   }
   
   /**
    * Waits until all actors are closed and then shuts the system down.
    */
   public void shutdown() throws InterruptedException {
	   actorMap.forEach((k,v) -> v.stop() );
	   synchronized (actorMap) {
		   while ( !actorMap.isEmpty() ) {
			   logger.info("Waiting because there are {} more opened actor(s).", actorMap.size() );
			   actorMap.wait(100);
	       }
	   }
	   logger.info("All actors are finished.");
	   executorService.shutdownNow();
	   logger.info("Actor system shutdown successful.");      
   }
    
}
