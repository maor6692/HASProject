package JunitTests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import controller.UserClient;

public class TestSubmitTask {
	UserClient userClient;
	
	
	@Before
	public void setUp() throws Exception {
		try {
			 userClient = new UserClient("localhost",5555);
		} catch (Exception e1) {
			return;
		}
	}

	@After
	public void tearDown() throws Exception {
		userClient.closeConnection();
	}

	@Test
	public void test() {
	
	}

}
