package cn.innovativest.ath.utils;


import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

/**
 * AES加解密
 * <p>
 * Created by yyh on 2015/10/9.
 */
public class AESUtils {

    /**
     * 密钥算法
     */
    private static final String ALGORITHM = "AES";
    /**
     * 加解密算法/工作模式/填充方式
     */
    private static final String ALGORITHM_STR = "AES/ECB/PKCS5Padding";

    private static final String hexKey = "sgg45747ss223455";


    /**
     * SecretKeySpec类是KeySpec接口的实现类,用于构建秘密密钥规范
     */
    private static SecretKeySpec key = new SecretKeySpec(hexKey.getBytes(), ALGORITHM);

    /**
     * AES加密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static String encryptData(String data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_STR); // 创建密码器
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            return new BASE64Encoder().encodeBuffer(cipher.doFinal(data.getBytes())).replace("\n", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * AES解密
     *
     * @param base64Data
     * @return
     * @throws Exception
     */
    public static String decryptData(String base64Data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_STR);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(new BASE64Decoder().decodeBuffer(base64Data)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String floatToStringByTruncate(double num, int remainBitNum) {
        String numStr = Double.toString(num);
        BigDecimal bd = new BigDecimal(numStr);
        bd = bd.setScale(remainBitNum,BigDecimal.ROUND_DOWN);
        return bd.toString();
    }


    public static void main(String[] args) throws Exception {
        String string = "1547000492";
        System.out.println("original data:  " + string);
        System.out.println("encrypt data:   " + AESUtils.encryptData(string)); // 加密
        String enStr = AESUtils.encryptData(string);
        System.out.println("decrypt result:   " + AESUtils.decryptData("ql5YWJ7nEkNAIYEx4Sf1GSqyMKSKaByNB7uiBX1UCb+vLxpFZi3Vzt8puqu3agAaatrkyGGdGJ97kOPqs1SMttZ/f/p8ad/scUOeqUqVeaQ=")); // 解密

        System.out.println(HeaderUtil.generateRandomByScope(10, 10));

//        System.out.print(floatToStringByTruncate(8.05/8.02,6)+"sdds");


    }
}
