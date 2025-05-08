package com.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.client.Connessione.Connessione;
import com.client.Model.Partita;

import java.net.Socket;

public class HomePageController {
    @FXML
    private Button invia_button;

    @FXML
    private Label benvenuto_label;

    @FXML
    private ListView<HBox> partite_list;

    private static String nomeGiocatore;

    public static void setNomeGiocatore(String nome) {
        nomeGiocatore = nome; // Metodo per impostare il nome del giocatore
    }

    @FXML
    private void initialize() {
        if (nomeGiocatore != null && !nomeGiocatore.isEmpty()) {
            benvenuto_label.setText("Benvenuto, " + nomeGiocatore + "!");
        } else {
            benvenuto_label.setText("Benvenuto!");
        }

        initPartiteInAttesa();
    }

    private void initPartiteInAttesa() {
        // Simula il caricamento delle partite in attesa
        List<Partita> partite = getPartiteInAttesa();

        for (Partita partita : partite) {
            HBox riga = new HBox(10); // Layout orizzontale per ogni partita
            Text idPartita = new Text("ID: " + partita.getId());
            Text nomeCreatore = new Text("Creatore: " + partita.getNomeCreatore());
            Button partecipaButton = new Button("Partecipa");

            // Aggiungi un'azione al pulsante "Partecipa"
            partecipaButton.setOnAction(e -> partecipaAPartita(partita.getId()));

            // Aggiungi gli elementi alla riga
            riga.getChildren().addAll(idPartita, nomeCreatore, partecipaButton);

            // Aggiungi la riga al ListView
            partite_list.getItems().add(riga);
        }
    }

    private List<Partita> getPartiteInAttesa() {    //RECUPERARE LE PARTITE IN ATTESA DAL SERVER
        String partiteString= Connessione.getClientSocket("Partita:getPartiteInAttesa:");
        if(partiteString == null || partiteString.isEmpty()) {
            System.out.println("Nessuna partita in attesa.");
            return new ArrayList<>(); // Restituisce una lista vuota se non ci sono partite
        }
        System.out.println("Partite in attesa: " + partiteString);
        List<Partita> partite = Partita.convertToObjects(partiteString);

        return partite;
    }

    private void partecipaAPartita(int idPartita) {
        System.out.println("Partecipando alla partita con ID: " + idPartita);
        // Logica per partecipare alla partita
    }

    @FXML
    private void handleClickCreaNuovaPartitaButton() throws IOException {
        String partiteString= Connessione.getClientSocket("Partita:putCreaPartita:" + nomeGiocatore);
        System.out.println(partiteString);
        //List<Partita> partite = Partita.convertToObjects(partiteString);
        App.setRoot("match");
    }    
}


