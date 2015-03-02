import java.io.*;
import java.net.*;

public class WorkerThread implements Runnable {

	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private ConfigManager config;
	private DynamicContentGenerator dcg;
	private CookieMap cookiemap;

	WorkerThread(Socket clientSocket, ConfigManager config,
			DynamicContentGenerator dCG, CookieMap cookies) {
		this.setSocket(clientSocket);
		this.config = config;
		this.dcg = dCG;
		this.cookiemap = cookies;
		setStreams();
	}

	public void setStreams() {

		try {
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		BufferedReader clientRequest = new BufferedReader(
				new InputStreamReader(inputStream));
		DataOutputStream outToClient = new DataOutputStream(outputStream);
		String line = null;
		String requestLine = "";
		String[] headerFields;
		boolean keepAlive = false;
		int timeOut = 0;
		try {
			timeOut = socket.getSoTimeout();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			do {
				System.out.println(Thread.currentThread().getName() + ":"
						+ socket);
				System.out.println();
				requestLine = clientRequest.readLine();
				if (requestLine != null) {
					String Heads[] = requestLine.split("\\s+");
					if (Heads[0] != null
							&& RequestMessage.isValidMethodType(Heads[0])) {
						RequestMessage reqMsg = new RequestMessage();
						reqMsg.setRequestLine(requestLine);
						reqMsg.setMethodType(Heads[0]);
						reqMsg.setURL(Heads[1]);
						reqMsg.setVersion(Heads[2]);
						System.out.println(requestLine);
						if (!("HTTP/1.1".equalsIgnoreCase(Heads[2]) || "HTTP/1.0"
								.equalsIgnoreCase(Heads[2]))) {
							ResponseMessage badReq = generateErrorResponse(505,
									Heads[2]);
							outToClient.write(badReq.getBytes());
						} else {
							while ((line = clientRequest.readLine()) != null) {
								System.out.println(line);
								if (line.isEmpty()) {
									break;
								}

								headerFields = line.split(":", 2);
								reqMsg.addtoHeaderFileds(headerFields[0],
										headerFields[1]);

							}

							if (reqMsg.getHeaderFileds().containsKey(
									"Content-Length")) {

								int contentLength = Integer.parseInt(reqMsg
										.getHeaderFileds()
										.get("Content-Length").trim());

								if (contentLength > 0) {
									char[] cbuf = new char[contentLength];
									clientRequest.read(cbuf, 0, contentLength);
									reqMsg.setMessageBody(new String(cbuf));
									setRequestParameters(reqMsg);
									// System.out.println(reqMsg.getMessageBody());
								}

							}

							if (timeOut != 0
									&& reqMsg.getHeaderFileds().containsKey(
											"Connection")) {
								if (reqMsg.getHeaderFileds().get("Connection")
										.contains("keep-alive")) {
									keepAlive = true;
								} else {
									keepAlive = false;
								}
							}
							ResponseMessage response = null;
							if (reqMsg.getURL().contains("dynamic")) {
								String className = "";
								for (String URL : config
										.getHelperObjectsURLMapping().keySet()) {
									// System.out.println(reqMsg.getURL() +
									// " : "
									// + URL);
									if (reqMsg.getURL().contains(URL)) {
										className = config
												.getHelperObjectsClass()
												.get(config
														.getHelperObjectsURLMapping()
														.get(URL));
									}
								}
								// System.out.println("class:" + className);
								if (className == null || className.isEmpty()) {
									response = generateErrorResponse(404,
											reqMsg.getVersion());
								} else {
									Cookie cookE = null;
									if (reqMsg.getHeaderFileds().containsKey(
											"Cookie")) {
										String cookie = reqMsg
												.getHeaderFileds()
												.get("Cookie");
										String cookieId = null;
										String[] vals = cookie.split(";");

										if (vals.length > 0) {
											String[] id = vals[0].split("=");
											if (id.length > 1) {
												cookieId = id[1];
											}
										}
										// System.out.println("cid:" +
										// cookieId);
										cookE = cookiemap.getCookie(cookieId);
										// System.out.println(cookE);
									}
									if (cookE == null) {
										cookE = new Cookie(java.util.UUID
												.randomUUID().toString());
										cookiemap.addCookie(cookE.getId(),
												cookE);
									}

									reqMsg.setCookie(cookE);
									response = new ResponseMessage();
									response.setVersion(reqMsg.getVersion());
									dcg.generateDynmaicResponse(className,
											reqMsg, response);
								}
							} else {
								response = processRequest(reqMsg);
							}
							if (response == null) {
								response = new ResponseMessage(404);
							}
							if (keepAlive) {
								response.AddtoHeaderFileds("Connection",
										"Keep-Alive");
								// String timeoutStr =
								// "timeout="+(timeOut/1000);
								// response.AddtoHeaderFileds("Keep-Alive",timeoutStr);
							}
							if (reqMsg.getMethodType().equalsIgnoreCase("HEAD")) {
								outToClient.write(response.getHeaderinBytes());
							} else {
								outToClient.write(response.getBytes());
							}

							System.out.println(response.getHeader());
						}
					} else {
						ResponseMessage badReq = generateErrorResponse(400,
								"HTTP/1.1");
						outToClient.write(badReq.getBytes());
					}
				}
			} while (keepAlive);
		} catch (IOException e4) {
			// TODO Auto-generated catch block
			// e4.printStackTrace();
		}

		finally {
			try {
				// System.out.println("closing:"+socket);
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public ResponseMessage processRequest(RequestMessage req) {

		String url = req.getURL();
		String resource = "";
		if (url.startsWith("http://")) {
			if (url.contains("?")) {
				resource = url.substring(url.indexOf("/", 7),
						url.indexOf("?", 7));
			} else {
				resource = url.substring(url.indexOf("/", 7));
			}
		} else if (url.startsWith("/")) {
			if (url.contains("?")) {
				resource = url.substring(url.indexOf("/"), url.indexOf("?"));
			} else {
				resource = url.substring(url.indexOf("/"));
			}
		} else {
			return generateErrorResponse(400, req.getVersion());
		}
		resource = resource.replace('/', File.separatorChar);
		resource = resource.replace("%20", " ");
		if (resource.equalsIgnoreCase(File.separator)) {
			resource = File.separator + config.getDefaultPage();
		}
		// System.out.println("File.separator:" + File.separator);
		// System.out.println("resource:" + resource);
		String fileName = config.getRootDirectory() + resource;

		// System.out.println("File:" + fileName);
		File file = new File(fileName);
		if (file.isDirectory()) {
			fileName = config.getRootDirectory() + File.separator
					+ config.getDefaultPage();
			file = new File(fileName);
		}
		int numOfBytes = (int) file.length();
		FileInputStream inFile = null;
		BufferedInputStream is = null;
		try {
			inFile = new FileInputStream(file);
			is = new BufferedInputStream(inFile);
		} catch (FileNotFoundException e2) {
			return generateErrorResponse(404, req.getVersion());
		}

		byte[] fileInBytes = new byte[numOfBytes];
		try {

			ResponseMessage response = new ResponseMessage(200);
			response.setVersion(req.getVersion());
			String contentType = URLConnection.guessContentTypeFromStream(is);
			if (contentType == null) {
				contentType = getType(resource);
			}

			response.AddtoHeaderFileds("Content-Type", contentType);
			response.AddtoHeaderFileds("Content-Length", numOfBytes + "");
			is.read(fileInBytes);
			response.setMessageBodyinBytes(fileInBytes);
			// System.out.println(response.getHeader());
			return response;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return null;

	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	private static String getType(String fileName) {
		// ToDo add many file types
		if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
			return "text/html";
		} else if (fileName.endsWith(".mp3")) {
			return "audio/mpeg3";
		} else if (fileName.endsWith(".mp4")) {
			return "video/mp4";
		} else if (fileName.endsWith("m1v") || fileName.endsWith("m2v")) {
			return "video/mpeg";
		} else {
			return "application/octet-stream";
		}
	}

	ResponseMessage generateErrorResponse(int code, String version) {
		ResponseMessage errResp = new ResponseMessage(code);
		errResp.setVersion(version);
		byte[] entityBody = errResp.getErrorEntityBody(code).getBytes();
		errResp.setMessageBodyinBytes(entityBody);
		errResp.AddtoHeaderFileds("Content-Type", "text/html");
		errResp.AddtoHeaderFileds("Content-Length", entityBody.length + "");
		return errResp;
	}

	void setRequestParameters(RequestMessage req) {
		String messageBody = req.getMessageBody();
		if (messageBody != null) {
			messageBody = messageBody.replaceAll("\\+", " ");
			String[] params = messageBody.split("&");

			for (String param : params) {
				// System.out.println("parsing:" + param);
				String[] pmValue = param.split("=");
				if (pmValue != null && pmValue.length == 2) {
					req.addtoParameters(pmValue[0].toLowerCase(),
							pmValue[1].toLowerCase());
					// System.out.println("values:" + pmValue[0] + pmValue[1]);
				} else {
					System.out.println("parameters not in proper format:"
							+ param + " pmval:" + pmValue.length);
				}
			}
		}
	}
}
