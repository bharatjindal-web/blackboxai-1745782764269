package com.example.messagingapp;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {

    private static final String AES_MODE = "AES/CBC/PKCS5Padding";
    private static final int IV_SIZE = 16;

    public static String encrypt(String data, String password) throws Exception {
        byte[] clean = data.getBytes("UTF-8");

        // Generate key and IV
        SecretKeySpec key = generateKey(password);
        byte[] iv = generateIv();

        Cipher cipher = Cipher.getInstance(AES_MODE);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);

        byte[] encrypted = cipher.doFinal(clean);

        // Combine IV and encrypted part
        byte[] encryptedIvAndText = new byte[IV_SIZE + encrypted.length];
        System.arraycopy(iv, 0, encryptedIvAndText, 0, IV_SIZE);
        System.arraycopy(encrypted, 0, encryptedIvAndText, IV_SIZE, encrypted.length);

        return Base64.encodeToString(encryptedIvAndText, Base64.DEFAULT);
    }

    public static String decrypt(String encryptedData, String password) throws Exception {
        byte[] encryptedIvTextBytes = Base64.decode(encryptedData, Base64.DEFAULT);

        // Extract IV
        byte[] iv = new byte[IV_SIZE];
        System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.length);

        // Extract encrypted part
        int encryptedSize = encryptedIvTextBytes.length - IV_SIZE;
        byte[] encryptedBytes = new byte[encryptedSize];
        System.arraycopy(encryptedIvTextBytes, IV_SIZE, encryptedBytes, 0, encryptedSize);

        SecretKeySpec key = generateKey(password);

        Cipher cipher = Cipher.getInstance(AES_MODE);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);

        byte[] decrypted = cipher.doFinal(encryptedBytes);

        return new String(decrypted, "UTF-8");
    }

    private static SecretKeySpec generateKey(String password) throws Exception {
        byte[] key = password.getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        key = sha.digest(key);
        return new SecretKeySpec(key, "AES");
    }

    private static byte[] generateIv() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return iv;
    }
}
