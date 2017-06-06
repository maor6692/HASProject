package common;

import java.io.Serializable;

public class Class implements Serializable{
		private int id;
		private String name;
		private int year;
		private int semester;
		
		public Class(int id,String name,int year,int semester){
			this.id = id;
			this.name = name;
			this.year = year;
			this.semester = semester;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getYear() {
			return year;
		}

		public void setYear(int year) {
			this.year = year;
		}

		public int getSemester() {
			return semester;
		}

		public void setSemester(int semester) {
			this.semester = semester;
		}
		
		
}
