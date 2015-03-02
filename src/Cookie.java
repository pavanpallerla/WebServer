import java.util.*;
import java.util.concurrent.*;

public class Cookie {

	String id;
	Map<String, String> values;

	Cookie(String id) {
		this.id = id;
		values = new ConcurrentHashMap<String, String>();
	}

	void setId(String id) {
		this.id = id;
	}

	String getId() {
		return this.id;
	}

	Map<String, String> getValues() {
		return this.values;
	}

	void addtoValues(String key, String value) {
		this.values.put(key, value);
	}

}
