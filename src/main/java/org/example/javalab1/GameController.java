package org.example.javalab1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.event.ActionEvent;

public class GameController {

    @FXML
    private Pane pane1;

    @FXML
    private Circle BigCircle;

    @FXML
    private Circle LittleCircle;

    @FXML
    private Label scoreLabel;

    private int score = 0;
    private boolean running = false;
    private Line arrow;

    @FXML
    void startGame(ActionEvent event) {
        running = true;
        score = 0;

        Thread thread = new Thread(() -> {
            while (running) {
                Platform.runLater(() -> {
                    BigCircle.setLayoutY(BigCircle.getLayoutY() + 2);
                    LittleCircle.setLayoutY(LittleCircle.getLayoutY() + 3);
                    if (arrow != null) {
                        arrow.setEndY(arrow.getEndY() - 5);
                        if (arrow.getEndY() < 0 || checkCollision(arrow)) {
                            pane1.getChildren().remove(arrow);
                            arrow = null;
                        }
                    }
                });

                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }

    @FXML
    void stopGame(ActionEvent event) {
        running = false;
    }

    @FXML
    void shoot(MouseEvent event) {
        if (arrow == null) {
            arrow = new Line(event.getX(), event.getY(), event.getX(), 0);
            arrow.setStroke(Color.BLACK);
            pane1.getChildren().add(arrow);
        }
    }

    private boolean checkCollision(Line arrow) {
        double arrowEndX = arrow.getEndX();
        double arrowEndY = arrow.getEndY();
        double bigCircleCenterX = BigCircle.getCenterX();
        double bigCircleCenterY = BigCircle.getCenterY();
        double littleCircleCenterX = LittleCircle.getCenterX();
        double littleCircleCenterY = LittleCircle.getCenterY();
        double bigCircleRadius = BigCircle.getRadius();
        double littleCircleRadius = LittleCircle.getRadius();
        double dx, dy, distance;

        // Проверка столкновения со большой целью
        dx = arrowEndX - bigCircleCenterX;
        dy = arrowEndY - bigCircleCenterY;
        distance = Math.sqrt(dx * dx + dy * dy);
        if (distance <= bigCircleRadius) {
            score++;
            return true;
        }

        // Проверка столкновения с маленькой целью
        dx = arrowEndX - littleCircleCenterX;
        dy = arrowEndY - littleCircleCenterY;
        distance = Math.sqrt(dx * dx + dy * dy);
        if (distance <= littleCircleRadius) {
            score += 2;
            return true;
        }

        return false;
    }
}
