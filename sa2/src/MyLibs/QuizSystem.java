/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MyLibs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author abero
 */
public class QuizSystem {
    private ArrayList <String> questions = new ArrayList<>();
    private ArrayList <String> answers = new ArrayList<>();
    
    public void getTextFile(String textFile) {
    
        File file = new File(textFile);
        try {
            Scanner textScan = new Scanner(file);
            questions.clear();
            answers.clear();
            while (textScan.hasNextLine()){
                questions.add(textScan.nextLine());
                answers.add(textScan.nextLine());
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(QuizSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    public String[] randomQuestion(){
    
        int numRand = new Random().nextInt(questions.size());
        //places the taken question and answer into a string
        //TODO: something with it to make it output to a JOptionPane
        //TODO: check string outputAnswer to user input answer
        String outputQuestion = questions.get(numRand);
        String outputAnswer = answers.get(numRand);
        
        String[] outputArray = {outputQuestion, outputAnswer};
        return outputArray;
    }
    
    
}
