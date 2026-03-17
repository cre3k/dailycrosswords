const directions = {
    ACROSS: "across",
    DOWN: "down"
};

const controlKeys = new Set([
    "ArrowUp",
    "ArrowDown",
    "ArrowLeft",
    "ArrowRight",
    "Backspace"
]);

let direction = directions.ACROSS;
const allCells = Array.from(document.querySelectorAll('.cell'));
const allClues = Array.from(document.querySelectorAll('.clue'));
const total = allCells.length;
const board = document.querySelector('.board');
const width = board.style.gridTemplateColumns.split('(')[1]?.split(',')[0].trim();
const colCount = parseInt(width) || 5;
const rowCount = total / colCount;
const enabledCount = Array.from(document.querySelectorAll('.guess'))
    .filter(input => !input.disabled).length;

// === Устанавливаем текущую клетку ===
function setCurrentCellId(input) {
    allCells.forEach(cell => cell.removeAttribute('id'));
    const cell = input.closest('.cell');
    cell.id = 'current-input';
    const fail = document.getElementById("fail");
    fail.hidden = true;
    paintCurrentWord(input);
    paintCurrentClue(input);
}

// === Подсветка слова ===
function paintCurrentWord(input) {
    const cell = input.closest('.cell');
    const index = parseInt(cell.dataset.index);
    allCells.forEach(cell => cell.classList.remove('currentWord'));

    if (direction === directions.ACROSS) {
        let start = index - index % colCount;
        for (let i = start; i < start + colCount; ++i) allCells[i].classList.add('currentWord');
    } else {
        let start = index % colCount;
        for (let i = start; i < total; i += colCount) allCells[i].classList.add('currentWord');
    }
}

// === Подсветка подсказки ===
function paintCurrentClue(input) {
    const cell = input.closest('.cell');
    const clues = JSON.parse(cell.dataset.clues);
    allClues.forEach(cell => cell.classList.remove('currentWord'));
    if (direction === directions.ACROSS) allClues[clues[0]].classList.add('currentWord');
    else allClues[clues[1]].classList.add('currentWord');
}

function moveFocusForward(input) {
    const cell = input.closest('.cell');
    const index = parseInt(cell.dataset.index);
    let next = null;
    let nextInput = null;

    if (direction === directions.ACROSS) {
        next = index + 1;
        while (next < total && (allCells[next].classList.contains('black') || allCells[next].classList.contains('revealed'))) next++;
    } else if (direction === directions.DOWN) {
        next = index + colCount;
        while (next < total && (allCells[next].classList.contains('black') || allCells[next].classList.contains('revealed'))) next += colCount;
    }

    if (next !== null && next < total) {
        nextInput = allCells[next].querySelector('input.guess');
        nextInput?.focus();
    }
    return nextInput;
}

function moveFocusBackward(input) {
    const cell = input.closest('.cell');
    const index = parseInt(cell.dataset.index);
    let prev = null;
    let prevInput = null;

    if (direction === directions.ACROSS) {
        prev = index - 1;
        while (prev >= 0 && (allCells[prev].classList.contains('black') || allCells[prev].classList.contains('revealed'))) prev--;
    } else {
        prev = index - colCount;
        while (prev >= 0 && (allCells[prev].classList.contains('black') || allCells[prev].classList.contains('revealed'))) prev -= colCount;
    }

    if (prev !== null && prev >= 0) {
        prevInput = allCells[prev].querySelector('input.guess');
        prevInput?.focus();
    }
    return prevInput;
}

// === Стрелки и Backspace ===
function moveFocusWithArrows(event, input) {
    const cell = input.closest('.cell');
    const index = parseInt(cell.dataset.index);
    let target = null;

    if (event.key === "ArrowLeft") {
        if (direction === directions.DOWN) {
            direction = directions.ACROSS;
            paintCurrentWord(input);
            paintCurrentClue(input);
            return; // только меняем направление
        }
        if (index == 0) {
            target = total - 1;
        } else {
            target = index - 1;
        }
        while (target >= 0 && (allCells[target].classList.contains('black') || allCells[target].classList.contains('revealed'))) target--;
    }
    else if (event.key === "ArrowRight") {
        if (direction === directions.DOWN) {
            direction = directions.ACROSS;
            paintCurrentWord(input);
            paintCurrentClue(input);
            return;
        }
        if (index == total - 1) {
            target = 0;
        } else {
            target = index + 1;
        }
        while (target < total && (allCells[target].classList.contains('black') || allCells[target].classList.contains('revealed'))) target++;
    }
    else if (event.key === "ArrowUp") {
        if (direction === directions.ACROSS) {
            direction = directions.DOWN;
            paintCurrentWord(input);
            paintCurrentClue(input);
            return;
        }
        if (index == 0) {
            target = total - 1;
        } else if (index - colCount < 0) {
            target = index - 1 + (rowCount - 1) * colCount;
        } else {
            target = index - colCount;
        }
        while (target > 0 && (allCells[target].classList.contains('black') || allCells[target].classList.contains('revealed'))) {
            target -= colCount;
            if (target < 0) {
                target = total + target - 1;
            }
        }
    }
    else if (event.key === "ArrowDown") {
        if (direction === directions.ACROSS) {
            direction = directions.DOWN;
            paintCurrentWord(input);
            paintCurrentClue(input);
            return;
        }
        if (index + colCount > total) {
            target = (index + 1) % colCount ;
        } else {
            target = index + colCount;
        }
        while (target < total && (allCells[target].classList.contains('black') || allCells[target].classList.contains('revealed'))) target += colCount;
        if (target >= total) {
            target = (target + 1) % colCount
        }
    }

    else if (event.key === "Backspace") {
        event.preventDefault();
        if (input.value !== "") {
            input.value = "";
            moveFocusBackward(input);
        } else {
            prevInput = moveFocusBackward(input);
            prevInput.value = "";
            moveFocusBackward(input);
        }
        return;
    }

    if (target !== null && target >= 0 && target < total) {
        const nextInput = allCells[target].querySelector('input.guess');
        nextInput?.focus();
    }
}

async function checkAnswer() {
    const values = Array.from(inputs).map(input => input.value);
    const nonEmptyCount = values.filter(v => v !== "").length;
    if (nonEmptyCount !== enabledCount) return;

    try {
        const response = await fetch(`/check`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(values),
            credentials: 'include'
        });
        const result = await response.json();

        const success = document.getElementById("success");
        const fail = document.getElementById("fail");

        if (result) {
            const minutes = Math.floor(elapsed / 60);
            const seconds = Math.floor(elapsed % 60);
            const formatted = `${String(minutes).padStart(2,'0')}:${String(seconds).padStart(2,'0')}`;
            new Audio('win.mp3').play();
            fail.hidden = true;
            success.textContent = `Congratulations! Solved in ${formatted}`;
            success.hidden = false;
        } else {
            success.hidden = true;
            fail.hidden = false;
        }
    } catch (err) {
        console.error('Ошибка при отправке:', err);
    }
}

async function performAutocheck() {
    const values = Array.from(inputs).map(input => input.value);

    const autochecked = document.getElementById("autochecked");

            autochecked.hidden = false;

            setTimeout(() => {
                autochecked.hidden = true;
            }, 3000);

    try {
        const response = await fetch(`/autocheck`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(values),
            credentials: 'include'
        });

        const indicesToDisable = await response.json();
        indicesToDisable.forEach(index => {
            const cell = document.querySelector(`.cell[data-index="${index}"]`);
            const input = cell?.querySelector('input.guess');
            if (input) {
                input.disabled = true;
                cell.querySelector("input.guess").classList.add('revealed');
                cell.classList.add('revealed');
            }
        });
    } catch (err) {
        console.error('Ошибка при отправке:', err);
    }
}

const inputs = document.querySelectorAll('.guess');

inputs.forEach(input => {

    input.maxLength = 1;

    input.addEventListener('keydown', (e) => {
        const value = e.target.value;
        const key = e.key;
        if (!controlKeys.has(event.key) && value != "") {
            let nextInput = moveFocusForward(input);
            checkAnswer();
        }
    });

    // Ввод буквы
    input.addEventListener('input', (e) => {
        const value = e.target.value;
        if (!/^[a-zA-Zа-яА-Я]$/.test(value)) {
            e.target.value = '';
            return;
        }
        moveFocusForward(input);
        checkAnswer();
    });


});

document.getElementById('reveal').addEventListener('click', async () => {
    const cell = document.getElementById('current-input');
    if (!cell) return;

    const index = parseInt(cell.dataset.index);

    try {
        const response = await fetch('/reveal', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(index)
        });

        if (!response.ok) throw new Error('Ошибка сети');

        const letter = await response.text();
        const input = cell.querySelector('input.guess');
        if (input) {
            input.value = letter;
            input.disabled = true;
            cell.querySelector("input.guess").classList.add('revealed');
            cell.classList.add('revealed');
        }
        cell.removeAttribute('id');
        checkAnswer();
    } catch (err) {
        console.error('Ошибка при раскрытии буквы:', err);
    }
});

// === Таймер ===
let startTime;
let intervalId;
let elapsed = 0;

function startTimer() {
    startTime = Date.now();
    intervalId = setInterval(updateTimer, 100);
}

function updateTimer() {
    const now = Date.now();
    elapsed = (now - startTime) / 1000;
}

function stopTimer() {
    clearInterval(intervalId);
    elapsed = Math.floor((Date.now() - startTime) / 1000);
}

startTimer();