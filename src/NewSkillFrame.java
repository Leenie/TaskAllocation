/*This is to create new skill information
 * this will be added on to the database
 * 
 */
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class NewSkillFrame extends JDialog implements ActionListener {

	
	private static final long serialVersionUID = -8360506284585122220L;
	private JLabel skill_name;
	private JLabel skill_desc;
	private JTextField skillname_field;
	private JTextField skilldesc_field;
	private String skillname_input;
	private String skilldesc_input;
	
	
	private JButton clear;
	private JButton submit;

	Object[] skill;

	public NewSkillFrame() {
		this.setModal(true);
		this.setTitle("Create Skill");
		this.setSize(380,120);
		this.setLayout(new BorderLayout());


		skill_name = new JLabel("Name:");
		skill_desc = new JLabel("Description:");


		skillname_field = new JTextField();
		skilldesc_field = new JTextField();


		clear = new JButton("Clear");
		submit = new JButton("Submit");

		clear.addActionListener(this);
		submit.addActionListener(this);


		JPanel labeltextpanel = new JPanel();
		labeltextpanel.setLayout(new GridLayout(2,1));


		labeltextpanel.add(skill_name);
		labeltextpanel.add(skillname_field);
		labeltextpanel.add(skill_desc);
		labeltextpanel.add(skilldesc_field);



		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(labeltextpanel, BorderLayout.CENTER);

		JPanel buttonpanel = new JPanel();
		buttonpanel.setLayout(new FlowLayout());
		buttonpanel.add(clear);
		buttonpanel.add(submit);

		add(mainPanel, BorderLayout.CENTER);
		add(buttonpanel, BorderLayout.SOUTH);

		this.setVisible(true);


	}

	public void actionPerformed(ActionEvent event) {

		if(event.getSource() == clear) {


			skillname_field.setText("");
			skilldesc_field.setText("");

		}

		if (event.getSource() == submit) {

			try {
				SqlConnection.connect();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			skillname_input = skillname_field.getText();
			skilldesc_input = skilldesc_field.getText();

			skill = new Object[2];
			skill[0] = skillname_input;
			skill[1] = skilldesc_input;
			//
			if(skillname_input.isEmpty()) {
				JOptionPane.showMessageDialog(this, "skill id cannot be empty. Please enter a skill name/id.");
			} else 
				if(skillname_input.length() > 20) {
					JOptionPane.showMessageDialog(this, "The skill name/id has to be less than or equals to 20 characters.");
				}
			else {

				try {

					SqlConnection.statement = SqlConnection.connection.createStatement();

					SqlConnection.statement.executeUpdate("INSERT INTO SKILL VALUES ('"+
							skillname_input+"', '"+
							skilldesc_input+"')");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				SqlConnection.closeConnection();// close the conneciton		
				this.setVisible(false);

			}
		}

	}
	public static void main(String[] args) {
		NewSkillFrame newSkill = new NewSkillFrame();

	}

}