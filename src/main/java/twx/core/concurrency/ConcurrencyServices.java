package twx.core.concurrency;

import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.resources.Resource;
import com.thingworx.things.Thing;

import org.slf4j.Logger;

import twx.core.concurrency.imp.AtomicManager;
import twx.core.concurrency.imp.MutexManager;
import twx.core.concurrency.imp.QueueManager;

import org.json.JSONObject;

public class ConcurrencyServices extends Resource {
    private static final long serialVersionUID = -3506034524403520608L;

    private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(ConcurrencyServices.class);

    private static String getThingID(Thing thing, String id/* optional */) {
        String mutexId = thing.getName();
        if (id != null) {
            if (!id.equals(""))
                mutexId = mutexId + "/" + id;
        }
        return mutexId;
    }

    // region queue Services Thingworx
    @ThingworxServiceDefinition(name = "queue_deleteAll", description = "Deletes all registered queues.", category = "Queue", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void queue_deleteAll() {
        QueueManager.getInstance().deleteAll();
    }

    @ThingworxServiceDefinition(name = "queue_delete", description = "Deletes a named queue.", category = "Queue", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void queue_delete(
            @ThingworxServiceParameter(name = "name", description = "", baseType = "STRING", aspects = {"isRequired:true" }) String name
    ) {
        QueueManager.getInstance().delete(name);
    }

    @ThingworxServiceDefinition(name = "queue_exists", description = "Checks if a named queue is created.", category = "Queue", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "BOOLEAN", aspects = {})
    public Boolean queue_exists(
            @ThingworxServiceParameter(name = "name", description = "", baseType = "STRING", aspects = {"isRequired:true" }) String name) {
        return QueueManager.getInstance().exists(name);
    }

    @ThingworxServiceDefinition(name = "queue_push", description = "Pushes a JSON to the named queue.", category = "Queue", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void queue_push(
            @ThingworxServiceParameter(name = "name", description = "", baseType = "STRING", aspects = {"isRequired:true" }) String name,
            @ThingworxServiceParameter(name = "value", description = "", baseType = "JSON", aspects = {"isRequired:true" }) JSONObject value) {
        QueueManager.getInstance().push(name, value);
    }

    @ThingworxServiceDefinition(name = "queue_pop", description = "Pop a JSON from the named queue. JSON will be removed from queue.", category = "Queue", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "JSON", aspects = {})
    public JSONObject queue_pop(
            @ThingworxServiceParameter(name = "name", description = "", baseType = "STRING", aspects = {"isRequired:true" }) String name) {
        return QueueManager.getInstance().pop(name);
    }

    @ThingworxServiceDefinition(name = "queue_peek", description = "Peeks a JSON from the named queue. JSON will remain in queue.", category = "Queue", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "JSON", aspects = {})
    public JSONObject queue_peek(
            @ThingworxServiceParameter(name = "name", description = "", baseType = "STRING", aspects = {"isRequired:true" }) String name) {
        return QueueManager.getInstance().peek(name);
    }

    @ThingworxServiceDefinition(name = "queue_isEmtpy", description = "Check if the named queue contains ", category = "Queue", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "BOOLEAN", aspects = {})
    public Boolean queue_isEmtpy(
            @ThingworxServiceParameter(name = "name", description = "", baseType = "STRING", aspects = {"isRequired:true" }) String name) {
        return QueueManager.getInstance().emtpy(name);
    }

    @ThingworxServiceDefinition(name = "queue_size", description = "", category = "Queue", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INTEGER", aspects = {})
    public Integer queue_size(
            @ThingworxServiceParameter(name = "name", description = "", baseType = "STRING", aspects = {"isRequired:true" }) String name) {
        return QueueManager.getInstance().size(name);
    }

    @ThingworxServiceDefinition(name = "queue_clear", description = "", category = "Queue", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void queue_clear(@ThingworxServiceParameter(name = "name", description = "", baseType = "STRING", aspects = {"isRequired:true" }) String name) {
        QueueManager.getInstance().clear(name);
    }
    // endregion

    // region Lock Services Thingworx
    @ThingworxServiceDefinition(name = "mtx_deleteAll", description = "Deletes all registered mutexes.", category = "Mutex", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void mtx_deleteAll() {
        MutexManager.getInstance().deleteAll();
    }

    @ThingworxServiceDefinition(name = "mtx_delete", description = "Deletes a named mutex.", category = "Mutex", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void mtx_delete(
            @ThingworxServiceParameter(name = "name", description = "", baseType = "STRING", aspects = {"isRequired:true" }) String name ) {
        MutexManager.getInstance().delete(name);
    }

    @ThingworxServiceDefinition(name = "mtx_exists", description = "Checks if a named mutex is created.", category = "Mutex", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "BOOLEAN", aspects = {})
    public Boolean mtx_exists(
            @ThingworxServiceParameter(name = "name", description = "", baseType = "STRING", aspects = {"isRequired:true" }) String name) {
        return MutexManager.getInstance().exists(name);
    }

    @ThingworxServiceDefinition(name = "mtx_lock", description = "Locks a named Lock.", category = "Mutex", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void mtx_lock(@ThingworxServiceParameter(name = "name", description = "", baseType = "STRING", aspects = {"isRequired:true" }) String name
    ) throws Exception {
        MutexManager.getInstance().lock(name);
    }

    @ThingworxServiceDefinition(name = "mtx_tryLock", description = "Tries to Lock the Lock.", category = "Mutex", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "BOOLEAN", aspects = {})
    public Boolean mtx_tryLock(
            @ThingworxServiceParameter(name = "name", description = "", baseType = "STRING", aspects = {"isRequired:true" }) String name,
            @ThingworxServiceParameter(name = "timeOut", description = "", baseType = "LONG") Long timeOut
    ) throws Exception {        
        return MutexManager.getInstance().tryLock(name,timeOut);
    }

    @ThingworxServiceDefinition(name = "mtx_unlock", description = "Unlocks the named Lock.", category = "Mutex", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void mtx_unlock(
            @ThingworxServiceParameter(name = "name", description = "", baseType = "STRING", aspects = {"isRequired:true" }) String name
    ) throws Exception {
            MutexManager.getInstance().unlock(name);
    }

    @ThingworxServiceDefinition(name = "mtx_isLocked", description = "Checks if the Lock is taken.", category = "Mutex", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "BOOLEAN", aspects = {})
    public Boolean mtx_isLocked(
            @ThingworxServiceParameter(name = "name", description = "", baseType = "STRING", aspects = {"isRequired:true" }) String name
    ) throws Exception {
        return MutexManager.getInstance().isLocked(name);
    }

    @ThingworxServiceDefinition(name = "mtx_getTotalActiveLocks", description = "", category = "Mutex", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INTEGER", aspects = {})
    public Object mtx_getTotalActiveLocks() {
        return MutexManager.getInstance().getTotalActiveLocks();
    }

    @ThingworxServiceDefinition(name = "mtx_getTotalActiveWaiting", description = "", category = "Mutex", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INTEGER", aspects = {})
    public Object mtx_getTotalActiveWaiting() {
        return MutexManager.getInstance().getTotalActiveWaiting();
    }

    @ThingworxServiceDefinition(name = "mtx_getTotalActiveLocks", description = "", category = "Mutex", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "INTEGER", aspects = {})
    public Object mtx_getTotalThingsLocksUsage() {
        return MutexManager.getInstance().getTotalThingsLocksUsage();
    }
    // endregion

    // region atomic Services Thingworx

 /*
     * Atomic Services
     */
    @ThingworxServiceDefinition(name = "atomic_deleteAll", description = "", category = "", isAllowOverride = true, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void atomic_deleteAll() {
        AtomicManager.getInstance().deleteAll();
    }

    @ThingworxServiceDefinition(name = "atomic_delete", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void atomic_delete( @ThingworxServiceParameter(name = "name", description = "", baseType = "STRING") String name) {
        AtomicManager.getInstance().delete(name);
    }

    @ThingworxServiceDefinition(name = "atomic_get", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "LONG", aspects = {})
    public Long atomic_get(@ThingworxServiceParameter(name = "name", description = "", baseType = "STRING") String name) {
        return AtomicManager.getInstance().get(name);
    }

    @ThingworxServiceDefinition(name = "atomic_set", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
    public void atomic_set(
        @ThingworxServiceParameter(name = "name", description = "", baseType = "STRING") String name,
        @ThingworxServiceParameter(name = "value", description = "", baseType = "LONG") Long value
    ) {
        AtomicManager.getInstance().set(name,value);
    }

    @ThingworxServiceDefinition(name = "atomic_incrementAndGet", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "LONG", aspects = {})
    public Long atomic_incrementAndGet(@ThingworxServiceParameter(name = "name", description = "", baseType = "STRING") String name ) {
        return AtomicManager.getInstance().incrementAndGet(name);
    }

    @ThingworxServiceDefinition(name = "atomic_decrementAndGet", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "LONG", aspects = {})
    public Long atomic_decrementAndGet(@ThingworxServiceParameter(name = "name", description = "", baseType = "STRING") String name ) {
        return AtomicManager.getInstance().decrementAndGet(name);
    }

    @ThingworxServiceDefinition(name = "atomic_addAndGet", description = "", category = "", isAllowOverride = false, aspects = {"isAsync:false" })
    @ThingworxServiceResult(name = "Result", description = "", baseType = "LONG", aspects = {})
    public Long atomic_addAndGet(
            @ThingworxServiceParameter(name = "name", description = "", baseType = "STRING") String name,
            @ThingworxServiceParameter(name = "delta", description = "", baseType = "LONG") Long delta
    ) {
        return AtomicManager.getInstance().addAndGet(name,delta);
    }
    // endregion
}
