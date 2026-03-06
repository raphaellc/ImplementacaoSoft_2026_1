Project Rules

## Git & Commits

- **Conventional Commits only** — Follow [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/). Use `feat:` for features, `fix:` for bug fixes, `docs:` for docs, `refactor:` for non-feature/non-fix changes. Optional scope in parentheses (e.g. `feat(automatic-renovation):`).
- **English only** — All commit messages (title and body) must be in English.
- **Branching** — Create a descriptive branch before starting work (e.g. `feat/feature-name`, `fix/bug-description`).
- **Atomic commits** — Prefer small, focused commits that do one logical thing.
- **Split commits when it makes sense** — Avoid a single commit that contains the entire PR. Split the work into multiple commits so each commit does one thing (e.g. add config, add resolver, wire processor, add tests). This simplifies review, bisecting, and reverts. If the change is tiny, one commit is fine; if it spans config + new class + integration + tests, separate when the split is natural.
- **Co-authored-by** — When generating or executing commits (via terminal or suggestion), **always** append this trailer at the end of the commit body so Cursor appears as co-author on GitHub:
  ```
  Co-authored-by: Cursor AI <cursor-ai@cursor.com>
  ```
  Example: `git commit -m "feat(scope): title" -m "Body." -m "Co-authored-by: Cursor AI <cursor-ai@cursor.com>"`.

## Autonomy & Safety

- **Act as an agent** — Proceed with the next steps until the task is done; verify with evidence (tests, command output, logs).
- **Avoid asking for confirmation** on common assumptions; state the assumption and proceed. Revert or adjust if evidence contradicts it.
- **Before large changes** — Ensure a green state (build and tests). If things work, commit before starting broad refactors.

## Execution Preferences (Java/Spring)

- **Build** — Use **Maven** for `gnarus` (`mvn test`, `mvn -DskipTests package`, etc.). Do not introduce other build tools.
- **Bug fixes** — Reproduce with a test or log, fix, run relevant tests, iterate until green.
- **Configuration** — Prefer `application.properties` or profiles; use `@ConfigurationProperties` for structured config.

## Code Standards (Java 17+, Spring Boot 3.x)

- **Line length** — Keep lines to a maximum of **120 characters**. Break long statements, method chains, or annotations across lines when they exceed this limit.
- **Line wrapping and indentation (Oracle-style)** — When a line is broken:
  - Break **after a comma**, or **before an operator**. Prefer higher-level breaks over lower-level ones.
  - **Align** the continuation with the start of the expression at the same level on the previous line; if that would look cramped or hit the margin, **indent 8 spaces** instead.
  - **Method calls:** break after comma; align continuation with first argument, or use 8-space indent for deep nesting.
    ```
    someMethod(longExpression1, longExpression2, longExpression3,
            longExpression4, longExpression5);
    ```
  - **Method declarations:** align continuation with first parameter; if that pushes too far right, use 8-space indent.
    ```
    someMethod(int anArg, Object anotherArg, String yetAnotherArg,
               Object andStillAnother) { ... }
    ```
  - **Arithmetic/expressions:** prefer breaking **outside** parenthesized subexpressions (higher-level break).
    ```
    longName1 = longName2 * (longName3 + longName4 - longName5)
               + 4 * longname6;   // PREFER
    ```
  - **if conditions:** use **8-space indent** for continuation lines so the body is easy to see.
    ```
    if ((condition1 && condition2)
            || (condition3 && condition4)
            || !(condition5 && condition6)) {
        doSomethingAboutIt();
    }
    ```
  - **Ternary:** either one line, or break with `?` and `:` on new lines aligned, or continuation indented 8 spaces.
    ```
    alpha = (aLongBooleanExpression) ? beta : gamma;
    alpha = (aLongBooleanExpression)
            ? beta
            : gamma;
    ```
- **Injection** — Use constructor injection; avoid field injection.
- **Explicit types** — Always use explicit type declarations for local variables (e.g. `boolean`, `String`, `List<Cart>`) instead of `var`. This improves readability and makes intent clear at a glance.
- **Naming** — PascalCase for classes, camelCase for methods and variables, ALL_CAPS for constants.
- **Structure** — Follow layers (controller / service / repository); keep low coupling.
- **Web/API** — Use proper REST (correct status codes, Bean Validation, `@ControllerAdvice` for exceptions).
- **Comments** — **Do not add comments** for normal code. Rely on clear names and small methods. Add a comment **only when the logic is genuinely hard to follow** (e.g. non-obvious algorithm, workaround for external quirk, non-obvious invariant); in that case keep the comment short and explain *why*, not *what*. Prefer refactoring to make the code self-explanatory before commenting.

## Design & best practices

Apply these principles when designing or changing code. Prefer clarity and maintainability over cleverness.

- **SOLID**
  - **S — Single Responsibility:** One class, one reason to change. If a class does “config + resolution + formatting”, split by responsibility.
  - **O — Open/Closed:** Extend via new types or strategy/plugin; avoid changing existing behavior by piling on `if` branches. Prefer composition and interfaces.
  - **L — Liskov Substitution:** Subtypes must be usable where the base type is expected; don’t weaken contracts or throw unexpected exceptions.
  - **I — Interface Segregation:** Small, focused interfaces (e.g. “read config” vs “write config”) rather than one fat interface. Callers depend only on what they use.
  - **D — Dependency Inversion:** Depend on abstractions (interfaces, contracts); inject them. High-level code should not depend on low-level details.

- **DRY (Don’t Repeat Yourself)** — Extract repeated logic or data into a single place (method, class, constant). If you copy-paste and then change one copy, consider a shared abstraction instead.

- **KISS (Keep It Simple, Stupid)** — Prefer the simplest design that works. Avoid unnecessary abstraction, indirection, or “future-proofing” that doesn’t solve a current need.

- **YAGNI (You Aren’t Gonna Need It)** — Don’t add code or structure for hypothetical future requirements. Implement what’s needed now; refactor when new needs appear.

- **Layered / Clean-style structure** — Keep clear boundaries: controllers handle HTTP and delegation; services hold use-case logic; repositories handle persistence. Domain logic should not depend on frameworks or I/O details. Dependencies point inward (e.g. controller → service → repository, not the reverse).

- **Naming and readability** — Names should reveal intent. Methods do one thing and are easy to name. Prefer small, composable methods over long procedures.

When in doubt, favor readability and testability; refactor in small steps and keep tests green.

## Testing

- **Framework** — JUnit 5. Use `MockMvc` for web layer; `@DataJpaTest` for repositories when applicable.
- **Coverage** — New or changed behavior must have tests (or updated existing tests), including edge cases.
- **Mocks** — Prefer **`@Mock`** (and `@InjectMocks` when it fits) with **`@ExtendWith(MockitoExtension.class)`** instead of creating mocks manually in `@BeforeEach`. Keeps test setup clear and consistent.
- **Assertions** — For simple conditions use the **concise JUnit 5 assertions**: `assertTrue(...)`, `assertFalse(...)`, `assertNull(...)`, `assertEquals(expected, actual)` (static import from `org.junit.jupiter.api.Assertions`). Prefer these over `assertThat(x).isTrue()`, `assertThat(x).isFalse()`, `assertThat(x).isNull()`, etc. Use AssertJ `assertThat(...)` when you need richer matchers (e.g. `assertThat(list).hasSize(2)`, `assertThat(string).contains("x")`, or custom matchers).
- **Avoid repeated calls in assertions** — When the same getter or method is used multiple times in assertions (e.g. `sentMail.getTo()`), store its result in a well-named local variable (e.g. `List<String> recipients = sentMail.getTo();`) and use that in subsequent assertions. This improves readability and avoids subtle behavior changes if the method implementation changes.

## Cursor Workflow

- **Short conversations** — If context shifts or the thread gets long, start a new chat.
- **Intentional temporary code** — Add a `// TODO:` comment explaining why, so the agent does not “fix” it automatically.

## Pull Request Description (template & checklist)

When writing or generating PR descriptions that use the repo template (e.g. `pull_request_template.md`), format the checklist at the end as follows:

- **`[x]`** — Item **done** / already satisfied by the PR or the author.
- **`[ ]`** — Item the **author still has to do** (e.g. self-review, add PR link to card, ensure build passes); leave unchecked.
- **`[NA]`** — Item **not applicable** to this PR (e.g. no migrations → all “Migrations and database” items `[NA]`; backend-only PR → all “Front” items `[NA]`).

Apply this to **all** checklist sections (General, Migrations and database, Back, Front) so reviewers see what is done, pending, or N/A.

---

# Agent Instructions

## Core Principle: Stay Within the Request

**Do not** change anything beyond what the user explicitly asked for.

## Guidelines

1. **Read the request carefully** — Understand exactly what is being asked before acting.
2. **Stay in scope** — Only modify, create, or update what the user specified.
3. **Do not assume** — If the user asks to update one test, do not update others unless they ask.
4. **Clarify when unclear** — If the request is ambiguous, ask instead of guessing.
5. **Respect manual changes** — If the user reverts or edits your changes, do not re-apply them unless asked.
6. **One thing at a time** — Finish the requested task before suggesting or doing extra work.

## Examples

**Don’t:**
- User: “Update test A” → You change tests A, B, and C.
- User: “Fix this bug” → You refactor the whole module.
- User edits your change → You revert their edit.

**Do:**
- User: “Update test A” → You update only test A.
- User: “Fix this bug” → You fix only that bug.
- User edits your change → You acknowledge and leave it as they want.

## Remember

The user knows the codebase best. Your role is to help with their specific requests, not to decide what else to change.
