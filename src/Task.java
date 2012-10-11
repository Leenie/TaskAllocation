import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/*A class to hold objects for the Task
 * constains the constructor for manipulating 
 * the database and java
 */

public class Task implements Comparable<Task> {

	
	String task_id; // task id
	String task_name; // task name
	String task_description; // task description
	int priority_value; // the priority of the task
	Date due_date; //the due date for the task
	Date start_date; // the date when the task was allocated
	Date end_date; // the date when the task is completed(due date)
	int numberofDays; //number of days expected to take for a task to be completed

	
	ArrayList<Skill> required_skills; // list containing the required skills of the task
	ArrayList<Task> task_list; // the list contains all the tasks from the database
	Employee allocatedEmployee; //employee assigned to task


	int isallocated; //to check if employee is allocated or not
	long value; // to value the task
	int unallocateable; //to check if the task is unallocatable
	public Date latest_possible_start; //the latest possible start for a task
	boolean isDummy; // to identify a dummy task
	
	
	//constructor 
	public Task(String task_id, String task_name, String task_description, int priority_value, 
			int isallocated, int value, Date due_date, int unallocateable, boolean isDummy){

		this.task_id = task_id;
		this.task_name = task_name;
		this.task_description = task_description;
		this.priority_value = priority_value;
		this.isallocated = isallocated;
		this.value = value;
		this.unallocateable = unallocateable;
		this.isDummy = isDummy;
		
		this.required_skills = new ArrayList<Skill>();
		this.numberofDays = 1;
		this.due_date = due_date;
	}

	//constructor 
	public Task(String task_id, String task_name, String task_description, int priority_value, 
			Date due_date, Date start_date, Date end_date, int numberofDays, ArrayList<Skill> required_skills,
			Employee allocatedEmployee, int isallocated, int value, int unallocateable, boolean isDummy) {
		this.task_id = task_id;
		this.task_name = task_name;
		this.task_description = task_description;
		this.priority_value = priority_value;
		this.due_date = due_date;
		this.start_date = start_date;
		this.end_date = due_date;
		this.numberofDays = numberofDays;
		this.required_skills = required_skills;
		this.allocatedEmployee = allocatedEmployee;
		this.isallocated = isallocated;
		this.value = value;
		this.unallocateable = unallocateable;
		this.isDummy = isDummy;

		// this is used for setting the latest possible date that the task must be started by
		// latest possible date = due_date - numberOfDays
		Calendar cal = Calendar.getInstance();
		cal.setTime(this.due_date);
		cal.add(Calendar.DAY_OF_MONTH, +1-this.numberofDays);
		this.latest_possible_start = cal.getTime();

	}

	//this is for getting the employee's tasks. if allocated
	public Task(String task_id, Date start_date, Date end_date) { 
		this.task_id = task_id;
		this.start_date = start_date;
		this.end_date = end_date;

	}




	public Task(String string, String string2, String string3, int priority,
			Date due_by_date, int length, ArrayList<Skill> selectedSkills) {
	
		this.task_id = string;
		this.task_name = string2;
		this.task_description = string3;
		this.priority_value = priority;
		this.due_date = due_by_date;
		this.numberofDays = length;
		this.required_skills = selectedSkills;
	}

	@Override
	public int compareTo(Task t) {
		if(this.latest_possible_start.compareTo(t.latest_possible_start) != 0){ // check if not same latest-start-date date
			return this.latest_possible_start.compareTo(t.latest_possible_start); // if not, order by latest start date
		} else { // if same latest start date
			return new Integer(this.priority_value).compareTo(new Integer(t.priority_value)); // order by priority
		}
	}
}

