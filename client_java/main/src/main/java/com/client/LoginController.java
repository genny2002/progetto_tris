package com.client;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private Button invia_button;

    @FXML
    private TextField name_textField;

    @FXML
    private void handleClickInviaButton() throws IOException {
        String nomeGiocatore = name_textField.getText(); // Ottieni il nome del giocatore
        HomePageController.setNomeGiocatore(nomeGiocatore);  // Passa il nome al controller della home page
        App.setRoot("homePage");
    }
}
