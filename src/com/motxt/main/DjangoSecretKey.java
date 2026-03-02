package com.motxt.main;

import java.security.SecureRandom;

public class DjangoSecretKey {
	private static final String RANDOM_STRING_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	public String getRandomSecretKey() {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*(-_=+)";
        return getRandomString(50, chars);
    }

    /**
     * Return a securely generated random string.
     */
    public String getRandomString(Integer length, String allowedChars) {
        // Fallback to default characters if none are provided
        if (allowedChars == null || allowedChars.isEmpty()) {
            allowedChars = RANDOM_STRING_CHARS;
        }
        
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = SECURE_RANDOM.nextInt(allowedChars.length());
            sb.append(allowedChars.charAt(randomIndex));
        }
        
        return sb.toString();
    }
}
