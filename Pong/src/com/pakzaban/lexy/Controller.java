package com.pakzaban.lexy;

import javafx.animation.AnimationTimer;
import javafx.scene.control.*;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;
import java.util.Optional;


public class Controller {
    public Pane graphPane;
    public Button startButton;
    public AnchorPane anchorPane;
    public Label scoreLabel;
    public Label messageBox;
    public Button stopButton;
    public Button nextLevelButton;
    public Label levelLabel;
    public Label messageLabel;

    private Circle c;
    private Rectangle paddle;
    private double vel;
    private double angle;
    private double xVel;
    private double yVel;
    private int score;
    private AnimationTimer animationTimer;
    private int level;
    private double paddleVel;
    private String username;
    private String password;
    private String credentials = "lexy,1342";
    private String[] credArray;
    private boolean loginFailed;
    private int highScore =0;

    public void initialize(){
        read();
        Login();
        readScore();
        startGame();
    }

    public void startGame(){
        graphPane.getChildren().clear();

        c = new Circle(200,500,20);
        Stop s5 = new Stop(0,Color.MAGENTA);
        Stop s6 = new Stop(1,Color.PURPLE);
        Stop[] stops1 = {s5,s6};
        RadialGradient rg = new RadialGradient(90,0,0.5,0.5,0.7,true, CycleMethod.NO_CYCLE,stops1);
        c.setFill(rg);

        vel = 10.0;
        angle = randomAngle();
        xVel = Math.cos(angle) * vel;
        yVel = - Math.sin(angle) * vel;

        paddle = new Rectangle(150,250,10,100);
        paddle.setFill(Color.DARKTURQUOISE);
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setWidth(3);
        innerShadow.setHeight(50);
        paddle.setEffect(innerShadow);
        paddleVel = 50;

        messageBox.setVisible(false);
        score = 0;
        scoreLabel.setText("Score: " + score);
        level = 1;
        levelLabel.setText("Level: " + level);
        graphPane.getChildren().addAll(c,paddle);
        startButton.setDisable(false);
        stopButton.setDisable(true);
        nextLevelButton.setVisible(false);
    }

    public void moveBall(){
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                //right wall
                if (c.getCenterX() + c.getRadius() > graphPane.getWidth()){
                   xVel = - xVel;
                }
                //top wall
                else if (c.getCenterY() + c.getRadius() > graphPane.getHeight()){
                    yVel = - yVel;
                }
                //bottom wall
                else if (c.getCenterY() - c.getRadius() < 0){
                    yVel = -yVel;
                }
                //left wall
                else if (c.getCenterX() - c.getRadius() < 0){
                    xVel = -xVel;
                    score--;
                    scoreLabel.setText(String.valueOf("Score: " + score));
                    boing();
                }
                //paddle collision
                else if (c.getCenterX() - c.getRadius() < paddle.getX() + paddle.getWidth() &&
                        c.getCenterY() + c.getRadius() >= paddle.getY() &&
                        c.getCenterY() - c.getRadius() <= paddle.getY() + paddle.getHeight() &&
                        xVel < 0){
                    xVel = -xVel;
                    score++;
                    scoreLabel.setText(String.valueOf("Score: " + score));
                    click();
                }
                c.setCenterX(c.getCenterX()+ xVel);
                c.setCenterY(c.getCenterY() + yVel);

                if(score == 5 * level){
                    animationTimer.stop();
                    messageBox.setVisible(true);
                    messageBox.setText("You won!");
                    stopButtonPressed();
                    nextLevelButton.setVisible(true);
                }

                else if (score == -5){
                    animationTimer.stop();
                    messageBox.setVisible(true);
                    messageBox.setText("You lost loser!");
                    stopButtonPressed();
                }

                if(stopButton.isPressed()){
                    animationTimer.stop();
                }


            }
        };

        animationTimer.start();
    }

    public double randomAngle(){
        double randomAngle = (Math.random() * (Math.PI/2)) - (Math.PI/4);
        return randomAngle;
    }

    public void movePaddle(javafx.scene.input.KeyEvent ke){
        if(ke.getText().equals("h") && paddle.getY() > 0){
            paddle.setY(paddle.getY() - paddleVel);
        }

        else if (ke.getText().equals("b") && (paddle.getY() + paddle.getHeight()) < graphPane.getHeight()){
            paddle.setY(paddle.getY() + paddleVel);
        }

    }

    public void startButtonPressed(){
        startGame();
        moveBall();
        startButton.setDisable(true);
        stopButton.setDisable(false);
    }

    public void stopButtonPressed(){
        stopButton.setDisable(true);
        startButton.setDisable(false);
    }

    public void nextLevelButtonPressed(){
        level++;
        levelLabel.setText("Level: " + level);
        startButton.setDisable(true);
        stopButton.setDisable(false);
        nextLevelButton.setVisible(false);
        messageBox.setVisible(false);
        xVel += 5;
        yVel += 5;
        paddleVel += 10;
        moveBall();
    }

    public void Login(){
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Login");
        if (loginFailed){
            dialog.setHeaderText("Login failed. Try again.");
        }
        else {
            dialog.setHeaderText("Enter username and password");
        }

        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType registerButton = new ButtonType("Register");
        dialog.getDialogPane().getButtonTypes().addAll(cancelButton,okButton,registerButton);

        ImageView imageView = new ImageView("login.png");
        imageView.setFitHeight(50);
        imageView.setPreserveRatio(true);
        dialog.setGraphic(imageView);

        Label userLabel = new Label("Username:");
        Label passwordLabel = new Label("Password:");
        TextField userField = new TextField();
        PasswordField passwordField = new PasswordField();
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.add(userLabel,0,0);
        gridPane.add(userField, 1,0);
        gridPane.add(passwordLabel,0,1);
        gridPane.add(passwordField,1,1);
        dialog.getDialogPane().setContent(gridPane);


        dialog.setResultConverter(dialogButton -> {
            if(dialogButton.equals(okButton)){
                String[] resultArray = {userField.getText(),passwordField.getText()};
                return resultArray;
            }

            else if (dialogButton.equals(registerButton)){
                username = userField.getText();
                password = passwordField.getText();
                System.out.println("Username: " + username);
                System.out.println("Password: " + password);

                try (FileWriter fileWriter = new FileWriter("data.txt")){
                    fileWriter.write(credentials + "," + username + "," + password);
                }

                catch (Exception e){

                }
            }
            else {
                System.exit(1);
            }
            return null;
        });
        Optional<String[]> result = dialog.showAndWait();
        String enteredUsername = result.get()[0];
        String enteredPassword = result.get()[1];
        Boolean success = false;

        for (int i = 0; i < credArray.length - 1; i++){
            if(credArray[i].equals(enteredUsername) && credArray[i+1].equals(enteredPassword)){
                success = true;
                username = enteredUsername;
                password = enteredPassword;
            }
        }

        if (success == false){
            loginFailed = true;
            Login();
        }
    }

    public void read(){
        try (FileReader fileReader = new FileReader("data.txt")){
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            credentials = bufferedReader.readLine();
            credArray = credentials.split(",");
        }

        catch (Exception e){
        }
    }

    public void click(){
        File clickFile = new File("sounds/click.wav");
        AudioClip clickSound = new AudioClip(clickFile.toURI().toString());
        clickSound.play();
    }

    public void boing(){
        File boingFile = new File("sounds/boing.wav");
        AudioClip boingSound = new AudioClip(boingFile.toURI().toString());
        boingSound.play();

    }

    public void quitButtonPressed(){
        Date date = new Date();

        if (score > highScore){
            highScore = score;
            System.out.println(username + " got high score of " + highScore + " on " + date);
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("High score");
            info.setHeaderText("Congrats!");
            info.setContentText("You have a new high score");
            info.showAndWait();

            try (FileWriter fileWriter = new FileWriter("score.txt")){
                fileWriter.write( username + "," + highScore + "," + date);
            }

            catch (Exception e){
            }
        }
        System.exit(1);
    }

    public void readScore(){
        try (FileReader fileReader = new FileReader("score.txt")){
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String scores = bufferedReader.readLine();
            String[] scoreArray = scores.split(",");
            highScore = Integer.valueOf(scoreArray[1]);
            messageLabel.setText(scoreArray[0] + " got high score of " + highScore + " on " + scoreArray[2]);
        }

        catch (Exception e){
        }

    }
}
