package com.client;
import java.io.IOException;
import java.net.Socket;
import com.client.Connessione.NotificaListener; // Ensure this is the correct package for NotificaListener

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MatchController {
    @FXML private Button button_00;
    @FXML private Button button_01;
    @FXML private Button button_02;
    @FXML private Button button_10;
    @FXML private Button button_11;
    @FXML private Button button_12;
    @FXML private Button button_20;
    @FXML private Button button_21;
    @FXML private Button button_22;

    private boolean isXTurn = true; // True = 'X', False = 'O'
    public static Socket clientSocket;

    @FXML
    public void initialize() {
        System.out.println("Ciao!!");
        // Aggiungi azioni per ogni pulsante
        button_00.setOnAction(e -> handleMove(button_00));
        button_01.setOnAction(e -> handleMove(button_01));
        button_02.setOnAction(e -> handleMove(button_02));
        button_10.setOnAction(e -> handleMove(button_10));
        button_11.setOnAction(e -> handleMove(button_11));
        button_12.setOnAction(e -> handleMove(button_12));
        button_20.setOnAction(e -> handleMove(button_20));
        button_21.setOnAction(e -> handleMove(button_21));
        button_22.setOnAction(e -> handleMove(button_22));

        Thread notificaThread = new Thread(new NotificaListener(clientSocket));
        notificaThread.start();
        
    }

    private void handleMove(Button button) {
        System.out.println("Button clicked: " + button.getId());
        if (button.getText().isEmpty()) {
            button.setText(isXTurn ? "X" : "O");
            isXTurn = !isXTurn; // Cambia turno
        }
    }

    public static void setClientSocket(Socket newSocket) {
        clientSocket = newSocket;
    }
}
