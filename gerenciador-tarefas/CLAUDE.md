# Gerenciador de Tarefas — Contexto do Projeto

## Visão Geral

API REST de gerenciamento de tarefas escrita em **Java puro** (sem Spring Boot).
Usa `com.sun.net.httpserver.HttpServer` (JDK built-in) e JDBC direto.
Banco de dados padrão: **H2 in-memory**. Implementação MySQL também existe, mas está desativada.

## Como rodar

```bash
mvn compile
mvn exec:java
# API disponível em http://localhost:8080/api/tarefas
```

## Estrutura de Pacotes

```
com.gerenciadortarefas/
├── GerenciadorTarefas.java        # main() — monta e inicia o servidor HTTP
├── api/
│   └── TarefaHandler.java         # Rotas HTTP: GET, POST, PUT /api/tarefas
├── model/
│   ├── Tarefa.java                # Record imutável (id, descricao, concluida)
│   ├── TarefaRepository.java      # Interface do repositório
│   ├── TarefaRepositoryH2.java    # Implementação H2 (ativa)
│   └── TarefaRepositoryMySQL.java # Implementação MySQL (inativa)
├── service/
│   ├── TarefaService.java         # Interface do serviço
│   └── TarefaServiceImpl.java     # Regras de negócio
├── util/
│   ├── DatabaseConnectionH2.java  # Conexão JDBC com H2
│   └── DatabaseConnection.java    # Conexão JDBC com MySQL
├── controller/
│   └── TarefaController.java      # CLI controller (não usado — comentado no main)
└── view/
    └── TarefaView.java            # CLI view (não usado — comentado no main)
```

## Entidade

```java
public record Tarefa(int id, String descricao, boolean concluida) {
    public Tarefa comConcluida(boolean concluida) { ... } // cria cópia com status atualizado
}
```

## Endpoints da API

| Método | Rota                  | Descrição                        | Resposta       |
|--------|-----------------------|----------------------------------|----------------|
| GET    | `/api/tarefas`        | Lista todas as tarefas           | 200 + JSON     |
| POST   | `/api/tarefas`        | Cria uma tarefa                  | 201 / 400      |
| PUT    | `/api/tarefas/{id}`   | Marca tarefa como concluída      | 200 / 404      |
| OPTIONS| `/api/tarefas`        | Preflight CORS                   | 204            |

**Payload POST:**
```json
{ "descricao": "Texto da tarefa" }
```
Regras: não nulo, não vazio, máximo 255 caracteres.

## Banco de Dados

### H2 (ativo)
- Modo: **in-memory** (`jdbc:h2:mem:tarefadb;DB_CLOSE_DELAY=-1`)
- Dados perdidos ao encerrar a JVM — adequado para desenvolvimento/testes
- Tabela criada automaticamente no construtor de `TarefaRepositoryH2`
- Driver registrado explicitamente: `Class.forName("org.h2.Driver")`

### MySQL (inativo)
- Banco: `gerenciador_tarefa`, tabela `tarefas`
- Para ativar: trocar para `repositoryMySql` em `GerenciadorTarefas.java` e definir env vars

## Variáveis de Ambiente

| Variável      | Padrão                                          | Uso                      |
|---------------|-------------------------------------------------|--------------------------|
| `H2_USER`     | `sa`                                            | Usuário H2               |
| `H2_PASSWORD` | `""` (vazio)                                    | Senha H2                 |
| `DB_URL`      | `jdbc:mysql://localhost:3306/gerenciador_tarefa`| URL MySQL                |
| `DB_USER`     | `root`                                          | Usuário MySQL            |
| `DB_PASSWORD` | _obrigatória_ (sem default)                     | Senha MySQL              |
| `CORS_ORIGIN` | `http://localhost:3000`                         | Origem CORS permitida    |

## Decisões de Arquitetura

- **Sem Spring Boot:** projeto usa apenas JDK + Jackson + H2 + MySQL connector
- **Record imutável:** `Tarefa` é um Java Record — para alterar campos usa `comConcluida()`
- **Sem connection pool:** usa `DriverManager.getConnection()` direto (adequado para estudo; produção precisaria de HikariCP)
- **Erros internos:** repositórios lançam `RuntimeException` — detalhes SQL ficam nos logs da JVM, nunca na resposta HTTP
- **Sem framework de injeção de dependência:** instâncias criadas manualmente no `main()`

## Limitações Conhecidas

- Sem autenticação/autorização
- Sem persistência entre reinicializações (H2 in-memory)
- Sem logging estruturado (SLF4J/Logback)
- `TarefaRepositoryMySQL` ainda usa `System.out.println` para erros (não foi migrado)
- CLI (`TarefaController`, `TarefaView`) implementada mas desativada

## Dependências (pom.xml)

| Dependência              | Versão   | Uso                        |
|--------------------------|----------|----------------------------|
| `com.h2database:h2`      | 2.2.224  | Banco de dados H2          |
| `com.mysql:mysql-connector-j` | 9.6.0 | Conector MySQL           |
| `com.fasterxml.jackson.core:jackson-databind` | 2.15.2 | Serialização JSON |

Java: 25 | Maven wrapper: `exec:maven-plugin` + `assembly-plugin` (fat jar)
