package Client;

import java.net.*;
import java.io.*;
import java.util.ArrayList;


public class UDP_client {
    public static void main(String args[]) throws IOException {
        Node node1 = new Node("127.0.0.1", 5000);
        Node node2 = new Node("127.0.0.1", 5003);
        Node node3 = new Node("127.0.0.1", 5006);
//        new Thread(node3).start();
        Node node4 = new Node("127.0.0.1", 5009);
//        new Thread(node4).start();
//        Node node5 = new Node("127.0.0.1", 5012);

        System.out.println(node1.getPort()+": registering");
        node1.register();
        new Thread(node1).start();

        System.out.println(node2.getPort()+": registering");
        node2.register();
        node2.join();
        new Thread(node2).start();

        System.out.println(node3.getPort()+": registering");
        node3.register();
        node3.join();
        new Thread(node3).start();

        System.out.println(node4.getPort()+": registering");
        node4.register();
        node4.join();
        new Thread(node4).start();
    }
}
