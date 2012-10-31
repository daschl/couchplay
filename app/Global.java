import play.*;

import datasources.Couchbase;

public class Global extends GlobalSettings {
	
	@Override
	public void onStart(Application app) {
		Couchbase.connect();
	}

	@Override
	public void onStop(Application app) {
		Couchbase.disconnect();
	}

}