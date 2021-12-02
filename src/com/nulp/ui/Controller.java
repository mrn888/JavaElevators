package com.nulp.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
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
    }

    public void readFields(javafx.event.ActionEvent actionEvent) throws IOException {
        floorCounter = Integer.parseInt(floorcountid.getText());
        elevatorCounter = Integer.parseInt(elevatorcountid.getText());
        humanGenerationSpeed = Integer.parseInt(humanspeedid.getText());
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("elevatorsConfigsWindow.fxml"));
        root = loader.load();

        stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);

        var s = (ElevatorConfigController)loader.getController();
        s.setBaseConfiguration(Integer.parseInt(floorcountid.getText()),
                Integer.parseInt(elevatorcountid.getText()),
                Integer.parseInt(humanspeedid.getText()));
        stage.show();

    }
}