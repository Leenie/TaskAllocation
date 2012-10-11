/*THIS CLASS CREATES THE GUI FOR THE TASK MANAGER
 * CAN VIEW ALL THE EMPLOYEES THAT ARE CURRENTLY
 * STORED IN THE DATABASE.
 * */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;



import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

public class ViewAllEmployees extends JFrame implements ActionListener {

	
	private static final long serialVersionUID = 1L;

	//names for the table
	String[] column_names = { "Employee ID", "First Name", "Last Name", "Email Add", "Telephone", "Mobile", "Full Address", "N.I" };

	//scroll panel
	private JScrollPane scrollpane;

	//employee table model
	DefaultTableModel empTableModel;
	//table for employee
	JTable emp_table;

	
	private int row_count; //number of rows
	private int column_count; // number of columns
	
	//add, edit and remove button
	private JButton addEmp;
	private JButton editEmp;
	private JButton removeEmp;
	
	//button panel
	private JPanel buttonPanel;
	
	//table panel
	private JPanel tablepanel;

	public ViewAllEmployees() throws SQLException {
		
		super("Employee Database");//title of frame

		setLayout(new BorderLayout()); //layout of frame set to as a border layout
		setSize(800,400); //size of frame

		//initialize add, edit and remove button
		addEmp = new JButton("Add Employee");
		editEmp = new JButton("Edit Employee");
		removeEmp = new JButton("Remove Employee");

		//add listeners for the buttons
		addEmp.addActionListener(this);
		editEmp.addActionListener(this);
		removeEmp.addActionListener(this);

		//initialize button panel
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout()); //set layout for button panel to flow layout
		buttonPanel.add(addEmp); //add addEmp button to panel
		buttonPanel.add(editEmp);//add editEmp button to panel
		buttonPanel.add(removeEmp);//add removeEmp button to panel



		try {
			SqlConnection.connect(); //connect to the database
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!SqlConnection.connection.isClosed()) {
			//query database    select employee id and employee name
			SqlConnection.statement = SqlConnection.connection.createStatement();

			SqlConnection.statement.executeQuery("SELECT * FROM EMPLOYEE ");


			//process query results
			SqlConnection.result = SqlConnection.statement.getResultSet();
		}
		
		//store results
		ResultSetMetaData metaData = SqlConnection.result.getMetaData();
		
		//initialise table model
		empTableModel = new DefaultTableModel(); 
		emp_table = new JTable(empTableModel); //add table model to table object

		empTableModel.setColumnIdentifiers(column_names); //set column names of table


		column_count = metaData.getColumnCount(); //give number of columns
		while(SqlConnection.result.next()) {
			Object[] row = new Object[column_count]; //create row object to hold column names
			for(int j = 0; j < column_count; j++) {
				row[j] = SqlConnection.result.getObject(j+1); //add each column name to row object
			}
			empTableModel.addRow(row); //append column names to table model
			row_count++; 

		}
		SqlConnection.closeConnection(); //close connection


		scrollpane = new JScrollPane(emp_table); //attach table to scroll pane

		//panel for main window
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout()); //panel layout set to border layout

		mainPanel.add(scrollpane, BorderLayout.CENTER); //add scrollpane to center of main window panel 
		add(mainPanel, BorderLayout.CENTER); //add main window panel to this frame
		add(buttonPanel, BorderLayout.SOUTH); //add button panel to south of frame 

		this.setVisible(true); //set this window visible
	}

	public void actionPerformed(ActionEvent event) {
		
		//if Add employee button is pressed
		if(event.getSource() == addEmp) {

			try {
				NewEmployeeFrame newemp = new NewEmployeeFrame(); //open New Employee Frame
				empTableModel.addRow(newemp.employee); //update the Table with the new employee
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		//if the edit employee button is pressed
		if(event.getSource() == editEmp) {

			int selectedRow = emp_table.getSelectedRow(); //get the selected line from the table

			if(selectedRow != -1) { //if line is selected
				//select a line from table
				//to edit data pull data from database
				String emp_id = empTableModel.getValueAt(selectedRow, 0).toString();
				Employee employee = new Sql().selectEmployee(emp_id); //pull employee data from database
				try {
					boolean unEditable = false; // assume employee has no future tasks, and therefore is editable

					for(int i = 0; i < employee.employee_tasks.size(); ++i){
						// if the employee has a task that has an end date that is today or after today
						if(employee.employee_tasks.get(i).end_date.compareTo(new java.util.Date()) >= 0){
							unEditable = true; // then the employee cannot have details edited 
						}
					}

					if(unEditable == true){ // if the employee is uneditable, do not allow user to remove 
						JOptionPane.showMessageDialog(this, 
						"This employee has been allocated tasks that have not finished"); //display warning message
					} else{

						EditEmpFrame edit_emp = new EditEmpFrame(employee); //open Edit Employee Frame

						// 	String[] column_names = { "Employee ID", "First Name", "Last Name", "Email Add", "Telephone", "Mobile", "Full Address", "N.I" };
						//set the entered values
						empTableModel.setValueAt(edit_emp.e.firstname, selectedRow, 1);
						empTableModel.setValueAt(edit_emp.e.lastname, selectedRow, 2);
						empTableModel.setValueAt(edit_emp.e.email_address, selectedRow, 3);
						empTableModel.setValueAt(edit_emp.e.telephone, selectedRow, 4);
						empTableModel.setValueAt(edit_emp.e.mobile, selectedRow, 5);
						empTableModel.setValueAt(edit_emp.e.address, selectedRow, 6);
						empTableModel.setValueAt(edit_emp.e.ni_num, selectedRow, 7);

					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		} //end EDIT EMPLOYEE 
		
		//if the Remove Employee button is pressed
		if(event.getSource() == removeEmp) {
			//user has to select a link from the table
			//then remove employee from database including all details
			int selectedRow = emp_table.getSelectedRow();

			if(selectedRow != -1) {
				//select a line from table
				//to edit data pull data from database
				String employee_id = empTableModel.getValueAt(selectedRow, 0).toString();

				try{
					Employee employee = new Sql().selectEmployee(employee_id);
					boolean unEditable = false; // assume employee has no future tasks, and therefore is editable

					for(int i = 0; i < employee.employee_tasks.size(); ++i){
						if(employee.employee_tasks.get(i).end_date.compareTo(new java.util.Date()) >= 0){
							// if the employee has a task that has an end date that is today or after today
							unEditable = true; // then the employee cannot have details edited 
						}
					}

					if(unEditable == true){ // if the employee is uneditable, do not allow user to remove
						JOptionPane.showMessageDialog(this, 
						"This employee has been allocated tasks that have not finished");
					} else{
						SqlConnection.connect();

						// remove from database where emp_id = employee_id
						SqlConnection.ps = SqlConnection.connection.prepareStatement("DELETE FROM EMPLOYEE WHERE emp_id=?");
						SqlConnection.ps.setString(1, employee_id);
						SqlConnection.ps.executeUpdate();
						// remove selected row from model
						empTableModel.removeRow(selectedRow);

					}


				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}


		}
	}

	public static void main(String[] args) throws SQLException {

		ViewAllEmployees allworkers = new ViewAllEmployees ();
		allworkers.setVisible(true);
		allworkers.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		allworkers.getContentPane();
	}
}
