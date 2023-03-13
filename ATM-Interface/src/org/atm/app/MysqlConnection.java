package org.atm.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlConnection {
	
	static Connection conn=null;
	
	public static Connection getConnection() throws SQLException, ClassNotFoundException
	{
		Class.forName("com.mysql.cj.jdbc.Driver");
		conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_db", "root", "");
		return conn;
	}
	
	
	public static void closeConnection()
	{
		if(conn!=null)
		{
			try 
			{
				conn.close();
			} 
			catch (SQLException e) 
			{
				
				System.out.println("Something went wrong!!");
			}
		}
	}

}
