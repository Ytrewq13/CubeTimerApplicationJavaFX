package timerfx;

import javafx.animation.AnimationTimer;
import static timerfx.TimerFX.startTimer;

public class TimerHandler {

	private static int inspectionTime = 15;

	private AnimationTimer inspection;
	private AnimationTimer timer;

	public TimerHandler() {

		// Define timers.
		this.inspection = new AnimationTimer() {
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
				TimerFX.layoutHandler.setCurrentTime(Long.toString(this.remainingSeconds));
				if (this.remainingSeconds == 0) {
					this.stop();
					startTimer();
				}
			}
		};

		this.timer = new AnimationTimer() {
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
				TimerFX.times.add(this.time / 1000f);
				TimerFX.timestamps.add(System.currentTimeMillis());
				// save leftover time not handled with the last update
			}

			@Override
			public void handle(long l) {
				// Do stuff here.
				long newTime = System.currentTimeMillis();
				this.time = newTime - this.startTime;
				TimerFX.layoutHandler.setCurrentTime(Float.toString(this.time / 1000f));
			}
		};
	}
	
	public void stopInspecting() {
		this.inspection.stop();
	}
	
	public void startInspecting() {
		this.inspection.start();
	}
	
	public void stopTiming() {
		this.timer.stop();
	}
	
	public void startTiming() {
		this.timer.start();
	}
}
