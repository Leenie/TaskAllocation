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

public class NewEmployeeFrame extends JDialog implements ActionListener {


	private static final long serialVersionUID = 1L;
	//textfield

	private JTextField id_field; // to enter employee id
	private JTextField firstname_field; //to enter first name
	private JTextField lastname_field; // to enter last name
	private JTextField email_field; // to enter email
	private JTextField tel_field;
	private JTextField mobile_field;
	private JTextField address_field;
	private JTextField empni_field;

	//buttons for clear and submit
	private JButton clear;
	private JButton submit;

	//checkbox for manager to select the skills to assign to the employee
	ArrayList<JCheckBox> checklist;
	Object[] employee;

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



	//constructor to create frame and initialise fields
	public NewEmployeeFrame() throws SQLException {

		this.setModal(true);
		this.setTitle("New Employee"); //title
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
		Object[] id_possibilities = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
		StringBuffer generated_name = new StringBuffer("k");

		for(int i = 0; i < 6; ++i){
			generated_name.append(id_possibilities[new java.util.Random().nextInt(id_possibilities.length)]);
		}


		id_field = new JTextField(generated_name.toString());
		id_field.setEditable(false);
		firstname_field = new JTextField();
		lastname_field = new JTextField();
		email_field = new JTextField();
		tel_field = new JTextField();
		mobile_field = new JTextField();
		address_field = new JTextField();
		empni_field = new JTextField();

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


		this.getSkills(); //get of JCheckBox skills


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

		//now loop through the list of checkboxes and add each checkbox onto the panel
		for (int i = 0; i < checklist.size(); i++) { 
			checkbox_panel.add(checklist.get(i)); 
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
			
			try {
			if(this.id_field.getText().length() != 7 ) {
				JOptionPane.showMessageDialog(this, "Employee ID should be 7 chars.");
			} else if (this.empni_field.getText().length() != 9) {
				JOptionPane.showMessageDialog(this, "\n"+"NI number has to be 9 chars. ");
			} else if (this.firstname_field.getText().length()>15) {
				JOptionPane.showMessageDialog(this, " First name <= 15 chars.");
			} else if(this.lastname_field.getText().length()>15) {
				JOptionPane.showMessageDialog(this, "Last Name <= 15 chars");
			} else if (this.email_field.getText().length()>50) {
				JOptionPane.showMessageDialog(this, "email <=50 chars");
			} else if(this.tel_field.getText().length()>11) {
				JOptionPane.showMessageDialog(this, "tel <=11 chars");
			} else if (this.mobile_field.getText().length()>11) {
				JOptionPane.showMessageDialog(this, "mob <= 11 chars");
			} else if (this.address_field.getText().length()>50 ) {
				JOptionPane.showMessageDialog(this, "address <= 50 chars");
			} else {
				try{
					try {
						SqlConnection.connect();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					//get the data from GUI to store the input from all textfields
					String idfield_input = id_field.getText();
					String firstnamefield_input = firstname_field.getText();
					String lastnamefield_input = lastname_field.getText();
					String addressfield_input = address_field.getText();
					String emailfield_input = email_field.getText();

					long telfield_input = Long.parseLong(tel_field.getText());
					long mobfield_input = Long.parseLong(mobile_field.getText());

					String empnifield_input = empni_field.getText();

					employee = new Object[8];

					employee[0] = idfield_input;
					employee[1] = firstnamefield_input;
					employee[2] = lastnamefield_input;
					employee[3] = emailfield_input;
					employee[4] = telfield_input;
					employee[5] = mobfield_input;
					employee[6] = addressfield_input;
					employee[7] = empnifield_input;


					//try {

					SqlConnection.statement = SqlConnection.connection.createStatement();

					SqlConnection.statement.executeUpdate("INSERT INTO EMPLOYEE VALUES ('"+idfield_input+"', '"+
							firstnamefield_input+"', '"+lastnamefield_input+"', '"+emailfield_input+"', '"+telfield_input+"', '"
							+mobfield_input+"', '"+addressfield_input+"', '"+empnifield_input+"')");
					// i added the employee details into employee table

					// now i have to add employee_skills into employee_skills table
					// loop through checklist
					for(int i = 0; i < checklist.size(); i++) {
						if(checklist.get(i).isSelected()) {
							String skillid = checklist.get(i).getText();
							SqlConnection.statement.executeUpdate("INSERT INTO EMPLOYEESKILL VALUES ('"+skillid+"', '"+idfield_input+"')");
						}
					}

					SqlConnection.closeConnection();//close the connection

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				this.setVisible(false);
			}
		} 
		catch(Exception e){
			JOptionPane.showMessageDialog(this, "Check your inputs: tel and mob have to be numbers");
		}
		}
	}

	//display dialog box asking task manager if they want to quit, 
	//if task manager does not want to quit, go back to adding more customers
	//if customer quits, close the dialog box and the frame

	public static void main(String[] args) throws SQLException {
		NewEmployeeFrame createEmp = new NewEmployeeFrame();


	}

}