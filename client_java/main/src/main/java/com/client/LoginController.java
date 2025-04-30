package com.client;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class LoginController {
    @FXML
    private Button invia_button;

    @FXML
    private void handleButtonClick() {
        System.out.println("Bottone cliccato!");
    }
}
