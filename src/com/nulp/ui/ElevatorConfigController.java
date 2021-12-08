package com.nulp.ui;
import com.nulp.logic.configuration.*;
import com.nulp.logic.strategy.*;

import com.nulp.logic.utils.MyLogger;
import com.nulp.logic.configuration.MainConfiguration;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class ElevatorConfigController implements Initializable {
    private static final Logger LOGGER = MyLogger.getLOGGER();

    @FXML
    private AnchorPane scene;

    @FXML
    private GridPane elevatorsConfigDataGrid;

    @FXML
    private Button runSimulationButton;

    private int floorNumber;
    private int elevatorNumber;
    private int passengersGenerationSpeed;
    private ArrayList<ElevatorConfiguration> elevatorConfigurations;

    // vars to open simulation window
    private Stage simulationWindowStage;
    private Scene simulationWindowScene;
    private Parent simulationWindowRoot;

    void setBaseConfiguration(int floorNumber, int elevatorNumber, int passengersGenerationSpeed) {
        this.floorNumber = floorNumber;
        this.elevatorNumber = elevatorNumber;
        this.passengersGenerationSpeed = passengersGenerationSpeed;

        elevatorConfigurations = new ArrayList<>(elevatorNumber);
        LOGGER.info("Set base configuration to Elevator Config Controller");

        for (int i = 0; i < elevatorNumber; ++i) {
            elevatorConfigurations.add(new ElevatorConfiguration());

            elevatorsConfigDataGrid.addRow(i);

            var elevatorName = new Label("Ліфт #" + String.valueOf(i + 1));
            elevatorsConfigDataGrid.add(elevatorName, 0, i);
            elevatorsConfigDataGrid.setHalignment(getNodeFromGridPane(elevatorsConfigDataGrid, 0, i), HPos.CENTER);

            ChoiceBox<String> elevatorStrategyChoiseBox = new ChoiceBox<String>(
                    FXCollections.observableArrayList("Plain", "Preemptive"));
            elevatorStrategyChoiseBox.setValue("Plain");
            elevatorsConfigDataGrid.add(elevatorStrategyChoiseBox, 1, i);
            elevatorsConfigDataGrid.setHalignment(getNodeFromGridPane(elevatorsConfigDataGrid, 1, i), HPos.CENTER);

            var maxWeightTextBox = new javafx.scene.control.TextField();
            maxWeightTextBox.setText(String.valueOf(MainConfiguration.DEFAULT_WEIGHT));
            maxWeightTextBox.textProperty().addListener(new TextFieldNumbersOnlyValidator(maxWeightTextBox));
            elevatorsConfigDataGrid.add(maxWeightTextBox, 2, i);
            elevatorsConfigDataGrid.setHalignment(getNodeFromGridPane(elevatorsConfigDataGrid, 2, i), HPos.CENTER);

            var maxPeopleInElevator = new javafx.scene.control.TextField();
            maxPeopleInElevator.setText(String.valueOf(MainConfiguration.DEFAULT_PASSENGERS));
            maxPeopleInElevator.textProperty().addListener(new TextFieldNumbersOnlyValidator(maxPeopleInElevator));
            elevatorsConfigDataGrid.add(maxPeopleInElevator, 3, i);
            elevatorsConfigDataGrid.setHalignment(getNodeFromGridPane(elevatorsConfigDataGrid, 3, i), HPos.CENTER);

            var elevatorSpeed = new javafx.scene.control.TextField();
            elevatorSpeed.setText(String.valueOf(MainConfiguration.DEFAULT_ELEVATOR_SPEED));
            elevatorSpeed.textProperty().addListener(new TextFieldNumbersOnlyValidator(elevatorSpeed));
            elevatorsConfigDataGrid.add(elevatorSpeed, 4, i);
            elevatorsConfigDataGrid.setHalignment(getNodeFromGridPane(elevatorsConfigDataGrid, 4, i), HPos.CENTER);
        }
    }

    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    public void runSimulation(ActionEvent actionEvent) throws IOException {
        LOGGER.info("Start elevators simulation");
        int currentRow;
        for (Node node : elevatorsConfigDataGrid.getChildren()) {
            currentRow = GridPane.getRowIndex(node);

            if (GridPane.getColumnIndex(node) == 0) // skip labels
                continue;

            if (GridPane.getColumnIndex(node) == 1) { // elevator strategy
                String elevatorStrategy = ((ChoiceBox<String>) node).getValue();

                if (elevatorStrategy.equals("Plain"))
                    elevatorConfigurations.get(currentRow).strategy = new PlainStrategy();
                else if (elevatorStrategy.equals("Preemptive"))
                    elevatorConfigurations.get(currentRow).strategy = new PreemtiveStrategy();
            } else if (GridPane.getColumnIndex(node) == 2) { // max weigth

                try {
                    int maxElevatorLoad = Integer.parseInt (((TextField)node).getText());

                    if (maxElevatorLoad < MainConfiguration.MIN_ELEVATOR_LOAD ||
                            maxElevatorLoad > MainConfiguration.MAX_ELEVATOR_LOAD)
                    {
                        generateAlertOnInvalidValue("вантажності",
                                MainConfiguration.MIN_ELEVATOR_LOAD, MainConfiguration.MAX_ELEVATOR_LOAD,
                                currentRow + 1);
                        return;
                    }

                    elevatorConfigurations.get(currentRow).weight = maxElevatorLoad;
                } catch(NumberFormatException exc)
                {
                    generateAlertOnEmptyNumberField("вантажності", currentRow + 1);
                    return;
                }
            } else if (GridPane.getColumnIndex(node) == 3) { // max passengers count
                try {
                    int maxPassengerNumber = Integer.parseInt (((TextField)node).getText());

                    if (maxPassengerNumber < MainConfiguration.MIN_PASSENGERS_COUNT ||
                            maxPassengerNumber > MainConfiguration.MAX_PASSENGERS_COUNT)
                    {
                        generateAlertOnInvalidValue("к-сті людей",
                                MainConfiguration.MIN_PASSENGERS_COUNT, MainConfiguration.MAX_PASSENGERS_COUNT,
                                currentRow + 1);
                        return;
                    }

                    elevatorConfigurations.get(currentRow).capacity = maxPassengerNumber;
                } catch(NumberFormatException exc)
                {
                    generateAlertOnEmptyNumberField("к-сті людей", currentRow + 1);
                    return;
                }
            } else if (GridPane.getColumnIndex(node) == 4) { // speed
                try {
                    int maxElevatorsSpeed = Integer.parseInt (((TextField)node).getText());

                    if (maxElevatorsSpeed < MainConfiguration.MIN_ELEVATOR_SPEED ||
                            maxElevatorsSpeed > MainConfiguration.MAX_ELEVATOR_SPEED)
                    {
                        generateAlertOnInvalidValue("швидкості",
                                MainConfiguration.MIN_ELEVATOR_SPEED, MainConfiguration.MAX_ELEVATOR_SPEED,
                                currentRow + 1);
                        return;
                    }

                    elevatorConfigurations.get(currentRow).speed = maxElevatorsSpeed;
                } catch(NumberFormatException exc)
                {
                    generateAlertOnEmptyNumberField("швидкості", currentRow + 1);
                    return;
                }
            }
        }

        runSimulationWindow(actionEvent);
    }

    void generateAlertOnEmptyNumberField(String fieldName, int atRow)
    {
        LOGGER.info("Enter empty value");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Невалідні дані");
        alert.setContentText("Пусте поле " + fieldName + " для ліфта №" + atRow);
        alert.showAndWait().ifPresent(rs -> {
        });
    }

    void generateAlertOnInvalidValue(String fieldName, int minValue, int maxValue, int atRow)
    {
        LOGGER.info("Enter invalid value");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Невалідні дані");
        alert.setContentText("Невалідне значення " + fieldName + " для ліфта № " + atRow +
                "\n мін. можливе значення: " + minValue + "\n макс. можливе значення: " + maxValue + "\n");
        alert.showAndWait().ifPresent(rs -> {
        });
    }

    void runSimulationWindow(ActionEvent actionEvent)
    {
        try {
            LOGGER.info("Main simulation window running...");
            BuildingConfiguration.setInstance(elevatorConfigurations, floorNumber,
                    passengersGenerationSpeed);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("mainApp.fxml"));
            simulationWindowRoot = loader.load();

            simulationWindowStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            simulationWindowScene = new Scene(simulationWindowRoot);
            simulationWindowStage.setScene(simulationWindowScene);

        } catch (IOException exc) {

        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
