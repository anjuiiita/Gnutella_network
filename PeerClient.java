import java.net.Socket;
import java.io.IOException;
import java.io.*;

public class PeerClient extends Thread {

	int portofconnection;
	int peertoconnect;
	String filetodownload;
	Socket socket = null;
	int[] peersArray;
	Beacon beacon = new Beacon();
	String msgid;
	int frompeer_id;

	public PeerClient(int portofconnection, int peertoconnect, String filetodownload, String msgid, int frompeer_id) {
		this.portofconnection = portofconnection;
		this.peertoconnect = peertoconnect;
		this.filetodownload = filetodownload;
		this.msgid = msgid;
		this.frompeer_id = frompeer_id;
	}

	public void run() {
		try {
			socket = new Socket("localhost", portofconnection);
			OutputStream os = socket.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			InputStream is = socket.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			beacon.fname = filetodownload;
			beacon.msgId = msgid;
			beacon.fromPeerId = frompeer_id;
			oos.writeObject(beacon);
			System.out.println("PONG from " + this.peertoconnect);
			peersArray = (int[]) ois.readObject();
		} catch (IOException io) {
		} catch (ClassNotFoundException cp) {
			// System.out.println("Peer " + peertoconnect + " is not active");
		}
	}

	public int[] getarray() {
		return peersArray;
	}
}
