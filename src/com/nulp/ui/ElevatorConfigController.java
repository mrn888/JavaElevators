package com.nulp.ui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class ElevatorConfigController implements Initializable {

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
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        elevatorConfigurations = new ArrayList<>(elevatorNumber);

        for (int i = 0; i < 6; ++i) { // <== TODO: Change this hardcoded 6 to "elevatorNumber"
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
        int currentRow;
        for (Node node : elevatorsConfigDataGrid.getChildren()) {
            currentRow = GridPane.getRowIndex(node);

            if (GridPane.getColumnIndex(node) == 0) // skip labels
                continue;

            if (GridPane.getColumnIndex(node) == 1) { // elevator strategy
                String elevatorStrategy = ((ChoiceBox<String>) node).getValue();

                if (elevatorStrategy.equals("Plain"))
                    elevatorConfigurations.get(currentRow).setElevatorStrategy(ElevatorStrategy.Plain);
                else if (elevatorStrategy.equals("Preemptive"))
                    elevatorConfigurations.get(currentRow).setElevatorStrategy(ElevatorStrategy.Preemptive);
            } else if (GridPane.getColumnIndex(node) == 2) { // max weigth

                try {
                    int maxWeight = Integer.parseInt (((TextField)node).getText());
                    elevatorConfigurations.get(currentRow).setMaxWeight(maxWeight);
                } catch(NumberFormatException exc)
                {
                    generateAlertOnEmptyNumberField("max weigth", currentRow + 1);
                    return;
                }
            } else if (GridPane.getColumnIndex(node) == 3) { // max passengers count
                try {
                    int maxPassengerNumber = Integer.parseInt (((TextField)node).getText());
                    elevatorConfigurations.get(currentRow).setMaxPassengersNumber(maxPassengerNumber);
                } catch(NumberFormatException exc)
                {
                    generateAlertOnEmptyNumberField("max passenger number", currentRow + 1);
                    return;
                }
            }
        }

        runSimulationWindow(actionEvent);
    }

    void generateAlertOnEmptyNumberField(String fieldName, int atRow)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid data");
        alert.setContentText("Empty " + fieldName + " for " + "Elevator" + atRow);
        alert.showAndWait().ifPresent(rs -> {
        });
    }

    void runSimulationWindow(ActionEvent actionEvent)
    {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("mainApp.fxml"));
            simulationWindowRoot = loader.load();

            simulationWindowStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            simulationWindowScene = new Scene(simulationWindowRoot);
            simulationWindowStage.setScene(simulationWindowScene);

            ElevatorsScene s = loader.getController();
            s.setElevatorsConfiguration(elevatorConfigurations, floorNumber, passengersGenerationSpeed);
        } catch (IOException exc) {

        }
    }
}
