package com.minxing.integral.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AesHelper {

	protected static Logger logger = LoggerFactory.getLogger(AesHelper.class);

	static String secret_type = "AES/CBC/PKCS5Padding";

	/**
	 * 加解密
	 *
	 * @param data
	 * @param mode 加密：Cipher.ENCRYPT_MODE  解密：Cipher.DECRYPT_MODE
	 * @return
	 */
	public static String doAES(String data, int mode) {
		try {
			//判断是加密还是解密
			boolean encrypt = mode == Cipher.ENCRYPT_MODE;
			byte[] content;
			//true 加密内容 false 解密内容
			if (encrypt) {
				content = data.getBytes("UTF-8");
			} else {
				content = hexStr2Byte(data);
			}
			//1.构造密钥生成器，指定为AES算法,不区分大小写
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			//2.根据ecnodeRules规则初始化密钥生成器
			//生成一个128位的随机源,根据传入的字节数组
			kgen.init(128, new SecureRandom(secret_type.getBytes()));
			//3.产生原始对称密钥
			SecretKey secretKey = kgen.generateKey();
			//4.获得原始对称密钥的字节数组
			byte[] enCodeFormat = secretKey.getEncoded();
			//5.根据字节数组生成AES密钥
			SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat, "AES");
			//6.根据指定算法AES自成密码器
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			//7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
			cipher.init(mode, keySpec);// 初始化
			byte[] result = cipher.doFinal(content);
			if (encrypt) {
				//将二进制转换成16进制
				return byte2HexStr(result);
			} else {
				return new String(result, "UTF-8");
			}
		} catch (Exception e) {
			logger.error("AES 密文处理异常", e);
		}
		return null;
	}

	// 解密
	public static String decrypt(byte[] sSrc, String key, String iv) {
		try {
			// 判断Key是否正确
			if (key == null) {
				key = "c351bfd05dab20d7";
			}
			if (iv == null) {
				iv = "a0fe7c7c98e09e8c";
			}
			byte[] raw = key.getBytes();
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance(secret_type);
			IvParameterSpec ivp = new IvParameterSpec(iv.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivp);

			try {
				byte[] original = cipher.doFinal(sSrc);
				String originalString = new String(original);
				return originalString;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * 将二进制转换成16进制
	 *
	 * @param buf
	 * @return
	 */
	public static String byte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * 将16进制转换为二进制
	 *
	 * @param hexStr
	 * @return
	 */
	public static byte[] hexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
					16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

	public static String base64decode(String origin) {
		byte[] b = null;
		try {
			b = replaceBlank(origin).getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Decoder base64 = Base64.getDecoder();
		b = base64.decode(b);
		String s = new String(b);
		return s;
	}

	public static byte[] base64decodeByte(String origin) {
		byte[] b = null;
		try {
			b = replaceBlank(origin).getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Decoder base64 = Base64.getDecoder();
		b = base64.decode(b);
		return b;
	}

	public static String licenseDecode(String secret){
		String s = base64decode(secret);
		byte[] b = hexStr2Byte(s);
		return decrypt(b, (String)null, (String)null);
	}

	public static String replaceBlank(String str) {
		String dest = "";
		if (str!=null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	public static String SHA1(String decript) {
		try {
			MessageDigest digest = MessageDigest
					.getInstance("SHA-1");
			digest.update(decript.getBytes());
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static byte[] SHA256(String decript) {
		try {
			MessageDigest digest = MessageDigest
					.getInstance("SHA-256");
			digest.update(decript.getBytes());
			byte messageDigest[] = digest.digest();
			return messageDigest;
			// Create Hex String
//			StringBuffer hexString = new StringBuffer();
//			// 字节数组转换为 十六进制 数
//			for (int i = 0; i < messageDigest.length; i++) {
//				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
//				if (shaHex.length() < 2) {
//					hexString.append(0);
//				}
//				hexString.append(shaHex);
//			}
//			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}
}
