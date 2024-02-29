package org.example.javalab1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;


public class GameController {
    Thread thread;

    public int orientationBig = 1;
    public int orientationSmall = 1;
    public int scoreForShoot = 0;
    public  int scoreForHit = 0;

    private boolean isRunning = false;
    private boolean isShot = false;
    @FXML
    public Group strela;
    @FXML
    public Circle BigCircle;
    @FXML
    public Circle LittleCircle;

    public Arrow arrow;

    @FXML
    public void initialize() {
        arrow = new Arrow(strela, strela.getLayoutX(), strela.getLayoutY());
    }

    @FXML
    public void pauseGame() {
        synchronized (lock) {
            if (isRunning) {
                isRunning = false;
                lock.notify();
            } else {
                isRunning = true;
            }
        }
    }

    @FXML
    public Label shootScore;
    @FXML
    public Label scorePoints;

    private final Object lock = new Object();
    @FXML
    public void Shooting() {
        if (!isShot) {
            isShot = true;
            scoreForShoot += 1;
            shootScore.setText(Integer.toString(scoreForShoot));
        }

    }

    public int orientCircle(int orent, Circle circle, int BL, int Fl){
        if(orent == 1 && circle.getLayoutY() >= BL){
            orent = -1;
        } else if (orent == -1 && circle.getLayoutY() <= Fl) {
            orent = 1;
        }
        return orent;
    }

    private void checkHit(double arrowX, double arrowY, double targetCenterX, double targetCenterY, double targetRadius, int points) {
        if (HitTheTarget(arrowX, arrowY, targetCenterX, targetCenterY, targetRadius)) {
            System.out.println("Попадание!");
            scoreForHit += points;
            scorePoints.setText(Integer.toString(scoreForHit));
            resetPos(78, 193, arrow);

            isShot = false;
        }
    }

    private  void resetPos(double x, double y, Arrow s){
        s.setLayoutX(x);
        s.setLayoutY(y);
    }

    private void checkShoot() {
        if (isShot) {
            if (arrow.getLayoutX() >= 550) {
                resetPos(78, 193, arrow);
                isShot = false;
            } else {
                arrow.move(7);
                checkHit(arrow.getLayoutX(), arrow.getLayoutY(), BigCircle.getLayoutX() - BigCircle.getRadius(), BigCircle.getLayoutY() - BigCircle.getRadius(), BigCircle.getRadius(), 1);
                checkHit(arrow.getLayoutX(), arrow.getLayoutY(), LittleCircle.getLayoutX() - LittleCircle.getRadius(), LittleCircle.getLayoutY() - LittleCircle.getRadius(), LittleCircle.getRadius(), 2);
            }
        }
    }




    private boolean HitTheTarget(double arrowX, double arrowY, double targetCenterX, double targetCenterY, double targetRadius) {
        double distance = Math.sqrt(Math.pow(arrowX - targetCenterX, 2) + Math.pow(arrowY - targetCenterY, 2));

        return distance <= targetRadius;
    }
    @FXML
    public void startGame(){
        if(thread != null && thread.isAlive()) {
            stopGame();
            return;
        }

        thread = new Thread(() -> {

            while (!Thread.currentThread().isInterrupted()) {

                synchronized (lock) {
                    if (isRunning) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            System.out.println(e);
                        }
                    }
                }

                Platform.runLater(()->{

                    orientationBig = orientCircle(orientationBig, BigCircle, 370, 25);
                    orientationSmall = orientCircle(orientationSmall, LittleCircle,380, 25);

                    checkShoot();

                    BigCircle.setLayoutY(BigCircle.getLayoutY() + (2 * orientationBig));
                    LittleCircle.setLayoutY(LittleCircle.getLayoutY() + (3 * orientationSmall));



                });

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println(e);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }


    @FXML
    public void stopGame(){
        thread.interrupt();

        try {
            thread.join();
        } catch (InterruptedException e) {
            System.out.println(e);
        }

        if (!thread.isAlive()){
            Platform.runLater(()->{
                BigCircle.setLayoutY(177);
                LittleCircle.setLayoutY(177);
                scoreForShoot = 0;
                shootScore.setText(Integer.toString(0));
                scorePoints.setText(Integer.toString(0));
                resetPos(78, 193, arrow);
                isShot = false;
            });
        }
    }
}
