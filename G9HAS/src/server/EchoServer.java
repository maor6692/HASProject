package server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.xml.crypto.dsig.spec.HMACParameterSpec;

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
						query = "SELECT name,teaching_unit,id FROM course WHERE year='"+ans.get(0)+"' AND semester='"+ans.get(1)+"'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						ans.clear();
						
						HashMap<Integer,HashMap<Integer,String>> currCourses = new HashMap<>(); // teachingUnit-->Map<courseid,coursename>
						ArrayList<Integer> tempUnits = new ArrayList<>();
						ArrayList<String> tempNames = new ArrayList<>();
						ArrayList<Integer> tempCourseId = new ArrayList<>();
						
						while (rs.next()) {  // create 2 arraylist, teachin units and course names
							tempUnits.add(Integer.parseInt(rs.getString(2)));
							tempNames.add(rs.getString(1));
							tempCourseId.add(Integer.parseInt(rs.getString(3)));
						}

						//** make arraylist of no duplicated units for keys
						ArrayList <Integer> teachingUnits = new ArrayList <Integer>();
						for(int t : tempUnits){ // making arraylist with teaching units - no duplications
							if(!teachingUnits.contains(t))
								teachingUnits.add(t);
						}


						for(int  unit : teachingUnits){ // for each unit(no duplications) *(1)
							HashMap<Integer,String> coursesInTeachingUnit = new HashMap<>();
							for(int t=0;t<tempUnits.size();t++){ // search in query result this teaching unit 
								if(unit == tempUnits.get(t)){ // if this unit *(1) == searched unit
									coursesInTeachingUnit.put(tempCourseId.get(t),tempNames.get(t)); // add course name
								}
							}
							currCourses.put(unit, coursesInTeachingUnit); // add courses unit + list of courses
						}




						stmt.close();
						rs.close();
						client.sendToClient(currCourses);//sends currSemester to SecretaryController.
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

					case "getTeachers":
						ResultSet rs2 = null;
						Statement stmt2;
						HashMap<String ,HashMap<String,ArrayList<String>>> units = new HashMap<String, HashMap<String, ArrayList<String>>>();//outer hashMap
						HashMap<String,ArrayList<String>> teachers = new HashMap<String, ArrayList<String>>();//inner hashMap
						ArrayList<String> teacherDetails = new ArrayList<String>();//contains teacher name and hours_limit
						query = "SELECT id FROM teaching_unit";//get all teaching units
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						while (rs.next()) { //for each teaching unit get teachers list with their names and our limit
							query = "SELECT t.id,u.first_name,u.last_name,t.hours_limit "
									+ "From teacher t,users u,teacher_teaching_unit ttu "
									+ "Where u.user_name=t.id AND ttu.teaching_unit="+rs.getString(1) +" AND ttu.teacher_id=t.id";
							stmt2 = conn.createStatement();
							rs2 = stmt2.executeQuery(query);
							while (rs2.next()) {
								teacherDetails.add(rs2.getString(2));//first_name
								teacherDetails.add(rs2.getString(3));//last_name
								teacherDetails.add(rs2.getString(4));//hours_limit
								teachers.put(rs2.getString(1), new ArrayList<String>(teacherDetails));//<id,teacherDetails>
								teacherDetails.clear();
							}
							units.put(rs.getString(1), new HashMap<String, ArrayList<String>>(teachers));//<teaching_unit,teachers>
							teachers.clear();
							stmt2.close();
						}
						stmt.close();
						rs.close();
						rs2.close();
						client.sendToClient(units);//sends units to Secretary controller
						break;

					case "upload":
						
						HashMap<String,byte[]> hm =  (HashMap<String, byte[]>) message.get("upload");
						for(String name : hm.keySet()){
						try {
							Files.write((Paths.get("Documents\\"+name)), (byte[])hm.get(name));
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						}
						client.sendToClient(null);
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
					case "Search for class_in_course_id":
						ans=(ArrayList<String>) message.get(key);
						System.out.println(ans.toString());
						int clsid=0;
						int cid=0;
						if (ans.get(0).length()>2 && ans.get(1).length()>0&& ans.get(0)!=null && ans.get(1)!=null){
							
						cid=Integer.parseInt(ans.get(0).substring(0, 3));
						clsid=Integer.parseInt(ans.get(1).substring(0, 1));}
						query = "Select id FROM class_in_course WHERE course_id="+cid+" AND class_id="+clsid;
						System.out.println(clsid);
						System.out.println(cid);
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						ans.clear();
						while (rs.next()) { 
							ans.add(rs.getString(1));
							
						}
						System.out.println(ans.toString()+"!!!----!!!");
						stmt.close();
						rs.close();
						client.sendToClient(ans);
						//ans.clear();
						break;
					case "Create task":
						ans=(ArrayList<String>) message.get(key);
						query = "INSERT INTO task_in_class_in_course (class_in_course_id,task_file,submission_date,release_date) VALUES (?,?,?,?)";
						pstmt = conn.prepareStatement(query);
						pstmt.setInt(1, Integer.parseInt(ans.get(0)));
						pstmt.setString(2, ans.get(1));
						pstmt.setDate(3, Date.valueOf(ans.get(2)));
						pstmt.setDate(4, Date.valueOf("2017-05-23"));
						pstmt.executeUpdate();
						ans.clear();
						pstmt.close();
						//JOptionPane.showMessageDialog(null, "Task uploaded successfuly","Task Upload",JOptionPane.PLAIN_MESSAGE);
						
						
						client.sendToClient(null);
						break;
					case "Get student class":
					      ArrayList<String> ans1=new ArrayList<String>();
					      ans= (ArrayList<String>)message.get(key);
					      query = "SELECT class_id FROM student WHERE id='"+ans.get(0)+"'";
					      
					      stmt = conn.createStatement();
					      rs = stmt.executeQuery(query);
					      ans.clear();
					      while (rs.next()) { 
					       ans1.add(String.valueOf(rs.getInt(1)));
					      }
					      stmt.close();
					      rs.close();
					      
					      client.sendToClient(ans1);
					      break;
				     case "get course in class":
				         ArrayList<String> courses=new ArrayList<String>();
				         ans= (ArrayList<String>)message.get(key);
				         query= "SELECT course_in_class_id  FROM student_in_course_in_class WHERE student_id='"+ans.get(0)+"'";
				         stmt = conn.createStatement();
				         rs = stmt.executeQuery(query);
				         ans.clear();
				         while (rs.next()) { 
				          courses.add(rs.getString(1));// course in class id
				         }
				         stmt.close();
				         rs.close();
				         client.sendToClient(courses);
				         break;
				     case "get course id":
				         ans = (ArrayList<String>) message.get("get course id");
				         ArrayList<String> coursesid = new ArrayList<String>();
				         for(i=0;i<ans.size();i++)
				         {
				          query = "SELECT course_id FROM class_in_course WHERE id='"+ans.get(i)+"'";
				          stmt = conn.createStatement();
				          rs = stmt.executeQuery(query);
				          while (rs.next()) { 
				           coursesid.add(String.valueOf(rs.getInt(1)));
				          }
				          stmt.close();
				          rs.close();
				         }
				         ans.clear();
				         client.sendToClient(coursesid);
				         break;
				     case "get student average for current semester":
				         ans = (ArrayList<String>) message.get(key);
				         ArrayList<String> arr = new ArrayList<String>();
				         ArrayList<String> arr2 = new ArrayList<String>();
				        // arr2.add(ans.get(0));//year
				        // arr2.add(ans.get(1));//semester
				         
				         for(i=3;i<ans.size();i++)
				         {
				          query = "SELECT course_id,id FROM class_in_course WHERE id='"+ans.get(i)+"' AND course_id"
				          		+ " IN (SELECT course.id FROM course where year="+Integer.parseInt(ans.get(0))+" AND semester='"+ans.get(1)+"')";
				          stmt = conn.createStatement();
				          rs = stmt.executeQuery(query);
				          while (rs.next()) { 
				           arr.add(String.valueOf(rs.getInt(2)));
				          }
				          stmt.close();
				          rs.close();
				         }
				         for(i=0;i<arr.size();i++)
				         {
				          query = "SELECT grade FROM student_in_course_in_class WHERE course_in_class_id='"+arr.get(i)+"' AND student_id='"+ans.get(2)+"' ";
				          stmt = conn.createStatement();
				          rs = stmt.executeQuery(query);
				          while (rs.next()) { 
				           arr2.add(String.valueOf(rs.getFloat(1)));
				          }
				          stmt.close();
				          rs.close();
				         }
				         ans.clear();
				         client.sendToClient(arr2);
				         break;
				     case "get grades for student in course":
				    	 ans = (ArrayList<String>) message.get(key);
				    	 ArrayList<String> grades = new ArrayList<String>();
				    	 query = "SELECT task_grade FROM task_of_student_in_course WHERE course_id="+Integer.valueOf(ans.get(1))+" AND student_id='"+ans.get(0)+"'";
				    	 stmt = conn.createStatement();
				    	 rs = stmt.executeQuery(query); 
				    	 while (rs.next()) { 
				    	  grades.add(String.valueOf(rs.getFloat(1))); 
				    	 }
				    	 stmt.close();
				    	 rs.close();
				    	 
				    	 client.sendToClient(grades);
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
						int newSemester;
						int newYear;
						if(currSemester.equals("")){//if there is no current semester in DB
							LocalDate localDate = LocalDate.now();
							newYear=Integer.parseInt(DateTimeFormatter.ofPattern("yyyy/MM/dd").format(localDate).substring(0, 4));
							newSemester=1;
						}else{
							newYear=Integer.parseInt(currSemester.substring(0, 4));
							newSemester= (currSemester.charAt(4)=='1'  ? 1 : 2);
							if(newSemester==2){
								newSemester=1;
								newYear++;
							}else newSemester = 2;
						}
						ArrayList<Object> semester = new ArrayList<Object>();
						semester.add(newYear);
						semester.add(newSemester > 1 ? 'B' : 'A');
						client.sendToClient(semester);//sends next Semester details to SecretaryController.
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
						client.sendToClient(null);
						break;

					case "logout":

						ans=(ArrayList<String>) message.get(key);
						query = "UPDATE users SET status='offline' WHERE user_name='"+ans.get(0)+"'";
						stmt = conn.createStatement();
						stmt.executeUpdate(query);
						ans.clear();
						//stmt.close();
						client.sendToClient(null);
						break;

					case "createClass":

						ArrayList<String> classDetails = (ArrayList<String>) message.get(key);
						query = "INSERT INTO class (name,year,semester) values (?,?,?)";
						pstmt = conn.prepareStatement(query);
						pstmt.setString(1,classDetails.get(0));
						pstmt.setInt(2, Integer.parseInt(classDetails.get(1)));
						pstmt.setInt(3, Integer.parseInt(classDetails.get(2)));
						pstmt.executeUpdate();
						query = "SELECT id FROM class WHERE name='"+classDetails.get(0)+"' AND year="+classDetails.get(1)+" AND semester="+classDetails.get(2);
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						if (rs.next())  
						client.sendToClient(rs.getString(1));
						break;

					case "assignStudentsToCourseInClass":
						ArrayList<String> studentDetails = (ArrayList<String>) message.get(key);
						query = "UPDATE student SET class_id=? WHERE id=?";
						pstmt = conn.prepareStatement(query);
						int classId = Integer.parseInt(studentDetails.get(0));
						studentDetails.remove(0);
						for(String studentId : studentDetails){
							pstmt.setInt(1, classId);
							pstmt.setString(2,studentId);
							pstmt.executeUpdate();
						}
						client.sendToClient(null);
						break;
			

					case "checkClassIsNotExist" :
						String answer="";
	ans=(ArrayList<String>) message.get(key);
						stmt = conn.createStatement();
						query = "SELECT * FROM class WHERE name = '"+ans.get(0)+"'"+" AND year="+ans.get(1)+" AND semester= "+ans.get(2);
						rs=stmt.executeQuery(query);
						if(rs.next()) answer = "no";
						//stmt.close();
						client.sendToClient(answer);
						break;
						
					case "isStudent":
						answer="";
						ans=(ArrayList<String>) message.get(key);
						query = "SELECT * FROM student WHERE id = '"+ans.get(0)+"'";
						stmt = conn.createStatement();
						rs=stmt.executeQuery(query);
						if(!rs.next()) answer = "no";
						else if(rs.getInt(4) > 0) answer="alreadyAssigned";
						client.sendToClient(answer);
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
