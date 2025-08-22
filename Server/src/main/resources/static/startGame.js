
const board = document.getElementById("board");
let isClickable = true;
let numClicks = 0;

for (let row = 0; row < 8; row++) {
  for (let col = 0; col < 8; col++) {
    const square = document.createElement("div");
    square.classList.add("square");
    
    if ((row + col) % 2 === 0) {
      square.classList.add("light");
    } else {
      square.classList.add("dark");
    }

    if (row < 3 && square.classList.contains("dark")) {
      const piece = document.createElement("div");
      piece.classList.add("piece", "black");
      square.appendChild(piece);
    }

    if (row > 4 && square.classList.contains("dark")) {
      const piece = document.createElement("div");
      piece.classList.add("piece", "red");
      square.appendChild(piece);
    }
    
    square.addEventListener('click', () => {
      

      if(numClicks >= 2 && !square.classList.contains("yellow")) 
        return
      else if(numClicks >= 1 && square.classList.contains("yellow")) 
        numClicks--;
      else 
        numClicks++;

      if(!isClickable)
        return

      if (square.classList.contains("yellow")) {

        square.classList.remove("yellow");
        if ((row + col) % 2 === 0) {
          square.classList.add("light");
        } else {
          square.classList.add("dark");
        }
      } else {
        
        square.classList.remove("light", "dark");
        square.classList.add("yellow");
      }
    });
    board.appendChild(square);
  }
}

