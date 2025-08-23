// --- Elements ---
const createForm = document.getElementById("createForm");
const joinForm = document.getElementById("joinForm");
const topLabel = document.getElementById("top");
const lobby = document.getElementById("lobby");
const gameBoardDiv = document.getElementById("gameBoard");
const boardContainer = document.getElementById("boardContainer");
const gameInfo = document.getElementById("gameInfo");

let protocol;
let host; // whatever was used to load the page
let socket;
let sessionId = null;
let gameId = null;
let numClicks = 0;
let isClickable = true;
let startX = -1;
let startY = -1;
let endX = -1;
let endY = -1;
let turnNum = 1;
let playerTurn;

// --- WebSocket setup ---
window.addEventListener("DOMContentLoaded", () => {
    protocol = window.location.protocol === "https:" ? "wss:" : "ws:";
    host = window.location.host; // whatever was used to load the page
    socket = new WebSocket(`${protocol}//${host}/ingame`);

    socket.onopen = () => console.log("WebSocket connected");

    socket.onmessage = (event) => {
        const data = JSON.parse(event.data);
        console.log("WS Message:", data);

        // Always check for sessionId
        if (data.sessionId && sessionId === null) {
            sessionId = data.sessionId;
        }

        if (data.resp1) {
            console.log("Game ready:", data.resp1);
            showGameBoard(gameId);
        }

        if (data.resp2) {
            if (data.resp2.game) {
                // Initial render or updates
                renderBoard(data.resp2.game);
            }
        }
    };

    socket.onclose = () => console.log("WebSocket disconnected");
});

// --- Lobby handlers ---
createForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const name = document.getElementById("createName").value;
    if (!name) { alert("Name is required!"); return; }

    const resp = await fetch("/games", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name , sessionId })
    });
    const data = await resp.json();
    topLabel.textContent = "Game ID: " + data.gameId;
    gameId = data.gameId;
    //showGameBoard(data.gameId);
    playerTurn = 1;
});

joinForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const player = document.getElementById("joinName").value;
    const joinGameId = document.getElementById("joinGameId").value;
    if (!player || !joinGameId) { alert("Name and Game ID are required!"); return; }

    const resp = await fetch("/games/players", {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ player, gameId: joinGameId, sessionId })
    });

    if (!resp.ok) {
        alert("Failed to join game: " + resp.statusText);
        return;
    }

    const data = await resp.json();
    gameId = data.gameId;
    playerTurn = 2;
    //showGameBoard(gameId);
});

// --- SPA swap ---
function showGameBoard(gameId) {
    lobby.style.display = "none";
    gameBoardDiv.style.display = "block";
    gameInfo.textContent = "Game ID: " + gameId;

    // create board if not already created
    if (!boardContainer.hasChildNodes()) initBoard();
}

// --- Board setup ---
function initBoard() {
    const board = document.createElement("div");
    board.classList.add("board");
    boardContainer.appendChild(board);

    for (let row = 0; row < 8; row++) {
        for (let col = 0; col < 8; col++) {
            const square = document.createElement("div");
            square.classList.add("square");
            square.dataset.row = row;
            square.dataset.col = col;

            if ((row + col) % 2 === 0) square.classList.add("light");
            else square.classList.add("dark");

            if (row < 3 && square.classList.contains("dark")) addPiece(square, "black");
            if (row > 4 && square.classList.contains("dark")) addPiece(square, "red");

            square.addEventListener("click", () => handleSquareClick(square, row, col));
            board.appendChild(square);
        }
    }
}

// --- Board helper functions ---
function addPiece(square, color) {
    const piece = document.createElement("div");
    piece.classList.add("piece", color);
    square.appendChild(piece);
}

function handleSquareClick(square, row, col) {

    if (!isClickable) return;

    if (numClicks >= 2 && !square.classList.contains("yellow")) return;
    else if (numClicks >= 1 && square.classList.contains("yellow")) numClicks--;
    else numClicks++;

    if(startX === -1 && startY === -1) {
        startX = row;
        startY = col;
    } else if(startX > -1 && startY > -1) {
        endX = row;
        endY = col;
    }

    if (numClicks === 2) {
        fetchData(startX, startY, endX, endY);
        isClickable = false;
        const startSquare = document.querySelector(
                    `.square[data-row="${startX}"][data-col="${startY}"]`
                );
        
        const endSquare = document.querySelector(
                    `.square[data-row="${endX}"][data-col="${endY}"]`
                );

        startSquare.classList.remove("yellow");
        startSquare.classList.add((row + col) % 2 === 0 ? "light" : "dark");
        endSquare.classList.remove("yellow");
        endSquare.classList.add((row + col) % 2 === 0 ? "light" : "dark");
        startX = -1;
        startY = -1;
        endX = -1;
        endY = -1;
        numClicks = 0;

    } else {
        if (square.classList.contains("yellow")) {
            square.classList.remove("yellow");
            square.classList.add((row + col) % 2 === 0 ? "light" : "dark");
        } else {
            square.classList.remove("light", "dark");
            square.classList.add("yellow");
        }
    }

}

async function fetchData(startRow, startCol, endRow, endCol) {

    if (!socket || socket.readyState !== WebSocket.OPEN) {
        console.error("WebSocket not connected!");
        return;
    }

    // Package the move
    const moveMessage = {
        gameId: gameId,
        sessionId: sessionId,
        move: {
            startX: startRow, startY: startCol,
            endX: endRow, endY: endCol  
        }
    };

    console.log("Sending move:", moveMessage);
    socket.send(JSON.stringify(moveMessage));
}

function renderBoard(game) {
    if (!game.changedPos) return;

    game.changedPos.forEach(pos => {
        const square = document.querySelector(
            `.square[data-row="${pos.x}"][data-col="${pos.y}"]`
        );

        if (!square) return;

        // Clear old piece
        square.innerHTML = "";

        // Redraw if a piece exists
        if (pos.piece) {
            const color = pos.piece.name === "B" ? "black" : "red";
            addPiece(square, color);

            if (pos.piece.type === "K") {
                square.firstChild.classList.add("king");
            }
        }

        // Always reset highlights when updating
        square.classList.remove("yellow");
        square.classList.add((pos.x + pos.y) % 2 === 0 ? "light" : "dark");
        const turn = game.turn;
        if(playerTurn === turn)
            isClickable = true;
    });
}

