package com.nulp.ui;

import com.nulp.logic.configuration.*;
import com.nulp.logic.strategy.*;
import com.nulp.logic.entities.*;
import com.nulp.logic.utils.MyLogger;
import com.nulp.ui.models.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class ElevatorsScene implements IElevatorScene  {
    private static final Logger LOGGER = MyLogger.getLOGGER();

    BuildingConfiguration buildingConfiguration;

    @FXML
    private ResourceBundle mResources;

    @FXML
    private URL mLocation;

    @FXML
    private Button nextButton;

    @FXML
    private AnchorPane mPane = new AnchorPane();
    private Group mElements = new Group();


    private Building mBuilding;
    private List<ElevatorView> mElevatorViews = new ArrayList<>();
    private List<Rectangle> mFloors = new ArrayList<>();
    private List<PassengerView> mPassengerViews = new ArrayList<>();

    private int mFloorCount;
    private int mElevatorCount ;



    @FXML
    void initialize() {
        start();
    }

    public void start() {
        LOGGER.info("Building the scene background");
        buildingConfiguration = BuildingConfiguration.getInstance();
        mFloorCount = buildingConfiguration.getFloors();
        mElevatorCount = buildingConfiguration.getElevatorConfiguration().size();
        mBuilding = new Building(buildingConfiguration, this);

        initializeElevators();
        initializeFloors();
        initializePassengers();

        mPane.getChildren().add(mElements);
        mPane.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        mPane.setPrefHeight(mFloorCount * ElevatorView.HEIGHT);
        mPane.setPrefWidth(MainConfiguration.FLOOR_WAITING_ZONE_WIDTH + mElevatorCount * ElevatorView.WIDTH
                + MainConfiguration.ELEVATOR_LEFT_MARGIN * mElevatorCount + ElevatorView.HEIGHT);
        mPane.getChildren().addAll(initializeFloorNumber());

        // TODO: ADD BUTTONS TO TURN ON/OFF PASSENGER GENERATING/ELEVATORS
        mBuilding.startPassengerGenerating();
        mBuilding.startElevatorsMovement();
    }

    public List<Label> initializeFloorNumber() {
        LOGGER.info("Initializing the floor number");
        List<Label> floorNumbers = new ArrayList<>();

        for (int i = 0; i < mFloorCount; i++) {
            Label label = new Label(String.valueOf(mFloorCount - i));
            label.setStyle("-fx-font-size: 52");
            label.setMaxWidth(ElevatorView.HEIGHT);
            if ((mFloorCount - i) < 10)
                label.setLayoutX(20);
            else
                label.setLayoutX(5);

            label.setLayoutY(i * ElevatorView.HEIGHT);

            floorNumbers.add(label);
        }

        return floorNumbers;
    }

    public void initializeFloors() {
        LOGGER.info("Initializing the floors itself");
        for (int i = 0; i < mFloorCount; i++) {
            Rectangle rectangle = new Rectangle();

            rectangle.setWidth(ElevatorView.HEIGHT);
            rectangle.setHeight(ElevatorView.HEIGHT);

            double x = 0;
            double y = i * ElevatorView.HEIGHT;

            rectangle.setX(x);
            rectangle.setY(y);

            rectangle.setFill(Color.WHITESMOKE);

            mFloors.add(rectangle);
        }

        mFloors.forEach(x -> mElements.getChildren().add(x));
    }

    public void initializeElevators() {
        LOGGER.info("Initializing elevators");
        for (int i = 0; i < mBuilding.getElevators().size(); i++) {
            StackPane stack = new StackPane();

            // calculating elevator coordinates with margins
            double x = MainConfiguration.FLOOR_WAITING_ZONE_WIDTH + i * ElevatorView.WIDTH + (i + 1) * MainConfiguration.ELEVATOR_LEFT_MARGIN + ElevatorView.HEIGHT;
            double y = (mFloorCount) * ElevatorView.HEIGHT;

            stack.setLayoutX(x);
            stack.setLayoutY(y);


            ElevatorView elevatorView = new ElevatorView(stack, buildingConfiguration.getElevatorConfiguration().get(i));
            elevatorView.setElevatorID(mBuilding.getElevators().get(i).getId());
            elevatorView.setGroup(stack);
            mElevatorViews.add(elevatorView);
        }

        mElevatorViews.forEach(x -> mElements.getChildren().add(x.getGroup()));
    }

    public void initializePassengers() {
        LOGGER.info("Initializing passengers");
        for(int j = 0; j < mFloorCount; ++j) {
            for (int k = 0; k <  mElevatorCount; k++) {
                for (int i = 0; i < MainConfiguration.MAX_PASSENGERS; i++) {
                    StackPane stack = new StackPane();
                    var width = MainConfiguration.ELEVATOR_LEFT_MARGIN / MainConfiguration.MAX_PASSENGERS;

                    double x =  mFloors.get(0).getX() +
                            mFloors.get(0).getWidth() +
                            (k + 1) * (MainConfiguration.ELEVATOR_LEFT_MARGIN + ElevatorView.WIDTH) - PassengerView.WIDTH - ElevatorView.WIDTH -
                            i *  PassengerView.WIDTH;
                    double y = (mFloorCount - j - 1) *  ElevatorView.HEIGHT;;


                    stack.setLayoutX(x);
                    stack.setLayoutY(y);

                    mElements.getChildren().addAll(stack);
                    PassengerView passengerView = new PassengerView(i, stack);

                    mPassengerViews.add(passengerView);
                }
            }
        }


    }

    @Override
    public void moveElevator(Elevator elevator, int floor) {
        var elevatorItem = mElevatorViews.get(elevator.getId());
        var elevatorRect =  elevatorItem.getGroup();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                elevatorItem.getCurrentWeight().setText(String.valueOf(elevator.getCurrentWeight()));
                elevatorItem.getCurrentCapacity().setText(String.valueOf(elevator.getPassengers().size()));
                elevatorRect.setLayoutY((mFloorCount - floor - 1) * elevatorRect.getHeight());
            }
        });
        updatePassengers();
    }

    @Override
    public void updatePassengers() {
        mPassengerViews.forEach(passenger -> {
            passenger.getRectangle().setVisible(false);
        });
        for(int j = 0; j < mFloorCount; ++j) {
            for(int i = 0; i < mElevatorCount; ++i) {
                var passengers = mBuilding.getFloors().get(j).getPassengers(i);
                for(int k = 0; k < passengers.size(); ++k) {
                    int index = getPassengerIndex(j, i, k);
//                    System.out.println("floor " + j + " ,elevator " + i + " ,index " + k + " = " + index);
                    var passengerView = mPassengerViews.get(index);
                    int finalK = k;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (passengers.size() == 0 || finalK >= passengers.size() || passengers.get(finalK) == null) return;
                            passengerView.getRectangle().setVisible(true);
                            passengerView.getWeightLabel().setText(String.valueOf(passengers.get(finalK).getWeight()));
                            passengerView.getTargetLabel().setText(String.valueOf(passengers.get(finalK).getFloorTarget()));
                        }
                    });
                }
            }
        };
    }
    private int getPassengerIndex(int floor, int elevator, int index) {
        return floor * mElevatorCount * MainConfiguration.MAX_PASSENGERS +
                elevator * MainConfiguration.MAX_PASSENGERS
                + index;
    }
}

