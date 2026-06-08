# Gerenciador de Tarefas — Contexto do Projeto

## Visão Geral

API REST de gerenciamento de tarefas escrita em **Java puro** (sem Spring Boot).
Usa `com.sun.net.httpserver.HttpServer` (JDK built-in) e JDBC via **HikariCP**.
Banco de dados padrão: **H2 em modo file** (`./data/tarefadb`). Implementação MySQL também existe, mas está desativada.

**Autenticação**: BCrypt + sessão server-side (cookie HttpOnly) + MFA TOTP (Google Authenticator) + CSRF token + rate limiting. Tarefas são escopadas por `usuario_id` (multi-usuário com isolamento estrito).

## Como rodar

```bash
# (opcional, recomendado) gerar uma KEK de 32 bytes para cifrar segredos MFA
openssl rand -base64 32   # copie o resultado e coloque em .env como AUTH_KEK=<valor>

mvn compile
mvn exec:java
# Frontend (login)  → http://localhost:8080/
# API tarefas       → http://localhost:8080/api/tarefas (requer sessão)
```

## Estrutura de Pacotes

```
com.gerenciadortarefas/
├── GerenciadorTarefas.java               # main() — instancia tudo e inicia o HttpServer
│
├── api/
│   ├── TarefaHandler.java                # CRUD de tarefas (lê usuarioId da sessão)
│   └── StaticHandler.java                # Serve src/main/recursos/static/*
│
├── model/                                # Domínio "Tarefa"
│   ├── Tarefa.java                       # Record (id, usuarioId, descricao, concluida)
│   ├── TarefaRepository.java             # Interface (CRUD escopado por usuarioId)
│   ├── TarefaRepositoryH2.java           # Implementação H2 (ativa)
│   └── TarefaRepositoryMySQL.java        # Implementação MySQL (inativa)
│
├── service/
│   ├── TarefaService.java                # Interface
│   └── TarefaServiceImpl.java            # Regras de negócio
│
├── auth/                                 # ── Subsistema de autenticação ──
│   ├── model/
│   │   ├── Usuario.java                  # Record (email, username, senha_hash, mfa…)
│   │   ├── Sessao.java                   # Record (token_hash, usuario_id, last_activity, csrf_token…)
│   │   ├── MfaPending.java               # Challenge curto entre senha-OK e código-OK
│   │   ├── UsuarioRepository.java + H2
│   │   ├── SessaoRepository.java + H2
│   │   └── MfaPendingRepository.java + H2
│   ├── service/
│   │   ├── PasswordHasher.java           # BCrypt cost 12 (at.favre.lib)
│   │   ├── TokenService.java             # SecureRandom 256-bit + SHA-256
│   │   ├── CryptoService.java            # AES-GCM com KEK de AUTH_KEK
│   │   ├── TotpService.java              # samstevens/totp + QR Code PNG inline
│   │   ├── RateLimiter.java              # Janela deslizante em memória
│   │   └── AuthService.java              # Orquestra: registrar, autenticar, MFA, logout
│   ├── api/
│   │   └── AuthHandler.java              # 8 rotas (públicas + autenticadas)
│   └── filter/
│       ├── SessionFilter.java            # Valida cookie + aplica timeout (30 min)
│       └── CsrfFilter.java               # Exige X-CSRF-Token nos mutadores
│
├── util/
│   ├── DatabaseConnectionH2.java         # HikariCP (10 conexões, H2 file mode)
│   ├── DatabaseConnection.java           # MySQL (inativo)
│   └── EnvLoader.java                    # Lê .env para variáveis de ambiente
│
├── controller/TarefaController.java      # CLI desativado (USUARIO_CLI hardcoded)
└── view/TarefaView.java                  # CLI view (desativado)
```

## Endpoints da API

### Públicos (contexto `/auth`)
| Método | Rota | Body | Descrição |
|---|---|---|---|
| POST | `/auth/register` | `{email, username, senha}` | Cria usuário; valida e-mail, username (3-60 alfanum.), senha (≥8, letras+números) |
| POST | `/auth/login` | `{login, senha}` | `login` aceita e-mail OU username. Retorna 200 com cookie+csrfToken; ou 200 com `mfaToken` se MFA ativo |
| POST | `/auth/mfa/verify` | `{mfaToken, codigo}` | Consome o challenge e cria sessão real |

### Autenticadas (contexto `/auth/account`, exige cookie de sessão)
| Método | Rota | Body | Descrição |
|---|---|---|---|
| POST | `/auth/account/logout` | — | Encerra sessão; grava `motivo_logout` |
| GET  | `/auth/account/me` | — | Dados do usuário logado (inclui `mfaHabilitado`) |
| GET  | `/auth/account/csrf` | — | Retorna `csrfToken` da sessão (recuperação após F5) |
| POST | `/auth/account/mfa/setup` | — | Gera secret + QR Code PNG inline (data URI) |
| POST | `/auth/account/mfa/confirm` | `{codigo}` | Valida 1º código e habilita MFA |

### Tarefas (contexto `/api/tarefas`, SessionFilter + CsrfFilter)
| Método | Rota | Body | Descrição |
|---|---|---|---|
| GET    | `/api/tarefas` | — | Lista tarefas **do usuário logado** |
| POST   | `/api/tarefas` | `{descricao}` | Cria tarefa (descricao ≤ 255 chars) |
| PUT    | `/api/tarefas/{id}` | `{descricao?, concluida?}` | Atualiza descrição e/ou status |
| DELETE | `/api/tarefas/{id}` | — | Remove tarefa |

> Toda chamada mutadora exige header `X-CSRF-Token` igual ao da sessão; ausência → 403.

## Entidades

```java
public record Tarefa(int id, int usuarioId, String descricao, boolean concluida) {
    public Tarefa comConcluida(boolean concluida) { ... }
}

public record Usuario(int id, String email, String username, String senhaHash,
                      boolean mfaHabilitado, byte[] otpSecretCifrado,
                      LocalDateTime ultimoLogin, LocalDateTime ultimoLogout) {}

public record Sessao(String tokenHash, int usuarioId,
                     LocalDateTime criadoEm, LocalDateTime lastActivity,
                     String ipAddress, String userAgent, String csrfToken,
                     LocalDateTime revogadaEm, String motivoLogout) {
    public boolean ativa() { return revogadaEm == null; }
}

public record MfaPending(String tokenHash, int usuarioId, LocalDateTime expiraEm) {}
```

## Banco de Dados

### H2 (ativo)
- Modo: **file** (`jdbc:h2:file:./data/tarefadb;AUTO_SERVER=TRUE;MODE=LEGACY`)
- Dados persistem entre reinicializações (usuários, sessões e tarefas sobrevivem)
- Pool: HikariCP (10 conexões; 2 idle mínimo; timeout 5 s)
- Tabelas criadas automaticamente pelos construtores dos repositórios — ordem: `usuarios` → `sessoes` / `mfa_pending` → `tarefas` (FKs para `usuarios`)

### Schema resumido

```sql
usuarios     (id, email UNIQUE, username UNIQUE, senha_hash, mfa_habilitado,
              otp_secret_cifrado VARBINARY, ultimo_login, ultimo_logout)

sessoes      (token_hash CHAR(64) PK, usuario_id FK, criado_em, last_activity,
              ip_address, user_agent, csrf_token, revogada_em, motivo_logout)

mfa_pending  (token_hash CHAR(64) PK, usuario_id FK, expira_em)

tarefas      (id PK, usuario_id FK, descricao, concluida)
```

### MySQL (inativo)
- Banco `gerenciador_tarefa`, mesmo schema lógico (tabela `tarefas` precisa coluna `usuario_id`)
- Ativar: trocar `TarefaRepositoryH2` por `TarefaRepositoryMySQL` em `GerenciadorTarefas.java` e definir `DB_PASSWORD`. Os repositórios de auth (`UsuarioRepositoryH2`, etc.) ainda não têm equivalente MySQL.

## Variáveis de Ambiente

| Variável | Padrão | Uso |
|---|---|---|
| `AUTH_KEK` | _gerada volátil + warning_ | Chave AES-256 (Base64 de 32 bytes) para cifrar `otp_secret`. **Definir em produção, senão segredos MFA não sobrevivem ao restart.** |
| `H2_URL` | `jdbc:h2:file:./data/tarefadb;AUTO_SERVER=TRUE;MODE=LEGACY` | URL do H2 (override possível para in-memory em testes) |
| `H2_USER` | `sa` | Usuário H2 |
| `H2_PASSWORD` | `""` | Senha H2 |
| `DB_URL` | `jdbc:mysql://localhost:3306/gerenciador_tarefa` | URL MySQL |
| `DB_USER` | `root` | Usuário MySQL |
| `DB_PASSWORD` | _obrigatória_ | Senha MySQL |
| `CORS_ORIGIN` | `http://localhost:8080` | Origem permitida no header `Access-Control-Allow-Origin` |

## Frontend (`src/main/recursos/static/`)

| Arquivo | Propósito |
|---|---|
| `login.html`     | 3 cards alternados: Login, MFA challenge, Registro |
| `mfa-setup.html` | QR Code inline (data URI) + confirmação do 1º código |
| `index.html`     | CRUD com edição inline, topbar (username, badge MFA, Sair, Configurar MFA). `apiFetch` injeta `X-CSRF-Token` automaticamente e redireciona para `/login.html` em 401 |

Cookie `sessao` é HttpOnly → JS não consegue lê-lo. `csrfToken` é guardado em `sessionStorage` (perda em F5/nova aba é tolerada: `GET /auth/account/csrf` reidrata).

## Decisões de Arquitetura

- **Sem Spring Boot:** projeto usa apenas JDK + Jackson + H2 + HikariCP + bcrypt + samstevens-totp
- **Records imutáveis:** todas as entidades são `record` Java
- **HikariCP obrigatório:** com `SessionFilter` no caminho de toda requisição autenticada, abrir conexão direto via `DriverManager` por request dobraria a latência
- **Sessão server-side** ao invés de JWT: revogação imediata + auditoria (`motivo_logout`) + alinhamento com requisito didático de "ultimo_login/ultimo_logout"
- **Cookie HttpOnly** ao invés de `Authorization: Bearer`: XSS não consegue exfiltrar o token
- **SHA-256 do token no DB** (não o token em texto): vazamento de DB não compromete sessões ativas
- **Token de challenge MFA efêmero**: impede pular o 2º fator chamando direto o endpoint de criação de sessão
- **Erros internos:** repositórios lançam `RuntimeException` com causa encadeada; handlers retornam mensagens genéricas (sem stack trace)
- **Sem framework de DI:** instâncias criadas manualmente em `main()`
- **Tarefas escopadas:** toda query inclui `WHERE usuario_id = ?` — IDOR (Insecure Direct Object Reference) é impossível por design

## Limitações Conhecidas

- **Backup codes de MFA não implementados** (recuperação se o usuário perder o celular)
- **Auditoria de tentativas de login** (`login_attempts`) não foi criada
- **CsrfFilter aplicado só em `/api/tarefas`** — `/auth/account/*` aceita mutações sem o header
- **Sem rotação de token pós-login** (defesa contra session fixation seria trivial: gerar novo token na transição "antes do login → depois")
- **Cookie não tem `Secure;`** — exigir HTTPS é responsabilidade do reverse proxy em produção
- **RateLimiter em memória** — single-instance only; cluster precisaria de Redis
- **Sem logging estruturado** (SLF4J/Logback) — apenas `System.out.println` em pontos de boot
- **Repositórios MySQL de auth ausentes** — só H2 implementa `UsuarioRepository`/`SessaoRepository`/`MfaPendingRepository`
- **CLI** (`TarefaController`/`TarefaView`) compila mas usa `USUARIO_CLI=1` hardcoded

## Dependências (pom.xml)

| Dependência | Versão | Uso |
|---|---|---|
| `com.h2database:h2` | 2.2.224 | Banco H2 |
| `com.mysql:mysql-connector-j` | 9.6.0 | Conector MySQL |
| `com.fasterxml.jackson.core:jackson-databind` | 2.15.2 | Serialização JSON |
| `com.zaxxer:HikariCP` | 5.1.0 | Connection pool |
| `at.favre.lib:bcrypt` | 0.10.2 | Hash de senhas (BCrypt cost 12, pure Java) |
| `dev.samstevens.totp:totp` | 1.7.1 | TOTP RFC 6238 + geração de QR Code PNG (ZXing transitivamente) |

Java: 25 | Maven: `exec:maven-plugin` + `assembly-plugin` (fat jar)
