import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CookieMap {

	Map<String, Cookie> cookieMap;

	CookieMap() {
		cookieMap = new ConcurrentHashMap<String, Cookie>();
	}

	public void addCookie(String id, Cookie cookie) {
		cookieMap.put(id, cookie);
	}

	public Cookie getCookie(String id) {
		return cookieMap.get(id);
	}

}
