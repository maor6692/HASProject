package JunitTests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import controller.UserClient;
import junit.framework.TestCase;

/**
 * The test checks that the submission uploaded to right class in course.
 *
 * @pattern JUnit Test Case
 *
 * @generatedBy CodePro at 11:46 09/07/17
 *
 * @author kobi
 *
 * @version $Revision$
 */
public class TestSubmitTask extends TestCase {
	private String student_id, task_id,course_id,submission_file;
	private UserClient userClient;
	/**
	 * Construct new test instance
	 *
	 * @param name the test name
	 */
	public TestSubmitTask(String name) {
		super(name);
	}

	/**
	 * Perform pre-test initialization
	 *
	 * @throws Exception
	 *
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		// Add additional set up code here
		student_id = "student1";
		task_id = "1";
		course_id = "100";
		submission_file = "10100_student1-blabla.doc";
		userClient = new UserClient("localhost",5555);
	}
	/**
	 * upload the submission of the student to server and returns true if the submission uploaded.
	 */
	public void testSubmissionUpload()
	{
		String arrans="";
		HashMap<String,ArrayList<String>> hm= new HashMap<String,ArrayList<String>>();
		ArrayList<String> arr = new ArrayList<String>();
		arr.add(student_id);
		arr.add(task_id);
		arr.add(course_id);
		arr.add(submission_file);
		hm.put("Submission Test", arr);
		try {
			userClient.sendToServer(hm);
			syncWithServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		arrans =  (String)(UserClient.ans);
		assertTrue(Boolean.valueOf(arrans));
		
		
	}
	/**
	 * wait for the answer to be ready from server.
	 */
	public void syncWithServer()
	{
		synchronized(userClient)
		{
			UserClient.setFlagFalse();

			while(!userClient.isready())
			{
				try{
					userClient.wait();	
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * Perform post-test clean up
	 *
	 * @throws Exception
	 *
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		// Add additional tear down code here
	}
}
