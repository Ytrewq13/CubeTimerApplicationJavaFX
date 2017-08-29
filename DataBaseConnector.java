package timerfx;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class DataBaseConnector {

	// IP addresses.
	private String lanIP;
	private String wanIP;

	// Database details.
	private String driver;
	private String userTable;
	private String sessionTable;
	private String timeTable;
	private String username;
	private String password;

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
			// DONOTHINGFORNOW.
			// TODO: SOMETHINGINTHEFUTURE.
			System.out.println(ex);
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
		this.driver = (String) jsonObject.get("driver");
		this.username = (String) jsonObject.get("username");
		this.userTable = (String) jsonObject.get("usertable");
		this.sessionTable = (String) jsonObject.get("sessiontable");
		this.timeTable = (String) jsonObject.get("timetable");
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
