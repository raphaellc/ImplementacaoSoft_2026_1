# 📚 Gerenciador de Livros

Aplicação Java para gerenciar uma lista de leitura pessoal. Permite adicionar livros, listar e marcar como lidos, com interface web e API REST.

---

## Tecnologias

- **Java 17** — linguagem principal
- **H2** — banco de dados relacional em memória (embutido, sem instalação)
- **Jackson** — serialização/desserialização JSON
- **HttpServer (JDK)** — servidor HTTP embutido, sem frameworks externos
- **Maven** — gerenciamento de dependências e build
- **HTML/CSS/JS** — frontend estático servido pelo próprio backend

---

## Arquitetura

O projeto segue o padrão de **arquitetura em camadas**, onde cada camada tem uma única responsabilidade e se comunica apenas com a camada imediatamente abaixo dela.

```
CLIENTE (Navegador)
        ↓ HTTP (GET / POST / PUT)
   [ API ]  LivroHandler
        ↓ interface LivroService
[ SERVIÇO ]  LivroServiceImpl
        ↓ interface LivroRepository
[REPOSITÓRIO]  LivroRepositoryH2
        ↓ JDBC / SQL
  [ BANCO ]  H2 (em memória)
```

### Estrutura de pastas

```
Aula-06/
├── pom.xml
└── src/
    └── main/
        ├── java/br/edu/projeto/
        │   ├── GerenciadorLivros.java       # main() — monta dependências e sobe o servidor
        │   ├── api/
        │   │   ├── LivroHandler.java        # Camada HTTP: rotas GET, POST, PUT
        │   │   └── StaticHandler.java       # Serve os arquivos estáticos (frontend)
        │   ├── model/
        │   │   ├── Livro.java               # Record imutável: id, titulo, autor, lido
        │   │   ├── LivroRepository.java     # Interface de acesso a dados
        │   │   └── LivroRepositoryH2.java   # Implementação com H2 via JDBC
        │   ├── service/
        │   │   ├── LivroService.java        # Interface de regras de negócio
        │   │   └── LivroServiceImpl.java    # Validações e orquestração
        │   ├── controller/
        │   │   └── LivroController.java     # Controlador para interface CLI (legado)
        │   ├── view/
        │   │   └── LivroView.java           # Interface de linha de comando (legado)
        │   └── util/
        │       └── DatabaseConnectionH2.java # Gerencia conexões JDBC
        └── recursos/
            └── static/
                └── index.html               # Frontend web
```

### Responsabilidade de cada camada

| Classe | Responsabilidade |
|---|---|
| `GerenciadorLivros` | Ponto de entrada. Instancia as dependências e inicia o servidor HTTP na porta 8080. |
| `LivroHandler` | Recebe requisições HTTP, valida entradas, serializa JSON e delega ao serviço. |
| `StaticHandler` | Serve o `index.html` e outros arquivos estáticos. |
| `LivroService` | Interface que define o contrato de negócio (sem detalhes de implementação). |
| `LivroServiceImpl` | Aplica as regras: título e autor obrigatórios, máximo de 255 caracteres. |
| `LivroRepository` | Interface que define o contrato de acesso a dados. |
| `LivroRepositoryH2` | Executa SQL contra o H2 com `PreparedStatement` (previne SQL Injection). |
| `DatabaseConnectionH2` | Centraliza a configuração da conexão JDBC. Lê credenciais de variáveis de ambiente. |
| `Livro` | Record Java imutável. Para alterar o status, usa o padrão *copy-with* (`comLido()`). |

---

## Pré-requisitos

- [Java 17+](https://adoptium.net/)
- [Maven 3.8+](https://maven.apache.org/download.cgi)

> O banco de dados H2 é embutido — não é necessário instalar nada além do Java e Maven.

---

## Como rodar

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/ImplementacaoSoft_2026_1.git
cd ImplementacaoSoft_2026_1/Dafni-Rosa/Aula-06
```

### 2. Compile o projeto

```bash
mvn package -DskipTests
```

### 3. Execute o backend

```bash
java -jar target/gerenciador-livros-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Você verá no terminal:

```
╔════════════════════════════════════════╗
║     Gerenciador de Livros iniciado!    ║
╠════════════════════════════════════════╣
║  Frontend : http://localhost:8080      ║
║  API REST : http://localhost:8080/api/livros ║
╚════════════════════════════════════════╝
```

### 4. Acesse o frontend

Abra o navegador em:

```
http://localhost:8080
```

> ⚠️ Não abra o `index.html` diretamente pelo explorador de arquivos — o CORS bloqueará as requisições. Sempre acesse pelo `http://localhost:8080`.

---

## API REST

Base URL: `http://localhost:8080/api/livros`

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/livros` | Lista todos os livros |
| `POST` | `/api/livros` | Adiciona um novo livro |
| `PUT` | `/api/livros/{id}` | Marca o livro como lido |

### Exemplos com curl

```bash
# Listar todos os livros
curl http://localhost:8080/api/livros

# Adicionar um livro
curl -X POST http://localhost:8080/api/livros \
  -H "Content-Type: application/json" \
  -d '{"titulo": "Dom Casmurro", "autor": "Machado de Assis"}'

# Marcar como lido (substitua 1 pelo id do livro)
curl -X PUT http://localhost:8080/api/livros/1
```

### Exemplo de resposta (GET)

```json
[
  {
    "id": 1,
    "titulo": "Dom Casmurro",
    "autor": "Machado de Assis",
    "lido": false
  }
]
```

---

## Observações

**Banco de dados em memória:** os dados são perdidos ao encerrar a aplicação. Para persistir entre execuções, altere a URL no `DatabaseConnectionH2.java`:

```java
// Trocar:
"jdbc:h2:mem:livrosdb;DB_CLOSE_DELAY=-1"

// Por:
"jdbc:h2:file:./dados/livros"
```

**Trocar para MySQL:** basta criar uma classe `LivroRepositoryMySQL` implementando `LivroRepository` e alterar a linha de instanciação em `GerenciadorLivros.java`. Nenhuma outra camada precisa ser modificada.