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
    private static final String ALG = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;

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
        if (base64Ciphertext == null || base64Ciphertext.isBlank()
                || base64Key == null || base64Key.isBlank()
                || base64Iv == null || base64Iv.isBlank()) {
            return null;
        }
        try {
            byte[] key = Base64.getDecoder().decode(base64Key.trim());
            byte[] iv = Base64.getDecoder().decode(base64Iv.trim());
            byte[] ciphertext = Base64.getDecoder().decode(base64Ciphertext.trim());
            if (key.length != 32 || iv.length != 12) {
                log.warn("AES: key must be 32 bytes, iv 12 bytes");
                return null;
            }
            SecretKeySpec spec = new SecretKeySpec(key, "AES");
            GCMParameterSpec gcm = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            Cipher cipher = Cipher.getInstance(ALG);
            cipher.init(Cipher.DECRYPT_MODE, spec, gcm);
            byte[] plain = cipher.doFinal(ciphertext);
            return new String(plain, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.debug("AES decrypt failed: {}", e.getMessage());
            return null;
        }
    }
}
