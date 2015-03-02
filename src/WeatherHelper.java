public class WeatherHelper implements Helper {

	@Override
	public void generateDynmaicResponse(String className, RequestMessage req,
			ResponseMessage resp) {
		Cookie cookiE = req.getCookie();
		if (req.headerFields.containsKey("Cookie")) {
			String cookieId = req.headerFields.get("Cookie");
			// System.out.println("cookieid in weathe app:" + cookieId);

			String name = cookiE.getValues().get("name");
			String zipcode = cookiE.getValues().get("zipcode");

			String name1 = req.getParameter("name");
			String zipcode1 = req.getParameter("zipcode");
			if (name1 != null && !name1.isEmpty()
					&& !name1.equalsIgnoreCase(name)) {
				name = name1;
			}
			if (zipcode1 != null && !zipcode1.isEmpty()
					&& !zipcode1.equalsIgnoreCase(zipcode)) {
				zipcode = zipcode1;
			}

			if (zipcode == null || zipcode.trim().isEmpty()
					|| !zipcode.matches("[0-9]{5}")) {
				String body = getPageforNewUser();
				resp.setStatusCode(200);
				resp.setMessageBodyinBytes(body.getBytes());
				resp.setMessageBody(body);
			} else {
				String body = getDynamicMessage(name, zipcode);
				byte[] bodyinBytes = body.getBytes();
				int numOfBytes = bodyinBytes.length;
				resp.setStatusCode(200);
				resp.AddtoHeaderFileds("Content-Type", "text/html");
				resp.AddtoHeaderFileds("Content-Length", numOfBytes + "");

				String values = "name=" + cookiE.getId();
				resp.AddtoHeaderFileds("Set-Cookie", values);
				cookiE.addtoValues("name", name);
				cookiE.addtoValues("zipcode", zipcode);
				resp.setMessageBodyinBytes(bodyinBytes);
				resp.setMessageBody(body);
			}

		} else {

			if (req.getParameters() != null && req.getParameters().size() > 0) {
				String name = req.getParameter("name");
				String zipcode = req.getParameter("zipcode");

				cookiE.addtoValues("name", name);
				cookiE.addtoValues("zipcode", zipcode);
				String body = getDynamicMessage(name, zipcode);
				byte[] bodyinBytes = body.getBytes();
				int numOfBytes = bodyinBytes.length;
				resp.setStatusCode(200);
				resp.AddtoHeaderFileds("Content-Type", "text/html");
				resp.AddtoHeaderFileds("Content-Length", numOfBytes + "");

				String values = "name=" + cookiE.getId();
				resp.AddtoHeaderFileds("Set-Cookie", values);
				resp.setMessageBodyinBytes(bodyinBytes);
				resp.setMessageBody(body);
			} else {
				String body = getPageforNewUser();
				resp.setStatusCode(200);
				resp.setMessageBodyinBytes(body.getBytes());
				resp.setMessageBody(body);
			}

		}

	}

	String getPageforNewUser() {
		return new String(
				"<html> <head> <title>Weather</title></head><body><p>Enter your zipcode to get your weather info.</p><form method=\"post\">Name:<br><input type=\"text\" name=\"name\" required><br>Zip Code:<br><input type=\"text\" name=\"zipCode\" required><input type=\"submit\" value=\"Submit\"></form></body></html>");

	}

	String getDynamicMessage(String name, String zipCode) {
		StringBuilder sb = new StringBuilder();
		String weatherMsg = "";
		if (name == null || name.trim().isEmpty()) {
			name = "Guest";
		}

		if (zipCode.matches("[0-9]{5}")) {
			if (zipCode.matches("146[0-9][0-9]")) {
				weatherMsg = "Well.It's December and you are in Rochester!!!";
			} else {
				weatherMsg = "I bet it is better than Rochester!!!";
			}

		} else {
			weatherMsg = " Invalid ZipCode!!!";
		}

		sb.append("<html> <head> <title>Weather Info</title></head><body><h1>Welcome ");
		sb.append(name);
		sb.append(" !!!</h1><p> ");
		sb.append(weatherMsg);
		if (weatherMsg.contains("Invalid"))
			sb.append("<a  href=\"/dynamic/weather.html\">Click here to go back</a>");
		sb.append("</p></body></html>");
		return sb.toString();
	}
}
