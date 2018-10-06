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
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while (true){
            byte[] buffer = new byte[65536];
            DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
            try {

                socket.receive(incoming);
            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] data = incoming.getData();
            String received = new String(data, 0, incoming.getLength());

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
                    if(this.resources.get(fileName) == null)
                        System.out.println(this.port+": I dont have "+fileName);
                    else
                        System.out.println(this.port+": I have "+fileName);

            }
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
        byte b[] = ("0036 REG "+this.ip+" "+this.port+" sidath").getBytes();     //request to register

        InetAddress ip = InetAddress.getByName("localhost");
        int port = 45454;

        DatagramPacket packet = new DatagramPacket(b, b.length, ip, port);
        ds.send(packet);

        addNeighboursAfterRegister();
    }

    public void search(String name) throws IOException {
        ds = new DatagramSocket();
        //0047 SER 129.82.62.142 5070 "Lord of the rings"
        byte b[] = ("0047 SER "+this.ip+" "+this.port+" mario 0").getBytes();

        InetAddress ip = InetAddress.getLocalHost();
        for(Node n: myNeighbours){
            int port = n.getPort();

            DatagramPacket packet = new DatagramPacket(b, b.length, ip, port);
            ds.send(packet);
        }


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
