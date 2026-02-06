package nca.scc.com.admin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Configuración de encriptación AES-256-GCM para datos sensibles
 * Campos encriptados: documento, teléfono, email, licencia
 */
@Configuration
public class EncryptionConfig {

    @Value("${app.encryption.key:dev-secret-key-32-bytes-long!!}")
    private String encryptionKey;

    @Value("${app.encryption.iv:dev-iv-16-bytes!}")
    private String encryptionIv;

    @Value("${app.encryption.algorithm:AES_256_GCM}")
    private String algorithm;


    /**
     * Bean para encriptación/desencriptación de datos sensibles
     */
    @Bean(name = "dataEncryptionService")
    public DataEncryptionService dataEncryptionService() {
        return new DataEncryptionService(encryptionKey, encryptionIv);
    }

    /**
     * Clase interna para manejar encriptación AES-256-GCM
     */
    public static class DataEncryptionService {
        private static final int GCM_NONCE_LENGTH = 12; // 96 bits
        private static final int GCM_TAG_LENGTH = 128; // 128 bits
        private static final String ALGORITHM = "AES/GCM/NoPadding";

        private final SecretKey secretKey;
        private final byte[] iv;

        public DataEncryptionService(String keyString, String ivString) {
            try {
                // Procesar clave: si es corta, usar texto plano; si es larga, asumir Base64
                byte[] decodedKey;
                if (keyString.length() < 32) {
                    // Clave corta: usar texto plano y rellenar a 32 bytes
                    String paddedKey = padKey(keyString);
                    decodedKey = paddedKey.getBytes();
                } else {
                    // Clave larga: asumir Base64 y decodificar
                    try {
                        decodedKey = Base64.getDecoder().decode(keyString);
                    } catch (IllegalArgumentException e) {
                        // Si no es Base64 válido, usar como texto plano
                        decodedKey = keyString.getBytes();
                    }
                }
                this.secretKey = new SecretKeySpec(decodedKey, 0, Math.min(32, decodedKey.length), "AES");

                // Procesar IV
                byte[] decodedIv;
                if (ivString.length() < 16) {
                    // IV corto: usar texto plano y rellenar a 16 bytes
                    String paddedIv = padKey(ivString);
                    decodedIv = paddedIv.getBytes();
                } else {
                    // IV largo: asumir Base64 y decodificar
                    try {
                        decodedIv = Base64.getDecoder().decode(ivString);
                    } catch (IllegalArgumentException e) {
                        // Si no es Base64 válido, usar como texto plano
                        decodedIv = ivString.getBytes();
                    }
                }
                this.iv = decodedIv.length >= 12 ? decodedIv : padKey(ivString).getBytes();
            } catch (Exception e) {
                throw new RuntimeException("Error al inicializar clave de encriptación", e);
            }
        }

        /**
         * Encripta un string usando AES-256-GCM
         */
        public String encrypt(String plainText) {
            if (plainText == null || plainText.isEmpty()) {
                return plainText;
            }
            try {
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, generateNonce());
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

                byte[] cipherText = cipher.doFinal(plainText.getBytes());
                return Base64.getEncoder().encodeToString(cipherText);
            } catch (Exception e) {
                throw new RuntimeException("Error al encriptar datos", e);
            }
        }

        /**
         * Desencripta un string usando AES-256-GCM
         */
        public String decrypt(String encryptedText) {
            if (encryptedText == null || encryptedText.isEmpty()) {
                return encryptedText;
            }
            try {
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
                cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

                byte[] decodedCipherText = Base64.getDecoder().decode(encryptedText);
                byte[] plainText = cipher.doFinal(decodedCipherText);
                return new String(plainText);
            } catch (Exception e) {
                throw new RuntimeException("Error al desencriptar datos", e);
            }
        }

        /**
         * Genera un nonce aleatorio para GCM
         */
        private byte[] generateNonce() {
            byte[] nonce = new byte[GCM_NONCE_LENGTH];
            new SecureRandom().nextBytes(nonce);
            return nonce;
        }

        /**
         * Padroniza una clave corta a 32 bytes (256 bits)
         */
        private String padKey(String key) {
            while (key.length() < 32) {
                key += "=";
            }
            return key.substring(0, 32);
        }
    }
}
