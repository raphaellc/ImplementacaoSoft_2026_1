const API_URL = "http://localhost:8080/api/livros";

async function adicionarLivro() {
  const livroInput = document.getElementById("novo-livro-input");

  if (!livroInput.value) return;

  fetch("http://localhost:8080/api/livros", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      nome: livroInput.value,
    }),
  })
    .then((response) => response.json())
    .then((data) => {
      console.log("Sucesso:", data);
    })
    .catch((error) => {
      console.error("Erro:", error);
    });
}

async function carregarLivros() {
  const listaUl = document.getElementById("lista-livros");

  try {
    const response = await fetch(API_URL);

    if (!response.ok) throw new Error("Erro ao buscar dados da API");

    const livros = await response.json();
    listaUl.innerHTML = ""; // Limpa a lista

    if (livros.length === 0) {
      listaUl.innerHTML = "<li>Nenhum livro encontrado.</li>";
      return;
    }

    livros.forEach((livro) => {
      const li = document.createElement("li");

      // O record Livro no Java gera campos: id, nome
      li.innerHTML = `
                    <span>
                        <strong>#${livro.id}</strong> - ${livro.nome}
                    </span>
                `;
      listaUl.appendChild(li);
    });
  } catch (error) {
    console.error("Erro:", error);
    listaUl.innerHTML = `<li style="color:red">Erro ao conectar com a API: ${error.message}</li>`;
  }
}

// Carregar ao abrir a página
window.onload = carregarLivros;
