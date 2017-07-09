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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.xml.crypto.dsig.spec.HMACParameterSpec;

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
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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

					case "getBlockedStudents":
						ArrayList<String> childfn=new ArrayList<String>();
						ans=(ArrayList<String>) message.get(key);
						stmt = conn.createStatement();
						rs = stmt.executeQuery(ans.get(0));
						ans.clear();
						while (rs.next()) { 
							childfn.add(rs.getString(1)+ " " +rs.getString(2) +" belongs to class "+rs.getString(3));
						}
						stmt.close();
						rs.close(); 
						client.sendToClient(childfn);
						break;
					case "get course in class id":
						ans= (ArrayList<String>)message.get(key);
						query= "SELECT id  FROM class_in_course WHERE course_id="+ans.get(0)+" AND class_id="+ans.get(1)+" AND teacher_id='"+ans.get(2)+"'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						ans.clear();
						String class_in_course="";
						while (rs.next()) { 
							class_in_course+=rs.getString(1);// course in class id
						}
						stmt.close();
						rs.close();
						client.sendToClient(class_in_course);
						break;
					case "get teacher fullname":
						String teacher_id= (String)message.get(key); // 
						query = "SELECT first_name,last_name FROM users WHERE user_name='"+teacher_id+"'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						String tname="";
						while(rs.next())
							tname+=rs.getString(1)+" "+rs.getString(2);
						client.sendToClient(tname);
						break;
					case "get teachers from teaching_unit":
						int teaching_u=(Integer)message.get(key);
						query = "SELECT teacher_id FROM teacher_teaching_unit WHERE teaching_unit='"+teaching_u+"'";
						stmt = conn.createStatement();
						rs=stmt.executeQuery(query);
						ArrayList<String> teacherss=new ArrayList<String>();
						while (rs.next()) { 
							teacherss.add(rs.getString(1));
						}
						stmt.close();
						client.sendToClient(teacherss);
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
					case "send change teacher appointment to manager":
						dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
						localDate = LocalDate.now();
						ans= (ArrayList<String>)message.get(key);
						query= "INSERT INTO manager_request(secretary_id,details,status,date) VALUES (?,?,?,?)";
						pstmt = conn.prepareStatement(query);
						pstmt.setString(1, ans.get(0));
						pstmt.setString(2, "3:"+ans.get(1)+":"+ans.get(2)+":"+ans.get(3)+":"+ans.get(4)+":"+ans.get(5)+":"+ans.get(6));
						pstmt.setString(3, "Pending");
						pstmt.setDate(4,Date.valueOf(dtf.format(localDate)));
						pstmt.executeUpdate();
						client.sendToClient(null);
						break;
					case "get teacher class in course":
						ans=(ArrayList<String>) message.get(key);
						String teacher_name="";
						query = "SELECT teacher_id FROM class_in_course WHERE course_id='"+ans.get(0)+"' AND class_id='"+ans.get(1)+"'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						while (rs.next()) { 
							teacher_name+=(rs.getString(1));
						}
						stmt.close();
						rs.close();
						client.sendToClient(teacher_name);

						break;
					case "get course teaching_unit":
						ans=(ArrayList<String>) message.get(key);
						query = "SELECT teaching_unit FROM course WHERE id="+ans.get(0)+" AND year="+ans.get(1)+" AND semester="+ans.get(2);
						stmt = conn.createStatement();
						rs=stmt.executeQuery(query);
						String tu="";
						while (rs.next()) { 
							tu+=rs.getString(1);
						}
						stmt.close();
						client.sendToClient(tu);
						break;

					case "get id":
						ans=(ArrayList<String>) message.get(key);
						String teacher_un="";                  
						query = "SELECT user_name FROM users WHERE first_name='"+ans.get(0)+"'AND last_name='"+ans.get(1)+"'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						while (rs.next()) { 
							teacher_id =(rs.getString(1));
						}
						stmt.close();
						rs.close();
						client.sendToClient(teacher_un);
						break;


					case "get teacher id for course id":
						ans=(ArrayList<String>) message.get(key);
						String teacherid="";
						query = "SELECT teacher_id FROM class_in_course WHERE course_id='"+ans.get(0)+"'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						while (rs.next()) { 
							teacherid =(rs.getString(1));
						}
						stmt.close();
						rs.close();
						client.sendToClient(teacherid);
						break;
					case "send mail to teacher":
						ans=(ArrayList<String>) message.get(key);
						query = "INSERT INTO teacher_inbox (teacher_id,msg) VALUES (?,?)";
						pstmt = conn.prepareStatement(query);
						pstmt.setString(1, ans.get(0));
						pstmt.setString(2, ans.get(1));
						pstmt.executeUpdate();
						ans.clear();
						pstmt.close();							
						client.sendToClient(null);
						break;
					case "get submission date for task id":
						ans=(ArrayList<String>) message.get(key);
						query = "SELECT submission_date FROM task_in_class_in_course WHERE id='"+ans.get(0)+"'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						ans.clear();
						while (rs.next()) { 
							ans.add(String.valueOf(rs.getDate(1)));
						}
						stmt.close();
						rs.close();
						client.sendToClient(ans);
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
					case "add task to student":
						ans=(ArrayList<String>) message.get(key);
						query = "INSERT INTO task_of_student_in_course (student_id,task_id,course_id,submission_file) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE submission_file=?";
						pstmt = conn.prepareStatement(query);
						pstmt.setString(1, ans.get(0));
						pstmt.setInt(2,Integer.parseInt( ans.get(1)));
						pstmt.setInt(3, Integer.parseInt(ans.get(2)));
						pstmt.setString(4, ans.get(3));
						pstmt.setString(5, ans.get(3));
						pstmt.executeUpdate();

						ans.clear();
						pstmt.close();
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
						query = "SELECT class_in_course.course_id, course.teaching_unit FROM class_in_course,course WHERE class_in_course.teacher_id='"+sans+"' AND class_in_course.course_id=course.id";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						while (rs.next()) { 
							ans.add(rs.getString(2)+""+rs.getString(1));
						}
						stmt.close();
						rs.close();
						client.sendToClient(ans);
						break;
					case "get course teaching unit":
						ans=(ArrayList<String>) message.get(key);
						ArrayList<String> answ=new ArrayList<String>();
						for(int k=0;k<ans.size();k++){
							query = "SELECT teaching_unit FROM course where id='"+ans.get(k)+"'";
							stmt = conn.createStatement();
							rs = stmt.executeQuery(query);
							while (rs.next()) { 
								if(!answ.contains(rs.getString(1)))
									answ.add(rs.getString(1));
							}

							stmt.close();
							rs.close();
						}

						client.sendToClient(answ);
						break;
					case "Search for course name":

						ans = (ArrayList<String>) message.get("Search for course name");
						ArrayList<String> newans = new ArrayList<String>();
						for(int i=0;i<ans.size();i++)
						{
							query = "SELECT name FROM course WHERE id='"+ans.get(i).substring(2)+"'";
							stmt = conn.createStatement();
							rs = stmt.executeQuery(query);
							while (rs.next()) {
								if(!newans.contains(rs.getString(1)))
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
					case "insert task grade":
						ans = (ArrayList<String>)message.get(key);
						query = "UPDATE task_of_student_in_course SET task_grade=?,comments=? WHERE student_id=? AND task_id=? AND course_id=?";
						pstmt = conn.prepareStatement(query);
						pstmt.setFloat(1, Float.parseFloat(ans.get(3)));
						pstmt.setString(3,ans.get(0));
						pstmt.setInt(4,Integer.parseInt(ans.get(1)));
						pstmt.setInt(5,Integer.parseInt(ans.get(2)));
						pstmt.setString(2, ans.get(4));
						pstmt.executeUpdate();
						client.sendToClient(null);
						break;

					case "Search for class_in_course_id":
						ans=(ArrayList<String>) message.get(key);
						int clsid=0;
						int cid=0;
						if (ans.get(0).length()>2 && ans.get(1).length()>0&& ans.get(0)!=null && ans.get(1)!=null){

							cid=Integer.parseInt(ans.get(0).substring(2, 5));
							clsid=Integer.parseInt(ans.get(1));
						}
						query = "Select id FROM class_in_course WHERE course_id="+cid+" AND class_id="+clsid;

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
					case "Create task":
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
					case "get user full name":
						ArrayList<String> fullname=new ArrayList<String>();
						ans= (ArrayList<String>)message.get(key);
						query= "SELECT first_name,last_name  FROM users WHERE user_name='"+ans.get(0)+"'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						ans.clear();
						while (rs.next()) { 
							fullname.add(rs.getString(1) + " " + rs.getString(2));
						}
						stmt.close();
						rs.close();
						client.sendToClient(fullname);
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
					case "get course id from course name":
						ans = (ArrayList<String>) message.get("get course id from course name");
						ArrayList<String> crsid = new ArrayList<String>();

						query = "SELECT id FROM course WHERE name='"+ans.get(0)+"'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						while (rs.next()) {
							if(!(crsid.contains(rs.getString(1))))
								crsid.add(rs.getString(1));
						}
						stmt.close();


						ans.clear();
						client.sendToClient(crsid);
						break;
					case "check if student in class in course":
						//ans=[class_in_course_id,student_id]
						ans= (ArrayList<String>)message.get(key);
						boolean exists=false;
						query= "SELECT *  FROM student_in_course_in_class WHERE course_in_class_id='"+ans.get(0)+"' AND student_id='"+ans.get(1)+"'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						ans.clear();
						while (rs.next()) { 
							exists=true;
						}
						stmt.close();
						rs.close();
						client.sendToClient(exists);
						break;
					case "get course id":
						ans = (ArrayList<String>) message.get("get course id");
						ArrayList<String> coursesid = new ArrayList<String>();
						for(i=0;i<ans.size();i++)
						{
							query = "SELECT DISTINCT class_in_course.course_id, course.teaching_unit FROM class_in_course,course WHERE class_in_course.id="+ans.get(i)+" AND class_in_course.course_id=course.id";
							stmt = conn.createStatement();
							rs = stmt.executeQuery(query);
							while (rs.next()) { 
								coursesid.add(String.valueOf(rs.getInt(2))+""+String.valueOf(rs.getInt(1)));
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
							query = "SELECT course_id,id FROM class_in_course WHERE id="+Integer.parseInt(ans.get(i))+" AND course_id"
									+ " IN (SELECT course.id FROM course where year="+Integer.parseInt(ans.get(0))+" AND semester="+Integer.parseInt(ans.get(1))+")";
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
					case "get status for task of student":
						ans = (ArrayList<String>) message.get(key);
						ArrayList<String> tstatus = new ArrayList<String>();
						query = "SELECT task_grade,comments FROM task_of_student_in_course WHERE course_id="+Integer.valueOf(ans.get(2))+" AND student_id='"+ans.get(0)+"' AND task_id="+ans.get(1);
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query); 
						while (rs.next()) { 
							tstatus.add(String.valueOf(rs.getFloat(1)));
							tstatus.add(rs.getString(2));
						}
						stmt.close();
						rs.close();

						client.sendToClient(tstatus);
						break;
					case "get class in course id for course id":
						ans= (ArrayList<String>)message.get(key);
						query= "SELECT id FROM class_in_course WHERE course_id="+Integer.parseInt(ans.get(0));
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
					case "get class in course id for course id and class id":
						ans= (ArrayList<String>)message.get(key);
						query= "SELECT id FROM class_in_course WHERE course_id="+Integer.parseInt(ans.get(0))+" AND class_id="+Integer.parseInt(ans.get(1));
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
					case "get students in class":		
						ans=(ArrayList<String>) message.get(key);
						query = "SELECT id FROM student WHERE class_id ="+Integer.parseInt((ans.get(0)));
						stmt = conn.createStatement();
						rs=stmt.executeQuery(query);
						ans.clear();
						while(rs.next()){ 
							ans.add(rs.getString(1));
						}
						client.sendToClient(ans);
						break;
					case "get tasks":
						ans= (ArrayList<String>)message.get(key);						
						query= "SELECT student_id,task_grade FROM task_of_student_in_course WHERE course_id="+Integer.parseInt(ans.get(1).substring(2))+" AND task_id="+Integer.parseInt(ans.get(0));
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						ans.clear();
						String st="";
						while (rs.next()) {	 
							st+=(rs.getString(1));
							if(rs.getFloat(2)!=0)
								st+=" - "+String.valueOf(rs.getFloat(2));
							ans.add(st);
							st="";
						}
						stmt.close();
						rs.close();
						client.sendToClient(ans);
						break;
					case "get tasks id":
						ans= (ArrayList<String>)message.get(key);
						query= "SELECT id FROM task_in_class_in_course WHERE class_in_course_id="+Integer.parseInt(ans.get(0));
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
						query= "SELECT task_file FROM task_in_class_in_course WHERE id="+Integer.parseInt(ans.get(0));
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
					case "get solution file of student":
						ans= (ArrayList<String>)message.get(key);
						query= "SELECT submission_file FROM task_of_student_in_course WHERE student_id='"+ans.get(0)+"' AND task_id="+ans.get(1)+" AND course_id="+ans.get(2);
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
					case "get file from filename for solution file":
						ans= (ArrayList<String>)message.get(key);
						byte[] byt = null;
						File solfile = new File("Students solutions\\"+ans.get(0));
						try {
							byt = Files.readAllBytes(solfile.toPath());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						client.sendToClient(byt);
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
					case "update grade of student in class in course":
						ans=(ArrayList<String>) message.get(key);
						query = "UPDATE student_in_course_in_class SET grade='"+ans.get(2)+"' WHERE student_id='"+ans.get(0)+"' AND course_in_class_id='"+ans.get(1)+"'";
						stmt = conn.createStatement();
						stmt.executeUpdate(query);
						ans.clear();
						stmt.close();
						client.sendToClient(null);
						break;
					case "get child id":
						String str=(String)message.get(key);
						query= "SELECT child_id FROM children_of_parent WHERE parent_id='"+str+"'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						ArrayList<String> childId=new ArrayList<String>();
						while (rs.next()) { 
							childId.add(rs.getString(1));
						}
						stmt.close();
						rs.close();
						client.sendToClient(childId);
						break;
					case "get child username":
						ArrayList<String> cidarr=(ArrayList<String>)message.get(key);
						ArrayList<String> users=new ArrayList<String>();
						for(int j=0;j<cidarr.size();j++){
							query= "SELECT first_name FROM users WHERE user_name='"+cidarr.get(j)+"'";
							stmt = conn.createStatement();
							rs = stmt.executeQuery(query);
							while (rs.next()) { 
								users.add(rs.getString(1));
							}

							stmt.close();
							rs.close();
						}
						client.sendToClient(users);
						break;
					case "get block status":
						ArrayList<String> child=(ArrayList<String>)message.get(key);
						ArrayList<String> isBlocked =new ArrayList<String>();
						for(int j=0;j<child.size();j++){
							query= "SELECT pblocked FROM Student WHERE id='"+child.get(j)+"'";
							stmt = conn.createStatement();
							rs = stmt.executeQuery(query);
							while (rs.next()) { 
								isBlocked.add(rs.getString(1));
							}

							stmt.close();
							rs.close();
						}
						client.sendToClient(isBlocked);
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
					case "get students for course in class":
						ans= (ArrayList<String>)message.get(key);
						String qu1 = "SELECT cic.id,cl.id FROM class_in_course cic,class cl WHERE cic.class_id=cl.id AND cl.year='"+ans.get(1)+"' AND cl.semester='"+ans.get(2)+"' AND cic.course_id='"+ans.get(0)+"'";
						ArrayList<String> cicGS = new ArrayList<>();
						stmt = conn.createStatement();
						stmt2 = conn.createStatement();
						ResultSet rss2;
						rs = stmt.executeQuery(qu1);
						while (rs.next()) { 
							String qu2 = "SELECT student_id FROM student_in_class WHERE class_id='"+rs.getString(2)+"'";

							rss2 = stmt2.executeQuery(qu2);
							while (rss2.next()) {
								cicGS.add(rs.getString(1)+" - "+rss2.getString(1));
							}
						}

						stmt.close();
						rs.close();
						client.sendToClient(cicGS);
						break;

					case "send remove request to manager":
						dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
						localDate = LocalDate.now();
						ans= (ArrayList<String>)message.get(key);
						query= "INSERT INTO manager_request(secretary_id,details,status,date) VALUES (?,?,?,?)";
						pstmt = conn.prepareStatement(query);
						pstmt.setString(1, ans.get(0));
						pstmt.setString(2, "1:"+ans.get(1)+":"+ans.get(2)+":"+ans.get(3)+":"+ans.get(4)+":"+ans.get(5));
						pstmt.setString(3, "Pending");
						pstmt.setDate(4,Date.valueOf(dtf.format(localDate)));
						pstmt.executeUpdate();
						client.sendToClient(null);
						break;
					case "send add request to manager":
						dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
						localDate = LocalDate.now();
						ans= (ArrayList<String>)message.get(key);
						query= "INSERT INTO manager_request(secretary_id,details,status,date) VALUES (?,?,?,?)";
						pstmt = conn.prepareStatement(query);
						pstmt.setString(1,ans.get(0));
						pstmt.setString(2,ans.get(1));
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
								newSemester=1;
								newYear++;
							}
						}
						ArrayList<Object> semester = new ArrayList<Object>();
						semester.add(newYear);
						semester.add(newSemester == 1 ? 'A' : 'B');
						client.sendToClient(semester);//sends next Semester details to SecretaryController.
						break;
					case "defineNewSemester":
						ans= (ArrayList<String>)message.get(key);
						int sem = ans.get(1).equals("A")? 1 : 2;
						query = "SELECT year,sem FROM semester WHERE year = '"+ans.get(0)+"' AND sem = '"+sem+"'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						boolean checker = false;
						while(rs.next())
							checker = true;
						int year = Integer.parseInt(ans.get(0));
						sem = ans.get(1).equals("A")? 1 : 2;
						if(sem == 1){year--;sem=2;}else{sem=1;}
						query = "UPDATE semester SET iscurrent = 0 WHERE year = '"+year+"' AND sem = '"+sem+"'";
						stmt.executeUpdate(query);
						sem = ans.get(1).equals("A")? 1 : 2;
						if(checker == true){ // already exists
							query = "UPDATE semester SET iscurrent = 1 WHERE year = '"+ans.get(0)+"' AND sem = '"+sem+"'";
							stmt.executeUpdate(query);
						}else{
							query = "INSERT INTO semester (year,sem,iscurrent) VALUES('"+ans.get(0)+"','"+sem+"','1')";
							stmt.executeUpdate(query);
						}
						client.sendToClient(null);//sends the answer to client.
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
						pstmt.setInt(3, Integer.parseInt(ans.get(2)));
						pstmt.setInt(4, Integer.parseInt(ans.get(3)));
						pstmt.setInt(5, Integer.parseInt(ans.get(4)));
						pstmt.setInt(6, Integer.parseInt(ans.get(5)));
						try{
							pstmt.executeUpdate();
						}
						catch(SQLException e){
							System.err.println("course id already defined");
						}
						//client.sendToClient(ans);//sends the answer to client.
						client.sendToClient(null);
						break;
						
					case "check if course exists":
						ans=(ArrayList<String>) message.get(key);
						query = "SELECT * FROM Course WHERE id='"+ans.get(1)+"' AND teaching_unit='"+ans.get(2)+"'";
						stmt = conn.createStatement();
						rs=stmt.executeQuery(query);
						if(rs.next()) client.sendToClient("exist");
						else
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
						Statement st22 = conn.createStatement();
						int classId = Integer.parseInt(studentDetails.get(0));
						studentDetails.remove(0);
						for(String studentId : studentDetails){
							st22.executeUpdate("INSERT INTO student_in_class (student_id,class_id) VALUES ('"+studentId+"','"+classId+"')");
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
						while(rs.next()){ //id:first name,last name,pBlocked
							rs_users=stmt2.executeQuery("select first_name,last_name from users where user_name="+"'"+rs.getString(1)+"'");
							while(rs_users.next()){ 
								ans.add(rs_users.getString(1));
								ans.add(rs_users.getString(2));
								ans.add(rs.getString(2));
								students.put(rs.getString(1), new ArrayList<String>(ans));
								ans.clear();
							}

						}
						client.sendToClient(students);//id:first name,last name,pBlocked
						break;


					case "getStudentsInClassVAI":
						rs_users = null;
						HashMap<String, ArrayList<String>> studentsInClass = new HashMap<String, ArrayList<String>>();
						ans=(ArrayList<String>) message.get(key);
						query = "SELECT student_id FROM student_in_class WHERE class_id ="+ans.get(0);
						stmt = conn.createStatement();
						stmt2=conn.createStatement();
						rs=stmt.executeQuery(query);
						ans.clear();
						while(rs.next()){ //id:first name,last name,pBlocked
							rs_users=stmt2.executeQuery("select first_name,last_name from users where user_name="+"'"+rs.getString(1)+"'");
							while(rs_users.next()){ 
								ans.add(rs_users.getString(1));
								ans.add(rs_users.getString(2));
								studentsInClass.put(rs.getString(1), new ArrayList<String>(ans));
								ans.clear();
							}

						}
						client.sendToClient(studentsInClass);//id:first name,last name,pBlocked
						break;


					case "getStudentsInCourseInClass":
						ResultSet rs_usersGS = null;
						HashMap<String, ArrayList<String>> studentsGS = new HashMap<String, ArrayList<String>>();
						ans=(ArrayList<String>) message.get(key);
						query = "SELECT student_id FROM student_in_class WHERE class_id ="+ans.get(0);
						stmt = conn.createStatement();
						stmt2=conn.createStatement();
						rs=stmt.executeQuery(query);
						ans.clear();
						while(rs.next()){ 
							rs_users=stmt2.executeQuery("select first_name,last_name from users where user_name="+"'"+rs.getString(1)+"'");
							while(rs_users.next()){ 
								ans.add(rs_users.getString(1));
								ans.add(rs_users.getString(2));

								studentsGS.put(rs.getString(1), new ArrayList<String>(ans));
							}
							ans.clear();
						}
						client.sendToClient(studentsGS);//id:first name,last name,pBlocked
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

					case "getSecretayInbox":
						ans= (ArrayList<String>)message.get(key); //
						HashMap<String,String> secMsg = new HashMap<>();
						query  = "SELECT msg_id,msg FROM secretary_inbox WHERE secretary_id='"+ans.get(0)+"'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						ans.clear();
						while(rs.next())
							secMsg.put(rs.getString(1), rs.getString(2));

						client.sendToClient(secMsg);
						break;

					case "getTeacherInbox":
						ans= (ArrayList<String>)message.get(key); //
						HashMap<String,String> teacherMsg = new HashMap<>();
						query  = "SELECT msg_id,msg FROM teacher_inbox WHERE teacher_id='"+ans.get(0)+"'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						ans.clear();
						while(rs.next())
							teacherMsg.put(rs.getString(1), rs.getString(2));

						client.sendToClient(teacherMsg);
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
					case "getClassOfCourseAS":
						ans= (ArrayList<String>)message.get(key); // 
						HashMap<String,ArrayList<String>> classesInCourseAS = new HashMap<>();
						query  = "SELECT c.name,c.id,cic.id FROM class_in_course cic, class c WHERE cic.course_id='"+ans.get(2)+"' AND cic.class_id=c.id AND c.year='"+ans.get(0)+"' AND c.semester='"+ans.get(1)+"'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						while(rs.next()){
							ArrayList<String> t = new ArrayList<>();
							t.add(rs.getString(2));
							t.add(rs.getString(1));
							classesInCourseAS.put(rs.getString(3), t);
						}
						client.sendToClient(classesInCourseAS);
						break;
					case "getClassesOfCourseSM":
						ans= (ArrayList<String>)message.get(key); // 
						HashMap<String,ArrayList<String>> classesInCourseSM = new HashMap<>();
						query  = "SELECT c.name,c.id,cic.id, u.first_name, u.last_name FROM class_in_course cic, class c, users u WHERE cic.course_id='"+ans.get(2)+"' AND cic.class_id=c.id AND c.year='"+ans.get(0)+"' AND c.semester='"+ans.get(1)+"' AND cic.teacher_id=u.user_name";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);

						while(rs.next()){
							ArrayList<String> t = new ArrayList<>();
							t.add(rs.getString(2));
							t.add(rs.getString(1));
							t.add(rs.getString(4)+" "+rs.getString(5));
							classesInCourseSM.put(rs.getString(3), t);

						}
						client.sendToClient(classesInCourseSM);
						break;
					case "getTeachersAndClassesGSR":
						String currYear="";
						String currSem="";
						String bottomYear="";
						String bottomSem="";

						ArrayList<Object> arrGSR= new ArrayList<Object>();
						ArrayList<String> tempList = new ArrayList<String> ();
						ArrayList<String> semesters = new ArrayList<String> ();
						ArrayList<ArrayList<String>> teacher = new ArrayList<ArrayList<String>> ();
						ArrayList<ArrayList<String>> classes = new ArrayList<ArrayList<String>> ();
						stmt = conn.createStatement();

						rs=stmt.executeQuery("select * from semester");
						while(rs.next()){
							if(rs.getString(3).equals("1")) {
								currYear=rs.getString(1);
								currSem=rs.getString(2);
							}else semesters.add(rs.getString(1)+rs.getString(2));	
						}
						Collections.sort(semesters);

						rs = stmt.executeQuery("select * from class where year="+currYear+" AND semester="+currSem);
						while(rs.next()){
							tempList.add(rs.getString(1));
							tempList.add(rs.getString(2));
							tempList.add(rs.getString(3));
							tempList.add(rs.getString(4));
							classes.add(new ArrayList<String>(tempList));
							tempList.clear();
						}

						rs = stmt.executeQuery("SELECT "+
								"DISTINCT cic.teacher_id,u.first_name,u.last_name"+
								" FROM "+
								"course co,class_in_course cic,class cl,users u"+
								" WHERE"+
								"(co.semester < "+currSem+" OR co.year < "+currYear+") AND "+
								"cic.course_id= co.id AND "+
								"cic.class_id = cl.id AND cl.year = co.year AND cl.year=co.year AND "+
								"u.user_name = cic.teacher_id");

						while(rs.next()){
							tempList.add(rs.getString(1));
							tempList.add(rs.getString(2)+" "+rs.getString(3));

							teacher.add(new ArrayList<String>(tempList));
							tempList.clear();
						}

						arrGSR.add(semesters);
						arrGSR.add(teacher);
						arrGSR.add(classes);

						client.sendToClient(arrGSR);
						break;

					case "approveRequest":
						try{
							stmt = conn.createStatement();
							ans= (ArrayList<String>)message.get(key);
							stmt.executeUpdate(ans.get(0));
						}catch (SQLException e) {
							client.sendToClient(null);
						} 
						client.sendToClient(null);
						break;

					case "notifySecretary":
						stmt = conn.createStatement();
						ans= (ArrayList<String>)message.get(key);
						stmt.executeUpdate("INSERT INTO secretary_inbox VALUES("+"0"+",'"+ans.get(0)+"','"+ans.get(1)+"')");
						client.sendToClient(null);
						break;

					case "changeRequestStatus":
						stmt = conn.createStatement();
						ans= (ArrayList<String>)message.get(key);
						stmt.executeUpdate("UPDATE manager_request SET status='"+ans.get(0)+"' WHERE id="+ans.get(1));
						client.sendToClient(null);
						break;
					case "GetReportGSR":
						ans= (ArrayList<String>)message.get(key); 
						//[operation,arbitrator,period]
						//operation values: ""All Classes of a teacher","All Teachers of a class","All Courses of a class"
						//arbitrator values:teacher,class,class
						//period values: yyyys
						String currY="",currS="";
						stmt = conn.createStatement();
						rs=stmt.executeQuery("select * from semester");
						while(rs.next()){
							if(rs.getString(3).equals("1")) {
								currY=rs.getString(1);
								currS=rs.getString(2);
							}
						}
						HashMap<String,ArrayList<String>> report = new HashMap<>();
						int currP = Integer.parseInt(currY+currS);
						int destP = Integer.parseInt(ans.get(2));
						String pYear = ans.get(2).substring(0, 4);
						String pSem = ans.get(2).substring(4);

						String q;
						Statement st2 = conn.createStatement();
						ResultSet res2;
						switch(ans.get(0)){
						case"All Classes of a teacher":
							//query on loop by period

							for(;destP<currP;){
								pYear = String.valueOf(destP).substring(0, 4);
								pSem = String.valueOf(destP).substring(4);

								ArrayList<String> vals = new ArrayList<>();
								q = "SELECT cl.name,co.name,cic.id FROM course co,class_in_course cic,class cl WHERE cic.teacher_id = '"+ans.get(1)+"' AND cic.class_id=cl.id AND cic.course_id=co.id AND cl.year=co.year AND cl.semester = co.semester AND cl.year = '"+pYear+"' AND cl.semester='"+pSem+"'";
								rs=stmt.executeQuery(q);

								while(rs.next()){
									res2 = st2.executeQuery("SELECT AVG(grade) FROM student_in_course_in_class WHERE course_in_class_id='"+rs.getString(3)+"'");
									int avg=-1;
									while(res2.next()) avg = res2.getInt(1);

									vals.add(rs.getString(1)+","+rs.getString(2)+","+avg);
								}
								report.put(String.valueOf(destP), vals);
								destP = destP%10 == 1 ? destP+1 : ((destP/10)+1)*10+1;//get next semester
							}

							break;
						case"All Teachers of a class":
							q="SELECT DISTINCT cic.teacher_id FROM class_in_course cic, class cl WHERE cl.name='"+ans.get(1)+"' AND cl.id=cic.class_id";
							rs = stmt.executeQuery(q);
							ArrayList<String> tteachers = new ArrayList<>();
							while(rs.next()) tteachers.add(rs.getString(1));
							int preserveDestP = destP;
							for(String tt:tteachers){// for each teacher
								for(destP=preserveDestP;destP<currP;){ // for each teacher in each semester
									pYear = String.valueOf(destP).substring(0, 4);
									pSem = String.valueOf(destP).substring(4);
									q="SELECT AVG(grade),co.name FROM class_in_course cic, class cl,student_in_course_in_class sicic,course co WHERE cic.teacher_id='"+tt+"'  AND cl.id=cic.class_id AND cl.year='"+pYear+"' AND cl.semester='"+pSem+"' AND cic.id=sicic.course_in_class_id AND co.id = cic.course_id GROUP BY cic.id";
									res2 = st2.executeQuery(q);
									ArrayList<String> vals = new ArrayList<>();
									int avg=-1;
									while(res2.next()){
										avg = res2.getInt(1);
										vals.add(res2.getString(2)+","+avg);
									}
									report.put(tt+","+pYear+","+pSem, vals);
									destP = destP%10 == 1 ? destP+1 : ((destP/10)+1)*10+1;//get next semester
								}
							}

							break;
						case"All Courses of a class":
							for(;destP<currP;){ // for each teacher in each semester
								pYear = String.valueOf(destP).substring(0, 4);
								pSem = String.valueOf(destP).substring(4);
								q="SELECT AVG(sicic.grade),co.name FROM class cl,class_in_course cic,student_in_course_in_class sicic,course co WHERE cl.name='"+ans.get(1)+"' AND cl.year='"+pYear+"' AND cl.semester='"+pSem+"' AND cic.id=sicic.course_in_class_id AND co.id=cic.course_id AND cic.class_id = cl.id GROUP BY cic.id";
								rs = stmt.executeQuery(q);
								ArrayList<String> vals = new ArrayList<>();
								int avg = -1;
								while(rs.next()){
									avg = rs.getInt(1);
									vals.add(rs.getString(2)+","+avg);
								}
								report.put(pYear+","+pSem, vals);
								destP = destP%10 == 1 ? destP+1 : ((destP/10)+1)*10+1;//get next semester
							}
							break;
						}


						client.sendToClient(report);
						break;

					case "get course weekly hours":
						ans=(ArrayList<String>) message.get(key);
						query = "SELECT weekly_hours FROM course where id='"+Integer.parseInt(ans.get(0))+"' AND year="+ans.get(1)+" AND semester="+ans.get(2);
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						ans.clear();

						while (rs.next()) 
							ans.add(String.valueOf(rs.getInt(1)));

						client.sendToClient(ans);

						break;

					case "get teacher working hours":
						ans=(ArrayList<String>) message.get(key);
						query = "SELECT hours_limit,working_hours FROM teacher where id='"+ans.get(0)+"'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						ans.clear();
						while (rs.next()) { 
							ans.add(String.valueOf(rs.getInt(1)));
							ans.add(String.valueOf(rs.getInt(2)));
						}
						client.sendToClient(ans);

						break;

					case "getSemesters":
						query = "SELECT * FROM semester";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						ans = new ArrayList<String>();
						while (rs.next()) { 
							ans.add(rs.getString(1)+rs.getString(2)+rs.getString(3));
						}
						stmt.close();
						rs.close(); 
						client.sendToClient(ans);
						break;

					case "getAllClassesOfSemester":
						HashMap<String,ArrayList<String>> hmAns1 = new HashMap<String,ArrayList<String>>();
						ans=(ArrayList<String>) message.get(key);
						stmt = conn.createStatement();
						rs = stmt.executeQuery(ans.get(0));
						ans.clear();
						while (rs.next()) { 
							ans.add(rs.getString(2));
							hmAns1.put(rs.getString(1), new ArrayList<String>(ans));
							ans.clear();
						}
						stmt.close();
						rs.close(); 
						client.sendToClient(hmAns1);
						break;

					case "getAllCoursesOfSemester":
						HashMap<String,ArrayList<String>> hmAns2 = new HashMap<String,ArrayList<String>>();
						ans=(ArrayList<String>) message.get(key);
						stmt = conn.createStatement();
						rs = stmt.executeQuery(ans.get(0));
						ans.clear();
						while (rs.next()) { 
							ans.add(rs.getString(2));
							ans.add(rs.getString(3));
							ans.add(rs.getString(4));
							hmAns2.put(rs.getString(1), new ArrayList<String>(ans));
							ans.clear();
						}
						stmt.close();
						rs.close(); 
						client.sendToClient(hmAns2);
						break;

					case "getClassesInCoursesOfSemester":
						HashMap<String,ArrayList<String>> c = new HashMap<String,ArrayList<String>>();
						ans=(ArrayList<String>) message.get(key);
						stmt = conn.createStatement();
						rs = stmt.executeQuery(ans.get(0));
						ans.clear();
						while (rs.next()) { 
							ans.add(rs.getString(2));
							ans.add(rs.getString(3));
							ans.add(rs.getString(4));
							ans.add(rs.getString(5));
							c.put(rs.getString(1), new ArrayList<String>(ans));
							ans.clear();
						}
						stmt.close();
						rs.close(); 
						client.sendToClient(c);
						break;

					case "getStudentsInCourse":
						HashMap<String,ArrayList<String>> s = new HashMap<String,ArrayList<String>>();
						ans=(ArrayList<String>) message.get(key);
						stmt = conn.createStatement();
						rs = stmt.executeQuery("select s.student_id,u.first_name,u.last_name,s.grade "
								+ "from users u,student_in_course_in_class s "
								+ "where u.user_name=s.student_id AND s.course_in_class_id="+ans.get(0));
						ans.clear();
						while (rs.next()) { 
							ans.add(rs.getString(2)+" "+rs.getString(3));
							ans.add(rs.getString(4));
							s.put(rs.getString(1), new ArrayList<String>(ans));
							ans.clear();
						}
						stmt.close();
						rs.close(); 
						client.sendToClient(s);
						break;
						
					case "Submission Test":
						ans=(ArrayList<String>) message.get(key);
						stmt = conn.createStatement();
						query="INSERT INTO task_of_student_in_course (student_id,task_id,course_id,submission_file) values(?,?,?,?) ";
						pstmt = conn.prepareStatement(query);
						pstmt.setString(1, ans.get(0));
						pstmt.setInt(2,Integer.parseInt(ans.get(1)));
						pstmt.setInt(3, Integer.parseInt(ans.get(2)));
						pstmt.setString(4,ans.get(3));
						pstmt.executeUpdate();
						rs = stmt.executeQuery("SELECT * FROM task_of_student_in_course WHERE student_id='"+ans.get(0)+"' AND task_id='"+ans.get(1)+"' AND course_id='"+ans.get(2)+"' AND submission_file='"+ans.get(3)+"'");
						if(rs.next())
							client.sendToClient("true");
						else client.sendToClient("false");
						break;
					case "check if classInCourseId exists":
						ans=(ArrayList<String>) message.get(key);
						stmt = conn.createStatement();
						rs = stmt.executeQuery("SELECT id FROM class_in_course WHERE id='"+ans.get(0)+"'");
						if(rs.next())
							client.sendToClient("true");
						else client.sendToClient("false");
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
