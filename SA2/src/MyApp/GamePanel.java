/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyApp;

import MyLibs.Powerup;
import MyLibs.QuizSystem;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
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
import java.util.Random;
import java.util.TimerTask;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.Timer;

/**
 *
 * @author Group 4
 */
public class GamePanel extends javax.swing.JPanel implements ActionListener, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
    // panel properties
    public final int WIDTH = 700, HEIGHT = 700;

    // handles the redrawing of the game
    private Timer timer;
     
    // handles the powerup effects of the game
    private java.util.Timer powTimer = new java.util.Timer(true);
    
    // paddle
    private final int INITIAL_PADDLE_WIDTH = 95;
    private int paddleWidth = INITIAL_PADDLE_WIDTH, paddleHeight = 15;
    private final int INITIAL_PADDLE_X_VEL = 2, INITIAL_PADDLE_X_VEL_MWHEEL = 4; // default paddle change speeds
    private int paddleXVelChange = 2; // change in paddle speed using keys
    private int paddleXVelMWheelChange = 4; // change in paddle speed using the mouse wheel; had to add this for ease 
    
    // center point of paddle // you only need to set paddle position y once
    private int paddlePosX, paddlePosY = HEIGHT - paddleHeight / 2;
    
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
    private final int INITIAL_BALL_RADIUS = 12;
    private final int[] INITIAL_BALL_VEL = {1, -2}; // default ball speed
    private int ballRadius = INITIAL_BALL_RADIUS;
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
    
    private QuizSystem qs = new QuizSystem();

    // flags
    private boolean enablePowerups = true, enableQuizSystem = false;
    
    // brick guide
    private boolean enableBrickGuide = false;
    private Color brickColor = Color.WHITE;
    private String brickText = "";
    
    // correct answers for a single game session
    private int correctAnswers = 0;
    
    // next level
    private int isNextLevel;
    private int go;
    
    // popup menu
    private JPopupMenu popupMenu;
    
    // current statistics variables
    protected int totalCorrectAnswers, powerupsTaken, questionsTaken;
    
    public GamePanel() {
        // properties
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        
        // create a popup menu
        popupMenu = new JPopupMenu();
        JMenuItem statsItem = new JMenuItem("Current Statistics");
        statsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Total Score:" + Score + "\n" + "Total Questions Taken:" + questionsTaken + "\n" + 
                        "Total Correct Answers:" + totalCorrectAnswers + "\n" + 
                        "Powerups Taken:" + powerupsTaken + "\n", "Overall Statistics for Current Session", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        JMenuItem endGameItem = new JMenuItem("End Game");
        endGameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // go to game over
                liveCount = 0;
            }
        });
        popupMenu.add(statsItem);
        popupMenu.add(endGameItem);
        
        // start a new game
        newGame();
        
        // redraws the game every 20 milliseconds
        timer = new Timer(20, this);
        timer.start();
    }

    public QuizSystem getQs() {
        return qs;
    }

    public boolean isEnablePowerups() {
        return enablePowerups;
    }

    public void setEnablePowerups(boolean enablePowerups) {
        this.enablePowerups = enablePowerups;
    }

    public boolean isEnableQuizSystem() {
        return enableQuizSystem;
    }

    public void setEnableQuizSystem(boolean enableQuizSystem) {
        this.enableQuizSystem = enableQuizSystem;
    }

    public boolean isEnableBrickGuide() {
        return enableBrickGuide;
    }

    public void setEnableBrickGuide(boolean enableBrickGuide) {
        this.enableBrickGuide = enableBrickGuide;
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }
    
    @Override
    public void paint(Graphics g) {
        // draw background
        g.setColor(Color.black);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        if (!ongoing && isGameOver != 1) { // if game has not started, draw the helper text; don't show text in game-over screen
            dispStartText(g);
        }
        
        // draw paddle
        g.setColor(Color.WHITE);
        g.fillRect(paddlePosX - paddleWidth / 2, paddlePosY - paddleHeight / 2, paddleWidth, paddleHeight);
        
        // draw ball
        g.setColor(Color.RED);
        g.fillOval(ballPosX - ballRadius, ballPosY - ballRadius, ballRadius * 2, ballRadius * 2);    

        // draw bricks
        g.setColor(Color.WHITE);
        for (int i = 0; i < bricks.length; i++) {
            if(bricks[i][0] != -BRICK_WIDTH && bricks[i][1] != -BRICK_HEIGHT){
                g.fillRect(bricks[i][0], bricks[i][1], BRICK_WIDTH, BRICK_HEIGHT);
            }
        }
        
        // draw powerups
        for(Powerup pow : powerups) {
            // set color 
            g.setColor(pow.getColor() );
            
            // draw powerup
            if (pow.getName().equals("t2paddle") ) { // x2 paddle speed powerup
                g.fillRect(pow.getX() - pow.getWidth() / 2, pow.getY() - pow.getHeight() / 2, pow.getWidth(), pow.getHeight());
            } else { // x2 paddle size powerup
                g.fillOval(pow.getX() - pow.getWidth() / 2, pow.getY() - pow.getHeight() / 2, pow.getWidth(), pow.getHeight());
            }
        }
        
        if(liveCount == 0){
            totalCorrectAnswers = 0;
            powerupsTaken = 0;
            questionsTaken = 0;
            gameOver(g);
        }

        // transition to the next level if no more bricks
        // if there are any powerups falling, wait for them to drop or be absorbed before transitioning to next level
        if(totalBricks == 0 && powerups.isEmpty() ){
            nextLevel(g);
        }
        
        g.setColor(Color.WHITE);
        dispText(g);
        
        // draw brick guide, if enabled
        if (enableBrickGuide) {
            g.setColor(brickColor);
            g.setFont(new Font("Times New Roman", Font.PLAIN, 15));
            g.drawString(brickText,  WIDTH - g.getFontMetrics().stringWidth(brickText) , HEIGHT - g.getFontMetrics(g.getFont()).getHeight());
        }
        
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
        // make the ball stationary in next level menu
        if (isNextLevel != 1) {
            ballPosX += ballVelX;
            ballPosY += ballVelY;         
        }
        
        paddlePosX += paddleVelX;
        
                
        // if the appropriate movement method chosen, mouse wheel has not moved, reset paddle velocity
        if (paddleMovementMode == 1) {
            isPaddleMoving[0] = false;
            isPaddleMoving[1] = false;
            paddleVelX = 0;
        }
        
        // check for any collisions
        
        // collisions between paddle and walls
        if (paddlePosX <= paddleWidth / 2) { // left wall
            // negate paddle velocity
            paddlePosX -= paddleVelX;
            
            // ensure that paddle will not go over the wall edge; this is a fault of the drawing scheme
            paddlePosX = paddleWidth / 2;
        } else if (paddlePosX >= WIDTH - paddleWidth / 2) { // right wall
            // negate paddle velocity
            paddlePosX -= paddleVelX;
            
            // ensure that paddle will not go over the wall edge; this is a fault of the drawing scheme
            paddlePosX = WIDTH - paddleWidth / 2;            
        }
        
        // collision between paddle and ball
        if (ballPosY >= paddlePosY - paddleHeight / 2 - ballRadius && 
                ballPosX >= paddlePosX -  paddleWidth / 2 && ballPosX <= paddlePosX + paddleWidth / 2) {
            
            // ensure that ball will not go over the edge of the paddle; this is a fault of the drawing scheme
            ballPosY = paddlePosY - paddleHeight / 2 - ballRadius;
            
            // negate the vertical velocity of the ball
            ballVelY *= -1;

        }
        // collision between ball and bricks
        for (int i = 0; i < bricks.length; i++) {
            // check if ball touches any of the edges of the bricks
            int x1 = bricks[i][0];
            int y1 = bricks[i][1];

            // rectangles/hitboxes of ball and brick
            Rectangle ballRect = new Rectangle(ballPosX - ballRadius, ballPosY - ballRadius, ballRadius * 2, ballRadius * 2);
            Rectangle brickRect = new Rectangle(x1, y1, BRICK_WIDTH, BRICK_HEIGHT);

            // ball intersects with brick
            if (ballRect.intersects(brickRect) ) { 
                if (ballRect.x + ballRect.width - 1 <= brickRect.x || ballRect.x + 1 >= brickRect.x + brickRect.width) {
                    ballVelX *= -1;
                } else {
                    ballVelY *= -1;
                }                
                
                // check if it is a special brick, and act appropriately
                // quiz brick does not work if quiz is not enabled
                if (bricks[i][2] == 1 && enableQuizSystem) {    // quiz brick
                    // get question and answer from qs
                    String[] arr = qs.randomQuestion();
                    
                    String userAnswer = JOptionPane.showInputDialog(this, arr[0]);
                    
                    if (userAnswer == null) {
                         // show prompt
                        JOptionPane.showMessageDialog(this, "You ran away! You get a -1 in your score!", "Coward!", JOptionPane.WARNING_MESSAGE);
                        
                        // subtract from score
                        Score -= 1;                       
                    } else if (userAnswer.toLowerCase().equals(arr[1].toLowerCase() ) ) { // correct answer
                        // show prompt
                        JOptionPane.showMessageDialog(this, "You get a +2 in your score!", "Correct!", JOptionPane.INFORMATION_MESSAGE);
                        
                        // add to score
                        Score += 2;
                        
                        // increment correct answers
                        correctAnswers++;
                        totalCorrectAnswers++;
                    } else {
                        // show prompt
                        JOptionPane.showMessageDialog(this, "You get a -4 in your score!", "Incorrect!", JOptionPane.ERROR_MESSAGE);
                        System.out.println(Score);
                        // subtract from score
                        Score -= 4;
                        
                    }
                    
                    // increment questions taken
                    questionsTaken++;
                } else if (bricks[i][2] == 2 && ballRadius == INITIAL_BALL_RADIUS) { // enlargen ball brick;dont stack
                    int a = 5;
                    ballRadius += a;

                    // after 20 seconds, revert the size
                    powTimer.schedule(new TimerTask(){
                        @Override
                        public void run() {
                            // revert effect, only if effect was not reverted prior (dying/new game)
                            if (ballRadius != INITIAL_BALL_RADIUS)
                                ballRadius -= a;
                        }
                    }, 20000);      
                }
                
                // 20% chance for powerup (10% for x2 longer paddle, 10% for x2 faster paddle)
                if (enablePowerups) {   // powerups must be enabled first
                    int rand = new Random().nextInt(100);
                    if (rand < 10) {
                       Powerup powerup = new Powerup();

                       // initialize the powerup properties
                       powerup.setName("t2paddle");
                       powerup.setX(x1+ BRICK_WIDTH/2);
                       powerup.setY(y1+ BRICK_HEIGHT/2);
                       powerup.setWidth(15);
                       powerup.setHeight(15);
                       powerup.setColor(Color.blue);

                       powerups.add(powerup);
                    } else if (rand < 20) {
                       Powerup powerup = new Powerup();

                       // initialize the powerup properties
                       powerup.setName("t2paddlespeed");
                       powerup.setX(x1+ BRICK_WIDTH/2);
                       powerup.setY(y1+ BRICK_HEIGHT/2);
                       powerup.setWidth(15);
                       powerup.setHeight(15);
                       powerup.setColor(Color.yellow);

                       powerups.add(powerup);               
                    }
                }
                
                //delete the brick
                bricks[i][0] = -BRICK_WIDTH;
                bricks[i][1] = -BRICK_HEIGHT;                
                totalBricks--;
                
                // don't add score for quiz bricks
                if (bricks[i][2] != 1)
                    Score++;

                // end loop
                break;
            }
        }
        
        // collisions between ball and walls
        if (ballPosX <= ballRadius) { // left wall
            // ensure that ball will not go over the edge of the wall; this is a fault of the drawing scheme
            ballPosX = ballRadius;
            
            // negate the horizontal velocity of the ball
            ballVelX *= -1;
        } else if (ballPosX >= WIDTH - ballRadius) { // right wall
             // ensure that ball will not go over the edge of the wall; this is a fault of the drawing scheme
            ballPosX = WIDTH - ballRadius;           
            
            // negate the horizontal velocity of the ball
            ballVelX *= -1;            
        }
        
        if (ballPosY <= ballRadius) { // top wall
            // ensure that ball always only touches the edge of the wall; this is a fault of the drawing scheme
            ballPosY = ballRadius;            
            
            // negate the vertical velocity of the ball
            ballVelY *= -1;            
        } else if (ballPosY >= HEIGHT - ballRadius) { // bottom wall
             // ensure that ball always only touches the edge of the wall; this is a fault of the drawing scheme
            ballPosY = HEIGHT - ballRadius;

            // game over
            ongoing = false;
            
            // remove active powerup effects
            if (paddleWidth != INITIAL_PADDLE_WIDTH) paddleWidth = INITIAL_PADDLE_WIDTH;
            
            if (paddleXVelChange != INITIAL_PADDLE_X_VEL) {
                paddleXVelChange = INITIAL_PADDLE_X_VEL;
                paddleXVelMWheelChange = INITIAL_PADDLE_X_VEL_MWHEEL;
            }
            // remove effect from special bricks
            if (ballRadius != INITIAL_BALL_RADIUS) ballRadius = INITIAL_BALL_RADIUS;
            
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
        
        // update powerup locations (make them fall)
        ArrayList<Powerup> deletedPowerups = new ArrayList<>();
        for (int i = 0; i < powerups.size(); i++) {
            Powerup pow = powerups.get(i);
            
            Rectangle powRect = new Rectangle(pow.getX() - pow.getWidth() / 2, pow.getY() - pow.getHeight() / 2, pow.getWidth(), pow.getHeight() );
            Rectangle paddleRect = new Rectangle(paddlePosX - paddleWidth / 2, paddlePosY - paddleHeight / 2, paddleWidth, paddleHeight);
            
            // update powerup's y value (only if the ball is engaged)
            if (ongoing)
                pow.setY(pow.getY() + 3);
            
            if (powRect.intersects(paddleRect) ) {
                // increment for statistics
                powerupsTaken++;
                
                // check for collision with paddle, if so, apply effect, and also prepare for deletion of reference
                if (pow.getName().equals("t2paddle") && paddleWidth == INITIAL_PADDLE_WIDTH) { // x2 paddle size effect; do not stack
                    paddleWidth *= 2;
                    
                    // after three seconds, revert the size
                    powTimer.schedule(new TimerTask(){
                        @Override
                        public void run() {
                            // revert effect, only if effect was not reverted prior (dying/new game)
                            if (paddleWidth != INITIAL_PADDLE_WIDTH)
                                paddleWidth /= 2;
                        }
                    }, 3000);
                } else if (pow.getName().equals("t2paddlespeed") && Math.abs(paddleXVelChange) == INITIAL_PADDLE_X_VEL) {    
                    // x2 paddle speed, don't stack the effect
                    
                    // BUGFIX: moving in a certain direction while receiving the powerup messes with the
                    // paddle's velocity
                    paddleVelX = 0;
                    isPaddleMoving[0] = false;
                    isPaddleMoving[1] = false;
                    
                    // apply effect for all movement modes
                    paddleXVelChange *= 2;
                    paddleXVelMWheelChange *= 2;
                    
                    // after five seconds, revert the speed
                    powTimer.schedule(new TimerTask(){
                        @Override
                        public void run() {
                            // BUGFIX: moving in a certain direction while receiving the powerup messes with the
                            // paddle's velocity
                            paddleVelX = 0;
                            isPaddleMoving[0] = false;
                            isPaddleMoving[1] = false;    
                            
                            // // revert effect, only if effect was not reverted prior (dying/new game)
                            if (paddleXVelChange != INITIAL_PADDLE_X_VEL) {
                                paddleXVelChange /= 2;
                                paddleXVelMWheelChange /= 2;
                            }
                        }
                    }, 5000);                   
                }
                
                deletedPowerups.add(pow);
            } else if (pow.getY() >= HEIGHT + pow.getHeight() / 2) 
                // if powerup is outside screen, prepare for deletion of its reference
                deletedPowerups.add(pow);
        }
        for (Powerup pow : deletedPowerups) {
            powerups.remove(pow);
        }

        // redraw the game
        repaint();
    }    
    
    @Override
    public void keyPressed(KeyEvent e) {
        // check if key was pressed for any of the following events: starting a game, moving the paddle,
        // starting a game
        if(isGameOver == 0 && isNextLevel == 0){      
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
            totalCorrectAnswers = 0; 
            powerupsTaken = 0; 
            questionsTaken = 0;            
            newGame();
        }else if(isRetry == 0 && e.getKeyCode() == KeyEvent.VK_ENTER && isGameOver == 1){
            System.exit(0);
        }
        
        // Moving the cursor for next level interface
        if (isNextLevel == 1 && e.getKeyCode() == KeyEvent.VK_DOWN) {
            go = 0;
        } else if(isNextLevel == 1 && e.getKeyCode() == KeyEvent.VK_UP) {
            go = 1;
        }
        if(go == 1 && e.getKeyCode() == KeyEvent.VK_ENTER && isNextLevel == 1){
            isNextLevel = 0;
            newGame();
            totalCorrectAnswers = 0; 
            powerupsTaken = 0; 
            questionsTaken = 0;              
        }else if(go == 0 && e.getKeyCode() == KeyEvent.VK_ENTER && isNextLevel == 1){
            System.exit(0);
        }        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // check if any arrow movement keys were released, if the prior key press was recognized, 
        // then update paddle velocity appropriately
        if (isPaddleMoving[0] && e.getKeyCode() == KeyEvent.VK_LEFT) {
            isPaddleMoving[0] = false;
            paddleVelX += paddleXVelChange;
        } else if (isPaddleMoving[1] && e.getKeyCode() == KeyEvent.VK_RIGHT) {
            isPaddleMoving[1] = false;
            paddleVelX -= paddleXVelChange;
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {}
    
    @Override
    public void mousePressed(MouseEvent e) {
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
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger() ) {
            popupMenu.show(e.getComponent(), e.getX(), e.getY() );
        }    
    }    
    @Override
    public void mouseClicked(MouseEvent e) {
        
    }
    @Override
    public void mouseEntered(MouseEvent e) {
    }    
    @Override
    public void mouseExited(MouseEvent e) {}
   
    @Override
    public void mouseDragged(MouseEvent e) {
        if (!enableBrickGuide || isGameOver == 1) return;
        
        brickText = "";
        
        for (int i = 0; i < bricks.length; i++) {
            int x1 = bricks[i][0];
            int y1 = bricks[i][1];
            int type = bricks[i][2];
            
            // rectangles/hitboxes of brick
            Rectangle brickRect = new Rectangle(x1, y1, BRICK_WIDTH, BRICK_HEIGHT);
            
            // mouse touches brick, update brick text
            if (brickRect.contains(e.getX(), e.getY() ) ) {
                switch (type) {
                    case 0:
                        brickText = "Normal brick.";
                        brickColor = Color.WHITE;
                    break;
                    case 1:
                        brickText = "Quiz brick.";
                        brickColor = Color.RED;
                    break;
                    case 2:
                        brickText = "Enlarge Ball brick.";
                        brickColor = Color.YELLOW;
                }
                
                // end loop
                break;
            }
        }        
    }
    @Override
    public void mouseMoved(MouseEvent e) {
        if (!enableBrickGuide || isGameOver == 1) return;
        
        brickText = "";
        
        for (int i = 0; i < bricks.length; i++) {
            int x1 = bricks[i][0];
            int y1 = bricks[i][1];
            int type = bricks[i][2];
            
            // rectangles/hitboxes of brick
            Rectangle brickRect = new Rectangle(x1, y1, BRICK_WIDTH, BRICK_HEIGHT);
            
            // mouse touches brick, update brick text
            if (brickRect.contains(e.getX(), e.getY() ) ) {
                switch (type) {
                    case 0:
                        brickText = "Normal brick.";
                        brickColor = Color.WHITE;
                    break;
                    case 1:
                        brickText = "Quiz brick.";
                        brickColor = Color.RED;
                    break;
                    case 2:
                        brickText = "Enlarge Ball brick.";
                        brickColor = Color.YELLOW;
                }
                
                // end loop
                break;
            }
        }
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
        
        // remove previous level's powerups
        powerups.clear();
        
        // remove active powerup effects
        if (paddleWidth != INITIAL_PADDLE_WIDTH) paddleWidth = INITIAL_PADDLE_WIDTH;
        if (paddleXVelChange != INITIAL_PADDLE_X_VEL) {
            paddleXVelChange = INITIAL_PADDLE_X_VEL;
            paddleXVelMWheelChange = INITIAL_PADDLE_X_VEL_MWHEEL;
        }
        
        // remove effect from special bricks
        if (ballRadius != INITIAL_BALL_RADIUS) ballRadius = INITIAL_BALL_RADIUS;
        
        // reset correct answers
        correctAnswers = 0;
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

        // center rectangle horizontally on screen
        x1 = WIDTH / 2 - width / 2;
        y1 = 150; // arbitrary number // just to fit all possible bricks on screen and also give space to paddle
        
        // set coordinates of each brick (by row)
        int brickIndex = 0;
        int quizBricksCount = 0, enlargeBricksCount = 0; // counts the number of current specific bricks
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                this.bricks[brickIndex][0] = x1 + (BRICK_WIDTH + BRICK_SPACE) * c;
                this.bricks[brickIndex][1] = y1 + (BRICK_HEIGHT + BRICK_SPACE) * r;
                
                // brick type (5% enlargen ball for 20 seconds, 15% quiz, 97% normal)
                int rand = new Random().nextInt(1000);
                if (rand < 800) {
                    this.bricks[brickIndex][2] = 0;
                } else if (rand < 950 && quizBricksCount <= (int)(0.40 * bricks) && enableQuizSystem) { // a max of 40% of bricks can be quiz bricks
                    // quiz system must be enabled
                    this.bricks[brickIndex][2] = 1;
                    quizBricksCount++;
                } else if (enlargeBricksCount == 0){ // only allow 1 brick of this type per level
                    this.bricks[brickIndex][2] = 2;
                    enlargeBricksCount++;
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
            paddleVelX += -paddleXVelChange;
        } else {
            paddleVelX += -paddleXVelMWheelChange;
        }
        
        isPaddleMoving[0] = true;
    }
    
    private void movePaddleRight() {
        // update paddle velocity to move paddle to the left
        if (paddleMovementMode != 1) {
            paddleVelX += paddleXVelChange;
        } else {
            paddleVelX += paddleXVelMWheelChange;
        }
        
       isPaddleMoving[1] = true;
    }
    private void initialPos(){
        paddlePosX = WIDTH / 2;
        
        int space = 50; // arbitrary number to put some space between ball and paddle
        ballPosX = WIDTH / 2;
        ballPosY = paddlePosY - paddleHeight / 2 - ballRadius - space;
        
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
    
    private void nextLevel(Graphics g){
        isNextLevel = 1;
        
        //Main
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.setColor(Color.WHITE);
        g.drawString("NEXT LEVEL",findCenter(g, "NEXT LEVEL") , 320);
                
        g.setFont(new Font("Arial", Font.BOLD, 24));
        //Retry
        g.drawString("Go",findCenter(g, "Go"), 400);

        //Quit
        g.drawString("Quit",findCenter(g, "Quit"), 450);
        
        //Cursor
        if(go == 1){
            g.drawString(">", findCenter(g, "Go") - 30, 400);

        } else{
            g.drawString(">", findCenter(g, "Quit") - 30, 450);

        }
        
        // write # of correct answers
        if (enableQuizSystem) {
            g.setColor(Color.green);
            g.drawString("Correct: " + correctAnswers, findCenter(g, "Correct: " + correctAnswers) - 30, 500);
        }
    }
    
    private void gameOver(Graphics g){
        isGameOver = 1;
        //Main
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.setColor(Color.WHITE);
        g.drawString("GAME OVER",findCenter(g, "GAME OVER") , 320);
                
        g.setFont(new Font("Arial", Font.BOLD, 24));
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
        
        // write # of correct answers
        if (enableQuizSystem) {
            g.setColor(Color.green);
            g.drawString("Correct: " + correctAnswers, findCenter(g, "Correct: " + correctAnswers) - 30, 500);
        }

    }
    private int findCenter(Graphics g, String text){
        int stringLen = (int)
        g.getFontMetrics().getStringBounds(text, g).getWidth();
        int start = WIDTH/2 - stringLen / 2;
        return start;
    }
}
