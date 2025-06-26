public class Peer {
    public String host;
    public int port;
    public boolean isAlive = true;

    public Peer(String host, int port) {
        this.host = host;
        this.port = port;
    }
}