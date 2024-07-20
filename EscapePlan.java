


//This is the EscapePlan mini-game

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

//Student class -- the characters
class Student {
    //stores player locations
    public int x;
    public int y;

    //stores player dimensions
    public int width;
    public int height;
    public int speed; //stores speed
    public int clickEffect; //used to increment player movement
    public boolean moving; //stores whether the character is moving, this variable is used to decide whether to update location
    public Image image; //used to store images of characters

    //student constructor
    public Student(int x, int y, int width, int height, int speed, int clickEffect, String imagePath) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.clickEffect = clickEffect;
        this.moving = false;
        try {
            this.image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //this method updates the location of the players if moving=true
    public void update() {
        if (moving) {
            y -= clickEffect;
            if (y <= 0) {
                moving = false;
            }
        }
    }

    //this method is used to draw the student character images
    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        }
    }
}


//Building class -- the obstacles
class Building {

    //stores building locations
    public int x;
    public int y;
    //stores building sizes
    public int size;
    public Image image; //stores building images

    //Building constructor
    public Building(int x, int y, int size, String imagePath) {
        this.x = x;
        this.y = y;
        this.size = size;
        try {
            this.image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //moves buildings to the left
    public void move(int speed) {
        x -= speed;
    }

    //draws building if it finds the image path, otherwise represented by a gray square
    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, size, size, null);
        } else {
            g.setColor(Color.GRAY);
            g.fillRect(x, y, size, size);
        }
    }
}

//EscapePlan -- main gameplay here
class EscapePlan extends JPanel implements KeyListener, MouseListener {

    //store the background image and start page image
    public Image startPageImage;
    public Image backgroundImage;
    //stores the bounds of the start button
    public Rectangle startButtonBounds;
    //keep track of the game state
    public boolean firstGameOver;
    public boolean secondGameOver;
    public boolean gameStarted;
    //represent the two instances of the student class
    public Student studentOne;
    public Student studentTwo;
    // stores the instances of the building as an array
    public Building[] buildings;
    //creates an array to keep track of the keys being pressed
    public boolean[] keys = new boolean[104];

    //constructor
    public EscapePlan() {

        //set screen size
        this.setPreferredSize(new Dimension(1024, 764));
        this.setSize(1024, 764);

        //print start page and background image
        try {
            startPageImage = ImageIO.read(new File("src/Images/EscapePlanStartPage.jpg"));
            backgroundImage = ImageIO.read(new File("src/Images/Background.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //create bounds for start button
        startButtonBounds = new Rectangle(358, 585, 308, 50);

        //initialize the gameStarted to false
        gameStarted = false;

        //set up event listeners
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
    }


    //This method sets up the start game logistics and is called when the startGame button is pressed
    public void startGame() {
        gameStarted = true; //when button is clicked
        restartGame(); //new instances of game components are created
        Thread gameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!firstGameOver && !secondGameOver) {
                        update();
                    }
                    repaint();
                    try {
                        Thread.sleep(1000 / 60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        gameThread.start();

    }

    //This method restarts the game each time the program is run and sets up all of the game components.
    public void restartGame() {

        //creates instances of students w/ image paths
        studentOne = new Student(50, 382, 50, 50, 20, 5, "src/Images/art.png");
        studentTwo = new Student(562, 382, 50, 50, 20, 5, "src/Images/classics.png");

        //creates instances of buildings w/ image paths
        buildings = new Building[12];
        int startingPosition = 400;
        int buildingSize = 90;

        String[] buildingImagePaths = {
                "src/Images/EscapePlan/SMUD.jpg",
                "src/Images/EscapePlan/JChap.jpg",
                "src/Images/EscapePlan/Fayerweather.jpg",
                "src/Images/EscapePlan/Barrett.jpg",
                "src/Images/EscapePlan/SCCE.jpg",
                "src/Images/EscapePlan/Chapin.jpg"
        };

        //set building positions and size
        for (int i = 0; i < buildings.length; i++) {
            int buildingY = (int) (Math.random() * (764 - buildingSize)) ;
            buildings[i] = new Building(startingPosition, buildingY, buildingSize, buildingImagePaths[i % buildingImagePaths.length]);
            startingPosition += buildingSize + 300;
        }

        //initialize both game states to false
        firstGameOver = false;
        secondGameOver = false;
    }

    //This method updates and implements the movement of the characters and
    // buildings using the previous methods from their respective classes.
    public void update() {

        //update student character movements
        studentOne.update();
        studentTwo.update();

        // implements the collision logic and checks the bounds to determine when the game should end
        for (Building building : buildings) {
            if (isColliding(studentOne, building)) {
                firstGameOver = true;
                break;
            }
            if (isColliding(studentTwo, building)) {
                secondGameOver = true;
                break;
            }
        }

        for (Building building : buildings) {
            building.move(2);
            if (building.x + building.size < 0) {
                building.x = 1024;
                building.y = (int) (Math.random() * (764 - building.size));
            }
        }

        //if the character hits the top and bottom of the screen, game ends
        if (studentOne.y <= 0 || studentOne.y + studentOne.height >= 764) {
            firstGameOver = true;
        }
        if (studentTwo.y <= 0 || studentTwo.y + studentTwo.height >= 764) {
            secondGameOver = true;
        }
    }



    //This method takes in the student and building instances as parameters to determine if the characters are colliding into the buildings.
    public boolean isColliding(Student student, Building building) {
        return student.x < building.x + building.size &&
                student.x + student.width > building.x &&
                student.y < building.y + building.size &&
                student.y + student.height > building.y;
    }

    //draw everything
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!gameStarted) {
            drawStartPage(g);
            return;
        }
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);

        studentOne.draw(g);
        studentTwo.draw(g);

        for (Building building : buildings) {
            building.draw(g);
        }

        Color newRed = new Color(120, 47, 44);


        //draw game over labels
        g.setColor(newRed);
        g.setFont(new Font("Font", Font.BOLD, 40));
        if (firstGameOver || secondGameOver) {
            String winner = "";
            if (firstGameOver && !secondGameOver) {
                winner = "Player Two Wins!";
            } else if (!firstGameOver && secondGameOver) {
                winner = "Player One Wins!";
            }
            g.drawString("Game Over! " + winner, 225, 382);
        }
    }

    //draw start page
    public void drawStartPage(Graphics g) {
        if (startPageImage != null) {
            g.drawImage(startPageImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    //key listener methods

    //implement key responses
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;

        //e moves player 1 up
        if (keys[KeyEvent.VK_E]) {
            studentOne.y -= studentOne.speed;
        }
        //d moves player 1 down
        if (keys[KeyEvent.VK_D]) {
            studentOne.y += studentOne.speed;
        }
        //i moves player 1 up
        if (keys[KeyEvent.VK_I]) {
            studentTwo.y -= studentTwo.speed;
        }
        //j moves player two down
        if (keys[KeyEvent.VK_J]) {
            studentTwo.y += studentTwo.speed;
        }

        //for testing cases, not implemented in game
//        if (keys[KeyEvent.VK_R]) {
//            restartGame();
//        }

    }
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }
    public void keyTyped(KeyEvent e) {
    }

    //mouse listener methods

    //check if startButton is clicked and start the game
    public void mouseClicked(MouseEvent e) {
        if (!gameStarted && startButtonBounds.contains(e.getPoint())) {
            startGame();
        }
    }
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    //this returns int 1 or 2 based on which player wins, used in main class to assign stars
    public int getWinner(){
        if (firstGameOver && !secondGameOver) {
            return 2;
        } else if (!firstGameOver && secondGameOver) {
            return 1;
        }
        else return 0;
    }

    public static void main(String[] args) {
        //code to play music from: https://www.muradnabizade.com/backgroundmusicjava
        //music (copy-right free) from: https://www.youtube.com/watch?v=mRN_T6JkH-c&list=PLwJjxqYuirCLkq42mGw4XKGQlpZSfxsYd
        String filePath = "src/Audio/RPReplay_Final1714956567.wav";
        Music play = new Music();
        play.playMusic(filePath);
        //end of borrowed code

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Mini Game");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                EscapePlan escapePlan = new EscapePlan();
                frame.setContentPane(escapePlan);
                frame.pack();
                frame.setVisible(true);
            }
        });

    }
}
