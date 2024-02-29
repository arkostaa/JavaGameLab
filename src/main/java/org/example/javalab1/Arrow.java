package org.example.javalab1;

import javafx.scene.Group;

public class Arrow {
    private Group arrow;
    private double layoutX;
    private double layoutY;

    public Arrow(Group arrow, double layoutX, double layoutY) {
        this.layoutX = layoutX;
        this.layoutY = layoutY;
        this.arrow = arrow;

    }

    public void move(int a){
        layoutX += a;
        arrow.setLayoutX(layoutX);
    }

    public double getLayoutX() {
        return layoutX;
    }

    public double getLayoutY() {
        return layoutY;
    }

    public void setLayoutX(double layoutX) {
        this.layoutX = layoutX;
        arrow.setLayoutX(layoutX);
    }

    public void setLayoutY(double layoutY) {
        this.layoutY = layoutY;
        arrow.setLayoutY(layoutY);
    }
}
