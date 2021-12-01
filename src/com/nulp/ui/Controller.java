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

//    @FXML
//    public void readFields() throws IOException {
//        a = Integer.parseInt(floorcountid.getText());
//        b = Integer.parseInt(elevatorcountid.getText());
//        c = Integer.parseInt(humanspeedid.getText());
//
//    }

    public void readFields(javafx.event.ActionEvent actionEvent) throws IOException {
        floorCounter = Integer.parseInt(floorcountid.getText());
        elevatorCounter = Integer.parseInt(elevatorcountid.getText());
        humanGenerationSpeed = Integer.parseInt(humanspeedid.getText());
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Scene2.fxml"));
        root = loader.load();

        stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);

        ElevatorConfigWindowController s = loader.getController();
        s.SetData("DATA");

        stage.show();

    }
}
