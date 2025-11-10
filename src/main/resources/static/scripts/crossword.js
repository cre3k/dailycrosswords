const directions = {
    ACROSS: "across",
    DOWN: "down"
};

direction = directions.ACROSS;
const allCells = Array.from(document.querySelectorAll('.cell'));
const allClues = Array.from(document.querySelectorAll('.clue'));
const total = allCells.length;
const board = document.querySelector('.board');
const width = board.style.gridTemplateColumns.split('(')[1]?.split(',')[0].trim();
const colCount = parseInt(width) || 5;
const rowCount = total / colCount;
const enabledCount = Array.from(document.querySelectorAll('.guess'))
    .filter(input => !input.disabled).length;


function setCurrentCellId(input) {
    allCells.forEach(cell => cell.removeAttribute('id'));
    const cell = input.closest('.cell');
    cell.id = 'current-input';
    const fail = document.getElementById("fail");
    fail.hidden = true;
    paintCurrentWord(input);
    paintCurrentClue(input);
}

function paintCurrentWord(input) {
    const cell = input.closest('.cell');
    const index = parseInt(cell.dataset.index);
    allCells.forEach(cell => cell.classList.remove('currentWord'));
    if (direction == directions.ACROSS) {
        let start = index - index % colCount;
        for (let i = start; i < start + colCount; ++i) {
            allCells[i].classList.add('currentWord');
        }
    }
    if (direction == directions.DOWN) {
        let start = index % colCount;
        for (let i = start; i < total; i += colCount) {
            allCells[i].classList.add('currentWord');
        }
    }
}

function paintCurrentClue(input) {
    const cell = input.closest('.cell');
    const clues = JSON.parse(cell.dataset.clues);
    allClues.forEach(cell => cell.classList.remove('currentWord'));
    if (direction == directions.ACROSS) {
        allClues[clues[0]].classList.add('currentWord')
    } else {
        allClues[clues[1]].classList.add('currentWord')
    }
}


function moveFocus(event, input) {
    const cell = input.closest('.cell');
    const index = parseInt(cell.dataset.index);


    // Переход при вводе буквы
    if (event.key.length === 1 && event.key.match(/[a-zA-Zа-яА-Я]/)) {
        if (direction == directions.ACROSS) {
            let next = index + 1;
            while (next < total && allCells[next].classList.contains('black') || allCells[next].classList.contains('revealed')) next++;
            if (next < total) allCells[next].querySelector('input.guess')?.focus();
            allCells[next].id = 'current-input';
            allCells[index].removeAttribute('id');
        } else if (direction = directions.DOWN) {
            let down = index + colCount;
            while (down < total && allCells[down].classList.contains('black') || allCells[down].classList.contains('revealed')) down += colCount;
            if (down < total) allCells[down].querySelector('input.guess')?.focus();
            allCells[down].querySelector('input.guess').id = 'current-input';
            allCells[index].querySelector('input.guess').removeAttribute('id');
        }
        checkAnswer();
    }
}

function moveFocusWithArrows(event, input) {
    const cell = input.closest('.cell');
    const index = parseInt(cell.dataset.index);

    // Стрелки
    if (event.key === "ArrowLeft") {
        if (direction == directions.DOWN) {
            direction = directions.ACROSS;
            return;
        }
        let prev = index - 1;
        while (prev >= 0 && allCells[prev].classList.contains('black') || allCells[prev].classList.contains('revealed')) prev--;
        if (prev >= 0) allCells[prev].querySelector('input.guess')?.focus();
        allCells[prev].id = 'current-input';
        allCells[index].removeAttribute('id');
    } else if (event.key === "ArrowRight") {
        if (direction == directions.DOWN) {
            direction = directions.ACROSS;
            return;
        }
        let next = index + 1;
        while (next < total && allCells[next].classList.contains('black') || allCells[next].classList.contains('revealed')) next++;
        if (next < total) allCells[next].querySelector('input.guess')?.focus();
        allCells[next].id = 'current-input';
        allCells[index].removeAttribute('id');
    } else if (event.key === "ArrowUp") {
        if (direction == directions.ACROSS) {
            direction = directions.DOWN;
            return;
        }
        let up = index - colCount;
        while (up >= 0 && allCells[up].classList.contains('black') || allCells[up].classList.contains('revealed')) up -= colCount;
        if (up >= 0) allCells[up].querySelector('input.guess')?.focus();
        allCells[up].id = 'current-input';
        allCells[index].removeAttribute('id');
    } else if (event.key === "ArrowDown") {
        if (direction == directions.ACROSS) {
            direction = directions.DOWN;
            return;
        }
        let down = index + colCount;
        while (down < total && allCells[down].classList.contains('black') || allCells[down].classList.contains('revealed')) down += colCount;
        if (down < total) allCells[down].querySelector('input.guess')?.focus();
        allCells[down].id = 'current-input';
        allCells[index].removeAttribute('id');
    } else if (event.key === "Backspace") {
        if (direction == directions.ACROSS) {
            let prev = index - 1;
            while (prev >= 0 && allCells[prev].classList.contains('black') || allCells[prev].classList.contains('revealed')) prev--;
            if (prev >= 0) allCells[prev].querySelector('input.guess')?.focus();
            allCells[prev].id = 'current-input';
            allCells[index].removeAttribute('id');
            input.value = "";
        } else {
            let up = index - colCount;
            while (up >= 0 && allCells[up].classList.contains('black') || allCells[up].classList.contains('revealed')) up -= colCount;
            if (up >= 0) allCells[up].querySelector('input.guess')?.focus();
            allCells[up].id = 'current-input';
            allCells[index].removeAttribute('id');
            input.value = "";
        }
    }
}

async function checkAnswer() {
    const values = Array.from(inputs).map(input => input.value);
    console.log(values);
    nonEmptyCount = values.filter(v => v !== "").length;
    if (nonEmptyCount != enabledCount) return;

    try {
        const response = await fetch(`/check`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(values),
            credentials: 'include'
        });

        const result = await response.json();
        console.log('Ответ от сервера:', result);
        if (result) {
            const minutes = Math.floor(elapsed / 60);
            const seconds = Math.floor(elapsed % 60);

            const formatted =
                String(minutes).padStart(2, '0') + ':' +
                String(seconds).padStart(2, '0');
            const audio = new Audio('win.mp3');
            audio.play();
            const fail = document.getElementById("fail");
            fail.hidden = true;
            const success = document.getElementById("success");
            success.textContent = "Congraulations! Solved in " + formatted;
            success.hidden = false;

        }
        else {
            const success = document.getElementById("success");
            success.hidden = true;
            const fail = document.getElementById("fail");
            fail.hidden = false;
        }
    } catch (err) {
        console.error('Ошибка при отправке:', err);
    }
}

async function performAutocheck() {
    const values = Array.from(inputs).map(input => input.value);

    try {
        const response = await fetch(`/autocheck`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(values),
            credentials: 'include'
        });

        const indicesToDisable = await response.json(); // массив чисел
        console.log('Ответ от сервера:', indicesToDisable);

        indicesToDisable.forEach(index => {
            const cell = document.querySelector(`.cell[data-index="${index}"]`);
            if (cell) {
                const input = cell.querySelector('input.guess');
                if (input) {
                    input.disabled = true;
                    input.style.color = 'blue';
                    cell.classList.add('revealed');
                }
            }
        });

    } catch (err) {
        console.error('Ошибка при отправке:', err);
    }
}

const inputs = document.querySelectorAll('.guess');
inputs.forEach(input => {
    input.addEventListener('input', checkAnswer);
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
            input.style.color = 'blue';
            cell.classList.add('revealed');
        }
        cell.removeAttribute('id');
        checkAnswer();
    } catch (err) {
        console.error('Ошибка при раскрытии буквы:', err);
    }
});


let startTime;
let intervalId;
let elapsed = 0;

window.onload = function () {
    startTime = Date.now();
    intervalId = setInterval(updateTimer, 100);
}

function updateTimer() {
    const now = Date.now();
    elapsed = (now - startTime) / 1000;
}

function stopTimer() {
    clearInterval(intervalId);

    elapsed = Math.floor((now - startTime) / 1000);

    console.log(`Таймер остановлен. Прошло ${elapsed.toFixed(1)} сек`);
}
