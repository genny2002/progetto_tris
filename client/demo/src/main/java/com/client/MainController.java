package com.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.client.Connessione.Connessione;
import com.client.Connessione.NotificaListener;
import com.client.Model.Partita;
import com.client.Model.Richiesta;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;


public class MainController {
    @FXML private Label avvisi_label;
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
    @FXML private ListView<HBox> partite_list;
    @FXML private Button creaNuovaPartita_button;
    @FXML private GridPane gridPane;

    private static String nomeGiocatore;
    public static Connessione connessione;
    public static String serverSocket;
    private Thread notificaThreadHomePage;
    private NotificaListener notificaListenerHomePage;
    public List<Partita> partite;
    private HBox rigaDaEliminare;
    public String nomeAvversario=null;
    public String simboloGiocatore=null;    //simbolo del giocatore attuale (che sta giocando)
    public String simboloAvversario=null;
    public String idPartita=null;
    public String idNuovaPartita;
    private boolean isXTurn=false;
    private Alert partitaTerminataAlert;

    @FXML
    public void initialize() {
        connessione = new Connessione();
        serverSocket = connessione.readResponse(connessione.clientSocket);

        partite=new ArrayList<Partita>();

        avvisi_label.setText("Benvenuto, " + nomeGiocatore + "!");
        button_00.setVisible(false);
        button_01.setVisible(false);
        button_02.setVisible(false);
        button_10.setVisible(false);
        button_11.setVisible(false);
        button_12.setVisible(false);
        button_20.setVisible(false);
        button_21.setVisible(false);
        button_22.setVisible(false);
        notifiche_list.setVisible(false);

        button_00.setOnAction(e -> handleMove(button_00));
        button_01.setOnAction(e -> handleMove(button_01));
        button_02.setOnAction(e -> handleMove(button_02));
        button_10.setOnAction(e -> handleMove(button_10));
        button_11.setOnAction(e -> handleMove(button_11));
        button_12.setOnAction(e -> handleMove(button_12));
        button_20.setOnAction(e -> handleMove(button_20));
        button_21.setOnAction(e -> handleMove(button_21));
        button_22.setOnAction(e -> handleMove(button_22));

        notificaListenerHomePage = new NotificaListener(connessione.clientSocket, this);
        notificaThreadHomePage = new Thread(notificaListenerHomePage);
        notificaThreadHomePage.start();

        initPartiteInAttesa();
        
    }

    private void handleMove(Button button) {
        System.out.println("Button clicked: " + button.getId());
        char row = button.getId().charAt(7);
        char col=button.getId().charAt(8);

        button.setText(simboloGiocatore);
        button_00.setDisable(true);
        button_01.setDisable(true);
        button_02.setDisable(true);
        button_10.setDisable(true);
        button_11.setDisable(true);
        button_12.setDisable(true);
        button_20.setDisable(true);
        button_21.setDisable(true);
        button_22.setDisable(true);

        connessione.sendRequest(connessione.clientSocket, "Partita:putMove:" + row + "," + col + "," + simboloGiocatore + "," + idPartita);
    }

    public void setMossa(int row, int column) {
        Platform.runLater(() -> {
            Button button = null;

            switch (row) {
                case 0:
                    switch (column) {
                        case 0:
                            button = button_00;
                            break;
                        case 1:
                            button = button_01;
                            break;
                        case 2:
                            button = button_02;
                            break;
                        default:
                            break;
                    }
                    break;
                case 1:
                    switch (column) {
                        case 0:
                            button = button_10;
                            break;
                        case 1:
                            button = button_11;
                            break;
                        case 2:
                            button = button_12;
                            break;
                        default:
                            break;
                    }
                    break;
                case 2:
                    switch (column) {
                        case 0:
                            button = button_20;
                            break;
                        case 1:
                            button = button_21;
                            break;
                        case 2:
                            button = button_22;
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
            if (button != null) {
                button.setDisable(true);
                button.setText(simboloAvversario);
            }

            if(!(button_00.getText().equals("X")) && !(button_00.getText().equals("O"))){
                button_00.setDisable(false);
            }
            if(!(button_01.getText().equals("X")) && !(button_01.getText().equals("O"))){
                button_01.setDisable(false);
            }
            if(!(button_02.getText().equals("X")) && !(button_02.getText().equals("O"))){
                button_02.setDisable(false);
            }
            if(!(button_10.getText().equals("X")) && !(button_10.getText().equals("O"))){
                button_10.setDisable(false);
            }
            if(!(button_11.getText().equals("X")) && !(button_11.getText().equals("O"))){
                button_11.setDisable(false);
            }
            if(!(button_12.getText().equals("X")) && !(button_12.getText().equals("O"))){
                button_12.setDisable(false);
            }
            if(!(button_20.getText().equals("X")) && !(button_20.getText().equals("O"))){
                button_20.setDisable(false);
            }
            if(!(button_21.getText().equals("X")) && !(button_21.getText().equals("O"))){
                button_21.setDisable(false);
            }
            if(!(button_22.getText().equals("X")) && !(button_22.getText().equals("O"))){
                button_22.setDisable(false);
            }
        });
    }

    public void initPartiteInAttesa() {
        Platform.runLater(() -> {
            partite_list.getItems().clear();
        });

        connessione.sendRequest(connessione.clientSocket, "Partita:getPartiteInAttesa:");
    }

    public void showPartiteInAttesa(List<Partita> partite) {
        for (Partita partita : partite) {
            HBox riga = new HBox(10);
            Text idPartita = new Text("ID: " + partita.getId());
            Text nomeCreatore = new Text("Creatore: " + partita.getNomeCreatore());
            Button partecipaButton = new Button("Partecipa");
            Text statoRichiesta = new Text("");

            partecipaButton.setOnAction(e -> {
                statoRichiesta.setText("richiesta in attesa");
                partecipaButton.setDisable(true);
                partecipaAPartita(partita.getId(), statoRichiesta);       
            });

            riga.getChildren().addAll(idPartita, nomeCreatore, partecipaButton, statoRichiesta);
            partite_list.getItems().add(riga);
        }
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

    public void addNuovaRichiesta(Richiesta richiesta) {
    
        Platform.runLater(() -> {
            HBox riga = new HBox(10);
            Text testoRichiesta = new Text(richiesta.messaggio + " (ID: " + richiesta.idRichiesta + ")");
            Button accettaButton = new Button("Accetta");
            Button rifiutaButton = new Button("Rifiuta");

            accettaButton.setOnAction(e -> handleAccept(richiesta, riga));
            rifiutaButton.setOnAction(e -> handleReject(richiesta, riga));
            riga.getChildren().addAll(testoRichiesta, accettaButton, rifiutaButton);
            notifiche_list.getItems().add(riga);
        });
    }

    private void handleAccept(Richiesta richiesta, HBox riga) {
        connessione.sendRequest(connessione.clientSocket, "Richiesta:putAccettaRichiesta:" + richiesta.idRichiesta);
        rigaDaEliminare=riga;
        deleteNotifica();
    }

    private void handleReject(Richiesta richiesta, HBox riga) {
        connessione.sendRequest(connessione.clientSocket, "Richiesta:deleteRifiutaRichiesta:" + richiesta.idRichiesta);
        rigaDaEliminare=riga;
    }

    public void deleteNotifica() {
        Platform.runLater(() -> notifiche_list.getItems().remove(this.rigaDaEliminare));
    }

    public void setRichiestaAccettata(Text statoRichiesta, String simbolo) throws IOException  {
        Platform.runLater(() -> {
            if(statoRichiesta != null){
                statoRichiesta.setText("richiesta accettata");
            }

            setMatchControllerVisible();

            if(simboloGiocatore != null && simboloGiocatore.equals("X")){
                button_00.setDisable(false);
                button_01.setDisable(false);
                button_02.setDisable(false);
                button_10.setDisable(false);
                button_11.setDisable(false);
                button_12.setDisable(false);
                button_20.setDisable(false);
                button_21.setDisable(false);
                button_22.setDisable(false);
                isXTurn = true;
            }

            avvisi_label.setText("Stai giocando contro " + nomeAvversario + " con simbolo " + simboloGiocatore);
        });
    }

    public void setRichiestaRifiutata(Text statoRichiesta) {
        statoRichiesta.setText("richiesta rifiutata");
    }

    private void setMatchControllerVisible(){
        creaNuovaPartita_button.setVisible(false);
        partite_list.setVisible(false);
        notifiche_list.setVisible(true);

        button_00.setVisible(true);
        button_01.setVisible(true);
        button_02.setVisible(true);
        button_10.setVisible(true);
        button_11.setVisible(true);
        button_12.setVisible(true);
        button_20.setVisible(true);
        button_21.setVisible(true);
        button_22.setVisible(true);
    }

    public void setHomePageControllerVisible(){
        initPartiteInAttesa();
        creaNuovaPartita_button.setVisible(true);
        partite_list.setVisible(true);
        notifiche_list.setVisible(false);

        button_00.setVisible(false);
        button_01.setVisible(false);
        button_02.setVisible(false);
        button_10.setVisible(false);
        button_11.setVisible(false);
        button_12.setVisible(false);
        button_20.setVisible(false);
        button_21.setVisible(false);
        button_22.setVisible(false);

        avvisi_label.setText("Benvenuto, " + nomeGiocatore + "!");
    }

    public static void setNomeGiocatore(String nome) {
        nomeGiocatore = nome;
    }

    @FXML
    private void handleClickLogoutButton() throws IOException {
        connessione.sendRequest(connessione.clientSocket, "logout:" + serverSocket);
        App.setRoot("login");
    }

    @FXML
    private void handleClickCreaNuovaPartitaButton() throws IOException {
        connessione.sendRequest(connessione.clientSocket, "Partita:putCreaPartita:" + nomeGiocatore);
        setMatchControllerVisible();
        avvisi_label.setText("In attesa di un avversario...");
    }

    public void setPartitaTerminata(String message) {
        Platform.runLater(() -> { Platform.runLater(() -> avvisi_label.setText(message)); });
        Platform.runLater(() -> {
            partitaTerminataAlert = new Alert(AlertType.CONFIRMATION);
            Alert alert = partitaTerminataAlert;
                        
            alert.setTitle("Partita terminata");
            alert.setHeaderText("La partita è terminata!");
            alert.setContentText("Vuoi giocare un'altra partita con lo stesso avversario?");
                        
            Optional<ButtonType> result = alert.showAndWait();
                        
            if (result.isPresent() && result.get() == ButtonType.OK) {
                connessione.sendRequest(connessione.clientSocket, "Partita:putRematch:" + idPartita + ",1," + simboloGiocatore);
                System.out.println("Hai accettato il rematch");
            }else{
                connessione.sendRequest(connessione.clientSocket, "Partita:putRematch:" + idPartita + ",-1," + simboloGiocatore);
                System.out.println("Hai rifiutato il rematch");
            }
            partitaTerminataAlert = null;
        });
    }

    public void setRematch(){
        Platform.runLater(() -> {
            button_00.setText("");
            button_01.setText("");
            button_02.setText("");
            button_10.setText("");
            button_11.setText("");
            button_12.setText("");
            button_20.setText("");
            button_21.setText("");
            button_22.setText("");

            button_00.setDisable(true);
            button_01.setDisable(true);
            button_02.setDisable(true);
            button_10.setDisable(true);
            button_11.setDisable(true);
            button_12.setDisable(true);
            button_20.setDisable(true);
            button_21.setDisable(true);
            button_22.setDisable(true);
        });
    }

    public void closePartitaTerminataAlert() {
        Platform.runLater(() -> {
            if (partitaTerminataAlert != null) {
                partitaTerminataAlert.close();
                partitaTerminataAlert = null;
            }
        });
    }
}

