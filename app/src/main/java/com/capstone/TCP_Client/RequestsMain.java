import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;

public class RequestsMain {

	public static void main(String[] args) {
		// Hostname - 192.168.0.1 or (localhost)
		String host = "";

		// Port - 80 & 443 (common)
		int port = 0;

		// Path - "/" (path to requested page)
		String path = "";

		// GET/POST data
		String data = "";

		// GET or POST
		String type = "GET";

		try {
			Socket socket;
			OutputStream out;
			BufferedReader in;

			LinkedList<Request> queue = new LinkedList<Request>();
			LinkedList<Request> active = new LinkedList<Request>();
			String request = "";
			String username = "";
			String password = "";
			socket = new Socket(host, port);
			out = socket.getOutputStream();
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));

			username = "";
			password = "";
			data = "username=" + username + "&password=" + password;
			request = "POST " + path + " HTTP/1.1\r\n" + "Accept: */*\r\n" + "Host: " + host + "\r\n"
					+ "Content-Type: application/x-www-form-urlencoded\r\n" + "Content-Length: " + data.length()
					+ "\r\n\r\n" + data;
			out.write(request.getBytes());
			out.flush();
			String input = in.readLine();
			do {
				if (input != null) {
					if (input.contains("")) {
						System.out.println(data);
					}
					input = in.readLine();
				}
			} while (input != null && input.contains("</p>"));

			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * **************For Capstone *************** while (!(type =
		 * input.next()).equals("quit")) { type = type.toUpperCase(); switch (type) {
		 * case "GET": GETRequest gr = new GETRequest(port, path, "", host);
		 * queue.add(gr); break; case "POST": POSTRequest pr = new POSTRequest(port,
		 * path, data, host); queue.add(pr); break; default:
		 * System.out.println("Invalid HTTP method"); break; } queue.poll().start(); }
		 */
	}
}

/*
 * // queue.add(new POSTRequest(out, in, port, path, data, host)); // cur = new
 * POSTRequest(out, in, port, path, data, host); // cur.start();
 * 
 * // } // } // while (!queue.isEmpty() && !active.isEmpty()) { // if
 * (!queue.isEmpty()) { // queue.peek().start(); // active.add(queue.poll()); //
 * if(cur == null) { // cur = queue.poll(); // cur.start(); // } //
 * System.out.println("QUEUE: " + queue.size()); // }
 * 
 * // if (!active.isEmpty()) { // System.out.println("ACTIVE: " +
 * active.size()); // for (Request current : active) { // if
 * (!current.isActive()) { // active.remove(current); // } // } // } // }
 */