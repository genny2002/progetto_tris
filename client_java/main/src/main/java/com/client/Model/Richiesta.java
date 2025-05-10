package com.client.Model;

public class Richiesta {
    public String idRichiesta;
    public String messaggio;
    public String stato;

    public Richiesta(String idRichiesta, String messaggio, String stato) {
        this.idRichiesta = idRichiesta;
        this.messaggio = messaggio;
        this.stato = stato;
    }   
}
