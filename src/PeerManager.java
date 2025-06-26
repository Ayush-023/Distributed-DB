import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PeerManager {
    private final CopyOnWriteArrayList<Peer> peers;

    public PeerManager(List<Peer> peers) {
        this.peers = new CopyOnWriteArrayList<>(peers);
    }

    public List<Peer> getAlivePeers() {
        return peers.stream().filter(p -> p.isAlive).toList();
    }

    public List<Peer> getAllPeers() {
        return peers;
    }

    public void markDead(Peer peer) {
        peer.isAlive = false;
    }

    public void markAlive(Peer peer) {
        peer.isAlive = true;
    }
}