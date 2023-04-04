package twx.core.db.utils;

import com.thingworx.types.InfoTable;
import com.thingworx.types.TagCollection;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.primitives.BlobPrimitive;
import com.thingworx.types.primitives.BooleanPrimitive;
import com.thingworx.types.primitives.DatetimePrimitive;
import com.thingworx.types.primitives.InfoTablePrimitive;
import com.thingworx.types.primitives.IntegerPrimitive;
import com.thingworx.types.primitives.JSONPrimitive;
import com.thingworx.types.primitives.LocationPrimitive;
import com.thingworx.types.primitives.LongPrimitive;
import com.thingworx.types.primitives.NumberPrimitive;
import com.thingworx.types.primitives.StringPrimitive;
import com.thingworx.types.primitives.TagCollectionPrimitive;
import com.thingworx.types.primitives.TimespanPrimitive;
import com.thingworx.types.primitives.XMLPrimitive;
import com.thingworx.types.primitives.structs.Location;
import com.thingworx.types.primitives.structs.Timespan;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.w3c.dom.Document;

public final class ValueCollectionBuilder {
  private final ValueCollection valueCollection = new ValueCollection();
  
  public ValueCollectionBuilder put(String key, Boolean value) {
    if (value != null) {
      this.valueCollection.put(key, new BooleanPrimitive(value));
    } else {
      this.valueCollection.remove(key);
    } 
    return this;
  }
  
  public ValueCollectionBuilder put(String key, byte[] value) {
    if (value != null) {
      this.valueCollection.put(key, new BlobPrimitive(value));
    } else {
      this.valueCollection.remove(key);
    } 
    return this;
  }
  
  public ValueCollectionBuilder put(String key, DateTime value) {
    if (value != null) {
      this.valueCollection.put(key, new DatetimePrimitive(value));
    } else {
      this.valueCollection.remove(key);
    } 
    return this;
  }
  
  public ValueCollectionBuilder put(String key, InfoTable value) {
    if (value != null) {
      this.valueCollection.put(key, new InfoTablePrimitive(value));
    } else {
      this.valueCollection.remove(key);
    } 
    return this;
  }
  
  public ValueCollectionBuilder put(String key, Integer value) {
    if (value != null) {
      this.valueCollection.put(key, new IntegerPrimitive(value));
    } else {
      this.valueCollection.remove(key);
    } 
    return this;
  }
  
  public ValueCollectionBuilder put(String key, JSONObject value) {
    if (value != null) {
      this.valueCollection.put(key, new JSONPrimitive(value));
    } else {
      this.valueCollection.remove(key);
    } 
    return this;
  }
  
  public ValueCollectionBuilder put(String key, Location value) {
    if (value != null) {
      this.valueCollection.put(key, new LocationPrimitive(value));
    } else {
      this.valueCollection.remove(key);
    } 
    return this;
  }
  
  public ValueCollectionBuilder put(String key, Long value) {
    if (value != null) {
      this.valueCollection.put(key, new LongPrimitive(value));
    } else {
      this.valueCollection.remove(key);
    } 
    return this;
  }
  
  public ValueCollectionBuilder put(String key, Double value) {
    if (value != null) {
      this.valueCollection.put(key, new NumberPrimitive(value));
    } else {
      this.valueCollection.remove(key);
    } 
    return this;
  }
  
  public ValueCollectionBuilder put(String key, String value) {
    if (value != null) {
      this.valueCollection.put(key, new StringPrimitive(value));
    } else {
      this.valueCollection.remove(key);
    } 
    return this;
  }
  
  public ValueCollectionBuilder put(String key, TagCollection value) {
    if (value != null) {
      this.valueCollection.put(key, new TagCollectionPrimitive(value));
    } else {
      this.valueCollection.remove(key);
    } 
    return this;
  }
  
  public ValueCollectionBuilder put(String key, Timespan value) {
    if (value != null) {
      this.valueCollection.put(key, new TimespanPrimitive(value));
    } else {
      this.valueCollection.remove(key);
    } 
    return this;
  }
  
  public ValueCollectionBuilder put(String key, Document value) {
    if (value != null) {
      this.valueCollection.put(key, new XMLPrimitive(value));
    } else {
      this.valueCollection.remove(key);
    } 
    return this;
  }
  
  public ValueCollection get() {
    return this.valueCollection;
  }
}
