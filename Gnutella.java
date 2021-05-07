import java.util.Properties;
import java.io.*;
import java.util.*;

public class Gnutella {
    static String fileName;

    public static void main(String[] args) {
        int ports;
        int portserver;
        int count = 0;
        String sharedDir;
        String msgid;
        ArrayList<PeerClient> peers = new ArrayList<PeerClient>(); // To store all client threads
        try {
            int peer_id = Integer.parseInt(args[0]);
            sharedDir = System.getProperty("user.dir");
            sharedDir += "/" + args[1];
            Properties prop = new Properties();
            InputStream is = new FileInputStream("topology.txt");
            prop.load(is);
            ports = Integer.parseInt(prop.getProperty("peer" + peer_id + ".serverport"));
            FileDownload sd = new FileDownload(ports, sharedDir);
            sd.start();
            portserver = Integer.parseInt(prop.getProperty("peer" + peer_id + ".port"));
            PeerThread cs = new PeerThread(portserver, sharedDir, peer_id);
            cs.start();

            ++count;
            msgid = peer_id + "." + count;

            Thread bd = new Thread(new AnnounceJoin(peer_id, prop, msgid, sharedDir));

            Thread search = new Thread(new Search(peers, prop, sharedDir, peer_id, msgid));

            bd.start();
            search.start();

        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
