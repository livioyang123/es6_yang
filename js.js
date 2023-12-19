document.addEventListener("DOMContentLoaded", function () {
    const board = document.getElementById("board");
    const cells = [];
    let currentPlayer = "X";
    let winner = null;
    let moveCount = 0;


    function initializeGame() {
        for (let i = 0; i < 3; i++) {
            for (let j = 0; j < 3; j++) {
                const cell = document.createElement("div");
                cell.classList.add("cell");
                cell.dataset.row = i;
                cell.dataset.col = j;
                cell.addEventListener("click", handleCellClick);
                board.appendChild(cell);
                cells.push(cell);
            }
        }


    }

    function handleCellClick(event) {
        const clickedCell = event.target;

        if (!clickedCell.textContent && !winner) {
            clickedCell.textContent = currentPlayer;
            moveCount++;

            if (checkWinner()) {
                endGame();
            } else if (moveCount === 9) {
                endGame(true); // Pareggio
            } else {
                currentPlayer = currentPlayer === "X" ? "O" : "X";
            }
        }
    }

    function checkWinner() {
        const winningCombos = [
            [0, 1, 2],
            [3, 4, 5],
            [6, 7, 8],
            [0, 3, 6],
            [1, 4, 7],
            [2, 5, 8],
            [0, 4, 8],
            [2, 4, 6],
        ];

        for (const combo of winningCombos) {
            const [a, b, c] = combo;
            const cellA = cells[a].textContent;
            const cellB = cells[b].textContent;
            const cellC = cells[c].textContent;

            if (cellA && cellA === cellB && cellA === cellC) {
                winner = cellA;
                return true;
            }
        }

        return false;
    }

    function endGame(draw = false) {

        if (draw) {
            showWinnerModal("It's a draw!");
        } else {
            showWinnerModal(`Player ${winner} wins!`);
        }
    }

    function showWinnerModal(message) {
        const modal = document.getElementById("winner-modal");
        const winnerMessage = document.getElementById("winner-message");


        winnerMessage.textContent = message;

        modal.style.display = "flex";
    }

    document.querySelector(".close").addEventListener("click", function () {
        document.getElementById("winner-modal").style.display = "none";
        resetGame();
    });

    function resetGame() {
        cells.forEach((cell) => {
            cell.textContent = "";
        });

        currentPlayer = "X";
        winner = null;
        moveCount = 0;

    }

    initializeGame();
});