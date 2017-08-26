package timerfx;

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
import java.util.List;
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
	
	// 3D material.
	private static final PhongMaterial redMaterial = new PhongMaterial(Color.RED);
	
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

	@Override
	public void start(Stage primaryStage) {
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
		Box cubeRender = new Box(5, 5, 5);
		cubeRender.setMaterial(redMaterial);
		cubeRender.getTransforms().addAll(
				new Rotate(45, Rotate.Y_AXIS),
				new Rotate(45, Rotate.X_AXIS),
				new Rotate(15, Rotate.Z_AXIS));
		StackPane cubeEnvironment = new StackPane();
		cubeEnvironment.getChildren().add(cubeRender);
		
		// Make the cube spin over time.
		cubeRender.setRotationAxis(Rotate.Y_AXIS);
		KeyValue cubeYNoRot = new KeyValue(cubeRender.rotateProperty(), 0);
		KeyValue cubeYFullRot = new KeyValue(cubeRender.rotateProperty(), -360);
		Timeline cubeSpinAnimation = new Timeline(
				new KeyFrame(Duration.millis(0), cubeYNoRot),
				new KeyFrame(Duration.millis(4000), cubeYFullRot));
		cubeSpinAnimation.setCycleCount(Timeline.INDEFINITE);
		cubeSpinAnimation.play();
		
		// SubScene for 3D view.
		SubScene subScene = new SubScene(cubeEnvironment, width, height);
		camera = new PerspectiveCamera(true);
		camera.setTranslateZ(-20);
		subScene.setCamera(camera);
		cubeEnvironment.getChildren().add(camera);
		primaryStage.setScene(primaryScene);
		// Add the 3D subscene to the scene.
		masterRoot.setRight(subScene);
		//subScene.setFill(Color.BLUE);
		// TODO: the rest of the 3d render thing.

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
		return scramble;
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
