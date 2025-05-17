package com.client;

import javafx.css.Match;
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
import com.client.Connessione.NotificaListener;
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
    List<Partita> partite;
    Connessione connessione;
    public String nomeProprietario;
    private Thread notificaThreadHomePage;

    public static void setNomeGiocatore(String nome) {
        nomeGiocatore = nome; // Metodo per impostare il nome del giocatore
    }

    @FXML
    private void initialize() {
        connessione = new Connessione();

        if (nomeGiocatore != null && !nomeGiocatore.isEmpty()) {
            benvenuto_label.setText("Benvenuto, " + nomeGiocatore + "!");
        } else {
            benvenuto_label.setText("Benvenuto!");
        }

        initPartiteInAttesa();
    }

    private void initPartiteInAttesa() {
        // Simula il caricamento delle partite in attesa
        this.partite = getPartiteInAttesa();

        for (Partita partita : partite) {
            HBox riga = new HBox(10); // Layout orizzontale per ogni partita
            Text idPartita = new Text("ID: " + partita.getId());
            Text nomeCreatore = new Text("Creatore: " + partita.getNomeCreatore());
            Button partecipaButton = new Button("Partecipa");
            Text statoRichiesta = new Text("");

            // Aggiungi un'azione al pulsante "Partecipa"
            partecipaButton.setOnAction(e -> {
                partecipaAPartita(partita.getId(), statoRichiesta);
                statoRichiesta.setText("richiesta in attesa");
            });

            // Aggiungi gli elementi alla riga
            riga.getChildren().addAll(idPartita, nomeCreatore, partecipaButton, statoRichiesta);

            // Aggiungi la riga al ListView
            partite_list.getItems().add(riga);
        }
    }

    private List<Partita> getPartiteInAttesa() {
        String partiteString = connessione.getClientSocket("Partita:getPartiteInAttesa:");
        
        if (partiteString == null || partiteString.isEmpty()) {
            System.out.println("Nessuna partita in attesa.");
            return new ArrayList<>(); // Restituisce una lista vuota se non ci sono partite
        }

        List<Partita> partite = Partita.convertToObjects(partiteString);
        
        return partite;
    }

    private void partecipaAPartita(int idPartita, Text statoRichiesta) {
        String nomeCreatore = null;

        for (Partita p : partite) {
            if (p.getId() == idPartita) {
                nomeCreatore = p.getNomeCreatore();
                break;
            }
        }

        String response = connessione.getClientSocket("Richiesta:putSendRequest:" + idPartita + "," + nomeGiocatore + "," + nomeCreatore); 
        notificaThreadHomePage = new Thread(new NotificaListener(connessione.clientSocket, this, statoRichiesta));
        notificaThreadHomePage.start();
    }

    public void setRichiestaRifiutata(Text statoRichiesta) {
        statoRichiesta.setText("richiesta rifiutata");
    }

    public void setRichiestaAccettata(Text statoRichiesta, String simbolo, String idPartita) throws IOException  {
        statoRichiesta.setText("richiesta accettata");

        if (notificaThreadHomePage != null && notificaThreadHomePage.isAlive()) {
            notificaThreadHomePage.interrupt();
        }

        MatchController.setClientSocket(connessione);
        MatchController.setNomeAvversario(nomeProprietario);
        MatchController.setSimbolo(simbolo);
        MatchController.setIdPartita(idPartita);
        App.setRoot("match");
    }

    @FXML
    private void handleClickCreaNuovaPartitaButton() throws IOException {
        String idPartita = connessione.getClientSocket("Partita:putCreaPartita:" + nomeGiocatore);
        MatchController.setClientSocket(connessione);
        MatchController.setIdPartita(idPartita);
        App.setRoot("match");
    }    
}


