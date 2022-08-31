package twx.core.concurrency.imp;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MutexManager {
  // the singleton instance ...
  private static final MutexManager SINGLETON = new MutexManager();

  private MutexManager() {
  };

  public static MutexManager getInstance() {
    return SINGLETON;
  }

  private final ConcurrentMap<String, ReentrantLock> mtxMap = new ConcurrentHashMap<>();
  private AtomicInteger activeLocks = new AtomicInteger(0);
  private AtomicInteger activeWaiting = new AtomicInteger(0);

  // region Lock helpers
  public void incrementLocks() {
    while (true) {
      int existingValue = activeLocks.get();
      int newValue = existingValue + 1;
      if (activeLocks.compareAndSet(existingValue, newValue)) {
        return;
      }
    }
  }

  public void decrementLocks() {
    while (true) {
      int existingValue = activeLocks.get();
      int newValue = existingValue - 1;
      if (activeLocks.compareAndSet(existingValue, newValue)) {
        return;
      }
    }
  }

  public void incrementWaiting() {
    while (true) {
      int existingValue = activeWaiting.get();
      int newValue = existingValue + 1;
      if (activeWaiting.compareAndSet(existingValue, newValue)) {
        return;
      }
    }
  }

  public void decrementWaiting() {
    while (true) {
      int existingValue = activeWaiting.get();
      int newValue = existingValue - 1;
      if (activeWaiting.compareAndSet(existingValue, newValue)) {
        return;
      }
    }
  }

  public int getTotalActiveLocks() {
    return this.activeLocks.get();
  }

  public int getTotalActiveWaiting() {
    return this.activeWaiting.get();
  }

  public int getTotalThingsLocksUsage() {
    return this.mtxMap.size();
  }

  public ReentrantLock getById(String id) {
    ReentrantLock meMtx = this.mtxMap.get(id);
    if (meMtx == null) {
      meMtx = this.mtxMap.computeIfAbsent(id, k -> new ReentrantLock(true));
    }
    return meMtx;
  }

  public void deleteById(String id) {
    var keys = this.mtxMap.keySet().iterator();
    while (keys.hasNext()) {
      String currentKey = (String) keys.next();
      if (currentKey.startsWith(id)) {
        keys.remove();
      }
    }
  }

  public void deleteAll() {
    this.mtxMap.clear();
  }

  public Boolean exists(String name) {
    return this.mtxMap.containsKey(name);
  }

  public void lock(String id) throws Exception {
    final ReentrantLock mutex = this.getById(id);
    if (mutex != null) {
      this.incrementWaiting();
      mutex.lock();
      // -- we must ensure that the lock it's returned, otherwise we must unlock here.
      try {
        this.decrementWaiting();
        this.incrementLocks();
      } catch (Exception e) {
        mutex.unlock();
        throw new Exception("Lock_ConcurrentServices/Failed to to additional steps, waiting counter maybe corrupted.");
      }
    } else {
      throw new Exception("Lock_ConcurrentServices/Cannot get instance Mutex");
    }
  }

  public Boolean tryLock(String id, Long timeOut) throws Exception {
    final ReentrantLock mutex = this.getById(id);
    if (mutex != null) {
      final Boolean result;
      Boolean incremented = false;
      if (((long) timeOut) < 0) {
        result = mutex.tryLock();
      } else {
        incremented = true;
        this.incrementWaiting();
        result = mutex.tryLock((long) timeOut, TimeUnit.MILLISECONDS);
      }

      if (result == true) {
        // -- we must ensure that the lock it's returned, otherwise we must unlock here.
        try {
          if (incremented == true)
            this.decrementWaiting();
          this.incrementLocks();
        } catch (Exception e) {
          mutex.unlock();
          throw new Exception("TryLock_ConcurrentServices/Failed to do additional steps, waiting counter maybe corrupted.");
        }
      }
      return result;
    } else {
      throw new Exception("TryLock_ConcurrentServices/Cannot get instance Mutex");
    }
  }

  public void unlock(String id) throws Exception {
    final ReentrantLock mutex = this.getById(id);
    if (mutex != null) {
      mutex.unlock();
      this.decrementLocks();
    } else {
      throw new Exception("Unlock_ConcurrentServices/Cannot get instance Mutex");
    }
  }

  public Boolean isLocked(String id) throws Exception {
    final ReentrantLock mutex = this.getById(id);
    if (mutex != null) {
      return mutex.isLocked();
    }
    return false;
  }
}
