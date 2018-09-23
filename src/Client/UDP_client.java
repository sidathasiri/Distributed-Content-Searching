package Client;
import java.net.*;
import java.io.*;


public class UDP_client {
    public static void main(String args[]) throws IOException {
        DatagramSocket ds = new DatagramSocket();
        byte b[] = "0036 REG 127.0.0.1 5001 sidath".getBytes();     //request to register

        InetAddress ip = InetAddress.getByName("localhost");
        int port = 55555;

        DatagramPacket packet = new DatagramPacket(b, b.length, ip, port);
        ds.send(packet);

        byte[] buffer = new byte[512];
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);
        ds.receive(response);      //get the server response
        System.out.println(new String(buffer, 0, response.getLength()));
    }
}
