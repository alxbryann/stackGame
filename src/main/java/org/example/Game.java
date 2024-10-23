package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

public class Game extends JPanel implements ActionListener, KeyListener {

    private static class Tile {
        int x;
        int y;
        int width;
        Color color;

        Tile(int x, int y, int width, Color color) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.color = color;
        }

        public void setWidth(int width) {
            this.width = width;
        }
    }


    //Jframe size
    int boardWidth;
    int boardHeigth;

    //Initial Block preset
    Tile initialBlock;
    int BlockWidth;
    int BlockHeigth;

    //New block preset
    Tile newBlock;

    //Velocity
    int velocityX;
    int velocityY;

    //Stack
    Stack<Tile> tiles = new Stack<>();

    //Gamemode
    boolean gameMode;
    boolean gameOver;

    //Game variables
    Timer gameLoop;
    Random rand = new Random();
    int currentSize;
    int score;
    int initialDirection;
    BufferedImage backgroundImage;
    String bestScore;


    Firebase firebase = new Firebase();


    //Constructor
    public Game(int boardWidth, int boardHeigth) {
        addKeyListener(this);
        setFocusable(true);
        this.boardWidth = boardWidth;
        this.boardHeigth = boardHeigth;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeigth));
        try {
            backgroundImage = ImageIO.read(new File("src/main/resources/bg.png"));
        } catch (IOException e) {
        }

        //Location of initialBlock
        BlockWidth = 150;
        BlockHeigth = 40;
        initialBlock = new Tile(600 / 2 - BlockWidth / 2, 670, BlockWidth, getColor());
        tiles.push(initialBlock);

        //New block
        newBlock = new Tile(600 / 2, 120, BlockWidth - 20, getColor());
        velocityX = 10;
        velocityY = 10;

        //Gameloop
        gameLoop = new Timer(10, this);
        gameLoop.start();
        currentSize = tiles.size();
        gameOver = false;
        gameMode = false;
        score = 0;
        initialDirection = rand.nextInt(2);
        firebase.createConnection();
        bestScore = firebase.getBestScore();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(backgroundImage, 0, 0, 600, 720, null);
        draw(g);
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        for (Tile tile : tiles) {
            g2d.setColor(tile.color.darker());
            g2d.fillRect(tile.x + 5, tile.y - 5, tile.width, BlockHeigth);

            g2d.setColor(tile.color);
            g2d.fillRect(tile.x, tile.y, tile.width, BlockHeigth);

            g2d.setColor(tile.color.brighter());
            g2d.drawLine(tile.x, tile.y, tile.x + tile.width, tile.y);
            g2d.drawLine(tile.x, tile.y, tile.x, tile.y + BlockHeigth);
        }

        g2d.setColor(newBlock.color.darker());
        g2d.fillRect(newBlock.x + 5, newBlock.y - 5, newBlock.width, BlockHeigth);

        g2d.setColor(newBlock.color);
        g2d.fillRect(newBlock.x, newBlock.y, newBlock.width, BlockHeigth);

        g2d.setColor(newBlock.color.brighter());
        g2d.drawLine(newBlock.x, newBlock.y, newBlock.x + newBlock.width, newBlock.y);
        g2d.drawLine(newBlock.x, newBlock.y, newBlock.x, newBlock.y + BlockHeigth);

        g2d.setColor(Color.BLUE);
        g2d.setFont(new Font("Agdasima-Bold", Font.PLAIN, 30));
        g2d.drawString("Score: " + score, 30, 30);
        g2d.drawString("best score: " + bestScore, 30, 60);

        if (gameOver) {
            g2d.setColor(Color.black);
            g2d.fillRoundRect(135, 240, 350, 250, 30, 30);
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Agdasima-Bold", Font.PLAIN, 50));
            g2d.drawString("GAME OVER", 160, 300);
            Map<String, Object> db = new HashMap<>();
            db.put("1", String.valueOf(score));
            firebase.insertData("score", db);
            /*if(Integer.parseInt(firebase.getBestScore()) <= score){
                Map<String, Object> bestScore = new HashMap<>();
                bestScore.put("bestScore", String.valueOf(score));
                firebase.insertData("bestScore", "OpWsBfD65NHN3pt8mnyf", bestScore);
            }*/
        }
    }


    public void bouncing() {
        if (tiles.size() > currentSize) {
            for (Tile tile : tiles) {
                tile.y += 20;
            }
            currentSize = tiles.size();
            BlockWidth = newBlock.width;
            newBlock = new Tile(600 / 2, 120, BlockWidth, getColor());
            velocityX++;
            velocityY = 10;
            score++;
            initialDirection = rand.nextInt(2);
        }
        if (newBlock.x + BlockWidth >= 600 || newBlock.x <= 0) {
            velocityX *= -1;
        }
        if (initialDirection < 1)
            newBlock.x += velocityX;
        else
            newBlock.x -= velocityX;
    }

    public void falling() {

        Tile lastBlock = tiles.peek();

        if (newBlock.y > 700) {
            gameOver = true;
            gameLoop.stop();
        }

        if (!gameOver && newBlock.y + BlockHeigth >= lastBlock.y &&
                newBlock.x < (lastBlock.x + lastBlock.width) &&
                (newBlock.x + newBlock.width) > lastBlock.x) {

            if (newBlock.x < lastBlock.x) {
                int difference = lastBlock.x - newBlock.x;
                newBlock.width -= difference;
                newBlock.x += difference;
            }

            if (newBlock.x > lastBlock.x) {
                int newBlockRight = newBlock.x + newBlock.width;
                int lastBlockRight = lastBlock.x + lastBlock.width;
                int difference = newBlockRight - lastBlockRight;
                newBlock.width -= difference;
            }
            gameMode = !gameMode;
            tiles.push(newBlock);
        } else {
            newBlock.y += velocityY;
        }
    }


    public Color getColor() {
        int r = rand.nextInt(256);
        int g = rand.nextInt(256);
        int b = rand.nextInt(256);

        return new Color(r, g, b);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameMode) {
            bouncing();
        } else {
            falling();
        }
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 32) {
            if (!gameMode)
                gameMode = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}

