package com.nulp.ui.models;

import com.nulp.logic.configuration.MainConfiguration;
import javafx.animation.SequentialTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Stack;

public class PassengerView {
    public static final int WIDTH = MainConfiguration.ELEVATOR_LEFT_MARGIN / MainConfiguration.MAX_PASSENGERS;
    public static final int HEIGHT = ElevatorView.HEIGHT;

    private int passengerID;
    private StackPane group;
    private Label weightLabel;
    private Label targetLabel;
    private SequentialTransition transition = new SequentialTransition();

    public PassengerView(int passengerID, StackPane group) {
        this.group = group;
        Rectangle rectangle = new Rectangle();
        weightLabel = new Label(String.valueOf(passengerID));
        targetLabel = new Label("0");

        VBox vbox = new VBox(0,
                initLabel(weightLabel, "W"),
                initLabel(targetLabel, "T"));
        rectangle.setFill(Color.LIGHTYELLOW);
        rectangle.setWidth(WIDTH);
        rectangle.setHeight(HEIGHT);

        group.getChildren().addAll(rectangle, vbox);
        group.setVisible(false);

        this.passengerID = passengerID;
    }

    private StackPane initLabel(Label label, String labelNameText) {
        var labelName = new Label(labelNameText);
        StackPane.setAlignment(labelName, Pos.CENTER_LEFT);
        StackPane.setAlignment(label, Pos.CENTER_RIGHT);
        StackPane stack = new StackPane(labelName, label);
        return stack;
    }

    public StackPane getRectangle() {
        return group;
    }


    public Label getWeightLabel() {
        return this.weightLabel;
    }

    public Label getTargetLabel() {
        return this.targetLabel;
    }
}
