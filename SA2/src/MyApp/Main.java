/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package MyApp;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;

/**
 *
 * @author killa
 */
public class Main extends javax.swing.JFrame {
    
    

    /**
     * Creates new form Main
     */
    private GamePanel game;
    String abt;
    
    public Main() {
        // initialize game
        game = new GamePanel();
        add(game);
        initComponents();
        // frame properties
        setResizable(false);
        setTitle("Brick Breaker : Quiz Edition");
        setLocationRelativeTo(null);
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem1 = new javax.swing.JMenuItem();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        aboutButton = new javax.swing.JMenuItem();
        newGameButton = new javax.swing.JMenuItem();
        quitButton = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        PadControls = new javax.swing.JMenu();
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem2 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem3 = new javax.swing.JRadioButtonMenuItem();

        jMenuItem1.setText("jMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jMenuBar1.setBackground(new java.awt.Color(0, 0, 0));
        jMenuBar1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jMenuBar1.setFont(new java.awt.Font("Monospaced", 0, 15)); // NOI18N

        menuFile.setBackground(new java.awt.Color(0, 0, 0));
        menuFile.setForeground(new java.awt.Color(255, 255, 255));
        menuFile.setMnemonic('A');
        menuFile.setText("App");
        menuFile.setFont(new java.awt.Font("Monospaced", 0, 15)); // NOI18N
        menuFile.setOpaque(true);

        aboutButton.setBackground(new java.awt.Color(0, 0, 0));
        aboutButton.setFont(new java.awt.Font("Monospaced", 0, 15)); // NOI18N
        aboutButton.setForeground(new java.awt.Color(255, 255, 255));
        aboutButton.setText("About");
        aboutButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        aboutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutButtonActionPerformed(evt);
            }
        });
        menuFile.add(aboutButton);

        newGameButton.setBackground(new java.awt.Color(0, 0, 0));
        newGameButton.setFont(new java.awt.Font("Monospaced", 0, 15)); // NOI18N
        newGameButton.setForeground(new java.awt.Color(255, 255, 255));
        newGameButton.setText("New Game");
        newGameButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        newGameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newGameButtonActionPerformed(evt);
            }
        });
        menuFile.add(newGameButton);

        quitButton.setBackground(new java.awt.Color(0, 0, 0));
        quitButton.setFont(new java.awt.Font("Monospaced", 0, 15)); // NOI18N
        quitButton.setForeground(new java.awt.Color(255, 255, 255));
        quitButton.setText("Quit");
        quitButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        quitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitButtonActionPerformed(evt);
            }
        });
        menuFile.add(quitButton);

        jMenuBar1.add(menuFile);

        jMenu4.setBackground(new java.awt.Color(0, 0, 0));
        jMenu4.setForeground(new java.awt.Color(255, 255, 255));
        jMenu4.setText("Settings");
        jMenu4.setFont(new java.awt.Font("Monospaced", 0, 15)); // NOI18N

        PadControls.setBackground(new java.awt.Color(0, 0, 0));
        PadControls.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        PadControls.setForeground(new java.awt.Color(255, 255, 255));
        PadControls.setText("Paddle Controls");
        PadControls.setFont(new java.awt.Font("Monospaced", 0, 15)); // NOI18N
        PadControls.setOpaque(true);

        jRadioButtonMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K, 0));
        buttonGroup1.add(jRadioButtonMenuItem1);
        jRadioButtonMenuItem1.setSelected(true);
        jRadioButtonMenuItem1.setText("Use Keys");
        jRadioButtonMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem1ActionPerformed(evt);
            }
        });
        PadControls.add(jRadioButtonMenuItem1);

        jRadioButtonMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, 0));
        buttonGroup1.add(jRadioButtonMenuItem2);
        jRadioButtonMenuItem2.setText("Use Mouse");
        jRadioButtonMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem2ActionPerformed(evt);
            }
        });
        PadControls.add(jRadioButtonMenuItem2);

        jRadioButtonMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        buttonGroup1.add(jRadioButtonMenuItem3);
        jRadioButtonMenuItem3.setText("Use Mouse Wheel");
        jRadioButtonMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItem3ActionPerformed(evt);
            }
        });
        PadControls.add(jRadioButtonMenuItem3);

        jMenu4.add(PadControls);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void newGameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newGameButtonActionPerformed
        game.newGame();
        
    }//GEN-LAST:event_newGameButtonActionPerformed

    private void quitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitButtonActionPerformed
        System.exit(0);
    }//GEN-LAST:event_quitButtonActionPerformed

    private void jRadioButtonMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem1ActionPerformed
        // set movement mode to keys
        game.setPaddleMovementMode(0);
        
        // reset cursor
        game.setCursor(new Cursor(Cursor.DEFAULT_CURSOR) );
    }//GEN-LAST:event_jRadioButtonMenuItem1ActionPerformed

    private void jRadioButtonMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem3ActionPerformed
        // set movement mode to mouse wheel
        game.setPaddleMovementMode(2);

        // reset cursor
        game.setCursor(new Cursor(Cursor.DEFAULT_CURSOR) );
    }//GEN-LAST:event_jRadioButtonMenuItem3ActionPerformed

    private void jRadioButtonMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItem2ActionPerformed
        // set movement mode to mouse
        game.setPaddleMovementMode(1);

        // set cursor
        game.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR) );
    }//GEN-LAST:event_jRadioButtonMenuItem2ActionPerformed

    private void aboutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutButtonActionPerformed
        AboutPage about = new AboutPage(this, true);
        about.setLocationRelativeTo(this);
        about.setResizable(false);
        about.setVisible(true);
    }//GEN-LAST:event_aboutButtonActionPerformed

    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().setVisible(true);
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu PadControls;
    private javax.swing.JMenuItem aboutButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem2;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem3;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenuItem newGameButton;
    private javax.swing.JMenuItem quitButton;
    // End of variables declaration//GEN-END:variables

}

