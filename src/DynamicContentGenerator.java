import java.util.*;

public class DynamicContentGenerator {

	Map<String, Helper> helperObjects = new HashMap<String, Helper>();

	DynamicContentGenerator(Map<String, String> helpers) {
		loadHelperObjects(helpers);
	}

	void loadHelperObjects(Map<String, String> helpers) {
		for (String helper : helpers.values()) {
			// System.out.println("loading:"+helper);
			Helper helperObject;
			try {
				helperObject = (Helper) Class.forName(helper).newInstance();
				helperObjects.put(helper, helperObject);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("ClassNotFoundException in loading "
						+ helper + "java class.");
				continue;
			}

		}
	}

	void generateDynmaicResponse(String className, RequestMessage req,
			ResponseMessage resp) {

		Helper helper = helperObjects.get(className);

		if (helper != null) {

			helper.generateDynmaicResponse(className, req, resp);
		} else {
			resp.setStatusCode(404);
			byte[] entityBody = resp.getErrorEntityBody(404).getBytes();
			resp.setMessageBodyinBytes(entityBody);
			resp.AddtoHeaderFileds("Content-Type", "text/html");
			resp.AddtoHeaderFileds("Content-Length", entityBody.length + "");
		}

	}
}
