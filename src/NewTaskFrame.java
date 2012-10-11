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



public class NewTaskFrame extends JDialog implements ActionListener {


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
	
	//buttons
	private JButton clear_button;
	private JButton submit_button;
	JScrollPane scrollpane;

	Object[] task;

	
	//method to retrieve all the skills that are stored in the database and add it to arraylist
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


	public NewTaskFrame() throws SQLException {

		this.setModal(true);
		this.setTitle("Create Task"); //title of frame
		this.setSize(400,350);
		this.setLayout(new BorderLayout()); //layout of frame

		JLabel task_id = new JLabel("ID:");
		JLabel task_name = new JLabel("Name:");
		JLabel task_desc = new JLabel("Description:");

		JLabel priority = new JLabel("Level of Priority:");
		JLabel duedate = new JLabel("Due Date (yyyy-mm-dd)");
		JLabel numberofdays = new JLabel("Number of days required:");
		JLabel skills_required = new JLabel("Skills Required:");


		Object[] id_possibilities = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
		StringBuffer generated_name = new StringBuffer("t");
		
		for(int i = 0; i < 6; ++i){
			generated_name.append(id_possibilities[new java.util.Random().nextInt(id_possibilities.length)]);
		}
		
		
		task_idfield = new JTextField(generated_name.toString());
		taskname_field = new JTextField();
		taskdesc_field = new JTextArea();

		priority_checkbox = new JCheckBox("High", false);
		numberofdays_field = new JTextField();
	
		format = new SimpleDateFormat("dd-MM-yyyy"); // set format
		duedate_field = new JFormattedTextField(format); 
		

		// set the duedatefield to tomorrow (since minimum task length = 1)
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.setTime(new Date()); // get current date
		cal.add(Calendar.DAY_OF_MONTH, 1); // add 1 to current date
		Date date = cal.getTime(); // get this date
		duedate_field.setValue(date); // set this date to the field

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
		labeltextpanel.add(duedate);
		labeltextpanel.add(duedate_field);
		labeltextpanel.add(skills_required);


		this.getSkills();

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


		for (int i = 0; i < skill_list.size(); i++) { 
			checkbox_panel.add(skill_list.get(i)); // make this panel flowlayout
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
				{
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
					if(this.task_idfield.getText().length() != 7 // check chars
							|| this.taskname_field.getText().length() > 50  // check chars
							|| this.taskdesc_field.getText().length() > 50
							|| ((Date) this.duedate_field.getValue()).compareTo(date) < 0 // check that the date is possible (see last *1)
							|| this.numberofdays_field.getText().length() > 2 || this.numberofdays_field.getText().length() == 0  // check number of digits
							|| Integer.parseInt(this.numberofdays_field.getText()) < 1 ) // check number of days required for task
					{
						JOptionPane.showMessageDialog(this, 
								"Task ID has to be 7 chars; " +
										"\n"+"Description <= 50 chars; " +
										"\n"+"Because the expected duration of the task is " + 
										Integer.parseInt(this.numberofdays_field.getText()) + " days, the due date cannot be earlier than: " + 
										new StringBuffer(new SimpleDateFormat("dd/MM/yyyy").format(date)).toString() + "; " +
										"\n"+"Task duration must be 2 digit number maximum and must be an integer greater than 0" );
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

								// 	String[] column_names = { "Task ID", "Task Name", "Task Description", "Task Priority", "Due Date", "Number of Days", "Is Allocated" , "Unallocateable" };

								
								task = new Object[8];
								task[0] = taskid_input;
								task[1] = taskname_input;
								task[2] = taskdesc_input;
								task[3] = priority_input;
								task[4] = date;
								task[5] = numberofdays_input;
								task[6] = 0;
								task[7] = 0;


								try {
									//
									SqlConnection.connect();


									SqlConnection.ps = SqlConnection.connection.prepareStatement("INSERT  INTO TASK VALUES (?,?,?,?,?,?,?,?)");

									SqlConnection.ps.setString(1, taskid_input);
									SqlConnection.ps.setString(2, taskname_input);
									SqlConnection.ps.setString(3, taskdesc_input);
									SqlConnection.ps.setInt(4, priority_input);
									SqlConnection.ps.setDate(5, new java.sql.Date(date.getTime()));
									SqlConnection.ps.setInt(6, numberofdays_input);
									SqlConnection.ps.setInt(7, 0);
									SqlConnection.ps.setInt(8, 0);

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
								//SqlConnection.ps.close();
								SqlConnection.closeConnection();
								this.setVisible(false);
							}
				}
			
				}

			 catch(Exception exc){
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
		

				
	}
	
}
