package controller;

import ocsf.client.*;
import java.io.*;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import javafx.application.Platform;

public class UserClient extends AbstractClient {
	public static boolean flag = false; //flag to indicate if there is new answer from server
	public static Object ans;
	public static String userName;
	public static String fullName;
	// Constructors ****************************************************

	/**
	 * 
	 * Constructs an instance of the UserClient.
	 *
	 * @param host
	 *            The server to connect to.
	 * @param port
	 *            The port number to connect on.
	 */

	public UserClient(String host, int port) throws Exception {
		super(host, port); // Call the superclass constructor
		openConnection();

	}

	// Instance methods ************************************************
	/**
	 * indicates if there is new answer from server
	 */
	public boolean isready() { 
		return flag;
	}

	/**
	 * indicates that answer was accepted 
	 */
	public static void setFlagFalse() { 
		flag = false;
	}
	//Insert server answer to global array list and set the flag to show there is new answer from server.
	@SuppressWarnings("unchecked")
	public void handleMessageFromServer(Object msg) {

		synchronized (this) {
			ans = msg;
			flag = true;
			notifyAll();//release the GUI thread from wait to new answer from server
		}

	}
	/**
	 * Transfer client request to server
	 *
	 * @param msg
	 *            The message to server
	 */

	public void sendServer(Object msg) {
		try {
			sendToServer(msg);
		} catch (Exception e) {//if the server is not listening we will end the program.
			JOptionPane.showMessageDialog(null, "Server is not responding, try again later");
			quit();
		}
	}

	/**
	 * This method terminates the client.
	 */
	public void quit() {
		try {
			closeConnection();
		} catch (IOException e) {
		}
		LoginController.logout();
		Platform.exit();
		System.exit(0);
	}
}
