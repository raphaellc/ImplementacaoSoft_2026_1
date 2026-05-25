package com.gerenciadortarefas.auth.service;

import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;

import java.util.Base64;

/**
 * Wrapper do samstevens/totp:
 *  - Gera secret base32 (160 bits)
 *  - Gera QR Code PNG em Data URI (sem dependência JS no frontend)
 *  - Verifica códigos com janela de ±1 step (tolerância 30s/60s)
 */
public class TotpService {

    private static final String ISSUER = "GerenciadorTarefas";

    private final SecretGenerator secretGen = new DefaultSecretGenerator();
    private final CodeVerifier verifier;

    public TotpService() {
        CodeGenerator codeGen = new DefaultCodeGenerator();
        this.verifier = new DefaultCodeVerifier(codeGen, new SystemTimeProvider());
        ((DefaultCodeVerifier) this.verifier).setAllowedTimePeriodDiscrepancy(1);
    }

    public String novoSecret() {
        return secretGen.generate();
    }

    public String qrCodeDataUri(String secretBase32, String emailUsuario) {
        QrData data = new QrData.Builder()
                .label(emailUsuario)
                .secret(secretBase32)
                .issuer(ISSUER)
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();
        try {
            byte[] png = new ZxingPngQrGenerator().generate(data);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(png);
        } catch (QrGenerationException e) {
            throw new RuntimeException("Falha ao gerar QR Code", e);
        }
    }

    public boolean verificar(String secretBase32, String codigoDigitado) {
        if (secretBase32 == null || codigoDigitado == null) return false;
        return verifier.isValidCode(secretBase32, codigoDigitado.trim());
    }
}
