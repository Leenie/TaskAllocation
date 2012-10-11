
/*a class to hold the objects of skills
 * it contains constructors that will be used 
 * to manipulate the database that contains skills
 * */

public class Skill {

	/**
	 * @uml.property  name="skill_id"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.util.ArrayList"
	 */
	String skill_id; //skill id
	/**
	 * @uml.property  name="skill_description"
	 */
	String skill_description; // skill name

	/**
	 * @uml.property  name="value"
	 */
	int value; // to hold the value of the skill

	//constructor
	public Skill(String skill_id, String skill_description, int value) {

		this.skill_id = skill_id;
		this.skill_description = skill_description;
		this.value = value;
	}
	//constructor - to assign skills to employees and tasks
	public Skill(String skill_id) { 
		this.skill_id = skill_id;
		this.value = 0;

	}
	
	//constructor
	public Skill(String skill_id, String skill_description) {
		this.skill_id = skill_id;
		this.skill_description = skill_description;
		this.value = 0;
	}
}


