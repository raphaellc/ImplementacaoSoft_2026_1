# ImplementacaoSoft_2026_1
Repositório de Códigos da AA de Implementação de Software 2026/1 - Porto Alegre

Este repositório centraliza as atividades práticas da disciplina. Utilizaremos um fluxo de trabalho profissional baseado em **Pull Requests** e **Integração Contínua (CI)** para automatizar a validação das entregas.

---

## Configurações de Software
### Linguagem de Programação
* Java - JDK 25.0.2

## 📂 1. Organização de Diretórios

Para que o corretor automático identifique sua entrega, a estrutura de pastas deve seguir rigorosamente este padrão:

### Entregas Individuais
Devem estar dentro de uma pasta com seu **Nome Completo** (substitua espaços por hífen e evite acentos).
* `Nome-Completo/exercicio1/`
* `Nome-Completo/exercicio2/`

### Trabalhos em Grupo
Devem ser colocados em uma pasta específica na **raiz do repositório**.
* `/Trabalho-Grupo-01/`
* `/Projeto-Final/`

---

## 🌿 2. Fluxo de Trabalho (GitFlow)

Siga estes passos para cada nova atividade:

1.  **Sincronize seu repositório local a partir da main:**
    ```bash
    git checkout main
    git pull origin main
    ```
2.  **Crie uma branch para suas tarefas a partir da branch main:**
    * *Individual:* `git checkout -b nome-aluno`
    * *Grupo:* `git checkout -b nome-do-grupo`
3.  **Desenvolva e Commite:** Certifique-se de que seu código compila localmente.
    ```bash
    git commit -m "exercicio x - atualizaçao Y | Final"
    ```
4.  **Envie para o GitHub:**
    ```bash
    git push origin sua-branch-de-entrega
    ```
5.  **Abra um Pull Request (PR):** No GitHub, solicite o PR da sua branch para a branch **`entrega`**.


---

## ✅ 3. Regras de Ouro e Automação

* **Compilação Obrigatória:** Ao abrir um PR, o **GitHub Actions** tentará compilar seu código Java automaticamente. 
    * ✅ **Verde:** O código compila. O professor fará a revisão lógica.
    * ❌ **Vermelho:** O código falhou. **A entrega não será avaliada** até que você corrija os erros e faça o push novamente no mesmo PR.
* **Acesso e Permissões:** * A branch `entrega` é protegida. Pushes diretos serão rejeitados.
    * Apenas o **Professor (Admin)** tem permissão para aprovar e finalizar o Merge dos PRs.
* **Ética e Colaboração:** Embora todos sejam colaboradores, é expressamente proibido alterar arquivos nas branches dos demais e na branch entregas.

# Guia de Contribuição e Boas Práticas

Para manter a qualidade, coesão e legibilidade do nosso código, adotamos as seguintes convenções e práticas de arquitetura de software para os exercícios e desafios deste repositório.

---

## 4. Guia de Contribuição
### Convenções de Nomenclatura (Padrão Java) ☕

Seguimos as convenções oficiais da linguagem para garantir que o código seja idiomático:

* **Classes e Interfaces:** Devem utilizar `PascalCase` (ex: `CalculadoraDeEstoque`, `Pagamento`). O nome do arquivo `.java` deve ser exatamente igual ao da classe pública.
* **Métodos e Atributos:** Devem utilizar `camelCase`, iniciando com a primeira letra minúscula (ex: `mostrarDetalhes()`, `valorDiaria`).
* **Constantes:** Escritas em `UPPER_SNAKE_CASE` e declaradas como `static final` (ex: `TAXA_A_RETER`).
* **Pacotes (Packages):** Escritos totalmente em letras minúsculas, utilizando o domínio reverso (ex: `br.com.empresa.projeto`) para evitar ambiguidade.