package timerfx;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import static timerfx.TimerFX.accountDetails;
import static timerfx.TimerFX.debug;

public class LayoutHandler {

	private int width;
	private int height;

	// JavaFX components.
	private Label currentTime;
	private Label listTimes;
	private Label scramble;
	// Time stats components.
	private Label worstTimeLabel;
	private Label bestTimeLabel;
	private Label timeCountLabel;
	private Label avgTimeLabel;
	private Label avg5Label;
	private Label avg12Label;
	private VBox timeStatsBox;
	// Containers for formatting.
	private BorderPane timeStatsFormattingBox;
	private BorderPane timeListFormattingBox;
	private BorderPane scrambleFormattingBox;
	private HBox loginDetailsFormattingBox;

	private BorderPane root;
	private BorderPane masterRoot;
	private Scene primaryScene;
	private Stage primaryStage;

	private Label loggedInStatusLabel;
	private Button loginButton;

	private Dialog loginDialog;

	public LayoutHandler(int width, int height, Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.width = width;
		this.height = height;
		this.setup();
	}

	public void setCurrentTime(String time) {
		this.currentTime.setText(time);
	}

	private void setup() {
		this.currentTime = new Label("00.00");
		this.listTimes = new Label("");
		this.scramble = new Label("");
		// Time stats components.
		this.worstTimeLabel = new Label("");
		this.bestTimeLabel = new Label("");
		this.timeCountLabel = new Label("");
		this.avgTimeLabel = new Label("");
		this.avg5Label = new Label("");
		this.avg12Label = new Label("");
		this.timeStatsBox = new VBox();
		this.timeStatsBox.getChildren().addAll(
				this.worstTimeLabel,
				this.bestTimeLabel,
				this.timeCountLabel,
				this.avgTimeLabel,
				this.avg5Label,
				this.avg12Label);
		
		this.timeStatsFormattingBox = new BorderPane();
		this.scrambleFormattingBox = new BorderPane();
		this.timeListFormattingBox = new BorderPane();

		this.timeStatsFormattingBox.setCenter(this.timeStatsBox);
		this.timeStatsBox.setAlignment(Pos.CENTER);
		this.timeListFormattingBox.setCenter(this.listTimes);
		this.scrambleFormattingBox.setCenter(this.scramble);

		this.root = new BorderPane();

		// Master layout.
		this.masterRoot = new BorderPane();
		this.masterRoot.setLeft(this.root);
		this.masterRoot.setRight(TimerFX.cubeRenderer.subScene());

		this.primaryScene = new Scene(masterRoot, this.width * 2, this.height);
		this.primaryStage.setScene(this.primaryScene);

		// Create the login details box.
		loginDetailsFormattingBox = new HBox();
		loginDetailsFormattingBox.setPadding(new Insets(5, 5, 5, 5));
		this.loggedInStatusLabel = new Label();
		this.setLoggedInStatusLabel();
		this.loginButton = new Button("login");
		loginDetailsFormattingBox.getChildren().addAll(
				this.loggedInStatusLabel,
				this.loginButton);
		this.loginButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				TimerFX.layoutHandler.createLoginPrompt();
			}
		});
		
		this.root.setCenter(this.currentTime);
		this.root.setTop(this.scrambleFormattingBox);
		this.root.setRight(this.timeListFormattingBox);
		this.root.setLeft(this.timeStatsFormattingBox);
		this.root.setBottom(this.loginDetailsFormattingBox);
	}

	private void createLoginPrompt() {
		if (debug) {
			System.out.println("Creating login dialog");
		}
		// Create the custom dialog.
		this.loginDialog = new Dialog<>();
		this.loginDialog.setTitle("Login Dialog");
		this.loginDialog.setHeaderText("Look, a Custom Login Dialog");
		// Set the button types.
		ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
		this.loginDialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField username = new TextField();
		username.setPromptText("Username");
		PasswordField password = new PasswordField();
		password.setPromptText("Password");

		grid.add(new Label("Username:"), 0, 0);
		grid.add(username, 1, 0);
		grid.add(new Label("Password:"), 0, 1);
		grid.add(password, 1, 1);

		// Enable/Disable login button depending on whether a username was entered.
		Node loginButton = this.loginDialog.getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		username.textProperty().addListener((observable, oldValue, newValue) -> {
			loginButton.setDisable(newValue.trim().isEmpty());
		});

		this.loginDialog.getDialogPane().setContent(grid);

		// Request focus on the username field by default.
		Platform.runLater(() -> username.requestFocus());

		// Convert the result to a username-password-pair when the login button is clicked.
		this.loginDialog.setResultConverter(dialogButton -> {
			if (dialogButton == loginButtonType) {
				return new Pair<>(username.getText(), password.getText());
			}
			return null;
		});

		Optional<Pair<String, String>> result = this.loginDialog.showAndWait();

		result.ifPresent(usernamePassword -> {
			if (debug) {
				System.out.println("Username="
						+ usernamePassword.getKey()
						+ ", Password="
						+ usernamePassword.getValue());
			}
			try {
				TimerFX.accountDetails =
						TimerFX.dbConn.login(
								usernamePassword.getKey(),
								usernamePassword.getValue());
				if (TimerFX.accountDetails.get("correctPassword").equals("YES")) {
					this.loggedIn();
				}
			} catch (SQLException ex) {
				System.out.println(ex);
			}
		});
	}

	private void setLoggedInStatusLabel() {
		String loggedOutText = "You are not logged in. ";
		if (TimerFX.accountDetails.isEmpty()) {
			this.loggedInStatusLabel.setText(loggedOutText);
		} else if (TimerFX.accountDetails.get("correctPassword").equals("YES")) {
			this.loggedInStatusLabel.setText("Logged in as "
					+ TimerFX.accountDetails.get("username")
					+ ". ");
		} else {
			this.loggedInStatusLabel.setText(loggedOutText);
		}
	}

	private void loggedIn() {
		this.loginButton.setText("logout");
		this.loginButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				TimerFX.accountDetails.clear(); // "Log out".
				TimerFX.layoutHandler.loggedOut();
			}

		});
		TimerFX.layoutHandler.setLoggedInStatusLabel();
	}

	private void loggedOut() {
		this.loginButton.setText("login");
		this.loginButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				TimerFX.layoutHandler.createLoginPrompt();
			}
		});
		TimerFX.layoutHandler.setLoggedInStatusLabel();
	}
	
	// Public methods.
	public void setListTimes(String s) {
		this.listTimes.setText(s);
	}
	public void setScrambleText(String s) {
		this.scramble.setText(s);
	}
	public void setWorstTimeText(String s) {
		this.worstTimeLabel.setText(s);
	}
	public void setBestTimeText(String s) {
		this.bestTimeLabel.setText(s);
	}
	public void setTimeCountText(String s) {
		this.timeCountLabel.setText(s);
	}
	public void setAvgTimeText(String s) {
		this.avgTimeLabel.setText(s);
	}
	public void setAvg5Text(String s) {
		this.avg5Label.setText(s);
	}
	public void setAvg12Text(String s) {
		this.avg12Label.setText(s);
	}
}
