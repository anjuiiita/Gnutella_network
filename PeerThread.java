import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Properties;

public class PeerThread extends Thread {
	String FileDir;
	int port_no;
	ServerSocket serverSocket = null;
	Socket socket = null;
	int peer_id;
	static ArrayList<String> msg;

	PeerThread(int port, String SharedDir, int peer_id) {
		port_no = port;
		FileDir = SharedDir;
		this.peer_id = peer_id;
		msg = new ArrayList<String>();
	}

	public void run() {

		try {
			serverSocket = new ServerSocket(port_no);
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		while (true)
		{
			try {
				socket = serverSocket.accept();
				new Download(socket, FileDir, peer_id, msg).start();
			} catch (IOException io) {
				io.printStackTrace();
			}
		}
	}
}

class Download extends Thread {
	protected Socket socket;
	String FileDirectory;
	int port;
	String fname;
	int peer_id;
	// Peer p=new Peer();
	ArrayList<String> peermsg;
	ArrayList<Thread> thread = new ArrayList<Thread>();
	ArrayList<PeerClient> peerswithfiles = new ArrayList<PeerClient>();
	int[] peersArray_list = new int[20];
	int[] a = new int[20];
	int countofpeers = 0;
	int messageId;
	int set = 0;
	Beacon beacon = new Beacon();

	Download(Socket socket, String FileDirectory, int peer_id, ArrayList<String> peermsg) {
		this.socket = socket;
		this.FileDirectory = FileDirectory;
		this.peer_id = peer_id;
		this.peermsg = peermsg;
	}

	public void run() {
		try {
			InputStream is = socket.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			OutputStream os = socket.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);

			beacon = (Beacon) ois.readObject();

			if (beacon.fname.contains("PING")) {
				return;
			}
			this.peermsg.add(beacon.msgId);
			fname = beacon.fname;
			System.out.println("Found: " + fname);

			File newfind;
			File directoryObj = new File(FileDirectory);
			String[] filesList = directoryObj.list();
			if (filesList != null) {
				for (int j = 0; j < filesList.length; j++) {
					newfind = new File(filesList[j]);
					if (newfind.getName().equals(fname)) {
						peersArray_list[countofpeers++] = peer_id;
						break;
					}
				}
			}
			Properties prop = new Properties();
			is = new FileInputStream("topology.txt");
			prop.load(is);
			String temp = prop.getProperty("peer" + peer_id + ".next");
			if (temp != null) {
				String[] neighbours = temp.split(",");

				for (int i = 0; i < neighbours.length; i++) {
					if (beacon.fromPeerId == Integer.parseInt(neighbours[i])) {
						continue;
					}
					int connectingport = Integer.parseInt(prop.getProperty("peer" + neighbours[i] + ".port"));
					int neighbouringpeer = Integer.parseInt(neighbours[i]);
					PeerClient cp = new PeerClient(connectingport, neighbouringpeer, fname, beacon.msgId, peer_id);
					Thread t = new Thread(cp);
					t.start();
					thread.add(t);
					peerswithfiles.add(cp);

				}
			}
			// oos.writeObject(p);
			for (int i = 0; i < thread.size(); i++) {
				((Thread) thread.get(i)).join();
			}
			for (int i = 0; i < peerswithfiles.size(); i++) {
				a = ((PeerClient) peerswithfiles.get(i)).getarray();
				if (a != null) {
					for (int j = 0; j < a.length; j++) {
						if (a[j] == 0)
							break;
						peersArray_list[countofpeers++] = a[j];
					}
				}
			}
			oos.writeObject(peersArray_list);

		} catch (Exception e) {

			e.printStackTrace();

		}
	}
}