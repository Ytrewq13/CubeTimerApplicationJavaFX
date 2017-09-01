package timerfx;

import java.sql.SQLException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.stage.Stage;
import javafx.animation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.util.Duration;
import javafx.util.Pair;

public class TimerFX extends Application {

	public static boolean debug = false; // For debugging.

	// Cube.
	public static RubiksCube cube;

	// Window dimensions.
	private static int width = 400;
	private static int height = 300;

	// JavaFX components handling object.
	public static LayoutHandler layoutHandler;

	// 3D window components/nodes handling object.
	public static CubeRenderer cubeRenderer;

	// 3D materials.
	public static final PhongMaterial blackMat = new PhongMaterial(Color.BLACK);
	public static final PhongMaterial materialRight = new PhongMaterial(Color.RED);
	public static final PhongMaterial materialFront = new PhongMaterial(Color.GREEN);
	public static final PhongMaterial materialBack = new PhongMaterial(Color.BLUE);
	public static final PhongMaterial materialUp = new PhongMaterial(Color.WHITE);
	public static final PhongMaterial materialDown = new PhongMaterial(Color.YELLOW);
	public static final PhongMaterial materialLeft = new PhongMaterial(Color.ORANGE);

	public static final PhongMaterial errorMat = new PhongMaterial(Color.MAGENTA);

	// Scramble handling object.
	private static ScrambleHandler scrambler;

	// Login details components.
	private static Dialog<Pair<String, String>> loginDialog;
	private static Button loginButton;
	private static Label loggedInStatusLabel;

	// Timer handling object.
	private static TimerHandler timerHandler;

	// Timestamps.
	private static long startTime;
	private static long endTime;
	private static long inspectionStartTime;

	// Timer booleans.
	private static boolean started = false;
	private static boolean ending = false;
	private static boolean inspecting = false;

	// Timer Lists.
	public static List<Float> times = new ArrayList<Float>();
	public static List<Long> timestamps = new ArrayList<Long>();

	// Time list length max.
	private static final int MAXTIMELISTLENGTH = 15;

	// Database connector.
	public static DataBaseConnector dbConn;
	// Account detail storage.
	public static HashMap<String, String> accountDetails;

	@Override
	public void start(Stage primaryStage) {
		accountDetails = new HashMap<String, String>();
		// Create the database connector.
		dbConn = new DataBaseConnector();
		// Create the cube.
		cube = new RubiksCube();
		// 3D cube shape.
		cubeRenderer = new CubeRenderer(width, height);
		// Create the layout handler.
		layoutHandler = new LayoutHandler(width, height, primaryStage);
		// Define timers.
		timerHandler = new TimerHandler();
		// Define the scramble generator.
		scrambler = new ScrambleHandler();

		//++++++++++++++++++++++++++++++++++++++++++++++
		// Event handling code taken from stackoverflow.
		primaryStage.getScene().addEventFilter(KeyEvent.KEY_PRESSED,
				new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke) {
				if (ke.getCode() == KeyCode.SPACE) {
					if (debug) {
						System.out.println("========================");
						System.out.println("====Ending:" + ending + "=========");
						System.out.println("====Started:" + started + "========");
						System.out.println("====Inspecting:" + inspecting + "====");
						System.out.println("========================");
					}
					if (started) {
						endTimer();
					}
					ke.consume(); // <-- stops passing the event to next node
				}
			}
		});
		//++++++++++++++++++++++++++++++++++++++++++++++
		primaryStage.getScene().addEventFilter(KeyEvent.KEY_RELEASED,
				new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke) {
				if (ke.getCode() == KeyCode.SPACE) {
					if (!started) {
						if (debug) System.out.println("[[[[STARTING]]]]");
						startTimer();
					} else if (inspecting) {
						inspecting = false;
						started = true;
						timerHandler.stopInspecting();
					}
					ke.consume();
				}
			}
		});

		primaryStage.setTitle("My cube timer.");
		updateAverages();
		primaryStage.setMinWidth(width * 2);
		primaryStage.setMinHeight(height);
		primaryStage.show();
	}

	public static void inspect() {
		timerHandler.startInspecting();
	}

	public static void startTimer() {
		if (!ending) {
			if (debug) {
				System.out.println("----NOT ENDING----");
			}
			if (!inspecting) {
				if (debug) {
					System.out.println("----NOT INSPECTING----");
				}
				inspecting = true;
				inspect();
			} else {
				if (debug) {
					System.out.println("----INSPECTING----");
				}
				timerHandler.stopInspecting();
				timerHandler.startTiming();
				started = true;
				inspecting = false;
			}
		} else {
			if (debug) {
				System.out.println("----ENDING----");
			}
			ending = false;
		}
	}

	public static void endTimer() {
		started = false;
		if (!ending) {
			if (debug) {
				System.out.println("++++NOT ENDING++++");
			}
			ending = true;
			timerHandler.stopTiming();
			updateAverages();
		}
	}

	public static void updateAverages() {
		if (debug) {
			System.out.println("updating averages");
		}
		// Update the list of times.
		String listOfTimes = "";
		int timesToShow = (times.size() < MAXTIMELISTLENGTH) ? times.size() : MAXTIMELISTLENGTH;
		for (int i = 1; i <= timesToShow; i++) {
			listOfTimes += Float.toString(times.get(times.size() - i)) + "\n";
		}
		layoutHandler.setListTimes(listOfTimes);
		// Make a scramble.
		scrambler.updateScramble();
		layoutHandler.setScrambleText(scrambler.scramble());
		// Reset the cube.
		cube = new RubiksCube();
		cube.moveSet(scrambler.scramble());
		if (debug) {
			cube.testCoords();
		}
		cubeRenderer.update();
		// Get the best and worst times.
		float worstTime = bestTime(-1);
		float bestTime = bestTime(1);
		if (times.size() > 0) {
			layoutHandler.setWorstTimeText("Worst: " + worstTime);
			layoutHandler.setBestTimeText("Best: " + bestTime);
			layoutHandler.setTimeCountText("Num. of times: " + times.size());
			layoutHandler.setAvgTimeText("Avg: " + averageTime());
		}
		if (times.size() < 5) {
			// Not enough times for any avg, return.
			return;
		} else {
			// Enough times for an avg5.
			float sum = 0;
			for (int i = 1; i <= 5; i++) {
				sum += times.get(times.size() - i);
			}
			float avg5 = sum / 5;
			layoutHandler.setAvg5Text("Avg5: " + avg5);
			if (times.size() >= 12) {
				// Enough times for an avg12.
				sum = 0;
				for (int i = 1; i <= 12; i++) {
					sum += times.get(times.size() - i);
				}
				float avg12 = sum / 12;
				layoutHandler.setAvg12Text("Avg12: " + avg12);
			}
		}
	}

	public static float bestTime(int direction) {
		// direction:
		// 1:  best time
		// -1: worst time
		float record = 0;
		if (times.size() > 0) {
			record = times.get(0);
		}
		for (int i = 0; i < times.size(); i++) {
			if (times.get(i) * direction < record * direction) {
				record = times.get(i);
			}
		}
		return record;
	}

	public static float averageTime() {
		float sum = 0;
		for (int i = 0; i < times.size(); i++) {
			sum += times.get(i);
		}
		return sum / times.size();
	}

	public static void main(String[] args) {
		launch();
	}

}
