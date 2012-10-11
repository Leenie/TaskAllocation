import java.text.SimpleDateFormat;
import java.util.*;




public class Allocation {


	public ArrayList<Task> confirmedAllocsList; // confirmed allocations

	public ArrayList<Task> waitTasksList;

	public ArrayList<Task> unconfirmedList;

	public ArrayList<Task> unallocableList;

	public ArrayList<Employee> availEmplList;

	public ArrayList<Employee> unavailEmplList;

	public ArrayList<Task> temp_task_process_list; // use this for ready tasks

	int impossibleValue; /* nominal + market values + 1 = max possible employee value + 1
	 * --- sqlskill.getImpossibleValue()
	 */

	Date cursor; // which date that the algorithm is scanning for unexpired unallocated tasks and un/available employees 

	int[][] matrix; // for hungarian algorithm

	Allocation(){

		confirmedAllocsList = new ArrayList<Task>(); // confirmed
		waitTasksList = new ArrayList<Task>();
		unconfirmedList = new ArrayList<Task>();
		unallocableList = new ArrayList<Task>();

		availEmplList = new ArrayList<Employee>();
		unavailEmplList = new ArrayList<Employee>();
	}

	public void initiate(){
		/* this method shall initialise the lists from the database
		 * public ArrayList<Task> curAllocsList; // current allocations, unfinished/unexpired tasks
		 * public ArrayList<Task> waitTasksList; // tasks waiting to be allocated that have not expired
		 * public ArrayList<Employee> availEmplList;  // available employees for the day (today)
		 * public ArrayList<Task> unconfirmedList;  // available employees for the day (today)
		 * public ArrayList<Task> unallocatedList; // unallocable tasks
		 */
		Sql sql = new Sql();
		this.waitTasksList = new ArrayList<Task>(sql.selectWaitingTasks()); // tasks waiting to be allocated
		Collections.sort(this.waitTasksList); //sorts it in chronological order by latest start date AND priority

		this.availEmplList = new ArrayList<Employee>(sql.selectAvailableEmployees()); // employees available tomorrow
		// do not check employees available today because manager might do allocation after business hours, so its better to 
		// see who is available the next day (tomorrow)

		/* employees that are available tomorrow are chosen
		 * 
		 * STILL IT IS IMPORTANT TO CHECK THAT WHEN ALLOCATING A TASK WITH A LENGTH OF X DAYS,
		 * EMPLOYEES MAY STILL HAVE A TASK THAT THEY HAVE BEEN ALLOCATED TO IN THE FUTURE
		 * 
		 * ******* I.E. TASKS MUST NOT OVERLAP ********** use method: availabilityForTask(Employee e, Task t)
		 * 
		 * THIS THEORETICAL FUTURE TASK WOULD HAVE BEEN ALLOCATED IN A PREVIOUS BATCH RUN
		 * 
		 * THEREFORE THE PROGRAM MUST CHECK THE NEW TASK LENGTH AND CHECK THAT THE END DATE WOULD NOT BE IN CONFLICT
		 * WITH THE EXISTING ALLOCATED TASK'S START DATE
		 * 
		 * THIS IS ANOTHER CONSTRAINT OF THIS ALLOCATION SYSTEM...
		 */
		this.unavailEmplList = new ArrayList<Employee>(sql.selectUnavailableEmployees()); // select employees unavailable tomorrow
		/*
		 * It is important to check when an employee becomes unavailable
		 */
		this.unconfirmedList = new ArrayList<Task>(); 
		// at the end of algorithm, all suggested allocations will move here, subject to confirmation by manager

		this.unallocableList = new ArrayList<Task>(sql.selectUnallocableTasks());
		// not all tasks will necessarily have the resources to be allocated; these tasks will move here because they are unallocable

		this.impossibleValue = new Sql().getImpossibleSum(); // max possible value for employee + 1
		// this value is used to note that we do not want a certain employee-task assignment

	}



	public void createHungarianSchedule(){ // create schedule


		System.out.println();
		System.out.println();
		System.out.println("CREATE_HUNGARIAN_SCHEDULE() METHOD STARTS HERE!!");
		System.out.println();
		System.out.println();



		cursor = new Date(); // points at the current date that the algorithm is working on

		/*
		 * I have a while loop that depends on all the tasks being assigned.
		 * The algorithm goes through each day in the future and plans ahead which employees to allocate to waiting tasks.
		 * It increments the date at the start before every assignment and keeps going until:
		 * a) all possible allocations have been processed and made
		 * b) or all impossible allocations (dependent on employee/resource availability) have been found
		 * 
		 * i.e. It makes all allocations possible on Monday with the employees (resources) available
		 * On Tuesday, it finds what resources are available and tries to allocate all possible tasks.
		 * This repeats until all tasks have been allocated or been found impossible to allocate
		 * 
		 */
		int day = 0;
		while(this.waitTasksList.size() > 0){ // while there are tasks that are available and are waiting, keep looping through creating schedule

			incrementDate(); // the cursor date will always be the day when the allocation is scheduled from
			// scheduling algorithm must keep going through each day in the future until all tasks have been finished

			System.out.println("Allocating tasks for day #"+ ++day + ", date starting: "+ new SimpleDateFormat("dd/MM/yyyy").format(cursor).toString() );
			// select all employees availEmplList - FOR EVERYDAY USE ***ALL*** EMPLOYEES THAT YOU CAN: I.E. USE AVAILEMPLLIST
			// select tasks:
			this.temp_task_process_list = this.selectWaitingTasks(); //create list to store temporary batch of allocations to be confirmed
			//temporary list is used because not all available tasks can be allocated, therefore tasks that cannot be allocated must continue to wait
			// while tasks that can will be moved into the temporary list for processing; e.g. 5 employees, 10 tasks -> 5 tasks must wait


			System.out.println();
			System.out.println("HUNGARIAN ALGORITHM STARTS HERE");
			System.out.println();

			//create matrix
			//this is going to be used to calculate cost of combinations of tasks and employees
			// will be used by the hungarian algorithm
			int matrix_size = availEmplList.size(); // number of tasks will equal number of available employees

			matrix = new int[matrix_size][matrix_size]; // initialise size of matrix


			// same number of tasks as employees, therefore size of availemplList is fine

			/*VALUING EACH EMPLOYEE ACCORDING TO THEIR SKILLS*/ 
			// the way matrix will be used is like so: matrix[i][j] = matrix[task][employee]


			for(int i = 0; i < matrix.length; ++i){ // looping through tasks (task = i)
				for(int j = 0; j < matrix[i].length; ++j){ // looping through the task's possible employees (employee = j)

					/*check task if its not a dummy AND
					 *  check if the employee has the required skills for the task AND 
					 *  if employee is available to do task (i.e. has NO existing allocations that might clash); 
					 *  
					 *  i.e. if you have a task that finishes 7 days later, you cannot be allocated
					 *  another task today
					 */

					if(hasSkills(this.availEmplList.get(j), this.temp_task_process_list.get(i))
							&& availabilityForTask(this.availEmplList.get(j), this.temp_task_process_list.get(i))
							&& this.temp_task_process_list.get(i).isDummy == false){ 


						matrix[i][j] = (int) (this.availEmplList.get(j).value - this.temp_task_process_list.get(i).value);


						// cost of allocating employee to task = emp_value - task_value

					} else {
						matrix[i][j] = this.impossibleValue; // undesirable cost of allocation

					}

					//display that employee is valued against task at a position in the matrix
					System.out.println(this.availEmplList.get(j).emp_id+" "+this.availEmplList.get(j).firstname+" "+this.availEmplList.get(j).lastname
							+" is valued against "+this.temp_task_process_list.get(i).task_id+" "+this.temp_task_process_list.get(i).task_name +" at "+matrix[i][j]+" points");
				}

			}

			// once all cost values have been entered into matrix, process the matrix with the Hungarian algorithm

			int[][] assignment = new HungarianScheduling(matrix).assignment_array;


			System.out.println();
			System.out.println("HUNGARIAN ALGORITHM ENDS HERE");
			System.out.println();


			/* remove tasks that have been assigned, and place in unconfirmed list with allocated employee, 
			 * IF they are not dummy tasks
			 * AND IF they are not undesirable values (impossibleValue)
			 */
			//ALLOCATION STARTS
			for (int i = 0; i < assignment.length; ++i) { // loop through array of assignments

				//if the task is not a dummy task (created to make a square matrix) 
				// and if it is a desirable assignment (not an impossible value)
				if(temp_task_process_list.get(i).isDummy == false 
						&& matrix[assignment[i][0]][assignment[i][1]] != this.impossibleValue) {
					/* assignment[i][0] is the task position number in array called MATRIX (value matrix)
					 * assignment[i][1] is the employee position number in the MATRIX
					 * therefore assignment[i][j] describes that Task i should be allocated to Employee j
					 *
					 *allocate the employee, if the matrix [i][j] is not equals to the impossible value
					 *
					 * if you want to allocate, then the allocation must have: task id, start date, end date and employee id (for the database)
					 * set the task allocation details: start date = cursor, end date = cursor+duration-1, employee
					 * 
					 * assignment[i][1] is the employee's position number in the MATRIX and in the available employee list
					 * assignment[i][0] is the task position in the list temp_task_process_list
					 * tasks in temp_task_process_list are in the same position in MATRIX
					 */
					temp_task_process_list.get(assignment[i][0]).start_date = cursor; //assign start date
					temp_task_process_list.get(assignment[i][0]).end_date = incrementDate(temp_task_process_list.get(assignment[i][0]).numberofDays); //assign end date
					// end date = start date + number of days - 1

					temp_task_process_list.get(assignment[i][0]).isallocated = 1;

					// assign the employee to the task
					temp_task_process_list.get(assignment[i][0]).allocatedEmployee = this.availEmplList.get(assignment[i][1]);
					// assign the task to the employee
					this.availEmplList.get(assignment[i][1]).employee_tasks.add(temp_task_process_list.get(assignment[i][0]));

					// add this task to the list of unconfirmed allocations (subject to manager approval)
					this.unconfirmedList.add(temp_task_process_list.get(assignment[i][0])); // moved to unconfirmed list
				}
			}

			// remove all these ABOVE allocated tasks and remove ALL the DUMMY tasks from the temp_task_process_list
			ArrayList<Task> temp_task_listB = new ArrayList<Task>(); // create ANOTHER list
			for(int i = 0; i < temp_task_process_list.size(); ++i){
				// add only the tasks that have isallocated = 0 AND  isDummy = false -- i.e. not allocated and not dummy tasks
				if(temp_task_process_list.get(i).isallocated == 0 && temp_task_process_list.get(i).isDummy == false){
					temp_task_listB.add(temp_task_process_list.get(i)); // add ALL the tasks we NEED to the OTHER list
				}
			}
			temp_task_process_list = temp_task_listB; // get rid of the tasks we DONT want by making the OTHER list the MAIN list
			/*
			 *  
			 * IF the currentDate is not the latest start date of the task,
			 * place tasks that have not been assigned into waitingTask
			 * e.g. if task is due in 2 years later, it can still be allocated in theory, EVEN IF not today
			 * 
			 * ELSE move it to unallocateable list (e.g. if the task was due in tomorrow and had to be done today)
			 */
			for(int i = 0; i < temp_task_process_list.size(); ++i){

				if(temp_task_process_list.get(i).latest_possible_start.compareTo(cursor) > 0){ // if still can be done
					this.waitTasksList.add(temp_task_process_list.get(i));  // move back to waitingTasks list
				} else {
					temp_task_process_list.get(i).unallocateable = 1; // NEEDS TO BE UPDATED IN DATABASE
					this.unallocableList.add(temp_task_process_list.get(i)); //add to the task that cant be allocated unallocateable list
				}

			}

			findAvailableEmployees(); // find all available employees and move them between lists; 
			// e.g. if cursor = Monday, find what employees are available on Monday
			findUnavailableEmployees(); // find which employees have become unavailable and move them between lists


			// GOES BACK FULL CIRCLE
			// NEW TASKS SELECTED UNLESS ALL WAITING TASKS ARE ALLOCATED OR FOUND TO BE IMPOSSIBLE TO ALLOCATE
		}

	}




	public void createGreedySchedule(){ // create schedule

		System.out.println("CREATE_GREEDY_SCHEDULE() METHOD STARTS HERE!!");


		cursor = new Date(); // points at the current date that the algorithm is working on
		int day = 0;
		while(this.waitTasksList.size() > 0){ // while there are tasks that are available and are waiting, keep looping through creating schedule
			incrementDate(); // the cursor date will always be the day when the allocation is scheduled from
			// scheduling algorithm must keep going through each day in the future until all tasks have been finished
			System.out.println("Allocating tasks for day #"+ ++day + ", date starting: "+ new SimpleDateFormat("dd/MM/yyyy").format(cursor).toString() );

			// GREEDY ALLOCATION STARTS HERE
			for(int i = 0; i < this.waitTasksList.size(); ++i){ // looping through tasks (task = i)

				for(int j = 0; j < this.availEmplList.size(); ++j){ // looping through the task's possible employees (employee = j)

					/*
					 * 	
					 *  if employee has skills for the task and is available for the task
					 * 					 */
					if(hasSkills(this.availEmplList.get(j), this.waitTasksList.get(i))
							&& availabilityForTask(this.availEmplList.get(j), this.waitTasksList.get(i))){

						waitTasksList.get(i).start_date = cursor;
						waitTasksList.get(i).end_date = incrementDate(waitTasksList.get(i).numberofDays);
						// end date = start date + number of days - 1

						waitTasksList.get(i).isallocated = 1;

						// assign the employee to the task
						waitTasksList.get(i).allocatedEmployee = this.availEmplList.get(j);

						// assign the task to the employee
						this.availEmplList.get(j).employee_tasks.add(waitTasksList.get(i));

						// add this task to the list of unconfirmed allocations (subject to manager approval)
						this.unconfirmedList.add(waitTasksList.get(i)); // moved to unconfirmed list

						this.unavailEmplList.add(this.availEmplList.remove(j)); // remove employee from available list - else this will give error

						break; // if allocated, stop searching for employees for current task and move on to next task

					}
				}
			}

			// remove all these ABOVE allocated tasks from the waitTasksList
			for(int i = 0; i < this.unconfirmedList.size(); ++i){
				this.waitTasksList.remove(this.unconfirmedList.get(i)); 
			}
			// get rid of the tasks we DONT need (have been allocated and copied already to elsewhere)
			/*check for expired tasks:
			 *  
			 * IF the currentDate is not the latest start date of the task,
			 * keep tasks that have not been assigned in waitingTask
			 * e.g. if task is due in 2 years later, it can still be allocated in theory, EVEN IF not today
			 * 
			 * ELSE move it to unallocateable list (e.g. if the task was due in tomorrow and had to be done today)
			 */


			for(int i = 0; i < waitTasksList.size(); ++i){

				if(! (waitTasksList.get(i).latest_possible_start.compareTo(cursor) > 0)){ // if  cant be done
					waitTasksList.get(i).unallocateable = 1; // NEEDS TO BE UPDATED IN DATABASE
					this.unallocableList.add(waitTasksList.get(i));
				}
			}
			for(int i = 0; i < this.unallocableList.size(); ++i){
				this.waitTasksList.remove(this.unallocableList.get(i)); 
			}


			findAvailableEmployees(); // find all available employees and move them between lists; 
			// e.g. if cursor = Monday, find what employees are available on Monday

			// GOES BACK FULL CIRCLE
			// NEW TASKS SELECTED UNLESS ALL WAITING TASKS ARE ALLOCATED OR FOUND TO BE IMPOSSIBLE TO ALLOCATE
		}
	}



	public void incrementDate(){

		//System.out.println(new SimpleDateFormat("dd/MM/yyyy").format(cursor)); //print current date

		// increment cursor date + 1 day
		Calendar cal = Calendar.getInstance();
		cal.setTime(this.cursor);
		cal.add(Calendar.DAY_OF_MONTH, +1);
		this.cursor = cal.getTime();

		//System.out.println(new SimpleDateFormat("dd/MM/yyyy").format(cursor)); // print incremented date
	}


	public Date incrementDate(int duration){
		// increment cursor date + duration -1 day
		Calendar cal = Calendar.getInstance();
		cal.setTime(this.cursor);
		cal.add(Calendar.DAY_OF_MONTH, duration-1);
		return cal.getTime();

	}



	public ArrayList<Task> selectWaitingTasks(){
		/*
		 * select tasks for processing / allocating
		 */
		ArrayList<Task> readyTasksList = new ArrayList<Task>();
		// create to hold all tasks that can be allocated


		if(this.availEmplList.size() == this.waitTasksList.size()){ // if both lists equal
			for(int i = 0; i < this.waitTasksList.size(); ++i){

				readyTasksList.add(this.waitTasksList.get(i)); // copy all the tasks to ready list
			}
			for(int i = 0; i < readyTasksList.size(); ++i){

				this.waitTasksList.remove(readyTasksList.get(i)); // remove from original list all copied tasks
			}
		} else 

			if(this.availEmplList.size() < this.waitTasksList.size()){ // if more tasks than employees
				// if there are more tasks than employees
				for(int i = 0; i < this.availEmplList.size(); ++i){

					readyTasksList.add(this.waitTasksList.get(i)); // select only same number of tasks as employees and add to ready list
				}

				for(int i = 0; i < readyTasksList.size(); ++i){

					this.waitTasksList.remove(readyTasksList.get(i)); // remove from original list all copied tasks
				}

			}
			else { // if there are less tasks than employees, create dummy tasks in the ready 
				/*
				 * e.g. 5 employees, 3 tasks: need 2 dummy tasks to make square matrix for hungarian
				 * then move all tasks from waiting list to ready list
				 * then create 2 dummy tasks in the ready list
				 */

				int number_of_dummy_tasks_needed = this.availEmplList.size() - this.waitTasksList.size();


				for(int i = 0; i < this.waitTasksList.size(); ++i){
					readyTasksList.add(this.waitTasksList.get(i)); // copy all the tasks to ready list
				}

				for(int i = 0; i < readyTasksList.size(); ++i){
					this.waitTasksList.remove(readyTasksList.get(i)); // remove from original list all copied tasks
				}


				for(int i = 1; i <= number_of_dummy_tasks_needed; ++i){
					readyTasksList.add(new Task("Dummy_id"+(i+1), "Dummy_name"+(i+1), "This is a dummy task", 0, 
							0, this.impossibleValue, cursor, 0, true)); //add the dummy tasks to the readytask list
				}
			}

		return readyTasksList;
	}


	public void findAvailableEmployees(){
		// find employees in the unavailable employee list that are available tomorrow (cursor date + 1)
		// and move them to the available employee list


		for(int i = 0; i < this.unavailEmplList.size(); ++i){ // go through each employee to search if available or not

			boolean isAvailable = true; // assume true, until found otherwise

			for(int j = 0; j < this.unavailEmplList.get(i).employee_tasks.size(); ++j){ // go through each employee's tasks / employees schedule

				java.util.Date startDate = this.unavailEmplList.get(i).employee_tasks.get(j).start_date;
				java.util.Date endDate = this.unavailEmplList.get(i).employee_tasks.get(j).end_date;
				// currentdate = cursor

				// check if startdate.compareTo(currentDate) < 2 -- task starts before tomorrow, or tomorrow
				// check if enddate.compareTo(currentDate) > 0 -- task ends after today
				// both must be true at the same time
				//"constraint in newtaskframe" although of course, start and end dates cannot be the same, since minimum task length is 1 day


				// ALL TASKS ARE ALLOCATED FROM TODAY ONWARDS SO TASKS CANNOT START OR END TODAY ELSE UNAVAILABLE
				if(startDate.compareTo(cursor) <=0 && endDate.compareTo(cursor) >= 0){ // if they have a task that clashes with today!

					isAvailable = false; // remove assumption that this employee is available
					break; // break out of inner loop, since unnecessary to check further
				}
			} // finish searching through tasks

			if(isAvailable == true){ // if employee is found to be available after searching all its tasks
				this.availEmplList.add(this.unavailEmplList.get(i)); // copy the employee by using the current index
			}
		} // finish searching through employee_list

		for(int i = 0; i < this.availEmplList.size(); ++i){
			this.unavailEmplList.remove(this.availEmplList.get(i)); // remove all available employees from unavailable list
		}
	}


	public void findUnavailableEmployees()  { //checks if they are scheduled any task

		for(int i = 0; i < this.availEmplList.size(); ++i){ // go through each employee to search if available or not

			boolean isUnavailable = false; // assume employee is available (not unavailable), until found otherwise

			for(int j = 0; j < this.availEmplList.get(i).employee_tasks.size(); ++j){ // go through each employee's tasks

				java.util.Date startDate = this.availEmplList.get(i).employee_tasks.get(j).start_date;
				java.util.Date endDate = this.availEmplList.get(i).employee_tasks.get(j).end_date;

				// check if startdate.compareTo(currentDate) <= 0 -- task starts today or before today
				// check if enddate.compareTo(currentDate) >= 0 -- task ends today or after today
				// both must be true at the same time
				//"constraint in newtaskframe" although of course, start and end dates cannot be the same, since minimum task length is 1 day

				// ALL TASKS ARE ALLOCATED FOR THE NEXT DAY ONWARDS SO TASKS STARTING OR ENDING TOMORROW = UNAVAILABLE EMPLOYEE
				if(startDate.compareTo(cursor) <= 0 && endDate.compareTo(cursor) >= 0){ // if the task clashes with today
					// if proven unavailable

					isUnavailable = true; // remove assumption that this employee is available
					break; // break out of inner loop, since unnecessary to check further to prove unavailablity

				}

			} // finish searching through tasks

			if(isUnavailable == true){ // if employee is found to be unavailable after searching all its tasks
				this.unavailEmplList.add(this.availEmplList.get(i)); // copy the employee by using the current index
			}

		} // finish searching through employee_list


		for(int i = 0; i < this.unavailEmplList.size(); ++i){
			this.availEmplList.remove(this.unavailEmplList.get(i)); // remove all unavailable employees from available list
		}

	}


	public boolean availabilityForTask(Employee e, Task t){

		/* for employee to be available to do the task, the employee must have no tasks that:
		 * 
		 * a)  end on the date that the task T is going to be started on
		 * b) start on the date within the expected end date of the task T
		 * 
		 * expected_end_date = cursor + T.duration - 1
		 * 
		 * i.e. Task T must not overlap with employee's existing allocated tasks
		 * 
		 */
		Calendar cal = Calendar.getInstance();
		cal.setTime(this.cursor);
		cal.add(Calendar.DAY_OF_MONTH, t.numberofDays-1);
		Date expected_end_date = cal.getTime();

		for(int i = 0; i < e.employee_tasks.size(); ++i){

			/*
			 * if e.employee_tasks.get(i).end_date.compareTo(cursor) >= 0, then do not allocate (set impossible value)
			 * 
			 * if e.employee_tasks.get(i).start_date.compareTo(expected_end_date) <= 0, set impossible value
			 * 
			 */


			if(e.employee_tasks.get(i).end_date.compareTo(cursor) >= 0 
					&& e.employee_tasks.get(i).start_date.compareTo(expected_end_date) <= 0){ //check employee is not available

				return false; // if employee has a task that overlaps, return false (meaning employee cannot do this)
			}

		}
		return true;
	}


	public boolean hasSkills(Employee e, Task t){
		ArrayList<String> employeeskills = new ArrayList<String>();
		for(int i = 0; i < e.employee_skills.size(); ++i){
			employeeskills.add(e.employee_skills.get(i).skill_id);
		}

		ArrayList<String> taskskills = new ArrayList<String>();
		for(int i = 0; i < t.required_skills.size(); ++i){
			taskskills.add(t.required_skills.get(i).skill_id);
		}

		for(int i = 0; i < taskskills.size(); ++i){
			if(employeeskills.indexOf(taskskills.get(i)) == -1){ // if a taskskill is not found among employeeskills
				return false; // return false
			}
		}

		return true; // if not returned false in loop, then every taskskill does exist among employeeskills

	}
}
