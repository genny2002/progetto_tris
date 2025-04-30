package com.client;



import java.io.IOException;


import javafx.fxml.FXML;
import javafx.scene.control.Button;


public class LoginController {
    @FXML
    private Button invia_button;

    @FXML
    private void Ciao() throws IOException {
        System.out.println("Ciao");
        // Qui puoi aggiungere il codice per gestire l'azione del pulsante
        // Ad esempio, puoi passare alla schermata principale dell'applicazione
        // App.setRoot("main");
    }
}
