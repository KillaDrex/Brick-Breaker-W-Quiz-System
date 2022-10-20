/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyApp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Timer;

/**
 *
 * @author killa
 */
public class GamePanel extends javax.swing.JPanel implements ActionListener, KeyListener {
    private int WIDTH = 700, HEIGHT = 700;
    private Timer timer;    // handles the animation/redrawing of the game
    
    private int PADDLE_WIDTH = 100, PADDLE_HEIGHT = 20;
    private int[] paddlePos = new int[2]; // center of the paddle
    private int paddleVel; // current velocity of the paddle
    private int paddleSpeed = 3; // speed of the paddle when moving
    
    public GamePanel() {
        // properties
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(this);
        
        // every 8 milliseconds, the game is redrawn
        timer = new Timer(8, this); 
        timer.start();
        
        // start new game
        newGame();
    }    
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
        // draw background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        // draw paddle
        g.setColor(Color.WHITE);
        g.fillRect(paddlePos[0], paddlePos[1], PADDLE_WIDTH, PADDLE_HEIGHT);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();  // redraw the panel
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode() ) {
            case KeyEvent.VK_LEFT: // left-arrow key pressed, move paddle to the left
                movePaddleLeft();
            case KeyEvent.VK_RIGHT: // left-arrow key pressed, move paddle to the left
                movePaddleRight();
        } 
    }
    @Override
    public void keyTyped(KeyEvent e) {System.out.println("s");}
    @Override
    public void keyReleased(KeyEvent e) {}
    
    private void newGame() {
        // initialize paddle at the center of the canvas
        paddlePos[0] = WIDTH / 2 - PADDLE_WIDTH / 2;
        paddlePos[1] = HEIGHT - PADDLE_HEIGHT;
    }
    
    private void movePaddleLeft() {
        paddleVel = paddleSpeed * -1;
    }
    
    private void movePaddleRight() {
        paddleVel = paddleSpeed;
    }
}
