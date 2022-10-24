/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyApp;

import MyLibs.Powerup;
import MyLibs.QuizSystem;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import javax.swing.JOptionPane;
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
    private final int INITIAL_PADDLE_X_VEL = 2; // default paddle speed
    private final int INITIAL_PADDLE_X_VEL_MWHEEL = 4; // default paddle speed using the mouse wheel; had to add this for ease 
    
    // center point of paddle // you only need to set paddle position y once
    private int paddlePosX, paddlePosY = HEIGHT - PADDLE_HEIGHT / 2;
    
    // velocity is tied to the game's redraw time
    private int paddleVelX = 0;
    
    private boolean[] isPaddleMoving = {false, false};
    
    // Player lives
    public int liveCount = 3;
    // Score
    public int Score;
    //Gameover
    public int isGameOver = 0;
    private int isRetry = 1;
    private int totalBricks;


    // paddle movement modes; 0=keys, 1=mouse wheel
    private int paddleMovementMode = 0;
    
    // ball
    private final int BALL_RADIUS = 12;
    private final int[] INITIAL_BALL_VEL = {1, -2}; // default ball speed
    private int ballPosX, ballPosY; // center point of ball
    private int ballVelX = 0, ballVelY = 0; // velocity is tied to the game's redraw time
    
    // determines if a game session is currently ongoing
    private boolean ongoing = false;
    
    // bricks - a 2d array where each 1d array represents a brick
    // the elements in 1d array in order are: x1, y1
    private final int BRICK_WIDTH = 105, BRICK_HEIGHT = 53;
    private final int BRICK_SPACE = 10; // horizontal and vertical spaces between bricks
    private int[][] bricks;

    private ArrayList<Powerup> powerups = new ArrayList<>();
    
    private int specialbricks;
    
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
        
        if (!ongoing && isGameOver != 1) { // if game has not started, draw the helper text; don't show text in game-over screen
            dispStartText(g);
        }
        
        // draw paddle
        g.setColor(Color.WHITE);
        g.fillRect(paddlePosX - PADDLE_WIDTH / 2, paddlePosY - PADDLE_HEIGHT / 2, PADDLE_WIDTH, PADDLE_HEIGHT);
        
        // draw ball
        g.setColor(Color.RED);
        g.fillOval(ballPosX - BALL_RADIUS, ballPosY - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);    

        // draw bricks
        g.setColor(Color.WHITE);
        for (int i = 0; i < bricks.length; i++) {
            if(bricks[i][0] != 0 && bricks[i][1] != 0){
                if (bricks[i][2] == 1) {
                    g.setColor(Color.red);
                } else if (bricks[i][2] == 2) {
                    g.setColor(Color.yellow);
                }
                
                g.fillRect(bricks[i][0], bricks[i][1], BRICK_WIDTH, BRICK_HEIGHT);
            }
        }

        // draw powerups
        for(Powerup pow : powerups) {
            // set color 
            g.setColor(pow.getColor() );
            
            // draw powerup
            if (pow.getName().equals("t2paddle") ) {
                g.fillRect(pow.getX() - pow.getWidth() / 2, pow.getY() - pow.getHeight() / 2, pow.getWidth(), pow.getHeight());
            } else {
                g.fillOval(pow.getX() - pow.getWidth() / 2, pow.getY() - pow.getHeight() / 2, pow.getWidth(), pow.getHeight());
            }
        }
        g.setColor(Color.WHITE);
        
        if(liveCount == 0){
            gameOver(g);
        }

        dispText(g);
        
        g.dispose();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {    // this method handles any updates to the game's state
        // BUGFIX: holding down a paddle movement key while the level resets, moves the paddle while the game is stationary
        // reset paddle velocity while game is not ongoing
        if (!ongoing) {
            paddleVelX = 0;
        }
        
        // update ball, paddle
        ballPosX += ballVelX;
        ballPosY += ballVelY;         
        
        paddlePosX += paddleVelX;
        
                
        // if the appropriate movement method chosen, mouse wheel has not moved, reset paddle velocity
        if (paddleMovementMode == 1) {
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
                if (ballRect.x + ballRect.width - 1 <= brickRect.x || ballRect.x + 1 >= brickRect.x + brickRect.width) {
                    ballVelX *= -1;
                } else {
                    ballVelY *= -1;
                }                
                
                
                // 20% chance for powerup (10% for x2 faster paddle, 10% for x2 longer paddle)
                if (new Random().nextInt(20) < 10) {
                   Powerup powerup = new Powerup() {
                       @Override
                       public void fall(){
                          
                       }
                   };
                   
                   powerup.setName("t2paddle");
                   powerup.setX(x1+ BRICK_WIDTH/2);
                   powerup.setY(y1+ BRICK_HEIGHT/2);
                   powerup.setWidth(15);
                   powerup.setHeight(15);
                   powerup.setColor(Color.blue);
                   powerups.add(powerup);
                } else {
                   Powerup powerup = new Powerup() {
                       @Override
                       public void fall(){
                           
                       }
                   };
                   
                   powerup.setName("t2paddlesize");
                   powerup.setX(x1+ BRICK_WIDTH/2);
                   powerup.setY(y1+ BRICK_HEIGHT/2);
                   powerup.setWidth(15);
                   powerup.setHeight(15);
                   powerup.setColor(Color.yellow);
                   
                   powerups.add(powerup);                
                }
                
                //delete the brick
                bricks[i][0] = -BRICK_WIDTH;
                bricks[i][1] = -BRICK_HEIGHT;                
                totalBricks--;
                Score++;

                // end loop
                break;
            }
            if(totalBricks == 0){
                newGame();
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
            ongoing = false;
            
            if(liveCount > 0 ){
                liveCount--;

            }
            if(liveCount > 0){
               initialPos();
            } else{
                ballVelX = 0;
                ballVelY = 0;
            }        
        }
        
        // redraw the game
        repaint();
    }    
    
    @Override
    public void keyPressed(KeyEvent e) {
        // check if key was pressed for any of the following events: starting a game, moving the paddle,
        // starting a game
        if(isGameOver == 0){      
            // FOR KEYS MODE ONLY: moving the paddle
            if (ongoing && paddleMovementMode == 0) {
                // moving the paddle(left)-only if paddle was not already moving
                if (!isPaddleMoving[0] && e.getKeyCode() == KeyEvent.VK_LEFT) {
                    movePaddleLeft();
                // moving the paddle(right)-only if paddle was not already moving
                } else if (!isPaddleMoving[1] && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    movePaddleRight();
                }
            }
        }
        //Moving the cursor for Game Over Interface
        if(isGameOver == 1 && e.getKeyCode() == KeyEvent.VK_DOWN){
            isRetry = 0;
        } else if(isGameOver == 1 && e.getKeyCode() == KeyEvent.VK_UP){
            isRetry = 1;
        }
        if(isRetry == 1 && e.getKeyCode() == KeyEvent.VK_ENTER && isGameOver == 1){
            liveCount = 3;
            Score = 0;
            isGameOver = 0;
            newGame();
        }else if(isRetry == 0 && e.getKeyCode() == KeyEvent.VK_ENTER && isGameOver == 1){
            System.exit(0);
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
        if(isGameOver == 0){
        // left click to start the game
        if (!ongoing && e.getButton() == MouseEvent.BUTTON1)  {
            ongoing = true;
            
            // engage the ball & 50/50 if the ball starts with a positive or negative horizontal velocity
            ballVelX = new Random().nextInt(2) == 0 ? INITIAL_BALL_VEL[0] : -INITIAL_BALL_VEL[0];
            ballVelY = INITIAL_BALL_VEL[1];
        }
        }
    }
    @Override
    public void mouseEntered(MouseEvent e) {
    }    
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
   
    @Override
    public void mouseDragged(MouseEvent e) {
    }
    @Override
    public void mouseMoved(MouseEvent e) {
    }
    
     @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // don't move the paddle if game is not ongoing or if its not the correct paddle movement mode
        if (!ongoing || paddleMovementMode != 1) return;             
        
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
        
        initialPos();
        
        // generate bricks
        generateBricks();
    }

    protected void setPaddleMovementMode(int mode) {
        paddleMovementMode = mode;
    }
    
    private void generateBricks() {
        int bricks, rows, cols;
        int x1, y1, width, height; // rectangle that the brick structure forms
        // get a valid pair of bricks & rows where # of bricks is divisible by # of rows
        do {
            // pick # of bricks between 5-12
            bricks = new Random().nextInt(8) + 5;

            // pick # of rows between 2-3
            rows = new Random().nextInt(2) + 2;
            
            // valid; exit loop
            if (bricks % rows == 0) {
                break;
            }

        } while (true);

        // get # of cols from bricks
        cols = bricks / rows;
        
        // create bricks array ; x1, y1, special brick(0=normal, 1=quiz, 2=enlarge ball)
        this.bricks = new int[bricks][3];
        
        // get width & height
        width = bricks / rows * BRICK_WIDTH + (bricks / rows - 1) * BRICK_SPACE;
        height = bricks / cols * BRICK_HEIGHT + (bricks / cols - 1) * BRICK_SPACE;
        
       //specialbricks = new Random().nextInt(4); //0-5;
       //bricks[i][2] = 1;
        // center rectangle horizontally on screen
        x1 = WIDTH / 2 - width / 2;
        y1 = 150; // arbitrary number // just to fit all possible bricks on screen and also give space to paddle
        
        // set coordinates of each brick (by row)
        int brickIndex = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                this.bricks[brickIndex][0] = x1 + (BRICK_WIDTH + BRICK_SPACE) * c;
                this.bricks[brickIndex][1] = y1 + (BRICK_HEIGHT + BRICK_SPACE) * r;
                
                // to be a special quiz brick (0.05% enlarge, 2.5% quiz, 97% normal)
                int rand = new Random().nextInt(100);
                if (rand < 0) {
                    this.bricks[brickIndex][2] = 0;
                } else if (rand < 96) {
                    this.bricks[brickIndex][2] = 1;
                } else {
                    this.bricks[brickIndex][2] = 2;
                }
                // move to next brick
                brickIndex++;
            }
        }
        totalBricks = this.bricks.length;

    }
    
    private void movePaddleLeft() {
        // update paddle velocity to move paddle to the left
        if (paddleMovementMode != 1) {
            paddleVelX += -INITIAL_PADDLE_X_VEL;
        } else {
            paddleVelX += -INITIAL_PADDLE_X_VEL_MWHEEL;
        }
        
        isPaddleMoving[0] = true;
    }
    
    private void movePaddleRight() {
        // update paddle velocity to move paddle to the left
        if (paddleMovementMode != 1) {
            paddleVelX += INITIAL_PADDLE_X_VEL;
        } else {
            paddleVelX += INITIAL_PADDLE_X_VEL_MWHEEL;
        }
        
       isPaddleMoving[1] = true;
    }
    private void initialPos(){
        paddlePosX = WIDTH / 2;
        
        int space = 50; // arbitrary number to put some space between ball and paddle
        ballPosX = WIDTH / 2;
        ballPosY = paddlePosY - PADDLE_HEIGHT / 2 - BALL_RADIUS - space;
        
        // ball is now stationary
        ballVelX = 0;
        ballVelY = 0;
    }
    private void dispText(Graphics g){

        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("SCORE: " + Score, 15 , 30);
        g.drawString("LIVES: " + liveCount, 585 , 30);
    }

    private void dispStartText(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Calibri", Font.PLAIN, 24));
        g.drawString("Left-click to engage the ball.", ballPosX - 135, ballPosY - 45);
    }
    
    private void gameOver(Graphics g){
        isGameOver = 1;
        //Main
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setFont(new Font("Ariel", Font.BOLD, 36));
        g.setColor(Color.WHITE);
        g.drawString("GAME OVER",findCenter(g, "GAME OVER") , 320);
                
        g.setFont(new Font("Ariel", Font.BOLD, 24));
        //Retry
        g.drawString("Retry",findCenter(g, "Retry"), 400);

        //Quit
        g.drawString("Quit",findCenter(g, "Quit"), 450);
        
        //Cursor
        if(isRetry == 1){
            g.drawString(">", findCenter(g, "Retry") - 30, 400);

        } else{
            g.drawString(">", findCenter(g, "Retry") - 30, 450);

        }

    }
    private int findCenter(Graphics g, String text){
        int stringLen = (int)
        g.getFontMetrics().getStringBounds(text, g).getWidth();
        int start = WIDTH/2 - stringLen / 2;
        return start;
    }
}
