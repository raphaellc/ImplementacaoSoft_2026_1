package com.gerenciadortarefas.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Carrega variáveis de ambiente a partir de um arquivo .env.
 *
 * <p>O Java não lê arquivos .env automaticamente — variáveis de ambiente são
 * fornecidas pelo sistema operacional. Esta classe faz a ponte: lê o arquivo
 * .env e injeta as entradas no mapa de variáveis de ambiente da JVM em tempo
 * de execução, mas apenas para as variáveis que ainda não estiverem definidas
 * no ambiente real. Isso garante que variáveis do SO sempre têm prioridade
 * sobre o arquivo (comportamento padrão de ferramentas como dotenv).
 *
 * <p>Uso no main():
 * <pre>
 *     EnvLoader.load(); // deve ser a primeira linha do main()
 * </pre>
 *
 * <p>Formato esperado do arquivo .env:
 * <pre>
 *     # comentários são ignorados
 *     CHAVE=valor
 *     OUTRA_CHAVE=outro valor
 * </pre>
 */
public class EnvLoader {

    private static final String ENV_FILE = ".env";

    private EnvLoader() {}

    /**
     * Carrega o arquivo .env do diretório de trabalho atual.
     * Linhas em branco e comentários (iniciados por #) são ignorados.
     * Variáveis já definidas no ambiente do SO não são sobrescritas.
     */
    public static void load() {
        Path envPath = Paths.get(ENV_FILE);

        if (!Files.exists(envPath)) {
            System.out.println("[EnvLoader] Arquivo .env não encontrado — usando variáveis do SO.");
            return;
        }

        Map<String, String> entries = parse(envPath);
        if (entries.isEmpty()) {
            return;
        }

        inject(entries);
        System.out.println("[EnvLoader] " + entries.size() + " variável(is) carregada(s) do arquivo .env.");
    }

    // -------------------------------------------------------------------------
    // Leitura e parsing do arquivo
    // -------------------------------------------------------------------------

    private static Map<String, String> parse(Path path) {
        Map<String, String> entries = new HashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.strip();

                // Ignora linhas em branco e comentários
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                int separatorIndex = line.indexOf('=');
                if (separatorIndex <= 0) {
                    System.out.println("[EnvLoader] Linha " + lineNumber + " ignorada (formato inválido): " + line);
                    continue;
                }

                String key   = line.substring(0, separatorIndex).strip();
                String value = line.substring(separatorIndex + 1).strip();

                // Remove aspas simples ou duplas opcionais ao redor do valor
                value = stripQuotes(value);

                // Variáveis do SO têm prioridade — não sobrescreve
                if (System.getenv(key) == null) {
                    entries.put(key, value);
                }
            }
        } catch (IOException e) {
            System.out.println("[EnvLoader] Erro ao ler o arquivo .env: " + e.getMessage());
        }

        return entries;
    }

    private static String stripQuotes(String value) {
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last  = value.charAt(value.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }

    // -------------------------------------------------------------------------
    // Injeção no mapa de variáveis de ambiente da JVM
    // -------------------------------------------------------------------------

    /**
     * Injeta as entradas no mapa interno de variáveis de ambiente da JVM.
     *
     * <p>O mapa retornado por {@link System#getenv()} é imutável por design.
     * Para contornar isso em desenvolvimento, acessamos o campo privado via
     * reflexão — técnica válida apenas em ambiente local/estudo. Em produção,
     * variáveis devem ser definidas diretamente no SO ou no contêiner.
     */
    @SuppressWarnings("unchecked")
    private static void inject(Map<String, String> entries) {
        try {
            // Obtém o mapa interno de variáveis de ambiente da JVM
            Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
            Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
            theEnvironmentField.setAccessible(true);
            Map<Object, Object> internalEnv = (Map<Object, Object>) theEnvironmentField.get(null);

            // Obtém também o mapa case-insensitive (usado em algumas JVMs no Windows)
            Field theCaseInsensitiveEnvironmentField =
                    processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
            theCaseInsensitiveEnvironmentField.setAccessible(true);
            Map<String, String> ciEnv =
                    (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);

            for (Map.Entry<String, String> entry : entries.entrySet()) {
                internalEnv.put(entry.getKey(), entry.getValue());
                ciEnv.put(entry.getKey(), entry.getValue());
            }

        } catch (NoSuchFieldException e) {
            // Fallback para JVMs que não expõem theCaseInsensitiveEnvironment (Linux/macOS)
            injectUnix(entries);
        } catch (Exception e) {
            System.out.println("[EnvLoader] Não foi possível injetar variáveis: " + e.getMessage());
            System.out.println("[EnvLoader] Defina as variáveis diretamente no ambiente do SO.");
        }
    }

    @SuppressWarnings("unchecked")
    private static void injectUnix(Map<String, String> entries) {
        try {
            Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
            Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
            theEnvironmentField.setAccessible(true);
            Map<Object, Object> internalEnv = (Map<Object, Object>) theEnvironmentField.get(null);

            for (Map.Entry<String, String> entry : entries.entrySet()) {
                internalEnv.put(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            System.out.println("[EnvLoader] Fallback Unix também falhou: " + e.getMessage());
        }
    }
}
