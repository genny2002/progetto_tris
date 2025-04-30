"use strict";

let player = 'x';
let moves = 0;
let endMatch = false;

function setupGame(containerId) {
    let container = document.getElementById(containerId);
    let table = document.createElement("table");
    let cont = 0;
    let indication = document.createElement("p");
    indication.id = "info";
    let text = document.createTextNode("Current move: ❌");

    indication.appendChild(text);
    container.appendChild(indication);
    container.appendChild(table);

    for (let i = 0; i < 3; i++) {
        let tr = document.createElement("tr");

        for (let j = 0; j < 3; j++) {
            let td = document.createElement("td");

            td.id = cont;
            cont++;
            td.addEventListener("click", function () {
                makeMove(td.id);
            });
            tr.appendChild(td);
        }

        table.appendChild(tr);
    }
}

function makeMove(cella) {
    let sign;

    moves++;

    if (document.getElementById(cella).childNodes.length === 0 && endMatch == false) {
        if (player === 'x') {
            document.getElementById("info").innerHTML = "Current move: 🔵";
            sign = document.createTextNode("❌");
            player = 'o';
        } else {
            document.getElementById("info").innerHTML = "Current move: ❌";
            sign = document.createTextNode("🔵");
            player = 'x';
        }

        document.getElementById(cella).appendChild(sign);

        if ((document.getElementById(0).childNodes.length > 0 && document.getElementById(1).childNodes.length > 0 && document.getElementById(2).childNodes.length > 0 &&
            (document.getElementById(0).firstChild.textContent == document.getElementById(1).firstChild.textContent) && (document.getElementById(1).firstChild.textContent == document.getElementById(2).firstChild.textContent)) ||
            (document.getElementById(3).childNodes.length > 0 && document.getElementById(4).childNodes.length > 0 && document.getElementById(5).childNodes.length > 0 &&
                (document.getElementById(3).firstChild.textContent == document.getElementById(4).firstChild.textContent) && (document.getElementById(4).firstChild.textContent == document.getElementById(5).firstChild.textContent)) ||
            (document.getElementById(6).childNodes.length > 0 && document.getElementById(7).childNodes.length > 0 && document.getElementById(8).childNodes.length > 0 &&
                (document.getElementById(6).firstChild.textContent == document.getElementById(7).firstChild.textContent) && (document.getElementById(7).firstChild.textContent == document.getElementById(8).firstChild.textContent)) ||
            (document.getElementById(0).childNodes.length > 0 && document.getElementById(3).childNodes.length > 0 && document.getElementById(6).childNodes.length > 0 &&
                (document.getElementById(0).firstChild.textContent == document.getElementById(3).firstChild.textContent) && (document.getElementById(3).firstChild.textContent == document.getElementById(6).firstChild.textContent)) ||
            (document.getElementById(1).childNodes.length > 0 && document.getElementById(4).childNodes.length > 0 && document.getElementById(7).childNodes.length > 0 &&
                (document.getElementById(1).firstChild.textContent == document.getElementById(4).firstChild.textContent) && (document.getElementById(4).firstChild.textContent == document.getElementById(7).firstChild.textContent)) ||
            (document.getElementById(2).childNodes.length > 0 && document.getElementById(5).childNodes.length > 0 && document.getElementById(8).childNodes.length > 0 &&
                (document.getElementById(2).firstChild.textContent == document.getElementById(5).firstChild.textContent) && (document.getElementById(5).firstChild.textContent == document.getElementById(8).firstChild.textContent)) ||
            (document.getElementById(0).childNodes.length > 0 && document.getElementById(4).childNodes.length > 0 && document.getElementById(8).childNodes.length > 0 &&
                (document.getElementById(0).firstChild.textContent == document.getElementById(4).firstChild.textContent) && (document.getElementById(4).firstChild.textContent == document.getElementById(8).firstChild.textContent)) ||
            (document.getElementById(2).childNodes.length > 0 && document.getElementById(4).childNodes.length > 0 && document.getElementById(6).childNodes.length > 0 &&
                (document.getElementById(2).firstChild.textContent == document.getElementById(4).firstChild.textContent) && (document.getElementById(4).firstChild.textContent == document.getElementById(6).firstChild.textContent))) {
            document.getElementById("info").innerHTML = `Player ${sign.textContent} wins! Reload this page to play again!`;
            endMatch = true;
        } else if (moves >= 9) {
            document.getElementById("info").innerHTML = `It's a draw! Reload this page to play again!`;
            endMatch = true;
        }
    }
}