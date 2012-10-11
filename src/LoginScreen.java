import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/*
 * LOGIN FRAME
 * */

public class LoginScreen extends JFrame implements ActionListener {

	JButton login;
	JButton cancel; 

	JTextField username_field;
	JPasswordField password_field;


	public LoginScreen() {
		super("Login to Task Allocation System");
		this.setSize(300,125);
		this.setLayout(new FlowLayout());

		JLabel username_label = new JLabel("Username:");
		JLabel password_label = new JLabel("Password:");

		username_field = new JTextField("admin");
		password_field = new JPasswordField();

		login = new JButton("Login");
		cancel = new JButton("Cancel");
		login.addActionListener(this);
		cancel.addActionListener(this);

		JPanel labelfield_panel = new JPanel();
		labelfield_panel.setLayout(new GridLayout(2,2));
		labelfield_panel.add(username_label);
		labelfield_panel.add(username_field);
		labelfield_panel.add(password_label);
		labelfield_panel.add(password_field);

		JPanel buttonpanel = new JPanel();
		buttonpanel.setLayout(new FlowLayout());
		buttonpanel.add(login);
		buttonpanel.add(cancel);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		mainPanel.add(labelfield_panel, BorderLayout.CENTER);
		mainPanel.add(buttonpanel, BorderLayout.SOUTH);

		add(mainPanel);

		this.setVisible(true);




	}
	@Override
	public void actionPerformed(ActionEvent event) {

		if(event.getSource() == login) {
			//check password
			if(password_field.getPassword().length < 5 || 
					username_field.getText().length() < 5){

				JOptionPane.showMessageDialog(this, "Username or password is too small");

			} else{
				try{
					SqlConnection.connect();
					SqlConnection.ps = SqlConnection.connection.prepareStatement("SELECT * FROM LOGIN WHERE id=?");
					SqlConnection.ps.setString(1, username_field.getText());
					SqlConnection.ps.executeQuery();
					SqlConnection.result = SqlConnection.ps.getResultSet();
					SqlConnection.result.next();

					if(SqlConnection.result.getString("password").equals(new String(password_field.getPassword()))){
						this.setVisible(false);

						TaskManagerFrame managerFrame = new TaskManagerFrame();
						managerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						managerFrame.setVisible(true);
						managerFrame.getContentPane();
						managerFrame.pack();


					} else {
						JOptionPane.showMessageDialog(this, "Warning: You have entered the wrong username/password.");

					}

				}catch(SQLException e){
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, "Warning: Check login details or database connection!");
				}

			}
		}

		if(event.getSource() == cancel) {
			System.exit(0);
		}
		// TODO Auto-generated method stub

	}


	public static void main(String[] args){
		LoginScreen y = new LoginScreen();
	}

}
