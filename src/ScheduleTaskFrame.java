/*This is where the task manager can schedule the tasks that are 
 * not currently allocated to any employee
 * 
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;



import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class ScheduleTaskFrame extends JFrame implements ActionListener {

	//names for the table

	String[] availEmp_column_names = {"Employee ID", "First Name", "Skills" }; //column names for available employee table
	String[] availTask_column_names = {"Task ID", "Task Name", "Start by Date", "Required Skills"}; // column names for available tasks
	String[] unconfirmedtasks_column_names = {"Task ID", "Employee ID", "Start Date", "End Date"}; // column names for unconfirmed tasks
	String[] unallocateable_column_names = {"Task ID", "Task Name", "Required Skills"}; //column names for unallocateable jtable


	JButton allocate_task;
	JButton confirm;


	//Table model for available employees
	DefaultTableModel availableemployeeModel;
	JTable avail_employee_table;

	//table mode for available tasks
	DefaultTableModel availabletasksModel;
	JTable avail_task_table;

	//table model for unconfirmed tasks
	DefaultTableModel unconfirmedtaskModel;
	JTable unconfirmed_task_table;

	//table model for unallocateable tasks
	DefaultTableModel unallocateableModel;
	JTable unallocateable_table; 


	ArrayList<Employee> available_emp_list;
	ArrayList<Task> available_task_list;
	ArrayList<Task> unconfirmed_task_list;
	ArrayList<Task> unallocateable_list;


	Allocation alloc;

	public ScheduleTaskFrame() throws SQLException {
		super("Scheduler");

		setLayout(new BorderLayout());

		this.setSize(1500, 500);


		//labels
		JLabel availemp_label = new JLabel("Available Employees");
		JLabel availtask_label = new JLabel("Available Tasks");
		JLabel unconfirmed_label = new JLabel("Unconfirmed Task Allocations");
		JLabel unallocatedtask_label = new JLabel("Unallocateable Tasks");


		this.alloc = new Allocation(); // ALLOCATION CLASS SUPPLIES THIS SCHEDULETASKFRAME CLASS
		this.alloc.initiate(); // INITIALISE THE DATA FROM DATABASE
		// THEN GET THE LISTS OF DATA FROM ALLOCATION CLASS

		//CREATE TABLE FOR AVAILABLEEMPLOYEES
		//sql qeury of avilable employess
		//QUERY ALL AVAILABLEMPLOYEES
		//ADD AVAILABLEEMPLOYEES TO JTABLE
		availableemployeeModel = new DefaultTableModel();
		avail_employee_table = new JTable(availableemployeeModel);
		availableemployeeModel.setColumnIdentifiers(availEmp_column_names);

		available_emp_list = new ArrayList<Employee>(this.alloc.availEmplList);

		for(int i = 0; i < available_emp_list.size(); ++i){
			Object[] array = new Object[3];
			array[0] = available_emp_list.get(i).emp_id;
			array[1] = available_emp_list.get(i).firstname;

			// skills -> write in a string
			StringBuffer skills = new StringBuffer();

			for(int j = 0; j < available_emp_list.get(i).employee_skills.size(); ++j){
				skills.append(available_emp_list.get(i).employee_skills.get(j).skill_id + "; ");

			}

			array[2] = skills.toString();

			availableemployeeModel.addRow(array);

		}

		//CREATE TABLE FOR AVAILABLE TASKS
		//sql qeury of availabletasks
		//add available tasks to JTABLE
		availabletasksModel = new DefaultTableModel();
		avail_task_table = new JTable(availabletasksModel);
		availabletasksModel.setColumnIdentifiers(availTask_column_names);

		available_task_list= new ArrayList<Task>(this.alloc.waitTasksList);

		java.util.Collections.sort(available_task_list); // this uses the comparable interface in the class Task to order each task 

		for(int i = 0; i < available_task_list.size(); ++i) {
			Object[] array = new Object[4];
			array[0] = available_task_list.get(i).task_id;
			array[1] = available_task_list.get(i).task_name;

			array[2] = new StringBuffer(new SimpleDateFormat("dd/MM/yyyy").format(available_task_list.get(i).latest_possible_start)).toString();


			// skills -> write in a string
			StringBuffer skills = new StringBuffer();

			for(int j = 0; j < available_task_list.get(i).required_skills.size(); ++j){
				skills.append(available_task_list.get(i).required_skills.get(j).skill_id + "; ");
			}

			array[3] = skills.toString();

			availabletasksModel.addRow(array);
		}



		//table model for unconfirmed tasks
		unconfirmedtaskModel = new DefaultTableModel();
		unconfirmed_task_table = new JTable(unconfirmedtaskModel);
		unconfirmedtaskModel.setColumnIdentifiers(unconfirmedtasks_column_names);

		unconfirmed_task_list = new ArrayList<Task>(); //at the start there are no unconfirmed allocations


		//table model for unallocateable tasks
		unallocateableModel = new DefaultTableModel();
		unallocateable_table = new JTable(unallocateableModel);
		unallocateableModel.setColumnIdentifiers(unallocateable_column_names);

		unallocateable_list = new ArrayList<Task>(); // at the start there are no unallocateable tasks


		//scroll panes
		JScrollPane availemp_pane;
		JScrollPane availtask_pane;
		JScrollPane unconfirmedtask_pane;
		JScrollPane unallocateabletask_pane;

		availemp_pane = new JScrollPane(avail_employee_table);
		availtask_pane = new JScrollPane(avail_task_table);
		unconfirmedtask_pane = new JScrollPane(unconfirmed_task_table);
		unallocateabletask_pane = new JScrollPane(unallocateable_table);	

		JPanel availemppanel = new JPanel();
		availemppanel.setLayout(new BorderLayout());
		availemppanel.add(availemp_label, BorderLayout.NORTH);
		availemppanel.add(availemp_pane, BorderLayout.SOUTH);

		JPanel availtaskpanel = new JPanel();
		availtaskpanel.setLayout(new BorderLayout());
		availtaskpanel.add(availtask_label, BorderLayout.NORTH);
		availtaskpanel.add(availtask_pane, BorderLayout.SOUTH);

		JPanel unconfirmedpanel = new JPanel();
		unconfirmedpanel.setLayout(new BorderLayout());
		unconfirmedpanel.add(unconfirmed_label, BorderLayout.NORTH);
		unconfirmedpanel.add(unconfirmedtask_pane, BorderLayout.SOUTH);

		JPanel unallopanel = new JPanel();
		unallopanel.setLayout(new BorderLayout());
		unallopanel.add(unallocatedtask_label, BorderLayout.NORTH);
		unallopanel.add(unallocateabletask_pane, BorderLayout.SOUTH);


		JPanel tablelabelpanel = new JPanel();
		tablelabelpanel.setLayout(new GridLayout(1,4));
		tablelabelpanel.add(availemppanel);
		tablelabelpanel.add(availtaskpanel);
		tablelabelpanel.add(unconfirmedpanel);
		tablelabelpanel.add(unallopanel);



		allocate_task = new JButton("Allocate Tasks");
		confirm = new JButton("Confirm Allocations");
		confirm.setEnabled(false); // cannot confirm before allocating

		allocate_task.addActionListener(this);
		confirm.addActionListener(this);

		//panel for buttons
		JPanel button_panel = new JPanel();
		button_panel.setLayout(new FlowLayout());
		button_panel.add(allocate_task);
		button_panel.add(confirm);

		//main panel
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		mainPanel.add(tablelabelpanel, BorderLayout.CENTER);
		this.add(mainPanel, BorderLayout.CENTER);
		this.add(button_panel, BorderLayout.SOUTH);

		//SqlConnection.closeConnection();
		setVisible(true);
	}


	/*IMPLEMENT EVENT HANDLING FOR  BUTTONS*/
	public void actionPerformed(ActionEvent event) {


		if(event.getSource() == allocate_task) {
			allocate_task.setEnabled(false);
			confirm.setEnabled(true);

			//run allocation class - run the earliest deadline task algorithm 

			//run hungarian class - run the hungarian algorithm
			//update isallocated of tasks to 1
			long total_tasks_cost = 0;
			for(int i = 0; i < this.alloc.waitTasksList.size(); ++i){
				total_tasks_cost = total_tasks_cost + this.alloc.waitTasksList.get(i).value;
			}




			alloc.createHungarianSchedule(); // comment out line if using greedy
			System.out.println("using Hungarian scheduling"); // comment out line if using greedy
			//alloc.createGreedySchedule(); // comment out line if using hungarian
			//System.out.println("using greedy scheduling"); // comment out line if using hungarian

			this.available_emp_list = new ArrayList<Employee>(this.alloc.availEmplList);
			this.available_task_list= new ArrayList<Task>(this.alloc.waitTasksList);
			this.unconfirmed_task_list = new ArrayList<Task>(this.alloc.unconfirmedList); //at the start there are no unconfirmed allocations
			this.unallocateable_list = new ArrayList<Task>(this.alloc.unallocableList); // at the start there are no unallocateable tasks

			System.out.println("The highest possible cost of allocation (that cannot be allowed) is " + this.alloc.impossibleValue);
			System.out.println("A cost of '0' is the best cost for allocating an employee to a task");

			long total_cost_of_allocation = 0;

			for(int i = 0; i < this.alloc.unconfirmedList.size(); ++i){ // looping through tasks
				long employeeminustask = this.alloc.unconfirmedList.get(i).allocatedEmployee.value - this.alloc.unconfirmedList.get(i).value;
				
				System.out.println("Cost of allocating task " + this.alloc.unconfirmedList.get(i).task_id + " to employee " + 
						this.alloc.unconfirmedList.get(i).allocatedEmployee.emp_id + " is " + this.alloc.unconfirmedList.get(i).allocatedEmployee.value
						+"-"+this.alloc.unconfirmedList.get(i).value+" = "+employeeminustask + 
						" and starts on " +new SimpleDateFormat("dd/MM/yyyy").format(this.alloc.unconfirmedList.get(i).start_date).toString()
						+ " and ends on "+ new SimpleDateFormat("dd/MM/yyyy").format(this.alloc.unconfirmedList.get(i).end_date).toString());

				
				total_cost_of_allocation = total_cost_of_allocation + employeeminustask;
			}

			System.out.println();
			

			System.out.println("# tasks could not be allocated: "+new Long(this.alloc.unallocableList.size()).toString() );

			System.out.println();
			for(int i = 0; i < this.alloc.unallocableList.size(); ++i){ // looping through tasks
				System.out.println("Task could not be allocated: task id ="+this.alloc.unallocableList.get(i).task_id);
			}
			System.out.println();


			System.out.println("Total value of tasks that were waiting to be allocated = " + new Long(total_tasks_cost).toString());

			long unconfirmed_cost = 0;
			for(int i = 0; i < this.alloc.unconfirmedList.size(); ++i){
				unconfirmed_cost = unconfirmed_cost + this.alloc.unconfirmedList.get(i).value;
			}
			System.out.println("Total value of tasks that can be allocated using this algorithm = " + new Long(unconfirmed_cost).toString());

			long unallocable_cost = 0;
			for(int i = 0; i < this.alloc.unallocableList.size(); ++i){
				unallocable_cost = unallocable_cost  + this.alloc.unallocableList.get(i).value;
			}
			System.out.println("Total value of tasks that cannot be allocated using this algorithm = " + new Long(unallocable_cost).toString());
			System.out.println();
			System.out.println("This cost of allocating (employee value minus their allocated task value) " +
					"using this algorithm is: " + new Long(total_cost_of_allocation).toString());


			// insert unallocable tasks into the database
			Sql sql = new Sql();
			sql.updateUnallocable(this.unallocateable_list);


			// clear all the jtables
			// taskTableModel.getDataVector().removeAllElements();
			// taskTableModel.fireTableDataChanged();
			this.availableemployeeModel.getDataVector().removeAllElements();
			this.availableemployeeModel.fireTableDataChanged();

			this.availabletasksModel.getDataVector().removeAllElements();
			this.availabletasksModel.fireTableDataChanged();

			this.unconfirmedtaskModel.getDataVector().removeAllElements();
			this.unconfirmedtaskModel.fireTableDataChanged();

			this.unallocateableModel.getDataVector().removeAllElements();
			this.unallocateableModel.fireTableDataChanged();

			//update lists and tables
			for(int i = 0; i < available_emp_list.size(); ++i){
				Object[] array = new Object[3];
				array[0] = available_emp_list.get(i).emp_id;
				array[1] = available_emp_list.get(i).firstname;

				// skills -> write in a string
				StringBuffer skills = new StringBuffer();

				for(int j = 0; j < available_emp_list.get(i).employee_skills.size(); ++j){
					skills.append(available_emp_list.get(i).employee_skills.get(j).skill_id + "; ");

				}

				array[2] = skills.toString();

				availableemployeeModel.addRow(array);

			}



			for(int i = 0; i < this.unconfirmed_task_list.size(); ++i) {
				Object[] array = new Object[4];
				array[0] = unconfirmed_task_list.get(i).task_id;
				array[1] = unconfirmed_task_list.get(i).allocatedEmployee.emp_id;

				array[2] = new StringBuffer(new SimpleDateFormat("dd/MM/yyyy").format(unconfirmed_task_list.get(i).start_date)).toString();

				array[3] = new StringBuffer(new SimpleDateFormat("dd/MM/yyyy").format(unconfirmed_task_list.get(i).end_date)).toString();


				this.unconfirmedtaskModel.addRow(array);
			}

			for(int i = 0; i < this.unallocateable_list.size(); ++i) {
				Object[] array = new Object[3];
				array[0] = unallocateable_list.get(i).task_id;
				array[1] = unallocateable_list.get(i).task_name;

				// skills -> write in a string
				StringBuffer skills = new StringBuffer();

				for(int j = 0; j < unallocateable_list.get(i).required_skills.size(); ++j){
					skills.append(unallocateable_list.get(i).required_skills.get(j).skill_id + "; ");
				}

				array[2] = skills.toString();

				this.unallocateableModel.addRow(array);
			}

		}





		if(event.getSource() == confirm) {
			confirm.setEnabled(false); // both buttons must be disabled now


			//confirm allocations and insert into database
			new Sql().insertAllocations(this.unconfirmed_task_list);

			this.unconfirmedtaskModel.getDataVector().removeAllElements();
			this.unconfirmedtaskModel.fireTableDataChanged();

			try{
				ViewAllocations viewAllocations = new ViewAllocations();
			}catch(SQLException e){
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, 
						this.unconfirmed_task_list.size() + 
						"\n task allocations committed to database;" +
						"\n To view the allocations, go back to the main window" +
						"\n and click \"View Allocations\"");
			}

			// update unallocable

			// insert unconfirmed allocations; clear unconfirmed jtable

		}
	}

	public static void main(String[] args) {
		//TableModel scheduleTablemodel = new DefaultTableModel();
		//JTable schedtable = new JTable(scheduleTablemodel);

		try {
			ScheduleTaskFrame schedtask = new ScheduleTaskFrame();
			schedtask.setVisible(true);
			schedtask.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			schedtask.getContentPane();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}