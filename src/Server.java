import java.io.*;
import java.net.*;

public class Server implements Runnable {
    private final int port;
    private final KeyValueStore store;

    public Server(int port, KeyValueStore store) {
        this.port = port;
        this.store = store;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handle(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handle(Socket socket) {
        try (
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
        ) {
            Message msg = (Message) in.readObject();
            if ("PUT".equals(msg.type)) {
                store.put(msg.key, msg.value);
            } else if ("GET".equals(msg.type)) {
                out.writeObject(new Message("RESULT", msg.key, store.get(msg.key)));
            } else if ("GET_ALL".equals(msg.type)) {
                Message reply = new Message("STORE_SNAPSHOT", null, null);
                reply.storeSnapshot = store.getAll();
                out.writeObject(reply);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}