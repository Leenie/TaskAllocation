/*This is where the task manager can schedule the tasks that are 
 * not currently allocated to any employee
 * 
 */
import javax.swing.*;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;




public class ViewAllSkills extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//names for the table
	/**
	 * @uml.property  name="column_names" multiplicity="(0 -1)" dimension="1"
	 */
	String[] column_names = { "Skill Name", "Skill Description"};

	//scroll panel
	/**
	 * @uml.property  name="scrollpane"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JScrollPane scrollpane;


	static DefaultTableModel skillTableModel;
	static JTable skilltable;



	/**
	 * @uml.property  name="row_count"
	 */
	private int row_count;
	/**
	 * @uml.property  name="column_count"
	 */
	private int column_count;

	/**
	 * @uml.property  name="addSkill"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JButton addSkill;

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

	public ViewAllSkills() throws SQLException {
		super("Skill Database");

		setLayout(new BorderLayout());
		setSize(400,400);

		addSkill = new JButton("Add Skill");

		addSkill.addActionListener(this);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(addSkill);

		try {
			SqlConnection.connect();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!SqlConnection.connection.isClosed()) {
			//query database    select employee id and employee name
			SqlConnection.statement = SqlConnection.connection.createStatement();

			SqlConnection.statement.executeQuery("SELECT skill_id,  skill_description  FROM SKILL ");


			//process query results
			SqlConnection.result = SqlConnection.statement.getResultSet();
		}


		ResultSetMetaData metaData = SqlConnection.result.getMetaData();
		skillTableModel = new DefaultTableModel();
		skilltable = new JTable(skillTableModel);

		skillTableModel.setColumnIdentifiers(column_names);


		column_count = metaData.getColumnCount();
		while(SqlConnection.result.next()) {
			Object[] row = new Object[column_count];
			for(int j = 0; j < column_count; j++) {
				row[j] = SqlConnection.result.getObject(j+1);
			}
			skillTableModel.addRow(row);
			row_count++;

		}
		SqlConnection.closeConnection();

		scrollpane = new JScrollPane(skilltable);


		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		mainPanel.add(scrollpane, BorderLayout.CENTER);
		//mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		add(mainPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);


		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent event) {

		if(event.getSource() == addSkill) {
			NewSkillFrame newSkill = new NewSkillFrame();

			skillTableModel.addRow(newSkill.skill);
		}

	}

	public static void main(String[] args) {
		try {
			ViewAllSkills skills = new ViewAllSkills();
			skills.setVisible(true);
			skills.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			skills.getContentPane();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
