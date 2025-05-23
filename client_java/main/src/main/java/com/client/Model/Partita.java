package com.client.Model;

import java.util.ArrayList;

public class Partita {
    private final int id;
    private final String nomeCreatore;

    public Partita(int id, String nomeCreatore) {
        this.id = id;
        this.nomeCreatore = nomeCreatore;
    }

    public int getId() {
        return id;
    }

    public String getNomeCreatore() {
        return nomeCreatore;
    }

    public static ArrayList<Partita> convertToObjects(String partiteString) {
        ArrayList<Partita> partite = new ArrayList<>();
        String[] partiteArray = partiteString.split("/");

        for (String p : partiteArray) {
            int idPartita = Integer.parseInt(p.split(",")[0].split(":")[1]);
            String nomeCreatore = p.split(",")[1].split(":")[1];

            partite.add(new Partita(idPartita, nomeCreatore));
            System.out.println("partita inserita:  ID: " + idPartita + ", Nome Creatore: " + nomeCreatore);
        }

        return partite;
    }
}

