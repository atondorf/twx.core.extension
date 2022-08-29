package twx.core.concurrency.imp;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicManager {

  // the singleton instance ...
  private static final AtomicManager SINGLETON = new AtomicManager();

  private AtomicManager() {
  };

  public static AtomicManager getInstance() {
    return SINGLETON;
  }

  private final ConcurrentMap<String, AtomicLong> atomicMap = new ConcurrentHashMap<String, AtomicLong>();

  public AtomicLong getById(String id) {
    AtomicLong atomic = this.atomicMap.get(id);
    if (atomic == null) {
      atomic = this.atomicMap.computeIfAbsent(id, k -> new AtomicLong());
    }
    return atomic;
  }

  public void deleteById(String id) {
    var keys = this.atomicMap.keySet().iterator();
    while (keys.hasNext()) {
      String currentKey = (String) keys.next();
      if (currentKey.startsWith(id)) {
        keys.remove();
      }
    }
  }

  public void deleteAll() {
    this.atomicMap.clear();
  }

  public Boolean exists(String name) {
    return this.atomicMap.containsKey(name);
  }

  public long get(String name) {
    AtomicLong atomic = this.getById(name);
    return atomic.get();
  }

  public void set(String name, long value) {
    AtomicLong atomic = this.getById(name);
    atomic.set(value);
  }

  public long incrementAndGet(String name) {
    AtomicLong atomic = this.getById(name);
    return atomic.incrementAndGet();
  }

  public long decrementAndGet(String name) {
    AtomicLong atomic = this.getById(name);
    return atomic.decrementAndGet();
  }

  public long addAndGet(String name, long delta) {
    AtomicLong atomic = this.getById(name);
    return atomic.addAndGet(delta);
  }

  public Boolean compareAndSet(String name, long expect, long update) {
    AtomicLong atomic = this.getById(name);
    return atomic.compareAndSet(expect, update);
  }

}
