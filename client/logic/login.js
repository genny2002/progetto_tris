"use strict";

document.getElementById('nomeForm').addEventListener('submit', function(event) {
    event.preventDefault(); // Previene l'invio del modulo
    const nome = document.getElementById('nome').value;

    // Salva il nome nel localStorage
    localStorage.setItem('nomeUtente', nome);

    // Reindirizza alla homepage
    window.location.href = "../view/homePage.html";
});