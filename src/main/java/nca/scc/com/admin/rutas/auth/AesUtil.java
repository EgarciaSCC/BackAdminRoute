package nca.scc.com.admin.rutas.auth;

import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Descifrado AES-256-GCM para credenciales enviadas por el frontend.
 * Solo se usa si app.auth.aes-base64-key y app.auth.aes-base64-iv están configurados.
 */
public final class AesUtil {

    private static final Logger log = LoggerFactory.getLogger(AesUtil.class);


    private AesUtil() {}

    /**
     * Descifra un valor en Base64 (formato típico de cifrado en cliente).
     * @param base64Ciphertext texto cifrado en Base64
     * @param base64Key clave en Base64 (32 bytes para AES-256)
     * @param base64Iv IV en Base64 (12 bytes para GCM)
     * @return texto en claro o null si falla
     */
    @Nullable
    public static String decrypt(String base64Ciphertext, String base64Key, String base64Iv) {
        if (base64Ciphertext == null || base64Key == null || base64Iv == null) {
            return null;
        }
        try {
            byte[] key = Base64.getDecoder().decode(base64Key);
            byte[] iv = Base64.getDecoder().decode(base64Iv);
            byte[] ciphertext = Base64.getDecoder().decode(base64Ciphertext);

            if (key.length != 32 || iv.length != 12) {
                throw new IllegalStateException(
                        "AES-GCM requires 32-byte key and 12-byte IV"
                );
            }

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    new SecretKeySpec(key, "AES"),
                    new GCMParameterSpec(128, iv)
            );

            return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);

        } catch (Exception e) {
            log.error("AES decrypt failed", e);
            return null;
        }
    }
}
