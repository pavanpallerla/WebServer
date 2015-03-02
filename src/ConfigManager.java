import java.io.*;
import java.util.*;

public class ConfigManager {
	File configFile;
	FileInputStream input;
	int numofThreads = 20;
	int port = 8080;
	int persistent_timeout = 0;
	Properties configProperties;
	String defaultPage = "home.html";
	String workingDir = System.getProperty("user.dir");
	String rootDirectory = System.getProperty("user.dir");
	HashMap<String, String> helperObjectsClass = new HashMap<String, String>();
	HashMap<String, String> helperObjectsURLMapping = new HashMap<String, String>();

	ConfigManager(String fileName) {
		try {
			configFile = new File(workingDir + File.separator + "resources"
					+ File.separator + fileName);
			input = new FileInputStream(configFile);
			configProperties = new Properties();
			configProperties.load(input);
			input.close();
			loadConfigProperties();
		} catch (FileNotFoundException fnf) {
			System.out
					.println("Config file not found,using default values.Dynamic helper objects might not be loaded!!!");
		} catch (IOException ioe) {
			System.out.println("IOException occured");
		}
		// helperObjectsClass.put("weather", "WeatherHelper");
		// helperObjectsURLMapping.put("weather.html", "weather");
	}

	void loadConfigProperties() {
		String root = configProperties.getProperty("root");
		if (root != null && !root.trim().isEmpty()) {
			// System.out.println(root);
			root = root.replaceAll("\"", "");
			root = root.replace('/', File.separatorChar);
			setRootDirectory(root);
		}
		String dfltPge = configProperties.getProperty("defaultPage");
		if (dfltPge != null && !dfltPge.trim().isEmpty()) {
			setDefaultPage(dfltPge);
		}
		String numThreads = configProperties.getProperty("numofThreads");
		if (numThreads != null) {
			int threads = Integer.parseInt(numThreads);
			if (threads > 0) {
				setNumofThreads(threads);
			}
		}

		String portStr = configProperties.getProperty("port");
		if (portStr != null) {
			int port = Integer.parseInt(portStr);
			if (port > 0) {
				setPort(port);
			}
		}
		String timeOutStr = configProperties.getProperty("persistent-timeout");
		if (timeOutStr != null) {
			int timeOut = Integer.parseInt(timeOutStr);
			if (timeOut > 0) {
				setPersistentTimeout(timeOut);
			}
		}
		String hoClass = configProperties.getProperty("helperObjectsClass");
		if (hoClass != null && !hoClass.trim().isEmpty()) {
			String[] pairs = hoClass.split(">");
			for (String pair : pairs) {
				String[] parts = pair.split(",");
				String key = parts[0].trim().substring(1);
				String value = parts[1].trim();
				addToHelperObjectsClass(key, value);
			}
		}
		String hoURLMapping = configProperties
				.getProperty("helperObjectURLMapping");

		if (hoURLMapping != null && !hoURLMapping.trim().isEmpty()) {
			String[] pairs = hoURLMapping.split(">");
			for (String pair : pairs) {
				String[] parts = pair.split(",");
				String key = parts[0].trim().substring(1).trim();
				String value = parts[1].trim();
				addToHelperObjectsURLMapping(key, value);
			}
		}
	}

	public String getRootDirectory() {
		return this.rootDirectory;
	}

	public void setRootDirectory(String root) {
		this.rootDirectory = root;
	}

	public String getDefaultPage() {
		return this.defaultPage;
	}

	public void setDefaultPage(String page) {
		this.defaultPage = page;
	}

	public Properties getConfigProperties() {
		return configProperties;
	}

	public String getProperty(String key) {
		return configProperties.getProperty(key);
	}

	public HashMap<String, String> getHelperObjectsClass() {
		return this.helperObjectsClass;
	}

	public void addToHelperObjectsClass(String key, String value) {
		this.helperObjectsClass.put(key, value);
	}

	public void addToHelperObjectsURLMapping(String key, String value) {
		this.helperObjectsURLMapping.put(key, value);
	}

	public HashMap<String, String> getHelperObjectsURLMapping() {
		return this.helperObjectsURLMapping;
	}

	public void setPort(int portNum) {
		if (portNum < 1 || portNum > 65535) {
			this.port = 8080;
		} else {
			this.port = portNum;
		}
	}

	public int getPort() {
		return this.port;
	}

	public void setNumofThreads(int numThreads) {
		this.numofThreads = numThreads;
	}

	public int getNumofThreads() {
		return this.numofThreads;
	}

	public void setPersistentTimeout(int timeout) {
		if (timeout < 0 || timeout > 5000) {
			timeout = 5000;
		}
		this.persistent_timeout = timeout;

	}

	public int getPersistentTimeout() {
		return this.persistent_timeout;
	}
}
