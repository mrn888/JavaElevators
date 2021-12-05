package com.nulp.ui.models;

import com.nulp.logic.configuration.ElevatorConfiguration;
import com.nulp.logic.utils.MyLogger;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ElevatorView {
    private static final Logger LOGGER = MyLogger.getLOGGER();

    public static final int WIDTH = 75;
    public static final int HEIGHT = 90;

    private int elevatorID;
    private StackPane group;
    private Label maxWeight;
    private Label capacity;
    private Label currentWeight;
    private Label strategy;
    private Label currentCapacity;



    public ElevatorView(StackPane group, ElevatorConfiguration elevatorConfiguration) {
        this.group = group;
        Rectangle rectangle = new Rectangle();

        // setting elevator width and height
        rectangle.setWidth(ElevatorView.WIDTH);
        rectangle.setHeight(ElevatorView.HEIGHT);
        rectangle.setFill(Color.AQUA);

        maxWeight = new Label(String.valueOf(elevatorConfiguration.weight));
        capacity = new Label(String.valueOf(elevatorConfiguration.capacity));
        strategy = new Label(elevatorConfiguration.strategy.getStrategyName());
        currentWeight = new Label("0");
        currentCapacity = new Label("0");

        VBox vbox = new VBox(0,
            initLabel(strategy, "Str"),
            initLabel(maxWeight, "Max w"),
            initLabel(capacity, "Max c"),
            initLabel(currentWeight, "Cur w"),
            initLabel(currentCapacity, "Cur c"));
        group.getChildren().addAll(rectangle, vbox);
    }

    private StackPane initLabel(Label label, String labelNameText) {
        var labelName = new Label(labelNameText);
        StackPane.setAlignment(labelName, Pos.CENTER_LEFT);
        StackPane.setAlignment(label, Pos.CENTER_RIGHT);
        StackPane stack = new StackPane(labelName, label);
        return stack;
    }

    public StackPane getGroup() {
        return group;
    }

    public void setGroup(StackPane group) {
        this.group = group;
    }

    public void setElevatorID(int elevatorID) {
        this.elevatorID = elevatorID;
    }


    public Label getCurrentWeight() {
        return currentWeight;
    }

    public Label getCurrentCapacity() {
        return currentCapacity;
    }


}
