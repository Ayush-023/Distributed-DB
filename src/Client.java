import java.util.Scanner;

public class Client {
    private final KeyValueStore store;
    private final ReplicationService replicator;

    public Client(KeyValueStore store, ReplicationService replicator) {
        this.store = store;
        this.replicator = replicator;
    }

    public void start() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Commands: put k v | get k | print");

        while (true) {
            String[] parts = sc.nextLine().split(" ");
            if (parts.length == 0) continue;
            switch (parts[0]) {
                case "put" -> {
                    if (parts.length == 3)
                        replicator.quorumWrite(parts[1], parts[2]);
                }
                case "get" -> {
                    if (parts.length == 2)
                        System.out.println(replicator.quorumRead(parts[1]));
                }
                case "print" -> store.printAll();
            }
        }
    }
}