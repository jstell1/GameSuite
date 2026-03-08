
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
let gameTurn;
let validate;
const Ajv = window.ajv7;
let ajv = new Ajv();


function resetGameState() {
    gameId = null;
    numClicks = 0;
    isClickable = true;
    startX = -1;
    startY = -1;
    endX = -1;
    endY = -1;
    playerTurn = null;
    gameTurn = null;
    sessionId = null;

}

window.addEventListener("DOMContentLoaded", setupSocket);


async function setupSocket() {
     protocol = window.location.protocol === "https:" ? "wss:" : "ws:";
    host = window.location.host; // whatever was used to load the page

    const resp = await fetch("/schema");
    //console.log(resp);
    schema = await resp.json();
    console.log(schema);
    validate = ajv.compile(schema);

    socket = new WebSocket(`${protocol}//${host}/ingame`);
    socket.onopen = () => console.log("WebSocket connected");

    socket.onmessage = (event) => {
        const data = JSON.parse(event.data);
        console.log("WS Message:", data);
        let valid = validate(data);
        console.log(valid);

        if(!valid) {
            let msg = {
                "badErrorRequest": {
                    "message": "don't recognize message type"
                }
            }
            socket.send(JSON.stringify(msg));
            return;
        }

        let type = Object.keys(data)[0];
        let payload = data[type];
        console.log(payload);

        switch(type) {
            case "sessionConnectedResponse":
                sessionId = payload.sessionId;
                break;
            case "gameCreatedResponse":
                gameId = payload.gameId;
                let game = payload.gameState;
               isClickable = false;
               playerTurn = 1;
                showGameBoard(payload);
                break;
            case "gameReadyResponse":
                gameId = payload.gameId;
                if(playerTurn == null)
                    playerTurn = 2;
                gameTurn = payload.gameState.turn;
                isClickable = false;
                if(gameTurn === playerTurn)
                    isClickable = true;
                showGameBoard(payload);
                gameInfo.textContent = `Player ${gameTurn}'s turn`;
                break;
            case "stateUpdateResponse":
                gameTurn = payload.gameState.turn;
                

                if(!payload.gameState.gameOver) {

                    if(playerTurn === gameTurn)
                        isClickable = true;
                    else
                        isClickable = false;
                    gameInfo.textContent = `Player ${gameTurn}'s turn`;
                } else {
                    console.log(payload.gameState);
                    gameInfo.textContent = `Winner is: ${payload.gameState.winner.name}`
                    isClickable = false;
                }
                renderBoard(payload.gameState);
                break;
            default: 
                console.log(data);
                break;
        }
    };

    socket.onclose = () => console.log("WebSocket disconnected");
}
// --- Lobby handlers ---
createForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const name = document.getElementById("createName").value;
    if (!name) { alert("Name is required!"); return; }

    let payload = {
        "createGameRequest": {
            "game": "Checkers",
            "name": name
        }
    }
    socket.send(JSON.stringify(payload));
});

joinForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const player = document.getElementById("joinName").value;
    const joinGameId = document.getElementById("joinGameId").value;
    if (!player || !joinGameId) { alert("Name and Game ID are required!"); return; }

    let payload = {
        "joinGameRequest": {
            "name": player,
            "gameId": joinGameId
        }
    }
    socket.send(JSON.stringify(payload));
});

document.getElementById("quit").addEventListener("click", async (e) => {
    e.preventDefault();
    socket.close();
    resetGameState();
    gameBoardDiv.style.display = "none";
    lobby.style.display = "block";
    playerTurn = null;
    setupSocket();
});

// --- SPA swap ---
async function showGameBoard(payload) {
    lobby.style.display = "none";
    gameBoardDiv.style.display = "block";
    gameInfo.textContent = "Game ID: " + payload.gameId;

    boardContainer.innerHTML = '';
    if(payload.board != null)
        initBoard(payload.board);
}

// --- Board setup ---
function initBoard(gameBoard) {
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

            console.log(gameBoard[row][col]);
            let piece = gameBoard[row][col]["piece"];
            let name = piece != null ? piece["name"] : null;

            if(name != null && name === "B") addPiece(square, "black");
            if(name != null && name === "R") addPiece(square, "red");

            square.addEventListener("mousedown", () => handleSquareClick(square, row, col));
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
        "moveRequest": {
            gameId: gameId,
            move: {
                startX: startRow, startY: startCol,
                endX: endRow, endY: endCol  
            }
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
    });
}

