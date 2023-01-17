package twx.core.concurrency.imp;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicManager {

  // the singleton instance ...
  private static final AtomicManager SINGLETON = new AtomicManager();

  private AtomicManager() {
  };

  public static AtomicManager getInstance() {
    return SINGLETON;
  }

  private final ConcurrentMap<String, AtomicInteger> atomicMap = new ConcurrentHashMap<String, AtomicInteger>();

  public AtomicInteger getById(String id) {
    AtomicInteger atomic = this.atomicMap.get(id);
    if (atomic == null) {
      atomic = this.atomicMap.computeIfAbsent(id, k -> new AtomicInteger());
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

  public int get(String name) {
    AtomicInteger atomic = this.getById(name);
    return atomic.get();
  }

  public void set(String name, int value) {
    AtomicInteger atomic = this.getById(name);
    atomic.set(value);
  }

  public int incrementAndGet(String name) {
    AtomicInteger atomic = this.getById(name);
    return atomic.incrementAndGet();
  }

  public int decrementAndGet(String name) {
    AtomicInteger atomic = this.getById(name);
    return atomic.decrementAndGet();
  }

  public int addAndGet(String name, int delta) {
    AtomicInteger atomic = this.getById(name);
    return atomic.addAndGet(delta);
  }

  public Boolean compareAndSet(String name, int expect, int update) {
    AtomicInteger atomic = this.getById(name);
    return atomic.compareAndSet(expect, update);
  }

}
