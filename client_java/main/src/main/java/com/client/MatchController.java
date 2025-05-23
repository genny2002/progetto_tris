package com.client;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import com.client.Connessione.NotificaListener; // Ensure this is the correct package for NotificaListener
import com.client.Model.Richiesta;
import com.client.Connessione.Connessione; // Ensure this is the correct package for NotificaListener

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
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
    @FXML private Label avvisi_label;

    private boolean isXTurn=false;
    public static Connessione connessione;
    public static Queue<Richiesta> richiesteRicevute = new LinkedList<>();
    private static ObservableList<String> richiesteList = FXCollections.observableArrayList();
    private HBox rigaDaEliminare;
    private static String nomeAvversario=null;
    private static String simboloGiocatore=null;    //simbolo del giocatore attuale (che sta giocando)
    private static String simboloAvversario=null; //nome del giocatore attuale
    private static String idPartita=null; //id della partita corrente
    private static Thread notificaThreadHomePage;
    private static NotificaListener notificaListenerHomePage;


    @FXML
    public void initialize() {
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
            isXTurn = true; // Il giocatore X inizia per primo
        }

        if(nomeAvversario != null) {
            avvisi_label.setText("Stai giocando contro " + nomeAvversario + " con simbolo " + simboloGiocatore);
        } else {
            avvisi_label.setText("In attesa di una richiesta");
        }

        button_00.setOnAction(e -> handleMove(button_00));
        button_01.setOnAction(e -> handleMove(button_01));
        button_02.setOnAction(e -> handleMove(button_02));
        button_10.setOnAction(e -> handleMove(button_10));
        button_11.setOnAction(e -> handleMove(button_11));
        button_12.setOnAction(e -> handleMove(button_12));
        button_20.setOnAction(e -> handleMove(button_20));
        button_21.setOnAction(e -> handleMove(button_21));
        button_22.setOnAction(e -> handleMove(button_22));

        notificaListenerHomePage.setMatchController(this);
    }

    private void handleMove(Button button) {
        System.out.println("Button clicked: " + button.getId());
        char row = button.getId().charAt(7); // Assuming the button ID is in the format "button_XX"
        char col=button.getId().charAt(8); // Extract the column part

        connessione.sendRequest(connessione.clientSocket, "Partita:putMove:" + row + "," + col + "," + simboloGiocatore + "," + idPartita);
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
                // Disabilita il pulsante
                button.setDisable(true);
                // Cambia il testo del pulsante in base al simbolo del giocatore
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

    public static void setClientSocket(Connessione newConnessione) {
        connessione = newConnessione;
    }

    public void addNuovaRichiesta(Richiesta richiesta) {
    
        Platform.runLater(() -> {
            // Crea una riga per la richiesta
            HBox riga = new HBox(10); // Spaziatura di 10 tra gli elementi
            Text testoRichiesta = new Text(richiesta.messaggio + " (ID: " + richiesta.idRichiesta + ")");
            Button accettaButton = new Button("Accetta");
            Button rifiutaButton = new Button("Rifiuta");

            // Azioni dei pulsanti
            accettaButton.setOnAction(e -> handleAccept(richiesta, riga));
            rifiutaButton.setOnAction(e -> handleReject(richiesta, riga));

            // Aggiungi gli elementi alla riga
            riga.getChildren().addAll(testoRichiesta, accettaButton, rifiutaButton);

            // Aggiungi la riga alla ListView
            notifiche_list.getItems().add(riga);
        });
    }

    private /*static*/ void handleAccept(Richiesta richiesta, HBox riga) {
        connessione.sendRequest(connessione.clientSocket, "Richiesta:putAccettaRichiesta:" + richiesta.idRichiesta);
        rigaDaEliminare=riga; // Logica per accettare la richiesta (es: invio al server)
        deleteNotifica();
    }

    private void handleReject(Richiesta richiesta, HBox riga) {
        connessione.sendRequest(connessione.clientSocket, "Richiesta:deleteRifiutaRichiesta:" + richiesta.idRichiesta);
        rigaDaEliminare=riga; //Platform.runLater(() -> notifiche_list.getItems().remove(riga));
    }

    public void deleteNotifica() {
        Platform.runLater(() -> notifiche_list.getItems().remove(this.rigaDaEliminare));
    }

    public static void setNomeAvversario(String avversario) {
        nomeAvversario = avversario;
    }

    public void setAvvisiLabel() {
        Platform.runLater(() -> avvisi_label.setText("Stai giocando contro " + nomeAvversario + " con simbolo " + simboloGiocatore));   
    }
    
    public void setPartitaTerminata(String message) {
        Platform.runLater(() -> { Platform.runLater(() -> avvisi_label.setText(message)); });
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.CONFIRMATION);
                        
            alert.setTitle("Partita terminata");
            alert.setHeaderText("La partita è terminata!");
            alert.setContentText("Vuoi giocare un'altra partita con lo stesso avversario?");
                        
            Optional<ButtonType> result = alert.showAndWait();
                        
            if (result.isPresent() && result.get() == ButtonType.OK) {
                connessione.sendRequest(connessione.clientSocket, "Partita:putRematch:" + idPartita + ",1," + simboloGiocatore);
            }else{
                connessione.sendRequest(connessione.clientSocket, "Partita:putRematch:" + idPartita + ",-1," + simboloGiocatore);
                
                try {
                    App.setRoot("homePage");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void setSimbolo(String simbolo){
        if(simbolo.equals("X")){
            simboloAvversario="O";
            simboloGiocatore = "X";
        }else{
            simboloAvversario="X";
            simboloGiocatore = "O";
        }
    }

    public void setSimboloFromNotificaListener(String simbolo){
        if(simbolo.equals("X")){
            simboloAvversario="O";
            simboloGiocatore = "X";
        }else{
            simboloAvversario="X";
            simboloGiocatore = "O";
        }

        if(simboloGiocatore.equals("X")){
            button_00.setDisable(false);
            button_01.setDisable(false);
            button_02.setDisable(false);
            button_10.setDisable(false);
            button_11.setDisable(false);
            button_12.setDisable(false);
            button_20.setDisable(false);
            button_21.setDisable(false);
            button_22.setDisable(false);
            isXTurn = true; // Il giocatore X inizia per primo
        }
    }

    public static void setIdPartita(String id){
        idPartita = id;
    }

    public static String setThread(Thread notificaThread, NotificaListener notificaListener){
        notificaThreadHomePage = notificaThread;
        notificaListenerHomePage = notificaListener;
        return idPartita;
    }

    public void goToHomePage() throws IOException{
        App.setRoot("homePage");
    }

    public void initButton(){
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
    
            if(simboloGiocatore.equals("X")){
                button_00.setDisable(false);
                button_01.setDisable(false);
                button_02.setDisable(false);
                button_10.setDisable(false);
                button_11.setDisable(false);
                button_12.setDisable(false);
                button_20.setDisable(false);
                button_21.setDisable(false);
                button_22.setDisable(false);
                isXTurn = true; // Il giocatore X inizia per primo
            }else{
                button_00.setDisable(true);
                button_01.setDisable(true);
                button_02.setDisable(true);
                button_10.setDisable(true);
                button_11.setDisable(true);
                button_12.setDisable(true);
                button_20.setDisable(true);
                button_21.setDisable(true);
                button_22.setDisable(true);
                isXTurn = false;
            }
        });
    }
}


