
public class SaveState {
	
	private int[][] s;
	private int row;
	private int col;
	private int[] poss;

	
	public SaveState(int[][] state, int row, int col, int[] poss) {
		copyState(state);
		this.row = row;
		this.col = col;
		this.poss = poss;
	}
	
	private void copyState(int[][] state) {
		s = new int[9][9];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				this.s[i][j] = state[i][j];
			}
		}
	}

	public int[][] getState() {
		return s;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public int[] getPoss() {
		return poss;
	}

}
