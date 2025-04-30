package com.client.Model;

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
}

