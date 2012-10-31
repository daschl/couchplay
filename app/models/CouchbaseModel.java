package models;

import com.couchbase.client.CouchbaseClient;
import datasources.Couchbase;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.spy.memcached.internal.OperationFuture;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;
/**
 * Base Model to provide most of the operations needed for all other
 * models.
 */
public class CouchbaseModel {

  /**
   * The connection to the cluster.
   */
  private CouchbaseClient connection = Couchbase.getInstance();

  /**
   * Holds the unique key inside the bucket.
   */
  @JsonIgnore
  private String key = null;


  /**
   * Create a new model and generate a unique key.
   */
  public CouchbaseModel() {
    this(null);
  }

  /**
   * Generate a model with the given key.
   * @param key The key of the model. If it is null or empty, a unique one will
   *    be created automatically.
   */
  public CouchbaseModel(String key) {
    if(key == null || key.isEmpty()) {
      key = generateKey();
    }

    this.key = key;
  }

  /**
   * Get the key for the document.
   * @return
   */
  public String getKey() {
    return key;
  }

  /**
   * Set the key for the document. A key is required.
   * @param key
   */
  public CouchbaseModel setKey(String key) {
    this.key = key;
    return this;
  }

  /**
   * Shortcut method to save and dont wait for the reply.
   * @return
   */
  public boolean save() {
    return save(false);
  }

  /**
   * Stores the JSONified object in Couchbase.
   *
   * @return whether the save was a success or not.
   */
  public boolean save(Boolean wait) {
    OperationFuture<Boolean> set = connection.set(getKey(), 0, this.toJson());
    if(wait == false) {
      return true;
    }

    boolean success;
    try {
      success = set.get();
    } catch (Exception ex) {
      throw new RuntimeException("Failed while waiting for the operation: " + ex);
    }
    return success;
  }

  /**
   * If no key is set, it will generate a UUID id.
   * @return
   */
  public final String generateKey() {
    String className = getClass().getSimpleName().toLowerCase();
    UUID uuid = UUID.randomUUID();

    return className + "-" + uuid.toString();
  }
  /**
   * Converts the current object to JSON
   * @return
   */
  public String toJson() {
    ObjectMapper mapper = new ObjectMapper();
    String result;

    try {
      result = mapper.writeValueAsString(this);
    } catch(IOException ex) {
      throw new RuntimeException("Could not convert to JSON: " + ex);
    }

    return result;
  }
}
