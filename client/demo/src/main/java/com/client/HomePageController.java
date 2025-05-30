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
import com.client.Connessione.NotificaListener;
import com.client.Model.Partita;

public class HomePageController {
    @FXML
    private Label benvenuto_label;

    @FXML
    private ListView<HBox> partite_list;

    @FXML
    private Button logout_button;

    private static String nomeGiocatore;
    List<Partita> partite;
    Connessione connessione;
    public String nomeAvversario;
    private Thread notificaThreadHomePage;
    private NotificaListener notificaListenerHomePage;
    public String idNuovaPartita;

    /*@FXML
    private void handleClickLogoutButton() throws IOException {
        connessione.sendRequest(connessione.clientSocket, "logout:");
        String response = connessione.readResponse(connessione.clientSocket);
        System.out.println("Risposta dal server: " + response);
        //App.setRoot("loginPage");
    }*/

    public static void setNomeGiocatore(String nome) {
        nomeGiocatore = nome;
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

        notificaListenerHomePage = new NotificaListener(connessione.clientSocket, this, null);
        notificaThreadHomePage = new Thread(notificaListenerHomePage);
        notificaThreadHomePage.start();
    }

    public void initPartiteInAttesa() {
        this.partite = getPartiteInAttesa();

        for (Partita partita : partite) {
            HBox riga = new HBox(10);
            Text idPartita = new Text("ID: " + partita.getId());
            Text nomeCreatore = new Text("Creatore: " + partita.getNomeCreatore());
            Button partecipaButton = new Button("Partecipa");
            Text statoRichiesta = new Text("");

            partecipaButton.setOnAction(e -> {
                statoRichiesta.setText("richiesta in attesa");
                partecipaAPartita(partita.getId(), statoRichiesta);       
            });

            riga.getChildren().addAll(idPartita, nomeCreatore, partecipaButton, statoRichiesta);
            partite_list.getItems().add(riga);
        }
    }

    private List<Partita> getPartiteInAttesa() {
        String partiteString = connessione.getClientSocket("Partita:getPartiteInAttesa:");
        
        if (partiteString == null || partiteString.isEmpty()) {
            System.out.println("Nessuna partita in attesa.");
            return new ArrayList<>();
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

        notificaListenerHomePage.setStatoRichiesta(statoRichiesta);
        connessione.sendRequest(connessione.clientSocket, "Richiesta:putSendRequest:" + idPartita + "," + nomeGiocatore + "," + nomeCreatore);
    }

    public void setRichiestaRifiutata(Text statoRichiesta) {
        statoRichiesta.setText("richiesta rifiutata");
    }

    public void setRichiestaAccettata(Text statoRichiesta, String simbolo, String idPartita) throws IOException  {
        statoRichiesta.setText("richiesta accettata");

        MatchController.setClientSocket(connessione);
        MatchController.setNomeAvversario(nomeAvversario);
        MatchController.setSimbolo(simbolo);
        MatchController.setIdPartita(idPartita);
        MatchController.setThread(notificaThreadHomePage, notificaListenerHomePage);

        App.setRoot("match");
    }

    @FXML
    private void handleClickCreaNuovaPartitaButton() throws IOException {
        connessione.sendRequest(connessione.clientSocket, "Partita:putCreaPartita:" + nomeGiocatore);

        MatchController.setClientSocket(connessione);
        MatchController.setIdPartita(idNuovaPartita);
        MatchController.setThread(notificaThreadHomePage, notificaListenerHomePage);
        App.setRoot("match");
    }    
}
