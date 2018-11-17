package Client;

import com.sun.org.apache.xpath.internal.functions.FuncFalse;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Node implements Runnable{
    private String ip;
    private int port;
    private String username;
    public ArrayList<Node> myNeighbours = new ArrayList<>();

    private Map resources = new HashMap<>();

    DatagramSocket ds;

    public  Node(String ip, int port, String username){
        this.ip = ip;
        this.port = port;
        this.username = username;
    }

    public  Node(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public void addResource(String name, String url){
        this.resources.put(name, url);
        System.out.println(this.port+":New resource url:"+this.resources.get(name));

    }

    @Override
    public void run() {
//        System.out.println(this.port+" port is listning...");
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(this.port);

            while (true){
                byte[] buffer = new byte[65536];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                try {

                    socket.receive(incoming);
                }catch (IOException e) {
                    e.printStackTrace();
                }

                byte[] data = incoming.getData();
                String received = new String(data, 0, incoming.getLength());
                System.out.println("received "+received);

                switch (received.split(" ")[1]){
                    case "JOIN":
                        System.out.println(this.port+": join request "+received);

                        String newNodeIp = received.split(" ")[2];
                        int newNodePort = Integer.parseInt(received.split(" ")[3]);

                        if(!isNeighbour(newNodeIp, newNodePort)){
                            myNeighbours.add(new Node(newNodeIp, newNodePort));
                        }

                        for(Node i: myNeighbours)
                            System.out.println(this.port+": neighbours "+i.toString());
                        break;
                    case "SER":
                        System.out.println(this.port+": search request "+received);
                        String fileName = received.split(" ")[4];
                        if(this.resources.get(fileName) == null) {
                            System.out.println(this.port + ": I dont have " + fileName);
                            try {
                                this.askNeighboursToSearch(received.split(" ")[4], received.split(" ")[2], received.split(" ")[3], received.split(" ")[5]);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            System.out.println(this.port + ": I have " + fileName);
                            try {
                                sendFilePathToRequester(fileName, received.split(" ")[2], received.split(" ")[3]);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case "SEROK":
                        System.out.println("SEROK received by "+port);
                        break;
                }
            }
        }catch (BindException ex){
            System.out.println("This is already registered! Try a different one or un-regiter first");
        }catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private Boolean isNeighbour(String ip, int port){
        boolean found = false;
        for(Node i:myNeighbours){
            if(i.getIp().equals(ip) && i.getPort()==port){
                found = true;
                break;
            }
        }
        return found;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void register() throws IOException {
        ds = new DatagramSocket();
        byte b[] = ("0036 REG "+this.ip+" "+this.port+" "+this.username).getBytes();     //request to register

        InetAddress ip = InetAddress.getByName("localhost");
        int port = 55555;

        DatagramPacket packet = new DatagramPacket(b, b.length, ip, port);
        ds.send(packet);

        addNeighboursAfterRegister();

    }

    public void unregister() throws IOException{
        //length UNREG IP_address port_no username
        ds = new DatagramSocket();
        byte b[] = ("0036 UNREG "+this.ip+" "+this.port+" "+this.username).getBytes();     //request to register

        InetAddress ip = InetAddress.getByName("localhost");
        int port = 55555;

        DatagramPacket packet = new DatagramPacket(b, b.length, ip, port);
        ds.send(packet);
        System.out.println("sent");


        byte[] buffer = new byte[512];
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);
        ds.receive(response);      //get the server response
        String responseMsg = new String(buffer, 0, response.getLength());
        String responseMsgArr[] = responseMsg.split(" ");
//        System.out.println(responseMsg);

        if(responseMsgArr[1].equals("UNROK")){
            if (responseMsgArr[2].equals("0"))
                System.out.println(this.ip+":"+this.port+" UNREGISTER succeful");
            else
                System.out.println("UNREGISTER succesful!");
        }
    }

    public void search(String name) throws IOException {
        ds = new DatagramSocket();
        //0047 SER 129.82.62.142 5070 "Lord of the rings"
        byte b[] = ("0047 SER "+this.ip+" "+this.port+" "+name+" 0").getBytes();

        InetAddress ip = InetAddress.getLocalHost();
        for(Node n: myNeighbours){
            int port = n.getPort();

            DatagramPacket packet = new DatagramPacket(b, b.length, ip, port);
            ds.send(packet);
        }
    }

    public void askNeighboursToSearch(String file, String searcherIp, String searcherPort, String hops) throws IOException{

        byte b[] = ("0047 SER "+searcherIp+" "+searcherPort+" "+file+" "+hops).getBytes();
        String received = b.toString();
        System.out.println("asking neighbour received "+received);
        ds = new DatagramSocket();

        InetAddress ip = InetAddress.getByName("localhost");

        for(Node n: myNeighbours){
            int port = n.getPort();
            if(port!=Integer.parseInt(searcherPort) && !n.getIp().equals(searcherIp)) {
                DatagramPacket packet = new DatagramPacket(b, b.length, ip, port);
                ds.send(packet);
            }
        }
    }

    public void sendFilePathToRequester(String fileName, String receiverIP, String receiverPort) throws IOException{
        byte b[] = ("0047 SEROK 1 "+ip+" "+port+" 1 "+fileName).getBytes();
        String received = b.toString();
        System.out.println("sending found results "+fileName);
        ds = new DatagramSocket();

        InetAddress ip = InetAddress.getByName("localhost");
        int port = Integer.parseInt(receiverPort);

        DatagramPacket packet = new DatagramPacket(b, b.length, ip, port);
        ds.send(packet);
    }

    public void addNeighboursAfterRegister() throws IOException {
        byte[] buffer = new byte[512];
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);
        ds.receive(response);      //get the server response
        String responseMsg = new String(buffer, 0, response.getLength());
        String responseMsgArr[] = responseMsg.split(" ");
//        System.out.println(responseMsg);

        if(responseMsgArr[1].equals("REGOK")){
            int availableNodes = Integer.parseInt(responseMsgArr[2]);
            if(availableNodes!=0){
                for(int i=3; i<responseMsgArr.length; i+=2){
                    String nodeIp = responseMsgArr[i];
                    int nodePort = Integer.parseInt(responseMsgArr[i+1]);
                    myNeighbours.add(new Node(nodeIp, nodePort));
                }
                for(Node i:myNeighbours)
                    System.out.println(this.port+": Neighbours"+i.toString());
            } else{
                System.out.println(this.port+": No neighbours yet");
            }
        }

    }

    public void join() throws IOException {
        byte[] msg = ("0027 JOIN "+this.ip+" "+this.port).getBytes();
        for(Node node:myNeighbours){
            InetAddress ip = InetAddress.getByName("localhost");
            int port = node.getPort();

            DatagramPacket packet = new DatagramPacket(msg, msg.length, ip, port);
            ds.send(packet);
        }
    }

    @Override
    public String toString() {
        return "Node{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
