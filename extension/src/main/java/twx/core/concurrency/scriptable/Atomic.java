package twx.core.concurrency.scriptable;

import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import twx.core.concurrency.imp.AtomicManager;

public class Atomic extends ScriptableObject {
    private static final long serialVersionUID = 1L;
    private AtomicInteger  atomic = null;
    private String         atomic_id = "";

    @Override
    public String getClassName() {
        return "Atomic";
    } 
    // The zero-argument constructor used by Rhino runtime to create instances
    public Atomic() { 
        this.atomic_id = "default";
        this.atomic = AtomicManager.getInstance().getById(atomic_id);     
    };
    // @JSConstructor annotation defines the JavaScript constructor
    @JSConstructor
    public Atomic(String id) { 
        this.atomic_id = id;
        this.atomic = AtomicManager.getInstance().getById(id);
    }
    @JSFunction
    public int addAndGet(int delta) {
        return atomic.addAndGet(delta);
    }
    @JSFunction
    public boolean compareAndSet(int expect, int update) {
        return atomic.compareAndSet(expect, update);
    }
    @JSFunction
    public int decrementAndGet() {
        return atomic.decrementAndGet();
    }
    @JSFunction
    public int get() {
        return atomic.get();
    }
    @JSFunction
    public int getAndAdd(int delta) {
        return atomic.getAndAdd(delta);
    }
    @JSFunction
    public int getAndDecrement() {
        return atomic.getAndDecrement();
    }
    @JSFunction
    public int getAndIncrement() {
        return atomic.getAndIncrement();
    }
    @JSFunction
    public int getAndSet(int newVal) {
        return atomic.getAndSet(newVal);
    }
    @JSFunction
    public int incrementAndGet() {
        return atomic.incrementAndGet();
    }
    @JSFunction
    public void set(int val) {
        atomic.set(val);
    }
}

