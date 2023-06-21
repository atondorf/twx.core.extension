package twx.core.concurrency;

import org.json.JSONObject;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.thingworx.dsl.engine.DSLConverter;
import com.thingworx.dsl.utils.ValueConverter;
import com.thingworx.security.authentication.AuthenticationUtilities;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.primitives.StringPrimitive;

import twx.core.concurrency.imp.MutexManager;
import twx.core.concurrency.imp.QueueManager;
import twx.core.concurrency.imp.AtomicManager;
import twx.core.concurrency.scriptable.Atomic;
import twx.core.db.scriptable.DBConnection;

public class ConcurrencyScriptLibrary {

    // Scriptable Interface for contrsution
    // --------------------------------------------------------------------------------
    public static void require_core_concurrency(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
        AuthenticationUtilities.validateUserSecurityContext();
        // Check if the class is already registered ...
        {
            var obj = ScriptableObject.getProperty(me, "Atomic");
            if (obj == Scriptable.NOT_FOUND)
                ScriptableObject.defineClass(me, Atomic.class);
        }
    }

    protected static Long argToLong(Object arg) throws Exception {
        if( arg instanceof Double )
            return ((Double)arg).longValue();
        if( arg instanceof Integer )
            return Long.valueOf((Integer)arg);
        throw new Exception("Invalid arg is not an Number: Type is: " + arg.getClass().getName() );
    }

    protected static Integer argToInt(Object arg) throws Exception {
        if( arg instanceof Double )
            return ((Double)arg).intValue();
        if( arg instanceof Long )
            return Integer.valueOf((Integer)arg);
        if( arg instanceof Integer )
            return (Integer)arg;            
        throw new Exception("Invalid arg is not an Number: Type is: " + arg.getClass().getName() );
    }

    // region atomic Services Thingworx
    // --------------------------------------------------------------------------------
    public static Object core_getAtomic(Context cx, Scriptable me, Object[] args, Function funObj) throws Exception {
        AuthenticationUtilities.validateUserSecurityContext();
        if (args.length != 1)
            throw new Exception("Invalid Number of Arguments in queue_exists");
        DSLConverter.convertValues(args, me);
        // Check if the class is already registered ...
        var obj  = ScriptableObject.getProperty(me,"Atomic");
        if( obj == Scriptable.NOT_FOUND )
            ScriptableObject.defineClass(me, Atomic.class);
        // create and return ... 
        StringPrimitive atomicId = (StringPrimitive)BaseTypes.ConvertToPrimitive(args[0], BaseTypes.STRING);
        Object[] args_new = { atomicId.getValue() };
        return cx.newObject(me, "Atomic", args_new);
    }

    public static Boolean atomic_exists(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 1)
            throw new Exception("Invalid Number of Arguments in atomic_get");
        if (!(args[0] instanceof String))
            throw new Exception("The first atomic_get argument must be a string with atomic name");
        String name = (String) args[0];
        return AtomicManager.getInstance().exists(name);
    }

    public static void atomic_delete(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 1)
            throw new Exception("Invalid Number of Arguments in atomic_get");
        if (!(args[0] instanceof String))
            throw new Exception("The first atomic_get argument must be a string with atomic name");
        String name = (String) args[0];
        AtomicManager.getInstance().deleteById(name);    
    }

    public static Object atomic_get(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 1)
            throw new Exception("Invalid Number of Arguments in atomic_get");
        if (!(args[0] instanceof String))
            throw new Exception("The first atomic_get argument must be a string with atomic name");
        String name = (String) args[0];
        return AtomicManager.getInstance().get(name);    
    }

    public static void atomic_set(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 2)
            throw new Exception("Invalid Number of Arguments in atomic_set");
        if (!(args[0] instanceof String))
            throw new Exception("The first atomic_set argument must be a string with atomic name");
        if (!(args[1] instanceof Integer) && !(args[1] instanceof Double) )
            throw new Exception("Invalid Argument Type (not a Number) in atomic_set");
        String name = (String) args[0];
        int value = argToInt(args[1]);
        AtomicManager.getInstance().set(name,value);    
    }

    public static Object atomic_incrementAndGet(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 1)
            throw new Exception("Invalid Number of Arguments in atomic_incrementAndGet");
        if (!(args[0] instanceof String))
            throw new Exception("The first atomic_incrementAndGet argument must be a string with atomic name");
        String name = (String) args[0];
        return AtomicManager.getInstance().incrementAndGet(name);    
    }

    public static Object atomic_decrementAndGet(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 1)
            throw new Exception("Invalid Number of Arguments in atomic_decrementAndGet");
        if (!(args[0] instanceof String))
            throw new Exception("The first atomic_decrementAndGet argument must be a string with atomic name");
        String name = (String) args[0];
        return AtomicManager.getInstance().decrementAndGet(name);    
    }

    public static Object atomic_addAndGet(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 2)
            throw new Exception("Invalid Number of Arguments in atomic_addAndGet");
        if (!(args[0] instanceof String))
            throw new Exception("The first atomic_addAndGet argument must be a string with atomic name.");
        if (!(args[1] instanceof Integer) && !(args[1] instanceof Double) )
            throw new Exception("Invalid Argument Type (not a Number) in atomic_addAndGet");
        String name = (String) args[0];
        int delta = argToInt(args[1]);
        return AtomicManager.getInstance().addAndGet(name,delta);    
    }

    public static Object atomic_compareAndSet(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 3)
            throw new Exception("Invalid Number of Arguments in atomic_compareAndSet");
        if (!(args[0] instanceof String))
            throw new Exception("The first atomic_compareAndSet argument must be a string with atomic name");
        String name = (String) args[0];
        int expected = argToInt(args[1]);
        int update = argToInt(args[2]);
        return AtomicManager.getInstance().compareAndSet(name,expected,update);    
    }
    // endregion

    // region queue Services Thingworx
    // --------------------------------------------------------------------------------
    public static Boolean queue_exists(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 1)
            throw new Exception("Invalid Number of Arguments in queue_exists");
        if (!(args[0] instanceof String))
            throw new Exception("The first queue_exists argument must be a string with queue name");
        String name = (String) args[0];
        return QueueManager.getInstance().exists(name);
    }

    public static void queue_delete(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 1)
            throw new Exception("Invalid Number of Arguments in queue_delete");
        if (!(args[0] instanceof String))
            throw new Exception("The first queue_delete argument must be a string with queue name");
        String name = (String) args[0];
        QueueManager.getInstance().deleteById(name);    
    }

    public static void queue_push(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 2)
            throw new Exception("Invalid Number of Arguments in queue_push");
        if (!(args[0] instanceof String))
            throw new Exception("The first queue_push argument must be a string with queue name");
        if (!(args[1] instanceof NativeObject))
            throw new Exception("Invalid Argument Type (not a JSON object) in queue_push");
        String queueName = (String) args[0];
        JSONObject jsonObject = ValueConverter.NativeObjectToJSON((NativeObject) args[1], thisObj);
        QueueManager.getInstance().push(queueName, jsonObject);
    }

    public static Object queue_pop(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 1)
            throw new Exception("Invalid Number of Arguments in queue_pop");
        if (!(args[0] instanceof String))
            throw new Exception("The first queue_pop argument must be a string with queue name");
        String queueName = (String) args[0];
        JSONObject object = QueueManager.getInstance().pop(queueName);
        return object;
    }

    public static Object queue_peek(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 1)
            throw new Exception("Invalid Number of Arguments in queue_peek");
        if (!(args[0] instanceof String))
            throw new Exception("The first queue_peek argument must be a string with queue name");
        String queueName = (String) args[0];
        JSONObject object = QueueManager.getInstance().peek(queueName);
        return object;
    }

    public static Object queue_isEmtpy(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 1)
            throw new Exception("Invalid Number of Arguments in queue_isEmtpy");
        if (!(args[0] instanceof String))
            throw new Exception("The first queue_isEmtpy argument must be a string with queue name");
        String queueName = (String) args[0];
        return QueueManager.getInstance().emtpy(queueName);
    }

    public static Object queue_size(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 1)
            throw new Exception("Invalid Number of Arguments in queue_size");
        if (!(args[0] instanceof String))
            throw new Exception("The first queue_size argument must be a string with queue name");
        String queueName = (String) args[0];
        return QueueManager.getInstance().size(queueName);
    }

    public static void queue_clear(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 1)
            throw new Exception("Invalid Number of Arguments in queue_clear");
        if (!(args[0] instanceof String))
            throw new Exception("The first queue_clear argument must be a string with queue name");
        String queueName = (String) args[0];
        QueueManager.getInstance().clear(queueName);
    }
    // endregion

    // region Lock Services Thingworx
    // --------------------------------------------------------------------------------
    public static Object mtx_getTotalActiveLocks(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        return MutexManager.getInstance().getTotalActiveLocks();
    }

    public static Object mtx_getTotalActiveWaiting(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        return MutexManager.getInstance().getTotalActiveWaiting();
    }
    
    public static Object mtx_getTotalThingsLocksUsage(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        return MutexManager.getInstance().getTotalThingsLocksUsage();
    }

    public static Boolean mtx_exists(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 1)
            throw new Exception("Invalid Number of Arguments in mtx_exists");
        if (!(args[0] instanceof String))
            throw new Exception("The first mtx_exists argument must be a string with mutex name");
        String name = (String) args[0];
        return MutexManager.getInstance().exists(name);
    }

    public static void mtx_delete(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 1)
            throw new Exception("Invalid Number of Arguments in mtx_delete");
        if (!(args[0] instanceof String))
            throw new Exception("The first mtx_delete argument must be a string with mutex name");
        String name = (String) args[0];
        MutexManager.getInstance().deleteById(name);    
    }

    public static void mtx_lock(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 1)
            throw new Exception("Invalid Number of Arguments in mtx_lock");
        if (!(args[0] instanceof String))
            throw new Exception("The first mtx_lock argument must be a string with mutex name");
        String name = (String) args[0];
        MutexManager.getInstance().lock(name);
    }

    public static Object mtx_tryLock(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 2)
            throw new Exception("Invalid Number of Arguments in mtx_tryLock");
        if (!(args[0] instanceof String))
            throw new Exception("The first mtx_tryLock argument must be a string with mutex name");
        if (!(args[1] instanceof Integer) && !(args[1] instanceof Long) && !(args[1] instanceof Double) )
        	throw new Exception("The second mtx_tryLock argument must be a number with ms");        
        String name = (String) args[0];
        Long timeOut = argToLong(args[1]);
        return MutexManager.getInstance().tryLock(name, timeOut);
    }

    public static void mtx_unlock(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 1)
            throw new Exception("Invalid Number of Arguments in mtx_unlock");
        if (!(args[0] instanceof String))
            throw new Exception("The first mtx_unlock argument must be a string with mutex name");
        String name = (String) args[0];
        MutexManager.getInstance().unlock(name);
    }

    public static Object mtx_isLocked(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 1)
            throw new Exception("Invalid Number of Arguments in mtx_isLocked");
        if (!(args[0] instanceof String))
            throw new Exception("The first mtx_isLocked argument must be a string with mutex name");
        String name = (String) args[0];
        return MutexManager.getInstance().isLocked(name);    
    }

    public static void mtx_callLocked(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        if (args.length != 2) 
            throw new Exception("Invalid number of arguments in mtx_callLocked");
        if (!(args[0] instanceof String))
            throw new Exception("The first mtx_callLocked argument must be a string with mutex name");
        if (!(args[1] instanceof NativeFunction)) 
            throw new Exception("The second mtx_callLocked argument must be a function");
        ReentrantLock mtx = MutexManager.getInstance().getById( (String) args[0] );
        mtx.lock();
        try {
            MutexManager.getInstance().incrementLocks();
            ((NativeFunction) args[1]).call(cx, func.getParentScope(), me, new Object[] {});
        } 
        finally {
            mtx.unlock();
            MutexManager.getInstance().decrementLocks();
        } 
    }

    public static Boolean mtx_callTryLocked(Context cx, Scriptable me, Object[] args, Function func) throws Exception {
        if (args.length != 3) 
            throw new Exception("Invalid number of arguments in mtx_callLocked");
        if (!(args[0] instanceof String))
            throw new Exception("The first mtx_callLocked argument must be a string with mutex name");
        if (!(args[1] instanceof NativeFunction)) 
            throw new Exception("The second mtx_callLocked argument must be a function");
        if (!(args[2] instanceof Integer) && !(args[1] instanceof Long) && !(args[1] instanceof Double) )
            throw new Exception("The third mtx_tryLock argument must be a number with ms");  
                      
        ReentrantLock mtx = MutexManager.getInstance().getById( (String) args[0] );
        Long timeOut 		= argToLong(args[2]);
        Boolean locked 	= mtx.tryLock((long) timeOut, TimeUnit.MILLISECONDS);
        if( locked ) {
            try {
                MutexManager.getInstance().incrementLocks();
                ((NativeFunction) args[1]).call(cx, func.getParentScope(), me, new Object[] {});
            } 
            finally {
                mtx.unlock();
                MutexManager.getInstance().decrementLocks();
            } 
        }
        return locked;
    }
    // endregion

}
