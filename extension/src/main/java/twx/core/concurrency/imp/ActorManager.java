package twx.core.concurrency.imp;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ActorManager {
    
  // the singleton instance ...
  private static final ActorManager SINGLETON = new ActorManager();

  private ActorManager() {

  };

  public static ActorManager getInstance() {
    return SINGLETON;
  }

  private final ConcurrentMap<String, ActorImp> actorMap = new ConcurrentHashMap<String, ActorImp>();

  public ActorImp get(String thingName) {
    ActorImp actr = this.actorMap.get(thingName);
    if (actr == null) {

      actr = this.actorMap.computeIfAbsent(thingName, k -> new ActorImp(thingName));
    }
    return actr;
  }

  

}
