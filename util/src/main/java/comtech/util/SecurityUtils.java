package comtech.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author Vlad Vinichenko (akerigan@gmail.com)
 * @since 2011-01-26 13:22 (Europe/Moscow)
 */
public class SecurityUtils {

    private final static String SYMMETRICAL_ALGORITHM = "DES";
    private static final String ASYMMETRICAL_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
    private static final int SIGNATURE_LENGTH = 128;

    public static String passwordHash(
            String password
    ) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return passwordHash(password, null);
    }

    public static String passwordHash(
            String password, String salt
    ) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md;
        md = MessageDigest.getInstance("SHA-1");
        md.update(password.getBytes("utf-8"));
        if (salt != null) {
            md.update(salt.getBytes("utf-8"));
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

    public static String getSwcPasswordHash(String plainTextPassword) throws NoSuchAlgorithmException {
        return getMD5_EncodedBase64(
                "IS09STKN" + getMD5_EncodedBase64(plainTextPassword) + "VSKIKNMISTKA"
        );
    }

    public static byte[] encryptSymmetrically(
            byte[] bytes, SecretKey key
    ) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
             IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(SYMMETRICAL_ALGORITHM);
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

    public static byte[] decryptSymmetrically(
            byte[] bytes, SecretKey key
    ) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
             IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(SYMMETRICAL_ALGORITHM);
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
