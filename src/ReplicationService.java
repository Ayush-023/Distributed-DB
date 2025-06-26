import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class ReplicationService {
    private final PeerManager peerManager;
    private final KeyValueStore store;
    private final int W = 2, R = 2;

    public ReplicationService(PeerManager peerManager, KeyValueStore store) {
        this.peerManager = peerManager;
        this.store = store;
    }

    public boolean quorumWrite(String key, String value) {
        int ack = 0;
        for (Peer peer : peerManager.getAlivePeers()) {
            try {
                sendWriteRequest(peer, key, value);
                ack++;
            } catch (Exception ignored) {}
        }
        store.put(key, value);
        ack++;
        return ack >= W;
    }

    public String quorumRead(String key) {
        Map<String, Integer> valueCounts = new HashMap<>();
        int responses = 0;
        for (Peer peer : peerManager.getAlivePeers()) {
            try {
                String val = sendReadRequest(peer, key);
                valueCounts.put(val, valueCounts.getOrDefault(val, 0) + 1);
                responses++;
            } catch (Exception ignored) {}
        }
        String localVal = store.get(key);
        valueCounts.put(localVal, valueCounts.getOrDefault(localVal, 0) + 1);
        responses++;
        if (responses < R) return "QUORUM_FAIL";
        return valueCounts.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
    }

    private void sendWriteRequest(Peer peer, String key, String value) throws Exception {
        try (
            Socket socket = new Socket(peer.host, peer.port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
        ) {
            out.writeObject(new Message("PUT", key, value));
        }
    }

    private String sendReadRequest(Peer peer, String key) throws Exception {
        try (
            Socket socket = new Socket(peer.host, peer.port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            out.writeObject(new Message("GET", key, null));
            Message res = (Message) in.readObject();
            return res.value;
        }
    }

    public void syncFromAnyPeer() {
        for (Peer peer : peerManager.getAllPeers()) {
            try (
                Socket socket = new Socket(peer.host, peer.port);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
            ) {
                out.writeObject(new Message("GET_ALL", null, null));
                Message res = (Message) in.readObject();
                if (res.storeSnapshot != null) {
                    store.setAll(res.storeSnapshot);
                    System.out.println("Synced from peer " + peer.port);
                    break;
                }
            } catch (Exception ignored) {}
        }
    }
}