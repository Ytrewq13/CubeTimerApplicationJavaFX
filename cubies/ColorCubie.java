package timerfx.cubies;

import timerfx.TimerFX;

public class ColorCubie extends Cubie {

	public ColorCubie(String col, int[] coords) {
		this.color = col;
		switch (this.color) {
			case "white":
				this.material = TimerFX.whiteMat;
				break;
			case "yellow":
				this.material = TimerFX.yellowMat;
				break;
			case "red":
				this.material = TimerFX.redMat;
				break;
			case "orange":
				this.material = TimerFX.orangeMat;
				break;
			case "green":
				this.material = TimerFX.greenMat;
				break;
			case "blue":
				this.material = TimerFX.blueMat;
				break;
			default:
				// Something has gone horribly wrong.
				throw new IllegalArgumentException("Invalid color for ColorCubie in ColorCubie constructor");
		}
		this.coords = coords;
		this.update();
	}

}
