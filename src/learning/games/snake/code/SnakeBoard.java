package learning.games.snake.code;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;

public class SnakeBoard extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8945557599697976352L;
	private int SIZE;
	private int applex;
	private int appley;
	private int direction = 1;
	private boolean donePaint = true;
	private Random rand = new Random();
	/*
	 * 1=UP 2=RIGHT 3=DOWN 4=LEFT
	 */
	private int headx = 4;
	private int heady = 4;
	private ArrayList<SnakeBody> snake = new ArrayList<SnakeBody>();
	private boolean gotApple = false;
	private boolean inGame = false;
	private boolean visible = false;
	private boolean tooLong;

	public SnakeBoard() {
		this(16);
	}

	public SnakeBoard(int size) {
		if (size < 4) {
			// Size should be greater than a 4x4
			throw new RuntimeException("Invalid Size");
		}
		this.SIZE = size;
		setBackground(Color.BLACK);
		setPreferredSize(new Dimension((SIZE * 20 > 320 ? SIZE * 20 : 320), (SIZE * 20 > 320 ? SIZE * 20 : 320)));
		clear();

	}

	// Creates a new apple on the board
	private void generateApple() {
		applex = rand.nextInt(SIZE);
		appley = rand.nextInt(SIZE);
		if (applex == headx && appley == heady) {
			generateApple();
			return;
		}
		for (SnakeBody a : snake) {
			if (a.getX() == applex && a.getY() == appley) {
				generateApple();
				return;
			}
		}
	}

	// Resets the board for the next game
	public void clear() {
		tooLong = false;
		headx = SIZE / 2;
		heady = SIZE / 2;
		generateApple();
		inGame = true;
		snake.clear();
		snake.add(new SnakeBody(headx, heady - 1));
		direction = 1;
	}
	/**
	 * Returns smaller area 
	 * @return
	 */
	public double[] info2() {
		double[] info = new double[6];
		if (headx > SIZE - 1 || headx < 0 || heady > SIZE - 1 || heady < 0) {

			return info;
		}
		double disY = (double) (appley - heady);
		double disX = (double) (applex - headx);
		double angle = Math.atan(disY / disX);
		if (disY > 0 && disX < 0) {
			angle += Math.PI;
		} else if (disY < 0 && disX < 0) {
			angle += Math.PI;
		} else if (disY < 0) {
			angle += 2 * Math.PI;
		}
		info[0] = angle;
		info[1] = direction;
		double[][] grid = getGrid();
		info[2] = (headx - 1 < 0 || grid[headx - 1][heady] != 0 ? headx - 1 < 0 ? -1 : -.5
				: grid[headx - 1][heady] == 10 ? 1 : 0);
		info[3] = (headx + 1 >= SIZE || grid[headx + 1][heady] != 0 ? headx + 1 >= SIZE ? -1 : -.5
				: grid[headx + 1][heady] == 10 ? 1 : 0);
		info[4] = (heady - 1 < 0 || grid[headx][heady - 1] != 0 ? heady - 1 < 0 ? -1 : -.5
				: grid[headx][heady - 1] == 10 ? 1 : 0);
		info[5] = (heady + 1 >= SIZE || grid[headx][heady + 1] != 0 ? heady + 1 >= SIZE ? -1 : -.5
				: grid[headx][heady + 1] == 10 ? 1 : 0);
		return info;
	}
	/**
	 * gives the snake a two block vision field
	 * @return
	 */
	public double[] info() {
		double[] info = new double[27];
		if (headx > SIZE - 1 || headx < 0 || heady > SIZE - 1 || heady < 0) {

			return info;
		}
		double disY = (double) (appley - heady);
		double disX = (double) (applex - headx);
		double angle = Math.atan(disY / disX);
		if (disY > 0 && disX < 0) {
			angle += Math.PI;
		} else if (disY < 0 && disX < 0) {
			angle += Math.PI;
		} else if (disY < 0) {
			angle += 2 * Math.PI;
		}
		info[0] = angle;
		info[1] = direction;
		int x = headx-2;
		int y = heady-2;
		double[][] grid = getGrid();
		int a=1;
		for(int i =0; i<25;i++) {
			a++;
			if(i%5==0) {
				y++;
				x=headx-2;
			}
			if(y>=SIZE||y<0||x>=SIZE||x<0) {
				info[a]=-2;
				x++;
				continue;
			}
			if(x==headx&&y==heady) {
				info[a]=5;
				x++;
				continue;
			}
			if(grid[x][y]==10) {
				info[a]=5;
				x++;
				continue;
			}
			if(grid[x][y]==-10) {
				info[a]=-2;
				x++;
				continue;
			}
		}
		return info;
	}

	// 1 Forward
	// 2 Right
	// 3 Left
	public void move(int dir) {
		gotApple = false;

		if (dir == 2) {
			if (direction == 4) {
				direction = 1;
			} else {
				direction++;
			}
		}
		if (dir == 3) {
			if (direction == 1) {
				direction = 4;
			} else {
				direction--;
			}
		} /*
			 * if(snake.size()==0) { direction=dir; }else {
			 * if(!(direction==1&&dir==3||direction==3&&dir==1||direction==2&&dir==4||
			 * direction==4&&dir==2)) { direction=dir; } }
			 */
		if (snake.size() > 0) {
			moveBody();
		}

		switch (this.direction) {
		case 1:
			heady++;
			break;
		case 2:
			headx++;
			break;
		case 3:
			heady--;
			break;
		case 4:
			headx--;
			break;
		}

		if (headx == applex && heady == appley) {
			generateApple();
			addPart();
			gotApple = true;
		} else {
			checkDead();
		}
		if (visible) {
			// only paint if the board is meant to be shown saves on resources
			if (donePaint) {

				repaint();

			}
		}

	}

	public boolean inGame() {
		return inGame;
	}

	public boolean tooLong() {
		return tooLong;
	}

	public boolean gotApple() {
		return gotApple;
	}

	private void checkDead() {
		if (headx > SIZE - 1 || headx < 0 || heady > SIZE - 1 || heady < 0) {
			inGame = false;
			return;
		}
		for (SnakeBody a : snake) {
			if (a.getX() == headx && a.getY() == heady) {
				inGame = false;
				return;
			}
		}
	}

	public double[][] getGrid() {
		double[][] grid = new double[SIZE][SIZE];

		grid[applex][appley] = 10;

		for (SnakeBody a : snake) {
			grid[a.getX()][a.getY()] = -10;
		}
		if (headx > SIZE - 1 || headx < 0 || heady > SIZE - 1 || heady < 0) {

		} else {
			grid[headx][heady] = -5 - (((double) direction) / 10.0);
		}
		return grid;
	}

	private void addPart() {
		if (snake.size() == 0) {
			snake.add(new SnakeBody(headx, heady));
			return;
		}
		snake.add(new SnakeBody(snake.get(snake.size() - 1).getX(), snake.get(snake.size() - 1).getY()));

	}

	private void moveBody() {
		snake.add(0, new SnakeBody(headx, heady));
		snake.remove(snake.size() - 1);
	}

	// Testing for performance of distance based rewards
	public double getDistance() {
		return Math.sqrt(Math.pow(headx - applex, 2) + Math.pow(heady - appley, 2));
	}

	@Override
	public void paintComponent(Graphics g) {
		donePaint = false;
		try {
			super.paintComponent(g);

			setBackground(Color.BLACK);
			g.setColor(Color.RED);
			g.fillRect(applex * 20, appley * 20, 20, 20);
			g.setColor(Color.GREEN);
			g.fillRect(headx * 20, heady * 20, 20, 20);

			for (SnakeBody a : snake) {
				g.fillRect(a.getX() * 20, a.getY() * 20, 20, 20);
			}
		} catch (Exception e) {
			// Fixes problem with concurrent painting and other weird things

		}
		donePaint = true;
	}

	public boolean good() {
		return snake.size() == 0;
	}

	public int getLength() {
		return snake.size();
	}

	public void visible() {
		visible = true;

	}
}
