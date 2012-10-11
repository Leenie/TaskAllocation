
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;

/*
 * CORRECT THIS CLASS THIS IS JUST A TEST
 */
public class SqlConnection {

	static Connection connection = null; //manages connection
	static Statement statement = null; //query statement
	static PreparedStatement ps = null;
	static ResultSet result = null; // manage results

	public static void connect() throws SQLException {


		//establish connection to database
		String url = "jdbc:mysql://localhost:3306/TASKALLOCATIONSYSTEM";
		String user = "root";
		String pass = "12345";

		
		connection = DriverManager.getConnection(url, user, pass);

	}

	public static void closeConnection() {

		try
		{
			result.close();
			statement.close();
			connection.close();

		} //end try
		catch (Exception exception) 
		{
			exception.printStackTrace();
		} //end catch
	}



}
