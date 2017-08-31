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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.util.Duration;

public class TimerFX extends Application {

	public static boolean debug = false; // For debugging.
	
	// Cube.
	private static RubiksCube cube;

	// Window dimensions.
	private static int width = 400;
	private static int height = 300;

	// JavaFX components.
	private static Label currentTime;
	private static Label averageTime;
	private static Label listTimes;
	private static Label scramble;
	
	// 3D window components/nodes.
	private static Camera camera;
	private static Group cubeGroup;
	
	private static Box[][][] stickers;
	
	// 3D materials.
	public static final PhongMaterial blackMat = new PhongMaterial(Color.BLACK);
	public static final PhongMaterial redMat = new PhongMaterial(Color.RED);
	public static final PhongMaterial greenMat = new PhongMaterial(Color.GREEN);
	public static final PhongMaterial blueMat = new PhongMaterial(Color.BLUE);
	public static final PhongMaterial whiteMat = new PhongMaterial(Color.WHITE);
	public static final PhongMaterial yellowMat = new PhongMaterial(Color.YELLOW);
	public static final PhongMaterial orangeMat = new PhongMaterial(Color.ORANGE);
	
	public static final PhongMaterial errorMat = new PhongMaterial(Color.MAGENTA);
	
	// Scramble.
	private static String scrambleString;
	// Time stats components.
	private static Label worstTimeLabel;
	private static Label bestTimeLabel;
	private static Label timeCountLabel;
	private static Label avgTimeLabel;
	private static Label avg5Label;
	private static Label avg12Label;
	private static VBox timeStatsBox;
	
	// Containers for formatting.
	private static BorderPane timeStatsFormattingBox;
	private static BorderPane timeListFormattingBox;
	private static BorderPane scrambleFormattingBox;

	// Timers.
	private static AnimationTimer inspectionTimer;
	private static AnimationTimer timer;

	// Timestamps.
	private static long startTime;
	private static long endTime;
	private static long inspectionStartTime;

	// Timer booleans.
	private static boolean started = false;
	private static boolean ending = false;
	private static boolean inspecting = false;

	// Timer Lists.
	private static List<Float> times = new ArrayList<Float>();
	private static List<Long> timestamps = new ArrayList<Long>();

	// Inspection length.
	private static final int inspectionTime = 15;
	// Time list length max.
	private static final int maxTimeListLength = 15;
	
	// Database connector.
	private static DataBaseConnector dbConn;
	// Account detail storage.
	private static HashMap<String, String> accountDetails;

	@Override
	public void start(Stage primaryStage) {
		// Create the database connector.
		dbConn = new DataBaseConnector();
		try {
			accountDetails = dbConn.login("ytrewq13","password");
			if (accountDetails.get("correctPassword").equals("YES")) {
				System.out.println(accountDetails.get("forename"));
			}
		} catch (SQLException ex) {
			System.out.println(ex);
		}
		
		
		// Create the cube.
		cube = new RubiksCube();
		
		BorderPane root = new BorderPane();
		
		timeStatsFormattingBox = new BorderPane();
		timeListFormattingBox = new BorderPane();
		scrambleFormattingBox = new BorderPane();

		// Create components.
		currentTime = new Label("00.00");
		listTimes = new Label("");
		scramble = new Label("");
		// Time stats components.
		worstTimeLabel = new Label("");
		bestTimeLabel = new Label("");
		timeCountLabel = new Label("");
		avgTimeLabel = new Label("");
		avg5Label = new Label("");
		avg12Label = new Label("");
		timeStatsBox = new VBox();
		timeStatsBox.getChildren().addAll(worstTimeLabel,
				bestTimeLabel, timeCountLabel, avgTimeLabel, avg5Label, avg12Label);

		timeStatsFormattingBox.setCenter(timeStatsBox);
		timeStatsBox.setAlignment(Pos.CENTER);
		timeListFormattingBox.setCenter(listTimes);
		scrambleFormattingBox.setCenter(scramble);

		root.setCenter(currentTime);
		root.setTop(scrambleFormattingBox);
		root.setRight(timeListFormattingBox);
		root.setLeft(timeStatsFormattingBox);
		
		// Master layout.
		BorderPane masterRoot = new BorderPane();
		masterRoot.setLeft(root);

		Scene primaryScene = new Scene(masterRoot, width*2, height);
		
		// 3D cube shape.
		cubeGroup = new Group();
		Box blackCube = new Box(2.9, 2.9, 2.9);
		blackCube.setMaterial(blackMat);
		cubeGroup.getChildren().add(blackCube);
		// Create the stickers and arrange them around the cube.
		stickers = new Box[6][3][3];
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 3; k++) {
					Box box = new Box(0.95, 0.95, 0.02);
					if (i == 0 || i == 1) {
						// U or D.
						box.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
						box.setTranslateX((j-1));
						box.setTranslateY(-(1.5*(2*i-1))); // Translate.
						box.setTranslateZ((k-1));
						box.setMaterial((i==0)?whiteMat:yellowMat);
					} else if (i == 2 || i == 3) {
						// L or R.
						box.getTransforms().add(new Rotate(90, Rotate.Y_AXIS));
						box.setTranslateX((1.5*(2*i-5))); // Translate.
						box.setTranslateY(-(j-1));
						box.setTranslateZ((k-1));
						box.setMaterial((i==2)?redMat:orangeMat);
					} else {
						// B or F.
						// No need to rotate.
						box.setTranslateX((j-1));
						box.setTranslateY(-(k-1));
						box.setTranslateZ((1.5*(2*i-9))); // Translate.
						box.setMaterial((i==4)?greenMat:blueMat);
					}
					stickers[i][j][k] = box;
					cubeGroup.getChildren().add(box);
				}
			}
		}
		cubeGroup.getTransforms().addAll(new Rotate(150, Rotate.X_AXIS));
		cubeGroup.setTranslateZ(15);
		StackPane cubeEnvironment = new StackPane();
		cubeEnvironment.getChildren().add(cubeGroup);
		
		// Make the cube spin over time.
		cubeGroup.setRotationAxis(Rotate.Y_AXIS);
		KeyValue cubeYNoRot = new KeyValue(cubeGroup.rotateProperty(), 0);
		KeyValue cubeYFullRot = new KeyValue(cubeGroup.rotateProperty(), -360);
		Timeline cubeSpinAnimation = new Timeline(
				new KeyFrame(Duration.millis(0), cubeYNoRot),
				new KeyFrame(Duration.millis(12000), cubeYFullRot));
		cubeSpinAnimation.setCycleCount(Timeline.INDEFINITE);
		cubeSpinAnimation.play();
		
		// SubScene for 3D view.
		SubScene subScene = new SubScene(cubeEnvironment, width, height, true, SceneAntialiasing.BALANCED);
		camera = new PerspectiveCamera(true);
		//camera.setTranslateZ(-15); // DO NOT DO THIS.
		/*
		* If the camera is moved to a negative z-coordinate,
		* only objects with a negative z-coordinate will be rendered.
		*
		* I think it has something to do with the near clipping plane,
		* which has a default z-coordinate of 0.01
		*
		* Perhaps the coordinate is absolute, and not relative to the
		* camera.
		*
		* In any case, NEVER translate the camera to a negative z-coordinate,
		* especially not before implementing a depth buffer / depth testing.
		*/
		subScene.setCamera(camera);
		cubeEnvironment.getChildren().add(camera);
		primaryStage.setScene(primaryScene);
		// Add the 3D subscene to the scene.
		masterRoot.setRight(subScene);

		// Define timers.
		inspectionTimer = new AnimationTimer() {
			private long endTime;
			private long remainingSeconds;

			@Override
			public void start() {
				this.endTime = System.currentTimeMillis() + inspectionTime * 1000;
				super.start();
			}

			@Override
			public void stop() {
				super.stop();
			}

			@Override
			public void handle(long l) {
				// Do stuff here.
				long newTime = System.currentTimeMillis();
				this.remainingSeconds = (this.endTime - newTime) / 1000 + 1;
				currentTime.setText(Long.toString(this.remainingSeconds));
				if (this.remainingSeconds == 0) {
					this.stop();
					startTimer();
				}
			}
		};

		timer = new AnimationTimer() {
			private long time = 0;
			private long startTime;

			@Override
			public void start() {
				startTime = System.currentTimeMillis();
				super.start();
			}

			@Override
			public void stop() {
				super.stop();
				times.add(this.time / 1000f);
				timestamps.add(System.currentTimeMillis());
				// save leftover time not handled with the last update
			}

			@Override
			public void handle(long l) {
				// Do stuff here.
				long newTime = System.currentTimeMillis();
				this.time = newTime - this.startTime;
				currentTime.setText(Float.toString(this.time / 1000f));
			}
		};

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
						if (debug) {System.out.println("[[[[STARTING]]]]");}
						startTimer();
					} else if (inspecting) {
						inspecting = false;
						started = true;
						inspectionTimer.stop();
					}
					ke.consume();
				}
			}
		});

		primaryStage.setTitle("My cube timer.");
		updateAverages();
		primaryStage.setMinWidth(width*2);
		primaryStage.setMinHeight(height);
		primaryStage.show();
	}

	public static void inspect() {
		inspectionTimer.start();
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
				inspectionTimer.stop();
				timer.start();
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
			timer.stop();
			updateAverages();
		}
	}

	public static void updateAverages() {
		if (debug) {
			System.out.println("updating averages");
		}
		// Update the list of times.
		String listOfTimes = "";
		int timesToShow = (times.size() < maxTimeListLength) ? times.size() : maxTimeListLength;
		for (int i = 1; i <= timesToShow; i++) {
			listOfTimes += Float.toString(times.get(times.size() - i)) + "\n";
		}
		listTimes.setText(listOfTimes);
		// Make a scramble.
		scrambleString = makeScramble(20);
		scramble.setText(scrambleString);
		// Reset the cube.
		cube = new RubiksCube();
		cube.moveSet(scrambleString);
		if (debug) cube.testCoords();
		updateCubeRender();
		// Get the best and worst times.
		float worstTime = bestTime(-1);
		float bestTime = bestTime(1);
		if (times.size() > 0) {
			worstTimeLabel.setText("Worst: " + worstTime);
			bestTimeLabel.setText("Best: " + bestTime);
			timeCountLabel.setText("Num. of times: " + times.size());
			avgTimeLabel.setText("Avg: " + averageTime());
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
			avg5Label.setText("Avg5: " + avg5);
			if (times.size() >= 12) {
				// Enough times for an avg12.
				sum = 0;
				for (int i = 1; i <= 12; i++) {
					sum += times.get(times.size() - i);
				}
				float avg12 = sum / 12;
				avg12Label.setText("Avg12: " + avg12);
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

	public static String makeScramble(int length) {
		char[] options = {'U', 'D', 'L', 'R', 'F', 'B'};
		char[] faces = new char[length];
		for (int i = 0; i < length; i++) {
			int index;
			boolean viable = false;
			do {
				index = (int) Math.floor(Math.random() * options.length);
				if (i > 0) {
					viable = !(options[index] == faces[i - 1]);
				} else {
					viable = true;
				}
			} while (!viable);
			faces[i] = options[index];
		}
		String scramble = "";
		for (int i = 0; i < faces.length; i++) {
			int turns = (int) Math.floor(Math.random() * 3);
			switch (turns) {
				case 0:
					// 1 turn.
					scramble += faces[i] + "  ";
					break;
				case 1:
					// 2 turns.
					scramble += faces[i] + "2 ";
					break;
				case 2:
					// 3 turns.
					scramble += faces[i] + "' ";
					break;
				default:
					System.out.println("Something went wrong.");
			}
		}
		//scramble = "R  U  R' U' "; // TODO: remove.
		return scramble;
	}

	public static float averageTime() {
		float sum = 0;
		for (int i = 0; i < times.size(); i++) {
			sum += times.get(i);
		}
		return sum / times.size();
	}
	
	public static void updateCubeRender() {
		cube.updateRender();
		for (int i = 0; i < stickers.length; i++) {
			for (int j = 0; j < stickers[0].length; j++) {
				for (int k = 0; k < stickers[0][0].length; k++) {
					stickers[i][j][k].setMaterial(cube.colorAt(i,j,k));
					if (j == 1 && k == 1) {
						switch(i) {
							case 0:
								stickers[i][j][k].setMaterial(whiteMat);
								break;
							case 1:
								stickers[i][j][k].setMaterial(yellowMat);
								break;
							case 2:
								stickers[i][j][k].setMaterial(redMat);
								break;
							case 3:
								stickers[i][j][k].setMaterial(orangeMat);
								break;
							case 4:
								stickers[i][j][k].setMaterial(greenMat);
								break;
							default:
								stickers[i][j][k].setMaterial(blueMat);
								break;
						}
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		launch();
	}

}
