package org.example;

import javax.swing.*;
import java.awt.*;

public class StartGame  extends JPanel {

    //Jframe size
    int boardWidth;
    int boardHeigth;
    JLabel title;
    JButton start;

    public StartGame(int boardWidth, int boardHeigth){
        this.boardWidth = boardWidth;
        this.boardHeigth = boardHeigth;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeigth));
        title.setBounds(100,100 ,100,100);
        title.setText("STACK GAME");
        start.setBounds(200,200,100,100);
        start.setText("START GAME");
    }


}
