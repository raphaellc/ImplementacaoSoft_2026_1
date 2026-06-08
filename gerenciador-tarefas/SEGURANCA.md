# Segurança em APIs REST Java — Material Didático

Este documento descreve as falhas de segurança identificadas no projeto **Gerenciador de Tarefas**
e as correções aplicadas. Para cada item você encontrará a teoria por trás do problema, o código
vulnerável, o código corrigido e como testar a diferença.

---

## Sumário

**Parte I — Falhas básicas do handler HTTP e do repositório**

1. [Injeção de JSON](#1-injeção-de-json)
2. [Leitura parcial do corpo HTTP](#2-leitura-parcial-do-corpo-http)
3. [Headers de segurança HTTP](#3-headers-de-segurança-http)
4. [ResultSet fora do try-with-resources](#4-resultset-fora-do-try-with-resources)
5. [Thread pool ilimitado](#5-thread-pool-ilimitado)
6. [Exceções engolidas com System.out.println](#6-exceções-engolidas-com-systemoutprintln)

**Parte II — Subsistema de autenticação**

7. [Hash de senhas (BCrypt vs. texto puro / MD5 / SHA)](#7-hash-de-senhas)
8. [Tokens de sessão seguros (SecureRandom + SHA-256 no DB)](#8-tokens-de-sessão-seguros)
9. [Cookie HttpOnly vs Authorization Bearer (defesa contra XSS)](#9-cookie-httponly-vs-authorization-bearer)
10. [CSRF — proteção via double-submit token](#10-csrf--proteção-via-double-submit-token)
11. [MFA TOTP — o token de challenge efêmero](#11-mfa-totp--o-token-de-challenge-efêmero)
12. [Cifragem em repouso do segredo MFA (AES-GCM)](#12-cifragem-em-repouso-do-segredo-mfa)
13. [Rate limiting (defesa contra força bruta)](#13-rate-limiting)
14. [Escopo por usuário (defesa contra IDOR)](#14-escopo-por-usuário)

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

## 7. Hash de senhas

### Teoria

Salvar senhas em texto puro no banco transforma qualquer vazamento de DB em vazamento total de credenciais. Pior: usuários costumam reutilizar senhas, então o vazamento da nossa aplicação compromete contas em outros sites.

Hashes "rápidos" como **MD5** e **SHA-256 puro** também são inadequados. Eles foram desenhados para ser velozes em hardware moderno — uma GPU comum testa ~10⁹ hashes/s, então mesmo senhas de 8 caracteres aleatórios são quebradas por brute-force em horas.

A defesa correta é um **password hashing function** especializado, com duas propriedades:

1. **Custo ajustável (work factor):** podemos aumentar o tempo de cálculo conforme o hardware evolui (BCrypt usa um expoente: cost 12 ≈ 250 ms por hash).
2. **Salt automático embutido:** cada hash inclui um valor aleatório único, impedindo rainbow tables e que duas senhas iguais produzam hashes idênticos.

BCrypt (1999) é o mais conservador e bem testado. Argon2id (2015) é mais moderno mas exige bibliotecas com binding nativo (JNI). Escolhemos **BCrypt cost 12** via `at.favre.lib:bcrypt` (pure Java).

### Código vulnerável

```java
// HIPOTÉTICO — o que NÃO fazer
String senhaHash = senha;                              // texto puro
String senhaHash = sha256(senha);                      // sem salt, hash rápido
String senhaHash = sha256(senha + "minha-app-salt");   // salt fixo (= sem salt)
```

### Código aplicado

```java
// auth/service/PasswordHasher.java
public class PasswordHasher {
    private static final int COST = 12;

    public String hash(String senhaPura) {
        return BCrypt.withDefaults().hashToString(COST, senhaPura.toCharArray());
    }

    public boolean verificar(String senhaPura, String hash) {
        if (senhaPura == null || hash == null) return false;
        return BCrypt.verifyer().verify(senhaPura.toCharArray(), hash).verified;
    }
}
```

### Anatomia do hash gerado

```
$2a$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
 ^   ^^ ^^^^^^^^^^^^^^^^^^^^^^ ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 alg cost   salt (22 chars)              hash (31 chars)
```

Tudo num só string: algoritmo, custo, salt e hash. Não precisa de coluna separada para o salt.

### Comparação em tempo constante

O método `verify()` da `at.favre.lib:bcrypt` compara byte a byte sem early return — impede **timing attacks** onde o atacante mede o tempo de resposta para deduzir quantos bytes do hash conferem.

### Regra prática

> **Senhas se hash com BCrypt, Argon2id ou scrypt — nunca SHA/MD5 puro nem texto cru.**
> O custo deve dar pelo menos 250 ms num CPU moderno. Compare sempre com a função `verify()` da própria lib (constant-time), nunca com `String.equals()`.

---

## 8. Tokens de sessão seguros

### Teoria

Uma sessão server-side é "uma linha numa tabela que prova quem o cliente é". A segurança dela depende de duas coisas:

1. **O token entregue ao cliente é imprevisível** — `Math.random()`, `UUID.randomUUID()` simples ou contadores são inadequados.
2. **O que está no DB não é o token** — se o DB vazar, o atacante não deveria conseguir sequestrar sessões ativas.

**Por que UUID v4 é "ok mas não ótimo":** ele usa `SecureRandom` mas só carrega 122 bits de entropia (6 bits são reservados para versão/variante). 256 bits é o padrão recomendado por OWASP. Em `SecureRandom`, 32 bytes = 256 bits.

**Por que armazenar hash do token:** o token é credencial. Se o DB cair em mãos erradas e tiver o token em texto, o atacante imediatamente assume a sessão de qualquer usuário sem precisar quebrar senha. Armazenando `SHA-256(token)`, o vazamento só revela hashes — inúteis para login.

> Nota: aqui não precisamos de salt no hash do token porque o token já tem 256 bits de aleatoriedade. Salt protege contra dicionário/rainbow table — irrelevante para entradas aleatórias longas.

### Código aplicado

```java
// auth/service/TokenService.java
public class TokenService {
    private static final SecureRandom RNG = new SecureRandom();
    private static final Base64.Encoder ENC = Base64.getUrlEncoder().withoutPadding();

    /** Token entregue ao cliente — 256 bits, URL-safe, não persistido em texto. */
    public String gerarToken() {
        byte[] bytes = new byte[32];
        RNG.nextBytes(bytes);
        return ENC.encodeToString(bytes);
    }

    /** Hash SHA-256 hex — esse é o valor persistido. */
    public String hash(String token) {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(token.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder(64);
        for (byte b : digest) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
```

### Fluxo

```
Login:
  tokenPlano = TokenService.gerarToken()           ──→ vai no cookie
  tokenHash  = TokenService.hash(tokenPlano)       ──→ vai no DB

Request seguinte:
  cookie     = <tokenPlano enviado pelo browser>
  tokenHash  = TokenService.hash(cookie)
  SessaoRepository.buscarPorTokenHash(tokenHash)   ──→ encontra a sessão
```

### Regra prática

> **Use `SecureRandom` (≥ 256 bits) para qualquer token de autenticação.**
> No banco, guarde o hash do token, não o token em si — assim um SELECT desautorizado não vira logon imediato.

---

## 9. Cookie HttpOnly vs Authorization Bearer

### Teoria

Em SPAs de browser, o token de sessão pode trafegar de duas formas:

| Mecanismo | Como o JS envia | Risco de XSS |
|---|---|---|
| `Authorization: Bearer <token>` | JS lê de `localStorage` / variável e adiciona header em cada `fetch` | **Alto** — XSS lê o token e exfiltra |
| Cookie `HttpOnly` | Browser anexa automaticamente; JS **não** pode ler | **Baixo** — XSS não consegue ler o cookie |

Cross-Site Scripting (XSS) é praticamente inevitável de prevenir 100%. Qualquer biblioteca terceira que entre no bundle, qualquer dependência transitiva atualizada com payload malicioso pode rodar JS no contexto do app. O cookie `HttpOnly` torna o token **inacessível ao JS por design** — XSS pode fazer requisições em nome do usuário enquanto o ataque dura, mas não pode roubar o token para uso permanente.

Os atributos importantes:

```
Set-Cookie: sessao=<token>; Path=/; Max-Age=1800; HttpOnly; SameSite=Strict
                                                  ^^^^^^^^  ^^^^^^^^^^^^^^^^
                                                  bloqueia  bloqueia envio
                                                  JS        cross-site (CSRF)
```

(Em produção HTTPS adiciona-se `Secure;` para impedir envio sobre HTTP.)

### Código aplicado

```java
// auth/api/AuthHandler.java
private void gravarCookieSessao(HttpExchange ex, String tokenPlano) {
    long maxAge = AuthService.SESSAO_TIMEOUT.toSeconds();
    ex.getResponseHeaders().add("Set-Cookie",
        "sessao=" + tokenPlano + "; Path=/; Max-Age=" + maxAge +
        "; HttpOnly; SameSite=Strict");
}
```

### Como verificar

No browser, abra DevTools → console:
```js
document.cookie    // não deve listar "sessao"
```
Os cookies HttpOnly aparecem em DevTools → Application → Cookies, mas não em `document.cookie`.

### Regra prática

> **Para SPAs de browser, o token de sessão vai em cookie `HttpOnly + SameSite=Strict + Secure` — não em `Authorization` header.**
> Mobile apps e APIs server-to-server podem usar Bearer porque não têm contexto de browser/XSS.

---

## 10. CSRF — proteção via double-submit token

### Teoria

**Cross-Site Request Forgery** explora o fato de que o browser anexa cookies automaticamente. Imagine este HTML em um site malicioso (`evil.com`) visitado por um usuário logado em `localhost:8080`:

```html
<form action="http://localhost:8080/api/tarefas" method="POST" id="f">
  <input name="descricao" value="hackeado">
</form>
<script>document.getElementById('f').submit();</script>
```

O browser do usuário envia a requisição com o cookie `sessao` anexado — do ponto de vista do servidor, é um POST legítimo do usuário logado.

`SameSite=Strict` no cookie já bloqueia esse cenário em browsers modernos: o cookie não vai junto se a requisição vem de outra origem. Mas é **defesa em profundidade** ter um segundo mecanismo, porque:

- Bugs/exceções em SameSite podem aparecer (subdomínios, redirects)
- Browsers antigos podem não respeitar
- Misconfigurações de proxy/CDN podem alterar headers

O padrão **double-submit cookie** funciona assim:

1. Servidor gera um `csrfToken` único na sessão (não relacionado ao token de sessão).
2. JS legítimo (mesmo origem) lê o token via endpoint `/auth/account/csrf` e envia como header `X-CSRF-Token` em cada POST/PUT/DELETE.
3. Servidor compara o header com o `csrfToken` da sessão.
4. JS atacante (cross-origin) não consegue ler o token (Same-Origin Policy do browser) → não consegue forjar o header.

### Código aplicado

```java
// auth/filter/CsrfFilter.java
public class CsrfFilter extends Filter {
    private static final Set<String> MUTADORES = Set.of("POST", "PUT", "DELETE", "PATCH");

    @Override
    public void doFilter(HttpExchange ex, Chain chain) throws IOException {
        if (MUTADORES.contains(ex.getRequestMethod())) {
            Sessao s = (Sessao) ex.getAttribute("sessao");
            String headerCsrf = ex.getRequestHeaders().getFirst("X-CSRF-Token");
            if (s == null || headerCsrf == null || !s.csrfToken().equals(headerCsrf)) {
                // 403 — bloqueia mutação
                return;
            }
        }
        chain.doFilter(ex);
    }
}
```

E no frontend (`index.html`):

```javascript
async function apiFetch(url, opts = {}) {
    opts.credentials = 'include';
    opts.headers = opts.headers || {};
    if (opts.method && opts.method !== 'GET') {
        opts.headers['X-CSRF-Token'] = CSRF;   // lido de sessionStorage
    }
    return fetch(url, opts);
}
```

### Por que GETs não precisam do header?

GETs devem ser **idempotentes e safe** — não alteram estado. Se um GET muda algo no servidor, isso já é um anti-pattern (CSRF é o menor dos problemas).

### Regra prática

> **Use SameSite=Strict no cookie como primeira camada e CSRF token em header como segunda.**
> Apenas métodos mutadores (POST/PUT/DELETE/PATCH) precisam do header.

---

## 11. MFA TOTP — o token de challenge efêmero

### Teoria

MFA (multi-factor authentication) exige duas evidências distintas: algo que você **sabe** (senha) + algo que você **tem** (código gerado pelo app autenticador). TOTP (RFC 6238) é o padrão usado pelo Google Authenticator: um código de 6 dígitos derivado de `HMAC-SHA1(segredoCompartilhado, tempoAtual / 30s)`.

**A falha sutil:** se o servidor expõe dois endpoints separados — um para "validar senha" e outro para "validar TOTP e criar sessão" — um atacante com a senha vazada pode pular o segundo. Basta chamar direto o endpoint de criação de sessão se ele aceitar só o usuario_id.

A defesa é fazer da etapa "senha OK" um estado intermediário **com seu próprio token efêmero**:

```
1. POST /auth/login        { login, senha }
   → 200 { mfaToken: "..." }       ← válido por 5 min, uso único, no DB

2. POST /auth/mfa/verify   { mfaToken, codigo }
   → consome o mfaToken (DELETE)
   → valida o código TOTP
   → CRIA a sessão (cookie)
```

Sem o `mfaToken` válido, é impossível chegar à criação de sessão. O `mfaToken` é entregue só depois da senha estar correta, então conhecer a senha é pré-requisito.

### Tabela de challenge

```sql
mfa_pending (
    token_hash CHAR(64) PRIMARY KEY,
    usuario_id INT NOT NULL,
    expira_em TIMESTAMP NOT NULL
)
```

`token_hash` por mesmo motivo do token de sessão — vazamento de DB não permite forjar challenges válidos.

### Consumo atômico

```java
// auth/model/MfaPendingRepositoryH2.java
public Optional<MfaPending> consumir(String tokenHash) {
    // SELECT + DELETE numa única transação
    // → impede que dois requests "concorrentes" usem o mesmo token
}
```

### Validação do TOTP

```java
// auth/service/TotpService.java
public boolean verificar(String secretBase32, String codigoDigitado) {
    return verifier.isValidCode(secretBase32, codigoDigitado.trim());
}
```

A biblioteca aceita janela de ±1 step (±30s) por padrão — tolera relógios levemente desincronizados sem abrir muito a janela.

### Regra prática

> **Toda etapa intermediária de um fluxo multi-passo precisa de um token de continuação único, curto e consumível.**
> Caso contrário, o atacante pula etapas chamando direto o endpoint seguinte.

---

## 12. Cifragem em repouso do segredo MFA

### Teoria

O segredo TOTP que o app autenticador armazena é a chave compartilhada usada para gerar todos os códigos futuros. **Quem tem o segredo gera códigos válidos para sempre**, até o usuário trocá-lo.

Se guardamos esse segredo em texto puro no banco e o DB vaza, o atacante:
1. Já tem o e-mail/username (vazaram juntos)
2. Já tem a senha (provavelmente quebrável via dicionário se o hash for fraco)
3. Agora também tem o segredo MFA → consegue gerar o código de 6 dígitos → MFA inútil

A defesa é cifrar o segredo no DB com uma chave que **não está no DB** — geralmente uma KEK (Key Encryption Key) armazenada em variável de ambiente, vault, ou KMS.

**Por que AES-GCM e não AES-CBC?** AES-GCM é **AEAD** (autenticated encryption with associated data): além de cifrar, ele autentica. Se alguém alterar 1 byte do ciphertext no DB, a decifragem falha — isso impede ataques de manipulação de bytes.

### Código aplicado

```java
// auth/service/CryptoService.java
public class CryptoService {
    private final SecretKeySpec key;   // 32 bytes lidos de AUTH_KEK (env var, Base64)

    public byte[] cifrar(String plaintext) {
        byte[] iv = new byte[12];                              // IV único por mensagem
        new SecureRandom().nextBytes(iv);
        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
        byte[] ct = c.doFinal(plaintext.getBytes(UTF_8));
        return ByteBuffer.allocate(iv.length + ct.length).put(iv).put(ct).array();
    }
    // decifrar é simétrico, lê IV dos primeiros 12 bytes
}
```

### Importância do IV aleatório por mensagem

Reusar IV em AES-GCM é catastrófico — permite recuperar o plaintext de ambas as mensagens cifradas com o mesmo IV. Por isso geramos um novo IV de 12 bytes a cada `cifrar()` e o prefixamos ao ciphertext.

### O que falta para "produção real"

- KEK numa variável de ambiente é "ok para estudo". Em produção: AWS KMS, GCP KMS, HashiCorp Vault — onde a KEK nunca toca a aplicação em texto.
- **Rotação de KEK:** deveria ser possível reencriptar todos os segredos sem downtime.
- **Backup codes** para perda do celular (não implementado nesta iteração).

### Regra prática

> **Dados sensíveis "que precisam voltar ao formato original" (não-hashes) usam cifragem simétrica com chave em fora do banco.**
> AES-GCM com IV único por mensagem é a escolha padrão. Nunca AES-ECB. Cuidado com reuso de IV em GCM.

---

## 13. Rate limiting

### Teoria

Um endpoint de login sem rate limit é um convite para ataques de **credential stuffing** (testar listas de senhas vazadas) e **brute force** (testar todas as combinações). Mesmo BCrypt cost 12 (250 ms) não salva — em 24h o atacante testa ~345 mil senhas por IP.

A defesa básica é uma **janela deslizante**: contar quantas tentativas vieram de um IP nos últimos N minutos e bloquear se passar de um limite.

Decisões de design:

- **Por IP, por usuário, ou ambos?** Por IP é o mínimo; por usuário também ajuda mas pode habilitar DoS (alguém propositalmente trava sua conta). Aqui fazemos só por IP.
- **Quantas tentativas?** 5 em 15 min é a recomendação OWASP para endpoints de autenticação.
- **Limpar contador no sucesso:** um usuário legítimo que errou a senha 3x e acertou na 4ª deve voltar ao zero, não ficar com 4 estritas no histórico.
- **In-memory vs persistido:** in-memory é simples mas perde tudo no restart e não funciona em cluster. Redis é o padrão para produção multi-instância.

### Código aplicado

```java
// auth/service/RateLimiter.java
public class RateLimiter {
    private final int maxTentativas;
    private final Duration janela;
    private final Map<String, Deque<Instant>> historico = new ConcurrentHashMap<>();

    public boolean permitir(String chave) {
        Instant agora = Instant.now();
        Instant limite = agora.minus(janela);
        Deque<Instant> fila = historico.computeIfAbsent(chave, k -> new ArrayDeque<>());
        synchronized (fila) {
            while (!fila.isEmpty() && fila.peekFirst().isBefore(limite)) fila.pollFirst();
            if (fila.size() >= maxTentativas) return false;
            fila.addLast(agora);
            return true;
        }
    }

    public void limpar(String chave) { historico.remove(chave); }
}
```

Uso no `AuthHandler`:

```java
private final RateLimiter loginLimiter = new RateLimiter(5, Duration.ofMinutes(15));
private final RateLimiter mfaLimiter   = new RateLimiter(8, Duration.ofMinutes(15));

private void login(HttpExchange ex) {
    String ip = ipDoCliente(ex);
    if (!loginLimiter.permitir(ip)) {
        responder(ex, 429, "...muitas tentativas, aguarde 15 minutos");
        return;
    }
    // ... valida credenciais
    if (sucesso) loginLimiter.limpar(ip);   // libera o contador
}
```

### Pegadinhas

- **Cliente atrás de NAT/proxy:** muitos usuários compartilham o mesmo IP público (ex.: rede de empresa). Limite por IP pode acidentalmente bloquear gente. Considerar combinar IP + user-agent ou usar `X-Forwarded-For` confiável.
- **IPv6:** trate `2001:db8::1` e `2001:db8::2` como diferentes mas considere que um atacante pode rotacionar 2⁶⁴ endereços facilmente.

### Regra prática

> **Endpoints de autenticação SEMPRE têm rate limit.** 5 tentativas / 15 min é um bom default OWASP.
> Aplique também em rotas que podem causar custo desproporcional (geração de relatórios, envio de e-mail, OTP por SMS).

---

## 14. Escopo por usuário

### Teoria

**IDOR (Insecure Direct Object Reference)** é a vulnerabilidade #1 de aplicações multi-usuário. Ocorre quando o servidor confia no ID enviado pelo cliente sem checar autorização:

```java
// VULNERÁVEL
GET  /api/tarefas/42        →  retorna tarefa 42, seja de quem for
DELETE /api/tarefas/99      →  deleta tarefa 99, seja de quem for
```

Basta ao atacante incrementar IDs (`/api/tarefas/1`, `/2`, `/3`...) para ler/modificar dados de outros usuários. Isso é tão comum que aparece em quase toda OWASP Top 10 ("Broken Access Control" é o #1 desde 2021).

A defesa estrutural é **escopar todas as queries pelo `usuario_id` da sessão**, não pelo ID enviado pelo cliente. Cliente envia `id da tarefa`; servidor filtra por `id AND usuario_id = ?`.

### Código aplicado

A interface obriga `usuarioId` em todas as operações:

```java
// model/TarefaRepository.java
public interface TarefaRepository {
    Tarefa adicionarTarefa(int usuarioId, String descricao);
    boolean atualizarTarefa(Tarefa tarefaAtualizada);        // tarefa carrega usuarioId
    boolean deletarTarefa(int usuarioId, int id);
    List<Tarefa> listarTarefas(int usuarioId);
    Optional<Tarefa> buscarPorId(int usuarioId, int id);
}
```

Implementação H2:

```java
// model/TarefaRepositoryH2.java
public boolean deletarTarefa(int usuarioId, int id) {
    String sql = "DELETE FROM tarefas WHERE id = ? AND usuario_id = ?";
    // ... bind id e usuarioId
}

public List<Tarefa> listarTarefas(int usuarioId) {
    String sql = "SELECT * FROM tarefas WHERE usuario_id = ? ORDER BY id";
    // ...
}
```

Handler obtém o `usuarioId` da sessão (nunca do request):

```java
// api/TarefaHandler.java
public void handle(HttpExchange exchange) {
    // ...
    Integer usuarioId = (Integer) exchange.getAttribute("usuarioId");  // injetado pelo SessionFilter
    if (usuarioId == null) { sendResponse(exchange, 401, "Não autenticado"); return; }
    // ...
}
```

### O que isso garante

```
Cenário:  Alice tem a tarefa 10. Bob (logado) faz DELETE /api/tarefas/10
SQL:      DELETE FROM tarefas WHERE id = 10 AND usuario_id = <id-do-Bob>
Resultado: 0 linhas afetadas → 404 "não encontrada" para Bob; tarefa de Alice intacta
```

Note que retornamos **404 e não 403** propositalmente — não vazamos a existência do recurso para usuários não autorizados.

### Padrão "row-level security"

Conceitualmente isso é equivalente a RLS (Row-Level Security) implementado na camada do servidor de banco. PostgreSQL e SQL Server suportam RLS nativamente; em H2 e MySQL implementamos na aplicação. O essencial é a regra:

> **Toda query que toca uma tabela "de usuário" inclui `WHERE owner_id = current_user`.**

### Regra prática

> **Nunca confie em IDs enviados pelo cliente para autorização.**
> Toda query de leitura/escrita em recurso "do usuário" deve filtrar pelo `usuario_id` extraído da sessão. Use 404 (não 403) para não vazar existência. Considere testes automatizados que tentem `DELETE /resource/<id-de-outro-user>` e exijam 404.

---

## Checklist de revisão de segurança

Use esta lista ao revisar ou implementar novos endpoints e repositórios:

### Handler HTTP
- [ ] Respostas JSON usam `ObjectMapper` — nunca concatenação de strings
- [ ] Leitura do corpo usa loop até EOF com contador de bytes acumulado
- [ ] Todos os métodos `handle()` chamam `addSecurityHeaders()`
- [ ] IDs e parâmetros de rota são validados (tipo e intervalo) antes de usar
- [ ] Exceções internas retornam mensagens genéricas ao cliente (sem stack trace)
- [ ] `usuarioId` lido de `exchange.getAttribute("usuarioId")` — nunca do body/path
- [ ] Métodos mutadores estão atrás do `CsrfFilter` (header `X-CSRF-Token` obrigatório)

### Repositório JDBC
- [ ] Todo `Connection`, `Statement`/`PreparedStatement` e `ResultSet` está em `try-with-resources`
- [ ] Queries com parâmetros usam `PreparedStatement` — nunca concatenação de SQL
- [ ] **Toda query toca tabela "de usuário" inclui `WHERE usuario_id = ?`** (defesa anti-IDOR)
- [ ] Exceções `SQLException` são relançadas como `RuntimeException` com a causa encadeada
- [ ] Nenhum `System.out.println` em blocos `catch`

### Autenticação
- [ ] Senhas hashadas com BCrypt cost ≥ 12 (nunca MD5/SHA puro nem texto cru)
- [ ] Comparação de senha via `BCrypt.verifyer().verify(...)` (constant-time)
- [ ] Tokens de sessão gerados com `SecureRandom` (≥ 32 bytes) e Base64 URL-safe
- [ ] No banco fica `SHA-256(token)`, nunca o token em texto
- [ ] Cookie de sessão tem `HttpOnly; SameSite=Strict; Max-Age=<timeout>;` (e `Secure;` em produção)
- [ ] MFA usa challenge token efêmero (TTL ≤ 5 min, consumível uma única vez)
- [ ] Segredos TOTP cifrados em repouso com AES-GCM (KEK fora do banco)
- [ ] Endpoints de autenticação (login, mfa/verify) têm `RateLimiter`
- [ ] Rate limit é limpo no sucesso para não punir usuário legítimo

### Servidor HTTP
- [ ] `setExecutor()` recebe um pool com tamanho máximo definido
- [ ] Porta exposta é adequada ao ambiente (não expor 8080 em produção sem firewall)
- [ ] `SessionFilter` aplicado em todos os contextos de API que exigem autenticação
- [ ] Conexões com banco vêm de pool (HikariCP) — não `DriverManager.getConnection()` direto

---

## Referências

### Geral
- [OWASP Top 10](https://owasp.org/www-project-top-ten/) — lista das 10 vulnerabilidades mais críticas em aplicações web
- [OWASP Application Security Verification Standard (ASVS)](https://owasp.org/www-project-application-security-verification-standard/) — checklist completo de verificação de segurança
- [OWASP Cheat Sheet Series](https://cheatsheetseries.owasp.org/) — guias práticos por tópico

### Parte I — Falhas básicas
- [OWASP Secure Headers Project](https://owasp.org/www-project-secure-headers/) — referência de headers de segurança HTTP
- [CWE-116](https://cwe.mitre.org/data/definitions/116.html) — Improper Encoding or Escaping of Output (JSON Injection)
- [CWE-400](https://cwe.mitre.org/data/definitions/400.html) — Uncontrolled Resource Consumption (Thread Exhaustion)
- [CWE-390](https://cwe.mitre.org/data/definitions/390.html) — Detection of Error Condition Without Action (Exceções engolidas)
- Documentação Java — [`InputStream.read()`](https://docs.oracle.com/en/java/docs/api/java.base/java/io/InputStream.html#read(byte%5B%5D)) · [try-with-resources](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html)

### Parte II — Autenticação
- [OWASP Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html) — BCrypt vs Argon2 vs scrypt; custos recomendados
- [OWASP Session Management Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Session_Management_Cheat_Sheet.html) — geração, entropia, timeout e revogação de sessões
- [OWASP CSRF Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html) — double-submit, SameSite, sync token
- [OWASP Multifactor Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Multifactor_Authentication_Cheat_Sheet.html) — TOTP, backup codes, recuperação
- [OWASP Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html) — rate limiting, lockout, mensagens genéricas
- [RFC 6238](https://www.rfc-editor.org/rfc/rfc6238) — TOTP: Time-Based One-Time Password Algorithm
- [RFC 6265](https://www.rfc-editor.org/rfc/rfc6265) — HTTP State Management Mechanism (cookies, atributos)
- [CWE-256](https://cwe.mitre.org/data/definitions/256.html) — Plaintext Storage of a Password
- [CWE-352](https://cwe.mitre.org/data/definitions/352.html) — Cross-Site Request Forgery (CSRF)
- [CWE-639](https://cwe.mitre.org/data/definitions/639.html) — Authorization Bypass Through User-Controlled Key (IDOR)
- [CWE-307](https://cwe.mitre.org/data/definitions/307.html) — Improper Restriction of Excessive Authentication Attempts (Brute Force)
- [NIST SP 800-63B](https://pages.nist.gov/800-63-3/sp800-63b.html) — Digital Identity Guidelines (autenticadores e fatores)
