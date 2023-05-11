package twx.core.concurrency.imp;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ActorManagerImp {
    
  // the singleton instance ...
  private static final ActorManagerImp SINGLETON = new ActorManagerImp();

  private ActorManagerImp() {
  };

  public static ActorManagerImp getInstance() {
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
