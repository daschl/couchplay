package datasources;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.lang.Exception;
import java.util.concurrent.TimeUnit;

import play.*;
import com.couchbase.client.CouchbaseClient;

/**
 * Creates and manages connections to the Couchbase cluster
 * based on the given configuration settings.
 */
public final class Couchbase {

	/**
	 * The instance of the client to connect to.
	 */
	private static CouchbaseClient client = null;

  /**
   * Imported play configuration.
   */
  private static Configuration config = Play.application().configuration();

	/**
	 * Make the constructor private so it will never be called directly.
	 */
	private Couchbase() {}

	/**
	 * Connect to a Couchbase cluster.
	 */
	public synchronized static boolean connect() {
		String hostname = config.getString("couchbase.hostname");
    String port = config.getString("couchbase.port");
    String bucket = config.getString("couchbase.bucket");
    String password = config.getString("couchbase.password");


 		List<URI> hosts = new ArrayList<URI>();
 		hosts.add(URI.create("http://" + hostname + ":" + port + "/pools"));

 		try {
 			client = new CouchbaseClient(hosts, bucket, password);
 		} catch(Exception e) {
 			Logger.error("Error creating Couchbase client: " + e.getMessage());
 			System.exit(0);
 		}

 		return true;
	}

	/**
	 * Disconnect from a Couchbase cluster.
	 */
	public synchronized static boolean disconnect() {
		if(client == null) {
			return false;
		}

		int timeout = config.getInt("couchbase.shutdownTimeout");
		return client.shutdown(timeout, TimeUnit.SECONDS);
	}

	/**
	 * Return the client object in a safe way. If the connection was
	 * not opened previously, go ahead and create it.
	 */
	public synchronized static CouchbaseClient getInstance() {
		if(client == null) {
			connect();
		}

		return client;
	}
}