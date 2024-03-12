package org.example.javalab1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;

public class GameController {
    Thread thread;

    public int moveBig = 1;
    public int moveLittle = 1;
    public int NumShootScore = 0;
    public  int NumScorePoints = 0;

    private boolean Stopped = false;
    private boolean isShot = false;
    @FXML
    public Group strela;
    @FXML
    public Circle BigCircle;
    @FXML
    public Circle LittleCircle;

    public Arrow arrow;

    @FXML
    public Label shootScore;
    @FXML
    public Label scorePoints;

    @FXML
    public Button pause;
    public Button end;
    public Button shoot;
    public Button start;

    private final Object lock = new Object();

    @FXML
    public void initialize() {
        arrow = new Arrow(strela, strela.getLayoutX(), strela.getLayoutY());
    }

    @FXML
    public void Shooting() {
        if (!isShot) {
            isShot = true;
            NumShootScore += 1; // подсчет выстрелов
            shootScore.setText(Integer.toString(NumShootScore));
        }

    }

    public int moveCircle(int movement, Circle circle, int BL, int Fl){
        if(movement == 1 && circle.getLayoutY() >= BL){
            movement = -1;
        } else if (movement == -1 && circle.getLayoutY() <= Fl) {
            movement = 1;
        }
        return movement;
    }

    private  void reset(double x, double y, Arrow s){
        s.setLayoutX(x);
        s.setLayoutY(y);
    }

    private void checkHit(double arrowX, double arrowY, double targetCenterX, double targetCenterY, double targetRadius, int points) { // проверка на попадание
        if (HitTheTarget(arrowX, arrowY, targetCenterX, targetCenterY, targetRadius)) {
            NumScorePoints += points; // подсчет очков при попадании
            scorePoints.setText(Integer.toString(NumScorePoints));
            reset(78, 193, arrow);

            isShot = false;
        }
    }

    private void checkShoot() {
        if (isShot) {
            if (arrow.getLayoutX() >= 550) {
                reset(78, 193, arrow); // возврат стрелы на исходное положение
                isShot = false;
            } else {
                arrow.move(10); // скорость полета стрелы
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
    public void startGame(){ // старт игры
        if(thread != null && thread.isAlive()) {
            stopGame();
            return;
        }
        pause.setDisable(false);
        end.setDisable(false);
        shoot.setDisable(false);
        start.setDisable(true);
        thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (lock) {
                    if (Stopped) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            System.out.println(e);
                        }
                    }
                }

                Platform.runLater(()->{

                    moveBig = moveCircle(moveBig, BigCircle, 370, 25);
                    moveLittle = moveCircle(moveLittle, LittleCircle,380, 25);

                    checkShoot();

                    BigCircle.setLayoutY(BigCircle.getLayoutY() + (2 * moveBig));
                    LittleCircle.setLayoutY(LittleCircle.getLayoutY() + (3 * moveLittle));

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
    public void pauseGame() { // пауза игры

        synchronized (lock) {
            Stopped = !Stopped;
            if (Stopped) {
                Stopped = true;
                pause.setText("ПРОДОЛЖИТЬ");
            } else {
                Stopped = false;
                pause.setText("ПАУЗА");
                lock.notifyAll();
            }
        }
    }

    
    @FXML
    public void stopGame(){ // остановка игры
        thread.interrupt();

        pause.setDisable(true);
        end.setDisable(true);
        shoot.setDisable(true);
        start.setDisable(false);

        try {
            thread.join();
        } catch (InterruptedException e) {
            System.out.println(e);
        }

        if (!thread.isAlive()){
            Platform.runLater(()->{
                BigCircle.setLayoutY(177);
                LittleCircle.setLayoutY(177);
                NumShootScore = 0;
                shootScore.setText(Integer.toString(0));
                scorePoints.setText(Integer.toString(0));
                reset(78, 193, arrow);
                isShot = false;
            });
        }
    }
}
