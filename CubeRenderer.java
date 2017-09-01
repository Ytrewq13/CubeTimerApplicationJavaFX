package timerfx;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import static timerfx.TimerFX.blackMat;
import static timerfx.TimerFX.materialRight;
import static timerfx.TimerFX.materialFront;
import static timerfx.TimerFX.materialBack;
import static timerfx.TimerFX.materialUp;
import static timerfx.TimerFX.materialDown;
import static timerfx.TimerFX.materialLeft;

public class CubeRenderer {

	private Group cubeGroup;
	private Box[][][] stickers;
	public StackPane cubeEnvironment;
	private SubScene subScene;
	private PerspectiveCamera camera;

	private int width;
	private int height;

	public CubeRenderer(int width, int height) {
		this.width = width;
		this.height = height;
		// 3D cube shape.
		this.setup();
	}

	public void update() {
		TimerFX.cube.updateRender();
		for (int i = 0; i < stickers.length; i++) {
			for (int j = 0; j < stickers[0].length; j++) {
				for (int k = 0; k < stickers[0][0].length; k++) {
					stickers[i][j][k].setMaterial(TimerFX.cube.colorAt(i, j, k));
					if (j == 1 && k == 1) {
						switch (i) {
							case 0:
								stickers[i][j][k].setMaterial(materialUp);
								break;
							case 1:
								stickers[i][j][k].setMaterial(materialDown);
								break;
							case 2:
								stickers[i][j][k].setMaterial(materialRight);
								break;
							case 3:
								stickers[i][j][k].setMaterial(materialLeft);
								break;
							case 4:
								stickers[i][j][k].setMaterial(materialFront);
								break;
							default:
								stickers[i][j][k].setMaterial(materialBack);
								break;
						}
					}
				}
			}
		}
	}

	public SubScene subScene() {
		return this.subScene;
	}

	// Private methods used for setup.
	private void setup() {
		// 3D cube shape.
		this.cubeGroup = new Group();
		Box blackCube = new Box(2.9, 2.9, 2.9);
		blackCube.setMaterial(blackMat);
		this.cubeGroup.getChildren().add(blackCube);
		this.cubeEnvironment = new StackPane();
		this.cubeEnvironment.getChildren().add(this.cubeGroup);
		// Other setup methods.
		this.setupStickers();
		this.setupPositioning();
		this.setupScene();
	}

	private void setupStickers() {
		// Create the stickers and arrange them around the cube.
		this.stickers = new Box[6][3][3];
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 3; k++) {
					Box box = new Box(0.95, 0.95, 0.02);
					if (i == 0 || i == 1) {
						// U or D.
						box.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
						box.setTranslateX((j - 1));
						box.setTranslateY(-(1.5 * (2 * i - 1))); // Translate.
						box.setTranslateZ((k - 1));
						box.setMaterial((i == 0) ? materialUp : materialDown);
					} else if (i == 2 || i == 3) {
						// L or R.
						box.getTransforms().add(new Rotate(90, Rotate.Y_AXIS));
						box.setTranslateX((1.5 * (2 * i - 5))); // Translate.
						box.setTranslateY(-(j - 1));
						box.setTranslateZ((k - 1));
						box.setMaterial((i == 2) ? materialRight : materialLeft);
					} else {
						// B or F.
						// No need to rotate.
						box.setTranslateX((j - 1));
						box.setTranslateY(-(k - 1));
						box.setTranslateZ((1.5 * (2 * i - 9))); // Translate.
						box.setMaterial((i == 4) ? materialFront : materialBack);
					}
					this.stickers[i][j][k] = box;
					this.cubeGroup.getChildren().add(box);
				}
			}
		}
	}

	private void setupPositioning() {
		this.cubeGroup.getTransforms().addAll(new Rotate(150, Rotate.X_AXIS));
		this.cubeGroup.setTranslateZ(15);
		// Make the cube spin over time.
		this.cubeGroup.setRotationAxis(Rotate.Y_AXIS);
		KeyValue cubeYNoRot = new KeyValue(this.cubeGroup.rotateProperty(), 0);
		KeyValue cubeYFullRot = new KeyValue(this.cubeGroup.rotateProperty(), -360);
		Timeline cubeSpinAnimation = new Timeline(
				new KeyFrame(Duration.millis(0), cubeYNoRot),
				new KeyFrame(Duration.millis(12000), cubeYFullRot));
		cubeSpinAnimation.setCycleCount(Timeline.INDEFINITE);
		cubeSpinAnimation.play();
	}

	private void setupScene() {
		this.subScene = new SubScene(this.cubeEnvironment, this.width, this.height, true, SceneAntialiasing.BALANCED);
		this.camera = new PerspectiveCamera(true);

		this.subScene.setCamera(this.camera);
		this.cubeEnvironment.getChildren().add(this.camera);
	}

}
