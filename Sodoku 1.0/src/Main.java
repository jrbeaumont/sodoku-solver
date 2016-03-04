import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JWindow;


public class Main {
	
	private static JFrame mainWindow;
	private static ArrayList<ArrayList<JTextField>> grid;
	private static int[][] sod = new int[9][9];
	private static int[][][] poss;
	private static Stack<SaveState> stack = new Stack<>();
	private static boolean changed = true;
	private static boolean firstTime = true;

	public static void main(String[] args) {
		mainWindow = new JFrame();
		
		mainWindow.setSize(500, 500);
		mainWindow.setDefaultCloseOperation(mainWindow.EXIT_ON_CLOSE);
		mainWindow.setLayout(new FlowLayout());
		
		GridLayout g = new GridLayout();
		g.setRows(3);
		g.setColumns(3);
		JPanel gridPanel = new JPanel();
		gridPanel.setLayout(g);
		
		initGrid();
		
		JPanel buttonPanel = new JPanel();
		JButton go = new JButton("GO");
		goAction(go);
		JButton step = new JButton("STEP");
		stepAction(step);
		JButton clear = new JButton("Clear");
		clearAction(clear);
		buttonPanel.add(clear);
		buttonPanel.add(go);
		buttonPanel.add(step);
		
		mainWindow.add(buttonPanel);
		
		mainWindow.setVisible(true);
		
	}
	
	private static void clearAction(JButton clear) {
		
		clear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				firstTime = true;
				for (int i = 0; i < 9; i++) {
					for (int j = 0; j < 9; j++) {
						grid.get(i).get(j).setText("");
					}
				}
			}
			
		});
		
		
	}

	private static void stepAction(JButton step) {
		
		step.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (!changed) {
					guess();
				}
				changed = false;
				
				if (firstTime) {
					prepareSod();
					firstTime = false;
					validate();
				}
				
				
				findAndAddPossibles();
				outputChanges();
			}
			
		});
		
	}
	
	private static void goAction(JButton go) {
		go.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				while (!complete()) {
					if (!changed) {
						guess();
					}
					changed = false;
					
					if (firstTime) {
						prepareSod();
						firstTime = false;
					}
					validate();
					
					findAndAddPossibles();
					outputChanges();
				}
			}
			
		});
	}
	
	public static void findAndAddPossibles() {
		poss = new int[9][9][9];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (sod[i][j] == 0) {
					for (int k = 0; k < 9; k++) {
						if (sod[i][j] == 0) {
							if (!checkNum(i, j, k + 1)) {
								poss[i][j][k] = k + 1;
							}
						}
					}
					if (singlePoss(poss[i][j]) == 1) {
						sod[i][j] = getSinglePoss(poss[i][j]);
						changed = true;
					} else if (singlePoss(poss[i][j]) == -1) {
						returnToSave();
						return;
					}
				}
			}
		}
		
	}

	private static int singlePoss(int[] poss) {
		int p = 0;
		for (int i = 0; i < 9; i++) {
			if (poss[i] > 0) p++;
		}
		if (p == 1) {
			return p;
		} else if (p == 0) {
			return -1;
		}
		return 0;
	}

	private static int getSinglePoss(int[] poss) {
		for (int i = 0; i < 9; i++) {
			if (poss[i] > 0) return poss[i];
		}
		return 0;
	}
	protected static boolean complete() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (sod[i][j] == 0) {
					return false;
				}
			}
		}
		return true;
	}

	protected static void outputChanges() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (sod[i][j] > 0)
					grid.get(i).get(j).setText("" + sod[i][j]);
				else 
					grid.get(i).get(j).setText("");
			}
		}
		
	}

	protected static void guess() {
		int guess = 0;
		int i = 0, j = 0;
		for (i = 0; i < 9; i++) {
			for (j = 0; j < 9; j++) {
				for (int k = 0; k < 9; k++) {
					if (poss[i][j][k] > 0) {
						guess = poss[i][j][k];
						SaveState s = new SaveState(sod, i, j, poss[i][j]);
						stack.push(s);
						sod[i][j] = guess;
						return;
					}
				}
			}
		}
		
		
	}

	protected static void addSinglePoss() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				int p = 0, r = 0;
				if (sod[i][j] == 0) {
					for (int k = 0; k < 9; k++) {
						if (poss[i][j][k] > 0) {
							p++;
							r = poss[i][j][k];
						}
					}
					if (p == 1) {
						sod[i][j] = r;
						changed = true;
					} if (p == 0) {
						returnToSave();
						return;
					}
				}
			}
		}
	}


	private static void returnToSave() {
		SaveState s = stack.pop();
		sod = s.getState();
		int row = s.getRow();
		int col = s.getCol();
		int guess = 0;
		int poss[] = s.getPoss();
		for (int k = 0; k < 9; k++) {
			if (poss[k] > 0) {
				guess = poss[k];
				poss[k] = 0;
				break;
			}
		}
		if (guess != 0) {
			SaveState t = new SaveState(sod, row, col, poss);
			stack.push(t);
			sod[row][col] = guess;
		} else if (guess == 0) {
			returnToSave();
		}
		
	}

	protected static void findPossibles() {
		poss = new int[9][9][9];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				for (int k = 0; k < 9; k++) {
					if (sod[i][j] == 0) {
						if (!checkNum(i, j, k + 1)) {
							poss[i][j][k] = k + 1;
						}
					}
				}
			}
		}
		
	}

	private static boolean checkNum(int row, int col, int target) {
		//Check row
		for (int i = 0; i < 9; i++) {
			if (sod[i][col] == target) {
				return true;
			}
		}
		
		//Check column
		for (int i = 0; i < 9; i++) {
			if (sod[row][i] == target) {
				return true;
			}
		}
		
		//Check square
		int maxR = 0, minR = 0, maxC = 0, minC = 0;
		if (row < 3) { 
			maxR = 3;
			minR = 0;
		} else if (row >= 6) {
			maxR = 9;
			minR = 6;
		} else {
			maxR = 6;
			minR = 3;
		}
		
		if (col < 3) {
			maxC = 3;
			minC = 0;
		} else if (col >=6) {
			maxC = 9;
			minC = 6;
		} else {
			maxC = 6;
			minC = 3;
		}
		
		for (int i = minR; i < maxR; i++) {
			for (int j = minC; j < maxC; j++) {
				if (sod[i][j] == target)
					return true;
			}
		}
		
		return false;
	}

	protected static void prepareSod() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				sod[i][j] = 0;
			}
		}
		
	}

	protected static void validate() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				String t = grid.get(i).get(j).getText();
				if (t.length() > 1) {
					JOptionPane.showMessageDialog(mainWindow, "Error: One of the entries is too long");
					return;
				} else if (t.length() > 0 && (t.charAt(0) < '1' || t.charAt(0) > '9')) {
					JOptionPane.showMessageDialog(mainWindow, "Error: One entry is not a correct number (1 - 9)");
					return;
				} else if (t.length() > 0) {
					sod[i][j] = t.charAt(0) - 48;
				}
			}
		}
		
	}

	public static void initGrid() {
		
		GridLayout g = new GridLayout();
		g.setRows(3);
		g.setColumns(3);
		JPanel gridPanel = new JPanel();
		gridPanel.setLayout(g);
		
		
		grid = new ArrayList<>();
		//Store in rows in grid
		for (int i = 0; i < 9; i++) {
			ArrayList<JTextField> r = new ArrayList<>();
			for (int j = 0; j < 9; j++) {	
				JTextField t = new JTextField(1);
				r.add(t);
			}
			grid.add(r);
		}
		
		//Create visual grid
		//TopLeft
		JPanel tl = new JPanel();
		tl.setLayout(g);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				JTextField u = grid.get(i).get(j);
				tl.add(u);
			}
		}
		gridPanel.add(tl);
		
		//TopCentre
		JPanel tc = new JPanel();
		tc.setLayout(g);
		for (int i = 0; i < 3; i++) {
			for (int j = 3; j < 6; j++) {
				JTextField u = grid.get(i).get(j);
				tc.add(u);
			}
		}
		gridPanel.add(tc);
		
		//TopRight
		JPanel tr = new JPanel();
		tr.setLayout(g);
		for (int i = 0; i < 3; i++) {
			for (int j = 6; j < 9; j++) {
				JTextField u = grid.get(i).get(j);
				tr.add(u);
			}
		}
		gridPanel.add(tr);
		
		//CentreLeft
		JPanel cl = new JPanel();
		cl.setLayout(g);
		for (int i = 3; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				JTextField u = grid.get(i).get(j);
				cl.add(u);
			}
		}
		gridPanel.add(cl);
		
		//CentreCentre
		JPanel c = new JPanel();
		c.setLayout(g);
		for (int i = 3; i < 6; i++) {
			for (int j = 3; j < 6; j++) {
				JTextField u = grid.get(i).get(j);
				c.add(u);
			}
		}
		gridPanel.add(c);
		
		//CentreRight
		JPanel cr = new JPanel();
		cr.setLayout(g);
		for (int i = 3; i < 6; i++) {
			for (int j = 6; j < 9; j++) {
				JTextField u = grid.get(i).get(j);
				cr.add(u);
			}
		}
		gridPanel.add(cr);
		
		//BottomLeft
		JPanel bl = new JPanel();
		bl.setLayout(g);
		for (int i = 6; i < 9; i++) {
			for (int j = 0; j < 3; j++) {
				JTextField u = grid.get(i).get(j);
				bl.add(u);
			}
		}
		gridPanel.add(bl);
		
		//BottomCentre
		JPanel bc = new JPanel();
		bc.setLayout(g);
		for (int i = 6; i < 9; i++) {
			for (int j = 3; j < 6; j++) {
				JTextField u = grid.get(i).get(j);
				bc.add(u);
			}
		}
		gridPanel.add(bc);
		
		//BottomRight
		JPanel br = new JPanel();
		br.setLayout(g);
		for (int i = 6; i < 9; i++) {
			for (int j = 6; j < 9; j++) {
				JTextField u = grid.get(i).get(j);
				br.add(u);
			}
		}
		gridPanel.add(br);
		
		mainWindow.add(gridPanel);
	}

}
