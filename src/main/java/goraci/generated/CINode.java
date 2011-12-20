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
public class CINode extends PersistentBase {
  public static final Schema _SCHEMA = Schema.parse("{\"type\":\"record\",\"name\":\"CINode\",\"namespace\":\"org.apache.gora.continuous.generated\",\"fields\":[{\"name\":\"prev\",\"type\":\"long\",\"default\":\"-1\"},{\"name\":\"client\",\"type\":\"string\"},{\"name\":\"count\",\"type\":\"long\"}]}");
  public static enum Field {
    PREV(0,"prev"),
    CLIENT(1,"client"),
    COUNT(2,"count"),
    ;
    private int index;
    private String name;
    Field(int index, String name) {this.index=index;this.name=name;}
    public int getIndex() {return index;}
    public String getName() {return name;}
    public String toString() {return name;}
  };
  public static final String[] _ALL_FIELDS = {"prev","client","count",};
  static {
    PersistentBase.registerFields(CINode.class, _ALL_FIELDS);
  }
  private long prev;
  private Utf8 client;
  private long count;
  public CINode() {
    this(new StateManagerImpl());
  }
  public CINode(StateManager stateManager) {
    super(stateManager);
  }
  public CINode newInstance(StateManager stateManager) {
    return new CINode(stateManager);
  }
  public Schema getSchema() { return _SCHEMA; }
  public Object get(int _field) {
    switch (_field) {
    case 0: return prev;
    case 1: return client;
    case 2: return count;
    default: throw new AvroRuntimeException("Bad index");
    }
  }
  @SuppressWarnings(value="unchecked")
  public void put(int _field, Object _value) {
    if(isFieldEqual(_field, _value)) return;
    getStateManager().setDirty(this, _field);
    switch (_field) {
    case 0:prev = (Long)_value; break;
    case 1:client = (Utf8)_value; break;
    case 2:count = (Long)_value; break;
    default: throw new AvroRuntimeException("Bad index");
    }
  }
  public long getPrev() {
    return (Long) get(0);
  }
  public void setPrev(long value) {
    put(0, value);
  }
  public Utf8 getClient() {
    return (Utf8) get(1);
  }
  public void setClient(Utf8 value) {
    put(1, value);
  }
  public long getCount() {
    return (Long) get(2);
  }
  public void setCount(long value) {
    put(2, value);
  }
}
