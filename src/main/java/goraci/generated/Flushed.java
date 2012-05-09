package goraci.generated;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.HashMap;
import org.apache.avro.Protocol;
import org.apache.avro.Schema;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Protocol;
import org.apache.avro.util.Utf8;
import org.apache.avro.ipc.AvroRemoteException;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificExceptionBase;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificFixed;
import org.apache.gora.persistency.StateManager;
import org.apache.gora.persistency.impl.PersistentBase;
import org.apache.gora.persistency.impl.StateManagerImpl;
import org.apache.gora.persistency.StatefulHashMap;
import org.apache.gora.persistency.ListGenericArray;

@SuppressWarnings("all")
public class Flushed extends PersistentBase {
  public static final Schema _SCHEMA = Schema.parse("{\"type\":\"record\",\"name\":\"Flushed\",\"namespace\":\"goraci.generated\",\"fields\":[{\"name\":\"count\",\"type\":\"long\"}]}");
  public static enum Field {
    COUNT(0,"count"),
    ;
    private int index;
    private String name;
    Field(int index, String name) {this.index=index;this.name=name;}
    public int getIndex() {return index;}
    public String getName() {return name;}
    public String toString() {return name;}
  };
  public static final String[] _ALL_FIELDS = {"count",};
  static {
    PersistentBase.registerFields(Flushed.class, _ALL_FIELDS);
  }
  private long count;
  public Flushed() {
    this(new StateManagerImpl());
  }
  public Flushed(StateManager stateManager) {
    super(stateManager);
  }
  public Flushed newInstance(StateManager stateManager) {
    return new Flushed(stateManager);
  }
  public Schema getSchema() { return _SCHEMA; }
  public Object get(int _field) {
    switch (_field) {
    case 0: return count;
    default: throw new AvroRuntimeException("Bad index");
    }
  }
  @SuppressWarnings(value="unchecked")
  public void put(int _field, Object _value) {
    if(isFieldEqual(_field, _value)) return;
    getStateManager().setDirty(this, _field);
    switch (_field) {
    case 0:count = (Long)_value; break;
    default: throw new AvroRuntimeException("Bad index");
    }
  }
  public long getCount() {
    return (Long) get(0);
  }
  public void setCount(long value) {
    put(0, value);
  }
}
