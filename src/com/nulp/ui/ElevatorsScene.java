package com.nulp.ui;

import com.nulp.logic.configuration.*;
import com.nulp.logic.entities.*;
import com.nulp.logic.utils.MyLogger;
import com.nulp.ui.models.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ElevatorsScene implements IElevatorScene  {
    private static final Logger LOGGER = MyLogger.getLOGGER();

    BuildingConfiguration buildingConfiguration;

    @FXML
    private BorderPane borderPane = new BorderPane();


    private AnchorPane buildingPane = new AnchorPane();
    private MenuBar menuBar = new MenuBar();

    private Group elements = new Group();
    private Building building;
    private List<ElevatorView> elevatorViews = new ArrayList<>();
    private List<Rectangle> floors = new ArrayList<>();
    private List<PassengerView> passengerViews = new ArrayList<>();

    private int floorCount;
    private int elevatorCount;

    @FXML
    void initialize() {

        initMenu();
        start();
    }

    private void initMenu() {
        Menu elevatorMenu = new Menu("Elevators");
        Menu passengersMenu = new Menu("Passengers");
        MenuItem startElevators = new MenuItem("Start elevators");
        MenuItem stopElevators = new MenuItem("Stop elevators");
        MenuItem startGeneration = new MenuItem("Start passengers generation");
        MenuItem stopGeneration = new MenuItem("Stop passengers generation");

        startElevators.setOnAction(e -> building.startElevatorsMovement());
        stopElevators.setOnAction(e -> building.stopElevatorsMovement());
        startGeneration.setOnAction(e -> {
            System.out.println("Start generating");
            building.startPassengerGenerating();
        });
        stopGeneration.setOnAction(e -> {
            System.out.println("Stop generating");
            building.stopPassengerGenerating();
        });

        elevatorMenu.getItems().addAll(startElevators, stopElevators);
        passengersMenu.getItems().addAll(startGeneration, stopGeneration);

        menuBar.getMenus().addAll(elevatorMenu, passengersMenu);
        borderPane.setTop(menuBar);
    }

    public void start() {
        LOGGER.info("Building the scene background");
        buildingConfiguration = BuildingConfiguration.getInstance();
        floorCount = buildingConfiguration.getFloors();
        elevatorCount = buildingConfiguration.getElevatorConfiguration().size();
        building = new Building(buildingConfiguration, this);

        initializeElevators();
        initializeFloors();
        initializePassengers();

        buildingPane.getChildren().add(elements);
        buildingPane.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        buildingPane.setPrefHeight(floorCount * ElevatorView.HEIGHT);
        buildingPane.setPrefWidth(MainConfiguration.FLOOR_WAITING_ZONE_WIDTH + elevatorCount * ElevatorView.WIDTH
                + MainConfiguration.ELEVATOR_LEFT_MARGIN * elevatorCount + ElevatorView.HEIGHT);
        buildingPane.getChildren().addAll(initializeFloorNumber());
        borderPane.setCenter(buildingPane);
    }

    public List<Label> initializeFloorNumber() {
        LOGGER.info("Initializing the floor number");
        List<Label> floorNumbers = new ArrayList<>();

        for (int i = 0; i < floorCount; i++) {
            Label label = new Label(String.valueOf(floorCount - i));
            label.setStyle("-fx-font-size: 52");
            label.setMaxWidth(ElevatorView.HEIGHT);
            if ((floorCount - i) < 10)
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
        for (int i = 0; i < floorCount; i++) {
            Rectangle rectangle = new Rectangle();

            rectangle.setWidth(ElevatorView.HEIGHT);
            rectangle.setHeight(ElevatorView.HEIGHT);

            double x = 0;
            double y = i * ElevatorView.HEIGHT;

            rectangle.setX(x);
            rectangle.setY(y);

            rectangle.setFill(Color.WHITESMOKE);

            floors.add(rectangle);
        }

        floors.forEach(x -> elements.getChildren().add(x));
    }

    public void initializeElevators() {
        LOGGER.info("Initializing elevators");
        for (int i = 0; i < building.getElevators().size(); i++) {
            StackPane stack = new StackPane();

            // calculating elevator coordinates with margins
            double x = MainConfiguration.FLOOR_WAITING_ZONE_WIDTH + i * ElevatorView.WIDTH + (i + 1) * MainConfiguration.ELEVATOR_LEFT_MARGIN + ElevatorView.HEIGHT;
            double y = (floorCount - 1) * ElevatorView.HEIGHT;

            stack.setLayoutX(x);
            stack.setLayoutY(y);


            ElevatorView elevatorView = new ElevatorView(stack, buildingConfiguration.getElevatorConfiguration().get(i));
            elevatorView.setElevatorID(building.getElevators().get(i).getId());
            elevatorView.setGroup(stack);
            elevatorViews.add(elevatorView);
        }

        elevatorViews.forEach(x -> elements.getChildren().add(x.getGroup()));
    }

    public void initializePassengers() {
        LOGGER.info("Initializing passengers");
        for(int j = 0; j < floorCount; ++j) {
            for (int k = 0; k < elevatorCount; k++) {
                for (int i = 0; i < MainConfiguration.MAX_PASSENGERS; i++) {
                    StackPane stack = new StackPane();
                    double x =  floors.get(0).getX() +
                            floors.get(0).getWidth() +
                            (k + 1) * (MainConfiguration.ELEVATOR_LEFT_MARGIN + ElevatorView.WIDTH) - PassengerView.WIDTH - ElevatorView.WIDTH -
                            i *  PassengerView.WIDTH;
                    double y = (floorCount - j - 1) *  ElevatorView.HEIGHT;;


                    stack.setLayoutX(x);
                    stack.setLayoutY(y);

                    elements.getChildren().addAll(stack);
                    PassengerView passengerView = new PassengerView(i, stack);
                    passengerView.getRectangle().setVisible(false);
                    passengerViews.add(passengerView);
                }
            }
        }


    }

    @Override
    public void moveElevator(Elevator elevator, int floor) {
        var elevatorItem = elevatorViews.get(elevator.getId());
        var elevatorRect =  elevatorItem.getGroup();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                elevatorItem.getCurrentWeight().setText(String.valueOf(elevator.getCurrentWeight()));
                elevatorItem.getCurrentCapacity().setText(String.valueOf(elevator.getPassengers().size()));
                elevatorRect.setLayoutY((floorCount - floor - 1) * elevatorRect.getHeight());
            }
        });
        updatePassengers();
    }

    @Override
    public void updatePassengers() {
        passengerViews.forEach(passenger -> {
            passenger.getRectangle().setVisible(false);
        });
        for(int j = 0; j < floorCount; ++j) {
            for(int i = 0; i < elevatorCount; ++i) {
                var passengers = building.getFloors().get(j).getPassengers(i);
                for(int k = 0; k < passengers.size(); ++k) {
                    int index = getPassengerIndex(j, i, k);
//                    System.out.println("floor " + j + " ,elevator " + i + " ,index " + k + " = " + index);
                    var passengerView = passengerViews.get(index);
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
        return floor * elevatorCount * MainConfiguration.MAX_PASSENGERS +
                elevator * MainConfiguration.MAX_PASSENGERS
                + index;
    }
}

