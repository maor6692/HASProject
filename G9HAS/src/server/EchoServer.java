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
		HashMap<String,Object> message ;
		Object entity=null;
		String query,temp=null;
		message = (HashMap<String, Object>)msg;
		ArrayList<String> ans=null;
		for(String key : message.keySet()){
			if(key!= null){
				switch(key){
				case "login":
					try{
						ans=(ArrayList<String>) message.get(key);
						query = "Select * FROM users WHERE user_name ='"+ans.get(0)+"' AND password='"+ans.get(1)+"'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						ans.clear();
						while (rs.next()) {    //insert each row's columns to array list.
							query = "UPDATE users SET status="+"'online'"+" WHERE user_name='"+rs.getString(1)+"'";
							ans.add(rs.getString(7));//user type
							ans.add(rs.getString(4));//first name
							ans.add(rs.getString(5));//last name
							ans.add(rs.getString(3));//status
							stmt.executeUpdate(query);
							break;
						}
						stmt.close();
						rs.close();
						client.sendToClient(ans);//sends the answer to client.

					} catch (SQLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
					
				case "logout":
					try{
						ans=(ArrayList<String>) message.get(key);
						query = "UPDATE users SET status='offline' WHERE user_name='"+ans.get(0)+"'";
						stmt = conn.createStatement();
						stmt.executeUpdate(query);
						ans.clear();
						stmt.close();
						
					} catch (SQLException e) {
						e.printStackTrace();
					}
					break;


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
				case "get info for blockParentAccess":
					HashMap<Integer, ArrayList<String>> classes = new HashMap<Integer, ArrayList<String>>();
					HashMap<Integer, ArrayList<String>> students = new HashMap<Integer, ArrayList<String>>();
					query = "SELECT * FROM class";
					try {
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						while (rs.next()) {
							ans = new ArrayList<>();
							ans.add(rs.getString(2)); // add name to array list
							ans.add(rs.getString(3)); // add year to array list
							ans.add(rs.getString(4)); // add semester to array list
							classes.put(Integer.parseInt(rs.getString(1)), ans); // add key and array list
						}
						query = "SELECT id,pblocked,class_id FROM student";
						rs = stmt.executeQuery(query);
						while (rs.next()) {
							ans = new ArrayList<>();
							ans.add(rs.getString(2)); // add pblocked to array list
							ans.add(rs.getString(3)); // add class_id to array list
							students.put(Integer.parseInt(rs.getString(1)), ans); // add key and array list
							stmt.close();
							HashMap<String,Object> replay = new HashMap<String,Object>();
							replay.put("class", classes);
							replay.put("student", students);
							client.sendToClient(replay);
							System.out.println("query for itay");
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}

	
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
