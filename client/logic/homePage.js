"use strict";

let partiteDisponibili = []; // Array per i codici delle partite disponibili

// Funzione per inizializzare le partite disponibili
function initPartite() {
    partiteDisponibili = [129, 124, 125]; // Esempio di codici delle partite
    mostraPartite(); // Mostra le partite disponibili
    mostraNomeUtente(); // Mostra il nome utente se presente
}

// Funzione per mostrare le partite disponibili
function mostraPartite() {
    const gameList = document.getElementById("gameList");
    gameList.innerHTML = ""; // Pulisce la lista delle partite

    partiteDisponibili.forEach((codicePartita) => {
        const gameItem = document.createElement("div");
        gameItem.className = "game-item";

        const gameSpan = document.createElement("span");
        gameSpan.textContent = `Partita #${codicePartita}`;

        const joinButton = document.createElement("button");
        joinButton.className = "join-btn";
        joinButton.textContent = "Partecipa";
        joinButton.onclick = () => joinGame(codicePartita);

        gameItem.appendChild(gameSpan);
        gameItem.appendChild(joinButton);
        gameList.appendChild(gameItem);
    });
}

function mostraNomeUtente() {
    const nomeUtente = localStorage.getItem('nomeUtente'); // Recupera il nome dal localStorage
    if (nomeUtente) {
        const header = document.querySelector("h1");
        const nomeSpan = document.createElement("span");

        nomeSpan.textContent = ` - Benvenuto, ${nomeUtente}!`;
        nomeSpan.style.color = "blue";
        header.appendChild(nomeSpan);
    }
}

function createNewGame() {
    alert("Creazione di una nuova partita!");
    // Qui puoi aggiungere la logica per la creazione di una nuova partita.
}

function joinGame(gameId) {
    alert(`Partecipazione alla partita #${gameId}`);
    // Qui puoi aggiungere la logica per partecipare alla partita con ID specifico.
}

document.addEventListener("DOMContentLoaded", initPartite);