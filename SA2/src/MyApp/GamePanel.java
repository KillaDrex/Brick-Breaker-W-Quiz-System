/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyApp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;
import javax.swing.Timer;

/**
 *
 * @author killa
 */
public class GamePanel extends javax.swing.JPanel implements ActionListener, KeyListener, MouseListener {
    // panel properties
    private final int WIDTH = 700, HEIGHT = 700;

    // handles the redrawing of the game
    private Timer timer;
    
    // paddle
    private final int PADDLE_WIDTH = 95, PADDLE_HEIGHT = 15;
    private final int INITIAL_PADDLE_X_VEL = 9; // default paddle speed
    
    // center point of paddle // you only need to set paddle position y once
    private int paddlePosX, paddlePosY = HEIGHT - PADDLE_HEIGHT / 2;
    
    // velocity is tied to the game's redraw time
    private int paddleVelX = 0;
    
    private boolean[] isPaddleMoving = {false, false};
        
    // ball
    private final int BALL_RADIUS = 8;
    private final int[] INITIAL_BALL_VEL = {3, 4}; // default ball speed
    private final int BALL_VEL_CHANGE = 4; // this is the change in velocity of a ball when hitting a certain side of the paddle
    private int ballPosX, ballPosY; // center point of ball
    private int ballVelX = 0, ballVelY = 0; // velocity is tied to the game's redraw time
    
    // determines if a game session is currently ongoing
    private boolean ongoing = false;
    
    // bricks - a 2d array where each 1d array represents a brick
    // the elements in 1d array in order are: x1, y1
    private final int BRICK_WIDTH = 60, BRICK_HEIGHT = 30;
    private final int BRICK_SPACE = 3; // horizontal and vertical spaces between bricks
    private int[][] bricks;
    
    public GamePanel() {
        // properties
        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        
        // start a new game
        newGame();
        
        // redraws the game every 8 milliseconds
        timer = new Timer(8, this);
        timer.start();
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }
    
    @Override
    public void paint(Graphics g) {
        // draw background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        // draw paddle
        g.setColor(Color.WHITE);
        g.fillRect(paddlePosX - PADDLE_WIDTH / 2, paddlePosY - PADDLE_HEIGHT / 2, PADDLE_WIDTH, PADDLE_HEIGHT);
        
        // draw ball
        g.setColor(Color.RED);
        g.fillOval(ballPosX - BALL_RADIUS, ballPosY - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);    

        // draw bricks
        g.setColor(Color.WHITE);
        for (int i = 0; i < bricks.length; i++) {
            if (bricks[i][2] == 1)  // brick has not been broken yet, draw it
                g.fillRect(bricks[i][0], bricks[i][1], BRICK_WIDTH, BRICK_HEIGHT);
        }
        
        g.dispose();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {    // this method handles any updates to the game's state
        // update ball, paddle
        ballPosX += ballVelX;
        ballPosY += ballVelY;         
        
        paddlePosX += paddleVelX;

        // check for any collisions
        
        // collisions between paddle and walls
        if (paddlePosX <= PADDLE_WIDTH / 2) { // left wall
            // negate paddle velocity
            paddlePosX -= paddleVelX;
            
            // ensure that paddle will not go over the wall edge; this is a fault of the drawing scheme
            paddlePosX = PADDLE_WIDTH / 2;
        } else if (paddlePosX >= WIDTH - PADDLE_WIDTH / 2) { // right wall
            // negate paddle velocity
            paddlePosX -= paddleVelX;
            
            // ensure that paddle will not go over the wall edge; this is a fault of the drawing scheme
            paddlePosX = WIDTH - PADDLE_WIDTH / 2;            
        }
        
        // collision between paddle and ball
        if (ballPosY >= paddlePosY - PADDLE_HEIGHT / 2 - BALL_RADIUS && 
                ballPosX >= paddlePosX -  PADDLE_WIDTH / 2 && ballPosX <= paddlePosX + PADDLE_WIDTH / 2) {
            
            // ensure that ball will not go over the edge of the paddle; this is a fault of the drawing scheme
            ballPosY = paddlePosY - PADDLE_HEIGHT / 2 - BALL_RADIUS;
            
            // negate the vertical velocity of the ball
            ballVelY *= -1;
        }
        
        // collision between ball and bricks
        for (int i = 0; i < bricks.length; i++) {
            // inactive brick, skip
            if (bricks[i][2] == 0) continue;
            
            // check if ball touches any of the edges of the bricks
            int x1 = bricks[i][0];
            int y1 = bricks[i][1];

            // rectangles/hitboxes of ball and brick
            Rectangle ballRect = new Rectangle(ballPosX - BALL_RADIUS, ballPosY - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
            Rectangle brickRect = new Rectangle(x1, y1, BRICK_WIDTH, BRICK_HEIGHT);

            // ball intersects with brick
            if (ballRect.intersects(brickRect) ) { 
                // update velocity depending on which brick side was hit by the ball
                if (
                    ballRect.x + ballRect.width - 1 >= brickRect.x &&
                    ballRect.x + ballRect.width -1 <= brickRect.x + brickRect.width - 1
                ) {
                    // left side of brick was hit, negate horizontal velocity
                    System.out.println("left");ballVelX *= -1;
                } else if (ballRect.x <= brickRect.x + brickRect.width - 1 && ballRect.x >= brickRect.x + brickRect.width - 1) {
                    // right side of brick was hit, negate horizontal velocity
                    System.out.println("right");ballVelX *= -1;                    
                } else {
                    ballVelY *= -1;
                }
                
                // destroy brick
               // bricks[i][2] = 0;
                
                // end loop
                break;
            }
        }
        
        // collisions between ball and walls
        if (ballPosX <= BALL_RADIUS) { // left wall
            // ensure that ball will not go over the edge of the wall; this is a fault of the drawing scheme
            ballPosX = BALL_RADIUS;
            
            // negate the horizontal velocity of the ball
            ballVelX *= -1;
        } else if (ballPosX >= WIDTH - BALL_RADIUS) { // right wall
             // ensure that ball will not go over the edge of the wall; this is a fault of the drawing scheme
            ballPosX = WIDTH - BALL_RADIUS;           
            
            // negate the horizontal velocity of the ball
            ballVelX *= -1;            
        }
        
        if (ballPosY <= BALL_RADIUS) { // top wall
            // ensure that ball always only touches the edge of the wall; this is a fault of the drawing scheme
            ballPosY = BALL_RADIUS;            
            
            // negate the vertical velocity of the ball
            ballVelY *= -1;            
        } else if (ballPosY >= HEIGHT - BALL_RADIUS) { // bottom wall
             // ensure that ball always only touches the edge of the wall; this is a fault of the drawing scheme
            ballPosY = HEIGHT - BALL_RADIUS;
            
            // game over
            newGame();
        }
        // redraw the game
        repaint();
    }    
    
    @Override
    public void keyPressed(KeyEvent e) {
        // check if key was pressed for any of the following events: starting a game, moving the paddle,
        // starting a game
        if (!ongoing) {
            ongoing = true;
            
            // engage the ball & 50/50 if the ball starts with a positive or negative horizontal velocity
            ballVelX = new Random().nextInt(2) == 0 ? INITIAL_BALL_VEL[0] : -INITIAL_BALL_VEL[0];
            ballVelY = INITIAL_BALL_VEL[1];
            
            // TEMP
//            ballPosX = 230;//15;//332;
//            ballPosY = 444;//15//444;
//            ballVelX = 0;
//            ballVelY = -4;
        // moving the paddle(left)-only if paddle was not already moving
        } else if (!isPaddleMoving[0] && e.getKeyCode() == KeyEvent.VK_LEFT) {
            movePaddleLeft();
        // moving the paddle(right)-only if paddle was not already moving
        } else if (!isPaddleMoving[1] && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            movePaddleRight();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // check if any arrow movement keys were released, if the prior key press was recognized, 
        // then update paddle velocity appropriately
        if (isPaddleMoving[0] && e.getKeyCode() == KeyEvent.VK_LEFT) {
            isPaddleMoving[0] = false;
            paddleVelX += INITIAL_PADDLE_X_VEL;
        } else if (isPaddleMoving[1] && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            isPaddleMoving[1] = false;
            paddleVelX -= INITIAL_PADDLE_X_VEL;
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {}
    
    @Override
    public void mouseClicked(MouseEvent e) {
        // left click to start the game
        if (!ongoing && e.getButton() == MouseEvent.BUTTON1)  {
            ongoing = true;
            
            // engage the ball & 50/50 if the ball starts with a positive or negative horizontal velocity
            ballVelX = new Random().nextInt(2) == 0 ? INITIAL_BALL_VEL[0] : -INITIAL_BALL_VEL[0];
            ballVelY = INITIAL_BALL_VEL[1];
        }
    }
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
   
    protected void newGame() {
        // end previous game
        ongoing = false;
        
        // set paddle & ball initial location
        paddlePosX = WIDTH / 2;
        
        int space = 15; // arbitrary number to put some space between ball and paddle
        ballPosX = WIDTH / 2;
        ballPosY = paddlePosY - PADDLE_HEIGHT / 2 - BALL_RADIUS - space;
        
        // ball is now stationary
        ballVelX = 0;
        ballVelY = 0;
        
        // generate bricks
        generateBricks();
    }

    private void generateBricks() {
        int bricks, rows, cols;
        int x1, y1, width, height; // rectangle that the brick structure forms
        
        // get a valid pair of bricks & rows where # of bricks is divisible by # of rows
        do {
            // pick # of bricks between 12-36
            bricks = new Random().nextInt(25) + 12;

            // pick # of rows between 4-6
            rows = new Random().nextInt(3) + 4;
            
            // valid; exit loop
            if (bricks % rows == 0) {
                break;
            }
        } while (true);

        // get # of cols from bricks
        cols = bricks / rows;
        
        // create bricks array ; x1, y1, active (nonzero=true, zero=false)
        this.bricks = new int[bricks][3];
        
        // get width & height
        width = bricks / rows * BRICK_WIDTH + (bricks / rows - 1) * BRICK_SPACE;
        height = bricks / cols * BRICK_HEIGHT + (bricks / cols - 1) * BRICK_SPACE;
        
        // center rectangle horizontally on screen
        x1 = WIDTH / 2 - width / 2;
        y1 = 75; // arbitrary number // just to fit all possible bricks on screen and also give space to paddle
        
        // set coordinates of each brick (by row)
        int brickIndex = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                this.bricks[brickIndex][0] = x1 + (BRICK_WIDTH + BRICK_SPACE) * c;
                this.bricks[brickIndex][1] = y1 + (BRICK_HEIGHT + BRICK_SPACE) * r;
                this.bricks[brickIndex][2] = 1;

                // move to next brick
                brickIndex++;
            }
        }
    }
    
    private void movePaddleLeft() {
        // updatde paddle velocity to move paddle to the left
        paddleVelX += -INITIAL_PADDLE_X_VEL;
        isPaddleMoving[0] = true;
    }
    
    private void movePaddleRight() {
       // update paddle velocity to move paddle to the right
       paddleVelX += INITIAL_PADDLE_X_VEL;
       isPaddleMoving[1] = true;
    }
}
