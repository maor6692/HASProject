package Fixtures.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import controller.SystemManagerController;
import controller.TeacherController;
import controller.UserClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class CreateNewAssignment {
	public static UserClient userClient;
	private String id;
	private String class_in_course_id;
	private String task_name;
	private String task_sub_date;
	private ArrayList<String> units;
	public void setCourseId(String id){
		this.id=id;
	}
	public void setClassInCourse(String class_in_course_id){
		this.class_in_course_id=class_in_course_id;
	}
	public void setTaskName(String task_name){	
		this.task_name=task_name;
	}
	public void setTaskSubDate(String task_sub_date){
		this.task_sub_date=task_sub_date;
	}
	public String checksIfThereIsEmptyField() {
		return String.valueOf(TeacherController.isEmpty(id,class_in_course_id,task_name,task_sub_date));
	}
	public String checkDateFormat(){
		return String.valueOf(TeacherController.isDateFormat(task_sub_date));
	}
	public String checkCourseIdLen(){
		return String.valueOf(TeacherController.is3digits(id));
	}
	public String isExist(){
		return String.valueOf(TeacherController.isClassInCourseExist(class_in_course_id,userClient));	
	}
	
	public void startController() {
		units= new ArrayList<String>();
		units.add("10");
		units.add("20");
		units.add("30");
				try {
					userClient = new UserClient("localhost",5555);
					return;
				} catch (Exception e1) {System.err.println("fit start failed");}
	}
	/**
	 * this method waits for new answer from server
	 */
	public static void syncWithServer()
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

}
