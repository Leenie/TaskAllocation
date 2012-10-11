import java.sql.SQLException;
import java.util.*;

class GenerateVersion2{
	public static void main(String[] args){

		for(int i = 0; i < 0; ++i){ // 
			Employee e = generateEmployee();
			insertEmployee(e);
			System.out.println("#"+i+ " created and inserted employee with id "+e.emp_id);
		}

		System.out.println();
		System.out.println("successfully generated and inserted all employees");
		System.out.println();


		employees = new Sql().selectAllEmployees(); // get all employees to check if tasks that are generated have skills

		for(int i = 0; i < 25; ++i){ // generate 50 tasks in total
			Task t = generateTask();
			insertTask(t);
			System.out.println("#"+i+" created and inserted task with id "+t.task_id);

		}

		System.out.println(); 
		System.out.println("successfully generated and inserted all tasks");
		System.out.println();





	}

	static Object[] number_possibilities = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
	static Object[] alphabet_possibilities = {"", 'A', 'B', 'C', 'D', 'E', 'F', 
		'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 
		'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	static Object[] alphanumerical_possibilities = {1, 2, 3, 4, 5, 6, 7, 8, 
		9, 0, 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 
		'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 
		'X', 'Y', 'Z'};
	static Object[] alphanumerical_withspaces = {"", 1, 2, 3, 4, 5, 6, 7, 8, 
		9, 0, 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 
		'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 
		'X', 'Y', 'Z'};

	/* String-Generate methods */
	public static String generateNumerical(int length){
		StringBuffer x = new StringBuffer();
		for(int i = 0; i < length; ++i){
			x.append(number_possibilities[new java.util.Random().nextInt(number_possibilities.length)]);
		}
		return x.toString();
	}

	public static String generateAlphabetical(int length){
		StringBuffer x = new StringBuffer();
		for(int i = 0; i < length; ++i){
			x.append(alphabet_possibilities[new java.util.Random().nextInt(alphabet_possibilities.length)]);
		}
		return x.toString();
	}



	public static String generateAlphaNumerical(int length){
		StringBuffer x = new StringBuffer();
		for(int i = 0; i < length; ++i){
			x.append(alphanumerical_possibilities[new java.util.Random().nextInt(alphanumerical_possibilities.length)]);
		}
		return x.toString();
	}

	public static String generateAlphaNumerical_withspaces(int length){
		StringBuffer x = new StringBuffer();
		for(int i = 0; i < length; ++i){
			x.append(alphanumerical_withspaces[new java.util.Random().nextInt(alphanumerical_withspaces.length)]);
		}
		return x.toString();
	}


	/* generate task */
	public static Task generateTask(){
		StringBuffer idfield = new StringBuffer("t");
		idfield.append(generateNumerical(6)); // id

		StringBuffer taskname = new StringBuffer();
		taskname.append(new java.util.Random().nextInt(50));

		StringBuffer taskdesc = new StringBuffer();
		taskdesc.append(new java.util.Random().nextInt(50));

		int priority = new java.util.Random().nextInt(2);

		java.util.Date date = new java.util.Date();
		Calendar cdr = Calendar.getInstance();
		cdr.setTime(date);
		long val1=cdr.getTimeInMillis();

		cdr.add(Calendar.DAY_OF_MONTH, 365);
		long val2=cdr.getTimeInMillis();

		java.util.Random r = new java.util.Random();
		long randomTS = (long) (r.nextDouble() * (val2-val1)) + val1;
		java.util.Date latest_start_date = new java.util.Date(randomTS);

		int length = new java.util.Random().nextInt(99);

		cdr.setTime(latest_start_date);
		cdr.add(Calendar.DAY_OF_MONTH, length);
		long dueby = cdr.getTimeInMillis();
		java.util.Date due_by_date = new java.util.Date(dueby);


		// select  1-3 skills at random..
		ArrayList<Skill> skills = new ArrayList<Skill>(new Sql().selectAllSkills());
		ArrayList<Skill> selectedSkills = new ArrayList<Skill>();
		int numberOfSkillsToAdd = 0;
		while(numberOfSkillsToAdd == 0){
			numberOfSkillsToAdd = new java.util.Random().nextInt(skills.size()); // chooses random number of skills
			// but 0 is not allowed by while-loop
		}

		
		
			while(selectedSkills.size() < numberOfSkillsToAdd){

				int number = new java.util.Random().nextInt(skills.size());

				if(selectedSkills.indexOf(skills.get(number)) == -1){ // if the index is -1, i.e. not selected, then add
					selectedSkills.add(skills.get(number));
				}
				

			}
			
		System.out.print(idfield + " has skills: ");

		for(int i = 0; i < selectedSkills.size(); ++i){
			System.out.print(selectedSkills.get(i).skill_id);
		}
		System.out.println();
		return new Task(idfield.toString(), taskname.toString(), taskdesc.toString(), 
				priority, due_by_date, length, selectedSkills); // if employees have the required skills, confirm this task 


	}



	public static Employee generateEmployee(){
		StringBuffer id = new StringBuffer("k");
		id.append(generateNumerical(6));

		StringBuffer nationalInsurance = new StringBuffer();
		nationalInsurance.append(generateAlphaNumerical(9));

		StringBuffer firstname = new StringBuffer();
		firstname.append(generateAlphaNumerical_withspaces(15));

		StringBuffer surname = new StringBuffer();
		surname.append(generateAlphaNumerical_withspaces(15));

		StringBuffer email = new StringBuffer();
		email.append(generateAlphaNumerical_withspaces(24));
		email.append('@');
		email.append(generateAlphaNumerical_withspaces(24));

		StringBuffer telString = new StringBuffer();
		telString.append(generateNumerical(11));
		long tel = new Long(telString.toString()).longValue();

		StringBuffer mobString = new StringBuffer();
		mobString.append(generateNumerical(11));
		long mob = new Long(mobString.toString()).longValue();

		StringBuffer address = new StringBuffer();
		address.append(generateAlphabetical(50));

		// select skills randomly, a random number of times
		ArrayList<Skill> skills = new ArrayList<Skill>(new Sql().selectAllSkills());
		ArrayList<Skill> selectedSkills = new ArrayList<Skill>();
		int numberOfSkillsToAdd = new java.util.Random().nextInt(skills.size());

		while(numberOfSkillsToAdd == 0){
			numberOfSkillsToAdd = new java.util.Random().nextInt(skills.size());
		}

		while(selectedSkills.size() < numberOfSkillsToAdd){
			int number = new java.util.Random().nextInt(skills.size());

			if(selectedSkills.indexOf(skills.get(number)) == -1){ // if the index is -1, i.e. not selected, then add
				selectedSkills.add(skills.get(number));
			}


		}
		System.out.print(id + " has skills: ");

		for(int i = 0; i < selectedSkills.size(); ++i){
			System.out.print(selectedSkills.get(i).skill_id);
		}
		System.out.println();

		return new Employee(id.toString(), firstname.toString(), surname.toString(), 
				address.toString(), email.toString(), nationalInsurance.toString(), tel,
				mob, selectedSkills);

	}

	static ArrayList<Employee> employees;

	

	public static void insertTask(Task t){
		try {
			SqlConnection.connect();


			SqlConnection.ps = SqlConnection.connection.prepareStatement("INSERT  INTO TASK VALUES (?,?,?,?,?,?,?,?)");

			SqlConnection.ps.setString(1, t.task_id);
			SqlConnection.ps.setString(2, t.task_name);
			SqlConnection.ps.setString(3, t.task_description);
			SqlConnection.ps.setInt(4, t.priority_value);

			long date = t.due_date.getTime();
			SqlConnection.ps.setDate(5, new java.sql.Date(date));
			SqlConnection.ps.setInt(6, t.numberofDays);
			SqlConnection.ps.setInt(7, 0);
			SqlConnection.ps.setInt(8, 0);

			SqlConnection.ps.executeUpdate();

			//now enter skills into TASK SKILL database
			for(int i = 0; i < t.required_skills.size(); i++) {
				SqlConnection.ps = SqlConnection.connection.prepareStatement("INSERT INTO TASKSKILL VALUES (?,?)");
				SqlConnection.ps.setString(1, t.task_id);
				SqlConnection.ps.setString(2, t.required_skills.get(i).skill_id);
				SqlConnection.ps.executeUpdate();
			}
			SqlConnection.ps.close();
			SqlConnection.closeConnection();
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void insertEmployee(Employee e){
		try {
			SqlConnection.connect();


			SqlConnection.ps = SqlConnection.connection.prepareStatement("INSERT INTO EMPLOYEE VALUES (?,?,?,?,?,?,?,?)");

			SqlConnection.ps.setString(1, e.emp_id);
			SqlConnection.ps.setString(2, e.firstname);
			SqlConnection.ps.setString(3, e.lastname);
			SqlConnection.ps.setString(4, e.email_address);
			SqlConnection.ps.setLong(5, e.telephone);
			SqlConnection.ps.setLong(6, e.mobile);
			SqlConnection.ps.setString(7, e.address);
			SqlConnection.ps.setString(8, e.ni_num);

			SqlConnection.ps.executeUpdate();

			// i added the employee details into employee table

			// now i have to add employee_skills into employee_skills table
			// loop through checklist
			for(int i = 0; i < e.employee_skills.size(); i++) {
				SqlConnection.ps = SqlConnection.connection.prepareStatement("INSERT INTO EMPLOYEESKILL VALUES (?,?)");
				SqlConnection.ps.setString(1, e.employee_skills.get(i).skill_id);
				SqlConnection.ps.setString(2, e.emp_id);
				SqlConnection.ps.executeUpdate();

			}
			SqlConnection.ps.close();

			SqlConnection.closeConnection();//close the connection

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}



}
