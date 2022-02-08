package ass2;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Client class creates a DatagramSocket to use for both sending and receiving
 * read and write requests, where the 11th request sent is an invalid request.
 * @author Gilles Myny
 * @id 101145477
 */
public class Client {
	
	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendReceiveSocket;
	
	private String requestType;
	private final String fileName = "test.txt";
	private final String mode = "netascii";
	private final byte zero = 0;
	private final byte one = 1;
	private final byte two = 2;
	private final byte invalid = 9;
	
	/**
	 * Constructor initializes the requestType String and
	 * initializes the sendReceiveSocket DatagramSocket.
	 */
	public Client() {
		requestType = new String();
	   try {
	      sendReceiveSocket = new DatagramSocket();
	   } catch (SocketException se) {
	      se.printStackTrace();
	      System.exit(1);
	   }
	}

	/**
	 * The sendAndReceive method cycles 11 times, where each other time
	 * the method is run either a read request is sent to the intermediate
	 * host, or a write request is sent to the intermediate, however
	 * the 11th/last request that is sent is an invalid request that
	 * stops the program.
	 */
	public void sendAndReceive() {
		for(int i = 0; i <= 11; i++) {
			byte msg[] = new byte[20];
			byte nameBytes[] = fileName.getBytes();
			byte modeBytes[] = mode.getBytes();
			
			/*
			 * Cycle through request types
			 */
			if(i == 11) {
				requestType = "invalid";
				msg[0] = zero;
				msg[1] = invalid;
			} else if(i % 2 == 0) {
				requestType = "read";
				msg[0] = zero;
				msg[1] = one;
			} else if(i % 2 == 1) {
				requestType = "write";
				msg[0] = zero;
				msg[1] = two;
			}
			
			/*
			 * Copy the filename and mode to the packet byte array.
			 */
			System.arraycopy(nameBytes, 0, msg, 2, nameBytes.length);
			msg[nameBytes.length + 2] = zero;
			System.arraycopy(modeBytes, 0, msg, nameBytes.length + 3, modeBytes.length);
			msg[nameBytes.length + modeBytes.length + 3] = zero;
			try {
				sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), 23);
			} catch(UnknownHostException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			/*
			 * Print sending information
			 */
			System.out.println("Client: Sending " + requestType + " request packet:");
		    System.out.println("To host: " + sendPacket.getAddress());
		    System.out.println("Destination host port: " + sendPacket.getPort());
		    int len = sendPacket.getLength();
		    System.out.println("Length: " + len);
		    System.out.println("Containing: ");
		    System.out.println("\tString: " + new String(sendPacket.getData(), 0, len));
		    System.out.println("\tBytes: " + Arrays.toString(sendPacket.getData())); 

		    /*
		     * Send packet using sendReceiveSocket
		     */
		    try {
		    	sendReceiveSocket.send(sendPacket);
		    } catch (IOException e) {
		    	e.printStackTrace();
		        System.exit(1);
		    }

		    /*
		     * Confirm packet delivery to user
		     */
		    System.out.println("\nClient: " + requestType + " request packet sent.\n");
		    
		    /*
		     * Check if the packet just sent was an invalid request,
		     * if so then close the DatagramSocket and exit the program. 
		     */
		    if(i == 11) {
		    	sendReceiveSocket.close();
		    	System.exit(1);
		    }
		    
		    byte data[] = new byte[4];
		    receivePacket = new DatagramPacket(data, data.length);
		    
		    /*
		     * Block until DatagramSocket receives a DatagramPacket.
		     */
		    try {
		    	sendReceiveSocket.receive(receivePacket);
		    } catch(IOException e) {
		    	e.printStackTrace();
		    	System.exit(1);
		    }
		    
		    /*
		     * Print received information.
		     */
		    System.out.println("Client: Packet received:");
		    System.out.println("From host: " + receivePacket.getAddress());
		    System.out.println("Host port: " + receivePacket.getPort());
		    len = receivePacket.getLength();
		    System.out.println("Length: " + len);
		    System.out.println("Containing:");
		    System.out.println("\tBytes: " + Arrays.toString(receivePacket.getData()) + "\n");
		}
	}

	public static void main(String args[]) {
		Client c = new Client();
		c.sendAndReceive();
	}
}
