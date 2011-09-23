package core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

	Connection con;
	boolean connected = false;

	/**
	 * testing only
	 * 
	 * @param argsl
	 */
	public static void main(String args[]) {
		Database db = new Database();
		db.connect();

		db.disconnect();
		System.out.println("done");
	}

	public Database() {
	}

	public void connect() {
		String dbUrl = "jdbc:mysql://mysql.stud.ntnu.no/andrskom_vis";
		String user = "andrskom_track";
		String pwd = "afyx64";

		String dbClass = "com.mysql.jdbc.Driver";

		try {

			Class.forName(dbClass);
			con = DriverManager.getConnection(dbUrl, user, pwd);
			connected = true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			connected = false;
		}

		catch (SQLException e) {
			e.printStackTrace();
			connected = false;
		}
	}

	public void disconnect() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		connected = false;
	}

	public ResultSet query(String query) {
		if (!connected)
			throw new NullPointerException("not connected to DB");
		Statement stmt;
		try {
			//System.out.println("QUERYING "+query);
			stmt = con.createStatement();

			ResultSet r = stmt.executeQuery(query);
			return r;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	public void execute(String query) {
		if (!connected)
			throw new NullPointerException("not connected to DB");
		Statement stmt;
		try {
			stmt = con.createStatement();
			stmt.execute(query);
			return;

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
