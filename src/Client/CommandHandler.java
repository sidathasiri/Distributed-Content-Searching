package Client;

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
            default:
                System.out.println("False command!");
        }
    }
}
