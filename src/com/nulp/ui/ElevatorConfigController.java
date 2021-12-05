package com.nulp.ui;
import com.nulp.logic.configuration.*;
import com.nulp.logic.strategy.*;

import com.nulp.logic.utils.MyLogger;
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

            var elevatorName = new Label("Elevator " + String.valueOf(i + 1));
            elevatorsConfigDataGrid.add(elevatorName, 0, i);
            elevatorsConfigDataGrid.setHalignment(getNodeFromGridPane(elevatorsConfigDataGrid, 0, i), HPos.CENTER);

            ChoiceBox<String> elevatorStrategyChoiseBox = new ChoiceBox<String>(
                    FXCollections.observableArrayList("Plain", "Preemptive"));
            elevatorStrategyChoiseBox.setValue("Plain");
            elevatorsConfigDataGrid.add(elevatorStrategyChoiseBox, 1, i);
            elevatorsConfigDataGrid.setHalignment(getNodeFromGridPane(elevatorsConfigDataGrid, 1, i), HPos.CENTER);

            var maxWeightTextBox = new javafx.scene.control.TextField();
            maxWeightTextBox.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                    if (!newValue.matches("\\d*")) {
                        maxWeightTextBox.setText(newValue.replaceAll("[^\\d]", ""));
                    }
                }
            });
            elevatorsConfigDataGrid.add(maxWeightTextBox, 2, i);
            elevatorsConfigDataGrid.setHalignment(getNodeFromGridPane(elevatorsConfigDataGrid, 2, i), HPos.CENTER);

            var maxPeopleInElevator = new javafx.scene.control.TextField();
            maxPeopleInElevator.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                    if (!newValue.matches("\\d*")) {
                        maxPeopleInElevator.setText(newValue.replaceAll("[^\\d]", ""));
                    }
                }
            });
            elevatorsConfigDataGrid.add(maxPeopleInElevator, 3, i);
            elevatorsConfigDataGrid.setHalignment(getNodeFromGridPane(elevatorsConfigDataGrid, 3, i), HPos.CENTER);


            var elevatorSpeed = new javafx.scene.control.TextField();
            elevatorSpeed.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                    if (!newValue.matches("\\d*")) {
                        elevatorSpeed.setText(newValue.replaceAll("[^\\d]", ""));
                    }
                }
            });
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
                    int maxWeight = Integer.parseInt (((TextField)node).getText());
                    elevatorConfigurations.get(currentRow).weight = maxWeight;
                } catch(NumberFormatException exc)
                {
                    generateAlertOnEmptyNumberField("max weigth", currentRow + 1);
                    return;
                }
            } else if (GridPane.getColumnIndex(node) == 3) { // max passengers count
                try {
                    int maxPassengerNumber = Integer.parseInt (((TextField)node).getText());
                    elevatorConfigurations.get(currentRow).capacity = maxPassengerNumber;
                } catch(NumberFormatException exc)
                {
                    generateAlertOnEmptyNumberField("max passenger number", currentRow + 1);
                    return;
                }
            } else if (GridPane.getColumnIndex(node) == 4) { // speed
                try {
                    int maxElevators = Integer.parseInt (((TextField)node).getText());
                    elevatorConfigurations.get(currentRow).speed = maxElevators;
                } catch(NumberFormatException exc)
                {
                    generateAlertOnEmptyNumberField("elevator speed", currentRow + 1);
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
        alert.setTitle("Invalid data");
        alert.setContentText("Empty " + fieldName + " for " + "Elevator" + atRow);
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
