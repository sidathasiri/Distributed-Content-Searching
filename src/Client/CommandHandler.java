package Client;

import java.io.IOException;

public class CommandHandler {
    private Node node;

    public CommandHandler(Node node){
        this.node = node;
    }

    public void execute(String command){
        switch (command){
            case "SHOW ROUTING TABLE":
                node.showRoutingTable();
                break;
            case "UNREGISTER":
                try {
                    node.unregister();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "REGISTER":
                try {
                    node.register();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                System.out.println("False command!");
        }
    }
}
