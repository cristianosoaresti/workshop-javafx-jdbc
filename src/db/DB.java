package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DB {

	private static Connection conn = null;
	
	// This method is responsible to connect with database 
	public static Connection getConnection () {
		if (conn == null) {
			try {
				Properties pros = loadProperties();
				String url = pros.getProperty("dburl");
				conn = DriverManager.getConnection(url, pros);
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
		return conn;
	}
	
	// This method is responsible to close the connection with a database
	public static void closeConnection () {
		if (conn != null) {
			try {
				conn.close();
			} catch(SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
	
	// This method is responsible to load the properties inside the db.properties file
	private static Properties loadProperties() {
		try(FileInputStream fs = new FileInputStream("db.properties")){
			Properties props = new Properties();
			props.load(fs);
			return props;
		} catch(IOException e) {
			throw new DbException(e.getMessage());
		}
	}
	
	public static void closeStatement (Statement st) {
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
	
	public static void closeResultSet (ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
}
