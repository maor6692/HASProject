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
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		LocalDate localDate;
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
						query = "SELECT name,teaching_unit,id,weekly_hours FROM course WHERE year='"+ans.get(0)+"' AND semester='"+ans.get(1)+"'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						HashMap<Integer,Integer> coursesWeeklyHours = new HashMap<>();
						ans.clear();
						
						HashMap<Integer,HashMap<Integer,String>> currCourses = new HashMap<>(); // teachingUnit-->Map<courseid,coursename>
						ArrayList<Integer> tempUnits = new ArrayList<>();
						ArrayList<String> tempNames = new ArrayList<>();
						ArrayList<Integer> tempCourseId = new ArrayList<>();
						
						while (rs.next()) {  // create 2 arraylist, teachin units and course names
							tempUnits.add(Integer.parseInt(rs.getString(2)));
							tempNames.add(rs.getString(1));
							tempCourseId.add(Integer.parseInt(rs.getString(3)));
							coursesWeeklyHours.put(rs.getInt(3),rs.getInt(4));
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



						ArrayList<Object> sendans = new ArrayList<>();
						sendans.add(currCourses);
						sendans.add(coursesWeeklyHours);
						stmt.close();
						rs.close();
						client.sendToClient(sendans);//sends currSemester to SecretaryController.
						break;

					case "getCurrentClasses":
					
						ans = (ArrayList<String>) message.get("getCurrentClasses");
						query = "SELECT name,id FROM class WHERE year='"+ans.get(0)+"' AND semester='"+ans.get(1)+"'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						ans.clear();
						HashMap<Integer,String> cls = new HashMap<>();
						while (rs.next()) { 
							cls.put(Integer.parseInt(rs.getString(2)), rs.getString(1));
						}
						stmt.close();
						rs.close();
						client.sendToClient(cls);//sends currSemester to SecretaryController.
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
					case "Submission upload":					
						HashMap<String,byte[]> submission =  (HashMap<String, byte[]>) message.get("Submission upload");
						for(String name : submission.keySet()){
						try {
							Files.write((Paths.get("Students solutions\\"+name)), (byte[])submission.get(name));
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
				    	dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
						localDate = LocalDate.now();
						ans=(ArrayList<String>) message.get(key);
						query = "INSERT INTO task_in_class_in_course (class_in_course_id,task_file,submission_date,release_date) VALUES (?,?,?,?)";
						pstmt = conn.prepareStatement(query);
						pstmt.setInt(1, Integer.parseInt(ans.get(0)));
						pstmt.setString(2, ans.get(1));
						pstmt.setDate(3, Date.valueOf(ans.get(2)));
						pstmt.setDate(4, Date.valueOf(dtf.format(localDate)));
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
				     case "get class in course id for course id":
				         ans= (ArrayList<String>)message.get(key);
				         query= "SELECT id  FROM class_in_course WHERE course_id="+Integer.parseInt(ans.get(0));
				         stmt = conn.createStatement();
				         rs = stmt.executeQuery(query);
				         ans.clear();
				         while (rs.next()) { 
				          ans.add(rs.getString(1));
				         }
				         stmt.close();
				         rs.close();
				         client.sendToClient(ans);
				         break;
				     case "get tasks id":
				         ans= (ArrayList<String>)message.get(key);
				         query= "SELECT id FROM task_in_class_in_course WHERE class_in_course_id='"+ans.get(0)+"'";
				         stmt = conn.createStatement();
				         rs = stmt.executeQuery(query);
				         ans.clear();
				         while (rs.next()) { 
				          ans.add(rs.getString(1));
				         }
				         stmt.close();
				         rs.close();
				         client.sendToClient(ans);
				         break;
				     case "get file name for task id":
				         ans= (ArrayList<String>)message.get(key);
				         query= "SELECT task_file FROM task_in_class_in_course WHERE id='"+ans.get(0)+"'";
				         stmt = conn.createStatement();
				         rs = stmt.executeQuery(query);
				         ans.clear();
				         while (rs.next()) { 
				          ans.add(rs.getString(1));
				         }
				         stmt.close();
				         rs.close();
				         client.sendToClient(ans);
				         break;
				     case "get file from filename":
				         ans= (ArrayList<String>)message.get(key);
				     	byte[] by = null;
				     	File file = new File("Documents\\"+ans.get(0));
				         try {
				 			by = Files.readAllBytes(file.toPath());
				 		} catch (IOException e) {
				 			// TODO Auto-generated catch block
				 			e.printStackTrace();
				 		}
				         client.sendToClient(by);
				         break;
				     case "get courses for semester":
				         ans= (ArrayList<String>)message.get(key);
				         query= "SELECT teaching_unit,id,name FROM course WHERE year="+Integer.parseInt(ans.get(0))+" AND semester="+Integer.parseInt(ans.get(1));
				         stmt = conn.createStatement();
				         rs = stmt.executeQuery(query);
				         ans.clear();
				         while (rs.next()) { 
				          ans.add(rs.getInt(1)+""+rs.getInt(2)+" - "+rs.getString(3));
				         }
				         stmt.close();
				         rs.close();
				         client.sendToClient(ans);
				         break;
				     case "get students for course":
				         ans= (ArrayList<String>)message.get(key);
				         query= "SELECT course_in_class_id,student_id FROM student_in_course_in_class WHERE course_in_class_id"
				         		+ " IN (SELECT id FROM class_in_course WHERE course_id="+Integer.parseInt(ans.get(0))+")";
				         stmt = conn.createStatement();
				         rs = stmt.executeQuery(query);
				         ans.clear();
				         while (rs.next()) { 
				          ans.add(rs.getInt(1)+" - "+rs.getString(2));
				         }
				         stmt.close();
				         rs.close();
				         client.sendToClient(ans);
				         break;
				     case "send remove request to manager":
				    	 dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
						localDate = LocalDate.now();
				         ans= (ArrayList<String>)message.get(key);
				         query= "INSERT INTO manager_request(secretary_id,details,status,date) VALUES (?,?,?,?)";
							pstmt = conn.prepareStatement(query);
							pstmt.setString(1, ans.get(0));
							pstmt.setString(2, "1:"+ans.get(1)+":"+ans.get(2));
							pstmt.setString(3, "Pending");
							pstmt.setDate(4,Date.valueOf(dtf.format(localDate)));
							pstmt.executeUpdate();
							client.sendToClient(null);
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
						int newSemester = 0;
						int newYear;
						if(currSemester.equals("")){//if there is no current semester in DB
							localDate = LocalDate.now();
							newYear=Integer.parseInt(DateTimeFormatter.ofPattern("yyyy/MM/dd").format(localDate).substring(0, 4));
							newSemester=1;
						}else{
							newYear=Integer.parseInt(currSemester.substring(0, 4));
							if(currSemester.charAt(4)=='1'){
								newSemester=2;
							}else{
								newSemester=2;
								newYear++;
							}
						}
						ArrayList<Object> semester = new ArrayList<Object>();
						semester.add(newYear);
						semester.add(newSemester == 1 ? 'A' : 'B');
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
						

					case "getStudentsInClass":
						ResultSet rs_users = null;
						HashMap<String, ArrayList<String>> students = new HashMap<String, ArrayList<String>>();
						ans=(ArrayList<String>) message.get(key);
						query = "SELECT id,pblocked FROM student WHERE class_id ="+ans.get(0);
						stmt = conn.createStatement();
						stmt2=conn.createStatement();
						rs=stmt.executeQuery(query);
						ans.clear();
						while(rs.next()){ 
							rs_users=stmt2.executeQuery("select first_name,last_name from users where user_name="+"'"+rs.getString(1)+"'");
							while(rs_users.next()){ 
								ans.add(rs_users.getString(1));
								ans.add(rs_users.getString(2));
								ans.add(rs.getString(2));
								students.put(rs.getString(1), new ArrayList<String>(ans));
							}
							ans.clear();
						}
						client.sendToClient(students);
						break;
						
					case "blockAccess":
						query = "UPDATE student SET pblocked=? WHERE id=?";
						pstmt = conn.prepareStatement(query);
							pstmt.setInt(1, 1);
							pstmt.setString(2,((ArrayList<String>) message.get(key)).get(0));
							pstmt.executeUpdate();
						client.sendToClient(null);
						break;
						
					case "returnAccess":
						query = "UPDATE student SET pblocked=? WHERE id=?";
						pstmt = conn.prepareStatement(query);
							pstmt.setInt(1, 0);
							pstmt.setString(2,((ArrayList<String>) message.get(key)).get(0));
							pstmt.executeUpdate();
						client.sendToClient(null);
						break;

					case "getPreCourses": // get preCourses and which classes studied this course
						ans= (ArrayList<String>)message.get(key); // ans [courseId]
						query = "SELECT pre_course_id FROM pre_courses WHERE course_id = '"+ans.get(0)+"'";
						HashMap <String,ArrayList<String>> preCourses = new HashMap <String,ArrayList<String>>();
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						ans.clear();
						while (rs.next()) {
							ans.add(rs.getString(1));
						}
						for(String courseId : ans){ // for each pre course id
							ArrayList<String> courseInClassId = new ArrayList<>();
							query = "SELECT id FROM class_in_course WHERE course_id = '"+courseId+"'";
							stmt = conn.createStatement();
							rs = stmt.executeQuery(query);
								while (rs.next()) 
									courseInClassId.add(rs.getString(1));
							preCourses.put(courseId, courseInClassId);
						}
						client.sendToClient(preCourses);
						break;
					case "getTeachersWorkingHours":
						ans= (ArrayList<String>)message.get(key); // ans [courseId]
						HashMap<String,Integer> teacherwh = new HashMap<String,Integer>();
						for(int k=0;k<ans.size();k++){
							query = "SELECT working_hours FROM teacher WHERE id = '"+ans.get(k)+"'";
							stmt = conn.createStatement();
							rs = stmt.executeQuery(query);
							while (rs.next()) {
								teacherwh.put(ans.get(k),rs.getInt(1));
							}
						}

						client.sendToClient(teacherwh);
						break;
					case "addClass_in_courseRow":
						ans= (ArrayList<String>)message.get(key); // 
						query = "INSERT INTO class_in_course (course_id,class_id,teacher_id) VALUES ('"+ans.get(0)+"','"+ans.get(1)+"','"+ans.get(2)+"')";
						stmt = conn.createStatement();
						int k=-1;
						stmt.executeUpdate(query);
						query = "SELECT MAX(id) FROM class_in_course";
						rs = stmt.executeQuery(query);
						while(rs.next())
							k = rs.getInt(1);
						client.sendToClient(k);
						break;
						
						
					case "updateTeacherWorkingHours":
						ans= (ArrayList<String>)message.get(key); // 
						query  = "SELECT working_hours FROM teacher WHERE id='"+ans.get(0)+"'";
						int wh=0;
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						while(rs.next())
							wh = rs.getInt(1);
						wh+=Integer.parseInt(ans.get(1));
						query  = "UPDATE teacher SET working_hours='"+wh+"' WHERE id='"+ans.get(0)+"'";
						stmt.executeUpdate(query);
						client.sendToClient(null);
						break;
					case "getStudentsOfClass":
						ans= (ArrayList<String>)message.get(key); //
						HashMap<String,String> studentsOfClass = new HashMap<>();
						query  = "SELECT s.id,u.first_name,u.last_name FROM student s,users u WHERE s.class_id='"+ans.get(0)+"' AND s.id=u.user_name";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						ans.clear();
						while(rs.next())
							studentsOfClass.put(rs.getString(1), rs.getString(2)+" "+rs.getString(3));

						client.sendToClient(studentsOfClass);
						break;
					case "checkPreCourseFromArray":
						boolean passed = false;
						ans= (ArrayList<String>)message.get(key); // 
						String sid = ans.remove(0);
			
						for(String courseInClassId : ans){	
							query  = "SELECT grade FROM student_in_course_in_class WHERE course_in_class_id='"+courseInClassId+"' AND student_id='"+sid+"'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						int grade=-1;
						while(rs.next()){
								grade = rs.getInt(1);
							}
						if(grade == -1) //student was not in this specific class
							continue; // keep looking
						else{ // student was in this specific class
							if(grade >= 55){ // student has this preRequisite
								passed = true;
							}
						}
						}

						client.sendToClient(passed);
						ans.clear();
						break;
					case "AssignStudentsToClassInCourse":
						ans= (ArrayList<String>)message.get(key); // 
						String cicid = ans.remove(0);
						for(String studentid : ans){
							query  = "INSERT INTO student_in_course_in_class (course_in_class_id,student_id) VALUES ('"+cicid+"','"+studentid+"')";
							stmt = conn.createStatement();
							stmt.executeUpdate(query);
						}
						client.sendToClient(null);
						break;
						
					case "getManagerRequets":
						if (ans!=null) ans.clear();
						else ans = new ArrayList<String>();
						query = "SELECT id,secretary_id,details,date FROM manager_request WHERE status = 'Pending'";
						HashMap <String,ArrayList<String>> requests = new HashMap <String,ArrayList<String>>();
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						while (rs.next()) {
							ans.add(rs.getString(2));
							ans.add(rs.getString(3));
							ans.add(rs.getString(4));
							requests.put(rs.getString(1), new ArrayList<String>(ans));
							ans.clear();
						}
						client.sendToClient(requests);
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
