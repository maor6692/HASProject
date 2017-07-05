package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SystemManagerController implements Initializable{
	public static HashMap<String, ArrayList<String>> msg;
	@FXML
	private Hyperlink linkLogout;
    @FXML
    private Pane defineCoursePane;
    @FXML
    private TextField tfWeeklyHours;


    @FXML
    private Label lblYear;

    @FXML
    private TextField tfSemester;

    @FXML
    private Label lblDefineCourse;

    @FXML
    private Label lblCourseID;

    @FXML
    private Label lblCourseName;

    @FXML
    private TextField tfYear;

    @FXML
    private Label lblUser; 
    @FXML
    private Label lblError;

    @FXML
    private Label lblTeachingUnit;

    @FXML
    private TextField tfCourseID;

    @FXML
    private Label lblWeeklyHours;

    @FXML
    private Label lblSemester;

    @FXML
    private TextField tfCourseName;

    @FXML
    private ComboBox<String> cbTeachingUnit;

    @FXML
    private Button btnCreateCourse;
    /**
     * sets the pane of the controller to define course
     * @param event
     */
    @FXML
    void defineCourseHandler(ActionEvent event) {
    	setPane(defineCoursePane);
    }
    /**
     * create new course for selected teaching unit
     * @param event
     */
    @FXML
    void createCourseHandler(ActionEvent event) {

    	lblError.setText("Course already exist");
    	lblError.setVisible(false);
    	if(tfCourseName.getText().equals("") || tfWeeklyHours.getText().equals(""))
    	{
    		lblError.setText("Please fill all the fields");
    		lblError.setVisible(true);
    		return;
    	}

    	msg = new HashMap<String, ArrayList<String>>();
    	ArrayList<String> courseInfo = new ArrayList<String>();
    	String tu[];
    	try{
    		if(String.valueOf(Integer.parseInt(tfCourseID.getText())).length()==3 && Integer.parseInt(tfCourseID.getText())>0)	
    			courseInfo.add(String.valueOf(Integer.parseInt(tfCourseID.getText())));
    		else{
    			JOptionPane.showMessageDialog(null, "Illegal value");
    			return;
    		}
    	}
    	catch(NumberFormatException ne)
    	{
    		JOptionPane.showMessageDialog(null, "Invalid input");
    		return;
    	}

    	if(cbTeachingUnit.getValue()!= null)
    		tu = cbTeachingUnit.getValue().split("\\s");
    	else
    	{
    		JOptionPane.showMessageDialog(null, "Illegal value");
    		return;
    	}
    	int year=0,semester = 0;
    	char sem = ' ';
    	msg.clear();
    	msg.put("getCurrentSemester",null);
    	LoginController.userClient.sendServer(msg);
    	LoginController.syncWithServer();
    	if(LoginController.userClient.ans != null){
    		year = ((ArrayList<Integer>)LoginController.userClient.ans).get(0);
    		semester = ((ArrayList<Character>)LoginController.userClient.ans).get(1)=='A'?1:2;
    
        	try{	
        			courseInfo.add(String.valueOf(Integer.parseInt(tfWeeklyHours.getText())));
        			courseInfo.clear();
        			if(Integer.parseInt(tfWeeklyHours.getText())<0)
        				throw new NumberFormatException();
        	}
        	catch(NumberFormatException ne)
        	{
        		JOptionPane.showMessageDialog(null, "Invalid input");
        		courseInfo.clear();
        		return;
        	}
        	
        	courseInfo.add(String.valueOf(Integer.parseInt(tfCourseID.getText())));
    		courseInfo.add(tfCourseName.getText());
    		courseInfo.add(tu[0]);
    		courseInfo.add(tfWeeklyHours.getText());
    		courseInfo.add(String.valueOf(year));
    		courseInfo.add(String.valueOf(semester));
    		msg.clear();
    		msg.put("check if course exists",courseInfo);
    		LoginController.userClient.sendServer(msg); 
    		LoginController.syncWithServer();

    		if(LoginController.userClient.ans!= null &&((String)LoginController.userClient.ans).equals("exist"))
    			lblError.setVisible(true);
    		else
    		{
    			lblError.setVisible(false);
    			msg.clear();	
    			msg.put("Create Course",courseInfo);
    			LoginController.userClient.sendServer(msg);
    			LoginController.syncWithServer();
    			JOptionPane.showMessageDialog(null, "Course added successfully");
    			return;
    		}
    	}
    }

		
    /**
     * logout handler, logout the user if X is pressed and change the window to login window.
     * @param event
     */
	@FXML
	void logoutHandler(ActionEvent event) {
		Parent nextWindow;
		try {
			LoginController.logout();
			nextWindow = FXMLLoader.load(getClass().getResource("/gui/loginWindow.fxml"));
			Scene nextScene = new Scene(nextWindow);
			Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
			stage.setScene(nextScene);
			stage.show();
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent t) {
					LoginController.logout();
					Platform.exit();
					System.exit(0);
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * sets the pane to the selected parameter
	 * @param pane - selected pane to display
	 * 
	 */
	void setPane(Pane pane){
		pane.setVisible(true);
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		lblUser.setText(UserClient.fullName);
		msg = new HashMap<String, ArrayList<String>>();
		msg.put("Get teaching unit",new ArrayList<String>());
		LoginController.userClient.sendServer(msg);
		LoginController.syncWithServer();
		if(LoginController.userClient.ans != null)
		{
			for(int i=0; i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
			{
				cbTeachingUnit.getItems().add(i,((ArrayList<String>)LoginController.userClient.ans).get(i).toString());
			}
		}
		
	}
}
