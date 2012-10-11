/*GUI FOR CREATING A NEW EMPLOYEE
 * contains methods to retrieve the skills 
 * which will be added on to the table EMPLOYEESKILLS
 */
import javax.swing.*;


import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EditEmpFrame extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	//textfield
	
	private JTextField id_field; // to enter employee id
	/**
	 * @uml.property  name="firstname_field"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JTextField firstname_field; //to enter first name
	/**
	 * @uml.property  name="lastname_field"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JTextField lastname_field; // to enter last name
	/**
	 * @uml.property  name="email_field"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JTextField email_field; // to enter email
	/**
	 * @uml.property  name="tel_field"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JTextField tel_field;
	/**
	 * @uml.property  name="mobile_field"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JTextField mobile_field;
	/**
	 * @uml.property  name="address_field"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JTextField address_field;
	/**
	 * @uml.property  name="empni_field"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JTextField empni_field;

	//buttons for clear and submit
	/**
	 * @uml.property  name="clear"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JButton clear;
	/**
	 * @uml.property  name="submit"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JButton submit;

	//checkbox for manager to select the skills to assign to the employee
	/**
	 * @uml.property  name="checklist"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="javax.swing.JCheckBox"
	 */
	ArrayList<JCheckBox> checklist;

	//this method will return a list of the skills that are currently in the database
	//this list will be used for the checkboxes (GUI)
	public void getSkills() throws SQLException {


		checklist = new ArrayList<JCheckBox>(); //initialize list for checkboxes

		SqlConnection.connect(); //connect to database
		SqlConnection.statement = SqlConnection.connection.createStatement(); 
		SqlConnection.statement.executeQuery("SELECT skill_id FROM SKILL"); //get all skills from SKILL table
		SqlConnection.result = SqlConnection.statement.getResultSet(); //store the results of sql querying of skills

		while(SqlConnection.result.next()) {
			checklist.add(new JCheckBox(SqlConnection.result.getString("skill_id"))); //add skill to checklist 
		}
	}

	//this method will assign the skills of the employee and will update the EMPLOYEESKILL table
	public void assignSkills(ArrayList<String> checklist) throws SQLException {
		//get array list of skill
		for (int i = 0; i < checklist.size(); i++) {

			//get first entry in check list
			SqlConnection.connect(); //connect to database

			//get skill id
			SqlConnection.statement.executeQuery("SELECT skill_id FROM SKILL WHERE skill_id = " + checklist.get(i)); //get the skill from checklist 
			SqlConnection.result = SqlConnection.statement.getResultSet(); // store the skill_name ids

			//get emp id
			SqlConnection.statement.executeQuery("SELECT emp_id FROM EMPLOYEE WHERE emp_id = " + checklist.get(i)); //get the employee id from the checklist
			ResultSet result1 = SqlConnection.statement.getResultSet();

			while (SqlConnection.result.next()) {
				String skillid = SqlConnection.result.getString(i);
				SqlConnection.statement.executeUpdate("INSERT INTO EMPLOYEESKILL VALUES ('"+skillid+"')"); //add the skill id to the EMLOYEESKILL table

			}
			while (result1.next()) {
				String empid = result1.getString(i);

				SqlConnection.statement.executeUpdate("INSERT INTO EMPLOYEESKILL VALUES ('"+empid+"')"); // add the employee id to the EMPLOYEESKILL table
			}
		}
	}

	/**
	 * @uml.property  name="e"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	Employee e;

	//constructor to create frame and initialise fields
	public EditEmpFrame(Employee employee) throws SQLException {


		this.setModal(true);
		this.e = employee;
		this.setTitle("Edit Employee"); //title
		this.setSize(400,400);
		this.setLayout(new BorderLayout()); //layout

		//create and initialise labels
		JLabel emp_id = new JLabel();
		emp_id.setText("ID");
		JLabel emp_firstname = new JLabel("First Name:");
		JLabel emp_lastname = new JLabel("Last Name:");
		JLabel fulladdress = new JLabel("Full Address:");
		JLabel emp_email = new JLabel("Email Address:");
		JLabel tel = new JLabel("Telephone:");
		JLabel mobile = new JLabel("Mobile:");
		JLabel ni = new JLabel("N.I:");
		JLabel skills_label = new JLabel("Select Skills:");


		//initialize all fields
		id_field = new JTextField(e.emp_id);
		id_field.setEnabled(false);
		firstname_field = new JTextField(e.firstname);
		lastname_field = new JTextField(e.lastname);
		email_field = new JTextField(e.email_address);
		tel_field = new JTextField(new Long(e.telephone).toString());
		mobile_field = new JTextField(new Long(e.mobile).toString());
		address_field = new JTextField(e.address);
		empni_field = new JTextField(e.ni_num);

		//initialize all buttons
		clear = new JButton("Clear");
		submit = new JButton("Submit");


		//panel to hold all label and fields
		JPanel labelfieldpanel = new JPanel();
		labelfieldpanel.setLayout(new GridLayout(10, 2)); //panel layout
		labelfieldpanel.add(emp_id);
		labelfieldpanel.add(id_field);
		labelfieldpanel.add(emp_firstname);
		labelfieldpanel.add(firstname_field);
		labelfieldpanel.add(emp_lastname);
		labelfieldpanel.add(lastname_field);
		labelfieldpanel.add(emp_email);
		labelfieldpanel.add(email_field);
		labelfieldpanel.add(fulladdress);
		labelfieldpanel.add(address_field);
		labelfieldpanel.add(tel);
		labelfieldpanel.add(tel_field);
		labelfieldpanel.add(mobile);
		labelfieldpanel.add(mobile_field);
		labelfieldpanel.add(ni);
		labelfieldpanel.add(empni_field);
		labelfieldpanel.add(skills_label);


		getSkills(); //get of JCheckBox skills


		//main panel to add labelfield panel and button panel
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout()); //panel layout
		mainPanel.add(labelfieldpanel, BorderLayout.CENTER); //add labelfieldpanel to layout

		//panel for buttons
		JPanel buttonpanel = new JPanel(); 
		buttonpanel.setLayout(new FlowLayout()); //layout for button panel
		buttonpanel.add(clear);
		buttonpanel.add(submit);

		//panel for jcheboxes
		JPanel checkbox_panel = new JPanel();
		checkbox_panel.setLayout(new FlowLayout()); //layout of checkbox panel

		ArrayList<String> emp_skills = new ArrayList<String>();

		for( int i = 0; i < e.employee_skills.size(); i++) {
			emp_skills.add(e.employee_skills.get(i).skill_id);
		}

		//now loop through the list of checkboxes and add each checkbox onto the panel
		for (int i = 0; i < checklist.size(); i++) { 
			checkbox_panel.add(checklist.get(i)); 


			if(emp_skills.indexOf(checklist.get(i).getText()) != -1){ //
				checklist.get(i).setSelected(true);
			}
		}






		//panel and scroll panel to hold skill checkboxes
		JScrollPane scrollpane = new JScrollPane(checkbox_panel); //add checkbox panel to scroll pane. 
		scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);


		JPanel panel = new JPanel(); //panel to hold the mainpanel and the scroll pane that contains a set of jcheckboxes
		panel.setLayout(new BorderLayout());
		panel.add(mainPanel,BorderLayout.CENTER);
		panel.add(scrollpane, BorderLayout.SOUTH);

		//add button panel and mainpanel to main frame
		add(buttonpanel, BorderLayout.SOUTH);
		add(panel, BorderLayout.CENTER);


		//assign listener for buttons
		clear.addActionListener(this);
		submit.addActionListener(this);

		this.setVisible(true);
	}


	public void actionPerformed(ActionEvent event) {

		//if clear is pressed, clear all fields
		if(event.getSource() == clear) {


			firstname_field.setText("");
			lastname_field.setText("");
			address_field.setText("");
			email_field.setText("");
			tel_field.setText("");
			mobile_field.setText("");
			empni_field.setText(""); 

			//deselect checkboxes?
		}

		/*if submit is pressed, connect to database
		 *get all input from fields and store in variables
		 *store the value from variables to database
		 *then close connection
		 */
		if(event.getSource() == submit) {
			try{
				int empni_field_length = this.empni_field.getText().length();
				int firstname_length = this.firstname_field.getText().length();
				int lastname_length = this.lastname_field.getText().length();
				int email_length =this.email_field.getText().length();
				int tel_length = this.tel_field.getText().length();
				int mob_length = this.mobile_field.getText().length();
				int address_length =  this.address_field.getText().length();


				if( empni_field_length != 9 || firstname_length > 15 || lastname_length > 15 || email_length > 50 
						|| tel_length > 11 
						|| mob_length > 11 
						|| address_length > 50)

				{

					JOptionPane.showMessageDialog(this, 
							"NI number has to be 9 chars; " +
									"\n"+"Forename and Surname <=15 chars each; " +
									"\n"+"email <=50 chars; " +
									"\n"+"tel+mob <= 11 chars each; " +
									"\n"+"address <= 50 chars");

				}else{



					//get the data from GUI to store the input from all textfields
					String idfield_input = id_field.getText();
					String firstnamefield_input = firstname_field.getText();
					String lastnamefield_input = lastname_field.getText();
					String addressfield_input = address_field.getText();
					String emailfield_input = email_field.getText();
					long telfield_input = Long.parseLong(tel_field.getText());
					long mobfield_input = Long.parseLong(mobile_field.getText());
					String empnifield_input = empni_field.getText();

					/* public Employee(String emp_id, String firstname, String lastname, 
			String address, String email_address, String ni_num, int telephone,
			int mobile, ArrayList<Skill> employee_skills, ArrayList<Task> employee_tasks, int value)
					 */
					e = new Employee(idfield_input, firstnamefield_input, lastnamefield_input, addressfield_input, emailfield_input,
							empnifield_input, telfield_input, mobfield_input, new ArrayList<Skill>(), new ArrayList<Task>(), 0);

					try {
						SqlConnection.connect();


						SqlConnection.ps = SqlConnection.connection.prepareStatement("UPDATE EMPLOYEE SET FirstName=?, LastName=?" +
								", EmailAddress=?, Telephone=?, Mobile=?, FullAddress=?, NationalInsuranceNumber=? WHERE emp_id=?");

						SqlConnection.ps.setString(1, this.firstname_field.getText());
						SqlConnection.ps.setString(2, this.lastname_field.getText());
						SqlConnection.ps.setString(3, this.email_field.getText());
						SqlConnection.ps.setLong(4, new Long(this.tel_field.getText()));
						SqlConnection.ps.setLong(5, new Long(this.mobile_field.getText()));
						SqlConnection.ps.setString(6, this.address_field.getText());
						SqlConnection.ps.setString(7, this.empni_field.getText());

						SqlConnection.ps.setString(8, this.id_field.getText());

						SqlConnection.ps.executeUpdate();

						// USE PREPARED STATEMENT


						// i added the employee details into employee table


						// FIRST REMOVE ALL EMPLOYEE SKILLS FROM THE EMPLOYEESKILL TABLE
						SqlConnection.ps = SqlConnection.connection.prepareStatement("DELETE FROM EMPLOYEESKILL WHERE emp_id=?");
						SqlConnection.ps.setString(1, this.id_field.getText());
						SqlConnection.ps.executeUpdate();


						// now i have to RE-add employee_skills into employee_skills table
						// loop through checklist
						for(int i = 0; i < checklist.size(); i++) {
							if(checklist.get(i).isSelected()) {
								String skillid = checklist.get(i).getText();
								SqlConnection.statement.executeUpdate("INSERT INTO EMPLOYEESKILL VALUES ('"+skillid+"', '"+idfield_input+"')");
							}
						}

						SqlConnection.closeConnection();//close the connection

						this.setVisible(false);


					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			} catch(Exception e){
				JOptionPane.showMessageDialog(this, "Check your inputs: tel and mob have to be numbers");

			}
		}
	}
	//display dialog box asking task manager if they want to quit, 
	//if task manager does not want to quit, go back to adding more customers
	//if customer quits, close the dialog box and the frame

	public static void main(String[] args) throws SQLException {


	}

}