package org.staxer.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-01-26 13:22 (Europe/Moscow)
 */
public class SecurityUtils {

    private final static String SYMMETRICAL_ALGORITHM = "DES";
    private static final String ASYMMETRICAL_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
    private static final int SIGNATURE_LENGTH = 128;

    public static String sha1Hash(
            String s
    ) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return sha1Hash(s.getBytes("UTF-8"), null);
    }

    public static String sha1Hash(
            String s, String salt
    ) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return sha1Hash(s.getBytes("UTF-8"), salt.getBytes("UTF-8"));
    }

    public static String sha1Hash(
            byte[] bytes
    ) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return sha1Hash(bytes, null);
    }

    public static String sha1Hash(
            byte[] bytes, byte[] salt
    ) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md;
        md = MessageDigest.getInstance("SHA-1");
        md.update(bytes);
        if (salt != null) {
            md.update(salt);
        }
        return StringUtils.hexencode(md.digest());
    }

    public static String getBasicHttpAuth(String login, String password) throws UnsupportedEncodingException {
        return "Basic " + new String(Base64.encodeBase64((login + ":" + password).getBytes("US-ASCII")), "US-ASCII");
    }

    public static String getMD5_EncodedBase64(String source) throws NoSuchAlgorithmException {
        if (!StringUtils.isEmpty(source)) {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(source.getBytes());
            return new String(Base64.encodeBase64(algorithm.digest()));
        } else {
            return null;
        }
    }

    public static SecretKey createSecretKey(String password) {
        byte[] passwordBytes = password.getBytes();
        byte[] keyBytes;
        int length = passwordBytes.length;
        if (length > 16) {
            keyBytes = new byte[16];
            System.arraycopy(passwordBytes, 0, keyBytes, 0, 16);
        } else if (length < 16) {
            keyBytes = new byte[16];
            Arrays.fill(keyBytes, (byte) 123);
            System.arraycopy(passwordBytes, 0, keyBytes, 0, length);
        } else {
            keyBytes = passwordBytes;
        }
        return new SecretKeySpec(keyBytes, SYMMETRICAL_ALGORITHM);
    }

    public static Cipher createSymmetricCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
        return Cipher.getInstance(SYMMETRICAL_ALGORITHM);
    }

    public static String encryptSymmetrically(
            String text, String textEncoding, SecretKey secretKey
    ) throws Exception {
        byte[] encryptedData = SecurityUtils.encryptSymmetrically(
                text.getBytes(textEncoding), secretKey
        );
        return Base64.encodeBase64URLSafeString(encryptedData);
    }

    public static byte[] encryptSymmetrically(
            byte[] bytes, SecretKey key
    ) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        return encryptSymmetrically(bytes, key, createSymmetricCipher());
    }

    public static byte[] encryptSymmetrically(
            byte[] bytes, SecretKey key, Cipher cipher
    ) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(bytes);
    }

    public static byte[] encryptAsymmetrically(
            byte[] bytes, PrivateKey key
    ) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(ASYMMETRICAL_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(bytes);
    }

    public static byte[] encryptAsymmetrically(
            byte[] bytes, PublicKey key
    ) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(ASYMMETRICAL_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(bytes);
    }

    public static String decryptSymmetrically(
            String base64Text, String base64TextEncoding, SecretKey secretKey
    ) throws Exception {
        byte[] decryptedData = SecurityUtils.decryptSymmetrically(
                Base64.decodeBase64(base64Text), secretKey
        );
        return new String(decryptedData, base64TextEncoding);
    }

    public static byte[] decryptSymmetrically(
            byte[] bytes, SecretKey key
    ) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        return decryptSymmetrically(bytes, key, createSymmetricCipher());
    }

    public static byte[] decryptSymmetrically(
            byte[] bytes, SecretKey key, Cipher cipher
    ) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(bytes);
    }

    public static byte[] decryptAsymmetrically(
            byte[] bytes, PrivateKey key
    ) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(ASYMMETRICAL_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(bytes);
    }

    public static byte[] decryptAsymmetrically(
            byte[] bytes, PublicKey key
    ) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(ASYMMETRICAL_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(bytes);
    }

    public static SecretKey generateSymmetricalKey() throws NoSuchAlgorithmException {
        return KeyGenerator.getInstance(SYMMETRICAL_ALGORITHM).generateKey();
    }

    public static SecretKey parseSymmetricalKey(
            byte[] bytes
    ) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException {
        DESKeySpec spec = new DESKeySpec(bytes);
        return SecretKeyFactory.getInstance(SYMMETRICAL_ALGORITHM).generateSecret(spec);
    }

    public static PublicKey parsePublicKey(byte[] bytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return KeyFactory.getInstance(ASYMMETRICAL_ALGORITHM).generatePublic(new X509EncodedKeySpec(bytes));
    }

    public static byte[] createSignature(
            byte[] data, PrivateKey key
    ) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(key);
        signature.update(data);
        return signature.sign();
    }

    public static boolean checkSignature(
            byte[] data, byte[] signatureBytes, PublicKey key
    ) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        if (key != null) {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(key);
            signature.update(data);
            return signature.verify(signatureBytes);
        } else {
            return false;
        }
    }

    public static int getSignatureLength() {
        return SIGNATURE_LENGTH;
    }

}
