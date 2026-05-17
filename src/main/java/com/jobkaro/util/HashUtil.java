package com.jobkaro.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class HashUtil {

    private static final int SALT_LENGTH = 16;
    private static final String SEP = ":";

    public static String hashPassword(String plainText) {
        try {
            byte[] salt = new byte[SALT_LENGTH];
            new SecureRandom().nextBytes(salt);
            return buildHash(salt, plainText);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public static boolean verifyPassword(String plainText, String storedHash) {
        if (plainText == null || storedHash == null) return false;
        String[] parts = storedHash.split(SEP, 2);
        if (parts.length != 2) return false;
        try {
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            String expected = buildHash(salt, plainText);
            return timingSafeEquals(expected, storedHash);
        } catch (Exception e) {
            return false;
        }
    }

    private static String buildHash(byte[] salt, String plain) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        byte[] hash = md.digest(plain.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(salt) + SEP + Base64.getEncoder().encodeToString(hash);
    }

    private static boolean timingSafeEquals(String a, String b) {
        if (a.length() != b.length()) return false;
        int r = 0;
        for (int i = 0; i < a.length(); i++) r |= a.charAt(i) ^ b.charAt(i);
        return r == 0;
    }
}
