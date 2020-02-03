import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public abstract class Request extends Thread {

	public int port;
	public String path, data, host, request;
	public boolean active;

	public Request(int port, String path, String data, String host) {
		active = false;
		this.port = port;
		this.path = path;
		this.data = data;
		this.host = host;
		request = "";
		createRequest();
	}

	public abstract void createRequest();

	public void run() {
		active = true;
		try {
			// Open socket to a specific host and port
			Socket socket = new Socket(host, port);

			// Get input and output streams for the socket
			OutputStream out = socket.getOutputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			if (!request.equals("")) {
				out.write(request.getBytes());
				out.flush();
			}
			String input = "";
			while ((input = in.readLine()) != null) {
				if (input.contains("Invalid password")) {
					System.out.println(data);
				}
			}
			in.close();
			out.close();
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		active = false;
	}

	public boolean isActive() {
		return active;
	}

}
