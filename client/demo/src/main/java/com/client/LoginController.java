package com.client;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

public class LoginController {
    @FXML
    private Button invia_button;

    @FXML
    private TextField name_textField;

    @FXML
    private Label versione_label;

    @FXML
    private void initialize() {
        versione_label.setText("V 1.0.0");
    }

    @FXML
    private void handleClickInviaButton() throws IOException {
        String nomeGiocatore = name_textField.getText();
        MainController.setNomeGiocatore(nomeGiocatore);
        App.setRoot("main");
    }
}
