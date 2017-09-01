package timerfx;

public class ScrambleHandler {

	private final int scrambleDefaultLength = 20;
	private String scramble;
	
	public ScrambleHandler() {
		this.updateScramble();
	}
	
	public String scramble() {
		return this.scramble;
	}
	
	public void updateScramble() {
		this.scramble = this.makeScramble(this.scrambleDefaultLength);
	}

	private String makeScramble(int length) {
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
}
