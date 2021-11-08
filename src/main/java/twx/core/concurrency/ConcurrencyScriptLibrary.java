package twx.core.concurrency;

import org.json.JSONObject;

import com.thingworx.dsl.utils.ValueConverter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import twx.core.concurrency.imp.MutexManager;
import twx.core.concurrency.imp.QueueManager;
import twx.core.concurrency.imp.AtomicManager;

public class ConcurrencyScriptLibrary {

    // region queue Services Thingworx
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
        Boolean empty = QueueManager.getInstance().emtpy(queueName);
        return empty;
    }

    public static Object queue_size(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 1)
            throw new Exception("Invalid Number of Arguments in queue_size");
        if (!(args[0] instanceof String))
            throw new Exception("The first queue_size argument must be a string with queue name");
        String queueName = (String) args[0];
        Integer size = QueueManager.getInstance().size(queueName);
        return size;
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
    public static void mtx_lock(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 1)
            throw new Exception("Invalid Number of Arguments in lock_lock");
        if (!(args[0] instanceof String))
            throw new Exception("The first mtx_lock argument must be a string with mutex name");
        String name = (String) args[0];
        MutexManager.getInstance().lock(name);
    }

    public static Object mtx_trylock(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 2)
            throw new Exception("Invalid Number of Arguments in lock_lock");
        if (!(args[0] instanceof String))
            throw new Exception("The first queue_push argument must be a string with queue name");
        if (!(args[1] instanceof Long))
            throw new Exception("Invalid Argument Type (not an Integer) in mtx_trylock");
        String name = (String) args[0];
        Long timeOut = (Long) args[1];
        return MutexManager.getInstance().tryLock(name, timeOut);
    }

    public static void mtx_unlock(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 1)
            throw new Exception("Invalid Number of Arguments in lock_lock");
        if (!(args[0] instanceof String))
            throw new Exception("The first queue_push argument must be a string with queue name");
        String name = (String) args[0];
        MutexManager.getInstance().lock(name);;
    }

    public static Object mtx_isLocked(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
        if (args.length != 1)
            throw new Exception("Invalid Number of Arguments in lock_lock");
        if (!(args[0] instanceof String))
            throw new Exception("The first queue_push argument must be a string with queue name");
        String name = (String) args[0];
        return MutexManager.getInstance().isLocked(name);    
    }
    // endregion

    // region atomic Services Thingworx
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
            throw new Exception("Invalid Number of Arguments in atomic_get");
        if (!(args[0] instanceof String))
            throw new Exception("The first atomic_set argument must be a string with atomic name");
        if (!(args[1] instanceof Long))
            throw new Exception("The second atomic_set argument must be a long");
        String name = (String) args[0];
        Long value = (Long) args[1];
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
            throw new Exception("The first atomic_addAndGet argument must be a string with atomic name");
        if (!(args[1] instanceof Long))
            throw new Exception("The second atomic_addAndGet argument must be a long");
        String name = (String) args[0];
        Long delta = (Long) args[1];
        return AtomicManager.getInstance().addAndGet(name,delta);    
    }
    // endregion
}
