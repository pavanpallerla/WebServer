import java.util.*;

public class RequestMessage {

	static List<String> methodTypes = Arrays.asList("GET", "HEAD", "POST");
	// static List<String> methodTypes = Arrays.asList("GET", "HEAD");
	String requestline;
	String methodType;
	String url;
	String version;
	HashMap<String, String> headerFields = new HashMap<String, String>();
	HashMap<String, String> parameters = new HashMap<String, String>();
	String messageBody;
	private Cookie cookie;

	RequestMessage() {

	}

	public HashMap<String, String> getHeaderFileds() {
		return this.headerFields;
	}

	public String getRequestLine() {
		return this.requestline;
	}

	public String getMethodType() {
		return this.methodType;
	}

	public String getURL() {
		return this.url;
	}

	public String getVersion() {
		return this.version;
	}

	public String getMessageBody() {
		return this.messageBody;
	}

	public void setMessageBody(String msgBody) {
		this.messageBody = msgBody;
	}

	public void addtoHeaderFileds(String key, String value) {

		this.headerFields.put(key, value);
	}

	public void addtoParameters(String key, String value) {

		this.parameters.put(key, value);
	}

	public HashMap<String, String> getParameters() {

		return this.parameters;
	}

	public String getParameter(String param) {
		String paramVal;
		paramVal = this.parameters.get(param.toLowerCase());

		if (paramVal != null)
			return paramVal;
		else
			return "";
	}

	public void setRequestLine(String reqLine) {
		this.requestline = reqLine;
	}

	public void setMethodType(String type) {
		this.methodType = type;
	}

	public void setURL(String uri) {
		this.url = uri;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public static boolean isValidMethodType(String type) {
		if (methodTypes.contains(type.toUpperCase())) {
			return true;
		} else {
			return false;
		}
	}

	public void setCookie(Cookie cookie) {
		this.cookie = cookie;
	}

	public Cookie getCookie() {
		return this.cookie;
	}

	public void addValtoCookie(String name, String value) {
		this.cookie.addtoValues(name, value);
	}

}
