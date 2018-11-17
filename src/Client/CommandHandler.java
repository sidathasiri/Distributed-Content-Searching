package Client;

import java.io.IOException;

public class CommandHandler {
    private Node node;

    public CommandHandler(Node node){
        this.node = node;
    }

    public void execute(String command){
        switch (command.split(" ")[0]){
            case "SHOW":
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
            case "JOIN":
                try {
                    node.join();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "SEARCH":
                try {
                    String[] commandArr = command.split(" ");
                    String fileName = "";
                    for(int i=1; i<commandArr.length; i++)
                        fileName += " "+ commandArr[i];
                    System.out.println("Searching:"+fileName.trim());
                    node.search(fileName.trim());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                System.out.println("False command!");
        }
    }
}
