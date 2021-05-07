import java.net.*;
import java.util.Properties;
import java.io.*;
import java.util.*;

public class Search extends Thread {
	ArrayList<PeerClient> peers;
	Properties prop;
	String sharedDir;
	int peer_id;
	String msgid;

	Search(ArrayList<PeerClient> peers, Properties prop, String sharedDir, int peer_id, String msgid) {
		this.peers = peers;
		this.prop = prop;
		this.sharedDir = sharedDir;
		this.peer_id = peer_id;
		this.msgid = msgid;
	}

	public void run() {
		try {
			System.out.println("Enter the file to be downloaded");
			Scanner scan = new Scanner(System.in);
			String f_name = scan.nextLine();
			ArrayList<Thread> thread = new ArrayList<Thread>();
			String[] neighbours = this.prop.getProperty("peer" + this.peer_id + ".next").split(",");
			for (int i = 0; i < neighbours.length; i++) {
				int connectingport = Integer.parseInt(prop.getProperty("peer" + neighbours[i] + ".port"));
				int neighbouringpeer = Integer.parseInt(neighbours[i]);
				try {
					PeerClient cp = new PeerClient(connectingport, neighbouringpeer, f_name, msgid, peer_id);
					Thread t = new Thread(cp);
					t.start();
					thread.add(t);
					peers.add(cp);
				} catch (Exception e) {
					System.out.println("Exception");
				}
			}
			for (int i = 0; i < thread.size(); i++) {
				try {
					((Thread) thread.get(i)).join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			int[] peerswithfiles;// part on how to send data from the ConnectingPeer
			boolean flag = false;
			System.out.println("Peers containing the file are: ");
			for (int i = 0; i < peers.size(); i++) {
				peerswithfiles = ((PeerClient) peers.get(i)).getarray();
				if (peerswithfiles != null) {
					for (int j = 0; j < peerswithfiles.length; j++) {
						if (peerswithfiles[j] == 0)
							break;
						System.out.println(peerswithfiles[j]);
						flag = true;
					}
				}

			}
			if (flag) {
				System.out.println("Enter the peer from where to download the file: ");
				int peerfromdownload = scan.nextInt();
				int porttodownload = Integer.parseInt(prop.getProperty("peer" + peerfromdownload + ".serverport"));
				PeerServer(peerfromdownload, porttodownload, f_name, sharedDir);
				System.out.println(
						"File: " + f_name + " downloaded from Peer " + peerfromdownload + " to Peer " + peer_id);

			}
			scan.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void PeerServer(int cspeerid, int csportno, String filename, String sharedDir) {
		try {
			Socket peerServersocket = new Socket("localhost", csportno);
			ObjectOutputStream ooos = new ObjectOutputStream(peerServersocket.getOutputStream());
			ooos.flush();
			ObjectInputStream oois = new ObjectInputStream(peerServersocket.getInputStream());
			ooos.writeObject(filename);
			int readbytes = (int) oois.readObject();

			System.out.println("bytes transferred: " + readbytes);
			byte[] b = new byte[readbytes];
			oois.readFully(b);
			OutputStream fileos = new FileOutputStream(sharedDir + "//" + filename);
			BufferedOutputStream bos = new BufferedOutputStream(fileos);
			bos.write(b, 0, (int) readbytes);
			System.out.println(filename + " file has be downloaded to your directory " + sharedDir);
			bos.flush();
			peerServersocket.close();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
