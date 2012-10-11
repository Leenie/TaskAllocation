/*This is to create a new task information
 * this will be added on to the database
 * 
 */
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.sql.*;



public class EditTaskFrame extends JDialog implements ActionListener {


	private static final long serialVersionUID = 1L;


	//text fields
	
	private JTextField task_idfield;
	private JTextField taskname_field;
	private JTextArea taskdesc_field;
	private JCheckBox priority_checkbox;
	private JTextField numberofdays_field;
	private JFormattedTextField duedate_field;

	

	//fields to hold the values that the user will input for 
	//creating a task which will be used for storing the values in the database
	
	String taskid_input;
	String taskname_input;
	String taskdesc_input;
	ArrayList<JCheckBox> skill_list;
	int priority_input;
	int numberofdays_input;
	SimpleDateFormat format; //date format
	Date date;
	//String duedate_input; // to store the date that will be inserted in the system



	//buttons
	private JButton clear_button;
	private JButton submit_button;

	JScrollPane scrollpane;

	public void getSkills() throws SQLException {

		skill_list = new ArrayList<JCheckBox>(); //initialise list

		SqlConnection.connect(); //connect to database
		SqlConnection.statement = SqlConnection.connection.createStatement(); 
		SqlConnection.statement.executeQuery("SELECT skill_id FROM SKILL"); //get all skills
		SqlConnection.result = SqlConnection.statement.getResultSet(); //store skills

		while(SqlConnection.result.next()) {

			skill_list.add(new JCheckBox(SqlConnection.result.getString("skill_id"))); //add skill skill to list
		}


	}

	Task t;


	public EditTaskFrame(Task task) throws SQLException {
		
		this.setModal(true);
		this.t = task;
		
		this.setTitle("Edit Task"); //title of frame
		this.setSize(500,500);

		this.setLayout(new BorderLayout()); //layout of frame


		JLabel task_id = new JLabel("ID:");
		JLabel task_name = new JLabel("Name:");
		JLabel task_desc = new JLabel("Description:");

		JLabel priority = new JLabel("Level of Priority:");
		JLabel duedate = new JLabel("Due Date (yyyy-mm-dd)");
		JLabel numberofdays = new JLabel("Number of days required:");
		//JLabel isallocated = new JLabel(" Is Allocated?:");
		JLabel skills_required = new JLabel("Skills Required:");

		//JLabel unallocateable_label = new JLabel("Unallocateable:");

		//initialize text fields
		task_idfield = new JTextField(t.task_id);
		task_idfield.setEnabled(false);
		
		taskname_field = new JTextField(t.task_name);
		taskdesc_field = new JTextArea(t.task_description);

		
		priority_checkbox = new JCheckBox("High", false);
		
		if(t.priority_value == 1 ){
			priority_checkbox.setSelected(true);
		}
		
		numberofdays_field = new JTextField(new Integer(t.numberofDays).toString());
		//isallocatedcheckbox = new JCheckBox("Allocated", false);
		format = new SimpleDateFormat("dd-MM-yyyy"); // set format
		duedate_field = new JFormattedTextField(format);//format); 
		//date = (Date)duedate_field.getValue();
		//duedate_field.setValue(date);

		//unallocateable_field = new JCheckBox("Tick if not allocateable", false);


		duedate_field.setValue(t.due_date); // set this date to the field

		//panel to hold labels and text fields
		JPanel labeltextpanel = new JPanel();
		labeltextpanel.setLayout(new GridLayout(7,2));
		labeltextpanel.add(task_id);
		labeltextpanel.add(task_idfield);
		labeltextpanel.add(task_name);
		labeltextpanel.add(taskname_field);
		labeltextpanel.add(task_desc);
		labeltextpanel.add(taskdesc_field);
		labeltextpanel.add(priority);
		labeltextpanel.add(priority_checkbox);
		labeltextpanel.add(numberofdays);
		labeltextpanel.add(numberofdays_field);
		//labeltextpanel.add(isallocated);
		//labeltextpanel.add(isallocatedcheckbox);
		labeltextpanel.add(duedate);
		labeltextpanel.add(duedate_field);
		//labeltextpanel.add(unallocateable_label);
		//labeltextpanel.add(unallocateable_field);
		labeltextpanel.add(skills_required);


		getSkills();

		//main panel for frame including its layout
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(labeltextpanel, BorderLayout.CENTER);

		// buttons
		clear_button = new JButton("Clear");
		submit_button = new JButton("Submit");

		//add listeners to buttons
		clear_button.addActionListener(this);
		submit_button.addActionListener(this);

		//create panel for buttons and add buttons
		JPanel buttonpanel = new JPanel();
		buttonpanel.setLayout(new FlowLayout());
		buttonpanel.add(clear_button);
		buttonpanel.add(submit_button);

		JPanel checkbox_panel = new JPanel();
		checkbox_panel.setLayout(new FlowLayout());

		
		ArrayList<String> task_skills = new ArrayList<String>();

		for( int i = 0; i < t.required_skills.size(); i++) {
			task_skills.add(t.required_skills.get(i).skill_id);
		}


		for (int i = 0; i < skill_list.size(); i++) { 

			//skills.add(skillbox);
			checkbox_panel.add(skill_list.get(i)); // make this panel flowlayout
			
			if(task_skills.indexOf(skill_list.get(i).getText()) != -1){ //
				skill_list.get(i).setSelected(true);
			}

		}


		scrollpane = new JScrollPane(checkbox_panel);
		scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);


		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(mainPanel,BorderLayout.CENTER);
		panel.add(scrollpane, BorderLayout.SOUTH);
		//add the main panel to main frame


		add(panel, BorderLayout.CENTER);
		add(buttonpanel, BorderLayout.SOUTH);		



		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent event) {

		//if clear button is pressed, clear all fields
		if(event.getSource() == clear_button) {
			//erase all fields
			
			taskname_field.setText("");
			taskdesc_field.setText("");

			priority_checkbox.setSelected(false);
			numberofdays_field.setText("");
			//isallocatedcheckbox.setSelected(false);
			duedate_field.setText("");
		}

		//if submit button is pressed, save all data onto database
		if(event.getSource() == submit_button) {
			try{
				//if(employeesHaveSkills()){
					
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date()); // set to current date
					calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(this.numberofdays_field.getText())-1); // add days required... then:
					// ^ subtract one day, because if task is created today and allocated tomorrow for one day, 
					//then it should be done by end of day tomorrow
					Date date = calendar.getTime(); // (see next *1) use this date to be the earliest
					//because of expected task duration

					/*This is a constraint 
					 * the date and the length of days that user expected to take
					 * if not feasible then the program will say so
					 * */
					//method compares the current date and the date that was entered by the user
					if(this.task_idfield.getText().length() != 7) {
						JOptionPane.showMessageDialog(this, "Task ID must be 7 characters.");
					} else 
						if(this.taskname_field.getText().length() > 50) {
							JOptionPane.showMessageDialog(this, "Task description must be less/equal than 50 characters.");
						} else
							if(((Date) this.duedate_field.getValue()).compareTo(date) < 0) {
								JOptionPane.showMessageDialog(this, "Because the expected duration of the task is " + 
										Integer.parseInt(this.numberofdays_field.getText()) + " days, the due date cannot be earlier than: " + 
										new StringBuffer(new SimpleDateFormat("dd/MM/yyyy").format(date)).toString());
							} else
								if(this.numberofdays_field.getText().length() > 2 || this.numberofdays_field.getText().length() == 0  // check number of digits
							|| Integer.parseInt(this.numberofdays_field.getText()) < 1) {
									JOptionPane.showMessageDialog(this, "Task duration must be 2 digit number maximum and must be an integer greater than 0");
								}
					
				
					
					else 

						//check if taskid_field is equals to any of the fields on the database



						if(task_idfield.equals("")) {
							JOptionPane.showMessageDialog(this, "Please insert an identification number for a task");

						} else //add constraint and tell customer to enter 7 values

							if(taskname_field.equals("")) {
								JOptionPane.showMessageDialog(this, "Please insert a name to identity the task");

							}

							else
							{

								//get all inputs from user inputs and store in variables 
								taskid_input = task_idfield.getText();
								taskname_input = taskname_field.getText();
								taskdesc_input = taskdesc_field.getText();

								if(priority_checkbox.isSelected()) {
									priority_input = 1;
								}

								else {
									priority_input = 0;

								}

								//create and store date from user input
								date = (Date) duedate_field.getValue();


								//store number of days input from user
								numberofdays_input = Integer.parseInt(numberofdays_field.getText());

								//public Task(String task_id, String task_name, String task_description, int priority_value, 
								//Date due_date, Date start_date, Date end_date, int numberofDays, ArrayList<Skill> required_skills,
								//Employee allocatedEmployee, int isallocated, int value, int unallocateable, boolean isDummy) {
								//String[] column_names = { "Task ID", "Task Name", "Task Description", "Task Priority", "Due Date", "Number of Days", "Is Allocated" , "Unallocateable" };


								t.task_id = taskid_input;
								t.task_name = taskname_input;
								t.task_description = taskdesc_input;
								t.priority_value = priority_input;
								t.due_date = date;
								t.numberofDays = numberofdays_input;
								// t.isallocated = 
								t.unallocateable = 0;

								try {
									//
									SqlConnection.connect();


									SqlConnection.ps = SqlConnection.connection.prepareStatement("UPDATE TASK SET task_name=?," +
											" task_description=?, priority=?, due_date=?, number_of_days=?, isallocated=?, unallocateable=?" +
											" WHERE task_id=? ");

									SqlConnection.ps.setString(1, taskname_input);
									SqlConnection.ps.setString(2, taskdesc_input);
									SqlConnection.ps.setInt(3, priority_input);
									SqlConnection.ps.setDate(4, new java.sql.Date(date.getTime()));
									SqlConnection.ps.setInt(5, numberofdays_input);
									SqlConnection.ps.setInt(6, t.isallocated);
									SqlConnection.ps.setInt(7, 0);
									SqlConnection.ps.setString(8, taskid_input);


									SqlConnection.ps.executeUpdate();

									
									// FIRST REMOVE ALL TASK SKILLS FROM THE TASKSKILL TABLE
									SqlConnection.ps = SqlConnection.connection.prepareStatement("DELETE FROM TASKSKILL WHERE task_id=?");
									SqlConnection.ps.setString(1, taskid_input);
									SqlConnection.ps.executeUpdate();

									
									//now enter skills into TASK SKILL database
									for(int i = 0; i < skill_list.size(); i++) {
										if(skill_list.get(i).isSelected()) {
											String skillid = skill_list.get(i).getText();
											SqlConnection.statement.executeUpdate("INSERT INTO TASKSKILL VALUES ('"+taskid_input+"', '"+skillid+"')");
										}
									}

									SqlConnection.ps.close();


								}
								catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								SqlConnection.closeConnection();
								this.setVisible(false);
							}

				//} else{
					//JOptionPane.showMessageDialog(this, "WARNING: NO EMPLOYEE EXISTS THAT HAS ALL THE REQUIRED SKILLS");
				//}
			} catch(Exception exc){
				JOptionPane.showMessageDialog(this, 
						"Check your inputs:" +
								"\n"+"Priority has to be a 1-digit number;" +
								"\n"+"Date has to be in the format dd/MM/yyyy;" +
								"\n"+"Task length has to be 2-digit number");
				System.out.println(exc);
			}
		}
		//display dialog box asking task manager if they want to quit, 
		//if task manager does not want to quit, go back to adding more tasks
		//if customer quits, close the dialog box and the frame

		//now close the window after saving


	}

	public static void main(String[] args) throws SQLException {
		NewTaskFrame createTask = new NewTaskFrame();

		createTask.getContentPane();		
	}
	
	public boolean employeesHaveSkills(){
		ArrayList<Employee> employee_list = new Sql().selectAllEmployees();
		for(int i = 0; i < employee_list.size(); ++i){
			if(hasSkills(employee_list.get(i))){
				return true; // if list has at least 1 employee with required skills, return true
			}
		}
		return false;	// return false	if search of all employees does not return true
	}

	
	public boolean hasSkills(Employee e){
		ArrayList<String> employeeskills = new ArrayList<String>(); // get employees skills in string format
		for(int i = 0; i < e.employee_skills.size(); ++i){
			employeeskills.add(e.employee_skills.get(i).skill_id);
		}

		ArrayList<String> taskskills = new ArrayList<String>(); // get selected skills in string format
		for(int i = 0; i < skill_list.size(); ++i){
			if(skill_list.get(i).isSelected()){ // if jbutton selected
				taskskills.add(skill_list.get(i).getText()); // add skill to string list
			}
		}

		for(int i = 0; i < taskskills.size(); ++i){
			if(employeeskills.indexOf(taskskills.get(i)) == -1){ // if a taskskill is not found among employeeskills
				return false; // return false
			}
		}

		return true; // if not returned false in loop, then every taskskill does exist among employeeskills

	}


}
