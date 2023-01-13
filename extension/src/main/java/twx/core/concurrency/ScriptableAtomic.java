package twx.core.concurrency;



import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;
import java.util.concurrent.atomic.AtomicLong;
import twx.core.concurrency.imp.AtomicManager;

public class ScriptableAtomic extends ScriptableObject {
    private static final long serialVersionUID = 1L;
    private AtomicLong  atomic = null;
    private String      atomic_id = "";

    @Override
    public String getClassName() {
        return "ScriptableAtomic";
    } 
    // The zero-argument constructor used by Rhino runtime to create instances
    public ScriptableAtomic() { 
        this.atomic_id = "default";
        this.atomic = AtomicManager.getInstance().getById(atomic_id);     
    };
    // @JSConstructor annotation defines the JavaScript constructor
    @JSConstructor
    public ScriptableAtomic(String id) { 
        this.atomic_id = id;
        this.atomic = AtomicManager.getInstance().getById(id);
    }
    @JSFunction
    public long addAndGet(long delta) {
        return atomic.addAndGet(delta);
    }
    @JSFunction
    public Boolean compareAndSet(long expect, long update) {
        return atomic.compareAndSet(expect, update);
    }
    @JSFunction
    public long decrementAndGet() {
        return atomic.decrementAndGet();
    }
    @JSFunction
    public Long get() {
        return atomic.get();
    }
    @JSFunction
    public Long getAndAdd(long delta) {
        return atomic.getAndAdd(delta);
    }
    @JSFunction
    public Long getAndDecrement() {
        return atomic.getAndDecrement();
    }
    @JSFunction
    public Long getAndIncrement() {
        return atomic.getAndIncrement();
    }
    @JSFunction
    public Long getAndSet(long newVal) {
        return atomic.getAndSet(newVal);
    }
    @JSFunction
    public Long incrementAndGet() {
        return atomic.incrementAndGet();
    }
    @JSFunction
    public void set(Long val) {
        atomic.set(val);
    }
}

