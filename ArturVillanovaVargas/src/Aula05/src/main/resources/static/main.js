'use strict'

const URL = "http://localhost:8080/api/books";

let renderAllBooks = (data) => {

    const container = document.getElementById('books-container');
    container.innerHTML = "";

    if(data.error) {
        container.innerHTML = `Erro na API: ${data.error}`
        return;
    }

    data.forEach(book => {
        const div = document.createElement("div");
        const title = document.createElement("h1");
        const available = document.createElement("p");

        title.textContent = book.title;
        available.textContent = book.available;

        div.appendChild(title);
        div.appendChild(available);

        container.appendChild(div);

    });
}

const getAllBooks = () => {
    fetch(URL)
        .then(response => response.json())
        .then(data => renderAllBooks(data))
        .catch(e => {
            console.log(`Erro de rede: ${e}`);
        })
}

document.addEventListener("DOMContentLoaded", () => {
    getAllBooks();
});

