package timerfx;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.bind.DatatypeConverter;

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
			//this.disconncetDB();
		} catch (SQLException ex) {
			System.out.println(ex);
		}
	}

	public HashMap<String, String> login(String username, String password) throws SQLException {
		Statement stmt = this.connection.createStatement();
		PreparedStatement passDetails = this.connection.prepareStatement(
				"SELECT password, salt FROM "
				+ this.userTable
				+ " WHERE username=? LIMIT 1;");
		passDetails.setString(1, username); // Using prepared statements to prevent SQL injection.
		ResultSet rs = passDetails.executeQuery();
		rs.next();
		String saltedPass = password + rs.getString(2);
		HashMap<String, String> loggedInValues = new HashMap<String, String>();
		if (this.getSha256Hex(saltedPass, "UTF-8").equals(rs.getString(1))) {
			/*
			* Note to self:
			* In java, NEVER compare Strings using "==" or "!="
			* Always use "String.equals(String)" instead.
			*/
			// Correct password, request username, forname from DB.
			PreparedStatement loggedInQuery = this.connection.prepareStatement(
					"SELECT username, forename FROM "
							+ this.userTable
							+ " WHERE username=? LIMIT 1;");
			loggedInQuery.setString(1, username); // Bobby Tables won't get me.
			rs = loggedInQuery.executeQuery();
			rs.next();
			loggedInValues.put("username", rs.getString(1));
			loggedInValues.put("forename", rs.getString(2));
			System.out.println("Correct password!");
			loggedInValues.put("correctPassword", "YES");
		} else {
			// Incorrect password.
			System.out.println("Incorrect password!");
			loggedInValues.put("correctPassword", "NO");
		}
		// Create the hashmap to return.
		return loggedInValues;
	}

	private void disconncetDB() {
		try {
			this.connection.close();
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
		String ip = (TimerFX.useWANConnections) ? this.wanIP : this.lanIP;
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
		* Eventually I tried just changing this.password to "password",
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
		if (TimerFX.debug) {
			System.out.println("Connected to database successfully!");
		}
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

	public String getSha256Hex(String text, String encoding) {
		String shaHex = "";
		encoding = (encoding == null)? "UTF-8" : encoding; // This line is probably the issue.
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			md.update(text.getBytes(encoding));
			byte[] digest = md.digest();

			shaHex = DatatypeConverter.printHexBinary(digest);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
			System.out.println(ex);
		}
		return shaHex.toLowerCase();
	}
}
