/*this is the main actual window where the user will be redirected to 
 * after a successful login to use the application
 * the user will be able to choose any feature that the application consists of
 * 
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class TaskManagerFrame extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2631905477396970181L;
	//create buttons

	/**
	 * @uml.property  name="viewTasks"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JButton viewTasks; //to view all the tasks that are stored in the database
	/**
	 * @uml.property  name="viewEmployees"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JButton viewEmployees; // to view all the employees that are stored in the database
	/**
	 * @uml.property  name="viewSkills"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JButton viewSkills; // to view all the skills that are stored in the database

	/**
	 * @uml.property  name="createSchedule"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JButton createSchedule; // to create a new batch of schedule
	/**
	 * @uml.property  name="viewAllocations"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JButton viewAllocations; // to view all the current allocations (tasks that are allocated to an employee)
	/**
	 * @uml.property  name="exit"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JButton exit;

	public TaskManagerFrame() {

		super("Task Manager");
		setLayout(new BorderLayout());
		this.setSize(400,200);
		
		Font font = new Font("Serif", Font.BOLD, 30);
		JLabel title = new JLabel("Task Allocation System");
		JLabel viewdatabase_label = new JLabel("Database:");
		title.setFont(font);
		
		//initialise buttons
		viewTasks = new JButton("View Tasks");
		viewEmployees = new JButton("View Employees");
		viewSkills = new JButton("View Skills");
		viewAllocations = new JButton("View Allocations");
		createSchedule = new JButton("Create Schedule");
		exit = new JButton("Exit");

		
		
		//panel to hold buttons for viewing database information: Employee, Skills, Tasks, Allocations
		JPanel view_panel = new JPanel();
		view_panel.setLayout(new GridLayout(2,2));
		view_panel.add(viewTasks);
		view_panel.add(viewEmployees);
		view_panel.add(viewSkills);
		view_panel.add(viewAllocations);
		

		
		//panel to hold database label
		JPanel databasePanel = new JPanel();
		databasePanel.setLayout(new BorderLayout());
		databasePanel.add(viewdatabase_label, BorderLayout.NORTH);
		databasePanel.add(view_panel, BorderLayout.CENTER);
		

		//panel to hold buttons: creatingSchedule and exit
		JPanel schedAlloPanel = new JPanel();
		schedAlloPanel.setLayout(new GridLayout(1,2));
		schedAlloPanel.add(createSchedule);
		schedAlloPanel.add(exit);

		this.add(title, BorderLayout.NORTH);
		this.add(databasePanel, BorderLayout.CENTER);
		this.add(schedAlloPanel, BorderLayout.SOUTH);

		//listeners for the buttons
		viewTasks.addActionListener(this);
		viewEmployees.addActionListener(this);
		viewSkills.addActionListener(this);
		createSchedule.addActionListener(this);
		viewAllocations.addActionListener(this);
		exit.addActionListener(this);
		
		this.setVisible(true);
		
		
	}

	public void actionPerformed(ActionEvent e) {

		if(e.getSource() == viewTasks) {
			try {
				ViewAllTasks alltasks = new ViewAllTasks();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		if(e.getSource() == viewEmployees) {
			
			try {
				ViewAllEmployees all_employees = new ViewAllEmployees();
				
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		if(e.getSource() == viewSkills) {
			
			try {
				ViewAllSkills	allskills = new ViewAllSkills();
				
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
		}

		if(e.getSource() == viewAllocations) {
			
			try {
				ViewAllocations	allo = new ViewAllocations();
				
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		if(e.getSource() == createSchedule) {
			
			try {
				ScheduleTaskFrame schedtask = new ScheduleTaskFrame();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		
		if(e.getSource() == exit){
			System.exit(0);
		}
	}


	public static void main(String[] args){
		TaskManagerFrame managerFrame = new TaskManagerFrame();
		managerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		managerFrame.getContentPane();
		managerFrame.pack();
	}
}
