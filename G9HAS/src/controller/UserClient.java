package controller;

import ocsf.client.*;
import java.io.*;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import javafx.application.Platform;

public class UserClient extends AbstractClient {
	public boolean flag = false; //flag to indicate if there is new answer from server
	// Constructors ****************************************************

	/**
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
	public boolean isready() { //checks if there is new answer from server
		return flag;
	}

	public void setFlagFalse() { //wait for new answer from server
		flag = false;
	}
	//Insert server answer to global array list and set the flag to show there is new answer from server.
	@SuppressWarnings("unchecked")
	public void handleMessageFromServer(Object msg) {

		synchronized (this) {
			LoginController.ans=(ArrayList<String>) msg;
			
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
			Platform.exit();
			System.exit(0);
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
		Platform.exit();
		System.exit(0);
	}
}
