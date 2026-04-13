# Segurança em APIs REST Java — Material Didático

Este documento descreve as falhas de segurança identificadas no projeto **Gerenciador de Tarefas**
e as correções aplicadas. Para cada item você encontrará a teoria por trás do problema, o código
vulnerável, o código corrigido e como testar a diferença.

---

## Sumário

1. [Injeção de JSON](#1-injeção-de-json)
2. [Leitura parcial do corpo HTTP](#2-leitura-parcial-do-corpo-http)
3. [Headers de segurança HTTP](#3-headers-de-segurança-http)
4. [ResultSet fora do try-with-resources](#4-resultset-fora-do-try-with-resources)
5. [Thread pool ilimitado](#5-thread-pool-ilimitado)
6. [Exceções engolidas com System.out.println](#6-exceções-engolidas-com-systemoutprintln)

---

## 1. Injeção de JSON

### Teoria

**JSON Injection** ocorre quando dados não confiáveis são inseridos dentro de uma string JSON
montada manualmente — por concatenação — sem que os caracteres especiais sejam escapados.

Os caracteres problemáticos em JSON são:

| Caractere | Significado em JSON   | O que causa se não escapado |
|-----------|-----------------------|-----------------------------|
| `"`       | Delimita strings      | Fecha a string prematuramente |
| `\`       | Caractere de escape   | Escapa o próximo caractere de forma imprevisível |
| `\n`      | Quebra de linha       | Quebra o formato do JSON |
| `<`, `>`  | Sem significado direto | Pode habilitar XSS se o JSON for lido pelo browser |

O efeito vai desde uma resposta malformada que quebra o cliente até a injeção de campos extras
na resposta JSON, enganando o cliente sobre o estado do servidor.

### Exemplo do problema

Imagine que uma mensagem de erro inclui a entrada do usuário ou algum texto com aspas:

```java
// VULNERÁVEL
String message = "Campo \"descricao\" inválido";
byte[] body = ("{\"mensagem\":\"" + message + "\"}").getBytes();
// Resultado: {"mensagem":"Campo "descricao" inválido"}
//                               ^--- JSON inválido
```

Ou pior — injeção de um campo extra:

```java
String message = "ok\",\"admin\":\"true";
// Resultado: {"mensagem":"ok","admin":"true"}
//             ^--- campo "admin" foi injetado
```

### Código vulnerável

```java
// TarefaHandler.java — antes
private void sendResponse(HttpExchange exchange, int status, String message) throws IOException {
    byte[] body = ("{\"mensagem\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
    // ...
}
```

### Código corrigido

```java
// TarefaHandler.java — depois
private void sendResponse(HttpExchange exchange, int status, String message) throws IOException {
    ObjectNode node = objectMapper.createObjectNode();
    node.put("mensagem", message);              // Jackson escapa tudo automaticamente
    byte[] body = objectMapper.writeValueAsBytes(node);
    // ...
}
```

### Por que o Jackson resolve?

O `ObjectMapper` do Jackson nunca concatena strings manualmente. Ele usa um `JsonGenerator`
interno que transforma cada caractere especial no seu escape correto antes de escrever:

```
"  →  \"
\  →  \\
/  →  \/   (opcional, mas seguro para HTML)
```

### Regra prática

> **Nunca construa JSON por concatenação de strings.**
> Use sempre uma biblioteca de serialização (`ObjectMapper`, `Gson`, etc.).
> Isso vale tanto para respostas quanto para consultas, logs e mensagens de erro.

---

## 2. Leitura parcial do corpo HTTP

### Teoria

O método `InputStream.read(byte[] buffer)` é definido pela especificação Java assim:

> *"Reads some bytes from an input stream and stores them into the buffer array. The number of
> bytes actually read is returned as an integer. This method blocks until input data is available,
> end of file is detected, or an exception is thrown."*

A palavra-chave é **some bytes** (alguns bytes). Um único `read()` **não garante** que todos os
bytes disponíveis serão lidos de uma vez. Em comunicação de rede isso é comum porque:

- O TCP divide dados em segmentos (pacotes);
- O sistema operacional pode entregar os pacotes em momentos distintos;
- O buffer interno do socket pode estar parcialmente preenchido no momento da chamada.

### Exemplo do problema

Considere um cliente enviando um JSON de 800 bytes com uma conexão lenta:

```
Pacote 1: 500 bytes  →  read() retorna 500  →  500 <= 1024  →  passa na validação
Pacote 2: 300 bytes  →  nunca lido (stream fechada após o return)
```

O JSON recebido está truncado → `objectMapper.readTree()` lança exceção → resposta 400.
O corpo legítimo de 800 bytes é **rejeitado** por um bug de implementação.

O problema inverso também existe: o cliente envia 2000 bytes (acima do limite), mas o primeiro
`read()` retorna apenas 1024 bytes por fragmentação → **o limite de tamanho não é aplicado**.

### Código vulnerável

```java
// TarefaHandler.java — antes
private String readBody(HttpExchange exchange) throws IOException {
    try (InputStream is = exchange.getRequestBody()) {
        byte[] buffer = new byte[MAX_BODY_BYTES + 1];
        int bytesRead = is.read(buffer);          // pode retornar menos que o disponível
        if (bytesRead > MAX_BODY_BYTES) {
            return null;                           // limite pode nunca ser atingido se fragmentado
        }
        // ...
    }
}
```

### Código corrigido

```java
// TarefaHandler.java — depois
private String readBody(HttpExchange exchange) throws IOException {
    try (InputStream is = exchange.getRequestBody();
         ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
        byte[] chunk = new byte[512];
        int bytesRead;
        int totalRead = 0;
        while ((bytesRead = is.read(chunk)) != -1) {   // lê até EOF
            totalRead += bytesRead;
            if (totalRead > MAX_BODY_BYTES) {
                return null;                            // limite garantido independente de fragmentação
            }
            buffer.write(chunk, 0, bytesRead);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }
}
```

### Como funciona o loop

```
Iteração 1: read() → 512 bytes  | totalRead = 512   | escreve no buffer
Iteração 2: read() → 512 bytes  | totalRead = 1024  | escreve no buffer
Iteração 3: read() → 100 bytes  | totalRead = 1124  | > 1024 → retorna null
```

O total acumulado **nunca ultrapassa o limite**, mesmo com múltiplos fragmentos TCP.

### Regra prática

> **Sempre leia streams em loop até EOF (`-1`).**
> Um único `read()` é suficiente apenas para streams locais de tamanho conhecido
> (como `FileInputStream` com `readAllBytes()`). Para streams de rede, use sempre o loop.

---

## 3. Headers de segurança HTTP

### Teoria

Além do conteúdo da resposta, o HTTP permite que o servidor envie **instruções ao browser**
sobre como aquela resposta deve ser tratada. Essas instruções vêm nos headers de resposta.

Sem esses headers, o browser usa comportamentos padrão que foram historicamente explorados
em ataques.

### Os três headers aplicados

#### `X-Content-Type-Options: nosniff`

**Problema que resolve:** *MIME Sniffing*

Browsers antigos (e alguns modernos) tentam "adivinhar" o tipo de um arquivo inspecionando
seu conteúdo — mesmo que o `Content-Type` seja informado. Isso é chamado de MIME Sniffing.

Cenário de ataque:
```
1. Atacante faz upload de um arquivo .jpg que na verdade contém JavaScript
2. Servidor serve com Content-Type: image/jpeg
3. Browser detecta o JavaScript e o executa → XSS
```

Com o header:
```
X-Content-Type-Options: nosniff
```
O browser respeita estritamente o `Content-Type` declarado e não executa o arquivo como script.

---

#### `X-Frame-Options: DENY`

**Problema que resolve:** *Clickjacking*

Clickjacking é um ataque onde a página legítima é carregada dentro de um `<iframe>` em um
site malicioso, invisível ou transparente. O usuário clica achando que está interagindo com
o site malicioso, mas na verdade está clicando na aplicação legítima sobreposta.

```html
<!-- Site malicioso -->
<iframe src="http://localhost:8080/" style="opacity: 0; position: absolute; top: 0; left: 0;">
</iframe>
<button style="position: absolute; top: 50px; left: 100px;">Clique para ganhar um prêmio!</button>
```

Com o header:
```
X-Frame-Options: DENY
```
O browser recusa carregar a página dentro de qualquer `<iframe>`.

---

#### `Content-Security-Policy: default-src 'self'`

**Problema que resolve:** *Cross-Site Scripting (XSS)*

CSP é uma política que instrui o browser sobre quais origens de conteúdo são confiáveis.
Com `default-src 'self'`, somente recursos carregados do mesmo domínio são permitidos.

Scripts inline (`<script>alert(1)</script>`) e recursos externos
(`<script src="https://evil.com/malware.js">`) são bloqueados.

```
Content-Security-Policy: default-src 'self'
```

Mesmo que um atacante consiga injetar HTML na página, o browser bloqueia a execução de
qualquer script que não tenha vindo do servidor original.

### Código aplicado

```java
// TarefaHandler.java
private void addSecurityHeaders(HttpExchange exchange) {
    exchange.getResponseHeaders().add("X-Content-Type-Options", "nosniff");
    exchange.getResponseHeaders().add("X-Frame-Options", "DENY");
    exchange.getResponseHeaders().add("Content-Security-Policy", "default-src 'self'");
}

@Override
public void handle(HttpExchange exchange) throws IOException {
    // ... headers CORS ...
    addSecurityHeaders(exchange);   // aplicado em toda resposta
    // ...
}
```

### Como verificar no browser

Abra as Ferramentas do Desenvolvedor (F12) → aba **Network** → clique em uma requisição →
aba **Headers** → seção **Response Headers**. Os três headers devem aparecer.

### Regra prática

> **Headers de segurança são a primeira linha de defesa do browser.**
> Eles não custam nada em performance e bloqueiam classes inteiras de ataques antes que
> o JavaScript da página seja sequer executado.

---

## 4. ResultSet fora do try-with-resources

### Teoria

Em Java, recursos que implementam `AutoCloseable` (conexões, streams, statements) devem ser
fechados explicitamente para liberar recursos do sistema operacional e do banco de dados.

O `try-with-resources` (introduzido no Java 7) garante que `close()` seja chamado mesmo
quando uma exceção ocorre — algo que blocos `finally` manuais frequentemente esquecem.

A hierarquia de recursos JDBC é:

```
Connection
  └── Statement / PreparedStatement
        └── ResultSet
```

A especificação JDBC garante que fechar um `Statement` fecha seu `ResultSet`. Portanto, se o
`Statement` está no `try-with-resources`, o `ResultSet` seria fechado indiretamente.

Porém, isso cria um problema de legibilidade e manutenção:

- O contrato implícito não está visível no código;
- Se alguém mover o `ResultSet` para fora do bloco (refatoração), o recurso vaza;
- Ferramentas de análise estática (SonarQube, SpotBugs) reportam como warning.

### Código vulnerável

```java
// TarefaRepositoryH2.java e TarefaRepositoryMySQL.java — antes
try (Connection conn = DatabaseConnectionH2.getConnection();
     PreparedStatement pstmt = conn.prepareStatement(sql)) {
    pstmt.setInt(1, id);
    ResultSet rs = pstmt.executeQuery();   // rs NÃO está no try-with-resources
    if (rs.next()) {
        return Optional.of(...);
    }
    // rs nunca é fechado explicitamente
}
```

### Código corrigido

```java
// TarefaRepositoryH2.java e TarefaRepositoryMySQL.java — depois
try (Connection conn = DatabaseConnectionH2.getConnection();
     PreparedStatement pstmt = conn.prepareStatement(sql)) {
    pstmt.setInt(1, id);
    try (ResultSet rs = pstmt.executeQuery()) {   // rs no seu próprio bloco
        if (rs.next()) {
            return Optional.of(...);
        }
    }   // rs.close() garantido aqui
}   // pstmt.close() e conn.close() garantidos aqui
```

### Por que o try-with-resources aninhado?

O `ResultSet` tem seu próprio ciclo de vida dentro do bloco do `PreparedStatement`.
O `try` aninhado deixa isso explícito e garante que qualquer exceção dentro do loop
de leitura do `ResultSet` ainda resulta no fechamento correto.

### Analogia

Pense em JDBC como abrir caixas dentro de caixas:

```
[ Connection (caixa grande)
    [ PreparedStatement (caixa média)
        [ ResultSet (caixa pequena) ]
    ]
]
```

Você deve fechar as caixas de dentro para fora, na ordem inversa da abertura.
O `try-with-resources` faz isso automaticamente — mas só para os recursos que
você declarou explicitamente nele.

### Regra prática

> **Todo recurso JDBC deve aparecer explicitamente em um `try-with-resources`.**
> Não confie no fechamento em cascata implícito — ele existe, mas não está visível
> no código e pode ser quebrado por refatorações futuras.

---

## 5. Thread pool ilimitado

### Teoria

Quando `HttpServer.setExecutor(null)` é chamado, o servidor usa internamente um
`Executors.newCachedThreadPool()`. Este pool:

- Cria uma nova thread para cada requisição que chega;
- Reutiliza threads ociosas por 60 segundos;
- **Não tem limite superior de threads**.

Em um servidor real, isso é uma vulnerabilidade de **negação de serviço (DoS)**:

```
Atacante → 10.000 requisições simultâneas
Servidor → cria 10.000 threads
JVM     → OutOfMemoryError: unable to create native thread
Resultado → servidor trava, requisições legítimas são recusadas
```

Cada thread consome memória (stack padrão: 512 KB a 1 MB) e tempo de CPU para troca
de contexto (context switching). Com threads demais, o sistema passa mais tempo
alternando entre threads do que processando requisições.

### Código vulnerável

```java
// GerenciadorTarefas.java — antes
server.setExecutor(null);   // pool sem limite → vulnerável a thread exhaustion
server.start();
```

### Código corrigido

```java
// GerenciadorTarefas.java — depois
server.setExecutor(Executors.newFixedThreadPool(10));
server.start();
```

### O que acontece com um pool fixo?

```
Pool de 10 threads criado na inicialização.

Requisição 1-10 chegam  → cada uma pega uma thread → processadas
Requisição 11 chega     → entra na fila (queue interna)
Requisição 11 aguarda   → quando uma das 10 termina, pega a thread liberada
```

A fila interna do `FixedThreadPool` é um `LinkedBlockingQueue` sem limite — em um
sistema de produção, você complementaria com um `ThreadPoolExecutor` configurando
também o tamanho máximo da fila e uma `RejectedExecutionHandler`.

### Escolha do número de threads

Não existe um valor universal. A heurística depende do tipo de trabalho:

| Tipo de operação | Fórmula orientativa          | Exemplo (4 CPUs) |
|------------------|------------------------------|------------------|
| CPU-intensiva    | Número de CPUs               | 4 threads        |
| I/O-intensiva    | CPUs × (1 + tempo_espera / tempo_cpu) | 10–20 threads |
| Banco de dados   | Limitado pelo pool de conexões | igual ao pool DB |

Para este projeto de estudo, 10 threads é suficiente para desenvolvimento local.

### Regra prática

> **Nunca use `setExecutor(null)` em um servidor HTTP de produção.**
> Sempre defina um executor com limite máximo de threads proporcional aos recursos
> da máquina e ao número de conexões do banco de dados disponíveis.

---

## 6. Exceções engolidas com System.out.println

### Teoria

**Engolir uma exceção** significa capturá-la sem relançá-la e sem comunicar a falha
ao chamador. É uma das práticas mais perigosas em Java porque cria uma divergência
entre o que o código *parece* fazer e o que ele *realmente* faz.

```java
// O chamador chama adicionarTarefa("Comprar pão")
// O banco de dados está fora do ar
// O catch imprime uma linha e retorna normalmente
// O chamador acredita que a tarefa foi criada → mentira silenciosa
```

Em um sistema de produção, isso pode causar:

- **Dados perdidos:** a operação falhou mas o sistema segue como se tivesse funcionado;
- **Estado inconsistente:** parte de uma transação executou, outra não;
- **Diagnóstico impossível:** o `System.out.println` some em ambientes de produção
  que redirecionam stdout ou usam logging estruturado.

### Por que System.out.println é inadequado para erros?

| Critério           | `System.out.println`      | `throw RuntimeException`     |
|--------------------|---------------------------|------------------------------|
| Comunica a falha   | Não (retorna normalmente) | Sim (propaga ao chamador)    |
| Rastreabilidade    | Apenas a mensagem         | Stack trace completo         |
| Integrável com logging | Não               | Sim (via causa encadeada)    |
| Testável           | Difícil                   | Fácil (`assertThrows`)       |
| Silencioso em prod | Sim (risco)               | Não (a exceção sobe)         |

### Código vulnerável

```java
// TarefaRepositoryMySQL.java — antes
public void adicionarTarefa(String descricao) {
    String sql = "INSERT INTO gerenciador_tarefa.tarefas (descricao, concluida) VALUES (?, ?)";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, descricao);
        pstmt.setBoolean(2, false);
        pstmt.executeUpdate();
    } catch (SQLException e) {
        System.out.println("Erro ao adicionar tarefa: " + e.getMessage());
        // retorna normalmente → chamador não sabe da falha
    }
}
```

### Código corrigido

```java
// TarefaRepositoryMySQL.java — depois
public void adicionarTarefa(String descricao) {
    String sql = "INSERT INTO gerenciador_tarefa.tarefas (descricao, concluida) VALUES (?, ?)";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, descricao);
        pstmt.setBoolean(2, false);
        pstmt.executeUpdate();
    } catch (SQLException e) {
        throw new RuntimeException("Erro ao adicionar tarefa", e);
        // causa original preservada → stack trace completo disponível
    }
}
```

### O padrão de encadeamento de exceções

```java
throw new RuntimeException("mensagem de contexto", e);
//                                                  ^
//                          causa original (SQLException) preservada
```

O `e` passado como segundo argumento é a **causa** (*cause*). Isso garante que o
stack trace completo da `SQLException` original esteja disponível para diagnóstico,
sem vazar detalhes de SQL para a resposta HTTP (o handler converte para 500 genérico).

### Fluxo completo após a correção

```
TarefaRepositoryMySQL.adicionarTarefa()
  └── lança RuntimeException("Erro ao adicionar tarefa", sqlException)
        └── TarefaServiceImpl.adicionarTarefa()
              └── exceção sobe (não capturada)
                    └── TarefaHandler.handlePost()
                          └── catch (Exception e)
                                └── sendResponse(exchange, 500, "Erro interno ao criar tarefa")
                                    // detalhes SQL nunca chegam ao cliente
```

### Regra prática

> **Nunca use `System.out.println` para tratar erros em camadas de dados.**
> Ou relance a exceção (com ou sem encapsulamento), ou registre com um framework
> de logging (`java.util.logging`, SLF4J, Log4j) E relance.
> Uma exceção capturada e silenciada é um bug esperando para acontecer em produção.

---

## Checklist de revisão de segurança

Use esta lista ao revisar ou implementar novos endpoints e repositórios:

### Handler HTTP
- [ ] Respostas JSON usam `ObjectMapper` — nunca concatenação de strings
- [ ] Leitura do corpo usa loop até EOF com contador de bytes acumulado
- [ ] Todos os métodos `handle()` chamam `addSecurityHeaders()`
- [ ] IDs e parâmetros de rota são validados (tipo e intervalo) antes de usar
- [ ] Exceções internas retornam mensagens genéricas ao cliente (sem stack trace)

### Repositório JDBC
- [ ] Todo `Connection`, `Statement`/`PreparedStatement` e `ResultSet` está em `try-with-resources`
- [ ] Queries com parâmetros usam `PreparedStatement` — nunca concatenação de SQL
- [ ] Exceções `SQLException` são relançadas como `RuntimeException` com a causa encadeada
- [ ] Nenhum `System.out.println` em blocos `catch`

### Servidor HTTP
- [ ] `setExecutor()` recebe um pool com tamanho máximo definido
- [ ] Porta exposta é adequada ao ambiente (não expor 8080 em produção sem firewall)

---

## Referências

- [OWASP Top 10](https://owasp.org/www-project-top-ten/) — lista das 10 vulnerabilidades mais críticas em aplicações web
- [OWASP Secure Headers Project](https://owasp.org/www-project-secure-headers/) — referência completa de headers de segurança HTTP
- [CWE-116](https://cwe.mitre.org/data/definitions/116.html) — Improper Encoding or Escaping of Output (JSON Injection)
- [CWE-400](https://cwe.mitre.org/data/definitions/400.html) — Uncontrolled Resource Consumption (Thread Exhaustion)
- [CWE-390](https://cwe.mitre.org/data/definitions/390.html) — Detection of Error Condition Without Action (Exceções engolidas)
- Documentação Java — [`InputStream.read()`](https://docs.oracle.com/en/java/docs/api/java.base/java/io/InputStream.html#read(byte%5B%5D))
- Documentação Java — [try-with-resources](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html)
