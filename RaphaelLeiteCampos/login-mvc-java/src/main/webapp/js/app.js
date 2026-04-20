/**
 * Integração da View (HTML) com o Controller (API REST Java)
 * Utiliza a Fetch API para realizar chamadas assíncronas.
 */
document.getElementById('loginForm').addEventListener('submit', async (event) => {
    event.preventDefault(); // Impede o recarregamento da página

    const email = document.getElementById('email').value;
    const senha = document.getElementById('senha').value;
    const mensagemDiv = document.getElementById('mensagem');

    // Limpar mensagens anteriores
    mensagemDiv.className = 'mensagem-alerta';
    mensagemDiv.textContent = 'Processando...';
    mensagemDiv.style.display = 'block';

    try {
        // Chamada à API REST (Controller)
        const response = await fetch('api/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, senha })
        });

        const data = await response.json();

        if (response.ok) {
            // Sucesso no login
            mensagemDiv.className = 'mensagem-alerta sucesso';
            mensagemDiv.textContent = data.mensagem;
            console.log('Usuário autenticado:', data.usuario);
            
            // Redirecionamento simulado após 2 segundos
            setTimeout(() => {
                alert('Redirecionando para o painel principal...');
            }, 2000);
        } else {
            // Erro no login (401 ou 400)
            mensagemDiv.className = 'mensagem-alerta erro';
            mensagemDiv.textContent = data.mensagem;
        }

    } catch (error) {
        // Erro de rede ou servidor
        mensagemDiv.className = 'mensagem-alerta erro';
        mensagemDiv.textContent = 'Erro de conexão com o servidor. Tente novamente mais tarde.';
        console.error('Erro na requisição:', error);
    }
});
