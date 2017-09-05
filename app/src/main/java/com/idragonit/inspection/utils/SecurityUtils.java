package com.idragonit.inspection.utils;

import android.util.Base64;

import com.loopj.android.http.MySSLSocketFactory;

import java.security.KeyStore;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by CJH on 3/21/2016.
 */
public class SecurityUtils {

    private static final String algorithm = "Blowfish";
    static final String ENCRYPT_KEY = "_inspection_e3_sciences_";

    public static String encodeKey(String key) {
        Random random = new Random();
        String k = key + random.nextInt(10) + random.nextInt(10) + ENCRYPT_KEY + random.nextInt(100);
        return Base64.encodeToString(k.getBytes(), Base64.DEFAULT);
    }

    public static String decodeKey(String key) {
        String k = new String(Base64.decode(key, Base64.DEFAULT));
        return k.substring(0, k.indexOf(ENCRYPT_KEY)-2);
    }


    public String encrypt(String data, String key) {
        String retVal = null;

        try {
            SecretKeySpec skeySpec = getSecretKeySpec(key);

            // Instantiate the cipher.
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

            //byte[] encrypted = cipher.doFinal( URLEncoder.encode(data).getBytes() );
            byte[] encrypted = cipher.doFinal(data.getBytes());
            retVal = new String(encrypted);
        } catch (Exception ex) {
            System.out.println("Exception in CryptoUtil.encrypt():");
            ex.printStackTrace();
            retVal = null;
        } finally {
            return retVal;
        }
    }

    public String decrypt(String data, String key) {
        String retVal = null;

        try {
            SecretKeySpec skeySpec = getSecretKeySpec(key);

            // Instantiate the cipher.
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);

            //byte[] decrypted = cipher.doFinal( URLDecoder.decode(data).getBytes() );
            byte[] decrypted = cipher.doFinal(data.getBytes());
            retVal = new String(decrypted);
        } catch (Exception ex) {
            System.out.println("Exception in CryptoUtil.decrypt():");
            ex.printStackTrace();
            retVal = null;
        } finally {
            return retVal;
        }
    }

    private SecretKeySpec getSecretKeySpec(String key) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), algorithm);

        return skeySpec;
    }

    public static MySSLSocketFactory getSSLSocketFactory() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            MySSLSocketFactory socketFactory = new MySSLSocketFactory(trustStore);
            socketFactory.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            return socketFactory;
        } catch (Exception e){}

        return null;
    }
}
