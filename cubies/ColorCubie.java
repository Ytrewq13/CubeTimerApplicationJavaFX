package timerfx.cubies;

import timerfx.TimerFX;

public class ColorCubie extends Cubie {

	public ColorCubie(String col, int[] coords) {
		this.color = col;
		switch (this.color) {
			case "white":
				this.material = TimerFX.materialUp;
				break;
			case "yellow":
				this.material = TimerFX.materialDown;
				break;
			case "red":
				this.material = TimerFX.materialRight;
				break;
			case "orange":
				this.material = TimerFX.materialLeft;
				break;
			case "green":
				this.material = TimerFX.materialFront;
				break;
			case "blue":
				this.material = TimerFX.materialBack;
				break;
			default:
				// Something has gone horribly wrong.
				throw new IllegalArgumentException("Invalid color for ColorCubie in ColorCubie constructor");
		}
		this.coords = coords;
		this.update();
	}

}
