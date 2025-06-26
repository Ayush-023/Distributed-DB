import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class HeartbeatService implements Runnable {
    private final PeerManager peerManager;

    public HeartbeatService(PeerManager peerManager) {
        this.peerManager = peerManager;
    }

    public void run() {
        while (true) {
            for (Peer peer : peerManager.getAllPeers()) {
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress(peer.host, peer.port), 1000);
                    peerManager.markAlive(peer);
                } catch (IOException e) {
                    peerManager.markDead(peer);
                    System.out.println("Peer down: " + peer.port);
                }
            }
            try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
        }
    }
}