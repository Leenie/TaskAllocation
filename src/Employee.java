import java.util.*;

/* a class to hold objects for the Employee 
 * contains constructor for manipulating the
 * database of employee and java
 * */

public class Employee {

	String emp_id; //employee id
	
	String firstname; // employee first name
	
	String lastname; // employee last name
	
	String address; //employee address
	
	String email_address; //employee email
	
	String ni_num; //employee ni number
	
	long telephone; // employee telephone number
	
	long mobile; // employee mobile number

	
	ArrayList<Skill> employee_skills; //skills of employee - list to hold their skills
	
	ArrayList<Task> employee_tasks; //tasks of employee - this is the employee's schedule

	
	long value =0; //this is use for valuing the employee

	//constructor - will be used for manipulating the database and java 
	public Employee(String emp_id, String firstname, String lastname, 
			String address, String email_address, String ni_num, long telephone,
			long mobile, ArrayList<Skill> employee_skills, ArrayList<Task> employee_tasks, int value) {

		this.emp_id = emp_id;
		this.firstname = firstname;
		this.lastname = lastname;
		this.address = address;
		this.email_address = email_address;
		this.ni_num = ni_num;
		this.telephone = telephone;
		this.mobile = mobile;
		this.employee_skills = employee_skills;
		this.employee_tasks = employee_tasks;

		this.value = value;
	}

	public Employee(String emp_id) {
		this.emp_id = emp_id;

	}

	public Employee(String string, String string2, String string3,
			String string4, String string5, String string6, long tel, long mob,
			ArrayList<Skill> selectedSkills) {
		this.emp_id = string;
		this.firstname = string2;
		this.lastname = string3;
		this.address = string4;
		this.email_address = string5;
		this.ni_num = string6;
		this.telephone = tel;
		this.mobile = mob;
		this.employee_skills = selectedSkills;
	}


}
