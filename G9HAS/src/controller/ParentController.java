package controller;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ParentController implements Initializable{
	ArrayList<String> cid=new ArrayList<String>();
	ArrayList<String> isBlocked=new ArrayList<String>();
    @FXML
    private TextField tfBlocked;

    @FXML
    private TextArea taTasks;
    @FXML
    private Label lblAvg;
	@FXML
	private Hyperlink linkLogout;
    @FXML
    private ComboBox<String> cbCourseInClass;
    @FXML
    private ComboBox<String> cbChild;
    
    @FXML
    private Label lblClassID;

    @FXML
    private Label lblClass;
    
    @FXML
    private Label lblSelectChild;

    @FXML
    private Label lblCourseAvg;
	@FXML
	private Label lblUser;
	
	/**
	 * This method handles the event of choosing child's specific course from his
	 * courses,and loads the course's tasks and grades, and the current course
	 *  average.
	 * @param event
	 */
	
	@FXML
	void cbCourseInClassHandler(ActionEvent event) {

		taTasks.setVisible(false);
		lblCourseAvg.setVisible(false);
		float gradeAvg=0;
		taTasks.setText("");
		taTasks.setVisible(true);
		lblCourseAvg.setVisible(true);
		ArrayList<String> arr = new ArrayList<String>();
		HashMap<String,ArrayList<String>> hm= new HashMap<String,ArrayList<String>>();
		arr.add(cid.get(cbChild.getItems().indexOf(cbChild.getValue())));
		String temp = "";
		if(cbCourseInClass.getValue()!=null){
		for(int i=2;i<5;i++)
		{
			temp+= cbCourseInClass.getValue().charAt(i);
		}
		
		arr.add(temp);
		hm.put("get grades for student in course",arr);
		LoginController.userClient.sendServer(hm);
		LoginController.syncWithServer();
		if(LoginController.userClient.ans != null){
			for(int i=0; i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
			{
				taTasks.setText(taTasks.getText()+ "Task no. "+ (i+1) + " Grade: "+((ArrayList<String>)LoginController.userClient.ans).get(i)+"\n");
				gradeAvg +=Float.parseFloat(((ArrayList<String>)LoginController.userClient.ans).get(i));
			}
		}
		if(gradeAvg>0)
		{
			lblCourseAvg.setVisible(true);
			lblCourseAvg.setText("Course Average: "+ gradeAvg/((ArrayList<String>)LoginController.userClient.ans).size());	
		}
		}
	
	}
	/**
	 * This method handles the event of choosing the child name and load 
	 * the child's current courses,class,and semester average.
	 * @param event
	 */
	@FXML
	void childSelectHandler(ActionEvent event) {
		cbCourseInClass.getItems().clear();
		lblCourseAvg.setVisible(false);
		lblClass.setVisible(true);
		lblClassID.setVisible(true);
		cbCourseInClass.getItems().clear();
		ArrayList<String> arr=new ArrayList<String>();
		ArrayList<String> arr2=new ArrayList<String>();
		ArrayList<String> arr3=new ArrayList<String>();
		HashMap<String,ArrayList<String>> hm=new HashMap<String,ArrayList<String>>();
		if(isBlocked.get(cbChild.getItems().indexOf(cbChild.getValue())).equals( "1")){
				tfBlocked.setVisible(true);	
				taTasks.setVisible(false);
				cbCourseInClass.setVisible(false);
				lblAvg.setVisible(false);
				lblClassID.setVisible(false);
				lblClass.setVisible(false);
				cbCourseInClass.getItems().clear();
				
		}
		else{
			lblAvg.setVisible(true);
			lblClass.setVisible(true);
			taTasks.setVisible(false);
			cbCourseInClass.setVisible(true);
			tfBlocked.setVisible(false);
			cid.get(cbChild.getItems().indexOf(cbChild.getValue()));
			hm.put("Get student class",cid);
	        LoginController.userClient.sendServer(hm);
	        LoginController.syncWithServer();
	        if(!((ArrayList<String>)LoginController.userClient.ans).isEmpty()){
	        	lblClassID.setText(((ArrayList<String>)LoginController.userClient.ans).get(0));
	        }
	        hm.clear();
	        arr.add(cid.get(cbChild.getItems().indexOf(cbChild.getValue())));
			hm.put("get course in class", arr);
			LoginController.userClient.sendServer(hm);
			LoginController.syncWithServer();
			arr.clear();
			if(LoginController.userClient.ans != null){
				for(int i=0;i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
					arr.add(((ArrayList<String>)LoginController.userClient.ans).get(i));
			}
			hm.remove("get course in class");
			hm.put("get course id",arr);
			LoginController.userClient.sendServer(hm);
			LoginController.syncWithServer();
			arr.clear();
			if(LoginController.userClient.ans != null){
				for(int i=0;i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
					arr.add(((ArrayList<String>)LoginController.userClient.ans).get(i));
			}
			hm.remove("get course id");
			hm.put("Search for course name",arr);
			LoginController.userClient.sendServer(hm);
			LoginController.syncWithServer();
			if(LoginController.userClient.ans != null){
				for(int i=0; i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
				{
					cbCourseInClass.getItems().add(i,arr.get(i)+" - "+((ArrayList<String>)LoginController.userClient.ans).get(i).toString());
				}
			}
			arr.clear();
			int year=0,semester = 0;
			char sem = ' ';
			hm.remove("Search for course name");
			hm.put("getCurrentSemester",null);
			LoginController.userClient.sendServer(hm);
			LoginController.syncWithServer();
			if(LoginController.userClient.ans != null){
				year = ((ArrayList<Integer>)LoginController.userClient.ans).get(0);
				sem = ((ArrayList<Character>)LoginController.userClient.ans).get(1);
				if(sem=='A')
				{
					year--;
					semester = 2;
				}
				else
				{
					semester = 1;
				}
			}
			arr.clear();
			arr.add(cid.get(cbChild.getItems().indexOf(cbChild.getValue())));
			arr.add(String.valueOf(year));
			arr.add(String.valueOf(semester));
			hm.remove("getCurrentSemester");
			hm.put("get course in class",arr);
			LoginController.userClient.sendServer(hm);
			LoginController.syncWithServer();
			System.out.println(((ArrayList<String>)LoginController.userClient.ans).toString()+"kkkkk");
			ArrayList<String> arr1 = new ArrayList<String>();
			arr1.add(String.valueOf(year));
			arr1.add(String.valueOf(semester));
			arr1.add(cid.get(cbChild.getItems().indexOf(cbChild.getValue())));
			if(LoginController.userClient.ans != null){
				for(int i=0; i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
				{
					arr1.add((i+3),((ArrayList<String>)LoginController.userClient.ans).get(i));
				}
			}
			System.out.println(arr1.toString());
			hm.remove("get course in class");
			hm.put("get student average for current semester",arr1);
			LoginController.userClient.sendServer(hm);
			LoginController.syncWithServer();
			float sumgrades = 0;
			
			if(LoginController.userClient.ans != null){
				for(int i=0; i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
				{
					sumgrades += Float.parseFloat(((ArrayList<String>)LoginController.userClient.ans).get(i));
				}
			}
			System.out.println(sumgrades);
			lblAvg.setText("Average:"+String.valueOf((Float)(sumgrades/((ArrayList<String>)LoginController.userClient.ans).size())));
        
		}

			
	}
	/**
	 * This method handles the event of pressing the button "x"\logout.
	 * @param event
	 */
	
	@FXML
	void logoutHandler(ActionEvent event) {
		Parent nextWindow;
		try {
			HashMap<String, ArrayList<String>> msg = new HashMap<String, ArrayList<String>>();
			ArrayList<String> arr = new ArrayList<String>();
			arr.add(UserClient.userName);
			msg.put("logout",arr);
			LoginController.userClient.sendServer(msg);
			nextWindow = FXMLLoader.load(getClass().getResource("../gui/loginWindow.fxml"));
			Scene nextScene = new Scene(nextWindow);
			Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
			stage.setScene(nextScene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		cbCourseInClass.getItems().clear();
		lblCourseAvg.setVisible(false);
		lblClass.setVisible(false);
		lblClassID.setVisible(false);
		tfBlocked.setVisible(false);
		lblUser.setText(UserClient.fullName);
		cbChild.getItems().clear();
		HashMap<String,String> data=new HashMap<String,String>();
		HashMap<String,ArrayList<String>> data2=new HashMap<String,ArrayList<String>>();
		HashMap<String,ArrayList<String>> hm=new HashMap<String,ArrayList<String>>();
		data.put("get child id", LoginController.userClient.userName);
        LoginController.userClient.sendServer(data);
        LoginController.syncWithServer();
        System.out.println(((ArrayList<String>)LoginController.userClient.ans));
        if(LoginController.userClient.ans!=null)
        	for(int i=0;i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
        		cid.add(((ArrayList<String>)LoginController.userClient.ans).get(i));
        data.clear();
        data2.put("get block status",cid);
        LoginController.userClient.sendServer(data2);
        LoginController.syncWithServer();
        System.out.println(((ArrayList<String>)LoginController.userClient.ans));
        if(LoginController.userClient.ans!=null)
        	for(int i=0;i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
        		isBlocked.add(((ArrayList<String>)LoginController.userClient.ans).get(i));
        data2.clear();
        data2.put("get child username",cid);
        LoginController.userClient.sendServer(data2);
        LoginController.syncWithServer();
        System.out.println(((ArrayList<String>)LoginController.userClient.ans));
        if(LoginController.userClient.ans!=null)
        	for(int i=0;i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
        		if(!(cbChild.getItems().contains(((ArrayList<String>)LoginController.userClient.ans).get(i))))
        			cbChild.getItems().add(((ArrayList<String>)LoginController.userClient.ans).get(i));
	}
	
	
}

