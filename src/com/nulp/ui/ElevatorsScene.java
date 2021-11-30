package com.nulp.ui;

import com.nulp.ui.models.*;
import com.nulp.ui.presenters.ElevatorsPresenter;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ElevatorsScene implements IElevatorsScene {
    public static final int ELEVATOR_ONE_FLOOR_MOVE_DURATION = 1000;
    public static final int ELEVATOR_LEFT_MARGIN = 30;

    public static final int FLOOR_WAITING_ZONE_WIDTH = 40;

    public static final int PASSENGER_ONE_ELEVATOR_MOVE_DURATION = 1000;

    @FXML
    private ResourceBundle mResources;

    @FXML
    private URL mLocation;

    @FXML
    private Button nextButton;

    @FXML
    private AnchorPane mPane = new AnchorPane();
    private Group mElements = new Group();

    private final Image mPersonRightHeadedImage = new Image("com/nulp/ui/resources/person_right_move.png");
    private final Image mPersonLeftHeadedImage = new Image("com/nulp/ui/resources/person_left_move.png");
    private final Image mElevatorImage = new Image("com/nulp/ui/resources/elevator_img.png");
    private final Image mBackgroundFloorImage = new Image("com/nulp/ui/resources/floor_background.jpg");
    private final Image mBackgroundImage = new Image("com/nulp/ui/resources/wallblack.jpg");
    private final BackgroundImage mBackground = new BackgroundImage(mBackgroundImage, null, null, null, null);

    private List<ElevatorView> mElevatorViews = new ArrayList<>();
    private List<Rectangle> mFloors = new ArrayList<>();
    private List<PassengerView> mPassengerViews = new ArrayList<>();

    private int mFloorCount = 10;
    private int mElevatorCount = 1;

    // Data from the main window
    private int mNumberOfElevators = 3;
    private int mNumberOfFloors = 3;
    private int mNumberOfPeople = 10;
    private int mStrategy = 2;

    ElevatorsPresenter mElevatorsPresenter;


    public void setElevatorsPresenter(ElevatorsPresenter mElevatorsPresenter) {
        this.mElevatorsPresenter = mElevatorsPresenter;
    }

    public void setElevatorsConfiguration(ArrayList<ElevatorConfiguration> elevatorConfigurations,
                                          int floorNumber, int passengersGenerationSpeed) {
        for (var config : elevatorConfigurations)
        {
            System.out.print(config.getElevatorStrategy().toString() + ", maxWeight: " + config.getMaxWeight() + ", maxPassengers: " +
                    config.getMaxPassengersNumber() + "\n");
        }
        // TODO: implement this method
    }

    @FXML
    void initialize() {

        nextButton.setOnAction(actionEvent -> {
            start();

            nextButton.setVisible(false);
        });
    }

    public void start() {
        // harcoded values
        mFloorCount = 6;
        mElevatorCount = 4;

        // initializeElevators(); need logic
        initializeFloors();

        mPane.getChildren().add(mElements);
        mPane.setBackground(new Background(mBackground));
        mPane.setPrefHeight(mFloorCount * ElevatorView.HEIGHT);
        mPane.setPrefWidth(FLOOR_WAITING_ZONE_WIDTH + mElevatorCount * ElevatorView.WIDTH
                + ELEVATOR_LEFT_MARGIN * mElevatorCount + ElevatorView.HEIGHT);
        mPane.getChildren().addAll(initializeFloorNumber());

    }

    public List<Label> initializeFloorNumber() {
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
        for (int i = 0; i < mFloorCount; i++) {
            Rectangle rectangle = new Rectangle();

            rectangle.setWidth(ElevatorView.HEIGHT);
            rectangle.setHeight(ElevatorView.HEIGHT);

            double x = 0;
            double y = i * ElevatorView.HEIGHT;

            rectangle.setX(x);
            rectangle.setY(y);

            rectangle.setFill(new ImagePattern(mBackgroundFloorImage));

            mFloors.add(rectangle);
        }

        mFloors.forEach(x -> mElements.getChildren().add(x));
    }

    public void initializeElevators() {
        for (int i = 0; mElevatorsPresenter.getElevators() != null && i < mElevatorsPresenter.getElevators().size(); i++) {
            Rectangle rectangle = new Rectangle();

            // setting elevator width and height
            rectangle.setWidth(ElevatorView.WIDTH);
            rectangle.setHeight(ElevatorView.HEIGHT);

            // setting corner radius
            rectangle.setArcHeight(10);
            rectangle.setArcWidth(10);

            // calculating elevator coordinates with margins
            double x = FLOOR_WAITING_ZONE_WIDTH + i * rectangle.getWidth() + (i + 1) * ELEVATOR_LEFT_MARGIN + ElevatorView.HEIGHT;
            double y = (mFloorCount - 1) * rectangle.getHeight();

            rectangle.setX(x);
            rectangle.setY(y);

            rectangle.setFill(new ImagePattern(mElevatorImage));

            ElevatorView elevatorView = new ElevatorView();
            elevatorView.setElevatorID(mElevatorsPresenter.getElevators().get(i).getId());
            elevatorView.setRectangle(rectangle);
            mElevatorViews.add(elevatorView);
        }

        mElevatorViews.forEach(x -> mElements.getChildren().add(x.getRectangle()));
    }

    public void saveParams(int numberOfElevators, int numberOfFloors, int numberOfPeople, int strategy) {
        this.mElevatorCount = numberOfElevators;
        this.mFloorCount = numberOfFloors;
        this.mNumberOfPeople = numberOfPeople;
        this.mStrategy = strategy;
    }

    // elevator methods
    private void planElevatorMove(int newFloor, ElevatorView elevatorView) {
        int oldFloor = 1;
        if (elevatorView.getSteps().size() > 0) {
            Step step = elevatorView.getSteps().get(elevatorView.getSteps().size() - 1);
            double x = step.getEnd().getX() - elevatorView.getRectangle().getWidth() / 2;
            double y = step.getEnd().getY() - elevatorView.getRectangle().getHeight() / 2;

            oldFloor = step.getFloor();
        } else {
            double x = elevatorView.getRectangle().getX();
            double y = elevatorView.getRectangle().getY();

            Elevator elevator = mElevatorsPresenter.findElevator(elevatorView.getElevatorID());
            if (elevator != null)
                oldFloor = elevator.getFloor();
        }

        // TODO: split on strategies
        // this is a strategy of lift ownership
        int difference = Math.abs(oldFloor - newFloor);
        if (difference > 0) {

            int increment = 1;
            if (oldFloor > newFloor)
                increment = -1;


            List<Step> steps = new ArrayList<>();
            double x = 0, y = 0;
            for (int i = 0; i < difference; i++) {
                Step step = new Step();
                step.setFloor(oldFloor + (i + 1) * increment);

                step.setDuration(ELEVATOR_ONE_FLOOR_MOVE_DURATION);

                // calculating step
                if (i == 0) {
                    if (elevatorView.getSteps().size() > 0) {
                        Step last = elevatorView.getSteps().get(elevatorView.getSteps().size() - 1);
                        x = last.getEnd().getX();
                        y = last.getEnd().getY();
                    } else {
                        x = elevatorView.getRectangle().getX() + elevatorView.getRectangle().getWidth() / 2;
                        y = elevatorView.getRectangle().getY() + elevatorView.getRectangle().getHeight() / 2;
                    }
                }
                step.setBeg(new Point2D(x, y));

                // moving one floor at a time
                y -= increment * elevatorView.getRectangle().getHeight();
                step.setEnd(new Point2D(x, y));

                if (step.getFloor() == newFloor)
                    step.setIsDestination(true);

                steps.add(step);
            }
            elevatorView.getSteps().addAll(steps);
        }
    }

    private void moveElevatorStepByStep(int newFloor, ElevatorView elevatorView) {
        Elevator elevator = mElevatorsPresenter.findElevator(elevatorView.getElevatorID());
//        if (elevator != null && elevator.getState() != ElevatorState.WAITING && elevator.getFloor() != newFloor) {
//            return;
//        }

        //planElevatorMove(newFloor, elevatorView);
        if (elevatorView.getSteps().isEmpty())
            return;

        if (elevatorView.getTransition() != null)
            elevatorView.getTransition().stop();
        else
            elevatorView.setTransition(new SequentialTransition());

        List<PathTransition> pathTransitions = new ArrayList<>();
        for (int i = 0; i < elevatorView.getSteps().size(); i++) {
            Step s = elevatorView.getSteps().get(i);

            Path p = new Path();
            p.getElements().add(new MoveTo(s.getBeg().getX(), s.getBeg().getY()));
            p.getElements().add(new LineTo(s.getEnd().getX(), s.getEnd().getY()));

            PathTransition pathTransition = new PathTransition();
            pathTransition.setDuration(Duration.millis(s.getDuration()));
            pathTransition.setPath(p);
            pathTransition.setNode(elevatorView.getRectangle());
            pathTransition.setOnFinished(actionEvent -> {
                double tX = elevatorView.getRectangle().getTranslateX();
                double tY = elevatorView.getRectangle().getTranslateY();

                if (elevatorView.getSteps().size() <= 0)
                    return;

                Step step = elevatorView.getSteps().get(0);

                int toDestination = 0;
                for (int j = 0; j < elevatorView.getSteps().size(); j++) {
                    toDestination++;
                    if (elevatorView.getSteps().get(j).isDestination())
                        break;
                }

                if (mElevatorsPresenter != null)
                    mElevatorsPresenter.onElevatorFloorChanged(elevatorView.getElevatorID(), step.getFloor());

                if (elevatorView.getSteps().get(0).isDestination() || elevatorView.getSteps().size() == 1) {
                    double newX = step.getEnd().getX() - elevatorView.getRectangle().getWidth() / 2;
                    double newY = step.getEnd().getY() - elevatorView.getRectangle().getHeight() / 2;
                    elevatorView.getRectangle().setX(newX);
                    elevatorView.getRectangle().setY(newY);
                    elevatorView.getRectangle().setTranslateX(0);
                    elevatorView.getRectangle().setTranslateY(0);
                }

                elevatorView.getSteps().remove(0);

                if (mElevatorsPresenter != null && (step.isDestination() || elevatorView.getSteps().size() == 0)) {
                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    mElevatorsPresenter.onElevatorArrived(elevatorView.getElevatorID());
                                }
                            },
                            10
                    );
                }
            });


            pathTransitions.add(pathTransition);
            if (s.isDestination())
                break;
        }

        elevatorView.getTransition().getChildren().clear();
        elevatorView.getTransition().getChildren().addAll(pathTransitions);
        elevatorView.getTransition().play();

//        if (elevator != null && elevator.getState() == ElevatorState.WAITING) {
//            if (mElevatorsPresenter != null)
//                mElevatorsPresenter.onElevatorDeparted(elevatorView.getElevatorID());
//        }
    }

    private void moveElevatorToDestination(int newFloor, ElevatorView elevatorView) {
        Elevator elevator = mElevatorsPresenter.findElevator(elevatorView.getElevatorID());
        if (elevator != null) {
            if (newFloor == elevator.getFloor())
                return;

            if (elevatorView.getSteps().size() > 0) {
                boolean wasInWay = false;
                for (int i = 0; i < elevatorView.getSteps().size(); i++) {
                    if (i != 0 && elevatorView.getSteps().get(i).getFloor() == newFloor) {
                        elevatorView.getSteps().get(i).setIsDestination(true);
                        wasInWay = true;
                        break;
                    }
                }

                if (wasInWay) {
                    wasInWay = false;
                    for (int i = elevatorView.getSteps().size() - 1; i >= 0; i--) {
                        if (wasInWay && elevatorView.getSteps().get(i).isDestination())
                            break;
                        else
                            wasInWay = true;

                        elevatorView.getSteps().remove(i);
                    }

                    if (elevatorView.getTimeline().getStatus() == Animation.Status.RUNNING)
                        return;
                }
            } else {
                if (elevatorView.getTimeline().getStatus() == Animation.Status.RUNNING)
                    return;

                //planElevatorMove(newFloor, elevatorView);
            }
        }

        if (elevatorView.getSteps().isEmpty())
            return;

        if (elevatorView.getTimeline() == null)
            elevatorView.setTimeline(new Timeline());

        List<KeyFrame> keyFrames = new ArrayList<>();
        double tYValue = 0;
        double duration = 0;
        for (int i = 0; i < elevatorView.getSteps().size(); i++) {
            Step s = elevatorView.getSteps().get(i);
            tYValue += s.getEnd().getY() - s.getBeg().getY();
            duration += s.getDuration();

            // TODO: test
            KeyFrame keyFrame = new KeyFrame(Duration.millis(duration),
                    actionEvent -> {
                        if (elevatorView.getSteps().size() <= 0)
                            return;

                        Step step = elevatorView.getSteps().get(0);
                        elevatorView.getSteps().remove(0);

                        if (mElevatorsPresenter != null)
                            mElevatorsPresenter.onElevatorFloorChanged(elevatorView.getElevatorID(), step.getFloor());

                        if (step.isDestination() || elevatorView.getSteps().size() == 0) {
                            double newX = step.getEnd().getX() - elevatorView.getRectangle().getWidth() / 2;
                            double newY = step.getEnd().getY() - elevatorView.getRectangle().getHeight() / 2;
                            elevatorView.getRectangle().setX(newX);
                            elevatorView.getRectangle().setY(newY);
                            elevatorView.getRectangle().setTranslateX(0);
                            elevatorView.getRectangle().setTranslateY(0);

                            elevatorView.getTimeline().stop();
                        }

                        if (mElevatorsPresenter != null && (step.isDestination() || elevatorView.getSteps().size() == 0)) {
                            new java.util.Timer().schedule(
                                    new java.util.TimerTask() {
                                        @Override
                                        public void run() {
                                            mElevatorsPresenter.onElevatorArrived(elevatorView.getElevatorID());
                                        }
                                    },
                                    10
                            );
                        }
                    },
                    new KeyValue(elevatorView.getRectangle().translateYProperty(), tYValue));

            keyFrames.add(keyFrame);
            if (s.isDestination())
                break;
        }

        elevatorView.getTimeline().getKeyFrames().clear();
        elevatorView.getTimeline().getKeyFrames().addAll(keyFrames);
        elevatorView.getTimeline().play();

//        if (elevator != null && elevator.getState() == StoppedState.WAITING) {
//            if (mElevatorsPresenter != null)
//                mElevatorsPresenter.onElevatorDeparted(elevatorView.getElevatorID());
//        }
    }
    // elevator methods

    @Override
    public void moveElevatorToFloor(int elevatorID, int newFloor) {
        ElevatorView elevatorView = null;
        for (ElevatorView ev : mElevatorViews) {
            if (ev.getElevatorID() == elevatorID) {
                elevatorView = ev;
                break;
            }
        }

        if (elevatorView != null) {
            moveElevatorToDestination(newFloor, elevatorView);
        }
    }

    // person methods
    private double getPassengerSpawnX(int floor) {
        double x = 0;
        if (mFloors.size() > 0) {
            x = mFloors.get(0).getX() + mFloors.get(0).getWidth();
            x += ELEVATOR_LEFT_MARGIN;
        }

        return x;
    }

    private void spawnNewPassenger(int passengerID, int floor) {
        Rectangle rectangle = new Rectangle();
        rectangle.setFill(new ImagePattern(mPersonRightHeadedImage));

        rectangle.setWidth(PassengerView.WIDTH);
        rectangle.setHeight(PassengerView.HEIGHT);

        double x = getPassengerSpawnX(floor);

        rectangle.setX(x);
        rectangle.setY((mFloorCount - floor) * PassengerView.HEIGHT);

        rectangle.setVisible(true);

        mElements.getChildren().add(rectangle);
        PassengerView passengerView = new PassengerView(passengerID, rectangle);
        mPassengerViews.add(passengerView);

        if (mElevatorsPresenter != null)
            mElevatorsPresenter.onPassengerSpawned(passengerID);
    }

    private void walkPassengerIntoElevator(int passengerID, int elevatorID) {
        PassengerView passengerView = mPassengerViews.stream()
                .filter(x -> passengerID == x.getPassengerID())
                .findAny()
                .orElse(null);

        ElevatorView elevatorView = mElevatorViews.stream()
                .filter(x -> elevatorID == x.getElevatorID())
                .findAny()
                .orElse(null);

        if (passengerView == null || elevatorView == null)
            return;

        Rectangle rectangle = passengerView.getRectangle();
        rectangle.setVisible(true);

        Path path = new Path();

        double x1, y1;
        x1 = rectangle.getX();
        y1 = rectangle.getY() + PassengerView.HEIGHT / 2;
        path.getElements().add(new MoveTo(x1, y1));

        double x2, y2;
        x2 = elevatorView.getRectangle().getX() + elevatorView.getRectangle().getWidth() / 2;
        y2 = rectangle.getY() + PassengerView.HEIGHT / 2;
        path.getElements().add(new LineTo(x2, y2));

        int duration = (int) (Math.abs(x2 - x1) / ElevatorView.WIDTH * PASSENGER_ONE_ELEVATOR_MOVE_DURATION);

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(duration));
        pathTransition.setNode(rectangle);
        pathTransition.setPath(path);

        pathTransition.setOnFinished(e -> {
            rectangle.setVisible(false);

            if (mElevatorsPresenter != null)
                mElevatorsPresenter.onPassengerEnteredElevator(passengerID, elevatorID);
        });

        passengerView.getTransition().getChildren().clear();
        passengerView.getTransition().getChildren().add(pathTransition);
        passengerView.getTransition().play();
    }

    private void walkPassengerFromElevator(int passengerID, int elevatorID) {
        PassengerView passengerView = mPassengerViews.stream()
                .filter(x -> passengerID == x.getPassengerID())
                .findAny()
                .orElse(null);


        ElevatorView elevatorView = mElevatorViews.stream()
                .filter(x -> elevatorID == x.getElevatorID())
                .findAny()
                .orElse(null);

        Elevator elevator = mElevatorsPresenter.findElevator(elevatorID);

        if (passengerView == null || elevatorView == null)
            return;

        Rectangle rectangle = passengerView.getRectangle();
        rectangle.setFill(new ImagePattern(mPersonLeftHeadedImage));
        rectangle.setVisible(true);

        Path path = new Path();

        double x1, y1;
        x1 = elevatorView.getRectangle().getX() + elevatorView.getRectangle().getWidth() / 2;
        y1 = elevatorView.getRectangle().getY() + elevatorView.getRectangle().getHeight() - passengerView.getRectangle().getHeight() / 2;
        path.getElements().add(new MoveTo(x1, y1));

        double x2, y2;
        x2 = 0;
        if (elevator != null)
            x2 = getPassengerSpawnX(elevator.getFloor());
        y2 = y1;
        path.getElements().add(new LineTo(x2, y2));

        int duration = (int) (Math.abs(x2 - x1) / ElevatorView.WIDTH * PASSENGER_ONE_ELEVATOR_MOVE_DURATION);

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(duration));
        pathTransition.setNode(rectangle);
        pathTransition.setPath(path);

        pathTransition.setOnFinished(e -> {
            rectangle.setVisible(false);
            mPassengerViews.remove(passengerView);

            if (mElevatorsPresenter != null) {
                mElevatorsPresenter.onPassengerDeleted(passengerID);
            }
        });

        passengerView.getTransition().getChildren().clear();
        passengerView.getTransition().getChildren().add(pathTransition);
        passengerView.getTransition().play();

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        if (mElevatorsPresenter != null) {
                            mElevatorsPresenter.onPassengerExitedElevator(passengerID, elevatorID);
                        }
                    }
                },
                PASSENGER_ONE_ELEVATOR_MOVE_DURATION / 2
        );

    }
    // person methods

    @Override
    public void spawnPassenger(int passengerID, int floor) {
        boolean isThere = false;
        for (PassengerView pv : mPassengerViews) {
            if (passengerID == pv.getPassengerID()) {
                isThere = true;
                break;
            }
        }

        if (isThere) {
            // TODO: implement some logic
        } else {
            spawnNewPassenger(passengerID, floor);
        }
    }

    @Override
    public void movePassengerIntoElevator(int passengerID, int elevatorID) {
        walkPassengerIntoElevator(passengerID, elevatorID);
    }

    @Override
    public void movePassengerFromElevator(int passengerID, int elevatorID) {
        walkPassengerFromElevator(passengerID, elevatorID);
    }
}

