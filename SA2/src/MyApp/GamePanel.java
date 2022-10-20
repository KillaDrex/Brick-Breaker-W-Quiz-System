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
import javax.swing.Timer;

/**
 *
 * @author killa
 */
public class GamePanel extends javax.swing.JPanel implements ActionListener {
    // panel properties
    private int WIDTH = 700, HEIGHT = 700;
    
    // handles the redrawing of the game
    private Timer timer;
    
    // paddle
    private int PADDLE_WIDTH = 75, PADDLE_HEIGHT = 25;
    
    public GamePanel() {
        // properties
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        
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
        
        g.dispose();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }    
    
    private void newGame() {
        
    }
}
