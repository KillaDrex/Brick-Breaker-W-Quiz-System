/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyApp;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Random;
import javax.swing.Timer;

/**
 *
 * @author killa
 */
public class GamePanel extends javax.swing.JPanel implements ActionListener, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
    // panel properties
    private final int WIDTH = 700, HEIGHT = 700;

    // handles the redrawing of the game
    private Timer timer;
    
    // get the most recent mouse x coordinate ; used in paddle movement
    private int prevMouseX;
     
    // paddle
    private final int PADDLE_WIDTH = 95, PADDLE_HEIGHT = 15;
    private final int INITIAL_PADDLE_X_VEL = 9; // default paddle speed
    private final int INITIAL_PADDLE_X_VEL_MWHEEL = 14; // default paddle speed using the mouse wheel; had to add this for ease 
    
    // center point of paddle // you only need to set paddle position y once
    private int paddlePosX, paddlePosY = HEIGHT - PADDLE_HEIGHT / 2;
    
    // velocity is tied to the game's redraw time
    private int paddleVelX = 0;
    
    private boolean[] isPaddleMoving = {false, false};
    
    // paddle movement modes; 0=keys, 1=mouse, 2=mouse wheel
    private int paddleMovementMode = 2;
    
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
        addMouseMotionListener(this);
        addMouseWheelListener(this);
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
        
        // if the appropriate movement method chosen, mouse/mouse wheel has not moved, reset paddle velocity
        if (paddleMovementMode == 1 || paddleMovementMode == 2) {
            isPaddleMoving[0] = false;
            isPaddleMoving[1] = false;
            paddleVelX = 0;
        }
        
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
            // check if ball touches any of the edges of the bricks
            int x1 = bricks[i][0];
            int y1 = bricks[i][1];

            // rectangles/hitboxes of ball and brick
            Rectangle ballRect = new Rectangle(ballPosX - BALL_RADIUS, ballPosY - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
            Rectangle brickRect = new Rectangle(x1, y1, BRICK_WIDTH, BRICK_HEIGHT);

            // ball intersects with brick
            if (ballRect.intersects(brickRect) ) { 
                // update velocity depending on which brick side was hit by the ball
                if (brickRect.contains(new Point(ballRect.x + ballRect.width -1, ballRect.y) ) ) {             
                        // ball hits left side of brick, negate horizontal velocity
                        ballVelX *= -1;
                } else if (brickRect.contains(new Point(ballRect.x, ballRect.y) ) ) {
                        // ball hits right side of brick, negate horizontal velocity
                        ballVelX *= -1;
                }
                
                if (brickRect.contains(new Point(ballRect.x, ballRect.y) ) ) {
                        // ball hits top side of brick, negate horizontal velocity
                        ballVelY *= -1;
                } else if (brickRect.contains(new Point(ballRect.x, ballRect.y + ballRect.height - 1) ) ) {
                        // ball hits bottom side of brick, negate horizontal velocity
                        ballVelY *= -1;
                }

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
            
        // FOR KEYS MODE ONLY: moving the paddle
        } else if (paddleMovementMode == 0) {
            // moving the paddle(left)-only if paddle was not already moving
            if (!isPaddleMoving[0] && e.getKeyCode() == KeyEvent.VK_LEFT) {
                movePaddleLeft();
            // moving the paddle(right)-only if paddle was not already moving
            } else if (!isPaddleMoving[1] && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                movePaddleRight();
            }
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
    public void mouseEntered(MouseEvent e) {
        // change the mouse cursor ONCEwhen mouse enters screen (ONCE)
        if (!getCursor().getName().equals("Crosshair Cursor") )
            setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR) );
    }    
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
   
    @Override
    public void mouseDragged(MouseEvent e) {
        // don't move the paddle if game is not ongoing or if its not the correct paddle movement mode
        if (!ongoing || paddleMovementMode != 1) return;      
        
        // make the paddle follow the mouse
        paddlePosX = e.getX();    
    }
    @Override
    public void mouseMoved(MouseEvent e) {
        // don't move the paddle if game is not ongoing or if its not the correct paddle movement mode
        if (!ongoing || paddleMovementMode != 1) return;       
        
        // move the paddle depending on the direction the mouse was moved
        if (e.getX() < prevMouseX) { // moved to the left
            movePaddleLeft();
        } else if (e.getX() > prevMouseX) { // moved to the right
            movePaddleRight();
        }
                
        // get coords
        prevMouseX = e.getX();
    }
    
     @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // don't move the paddle if game is not ongoing or if its not the correct paddle movement mode
        if (!ongoing || paddleMovementMode != 2) return;             
        
        // move the paddle depending on the direction the wheel was scrolled
        if (e.getWheelRotation() < 0) { // left paddle movement: scrolled negatively
            movePaddleLeft();
        } else { // right paddle movement: scrolled positively
            movePaddleRight();
        }
    }
    
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
        
        // create bricks array ; x1, y1
        this.bricks = new int[bricks][2];
        
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

                // move to next brick
                brickIndex++;
            }
        }
    }
    
    private void movePaddleLeft() {
        // update paddle velocity to move paddle to the left
        if (paddleMovementMode != 2) {
            paddleVelX += -INITIAL_PADDLE_X_VEL;
        } else {
            paddleVelX += -INITIAL_PADDLE_X_VEL_MWHEEL;
        }
        
        isPaddleMoving[0] = true;
    }
    
    private void movePaddleRight() {
        // update paddle velocity to move paddle to the left
        if (paddleMovementMode != 2) {
            paddleVelX += INITIAL_PADDLE_X_VEL;
        } else {
            paddleVelX += INITIAL_PADDLE_X_VEL_MWHEEL;
        }
        
       isPaddleMoving[1] = true;
    }
}
