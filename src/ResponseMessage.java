import java.util.*;

public class ResponseMessage {
	static String CRLF = "\r\n";
	static String SPACE = " ";
	static final Map<Integer, String> reasonPhrases = setStatusCodeReasonPhrases();
	String version;
	String statusLine;
	int statusCode;
	String statusMessage;
	HashMap<String, String> headerFields;
	String responseMsgBody;
	byte[] responseMsgBodyinBytes;

	ResponseMessage() {
		headerFields = new HashMap<String, String>();
		setStatusCode(501);
	}

	ResponseMessage(int code) {
		headerFields = new HashMap<String, String>();
		setStatusCode(code);
	}

	public HashMap<String, String> getHeaderFileds() {
		return this.headerFields;
	}

	public void setStatusLine(String statusLine) {
		this.statusLine = statusLine;
	}

	public String getStatusLine() {
		return this.statusLine;
	}

	public void setStatusCode(int code) {
		if (!reasonPhrases.containsKey(code)) {
			code = 501;
		}

		this.statusCode = code;
		setStatusMessage(getReasonPhrase(code));
	}

	public int getStatusCode() {
		return this.statusCode;
	}

	public void setStatusMessage(String msg) {
		this.statusMessage = msg;
	}

	public String getStatusMessage() {
		return this.statusMessage;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return this.version;
	}

	public void setMessageBody(String msgBody) {
		this.responseMsgBody = msgBody;
	}

	public String getMessageBody() {
		return this.responseMsgBody;
	}

	public void setMessageBodyinBytes(byte[] msgBodyByte) {
		this.responseMsgBodyinBytes = msgBodyByte;
	}

	public byte[] getMessageBodyinBytes() {
		return this.responseMsgBodyinBytes;
	}

	public void AddtoHeaderFileds(String key, String value) {

		this.headerFields.put(key, value);
	}

	static Map<Integer, String> setStatusCodeReasonPhrases() {
		HashMap<Integer, String> phrases = new HashMap<Integer, String>();
		phrases.put(100, "Continue");
		phrases.put(101, "Switching Protocols");
		phrases.put(200, "OK");
		phrases.put(201, "Created");
		phrases.put(202, "Accepted");
		phrases.put(203, "Non-Authoritative Information");
		phrases.put(204, "No Content");
		phrases.put(205, "Reset Content");
		phrases.put(206, "Partial Content");
		phrases.put(300, "Multiple Choices");
		phrases.put(301, "Moved Permanently");
		phrases.put(302, "Found");
		phrases.put(303, "See Other");
		phrases.put(304, "Not Modified");
		phrases.put(305, "Use Proxy");
		phrases.put(307, "Temporary Redirect");
		phrases.put(400, "Bad Request");
		phrases.put(401, "Unauthorized");
		phrases.put(402, "Payment Required");
		phrases.put(403, "Forbidden");
		phrases.put(404, "Not Found");
		phrases.put(405, "Method Not Allowed");
		phrases.put(406, "Not Acceptable");
		phrases.put(407, "Proxy Authentication Required");
		phrases.put(408, "Request Time-out");
		phrases.put(409, "Conflict");
		phrases.put(410, "Gone");
		phrases.put(411, "Length Required");
		phrases.put(412, "Precondition Failed");
		phrases.put(413, "Request Entity Too Large");
		phrases.put(414, "Request-URI Too Large");
		phrases.put(415, "Unsupported Media Type");
		phrases.put(416, "Requested range not satisfiable");
		phrases.put(417, "Expectation Failed");
		phrases.put(500, "Internal Server Error");
		phrases.put(501, "Not Implemented");
		phrases.put(502, "Bad Gateway");
		phrases.put(503, "Service Unavailable");
		phrases.put(504, "Gateway Time-out");
		phrases.put(505, "HTTP Version not supported");
		return Collections.unmodifiableMap(phrases);
	}

	static String getReasonPhrase(int statusCode) {
		return reasonPhrases.get(statusCode);
	}

	public String getErrorEntityBody(int code) {
		String reasonPhrase = getReasonPhrase(code);
		StringBuilder entityMsg = new StringBuilder();
		entityMsg.append("<html><head><title>Error</title></head><body><h1>");
		entityMsg.append(code);
		entityMsg.append(" ");
		entityMsg.append(reasonPhrase);
		entityMsg.append("</h1></body></html>");
		return entityMsg.toString();
	}

	public String getHeader() {
		StringBuilder header = new StringBuilder();
		header.append(getVersion());
		header.append(SPACE);
		header.append(getStatusCode());
		header.append(SPACE);
		header.append(getStatusMessage());
		header.append(CRLF);
		for (Map.Entry<String, String> entry : getHeaderFileds().entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			header.append(key);
			header.append(": ");
			header.append(value);
			header.append(CRLF);
		}
		header.append(CRLF);
		return header.toString();
	}

	public String toString() {
		return getHeader() + getMessageBody();
	}

	public byte[] getHeaderinBytes() {
		return getHeader().getBytes();
	}

	public byte[] getBytes() {

		int bodyLength = 0;
		if (getMessageBodyinBytes() != null) {
			bodyLength = getMessageBodyinBytes().length;
		}

		byte[] byteArray = new byte[getHeaderinBytes().length + bodyLength];
		System.arraycopy(getHeaderinBytes(), 0, byteArray, 0,
				getHeaderinBytes().length);
		if (getMessageBodyinBytes() != null)
			System.arraycopy(getMessageBodyinBytes(), 0, byteArray,
					getHeaderinBytes().length, bodyLength);
		return byteArray;

	}
}
