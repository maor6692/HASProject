package server;


import java.awt.List;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;

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
			try{
				if(key!= null){
					switch(key){
					case "login":

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
						break;
						
					case "getCurrentCourses":
						ans = (ArrayList<String>) message.get("getCurrentCourses");
						query = "SELECT name FROM course WHERE year='"+ans.get(0)+"' AND semester='"+ans.get(1)+"'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						ans.clear();
						while (rs.next()) { 
							ans.add(rs.getString(1));
						}
						
						stmt.close();
						rs.close();
						client.sendToClient(ans);//sends currSemester to SecretaryController.
						break;
						
					case "getCurrentClasses":
						ans = (ArrayList<String>) message.get("getCurrentClasses");
						query = "SELECT name FROM class WHERE year='"+ans.get(0)+"' AND semester='"+ans.get(1)+"'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						ans.clear();
						while (rs.next()) { 
							ans.add(rs.getString(1));
						}
						stmt.close();
						rs.close();
						client.sendToClient(ans);//sends currSemester to SecretaryController.
						break;

					case "upload":
						try {
							Files.write((Paths.get("sagi.docx")), (byte[])message.get(key), StandardOpenOption.CREATE_NEW);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						break;
					case "Search for teacher courses":
						String sans = (String) message.get(key);
						ans = new ArrayList<String>();
						query = "SELECT course_id FROM class_in_course WHERE teacher_id='"+sans+"'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						while (rs.next()) { 
							ans.add(rs.getString(1));
						}
						stmt.close();
						rs.close();
						client.sendToClient(ans);
						break;
					case "Search for course name":
						ans = (ArrayList<String>) message.get("Search for course name");
						ArrayList<String> newans = new ArrayList<String>();
						for(int i=0;i<ans.size();i++)
						{
							query = "SELECT name FROM course WHERE id='"+ans.get(i)+"'";
							stmt = conn.createStatement();
							rs = stmt.executeQuery(query);
							while (rs.next()) { 
								newans.add(rs.getString(1));
							}
							stmt.close();
							rs.close();
						}
						client.sendToClient(newans);
						break;
					case "Get class id for task":
						ArrayList<String> snewans = (ArrayList<String>)message.get(key);
						int i;
						query = "SELECT class_id FROM class_in_course WHERE course_id="+Integer.parseInt(snewans.get(0))+" AND teacher_id='"+snewans.get(1)+"'";	
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						snewans.clear();
						while (rs.next()) { 
							snewans.add(rs.getString(1));
						}
						stmt.close();
						rs.close();
						for(i=0;i<snewans.size();i++)
						{
							query = "SELECT name FROM class WHERE id="+snewans.get(i);
							stmt = conn.createStatement();
							rs = stmt.executeQuery(query);
							while (rs.next()) { 
								snewans.set(i, snewans.get(i) + " - " + rs.getString(1));
							}
							stmt.close();
							rs.close();
						}
						client.sendToClient(snewans);
						break;

					case "getCurrentSemester":

						query = "SELECT year,sem FROM semester WHERE iscurrent=1";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						String currSemester="";
						while (rs.next()) { 
							currSemester=rs.getString(1)+rs.getString(2);
						}
						stmt.close();
						rs.close();
						client.sendToClient(currSemester);//sends currSemester to SecretaryController.
						break;

					case "Get teaching unit":

						ans=(ArrayList<String>) message.get(key);
						query = "SELECT id,name FROM teaching_unit";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						ans.clear();
						while (rs.next()) { 
							ans.add(rs.getString(1)+" - "+ rs.getString(2));
						}
						stmt.close();
						rs.close();
						client.sendToClient(ans);//sends the answer to client.
						break;

					case "Create Course":

						ans=(ArrayList<String>) message.get(key);
						query = "INSERT INTO Course(id,name,teaching_unit,weekly_hours,year,semester) VALUES (?,?,?,?,?,?)";
						pstmt = conn.prepareStatement(query);
						pstmt.setInt(1, Integer.parseInt(ans.get(0)));
						pstmt.setString(2, ans.get(1));
						if(ans.get(2).charAt(1) == ' ')
							pstmt.setInt(3, Character.getNumericValue(ans.get(2).charAt(0)));
						else
						{
							pstmt.setInt(3, Integer.parseInt(Character.getNumericValue(ans.get(2).charAt(0)) + ""+Character.getNumericValue(ans.get(2).charAt(1))));
						}
						pstmt.setInt(4, Integer.parseInt(ans.get(3)));
						pstmt.setInt(5, Integer.parseInt(ans.get(4)));
						pstmt.setInt(6, Integer.parseInt(ans.get(5)));

						pstmt.executeUpdate();
						//client.sendToClient(ans);//sends the answer to client.
						break;

					case "logout":

						ans=(ArrayList<String>) message.get(key);
						query = "UPDATE users SET status='offline' WHERE user_name='"+ans.get(0)+"'";
						stmt = conn.createStatement();
						stmt.executeUpdate(query);
						ans.clear();
						stmt.close();
						break;

					case "define class":

						entity=(SClass)message.get(key);
						query = "INSERT INTO class (id,name) values (?,?)";
						pstmt = conn.prepareStatement(query);
						pstmt.setInt(1, ((SClass)message.get(key)).getId());
						pstmt.setString(2, ((SClass)message.get(key)).getName());
						pstmt.executeUpdate();

						break;
					case "get info for blockParentAccess":
						HashMap<Integer, ArrayList<String>> classes = new HashMap<Integer, ArrayList<String>>();
						HashMap<Integer, ArrayList<String>> students = new HashMap<Integer, ArrayList<String>>();
						query = "SELECT * FROM class";

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
						break;
					}
				}
			}
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
