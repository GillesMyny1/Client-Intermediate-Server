package ass2;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * The Intermediate Host class acts as a middle man between the Client
 * and Server classes, where neither of them know the other exist, and treat
 * this class as the receiving end.
 * @author Gilles Myny
 * @id 101145477
 */

public class Intermediate {
	
	DatagramSocket sendReceiveSocket, receiveSocket;
	DatagramPacket clientReceivePacket, serverSendPacket, clientSendPacket;
	
	/**
	 * Constructor that initializes the sendReceiveSocket, which acts as communication
	 * between the Server class and this class; and initializes the receiveSocket, which
	 * acts as the receiving end on port 23 for the Client class.
	 */
	public Intermediate() {
		try {
			sendReceiveSocket = new DatagramSocket();
			receiveSocket = new DatagramSocket(23);
		} catch(SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * receiveAndSend method receives a packet from the Client, prints the information,
	 * inserts its data into a new packet and socket destined for the Server, prints the copied
	 * information to double check no data was altered or lost, and waits to receive a response
	 * from the Server, if a response is received, it creates a new packet and socket to send the
	 * data back to the Client.
	 * @throws Exception when creating DatagramSocket to send back to the Client.
	 */
	public void receiveAndSend() throws Exception {
		byte clientDataR[] = new byte[20];
		clientReceivePacket = new DatagramPacket(clientDataR, clientDataR.length);
		
		System.out.println("Intermediate: Waiting for Packet from Client.\n");
		
		/*
		 * Blocks until the receiveSocket receives a packet from the Client.
		 */
		try {        
			System.out.println("Waiting...");
			receiveSocket.receive(clientReceivePacket);
		} catch(IOException e) {
		    System.out.print("IO Exception: likely:");
		    System.out.println("Receive Socket Timed Out.\n" + e);
		    e.printStackTrace();
		    System.exit(1);
		}
		
		/*
		 * Prints received information.
		 */
		System.out.println("Intermediate: Client packet received:");
		System.out.println("From host: " + clientReceivePacket.getAddress());
		System.out.println("Host port: " + clientReceivePacket.getPort());
		int lenCR = clientReceivePacket.getLength();
		System.out.println("Length: " + lenCR);
		System.out.println("Containing: " );
		System.out.println("\tString: " + new String(clientReceivePacket.getData(), 0, lenCR));
		System.out.println("\tBytes: " + Arrays.toString(clientReceivePacket.getData()));
		
		/*
		 * Slow things down with a sleep.
		 */
		try {
			Thread.sleep(2500);
		} catch(InterruptedException e ) {
		    e.printStackTrace();
		    System.exit(1);
		}
		
		/*
		 * Initialize packet to send to Server containing Client data
		 * on local host IP address and port 69.
		 */
		try {
			serverSendPacket = new DatagramPacket(clientReceivePacket.getData(), clientReceivePacket.getLength(), InetAddress.getLocalHost(), 69);
		} catch(UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		/*
		 * Confirm data in sending packet to Server.
		 */
		System.out.println("\nIntermediate: Sending packet to Server:");
	    System.out.println("To host: " + serverSendPacket.getAddress());
	    System.out.println("Destination host port: " + serverSendPacket.getPort());
	    int lenSS = serverSendPacket.getLength();
	    System.out.println("Length: " + lenSS);
	    System.out.println("Containing: ");
	    System.out.println("\tString: " + new String(serverSendPacket.getData(), 0, lenSS));
	    System.out.println("\tBytes: " + Arrays.toString(serverSendPacket.getData()));
	    
	    /*
	     * Send packet to Server.
	     */
	    try {
	    	sendReceiveSocket.send(serverSendPacket);
	    } catch (IOException e) {
	        e.printStackTrace();
	        System.exit(1);
	    }
	    
	    /*
	     * Checks if the second bit in the message was
	     * a 9 byte, indicating an invalid request sent,
	     * if so exit the program.
	     */
	    if(serverSendPacket.getData()[1] == (byte) 9) {
	    	System.exit(1);
	    }
	    
	    byte serverDataR[] = new byte[4];
	    DatagramPacket serverReceivePacket = new DatagramPacket(serverDataR, serverDataR.length);
	    
	    System.out.println("\nIntermediate: Waiting for Packet from Server.\n");
		
		/*
		 * Block until packet received from Server.
		 */
	    try {        
			System.out.println("Waiting...");
			sendReceiveSocket.receive(serverReceivePacket);
		} catch(IOException e) {
		    System.out.print("IO Exception: likely:");
		    System.out.println("Receive Socket Timed Out.\n" + e);
		    e.printStackTrace();
		    System.exit(1);
		}
		
	    /*
	     * Print data received from Server.
	     */
		System.out.println("\nIntermediate: Server packet received:");
		System.out.println("From host: " + serverReceivePacket.getAddress());
		System.out.println("Host port: " + serverReceivePacket.getPort());
		int lenSR = serverReceivePacket.getLength();
		System.out.println("Length: " + lenSR);
		System.out.println("Containing: " );
		System.out.println("\tBytes: " + Arrays.toString(serverReceivePacket.getData()));
		
		/*
		 * Slow things down using a sleep.
		 */
		try {
			Thread.sleep(2500);
		} catch(InterruptedException e ) {
		    e.printStackTrace();
		    System.exit(1);
		}
		
		/*
		 * Initialize packet to send back to Client containing Server response.
		 */
		clientSendPacket = new DatagramPacket(serverReceivePacket.getData(), serverReceivePacket.getLength(), clientReceivePacket.getAddress(), clientReceivePacket.getPort());
		
		/*
		 * Print information being sent to Client to verify data integrity.
		 */
		System.out.println("\nIntermediate: Sending packet to Client:");
	    System.out.println("To host: " + clientSendPacket.getAddress());
	    System.out.println("Destination host port: " + clientSendPacket.getPort());
	    int lenCS = clientSendPacket.getLength();
	    System.out.println("Length: " + lenCS);
	    System.out.println("Containing: ");
	    System.out.println("\tBytes: " + Arrays.toString(clientSendPacket.getData()) + "\n");
	    
	    /*
	     * Create Socket to send back packet to Client.
	     */
	    DatagramSocket clientSendSocket = new DatagramSocket();
	    
	    /*
	     * Send packet to Client.
	     */
	    try {
	    	clientSendSocket.send(clientSendPacket);
	    } catch (IOException e) {
	        e.printStackTrace();
	        System.exit(1);
	    }
	    
	    /*
	     * Close socket used to send Server response back to Client as this does not always happen,
	     * i.e. when invalid request is sent.
	     */
	    clientSendSocket.close();
	}
	
	public static void main(String[] args) throws Exception {
		Intermediate i = new Intermediate();
		while(true) {
			i.receiveAndSend();
		}
	}

}
