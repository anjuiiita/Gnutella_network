import java.util.*;
import java.util.concurrent.TimeUnit;

public class AnnounceJoin extends Thread {
    int peer_id;
    Properties prop;
    String msgid;
    String SharedDir;

    AnnounceJoin(int peer_id, Properties prop, String msgid, String sharedDir) {
        this.peer_id = peer_id;
        this.prop = prop;
        this.msgid = msgid;
        this.SharedDir = sharedDir;
    }

    public void run() {
        try {
            String[] neighbours = this.prop.getProperty("peer" + this.peer_id + ".next").split(",");

            while (true) {
                for (int i = 0; i < neighbours.length; i++) {
                    int connectingport = Integer.parseInt(prop.getProperty("peer" + neighbours[i] + ".port"));
                    int neighbouringpeer = Integer.parseInt(neighbours[i]);
                    try {
                        PeerClient cp = new PeerClient(connectingport, neighbouringpeer, "PING", msgid, peer_id);
                        cp.start();

                    } catch (Exception e) {
                        System.out.println("Exception");
                    }
                }
                TimeUnit.SECONDS.sleep(20);
            }

        } catch (Exception io) {
            io.printStackTrace();
        }
    }
}
