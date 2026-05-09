const API_URL = "http://localhost:8080/api/livros";

function showToast(message, isError = false) {
  Toastify({
    text: message,
    duration: 3000,
    gravity: "top",
    position: "right",
    style: {
      background: isError ? "#dc3545" : "#28a745",
    },
  }).showToast();
}

async function adicionarLivro() {
  const livroInput = document.getElementById("novo-livro-input");

  if (!livroInput.value.trim()) {
    showToast("Digite um nome válido", true);
    return;
  }

  try {
    const response = await fetch(API_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ nome: livroInput.value }),
    });

    if (!response.ok) throw new Error();

    showToast("Livro adicionado");

    livroInput.value = "";
    livroInput.focus();

    carregarLivros();
  } catch {
    showToast("Erro ao adicionar livro", true);
  }
}

async function deletarLivro(id) {
  if (!confirm("Deseja remover este livro?")) return;

  try {
    const response = await fetch(`${API_URL}/${id}`, {
      method: "DELETE",
    });

    if (!response.ok) throw new Error();

    showToast("Livro removido");
    carregarLivros();
  } catch {
    showToast("Erro ao remover livro", true);
  }
}

async function carregarLivros() {
  const listaUl = document.getElementById("lista-livros");

  try {
    const response = await fetch(API_URL);

    if (!response.ok) throw new Error();

    const livros = await response.json();

    listaUl.innerHTML = "";

    if (livros.length === 0) {
      listaUl.innerHTML = `<li class="empty">Nenhum livro encontrado</li>`;
      return;
    }

    livros.forEach((livro) => {
      const li = document.createElement("li");

      li.innerHTML = `
        <div class="livro-card">
          <div class="livro-info">
            <strong>#${livro.id}</strong>
            <span>${livro.nome}</span>
          </div>
          <button class="delete-btn">🗑️</button>
        </div>
      `;

      const btn = li.querySelector(".delete-btn");

      btn.addEventListener("click", () => deletarLivro(livro.id));

      listaUl.appendChild(li);
    });
  } catch {
    listaUl.innerHTML = `<li class="empty">Erro ao carregar livros</li>`;
    showToast("Erro ao conectar com API", true);
  }
}

window.onload = carregarLivros;
