package com.nulp.ui;

import com.nulp.logic.configuration.MainConfiguration;
import com.nulp.logic.utils.MyLogger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class Controller implements Initializable {
    private static final Logger LOGGER = MyLogger.getLOGGER();

    @FXML
    public Button button1;
    public TextField floorcountid;
    public TextField elevatorcountid;
    public TextField humanspeedid;
    int floorCounter, elevatorCounter, humanGenerationSpeed;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        floorcountid.setText(String.valueOf(MainConfiguration.DEFAULT_FLOORS));
        floorcountid.textProperty().addListener(new TextFieldNumbersOnlyValidator(floorcountid));

        elevatorcountid.setText(String.valueOf(MainConfiguration.DEFAULT_ELEVATORS));
        elevatorcountid.textProperty().addListener(new TextFieldNumbersOnlyValidator(elevatorcountid));

        humanspeedid.setText(String.valueOf(MainConfiguration.DEFAULT_PASSENGERS_GENERATION_SPEED));
        humanspeedid.textProperty().addListener(new TextFieldNumbersOnlyValidator(humanspeedid));
    }

    public void readFields(javafx.event.ActionEvent actionEvent) throws IOException {
        try{
            floorCounter = Integer.parseInt(floorcountid.getText());

            if (floorCounter < MainConfiguration.MIN_FLOORS_COUNT ||
                    floorCounter > MainConfiguration.MAX_FLOORS_COUNT)
            {
                generateAlertOnInvalidValue("кількості поверхів",
                        MainConfiguration.MIN_FLOORS_COUNT, MainConfiguration.MAX_FLOORS_COUNT);

                return;
            }
        } catch(NumberFormatException exc)
        {
            generateAlertOnEmptyNumberField("к-сті поверхів");
            return;
        }

        try{
            elevatorCounter = Integer.parseInt(elevatorcountid.getText());
            if (elevatorCounter < MainConfiguration.MIN_ELEVATORS_COUNT ||
                    elevatorCounter > MainConfiguration.MAX_ELEVATORS_COUNT)
            {
                generateAlertOnInvalidValue("кількості ліфтів",
                        MainConfiguration.MIN_ELEVATORS_COUNT, MainConfiguration.MAX_ELEVATORS_COUNT);

                return;
            }
        } catch(NumberFormatException exc)
        {
            generateAlertOnEmptyNumberField("кількості ліфтів");
            return;
        }

        try {
            humanGenerationSpeed = Integer.parseInt(humanspeedid.getText());
            if (humanGenerationSpeed < MainConfiguration.MIN_PASSENGERS_GENERATION_SPEED ||
                    humanGenerationSpeed > MainConfiguration.MAX_PASSENGERS_GENERATION_SPEED)
            {
                generateAlertOnInvalidValue("швидкості генерації пасажирів",
                        MainConfiguration.MIN_PASSENGERS_GENERATION_SPEED,
                        MainConfiguration.MAX_PASSENGERS_GENERATION_SPEED);

                return;
            }
        } catch(NumberFormatException exc)
        {
            generateAlertOnEmptyNumberField("швидкості генерації пасажирів");
            return;
        }

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("elevatorsConfigsWindow.fxml"));
        root = loader.load();

        stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);

        LOGGER.info("Got basic configurations : floors count = " + floorCounter +
                ", elevator count = " + elevatorCounter + ", human generation speed = " + humanGenerationSpeed);

        var s = (ElevatorConfigController)loader.getController();
        s.setBaseConfiguration(Integer.parseInt(floorcountid.getText()),
                Integer.parseInt(elevatorcountid.getText()),
                Integer.parseInt(humanspeedid.getText()));
        stage.show();
    }

    void generateAlertOnEmptyNumberField(String fieldName)
    {
        LOGGER.info("Enter empty value");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Невалідні дані");
        alert.setContentText("Пусте поле " + fieldName);
        alert.showAndWait().ifPresent(rs -> {
        });
    }

    void generateAlertOnInvalidValue(String fieldName, int minValue, int maxValue)
    {
        LOGGER.info("Enter invalid value");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Невалідні дані");
        alert.setContentText("Невалідне значення " + fieldName +
                "\n мін. можливе значення: " + minValue + "\n макс. можливе значення: " + maxValue + "\n");
        alert.showAndWait().ifPresent(rs -> {
        });
    }
}