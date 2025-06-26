import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Main <port>");
            return;
        }

        int myPort = Integer.parseInt(args[0]);

        List<Peer> peers = List.of(
            new Peer("localhost", 5001),
            new Peer("localhost", 5002),
            new Peer("localhost", 5003)
        );

        peers = peers.stream().filter(p -> p.port != myPort).toList();

        PeerManager peerManager = new PeerManager(peers);
        KeyValueStore store = new KeyValueStore();
        ReplicationService replicator = new ReplicationService(peerManager, store);
        Client client = new Client(store, replicator);

        new Thread(new Server(myPort, store)).start();
        new Thread(new HeartbeatService(peerManager)).start();
        replicator.syncFromAnyPeer();

        client.start();
    }
}