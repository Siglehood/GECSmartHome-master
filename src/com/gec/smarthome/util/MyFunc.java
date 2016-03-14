package com.gec.smarthome.util;

import java.util.Locale;

/**
 * 数据转换工具
 * 
 * @author Sig
 * @version 1.1
 */
public class MyFunc {

	/**
	 * 判断奇数或偶数，位运算，最后一位是1则为奇数，0为偶数
	 * 
	 * @param num
	 * @return
	 */
	public static int isOdd(int num) {
		return num & 0x1;
	}

	/**
	 * hex字符串转int
	 * 
	 * @param inHex
	 * @return
	 */
	public static int hex2Int(String inHex) {
		return Integer.parseInt(inHex, 16);
	}

	/**
	 * hex字符串转byte
	 * 
	 * @param inHex
	 * @return
	 */
	public static byte hex2Byte(String inHex) {
		return (byte) Integer.parseInt(inHex, 16);
	}

	/**
	 * 1个字节转2个hex字符
	 * 
	 * @param inByte
	 * @return
	 */
	public static String byte2Hex(Byte inByte) {
		return String.format("%02x", inByte).toUpperCase(Locale.getDefault());
	}

	/**
	 * 字节数组转hex字符串
	 * 
	 * @param inByteArr
	 * @return
	 */
	public static String byteArr2Hex(byte[] inByteArr) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0, len = inByteArr.length; i < len; i++) {
			sb.append(byte2Hex(inByteArr[i]) + " ");
		}
		return sb.toString();
	}

	/**
	 * 字节数组转转hex字符串，可选长度
	 * 
	 * @param inByteArr
	 * @param offset
	 * @param byteCount
	 * @return
	 */
	public static String byteArr2Hex(byte[] inByteArr, int offset, int byteCount) {
		StringBuilder sb = new StringBuilder();
		for (int i = offset, len = byteCount; i < len; i++) {
			sb.append(byte2Hex(inByteArr[i]) + " ");
		}
		return sb.toString();
	}

	/**
	 * hex字符串转字节数组
	 * 
	 * @param inHex
	 * @return
	 */
	public static byte[] hex2ByteArr(String inHex) {
		int len = inHex.length();
		byte[] result;
		if (isOdd(len) == 1) {// 奇数
			len++;
			result = new byte[(len / 2)];
			inHex = "0" + inHex;
		} else {// 偶数
			result = new byte[(len / 2)];
		}
		int j = 0;
		for (int i = 0; i < len; i += 2) {
			result[j] = hex2Byte(inHex.substring(i, i + 2));
			j++;
		}
		return result;
	}
}