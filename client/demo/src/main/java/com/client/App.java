package com.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("login"), 640, 480);
        stage.setScene(scene);
        stage.setResizable(false);

        stage.setOnCloseRequest((WindowEvent event) -> {
            try {
                // Prova a inviare il logout se sei loggato
                if (HomePageController.connessione != null && HomePageController.serverSocket != null) {
                    HomePageController.connessione.sendRequest(HomePageController.connessione.clientSocket, "logout:" + HomePageController.serverSocket);
                }
                if (MatchController.connessione != null && MatchController.serverSocket != null) {
                    MatchController.connessione.sendRequest(MatchController.connessione.clientSocket, "logout:" + MatchController.serverSocket);
                }
            } catch (Exception e) {
                // Ignora errori se la connessione non è attiva
            }
        });

        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}