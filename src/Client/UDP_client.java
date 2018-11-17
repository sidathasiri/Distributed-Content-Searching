package Client;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class UDP_client {
    public static void main(String args[]) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("IP is 127.0.0.1");
        String ip = "127.0.0.1";
        System.out.print("Enter port:");
        int port = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Ënter username:");
        String username = scanner.nextLine();
        Node node1 = new Node(ip, port, username);

        new Thread(node1).start();

        node1.addResource("mario", "/mario");

        System.out.println(node1.getPort()+": registering");
        node1.register();

        node1.join();

//        Node node1 = new Node("127.0.0.1", 5000);
//        Node node2 = new Node("127.0.0.1", 5003);
//        Node node3 = new Node("127.0.0.1", 5006);
//
//        System.out.println(node1.getPort()+": registering");
//        node1.register();
//        node1.addResource("Harry Potter", "/harry");
//        new Thread(node1).start();
//
//        System.out.println(node2.getPort()+": registering");
//        node2.register();
//        node2.addResource("mario", "/mario");
//        node2.join();
//        new Thread(node2).start();
//
//        System.out.println(node3.getPort()+": registering");
//        node3.register();
//        node3.addResource("idea", "/idea");
//        node3.join();
//        new Thread(node3).start();
//
//        node3.search("mario");
    }
}
