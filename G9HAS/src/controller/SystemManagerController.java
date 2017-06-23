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
    @FXML
    void defineCourseHandler(ActionEvent event) {
    	setPane(defineCoursePane);
    }
    @FXML
    void createCourseHandler(ActionEvent event) {
		msg = new HashMap<String, ArrayList<String>>();
		ArrayList<String> courseInfo = new ArrayList<String>();
		try{
		if(String.valueOf(Integer.parseInt(tfCourseID.getText())).length()==3)	
			courseInfo.add(String.valueOf(Integer.parseInt(tfCourseID.getText())));
		else{
			JOptionPane.showMessageDialog(null, "Illegal value");
			return;
		}
		courseInfo.add(tfCourseName.getText());
		courseInfo.add(cbTeachingUnit.getValue().toString());
		courseInfo.add(tfWeeklyHours.getText());
		courseInfo.add(tfYear.getText());
		courseInfo.add(tfSemester.getText());
		msg.put("Create Course",courseInfo);
		JOptionPane.showMessageDialog(null, "Course added successfully");
		}
		catch(NumberFormatException e){
			JOptionPane.showMessageDialog(null, "Illegal value");
		}
		try{
		LoginController.userClient.sendServer(msg);
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, "Error, the course not added.");
		}
			
    }
	@FXML
	void logoutHandler(ActionEvent event) {
		Parent nextWindow;
		try {
			LoginController.logout();
			nextWindow = FXMLLoader.load(getClass().getResource("../gui/loginWindow.fxml"));
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
