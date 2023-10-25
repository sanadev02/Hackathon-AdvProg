package uk.ac.mmu.advprog.hackathon;

import static spark.Spark.get;
import static spark.Spark.port;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handles the setting up and starting of the web service You will be adding
 * additional routes to this class, and it might get quite large Feel free to
 * distribute some of the work to additional child classes, like I did with DB
 * 
 * @author You, Mainly!
 */
public class TransportWebService {

	/**
	 * Main program entry point, starts the web service
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		port(8088);

		// Simple route so you can check things are working...
		// Accessible via http://localhost:8088/test in your browser
		get("/test", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				try (DB db = new DB()) {
					return "Number of Entries: " + db.getNumberOfEntries();
				}
			}
		});
		
		/**
		 * Finds how many stops there are in a locality.
		 * 
		 * @returns  nameLocality
		 */

		get("/stopcount", new Route() {
			@Override
			public Object handle(Request arg0, Response arg1) throws Exception {
				try (DB db = new DB()) {
					String nameLocality = arg0.queryParams("locality");

					if (nameLocality == null || nameLocality.equals("")) {
						return ("Invalid request");
					} else {
						return "Number of Stops: " + db.locality(nameLocality);
					}
				}
			}
		});
		
		/**
		 * Finds how many stops in a particular locality of a transport type.
		 * 
		 * @returns Type, nameLocality
		 */

		get("/stops", new Route() {
			@Override
			public Object handle(Request arg2, Response arg3) throws Exception {
				arg3.header("content-type", "application/json");

				try (DB db = new DB()) {
					String nameLocality = arg2.queryParams("locality");
					String Type = arg2.queryParams("type");
					
					if (Type == null || Type.equals("")) {
						return ("Invalid request");
					} else {
						return db.StopType(Type, nameLocality);
					}

				}
			}
		});
		
	
		
		get("/nearest", new Route() {
			@Override
			public Object handle(Request arg4, Response arg5) throws Exception {
				try (DB db = new DB()) {
					String Latitude = arg4.queryParams("latitude");
					String Longitude = arg4.queryParams("longitude");
					String Type = arg4.queryParams("type");
					return db.near(Latitude,Longitude,Type);
				}
			}
		});
		

		System.out.println("Server up! Don't forget to kill the program when done!");
	}

}
