package JunitTests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import controller.LoginController;
import controller.SecretaryController;
import controller.UserClient;
import java.util.Collections;
import controller.SecretaryController.ClassInCourseRow;
import controller.SecretaryController.StudentsExp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TestCreateSemester {
	static public UserClient  userClient;
	public HashMap<String,String> studentsId;//
	public ArrayList<String> studentsToAssign;//
	public ObservableList<StudentsExp> ExceptionStudents;//
	public HashMap <String,ArrayList<String>> preCourses;//
	public ClassInCourseRow t = new ClassInCourseRow("YodB1");
	public HashMap <String,String> classConvertToId;//
	public HashMap<String,String> studentsWithoutGrade;
	public HashMap<String,String> studentsWithoutCourse;
	public HashMap<String,String> approvedStudents;
	SecretaryController sec = new SecretaryController();
	@Before
	public void setUp() throws Exception {
		try {
			 userClient = new UserClient("localhost",5555);
		} catch (Exception e1) {
			return;
		}
		LoginController.userClient = userClient;
		studentsId = new HashMap<String,String>();
		studentsToAssign = new ArrayList<String>();
		ExceptionStudents = FXCollections.observableArrayList();
		preCourses = new HashMap <String,ArrayList<String>>();
		classConvertToId = new HashMap<String,String>();
		initClassConvertToId();
		initPreCourses();
		initStudentsId();
	}



	/*
	@After
	public void tearDown() throws Exception {
	}*/
	/**
	 * sending students without pre course at all,
	 * Expecting to get back in ExceptionStudents the same list
	 */
	@Test
	public void testStudentsWitouhtPreCourse() {
		studentsToAssign = new ArrayList<>();
		ExceptionStudents = FXCollections.observableArrayList();
		sec.checkClassPreCourse(studentsWithoutCourse,studentsToAssign,ExceptionStudents,preCourses,t,classConvertToId);
		//** make array list of students without pre course for equal
		ArrayList<String> forTest = new ArrayList<>();
		for(String s: studentsWithoutCourse.keySet())
			forTest.add(s);
		//** change exception list to array list for equal
		ArrayList<String> expList = new ArrayList<>();
		for(StudentsExp se : ExceptionStudents){
			expList.add(se.getId());
		}
		//**
		assertEquals(expList, forTest);
	}
	/**
	 * sending students that have done this course but didnt passed him,
	 * Expecting to get back in ExceptionStudents the same list
	 */
	@Test
	public void testStudentsWitouhtPreGrade() {
		studentsToAssign = new ArrayList<>();
		ExceptionStudents = FXCollections.observableArrayList();
		sec.checkClassPreCourse(studentsWithoutGrade,studentsToAssign,ExceptionStudents,preCourses,t,classConvertToId);
		//** make array list of students without grade of pre course for equal
		ArrayList<String> forTest = new ArrayList<>();
		for(String s: studentsWithoutGrade.keySet())
			forTest.add(s);
		//** change exception list to array list for equal
		ArrayList<String> expList = new ArrayList<>();
		for(StudentsExp se : ExceptionStudents){
			expList.add(se.getId());
		}
		//**
		assertEquals(expList, forTest);
	}
	/**
	 * sending students that have done this and passed him with grade >= 55,
	 * Expecting to get back in studentsToAssign the same list
	 */
	@Test
	public void testApprovedStudents() {
		studentsToAssign = new ArrayList<>();
		ExceptionStudents = FXCollections.observableArrayList();
		sec.checkClassPreCourse(approvedStudents,studentsToAssign,ExceptionStudents,preCourses,t,classConvertToId);
		//** make array list of appreved students
		ArrayList<String> forTest = new ArrayList<>();
		for(String s: approvedStudents.keySet())
			forTest.add(s);

		assertEquals(studentsToAssign, forTest);
	}
	/**
	 * sending mixed students list with students without pre course, without sufficient grade and approved students,
	 * Expecting to get back in studentsToAssign the approved list, in ExcptionStudents the students without sufficient grade,or pre course
	 */
	@Test
	public void testMixStudents() {
		studentsToAssign = new ArrayList<>();
		ExceptionStudents = FXCollections.observableArrayList();
		sec.checkClassPreCourse(studentsId,studentsToAssign,ExceptionStudents,preCourses,t,classConvertToId);
		//** make array list of approved students
		ArrayList<String> approvedForTest = new ArrayList<>();
		for(String s: approvedStudents.keySet())
			approvedForTest.add(s);
		
		//** change exception list to array list for equal
		ArrayList<String> expList = new ArrayList<>();
		for(StudentsExp se : ExceptionStudents){
			expList.add(se.getId());
		}
		//**
		//** combining students without pre course & without grade for equal with exception students list
		ArrayList<String> nonApprovedList = new ArrayList<>();
		for(String sid: studentsWithoutCourse.keySet())
			nonApprovedList.add(sid);
		for(String sid: studentsWithoutGrade.keySet())
			nonApprovedList.add(sid);
		//**
		Collections.sort(approvedForTest);
		Collections.sort(studentsToAssign);
		Collections.sort(expList);
		Collections.sort(nonApprovedList);
		assertEquals(studentsToAssign, approvedForTest); // equal approved
		assertEquals(expList, nonApprovedList); // equal non approved
	}
	
	private void initClassConvertToId() {
		classConvertToId.put("Tet2"," 64");
		classConvertToId.put("Yod1"," 65");
		classConvertToId.put("Yod2"," 66");
		classConvertToId.put("YodA1"," 67");
		classConvertToId.put("YodA2"," 68");
		classConvertToId.put("YodB1"," 69");
		classConvertToId.put("YodB2"," 70");
		classConvertToId.put("Het1"," 61");
		classConvertToId.put("Het2"," 62");
		classConvertToId.put("Tet1"," 63");
	}
	private void initPreCourses() {
		ArrayList<String> courseInClassId = new ArrayList<>();
		courseInClassId.add("266");
		courseInClassId.add("268");
		courseInClassId.add("286");
		courseInClassId.add("288");
		courseInClassId.add("306");
		courseInClassId.add("308");
		courseInClassId.add("326");
		courseInClassId.add("328");
		courseInClassId.add("346");
		courseInClassId.add("348");
		courseInClassId.add("358");
		courseInClassId.add("359");
		courseInClassId.add("360");
		courseInClassId.add("361");
		courseInClassId.add("362");
		courseInClassId.add("363");
		courseInClassId.add("371");
		courseInClassId.add("372");
		courseInClassId.add("374");
		preCourses.put("800", courseInClassId);
		
	}
	
	private void initStudentsId() {
		studentsId.put("Student1","Student1 Student1");
		studentsId.put("Student10","Student10 Student10");
		studentsId.put("Student11","Student11 Student11");
		studentsId.put("Student12","Student12 Student12");
		studentsId.put("Student13","Student13 Student13");
		studentsId.put("Student14","Student14 Student14");
		studentsId.put("Student15","Student15 Student15");
		studentsId.put("Student2","Student2 Student2");
		studentsId.put("Student3","Student3 Student3");
		studentsId.put("Student4","Student4 Student4");
		studentsId.put("Student5","Student5 Student5");
		studentsId.put("Student6","Student6 Student6");
		studentsId.put("Student7","Student7 Student7");
		studentsId.put("Student8","Student8 Student8");
		studentsId.put("Student9","Student9 Student9");
		studentsId.put("Student480","Student480 Student480");
		studentsId.put("Student481","Student481 Student481");
		//init list without sufficient grade
		studentsWithoutGrade = new HashMap<>();
		studentsWithoutGrade.put("Student1","Student1 Student1");
		studentsWithoutGrade.put("Student11","Student11 Student11");
		studentsWithoutGrade.put("Student14","Student14 Student14");
		studentsWithoutGrade.put("Student3","Student3 Student3");
		//init list of students who has preRequisits
		approvedStudents = new HashMap<>();
		approvedStudents.put("Student10","Student10 Student10");
		approvedStudents.put("Student12","Student12 Student12");
		approvedStudents.put("Student13","Student13 Student13");
		approvedStudents.put("Student15","Student15 Student15");
		approvedStudents.put("Student2","Student2 Student2");
		approvedStudents.put("Student4","Student4 Student4");
		approvedStudents.put("Student5","Student5 Student5");
		approvedStudents.put("Student6","Student6 Student6");
		approvedStudents.put("Student7","Student7 Student7");
		approvedStudents.put("Student8","Student8 Student8");
		approvedStudents.put("Student9","Student9 Student9");
		//students who havent done the pre course yet at all
		studentsWithoutCourse = new HashMap<>();
		studentsWithoutCourse.put("Student480","Student480 Student480");
		studentsWithoutCourse.put("Student481","Student481 Student481");
		
	}

}
