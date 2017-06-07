package server;


import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;



import common.SClass;
import ocsf.server.*;

public class EchoServer extends AbstractServer {
	// Class variables *************************************************

	/**
	 * The default port to listen on.
	 */

	// Constructors ****************************************************


	static Connection conn;

	/**
	 * Constructs an instance of the echo server.
	 *
	 * @param port
	 *            The port number to connect on.
	 */
	public EchoServer(int port) {
		super(port);
	}



	// Instance methods ************************************************

	/**
	 * This method handles any messages received from the client.
	 *
	 * @param msg
	 *            The message received from the client.
	 * @param client
	 *            The connection from which the message originated.
	 */
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {

		Statement stmt;
		PreparedStatement pstmt;
		ResultSet rs;
		HashMap<String,Object> message = new HashMap<String,Object>();
		Object entity=null;
		String query,temp=null;
		message = (HashMap<String,>)msg;
		ArrayList<String> ans;
		for(String key : message.keySet()){
			if(key!= null){
				temp=key;
				switch(temp){
				case "validate user":
					try{
						ans=(ArrayList<String>) message.get(temp);
						query = "Select * FROM users WHERE user_name='"+ans.get(1)+"' AND password='"+ans.get(2)+"'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						message.clear();
						ans.clear();
						while (rs.next()) {    //insert each row's columns to array list.
							ans.add(rs.getString(1));
							ans.add(rs.getString(2));
				
						}
						rs.close();
						client.sendToClient(ans);//sends the answer to client.
			
						} catch (SQLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
				
				



				case "define class":
					try{
					entity=(SClass)message.get(key);
					query = "INSERT INTO class (id,name) values (?,?)";
					pstmt = conn.prepareStatement(query);
					pstmt.setInt(1, ((SClass)message.get(key)).getId());
					pstmt.setString(2, ((SClass)message.get(key)).getName());
					pstmt.executeUpdate();
					}
					catch(Exception e){
						
					}
					break;
					
				}
			}
		}
	}
	
//		 query = "Select * FROM users WHERE user_name='"+arr.get(0)+"' AND password='"+arr.get(1)+"'";
//		try {
//
//			if(query.charAt(0)=='U')//checks if the query is an update.
//			{
//				stmt = conn.createStatement();
//				stmt.executeUpdate(query);
//			}
//			else // select query.
//			{
//				stmt = conn.createStatement();
//				rs = stmt.executeQuery(query);
//				arr.clear();
//				while (rs.next()) {    //insert each row's columns to array list.
//					arr.add(rs.getString(7));
//
//				}
//				rs.close();
//				client.sendToClient(arr);//sends the answer to client.
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//
//	}

	/**
	 * This method overrides the one in the superclass. Called when the server
	 * starts listening for connections.
	 */
	protected void serverStarted() {
		System.out.println("Server listening for connections on port " + getPort());
	}

	/**
	 * This method overrides the one in the superclass. Called when the server
	 * stops listening for connections.
	 */
	protected void serverStopped() {
		System.out.println("Server has stopped listening for connections.");
	}

	// Class methods ***************************************************

	/**
	 * This method is responsible for the creation of the connection to the Data Base.
	 *
	 */
	public int connect(String host, String db, String user, String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
		}
		try {
			conn = DriverManager.getConnection("jdbc:mysql://"+host+"/"+db, user, password);
			System.out.println("SQL connection succeed");
			listen(); // Start listening for connections

		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Authentication failed, check your DB login input");
			return 0;

		}
		catch (Exception ex) {
			System.out.println("ERROR - Could not listen for clients!");
			JOptionPane.showMessageDialog(null, "Please try another port");
			return 0;
		}
		return 1;

	}
}
// End of EchoServer class
