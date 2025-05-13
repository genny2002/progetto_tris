package com.client;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import com.client.Connessione.NotificaListener; // Ensure this is the correct package for NotificaListener
import com.client.Model.Richiesta;
import com.client.Connessione.Connessione; // Ensure this is the correct package for NotificaListener

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

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
    @FXML private ListView<HBox> notifiche_list;

    private boolean isXTurn = true; // True = 'X', False = 'O'
    public static Connessione connessione;
    public static Queue<Richiesta> richiesteRicevute = new LinkedList<>();
    private static ObservableList<String> richiesteList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
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

        Thread notificaThread = new Thread(new NotificaListener(connessione.clientSocket, this));
        notificaThread.start();
    }

    private void handleMove(Button button) {
        System.out.println("Button clicked: " + button.getId());
        if (button.getText().isEmpty()) {
            button.setText(isXTurn ? "X" : "O");
            isXTurn = !isXTurn; // Cambia turno
        }
    }

    public static void setClientSocket(Connessione newConnessione) {
        connessione = newConnessione;
    }


    public /*static*/ void addNuovaRichiesta(Richiesta richiesta) {
        //richiesteRicevute.add(richiesta);
        

        Platform.runLater(() -> {
            // Crea una riga per la richiesta
            HBox riga = new HBox(10); // Spaziatura di 10 tra gli elementi
            Text testoRichiesta = new Text("Richiesta: " + richiesta.messaggio + " (ID: " + richiesta.idRichiesta + ")");
            Button accettaButton = new Button("Accetta");
            Button rifiutaButton = new Button("Rifiuta");

            // Azioni dei pulsanti
            accettaButton.setOnAction(e -> handleAccept(richiesta));
            rifiutaButton.setOnAction(e -> handleReject(richiesta));

            // Aggiungi gli elementi alla riga
            riga.getChildren().addAll(testoRichiesta, accettaButton, rifiutaButton);

            // Aggiungi la riga alla ListView
            notifiche_list.getItems().add(riga);
        });
    }

    private static void handleAccept(Richiesta richiesta) {
        System.out.println("Richiesta accettata: " + richiesta.messaggio);
        // Logica per accettare la richiesta (es: invio al server)
    }

    private static void handleReject(Richiesta richiesta) {
        String response = connessione.getClientSocket("Richiesta:deleteRifiutaRichiesta:" + richiesta.idRichiesta);
        System.out.println("Risposta dal server ricevuta dopo il rifiuto della richiesta: " + response);
        //connessione.closeSocket();
        System.out.println("Richiesta " + richiesta.idRichiesta + "rifiutata");
        // Logica per rifiutare la richiesta (es: invio al server)
    }
}
