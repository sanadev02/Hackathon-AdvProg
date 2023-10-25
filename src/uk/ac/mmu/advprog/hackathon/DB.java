package uk.ac.mmu.advprog.hackathon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Handles database access from within your web service
 * 
 * @author You, Mainly!
 */
public class DB implements AutoCloseable {

	// allows us to easily change the database used
	private static final String JDBC_CONNECTION_STRING = "jdbc:sqlite:./data/NaPTAN.db";

	// allows us to re-use the connection between queries if desired
	private Connection connection = null;

	/**
	 * Creates an instance of the DB object and connects to the database
	 */
	public DB() {
		try {
			connection = DriverManager.getConnection(JDBC_CONNECTION_STRING);
		} catch (SQLException sqle) {
			error(sqle);
		}
	}

	/**
	 * Returns the number of entries in the database, by counting rows
	 * 
	 * @return The number of entries in the database, or -1 if empty
	 */
	public int getNumberOfEntries() {
		int result = -1;
		try {
			Statement s = connection.createStatement();
			ResultSet results = s.executeQuery("SELECT COUNT(*) AS count FROM Stops");
			while (results.next()) { // will only execute once, because SELECT COUNT(*) returns just 1 number
				result = results.getInt(results.findColumn("count"));
			}
		} catch (SQLException sqle) {
			error(sqle);

		}
		return result;
	}

	public int locality(String nameLocality) {
		int result = 0;
		try {
			PreparedStatement loc = connection
					.prepareStatement("SELECT COUNT(*) AS Number FROM Stops WHERE LocalityName = ? ");
			loc.setString(1, nameLocality);
			ResultSet result1 = loc.executeQuery();
			while (result1.next()) {
				result = result1.getInt(result1.findColumn("Number"));
				System.out.println("Number of Stops: " + result1.getInt("Number"));
			}
		} catch (SQLException sqle) {
			error(sqle);
		}
		return result;
	}

	public JSONArray StopType(String Type, String nameLocality) {
		JSONArray result = new JSONArray();
		try {

			PreparedStatement log = connection
					.prepareStatement("SELECT * FROM Stops WHERE LocalityName = ? AND StopType = ?");
			log.setString(1, nameLocality);
			log.setString(2, Type);
			ResultSet result2 = log.executeQuery();

			while (result2.next()) {

				String check = result2.getString("Landmark"); // Clears any empty data cells set to NULL.
				if (check == null) {
					check = "";
				}
				String check1 = result2.getString("Indicator");
				if (check1 == null) {
					check1 = "";
				}
				String check2 = result2.getString("Bearing");
				if (check2 == null) {
					check2 = "";
				}
				String check3 = result2.getString("Street");
				if (check3 == null) {
					check3 = "";
				}

				JSONObject object = new JSONObject();

				object.put("name", result2.getString("CommonName"));
				object.put("locality", result2.getString(result2.findColumn("LocalityName")));

				JSONObject object2 = new JSONObject();

				object2.put("indicator", check1);
				object2.put("bearing", check2);
				object2.put("street", check3);
				object2.put("landmark", check);

				object.put("location", object2);
				object.put("type", result2.getString("StopType"));

				result.put(object);

			}
		} catch (SQLException sqle) {
			error(sqle);
		}
		return result;
	}

	public int near(String Latitude, String Longitude, String Type) {
		int result = 0;
		try {
			PreparedStatement loc = connection.prepareStatement(
					"SELECT * FROM Stops WHERE StopType = ? AND Langitude IS NOT NULL AND Longitude IS NOT NULL ORDER BY ( ((53.472 - Latitude) * (53.472 - Latitude)) + (0.595 * ((-2.244 - Longitude) *(-2.244 - Longitude))) ) ASC LIMIT 5;");
			loc.setString(1, Type);
			ResultSet results = loc.executeQuery();

			while (results.next()) {
				result = results.getInt(results.findColumn("Number"));
			}
		} catch (SQLException sqle) {
			error(sqle);
		}
		return result;

	}

	/**
	 * Closes the connection to the database, required by AutoCloseable interface.
	 */
	@Override
	public void close() {
		try {
			if (!connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException sqle) {
			error(sqle);
		}
	}

	/**
	 * Prints out the details of the SQL error that has occurred, and exits the
	 * programme
	 * 
	 * @param sqle Exception representing the error that occurred
	 */
	private void error(SQLException sqle) {
		System.err.println("Problem Opening Database! " + sqle.getClass().getName());
		sqle.printStackTrace();
		System.exit(1);
	}

}
