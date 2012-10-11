import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

/*THIS CLASS CONTAINS ALL SQL METHODS 
 * FOR MANIPULATING THE DATABASE
 */

public class Sql {

	/*
	 * METHOD FOR SELECTING ALL SKILLS:
	 * get's a list of all the skills including it's details 
	 * then the method will find the market value of skills
	 */
	public ArrayList<Skill> selectAllSkills(){ // select * from skill table
		ArrayList<Skill> skill_list = new ArrayList<Skill>(); // query Skill table; store as list of skills

		try{
			SqlConnection.connect();
			SqlConnection.statement = SqlConnection.connection.createStatement();
			SqlConnection.statement.execute("SELECT * FROM SKILL");
			SqlConnection.result = SqlConnection.statement.getResultSet();
			while(SqlConnection.result.next()){

				//add skills to list
				skill_list.add(new Skill(SqlConnection.result.getString("skill_id"), 
						SqlConnection.result.getString("skill_description")));
			}

			// find market value of skills, by finding demand from tasks in TASKSKILL table
			for(int i = 0; i < skill_list.size(); ++i){ // for each skill...
				SqlConnection.ps = SqlConnection.connection.prepareStatement("SELECT * FROM TASKSKILL WHERE skill_id=?");
				SqlConnection.ps.setString(1, skill_list.get(i).skill_id);
				SqlConnection.ps.executeQuery();
				SqlConnection.result = SqlConnection.ps.getResultSet();

				// ... retrieve how many tasks have this skill: market value = size of demand
				while(SqlConnection.result.next()){ // if doesn't exist, then ends here and value = 0
					++skill_list.get(i).value; // if does exist, for each row increment the skill value by 1
				}
				
				// add nominal value of skill; nominal = 1 skill
				++skill_list.get(i).value;
				SqlConnection.ps.close();
			} // finish valuing skills
			SqlConnection.closeConnection(); //close connection
		}catch(SQLException e){
			e.printStackTrace();
		}
		return skill_list;
	}



	/*METHOD FOR SELECTING ALL EMPLOYEES:
	 * get's details from the EMPLOYEE table, EMPLOYEESKILLS TABLE and ALLOCATEDTASK table
	 * retrieve's list of employees including their details
	 * get's skills of each employee from EMPLOYEESKILL table and finds value for its skill.
	 * this method creates a JAVA representation of the employee in the database 
	 *  */
	public ArrayList<Employee> selectAllEmployees()  {

		ArrayList<Employee> employee_list = new ArrayList<Employee>(); //list to hold all employees

		try{
			ArrayList<Skill> skill_list = new ArrayList<Skill>(); 
			skill_list.addAll(selectAllSkills()); // list of ALL skills is used to narrow down and find skills each employee has


			SqlConnection.connect();
			SqlConnection.statement = SqlConnection.connection.createStatement();
			SqlConnection.statement.execute("SELECT * FROM EMPLOYEE "); //select employee details
			SqlConnection.result = SqlConnection.statement.getResultSet();

			while (SqlConnection.result.next()) { //add each employee from EMPLOYEE table to employee_list

				employee_list.add(new Employee(SqlConnection.result.getString("emp_id"), SqlConnection.result.getString("FirstName"),
						SqlConnection.result.getString("LastName"), SqlConnection.result.getString("FullAddress"), 
						SqlConnection.result.getString("EmailAddress"), SqlConnection.result.getString("NationalInsuranceNumber"), 
						SqlConnection.result.getLong("Telephone"), SqlConnection.result.getLong("Mobile"), 
						new ArrayList<Skill>(), new ArrayList<Task>(), 0));
			} //finish of retrieving all employees


			/*get skills of each employee from EMPLOYEESKILL table
			 * add each skill to the employee skill list
			 * */
			for (int i = 0; i < employee_list.size(); i++) { // loop through each employee in the list

				SqlConnection.ps = SqlConnection.connection.prepareStatement("SELECT * FROM EMPLOYEESKILL WHERE emp_id=?"); // find the employee's skills
				SqlConnection.ps.setString(1, employee_list.get(i).emp_id);
				SqlConnection.ps.executeQuery();
				SqlConnection.result = SqlConnection.ps.getResultSet(); //store the skills as result


				while(SqlConnection.result.next()) { //add them one by one the representation in JAVA of adding skills

					for(int j = 0; j < skill_list.size(); ++j) { // search list of skills for the details of skill from SQL query

						if(skill_list.get(j).skill_id.equals(SqlConnection.result.getString("skill_id"))) { // if the skill is found
							employee_list.get(i).employee_skills.add(skill_list.get(j)); // add it to the employee's list of skills

							// add nominal + market value of skill to employee value:
							employee_list.get(i).value += skill_list.get(j).value;
							break; // since the skill was found, break the loop, and find the details of the next skill from the loop
						}
					}
				}


				/*LOOP FOR LIST OF EMPLOYEES
				 * FIND OUT IF THE EMPLOYEE HAS ANY ALLOCATED TASKS
				 * IF THEY DO HAVE ALLOCATED TASK
				 * THEN ADD THEM TO THE EMPLOYEE SCHEDULE*/
				//next: get ALL EMPLOYEE'S TASKS from EMPLOYEETASK table, IF employee is already allocated ANY tasks

				SqlConnection.ps = SqlConnection.connection.prepareStatement("SELECT * FROM ALLOCATEDTASK WHERE emp_id=?");
				SqlConnection.ps.setString(1, employee_list.get(i).emp_id);
				SqlConnection.ps.executeQuery();
				SqlConnection.result= SqlConnection.ps.getResultSet();

				while(SqlConnection.result.next()) { //if not allocated, it ends here; else adds each task to employee

					employee_list.get(i).employee_tasks.add(new Task(SqlConnection.result.getString("task_id"),
							SqlConnection.result.getDate("start_date"), SqlConnection.result.getDate("end_date"))); //get each task (change this constructor you dont have it)
				}

				SqlConnection.ps.close();

			} // end for-loop: retrieved all allocated tasks for all employees
			SqlConnection.closeConnection();

		}catch(SQLException e){
			e.printStackTrace();
		}

		return employee_list;
	}



	/*METHOD FOR SELECTING ALL TASKS
	 * this method retrieves a list of all the tasks including it's details. 
	 * then the method checks the skills of each task from the TASK SKILL table
	 * 
	 * */

	public ArrayList<Task> selectAllTasks()  {

		ArrayList<Task> task_list = new ArrayList<Task>(); // list to hold tasks. query TASKSKILL table and store
		try{
			ArrayList<Skill> skill_list = new ArrayList<Skill>(); 
			skill_list.addAll(selectAllSkills()); // list of ALL skills is used to narrow down and find skills each task has

			SqlConnection.connect();
			SqlConnection.statement = SqlConnection.connection.createStatement();
			SqlConnection.statement.execute("SELECT * FROM TASK"); // select tasks
			SqlConnection.result = SqlConnection.statement.getResultSet();

			while(SqlConnection.result.next()) { // loop through each result and add to task_list

				task_list.add(new Task(SqlConnection.result.getString("task_id"), SqlConnection.result.getString("task_name"), 
						SqlConnection.result.getString("task_description"), SqlConnection.result.getInt("priority"), 
						SqlConnection.result.getDate("due_date"), null, null, SqlConnection.result.getInt("number_of_days"), 
						new ArrayList<Skill>(), null, SqlConnection.result.getInt("isallocated"), SqlConnection.result.getInt("unallocateable"), 0, false));

			} //finish retrieving all tasks


			/*get tasks"skills from TASKSKILL table
			 * Value the task*/
			for (int i = 0; i < task_list.size(); ++i) { // loop through each task

				SqlConnection.ps = SqlConnection.connection.prepareStatement("SELECT * FROM TASKSKILL WHERE task_id=?"); // select skills for each task
				SqlConnection.ps.setString(1, task_list.get(i).task_id);
				SqlConnection.ps.executeQuery();
				SqlConnection.result = SqlConnection.ps.getResultSet();

				while(SqlConnection.result.next()) { //add them one by one the representation in JAVA of adding skills

					for(int j = 0; j < skill_list.size(); ++j) { // search for the details of the skill from the skill_list

						if(skill_list.get(j).skill_id.equals(SqlConnection.result.getString("skill_id"))) { // if the skill is found

							task_list.get(i).required_skills.add(skill_list.get(j)); // add it to the task's list of required skills
							task_list.get(i).value += skill_list.get(j).value; // add nominal + market value of skill to task value
							break; // then exit the for-loop to find the details of the next skill
						}
					}
				}




				if(task_list.get(i).isallocated == 1) { //retrieve employee and start/end date details of each task IFF task is allocated

					SqlConnection.ps = SqlConnection.connection.prepareStatement("SELECT * FROM ALLOCATEDTASK WHERE task_id=?");
					SqlConnection.ps.setString(1, task_list.get(i).task_id);
					SqlConnection.ps.executeQuery();
					SqlConnection.result = SqlConnection.ps.getResultSet();
					SqlConnection.result.next();

					//query EMPLOYEETASK table
					task_list.get(i).start_date = SqlConnection.result.getDate("start_date");
					task_list.get(i).end_date = SqlConnection.result.getDate("end_date");

					String employee = SqlConnection.result.getString("emp_id");
					//to get more details than the ID of the employee assigned, assign to string and...
					//EMPLOYEE DETAILS
					SqlConnection.statement.execute("SELECT emp_id, FirstName, LastName, EmailAddress, Telephone, Mobile, " +
							"FullAddress, NationalInsuranceNumber FROM EMPLOYEE WHERE emp_id = '"+employee+"'");// search for employee with emp_id
					SqlConnection.result = SqlConnection.statement.getResultSet();
					SqlConnection.result.next();


					task_list.get(i).allocatedEmployee = new Employee(SqlConnection.result.getString("emp_id"),
							SqlConnection.result.getString("FirstName"), SqlConnection.result.getString("LastName"), 
							SqlConnection.result.getString("FullAddress"), SqlConnection.result.getString("EmailAddress"), 
							SqlConnection.result.getString("NationalInsuranceNumber"), 
							SqlConnection.result.getLong("Telephone"), SqlConnection.result.getLong("Mobile"), 
							new ArrayList<Skill>(), new ArrayList<Task>(), 0);
					//finish retrieving employee
					//retrieving employee's skills or other details when employee has been allocated to task

					SqlConnection.ps.close();
				}
			} //end for-loop: retrieve all attributes for allocated tasks
			SqlConnection.closeConnection();
		}catch(SQLException e){
			e.printStackTrace();
		}


		return task_list;
	} //end selectAllTasks







	/*METHOD TO SELECT ALL AVAILABLE EMPLOYEE AND PUT THEM IN A LIST*/
	public ArrayList<Employee> selectAvailableEmployees() {
		ArrayList<Employee> employee_list = new ArrayList<Employee>(); 
		employee_list.addAll(selectAllEmployees()); // get list of employees
		ArrayList<Employee> availEmployeeList = new ArrayList<Employee>();

		for(int i = 0; i < employee_list.size(); ++i){ // go through each employee to search if available or not

			boolean isAvailable = true; // assume true, until found otherwise

			for(int j = 0; j < employee_list.get(i).employee_tasks.size(); ++j){ // go through each employee's tasks / employees schedule

				/*this is where the program is 
				 * finding if the employee has any
				 * tasks that overlaps with the current date
				 * */
				java.util.Date startDate = employee_list.get(i).employee_tasks.get(j).start_date; //start date of task
				java.util.Date endDate = employee_list.get(i).employee_tasks.get(j).end_date; //end  date of task

				java.util.Date tomorrow = new java.util.Date();
				Calendar cal = Calendar.getInstance();
				cal.setTime(tomorrow);
				cal.add(Calendar.DAY_OF_MONTH, +1);
				tomorrow = cal.getTime();

				// check if startdate.compareTo(currentDate) < 2 -- task starts before tomorrow, or tomorrow
				// check if enddate.compareTo(currentDate) > 0 -- task ends after today
				// both must be true at the same time
				//"constraint in newtaskframe" although of course, start and end dates cannot be the same, since minimum task length is 1 day

				// ALL TASKS ARE ALLOCATED FOR THE NEXT DAY ONWARDS SO TASKS CANNOT START OR END TOMORROW ELSE UNAVAILABLE
				if(startDate.compareTo(tomorrow) <= 0 && endDate.compareTo(tomorrow) >= 0){
					isAvailable = false; // remove assumption that this employee is available
					break; // break out of inner loop, since unnecessary to check further
				}
			} // finish searching through tasks

			if(isAvailable == true){ // if employee is found to be available after searching all its tasks
				availEmployeeList.add(employee_list.get(i)); // copy the employee by using the current index
			}
		} // finish searching through employee_list

		return availEmployeeList;
	}


	/*METHOD TO SELECT ALL UNAVAILABLE EMPLOYEES AND PUT THEM IN A LIST
	 * these employees are either available but has a future task
	 * or these employees are currently unavailable - they are allocated with a task*/
	public ArrayList<Employee> selectUnavailableEmployees()  {

		ArrayList<Employee> employee_list = new ArrayList<Employee>(); 
		employee_list.addAll(selectAllEmployees()); // get list of employees
		ArrayList<Employee> unavailEmployeeList = new ArrayList<Employee>();

		for(int i = 0; i < employee_list.size(); ++i){ // go through each employee to search if available or not

			boolean isUnavailable = false; // assume employee is available (not unavailable), until found otherwise

			for(int j = 0; j < employee_list.get(i).employee_tasks.size(); ++j){ // go through each employee's tasks

				java.util.Date startDate = employee_list.get(i).employee_tasks.get(j).start_date;
				java.util.Date endDate = employee_list.get(i).employee_tasks.get(j).end_date;

				java.util.Date tomorrow = new java.util.Date();
				Calendar cal = Calendar.getInstance();
				cal.setTime(tomorrow);
				cal.add(Calendar.DAY_OF_MONTH, +1);
				tomorrow = cal.getTime();

				// check if startdate.compareTo(currentDate) < 2 -- task starts before tomorrow, or tomorrow
				// check if enddate.compareTo(currentDate) > 0 -- task ends after today
				// both must be true at the same time
				//"constraint in newtaskframe" although of course, start and end dates cannot be the same, since minimum task length is 1 day

				// ALL TASKS ARE ALLOCATED FOR THE NEXT DAY ONWARDS SO TASKS STARTING OR ENDING TOMORROW = UNAVAILABLE EMPLOYEE
				if(startDate.compareTo(tomorrow) <= 0 && endDate.compareTo(tomorrow) >= 0){
					// if proven unavailable

					isUnavailable = true; // remove assumption that this employee is available
					break; // break out of inner loop, since unnecessary to check further to prove unavailablity
				}
			} // finish searching through tasks

			if(isUnavailable == true){ // if employee is found to be unavailable after searching all its tasks
				unavailEmployeeList.add(employee_list.get(i)); // copy the employee by using the current index
			}
		} // finish searching through employee_list


		return unavailEmployeeList;
	}



	/*METHOD TO SELECT ALL AVAILABLE TASKS THAT NEEDS TO BE ALLOCATED (READY TO BE ALLOCATED)*/
	public ArrayList<Task> selectWaitingTasks() {
		ArrayList<Task> task_list = new ArrayList<Task>();
		task_list.addAll(selectAllTasks());

		ArrayList<Task> waitTaskList = new ArrayList<Task>();

		for(int i = 0; i < task_list.size(); ++i){ // search all tasks
			if(task_list.get(i).isallocated == 0 && // if task is NOT allocated AND latest start date after current date
					task_list.get(i).latest_possible_start.compareTo(new java.util.Date()) > 0 &&
					task_list.get(i).unallocateable == 0){  // and task is not unallocable
				waitTaskList.add(task_list.get(i)); // copy this index
			}
		} // finish searching for waiting tasks

		return waitTaskList;
	}


	/*METHOD TO SELECT ALL UNALLOCATEABLE TASKS THAT NEEDS TO BE CHECKED (TO BE MODIFIED)*/
	public ArrayList<Task> selectUnallocableTasks() {
		ArrayList<Task> task_list = new ArrayList<Task>();
		task_list.addAll(selectAllTasks());

		ArrayList<Task> unallocableList = new ArrayList<Task>();

		for(int i = 0; i < task_list.size(); ++i){ // search all tasks
			if(task_list.get(i).unallocateable == 1){ 
				// if this task is not allocated or the latest start date is after today, this is a waiting task!
				unallocableList.add(task_list.get(i)); // copy this index
			}
		} // finish searching for waiting tasks
		return unallocableList;
	}



	/*METHOD TO SELECT ALL TASKS THAT ARE CURRENTLY ALLOCATED AND ARE NOT COMPLETED YET*/
	public ArrayList<Task> selectAllocations() { 
		
		ArrayList<Task> task_list = new ArrayList<Task>();
		
		task_list.addAll(selectAllTasks());

		ArrayList<Task> allocTaskList = new ArrayList<Task>();

		for(int i = 0; i < task_list.size(); ++i){
			if(task_list.get(i).isallocated == 1 && // allocated AND
					task_list.get(i).end_date.compareTo(new java.util.Date()) >= 0){ // end is on or after current date
				// if this task is allocated+unfinished, add to new list!
				allocTaskList.add(task_list.get(i)); // copy this index
			} // ensures only tasks that are allocated and unfinished are in the allocTaskList
		}
		return allocTaskList;
	}



	/* this will be used by the valuing algorithm to prepare for Hungarian algorithm
	 * Knowing the sum value will help assigning a maximum cost to an incompatible employee-task combination.
	 * This will help prevent this employee from being selected by the algorithm for the task
	 */
	public int getImpossibleSum() { 

		int sum = 1; 
		/* impossible value for ordinary worker = sum(nominal values) + sum(demand for each skill) +1
		 * this is why sum = 1 ("+1" at the end)
		 * a worker may have all the skills and have the highest value, but +1 should be impossible
		 */
		ArrayList<Skill> allSkills = new ArrayList<Skill>(this.selectAllSkills());
		sum += allSkills.size(); // nominal values (1 point per skill) added to sum
		for(int i = 0; i < allSkills.size(); ++i){
			sum += allSkills.get(i).value; // all market values added to sum
		}
		return sum; // nominal+market values = max possible; impossible = max + 1
	}



	public void updateUnallocable(ArrayList<Task> t){

		try{
			SqlConnection.connect();
			for(int i = 0; i < t.size(); ++i){
				SqlConnection.ps = SqlConnection.connection.prepareStatement("UPDATE TASK SET UNALLOCATEABLE=? WHERE task_id=?");
				SqlConnection.ps.setInt(1, 1);
				SqlConnection.ps.setString(2, t.get(i).task_id);
				SqlConnection.ps.executeUpdate();
			}
			SqlConnection.closeConnection();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}

	//update allocated task table
	public void insertAllocations(ArrayList<Task> t){

		try{
			SqlConnection.connect();
			for(int i = 0; i < t.size(); ++i){
				SqlConnection.ps = SqlConnection.connection.prepareStatement("INSERT  INTO allocatedtask VALUES (?,?,?,?)");
				SqlConnection.ps.setString(1, t.get(i).task_id);
				SqlConnection.ps.setString(2, t.get(i).allocatedEmployee.emp_id);
				SqlConnection.ps.setDate(3, new java.sql.Date(t.get(i).start_date.getTime()));
				SqlConnection.ps.setDate(4, new java.sql.Date(t.get(i).end_date.getTime()));
				SqlConnection.ps.executeUpdate();
			}

			for(int i = 0; i < t.size(); ++i){
				SqlConnection.ps = SqlConnection.connection.prepareStatement("UPDATE TASK SET isallocated=? WHERE task_id=?");
				SqlConnection.ps.setInt(1, 1);
				SqlConnection.ps.setString(2, t.get(i).task_id);
				SqlConnection.ps.executeUpdate();
			}
			SqlConnection.ps.close();
			SqlConnection.closeConnection();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}


	public Employee selectEmployee(String employee_id) { // use by the ViewAllEmployee frame and editEmployeeFrame
		ArrayList<Employee> employee_list = new ArrayList<Employee>(); 
		employee_list.addAll(selectAllEmployees()); // get list of employees

		for(int i = 0; i < employee_list.size(); ++i) {
			if(employee_list.get(i).emp_id.equals(employee_id)) {
				return employee_list.get(i);
			}
		}
		return null; // this should never happen because the employee must existed if selected from ViewAllEmployees
	}


	public Task selectTask(String task_id) { // use by the ViewAllTask frame and editTaskFrame
		ArrayList<Task> task_list = new ArrayList<Task>(); 
		task_list.addAll(selectAllTasks()); // get list of tasks

		for(int i = 0; i < task_list.size(); ++i) {
			if(task_list.get(i).task_id.equals(task_id)) {
				return task_list.get(i);
			}
		}
		return null; // this should never happen because the task must existed if selected from ViewAllTask
	}
}
