const board = document.getElementById("board");

for (let row = 0; row < 8; row++) {
  for (let col = 0; col < 8; col++) {
    const square = document.createElement("div");
    square.classList.add("square");
    
    // alternate colors
    if ((row + col) % 2 === 0) {
      square.classList.add("light");
    } else {
      square.classList.add("dark");
    }
    square.addEventListener('click', () => {
      if (square.classList.contains("yellow")) {
    // remove highlight and restore original color
        square.classList.remove("yellow");
        if ((row + col) % 2 === 0) {
          square.classList.add("light");
        } else {
          square.classList.add("dark");
        }
      } else {
        // remove current color and add highlight
        square.classList.remove("light", "dark");
        square.classList.add("yellow");
      }
    });
    board.appendChild(square);
  }
}
