package Client;

import java.net.*;
import java.io.*;
import java.util.ArrayList;


public class UDP_client {
    public static void main(String args[]) throws IOException {
        Node node1 = new Node("127.0.0.1", 5000);
        Node node2 = new Node("127.0.0.1", 5003);
        Node node3 = new Node("127.0.0.1", 5006);
        Node node4 = new Node("127.0.0.1", 5009);
        Node node5 = new Node("127.0.0.1", 5012);

        node1.register();
        node2.register();
        node3.register();
        node4.register();
    }
}
