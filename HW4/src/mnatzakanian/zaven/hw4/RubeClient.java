package mnatzakanian.zaven.hw4;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import mnatzakanian.zaven.hw4.objects.EventMessage;
import android.util.Log;

public class RubeClient {
	private static final int DEFAULT_PORT = 4242;
	private Socket socket;
	private DataInputStream inStream;
	private DataOutputStream outStream;
	private final String ipAddress;
	private RubeInputThread receiveThread;

	public RubeClient(String ipAddress) {
		super();
		this.ipAddress = ipAddress;
	}

	public void open(EventHandler eventHandler) {
		try {
			this.socket = new Socket(getIpAddress(), DEFAULT_PORT);
			inStream = new DataInputStream(socket.getInputStream());
			outStream = new DataOutputStream(socket.getOutputStream());
			receiveThread = new RubeInputThread(eventHandler);
			receiveThread.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void close() {
		if (receiveThread != null) {
			receiveThread.interrupt();
			receiveThread = null;
		}
		closeConnections();
	}

	private void closeConnections() {
		try {
			if (inStream != null)
				inStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			inStream = null;
		}
		try {
			if (outStream != null)
				outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			outStream = null;
		}
		try {
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			socket = null;
		}
	}

	public void send(EventMessage event) throws IOException {
		outStream.writeInt(event.getX());
		outStream.writeInt(event.getY());
		outStream.writeUTF(event.getEventName());
		outStream.writeUTF(event.getDirectionName());
	}

	private class RubeInputThread extends Thread {
		private final EventHandler listener;

		public RubeInputThread(EventHandler handler) {
			super("RubeInput");
			this.listener = handler;
		}

		@Override
		public void run() {
			try {
				while (!isInterrupted()) {
					int x = inStream.readInt();
					if (x == MainActivity.STOP_CODE) {
						close();
					} else {
						int y = inStream.readInt();
						String eventName = inStream.readUTF();
						String directionName = inStream.readUTF();
						EventMessage event = new EventMessage(eventName, directionName, x, y);
						if (listener != null)
							listener.onEvent(event);
					}
				}
				closeConnections();
			} catch (ThreadDeath t) {
				throw t;
			} catch (Throwable t) {
				Log.d("RUBEINPUT", "Error reading " + ipAddress, t);
			}
		}
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}
}
