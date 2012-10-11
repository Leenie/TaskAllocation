/*This is where the task manager can schedule the tasks that are 
 * not currently allocated to any employee
 * 
 */
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



public class ViewAllTasks extends JFrame implements ActionListener {

	//names for the table
	/**
	 * @uml.property  name="column_names" multiplicity="(0 -1)" dimension="1"
	 */
	String[] column_names = { "Task ID", "Task Name", "Task Description", "Task Priority", "Due Date", "Number of Days", "Is Allocated" , "Unallocateable" };

	//scroll panel
	/**
	 * @uml.property  name="scrollpane"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JScrollPane scrollpane;


	/**
	 * @uml.property  name="taskTableModel"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	DefaultTableModel taskTableModel;
	/**
	 * @uml.property  name="task_table"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	JTable task_table;

	/**
	 * @uml.property  name="row_count"
	 */
	private int row_count;
	/**
	 * @uml.property  name="column_count"
	 */
	private int column_count;

	/**
	 * @uml.property  name="addTask"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JButton addTask;
	/**
	 * @uml.property  name="editTask"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JButton editTask;
	/**
	 * @uml.property  name="removeTask"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JButton removeTask;

	/**
	 * @uml.property  name="buttonPanel"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JPanel buttonPanel;
	/**
	 * @uml.property  name="tablepanel"
	 * @uml.associationEnd  readOnly="true"
	 */
	private JPanel tablepanel;

	public ViewAllTasks() throws SQLException {
		super("View Tasks");

		setLayout(new BorderLayout());
		setSize(800,400);

		addTask = new JButton("Add Task");
		editTask = new JButton("Edit Task");
		removeTask = new JButton("Remove Task");

		addTask.addActionListener(this);
		editTask.addActionListener(this);
		removeTask.addActionListener(this);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(addTask);
		buttonPanel.add(editTask);
		buttonPanel.add(removeTask);

		try {
			SqlConnection.connect();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 


		if(!SqlConnection.connection.isClosed()) {
			//query database    select employee id and employee name
			SqlConnection.statement = SqlConnection.connection.createStatement();

			SqlConnection.statement.executeQuery("SELECT * FROM TASK ");

			SqlConnection.result = SqlConnection.statement.getResultSet();
		}

		ResultSetMetaData metaData = SqlConnection.result.getMetaData();
		taskTableModel = new DefaultTableModel();
		task_table = new JTable(taskTableModel);

		taskTableModel.setColumnIdentifiers(column_names);


		column_count = metaData.getColumnCount();
		while(SqlConnection.result.next()) {
			Object[] row = new Object[column_count];
			for(int j = 0; j < column_count; j++) {
				row[j] = SqlConnection.result.getObject(j+1);
			}
			taskTableModel.addRow(row);
			row_count++;

		}
		SqlConnection.closeConnection();


		scrollpane = new JScrollPane(task_table);


		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		mainPanel.add(scrollpane, BorderLayout.CENTER);
		//mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		add(mainPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);


		this.setVisible(true);

	}

	public void actionPerformed(ActionEvent event) {

		if(event.getSource() == addTask) {
			NewTaskFrame newtask;
			try {
				newtask = new NewTaskFrame();
				newtask.setSize(400,400);
				newtask.setVisible(true);
				taskTableModel.addRow(newtask.task);

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			


		}
		if(event.getSource() == editTask) {

			//select a line from table
			//to edit data pull data from database

			int selectedRow = task_table.getSelectedRow();

			if(selectedRow != -1) {

				String task_id = taskTableModel.getValueAt(selectedRow, 0).toString();
				Task task = new Sql().selectTask(task_id);


				try {
					if(task.isallocated == 1){ // if the task is allocated
						if(task.end_date.compareTo(new java.util.Date()) >= 0){ // AND if the end date is today or after today
							JOptionPane.showMessageDialog(this, 
							"This task is currently allocated to an employee and is unfinished");
						}else{
							JOptionPane.showMessageDialog(this, 
							"This task was a) allocated and b) has expired, so you may not edit it");
						}
					} else{

						EditTaskFrame edit_task = new EditTaskFrame(task);
						// 	String[] column_names = { "Task ID", "Task Name", "Task Description", "Task Priority", "Due Date", "Number of Days", "Is Allocated" , "Unallocateable" };

						taskTableModel.setValueAt(edit_task.t.task_name, selectedRow, 1);
						taskTableModel.setValueAt(edit_task.t.task_description, selectedRow, 2);
						taskTableModel.setValueAt(edit_task.t.priority_value, selectedRow, 3);
						taskTableModel.setValueAt(edit_task.t.due_date, selectedRow, 4);
						taskTableModel.setValueAt(edit_task.t.numberofDays, selectedRow, 5);
						taskTableModel.setValueAt(edit_task.t.isallocated, selectedRow, 6);
						taskTableModel.setValueAt(edit_task.t.unallocateable, selectedRow, 7);



					}
				}catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}




			}
		}
		if(event.getSource() == removeTask) {
			//user has to select a link from the table
			//then remove task from database
			int selectedRow = task_table.getSelectedRow();

			if(selectedRow != -1) {
				//select a line from table
				//to edit data pull data from database
				String task_id = taskTableModel.getValueAt(selectedRow, 0).toString();

				try{
					Task task = new Sql().selectTask(task_id);

					if(task.isallocated == 1){ // if the task is allocated
							JOptionPane.showMessageDialog(this, 
							"This task is allocated to an employee therefore you cannot delete it through this program");
					} else{
						SqlConnection.connect();

						// remove from database where emp_id = employee_id
						SqlConnection.ps = SqlConnection.connection.prepareStatement("DELETE FROM TASK WHERE task_id=?");
						SqlConnection.ps.setString(1, task_id);
						SqlConnection.ps.executeUpdate();
						// remove selected row from model
						taskTableModel.removeRow(selectedRow);

					}


				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public static void main(String[] args) throws SQLException {

		ViewAllTasks alltasks = new ViewAllTasks();
		alltasks.setVisible(true);
		alltasks.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		alltasks.getContentPane();

	}
}
