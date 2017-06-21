package pl.wipek.helpers;

import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Random;

/**
 * @author Created by Krzysztof Adamczyk on 14.04.2017.
 * Helper used to hashing String for example passwords
 */
public class Hasher {

    private final static String saltHash = "4jE9vsvRunv284sJu2SzLJkR4SRgDA/icyEX7hd4OS8=";

    /**
     * Method hash password in SHA512 algorithm with salt method
     * @author http://stackoverflow.com/users/5145530/abhi edited by Krzysztof Adamczyk
     * @param password
     * @return String in sha512
     */
    public static String hashSHA512(String password) {
        String hashedPassword = null;

        try{
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(saltHash.getBytes("UTF-8"));
            byte[] bytes = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            hashedPassword = sb.toString();

        } catch(Exception e) {
            Logger logger = LogManager.getRootLogger();
            logger.error(e);
        }

        return hashedPassword;
    }

    /**
     * Create 32 bytes String with random bytes end encoded it to Base64
     * @return Base64 encoded string
     */
    private static String salty() {
        final Random random = new SecureRandom();
        byte[] salt = new byte[32];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}
