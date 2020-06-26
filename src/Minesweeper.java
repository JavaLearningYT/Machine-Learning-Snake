import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Java Minesweeper Game
 *
 * Author: Jan Bodnar
 * Website: http://zetcode.com
 */

public class Minesweeper extends JFrame {
	public static int wins =0;
    private JLabel statusbar;
    public static Board board;
    public Minesweeper() {

        initUI();
    }

    private void initUI() {

        statusbar = new JLabel("");
        add(statusbar, BorderLayout.SOUTH);
        board = new Board(statusbar);
        add(board);

        setResizable(false);
        pack();

        setTitle("Minesweeper");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) throws IOException {
    	System.setProperty("sun.java2d.opengl","True");
    	Control.minesweeperGame();
    	
        EventQueue.invokeLater(() -> {

            Minesweeper ex = new Minesweeper();
            ex.setVisible(true);

        });
        System.out.println(wins);
    }
    
}