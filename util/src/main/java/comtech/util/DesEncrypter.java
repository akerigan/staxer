package comtech.util;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.security.spec.InvalidKeySpecException;
import org.apache.commons.codec.binary.Base64;

/**
 * Класс реализующий работу с алгоритмом шифрования DES
 */
public class DesEncrypter {

    Cipher ecipher1;
    Cipher ecipher2;
    Cipher dcipher1;
    Cipher dcipher2;

    /**
     * Конструктор

     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     */
    public DesEncrypter() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException {
        byte[] theKey1 = "IS2009-SIRENA-TRAVEL".getBytes();
        byte[] theKey2 = "KOMTEH-N-2009-IS".getBytes();
        KeySpec ks1 = new DESKeySpec(theKey1);
        KeySpec ks2 = new DESKeySpec(theKey2);
        SecretKeyFactory kf = SecretKeyFactory.getInstance("DES");
        SecretKey key1 = kf.generateSecret(ks1);
        SecretKey key2 = kf.generateSecret(ks2);
        ecipher1 = Cipher.getInstance("DES");
        dcipher1 = Cipher.getInstance("DES");
        ecipher1.init(Cipher.ENCRYPT_MODE, key1);
        dcipher1.init(Cipher.DECRYPT_MODE, key1);
        ecipher2 = Cipher.getInstance("DES");
        dcipher2 = Cipher.getInstance("DES");
        ecipher2.init(Cipher.ENCRYPT_MODE, key2);
        dcipher2.init(Cipher.DECRYPT_MODE, key2);
    }

    /**
     * Функция шифрования
     * @param str строка открытого текста
     * @return зашифрованная строка в формате Base64
     */
    public String encrypt1(String str) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        byte[] utf8 = str.getBytes("UTF8");
        byte[] enc = ecipher1.doFinal(utf8);
        return new String(Base64.encodeBase64(enc),"UTF8");
    }

    /**
     * Функция шифровнаия
     * @param str строка открытого текста
     * @return зашифрованная строка в формате Base64
     */
    public String encrypt2(String str) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        byte[] utf8 = str.getBytes("UTF8");
        byte[] enc = ecipher2.doFinal(utf8);
        return new String(Base64.encodeBase64(enc),"UTF8");
    }

    /**
     * Функция расшифрования
     * @param str зашифрованная строка в формате Base64
     * @return расшифрованная строка
     */
    public String decrypt1(String str) throws IOException, IllegalBlockSizeException, BadPaddingException {
        byte[] dec = Base64.decodeBase64(str.getBytes("UTF8"));
        byte[] utf8 = dcipher1.doFinal(dec);
        return new String(utf8, "UTF8");
    }

    /**
     * Функция расшифрования
     * @param str зашифрованная строка в формате Base64
     * @return расшифрованная строка
     */
    public String decrypt2(String str) throws IOException, IllegalBlockSizeException, BadPaddingException {
        byte[] dec = Base64.decodeBase64(str.getBytes("UTF8"));
        byte[] utf8 = dcipher2.doFinal(dec);
        return new String(utf8, "UTF8");
    }

    public String encryptIS2009(int dynamicId)
            throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException {

        return encrypt1(encrypt2(encrypt1(dynamicId + "")));
    }

    public String encryptIS2009(String dynamicId)
            throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException {
        if (StringUtils.isEmpty(dynamicId)){
            return null;
        }
        return encrypt1(encrypt2(encrypt1(dynamicId)));
    }

    public int decryptIS2009(String str)
            throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException {
        String decStr = decrypt1(decrypt2(decrypt1(str)));
        //decStr = decStr.replaceAll("IS","");
        return Integer.parseInt(decStr);
    }

    public String decryptIS2009String(String str)
            throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException {
        if (StringUtils.isEmpty(str)){
            return null;
        }
        String decStr = decrypt1(decrypt2(decrypt1(str)));
        //decStr = decStr.replaceAll("IS","");
        return decStr;
    }
}
