package com.github.rickmvi.jtoolbox.config.annotations;

import java.lang.annotation.*;

/**
 * Marca um valor para descriptografia automática.
 *
 * <pre>
 * @Env("DB_PASS")
 * @Decrypt(algorithm = "AES")
 * String password;
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface Decrypt {
    /**
     * Algoritmo de criptografia (AES, RSA, etc)
     */
    String algorithm() default "AES";

    /**
     * Classe customizada de descriptografia
     */
    Class<? extends Decryptor> value() default DefaultDecryptor.class;

    /**
     * Interface para implementação customizada
     */
    interface Decryptor {
        String decrypt(String encrypted, String algorithm);
    }

    /**
     * Implementação padrão (placeholder)
     */
    class DefaultDecryptor implements Decryptor {
        @Override
        public String decrypt(String encrypted, String algorithm) {
            // Implementação básica - deve ser sobrescrita
            return encrypted;
        }
    }
}