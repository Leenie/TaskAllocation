/*This is where the task manager can schedule the tasks that are 
 * not currently allocated to any employee
 * 
 */
import javax.swing.*;

import javax.swing.table.DefaultTableModel;



import java.awt.*;
import java.awt.event.*;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;



public class ViewAllocations extends JDialog implements ActionListener {

	//names for the table
	/**
	 * @uml.property  name="column_names" multiplicity="(0 -1)" dimension="1"
	 */
	String[] column_names = { "Task ID", "Employee ID", "Start Date", "End Date", "Progress" };

	//scroll panel
	/**
	 * @uml.property  name="scrollpane"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JScrollPane scrollpane;


	/**
	 * @uml.property  name="alloTableModel"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	DefaultTableModel alloTableModel;
	/**
	 * @uml.property  name="allocation_table"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	JTable allocation_table;



	
	/**
	 * @uml.property  name="exit"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JButton exit;

	public ViewAllocations() throws SQLException {
		this.setModal(true);
		this.setTitle("Current Allocations");
		
		setLayout(new BorderLayout());
		setSize(400,400);


		ArrayList<Task> allocations = new ArrayList<Task>(new Sql().selectAllocations());



		alloTableModel = new DefaultTableModel();
		allocation_table = new JTable(alloTableModel);

		alloTableModel.setColumnIdentifiers(column_names);


		for(int i = 0; i < allocations.size(); ++i) {
			// 	String[] column_names = { "Task ID", "Employee ID", "Start Date", "End Date", "Progress" };

			Object[] row = new Object[5];
			row[0] = allocations.get(i).task_id;
			row[1] = allocations.get(i).allocatedEmployee.emp_id;
			row[2] = allocations.get(i).start_date;
			row[3] = allocations.get(i).end_date;



			row[4] =  (allocations.get(i).end_date.getTime() - new java.util.Date().getTime()) 
					/ (1000 * 60 * 60 * 24) +1 + " days left";

			alloTableModel.addRow(row);

		}


		scrollpane = new JScrollPane(allocation_table);
		
		exit = new JButton("Exit");

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(scrollpane, BorderLayout.CENTER);
		//mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		JPanel button_panel = new JPanel();
		button_panel.setLayout(new FlowLayout());
		
		button_panel.add(exit);
		add(mainPanel, BorderLayout.CENTER);
		add(button_panel, BorderLayout.SOUTH);

		try
		{
			SqlConnection.closeConnection();

		} //end try
		catch (Exception exception) 
		{
			exception.printStackTrace();
		} //end catch

	
		exit.addActionListener(this);

		this.setVisible(true);
	}


	public void actionPerformed(ActionEvent event) {

		if(event.getSource() == exit) {
			this.setVisible(false);
			
		}
	}

	public static void main(String[] args) {
		try {
			ViewAllocations allocations = new ViewAllocations();
			
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
