  package com.ssafy.musoonzup.global.util;

  import javax.crypto.Cipher;
  import javax.crypto.spec.SecretKeySpec;
  import java.util.Base64;

  public class JwtAesUtil {
    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY =  "abcdefghijklmnopqrstuvwx12345678"; // 32bytes for AES-256

    public static String encrypt(String data) throws Exception {
      SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, key);
      byte[] encrypted = cipher.doFinal(data.getBytes());
      return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String encryptedData) throws Exception {
      SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, key);
      byte[] decoded = Base64.getDecoder().decode(encryptedData);
      byte[] decrypted = cipher.doFinal(decoded);
      return new String(decrypted);
    }
  }

