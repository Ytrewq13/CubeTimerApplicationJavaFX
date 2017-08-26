package timerfx;

import java.util.ArrayList;
import timerfx.cubies.*;

public class Cube {

	// The pieces.
	private Cubie[] pieces;

	private boolean solved;

	public Cube() {
		this.solved = true;
		this.pieces = new Cubie[68];
		int index = 0;
		// Generate the real Cubies.
		// This is as redundant as Parapa the Rapper.
		// TODO(N'T): get rid of the real cubies.
		// I will end up representing the entire cube as 54 color cubies.
		// But I can't be bothered refactoring the code right now.
		// EDIT: I will not get rid of the real cubies.
		// They may not be needed, but they allow the use of polymorphism,
		// And apparently I have to use polymorphism to show that
		// I know what I'm doing (I don't).
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					int[] coords = {x, y, z};
					if (x == 0 && y == 0 || x == 0 && z == 0 || y == 0 && z == 0) {
						// we have at least 2 0's -- we only want 1 (max).
						// this happens when we have a center or the core.
						// We want to do nothing here.
					} else if (x == 0 || y == 0 || z == 0) {
						// We have an edge.
						this.pieces[index] = new Edge(coords);
						index++;
					} else {
						// We have a corner.
						this.pieces[index] = new Corner(coords);
						index++;
					}
				}
			}
		}
		// Generate the color cubies.
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				if (!(x == 0 && y == 0)) {
					int[] up = {x, 2, y};
					int[] down = {x, -2, y};
					int[] right = {2, x, y};
					int[] left = {-2, x, y};
					int[] front = {x, y, 2};
					int[] back = {x, y, -2};
					if (TimerFX.debug) {System.out.println(up[0] + "," + up[1] + "," + up[2]);}
					this.pieces[index] = new ColorCubie("white", up);
					index++;
					this.pieces[index] = new ColorCubie("yellow", down);
					index++;
					this.pieces[index] = new ColorCubie("red", right);
					index++;
					this.pieces[index] = new ColorCubie("orange", left);
					index++;
					this.pieces[index] = new ColorCubie("green", front);
					index++;
					this.pieces[index] = new ColorCubie("blue", back);
					index++;
				}
			}
		}
		if (TimerFX.debug) {
			this.testCoords();
			System.out.println();

			for (Cubie piece : this.pieces) {
				String color = piece.getColor();
				int[] coords = piece.getCoords();

				System.out.print(coords[0] + "," + coords[1] + "," + coords[2] + "  ==>  ");
				System.out.println(color);
			}
			int[] counter = new int[6];
			for (Cubie piece : this.pieces) {
				int[] coords = piece.getCoords();
				if (coords[0] == 2) {
					counter[0]++;
				} else if (coords[0] == -2) {
					counter[1]++;
				}
				if (coords[1] == 2) {
					counter[2]++;
				} else if (coords[1] == -2) {
					counter[3]++;
				}
				if (coords[2] == 2) {
					counter[4]++;
				} else if (coords[2] == -2) {
					counter[5]++;
				}
			}
			for (int i : counter) {
				System.out.println(i);
			}
		}
		// I tested every single move and all primes, which are all correct.
		// Should still test using multiple moves at once.
		// I will do that once I have a 3D render of the cube.

	}

	// Method to test parity of coordinates (Manually).
	public void testCoords() {
		for (Cubie piece : this.pieces) {
			piece.testCoords();
		}
	}

	// Method to apply multiple moves to the cube.
	public boolean moveSet(String set) {
		boolean success = true;
		ArrayList<String> moves = new ArrayList<String>();
		for (int i = 0; i < set.length(); i++) {
			char c = set.charAt(i);
			if (Character.isDigit(c)) {
				// Want to re-add the previous character to the list.
				c = set.charAt(i - 1);
			} else if (c == '\'') {
				c = set.charAt(i - 1);
				// Need to re-add the character twice more.
				moves.add(Character.toString(c));
			}
			moves.add(Character.toString(c));
		}
		for (int i = 0; i < moves.size(); i++) {
			if (TimerFX.debug) {System.out.println(moves.get(i));}
			moves.set(i, moves.get(i).toUpperCase());
		}
		System.out.println();
		for (String move : moves) {
			if (TimerFX.debug) {System.out.println(move);}
			if (!this.rotate(move.charAt(0))) {
				success = false;
			}
		}
		return success;
	}

	// Method to rotate one face 90 degrees clockwise.
	public boolean rotate(char face) {
		// Iterate through all pieces.
		for (Cubie piece : this.pieces) {
			piece.moveTo(piece.getNewCoordsAfterMove(face));
		}
		return true;
	}

	public static int[] transform(int[] coords) {
		// Multiply by the matrix:
		//   [0  1]
		//   [-1 0]
		int x = coords[0];
		int y = coords[1];
		// Multiply by the matrix.
		int newX = -y;
		int newY = x;
		// Form the new coords.
		int[] newLoc = {newX, newY};
		// Return the new location.
		return newLoc;
	}

	// Method to return boolean of whether the cube is solved or not.
	public boolean isSolved() {
		boolean solved = true;
		//TODO: work out if cube is solved.
		return true;
	}

}
