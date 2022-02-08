package ass2;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Server class creates a receiving DatagramSocket on port 69 to receive packets
 * and send a response if the packet received was a read or write request.
 * @author Gilles Myny
 * @id 101145477
 */

public class Server {
	
	DatagramPacket sendPacket, receivePacket;
	DatagramSocket receiveSocket;
	
	/**
	 * Constructor initializing DatagramSocket to port 69.
	 */
	public Server() {
		try {
			receiveSocket = new DatagramSocket(69);
		} catch(SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}
	
	public void receiveAndSend() throws Exception {
		byte data[] = new byte[20];
		receivePacket = new DatagramPacket(data, data.length);
		
		System.out.println("Server: Waiting for Packet.\n");
		
		/*
		 * Block until socket receives a packet.
		 */
		try {        
		      System.out.println("Waiting...");
		      receiveSocket.receive(receivePacket);
		} catch (IOException e) {
		      System.out.print("IO Exception: likely:");
		      System.out.println("Receive Socket Timed Out.\n" + e);
		      e.printStackTrace();
		      System.exit(1);
		}
		
		/*
		 * Print received information.
		 */
		System.out.println("Server: Packet received:");
		System.out.println("From host: " + receivePacket.getAddress());
		System.out.println("Host port: " + receivePacket.getPort());
		int len = receivePacket.getLength();
		System.out.println("Length: " + len);
		System.out.println("Containing: " );
	    System.out.println("\tString: " + new String(data, 0, len));
	    System.out.println("\tBytes: " + Arrays.toString(data));
	    
	    /*
	     * Slow things down using a sleep.
	     */
	    try {
	        Thread.sleep(5000);
	    } catch (InterruptedException e ) {
	        e.printStackTrace();
	        System.exit(1);
	    }
	    
	    /*
	     * Initialize response byte arrays.
	     */
	    byte readMsg[] = new byte[] {0, 3, 0, 1};
	    byte writeMsg[] = new byte[] {0, 4, 0, 0};
	    
	    /*
	     * Check which response is necessary based on first two bits of received data.
	     * Throw exception and quit if invalid request is sent.
	     */
	    if((data[0] == (byte) 0) && (data[1] == (byte) 1)) {
	    	sendPacket = new DatagramPacket(readMsg, readMsg.length, receivePacket.getAddress(), receivePacket.getPort());
	    } else if((data[0] == (byte) 0) && (data[1] == (byte) 2)) {
	    	sendPacket = new DatagramPacket(writeMsg, writeMsg.length, receivePacket.getAddress(), receivePacket.getPort());
	    } else {
	    	throw new Exception("Invalid Request");
	    }
	    
	    /*
	     * Print information being sent back.
	     */
	    System.out.println("\nServer: Sending packet:");
	    System.out.println("To host: " + sendPacket.getAddress());
	    System.out.println("Destination host port: " + sendPacket.getPort());
	    len = sendPacket.getLength();
	    System.out.println("Length: " + len);
	    System.out.println("Containing: ");
	    System.out.println("\tBytes: " + Arrays.toString(sendPacket.getData()));
	    
	    DatagramSocket sendSocket = new DatagramSocket();
	    
	    /*
	     * Send packet using sending socket.
	     */
	    try {
	    	sendSocket.send(sendPacket);
	    } catch(IOException e) {
	    	e.printStackTrace();
	    	System.exit(1);
	    }
	    
	    /*
	     * Confirm delivery and close sending socket.
	     */
	    System.out.println("\nServer: packet sent\n");
	    sendSocket.close();
	}

	public static void main(String[] args) throws Exception {
		Server s = new Server();
		while(true) {
			s.receiveAndSend();
		}
	}

}
