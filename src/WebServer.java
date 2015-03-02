import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class WebServer {

	ConfigManager config;
	DynamicContentGenerator dynamicContentGen;
	String filename = "config.properties";
	ServerSocket serverSocket;
	Socket clientSocket;
	ExecutorService threadPool;
	CookieMap cookies;

	public WebServer() {
		config = new ConfigManager(filename);
		dynamicContentGen = new DynamicContentGenerator(
				config.getHelperObjectsClass());
		cookies = new CookieMap();
		initServer();
	}

	public void initServer() {
		try {
			serverSocket = new ServerSocket(config.getPort());
			threadPool = Executors.newFixedThreadPool(config.getNumofThreads());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void serverClients() {
		while (true) {
			try {
				clientSocket = serverSocket.accept();
				if (config.getPersistentTimeout() != 0) {
					clientSocket.setSoTimeout(config.getPersistentTimeout());
				}
				threadPool.execute(new WorkerThread(clientSocket, config,
						dynamicContentGen, cookies));

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public static void main(String args[]) {

		WebServer server = new WebServer();
		server.serverClients();
	}
}
