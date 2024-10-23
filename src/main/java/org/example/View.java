package org.example;

import javax.swing.*;

public class View {


    public View(int boardWidth, int boardHeigth){

        //Initialize Jpanel
        Game jpanel = new Game(boardWidth, boardHeigth);
        //StartGame startGame = new StartGame(boardWidth,boardHeigth);

        //Add Jpanel to JFrame
        JFrame jframe = new JFrame("Stack game");
        jframe.setVisible(true);
        jframe.setSize(boardWidth, boardHeigth);
        jframe.setLocationRelativeTo(null);
        jframe.setResizable(false);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //jframe.add(startGame);
        jframe.add(jpanel);
        jframe.pack();
        jpanel.requestFocus();

    }
}
