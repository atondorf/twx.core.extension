package twx.core.concurrency.imp;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class QueueManager {

    // the singleton instance ...
    private static final QueueManager SINGLETON = new QueueManager();

    private QueueManager() {
    };

    public static QueueManager getInstance() {
        return SINGLETON;
    }

    // region queue ...
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<JSONObject>> queueMap = new ConcurrentHashMap<>();

    // region queue helpers
    public ConcurrentLinkedQueue<JSONObject> getById(String id) {
        ConcurrentLinkedQueue<JSONObject> meQueue = this.queueMap.get(id);
        if (meQueue == null) {
            meQueue = this.queueMap.computeIfAbsent(id, k -> new ConcurrentLinkedQueue<JSONObject>());
        }
        return meQueue;
    }

    public void deleteById(String id) {
        var keys = this.queueMap.keySet().iterator();
        while (keys.hasNext()) {
            String currentKey = (String) keys.next();
            if (currentKey.startsWith(id)) {
                keys.remove();
            }
        }
    }

    public void deleteAll() {
        this.queueMap.clear();
    }

    public Boolean exists(String name) {
        return this.queueMap.containsKey(name);
    }

    public void push(String name, JSONObject value) {
        ConcurrentLinkedQueue<JSONObject> q = getById(name);
        q.add(value);
    }

    public JSONObject pop(String name) {
        ConcurrentLinkedQueue<JSONObject> q = this.queueMap.get(name);
        if (q != null) {
            return q.poll();
        }
        return null;
    }

    public JSONArray popN(String name, Integer cnt) {
        ConcurrentLinkedQueue<JSONObject> q = this.queueMap.get(name);
        JSONArray outArray = new JSONArray();
        if (q == null) 
            return outArray;

        Integer qSize = q.size();
        if( cnt < 0 || cnt > qSize )
            cnt = qSize;
        if ( cnt == 0 )
            return outArray;
            
        for( int id = 0; id < cnt; id++ ) {
            outArray.put( q.poll() );
        }
        return outArray;
    }

    public JSONObject peek(String name) {
        ConcurrentLinkedQueue<JSONObject> q = this.queueMap.get(name);
        if (q != null) {
            return q.peek();
        }
        return null;
    }

    public JSONArray peekN(String name, Integer cnt) {
        ConcurrentLinkedQueue<JSONObject> q = this.queueMap.get(name);
        JSONArray outArray = new JSONArray();
        if (q == null) 
            return outArray;

        Integer qSize = q.size();
        if( cnt < 0 || cnt > qSize )
            cnt = qSize;
        if ( cnt == 0 )
            return outArray;
            
        var jsonArray = q.toArray();
        for( int id = 0; id < cnt; id++ ) {
            outArray.put( jsonArray[id] );
        }
        return outArray;
    }


    public JSONArray toArray(String name) {
        ConcurrentLinkedQueue<JSONObject> q = this.queueMap.get(name);
        JSONArray outArray = new JSONArray();
        if (q != null) {
            var queueArray = q.toArray();
            for( var item : queueArray ) {
                outArray.put(item);
            }
        }
        return outArray;
    }


    public Boolean emtpy(String name) {
        ConcurrentLinkedQueue<JSONObject> q = this.queueMap.get(name);
        if (q != null) {
            return q.isEmpty();
        }
        return true;
    }

    public Integer size(String name) {
        ConcurrentLinkedQueue<JSONObject> q = this.queueMap.get(name);
        if (q != null) {
            return q.size();
        }
        return 0;
    }

    public void clear(String name) {
        ConcurrentLinkedQueue<JSONObject> q = this.queueMap.get(name);
        if (q != null) {
            q.clear();
        }
    }
}
