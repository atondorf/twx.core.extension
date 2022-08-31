package twx.core.concurrency.imp;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

interface IActor {
	public static final int AVAILABLE 	= 0;
	public static final int EXECUTING 	= 1;
	public static final int ENDED		= 2;
	//! get the current status of the actor ...
	AtomicInteger getStatus();
    
	String getActorID();
	void setActorID(String actorID);
	
	ActorManager getActorManager();
	void setActorManager(ActorManager actManager);
	
	void stop();
	boolean isStopping();
	
	void addMessage(final Message message);
}

public abstract class AbstractActor implements IActor, Runnable {
	//!	
    protected static final Logger logger = LoggerFactory.getLogger(AbstractActor.class);
    //!	
    private AtomicInteger 	actStatus;
	//! name of the actor
    private String  		actID;
    //!parent actor system
    private ActorManager 	actManager;
    //!	closed flag
    private AtomicBoolean 	actStopFlag;
    //!	mailbox processing lock preventing more threads from processing messages
    private final Object 	actProcessingLock;
    //! the inbox of the actor 
    private final ConcurrentLinkedQueue<Message> mailbox;
    //! constructor
    public AbstractActor(final ActorManager actorManager, String actorID) {
    	actStatus = new AtomicInteger(AVAILABLE);
    	actProcessingLock = new Object();
    	actStopFlag = new AtomicBoolean(false);
    	mailbox = new ConcurrentLinkedQueue<Message>();
    	actManager = actorManager;
        actID = actorID;
        actManager.addActor(this);
    }
    //!
    public AtomicInteger getStatus() {
    	return actStatus;
    }
    //! 
    public String getActorID() {
    	return actID;
    }
    //! 
    public void setActorID(String actorID) {
    	this.actID = actorID;
    }
    //! returns the attached ActorSystem ... 
    public ActorManager getActorManager() {
        return actManager;
    }
    //! 
    public void setActorManager(ActorManager actorManager) {
    	actManager = actorManager;
    }
    //! stops and releases this actor from ActorManager ...
    public void stop() {
    	actStopFlag.set(true);
        actManager.scheduleActorProcessing(this);
    }

    public boolean isStopping() {
    	return actStopFlag.get();
    }
    
    public void addMessage(final Message message) {
        if (!actStopFlag.get()) {
            mailbox.add(message);
            actManager.scheduleActorProcessing(this);
        }
    }
    
    @Override
    public void run() {
    	synchronized (actProcessingLock) {
    		actStatus.set( EXECUTING );
    		final Message message = mailbox.poll();
    		if (message != null) {
            	try {
            		processMessage(message);
            		if( mailbox.size() > 0 || actStopFlag.get() ) { 
            			// still work to do .. schedule again ...
            			actManager.scheduleActorProcessing(this);
            		}
            	} catch (Exception e) {
            		logger.error("Error while processing message: " + message, e);
            	} finally {
            		Thread.yield();
                }
            } else {
            	if (actStopFlag.get()) {
            		logger.debug("Last message processed - closing actor.");
                    actManager.removeActor(this);
                    actStatus.set( ENDED );
            	}
    		}
    		actStatus.compareAndSet(EXECUTING, AVAILABLE);  		
    	}
    }
    
    protected abstract void processMessage(Message message) throws Exception;
};
