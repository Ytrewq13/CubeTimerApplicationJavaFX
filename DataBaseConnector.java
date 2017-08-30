package timerfx;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class DataBaseConnector {

	// IP addresses.
	private String lanIP;
	private String wanIP;
	private String port;

	// Database details.
	private String database;
	private String driver;
	private String userTable;
	private String sessionTable;
	private String timeTable;
	private String username;
	private String password;
	
	// Database fields.
	private Connection connection;

	// JavaScript parser for getting JSON data.
	private ScriptEngineManager factory;
	private ScriptEngine engine;
	private FileReader jsonReader;
	
	// JavaScript code for JavaScripting the JSON.
	private String javaScript;

	public DataBaseConnector() {
		this.defineJSString();
		this.factory = new ScriptEngineManager();
		this.engine = this.factory.getEngineByName("JavaScript");
		try {
			this.getDetails(".gitignore/dbinfo.json");
		} catch (IOException | ScriptException ex) {
			System.out.println(ex);
		}
		try {
			this.connectDB();
		} catch (SQLException ex) {
			System.out.println(ex);
		}
	}
	
	private void connectDB() throws SQLException {
		if (TimerFX.debug) {
			System.out.println("Connecting to database...");
			
			System.out.println("Using login username: " + this.username);
			System.out.println("Using login password: " + this.password);
		}
		String ip = this.lanIP;
		// Connect to the database.
		this.connection = DriverManager.getConnection(
				"jdbc:"
				+ this.driver
				+ "://"
				+ ip
				+ ":"
				+ this.port
				+ "/"
				+ this.database,
				this.username,
				this.password);
		/*
		* Note to self.
		*
		* I had an issue for ages which I couldn't figure out.
		* The Exception I was getting looked like I wasn't sending
		* a password to the database.
		* Eventually I tried just changing "this.password" to "password",
		* which was the password at the time. This made the error go away
		* and the system connected to the database successfully.
		*
		* It turned out the error was being caused by my not setting the
		* password field in the "getDetails()" method - I had completely
		* missed out the line.
		*
		* TL;DR.
		* ALWAYS print your variables when you run into an issue like this.
		* It will save you A LOT of time messing about with user creation
		* in an ssh terminal on the mysql server.
		*/
		// As long as there was no error, we are now connected to the database.
		if (TimerFX.debug) System.out.println("Connected to database successfully!");
	}

	private void getDetails(String JSONurl) throws FileNotFoundException, ScriptException, IOException {
		this.engine.eval(this.javaScript);
		ScriptEngine jsonParser = this.engine;

		this.jsonReader = new FileReader(JSONurl);
		String jsonString = "";
		int charInt = -2;
		while (charInt != -1) {
			if (charInt != -2) {
				jsonString += (char) charInt; // Don't include the EOF or -2 in the string.
			}
			charInt = jsonReader.read();
		}
		if (TimerFX.debug) {
			System.out.println("+++++++++++++++++++++++++++++++++++\n"
					+ "DATABASE DETAILS\n\n"
					+ jsonString
					+ "+++++++++++++++++++++++++++++++++++");
		}

		String evaluable = "new java.util.concurrent.atomic.AtomicReference(toJava((" + jsonString + ")))";

		AtomicReference ret = (AtomicReference) jsonParser.eval(evaluable);
		HashMap jsonObject = (HashMap) ret.get();
		//System.out.println(jsonObject.get("username")); // IT WORKS!!!
		// Get and set the stuff things.
		this.database = (String) jsonObject.get("database");
		this.driver = (String) jsonObject.get("driver");
		this.username = (String) jsonObject.get("username");
		this.password = (String) jsonObject.get("password");
		this.userTable = (String) jsonObject.get("usertable");
		this.sessionTable = (String) jsonObject.get("sessiontable");
		this.timeTable = (String) jsonObject.get("timetable");
		this.lanIP = (String) jsonObject.get("lanip");
		this.wanIP = (String) jsonObject.get("wanip");
		this.port = (String) jsonObject.get("port");
	}

	private void defineJSString() {
		this.javaScript = ""
				+ "toJava = function(o) {\n"
				+ "  return o == null ? null : o.toJava();\n"
				+ "};\n"
				+ "Object.prototype.toJava = function() {\n"
				+ "  var m = new java.util.HashMap();\n"
				+ "  for (var key in this)\n"
				+ "    if (this.hasOwnProperty(key))\n"
				+ "      m.put(key, toJava(this[key]));\n"
				+ "  return m;\n"
				+ "};\n"
				+ "Array.prototype.toJava = function() {\n"
				+ "  var l = this.length;\n"
				+ "  var a = new java.lang.reflect.Array.newInstance(java.lang.Object, l);\n"
				+ "  for (var i = 0;i < l;i++)\n"
				+ "    a[i] = toJava(this[i]);\n"
				+ "  return a;\n"
				+ "};\n"
				+ "String.prototype.toJava = function() {\n"
				+ "  return new java.lang.String(this);\n"
				+ "};\n"
				+ "Boolean.prototype.toJava = function() {\n"
				+ "  return java.lang.Boolean.valueOf(this);\n"
				+ "};\n"
				+ "Number.prototype.toJava = function() {\n"
				+ "  return java.lang.Integer(this);\n"
				+ "};";
	}
}
