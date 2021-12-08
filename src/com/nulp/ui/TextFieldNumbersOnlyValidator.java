package com.nulp.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class TextFieldNumbersOnlyValidator implements ChangeListener<String> {
    private TextField textField;

    public TextFieldNumbersOnlyValidator(TextField textField)
    {
        this.textField = textField;
    }

    @Override
    public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
        if (!newValue.matches("\\d*")) {
            textField.setText(newValue.replaceAll("[^\\d]", ""));
        }
    }
}
