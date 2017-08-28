package timerfx.cubies;

import javafx.scene.paint.PhongMaterial;

public abstract class Cubie {
	// This is the superclass.
	// Included to show inheritance and polymorphism.

	protected int[] coords;
	protected String color;
	protected PhongMaterial material;
	protected int[] renderCoords = {-1, -1, -1};
	// To store colors:
	// I will use 'Color' cubies

	public void moveTo(int[] coords) {
		this.coords = coords;
		this.update();
	}
	
	public void update() {
		// Need to check which face we are and update coords for the cube render.
		if (this.coords[0] == 2) {
			// R
			this.renderCoords[0] = 2;
			this.renderCoords[1] = this.coords[1] + 1;
			this.renderCoords[2] = this.coords[2] + 1;
		} else if (this.coords[0] == -2) {
			// L
			this.renderCoords[0] = 3;
			this.renderCoords[1] = this.coords[1] + 1;
			this.renderCoords[2] = this.coords[2] + 1;
		} else if (this.coords[1] == 2) {
			// U
			this.renderCoords[0] = 0;
			this.renderCoords[1] = this.coords[0] + 1;
			this.renderCoords[2] = this.coords[2] + 1;
		} else if (this.coords[1] == -2) {
			// D
			this.renderCoords[0] = 1;
			this.renderCoords[1] = this.coords[0] + 1;
			this.renderCoords[2] = this.coords[2] + 1;
		} else if (this.coords[2] == 2) {
			// F
			this.renderCoords[0] = 4;
			this.renderCoords[1] = this.coords[0] + 1;
			this.renderCoords[2] = this.coords[1] + 1;
		} else if (this.coords[2] == -2) {
			this.renderCoords[0] = 5;
			this.renderCoords[1] = this.coords[0] + 1;
			this.renderCoords[2] = this.coords[1] + 1;
		} else {
			this.renderCoords = new int[] {-1, -1, -1};
		}
	}
	
	public int[] getRenderCoords() {
		return this.renderCoords;
	}

	public int[] getCoords() {
		return this.coords;
	}

	public void testCoords() {
		System.out.println("(" + this.coords[0] + "," + this.coords[1] + "," + this.coords[2] + ")");
	}

	public String getColor() {
		return this.color;
	}
	
	public PhongMaterial getMaterial() {
		return this.material;
	}
	
	public int[] getNewCoordsAfterMove(char move) {
		// This is going to be a long one...
		switch (move) {
			case 'R':
				if (this.coords[0] < 1) {
					return this.coords;
				}
				switch (this.coords[0]) {
					case 1:
						switch (this.coords[1]) {
							case -2:
								switch (this.coords[2]) {
									case -1:
										// RDB(D) -> RDF(F)
										return new int[] {1,-1,2};
									case 0:
										// RD(D) -> RF(F)
										return new int[] {1,0,2};
									case 1:
										// RDF(D) -> RUF(F)
										return new int[] {1,1,2};
								}
							case -1:
								switch (this.coords[2]) {
									case -2:
										// RDB(B) -> RDF(D)
										return new int[] {1,-2,1};
									case -1:
										// RDB -> RDF
										return new int[] {1,-1,1};
									case 0:
										// RD -> RF
										return new int[] {1,0,1};
									case 1:
										// RDF -> RUF
										return new int[] {1,1,1};
									case 2:
										// RDF(F) -> RUF(U)
										return new int[] {1,2,1};
								}
							case 0:
								switch (this.coords[2]) {
									case -2:
										// RB(B) -> RD(D)
										return new int[] {1,-2,0};
									case -1:
										// RB -> RD
										return new int[] {1,-1,0};
									case 1:
										// RF -> RU
										return new int[] {1,1,0};
									case 2:
										// RF(F) -> RU(U)
										return new int[] {1,2,0};
								}
							case 1:
								switch (this.coords[2]) {
									case -2:
										// RUB(B) -> RDB(D)
										return new int[] {1,-2,-1};
									case -1:
										// RUB -> RDB
										return new int[] {1,-1,-1};
									case 0:
										// RU -> RB
										return new int[] {1,0,-1};
									case 1:
										// RUF -> RUB
										return new int[] {1,1,-1};
									case 2:
										// RUF(F) -> RUB(U)
										return new int[] {1,2,-1};
								}
							case 2:
								switch (this.coords[2]) {
									case -1:
										// RUB(U) -> RDB(B)
										return new int[] {1,-1,-2};
									case 0:
										// RU(U) -> RB(B)
										return new int[] {1,0,-2};
									case 1:
										// RUF(U) -> RUB(B)
										return new int[] {1,1,-2};
								}
						}
					case 2:
						switch (this.coords[1]) {
							case -1:
								switch (this.coords[2]) {
									case -1:
										// RDB(R) -> RDF(R)
										return new int[] {2,-1,1};
									case 0:
										// RD(R) -> RF(R)
										return new int[] {2,0,1};
									case 1:
										// RDF(R) -> RUF(R)
										return new int[] {2,1,1};
								}
							case 0:
								switch (this.coords[2]) {
									case -1:
										// RB(R) -> RD(R)
										return new int[] {2,-1,0};
									case 1:
										// RF(R) -> RU(R)
										return new int[] {2,1,0};
								}
							case 1:
								switch (this.coords[2]) {
									case -1:
										// RUB(R) -> RDB(R)
										return new int[] {2,-1,-1};
									case 0:
										// RU(R) -> RB(R)
										return new int[] {2,0,-1};
									case 1:
										// RUF(R) -> RUB(R)
										return new int[] {2,1,-1};
								}
						}
				}
			case 'U':
				if (this.coords[1] < 1) {
					return this.coords;
				}
				switch (this.coords[1]) {
					case 1:
						switch (this.coords[0]) {
							case -2:
								switch (this.coords[2]) {
									case -1:
										// LUB(L) -> RUB(B)
										return new int[] {1,1,-2};
									case 0:
										// LU(L) -> UB(B)
										return new int[] {0,1,-2};
									case 1:
										// LUF(L) -> LUB(B)
										return new int[] {-1,1,-2};
								}
							case -1:
								switch (this.coords[2]) {
									case -2:
										// LUB(B) -> RUB(R)
										return new int[] {2,1,-1};
									case -1:
										// LUB -> RUB
										return new int[] {1,1,-1};
									case 0:
										// LU -> UB
										return new int[] {0,1,-1};
									case 1:
										// LUF -> LUB
										return new int[] {-1,1,-1};
									case 2:
										// LUF(F) -> LUB(L)
										return new int[] {-2,1,-1};
								}
							case 0:
								switch (this.coords[2]) {
									case -2:
										// UB(B) -> UR(R)
										return new int[] {2,1,0};
									case -1:
										// UB -> UR
										return new int[] {1,1,0};
									case 1:
										// UF -> UL
										return new int[] {-1,1,0};
									case 2:
										// UF(F) -> UL(L)
										return new int[] {-2,1,0};
								}
							case 1:
								switch (this.coords[2]) {
									case -2:
										// RUB(B) -> RUF(R)
										return new int[] {2,1,1};
									case -1:
										// RUB -> RUF
										return new int[] {1,1,1};
									case 0:
										// RU -> UF
										return new int[] {0,1,1};
									case 1:
										// RUF -> LUF
										return new int[] {-1,1,1};
									case 2:
										// RUF(F) -> LUF(L)
										return new int[] {-2,1,1};
								}
							case 2:
								switch (this.coords[2]) {
									case -1:
										// RUB(R) -> RUF(F)
										return new int[] {1,1,2};
									case 0:
										// RU(R) -> UF(F)
										return new int[] {0,1,2};
									case 1:
										// RUF(R) -> LUF(F)
										return new int[] {-1,1,2};
								}
						}
					case 2:
						switch (this.coords[0]) {
							case -1:
								switch (this.coords[2]) {
									case -1:
										// LUB(U) -> RUB(U)
										return new int[] {1,2,-1};
									case 0:
										// LU(U) -> UB(U)
										return new int[] {0,2,-1};
									case 1:
										// LUF(U) -> LUB(U)
										return new int[] {-1,2,-1};
								}
							case 0:
								switch (this.coords[2]) {
									case -1:
										// UB(U) -> RU(U)
										return new int[] {1,2,0};
									case 1:
										// UF(U) -> LU(U)
										return new int[] {-1,2,0};
								}
							case 1:
								switch (this.coords[2]) {
									case -1:
										// RUB(U) -> RUF(U)
										return new int[] {1,2,1};
									case 0:
										// RU(U) -> UF(U)
										return new int[] {0,2,1};
									case 1:
										// RUF(U) -> LUF(U)
										return new int[] {-1,2,1};
								}
						}
				}
			case 'F':
				if (this.coords[2] < 1) {
					return this.coords;
				}
				switch (this.coords[2]) {
					case 1:
						switch (this.coords[0]) {
							case -2:
								switch (this.coords[1]) {
									case -1:
										// LDF(L) -> LUF(U)
										return new int[] {-1,2,1};
									case 0:
										// LF(L) -> UF(U)
										return new int[] {0,2,1};
									case 1:
										//LUF(L) -> RUF(U)
										return new int[] {1,2,1};
								}
							case -1:
								switch (this.coords[1]) {
									case -2:
										// LDF(D) -> LUF(L)
										return new int[] {-2,1,1};
									case -1:
										// LDF -> LUF
										return new int[] {-1,1,1};
									case 0:
										// LF -> UF
										return new int[] {0,1,1};
									case 1:
										// LUF -> RUF
										return new int[] {1,1,1};
									case 2:
										// LUF(U) -> RUF(R)
										return new int[] {2,1,1};
								}
							case 0:
								switch (this.coords[1]) {
									case -2:
										// DF(D) -> LF(L)
										return new int[] {-2,0,1};
									case -1:
										// DF -> LF
										return new int[] {-1,0,1};
									case 1:
										// UF -> RF
										return new int[] {1,0,1};
									case 2:
										// UF(U) -> RF(U)
										return new int[] {2,0,1};
								}
							case 1:
								switch (this.coords[1]) {
									case -2:
										// RDF(D) -> LDF(L)
										return new int[] {-2,-1,1};
									case -1:
										// RDF -> LDF
										return new int[] {-1,-1,1};
									case 0:
										// RF -> DF
										return new int[] {0,-1,1};
									case 1:
										// RUF -> RDF
										return new int[] {1,-1,1};
									case 2:
										// RUF(U) -> RDF(R)
										return new int[] {2,-1,1};
								}
							case 2:
								switch (this.coords[1]) {
									case -1:
										// RDF(R) -> LDF(D)
										return new int[] {-1,-2,1};
									case 0:
										// RF(R) -> DF(D)
										return new int[] {0,-2,1};
									case 1:
										// RUF(R) -> RDF(D)
										return new int[] {1,-2,1};
								}
						}
					case 2:
						switch (this.coords[0]) {
							case -1:
								switch (this.coords[1]) {
									case -1:
										// LDF(F) -> LUF(F)
										return new int[] {-1,1,2};
									case 0:
										// LF(F) -> UF(F)
										return new int[] {0,1,2};
									case 1:
										// LUF(F) -> RUF(F)
										return new int[] {1,1,2};
								}
							case 0:
								switch (this.coords[1]) {
									case -1:
										// DF(F) -> LF(F)
										return new int[] {-1,0,2};
									case 1:
										// UF(F) -> RF(F)
										return new int[] {1,0,2};
								}
							case 1:
								switch (this.coords[1]) {
									case -1:
										// RDF(F) -> LDF(F)
										return new int[] {-1,-1,2};
									case 0:
										// RF(F) -> DF(F)
										return new int[] {0,-1,2};
									case 1:
										// RUF(F) -> RDF(F)
										return new int[] {1,-1,2};
								}
						}
				}
			// This is taking soooooooo long to write...
			case 'B':
				if (this.coords[2] > -1) {
					return this.coords;
				}
				switch (this.coords[2]) {
					case -1:
						switch (this.coords[0]) {
							case -2:
								switch (this.coords[1]) {
									case -1:
										// LDB(L) -> RDB(D)
										return new int[] {1,-2,-1};
									case 0:
										// LB(L) -> DB(D)
										return new int[] {0,-2,-1};
									case 1:
										// LUB(L) -> LDB(D)
										return new int[] {-1,-2,-1};
								}
							case -1:
								switch (this.coords[1]) {
									case -2:
										// LDB(D) -> RDB(R)
										return new int[] {2,-1,-1};
									case -1:
										// LDB -> RDB
										return new int[] {1,-1,-1};
									case 0:
										// LB -> DB
										return new int[] {0,-1,-1};
									case 1:
										// LUB -> LDB
										return new int[] {-1,-1,-1};
									case 2:
										// LUB(U) -> LDB(L)
										return new int[] {-2,-1,-1};
								}
							case 0:
								switch (this.coords[1]) {
									case -2:
										// DB(D) -> RB(R)
										return new int[] {2,0,-1};
									case -1:
										// DB -> RB
										return new int[] {1,0,-1};
									case 1:
										// UB -> LB
										return new int[] {-1,0,-1};
									case 2:
										// UB(U) -> LB(L)
										return new int[] {-2,0,-1};
								}
							case 1:
								switch (this.coords[1]) {
									case -2:
										// RDB(D) -> RUB(R)
										return new int[] {2,1,-1};
									case -1:
										// RDB -> RUB
										return new int[] {1,1,-1};
									case 0:
										// RB -> UB
										return new int[] {0,1,-1};
									case 1:
										// RUB -> LUB
										return new int[] {-1,1,-1};
									case 2:
										// RUB(U) -> LUB(L)
										return new int[] {-2,1,-1};
								}
							case 2:
								switch (this.coords[1]) {
									case -1:
										// RDB(R) -> RUB(U)
										return new int[] {1,2,-1};
									case 0:
										// RB(R) -> UB(U)
										return new int[] {0, 2,-1};
									case 1:
										// RUB(R) -> LUB(U)
										return new int[] {-1,2,-1};
								}
						}
					case -2:
						switch (this.coords[0]) {
							case -1:
								switch (this.coords[1]) {
									case -1:
										// LDB(B) -> RDB(B)
										return new int[] {1,-1,-2};
									case 0:
										// LB(B) -> DB(B)
										return new int[] {0,-1,-2};
									case 1:
										// LUB(B) -> LDB(B)
										return new int[] {-1,-1,-2};
								}
							case 0:
								switch (this.coords[1]) {
									case -1:
										// DB(B) -> RB(B)
										return new int[] {1,0,-2};
									case 1:
										// UB(B) -> LB(B)
										return new int[] {-1,0,-2};
								}
							case 1:
								switch (this.coords[1]) {
									case -1:
										// RDB(B) -> RUB(B)
										return new int[] {1,1,-2};
									case 0:
										// RB(B) -> UB(B)
										return new int[] {0,1,-2};
									case 1:
										// RUB(B) -> LUB(B)
										return new int[] {-1,1,-2};
								}
						}
				}
			case 'D':
				if (this.coords[1] > -1) {
					return this.coords;
				}
				switch (this.coords[1]) {
					case -1:
						switch (this.coords[0]) {
							case -2:
								switch (this.coords[2]) {
									case -1:
										// LDB(L) -> LDF(F)
										return new int[] {-1,-1,2};
									case 0:
										// LD(L) -> DF(F)
										return new int[] {0,-1,2};
									case 1:
										// LDF(L) -> RDF(F)
										return new int[] {1,-1,2};
								}
							case -1:
								switch (this.coords[2]) {
									case -2:
										// LDB(B) -> LDF(L)
										return new int[] {-2,-1,1};
									case -1:
										// LDB -> LDF
										return new int [] {-1,-1,1};
									case 0:
										// LD -> DF
										return new int[] {0,-1,1};
									case 1:
										// LDF -> RDF
										return new int[] {1,-1,1};
									case 2:
										// LDF(F) -> RDF(R)
										return new int[] {2,-1,1};
								}
							case 0:
								switch (this.coords[2]) {
									case -2:
										// DB(B) -> LD(L)
										return new int[] {-2,-1,0};
									case -1:
										// DB -> LD
										return new int[] {-1,-1,0};
									case 1:
										// DF -> RD
										return new int[] {1,-1,0};
									case 2:
										// DF(F) -> RD(R)
										return new int[] {2,-1,0};
								}
							case 1:
								switch (this.coords[2]) {
									case -2:
										// RDB(B) -> LDB(L)
										return new int[] {-2,-1,-1};
									case -1:
										// RDB -> LDB
										return new int[] {-1,-1,-1};
									case 0:
										// RD -> DB
										return new int[] {0,-1,-1};
									case 1:
										// RDF -> RDB
										return new int[] {1,-1,-1};
									case 2:
										// RDF(F) -> RDB(R)
										return new int[] {2,-1,-1};
								}
							case 2:
								switch (this.coords[2]) {
									case -1:
										// RDB(R) -> LDB(B)
										return new int[] {-1,-1,-2};
									case 0:
										// RD(R) -> DB(B)
										return new int[] {0,-1,-2};
									case 1:
										// RDF(R) -> RDB(B)
										return new int[] {1,-1,-2};
								}
						}
					case -2:
						switch (this.coords[0]) {
							case -1:
								switch (this.coords[2]) {
									case -1:
										// LDB(D) -> LDF(D)
										return new int[] {-1,-2,1};
									case 0:
										// LD(D) -> DF(D)
										return new int[] {0,-2,1};
									case 1:
										// LDF(D) -> RDF(D)
										return new int[] {1,-2,1};
								}
							case 0:
								switch (this.coords[2]) {
									case -1:
										// DB(D) -> LD(D)
										return new int[] {-1,-2,0};
									case 1:
										// DF(D) -> RD(D)
										return new int[] {1,-2,0};
								}
							case 1:
								switch (this.coords[2]) {
									case -1:
										// RDB(D) -> LDB(D)
										return new int[] {-1,-2,-1};
									case 0:
										// RD(D) -> DB(D)
										return new int[] {0,-2,-1};
									case 1:
										// RDF(D) -> RDB(D)
										return new int[] {1,-2,-1};
								}
						}
				}
			case 'L':
				if (this.coords[0] > -1) {
					return this.coords;
				}
				switch (this.coords[0]) {
					case -1:
						switch (this.coords[1]) {
							case -2:
								switch (this.coords[2]) {
									case -1:
										// LDB(D) -> LUB(B)
										return new int[] {-1,1,-2};
									case 0:
										// LD(D) -> LB(B)
										return new int[] {-1,0,-2};
									case 1:
										// LDF(D) -> LDB(B)
										return new int[] {-1,-1,-2};
								}
							case -1:
								switch (this.coords[2]) {
									case -2:
										// LDB(B) -> LUB(U)
										return new int[] {-1,2,-1};
									case -1:
										// LDB -> LUB
										return new int[] {-1,1,-1};
									case 0:
										// LD -> LB
										return new int[] {-1,0,-1};
									case 1:
										// LDF -> LDB
										return new int[] {-1,-1,-1};
									case 2:
										// LDF(F) -> LDB(D)
										return new int[] {-1,-2,-1};
								}
							case 0:
								switch (this.coords[2]) {
									case -2:
										// LB(B) -> LU(U)
										return new int[] {-1,2,0};
									case -1:
										// LB -> LU
										return new int[] {-1,1,0};
									case 1:
										// LF -> LD
										return new int[] {-1,-1,0};
									case 2:
										// LF(F) -> LD(D)
										return new int[] {-1,-2,0};
								}
							case 1:
								switch (this.coords[2]) {
									case -2:
										// LUB(B) -> LUF(U)
										return new int[] {-1,2,1};
									case -1:
										// LUB -> LUF
										return new int[] {-1,1,1};
									case 0:
										// LU -> LF
										return new int[] {-1,0,1};
									case 1:
										// LUF -> LDF
										return new int[] {-1,-1,1};
									case 2:
										// LUF(F) -> LDF(D)
										return new int[] {-1,-2,1};
								}
							case 2:
								switch (this.coords[2]) {
									case -1:
										// LUB(U) -> LUF(F)
										return new int[] {-1,1,2};
									case 0:
										// LU(U) -> LF(F)
										return new int[] {-1,0,2};
									case 1:
										// LUF(U) -> LDF(F)
										return new int[] {-1,-1,2};
								}
						}
					case -2:
						switch (this.coords[1]) {
							case -1:
								switch (this.coords[2]) {
									case -1:
										// LDB(L) -> LUB(L)
										return new int[] {-2,1,-1};
									case 0:
										// LD(L) -> LB(L)
										return new int[] {-2,0,-1};
									case 1:
										// LDF(L) -> LDB(L)
										return new int[] {-2,-1,-1};
								}
							case 0:
								switch (this.coords[2]) {
									case -1:
										// LB(L) -> LU(L)
										return new int[] {-2,1,0};
									case 1:
										// LF(L) -> LD(L)
										return new int[] {-2,-1,0};
								}
							case 1:
								switch (this.coords[2]) {
									case -1:
										// LUB(L) -> LUF(L)
										return new int[] {-2,1,1};
									case 0:
										// LU(L) -> LF(L)
										return new int[] {-2,0,1};
									case 1:
										// LUF(L) -> LDF(L)
										return new int[] {-2,-1,1};
								}
								// DONE.
						}
				}
			
			default:
				// Space key (Hopefully).
				return this.coords;
		}
	}

}
