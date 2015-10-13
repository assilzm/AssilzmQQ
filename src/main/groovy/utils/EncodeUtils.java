package utils;

import java.security.NoSuchAlgorithmException;

public class EncodeUtils {

//	public static void main(String[] args) {
//		System.out.println(encodePass("yourpass", "!CEY",
//				"\\x00\\x00\\x00\\x00\\x35\\x33\\x5f\\x58"));
////7CB19FDCA983484CC8A6463CF483A718
//	}

    /**
     * 腾讯对QQ密码的加密算法
     *
     * @param password 用法密码
     * @param vcode    返回的check码 或者 用户输入的验证码
     * @param uin      返回的QQ十六进制字节
     * @return
     */
    public static String encodePass(String password, String vcode, String uin) {
        byte[] uinByte = str2byte(uin);
        return md5(md5(connectBytes(md5b(password), uinByte))
                + vcode.toUpperCase());
    }

    /**
     * 把抓包返回的\\x00\\x00\\x00\\x00\\x35\\x33\\x5f\\x58转换为字节数组
     *
     * @param str
     * @return
     */
    public static byte[] str2byte(String str) {
        String[] strArr = str.split("\\\\x");
        byte[] uinByte = new byte[strArr.length - 1];
        if (strArr.length == 9) {
            for (int i = 1; i < strArr.length; i++) {
                uinByte[i - 1] = getByte(Integer.parseInt(strArr[i], 16));
            }
        }
        return uinByte;
    }

    public static byte[] md5b(byte[] source) {
        byte[] re = null;
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance("MD5");
            md.update(source);
            re = md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return re;
    }

    public static byte[] md5b(String str) {
        return md5b(str.getBytes());
    }

    public static String md5(byte[] source) {
        String s = null;
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte tmp[] = md5b(source);
            char str[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            s = new String(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s.toUpperCase();
    }

    public static String md5(String str) {
        return md5(str.getBytes()).toUpperCase();
    }

    /**
     * QQ提供的一个js方法，其实就是把一个md5的结果再还原成字节数组，这里的md5b可以直接返回字节数组，所以用不上了。
     *
     * @param str
     * @return
     */
    public static byte[] hexchar2bin(String str) {
        byte[] bytes = new byte[str.length() / 2];
        int j = 0;
        for (int i = 0; i < str.length(); i = i + 2) {
            int iv = Integer.parseInt(str.substring(i, i + 2), 16);
            bytes[j++] = getByte(iv);
        }
        return bytes;
    }

    /**
     * 转换无符号的数为有符号的字节值
     *
     * @param intValue
     * @return
     */
    public static byte getByte(int intValue) {
        int byteValue = 0;
        int temp = intValue % 256;
        if (intValue < 0) {
            byteValue = temp < -128 ? 256 + temp : temp;
        } else {
            byteValue = temp > 127 ? temp - 256 : temp;
        }
        return (byte) byteValue;
    }

    /**
     * 连接两个字节数组
     *
     * @param b1
     * @param b2
     * @return
     */
    public static byte[] connectBytes(byte[] b1, byte[] b2) {
        byte[] bs = new byte[b1.length + b2.length];
        int i = 0;
        for (i = 0; i < b1.length; i++) {
            bs[i] = b1[i];
        }
        for (int j = 0; j < b2.length; j++) {
            bs[i + j] = b2[j];
        }
        return bs;
    }
}