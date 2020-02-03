
public class GETRequest extends Request {

	public GETRequest(int port, String path, String data, String host) {
		super(port, path, data, host);
	}

	public void createRequest() {
		request = "GET " + path + "?" + data + " HTTP/1.0\r\n" + "Accept: */*\r\n" + "Host: " + host + "\r\n"
				+ "Connection: close\r\n\r\n";
	}

}
