package com.example.administrator.goldaappnew.utils;

import java.io.ByteArrayOutputStream;
import java.util.zip.Inflater;

public class StringUtil {

    public static String encodeStr(byte[] paramArrayOfByte){
        byte[] unCompressed = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream(paramArrayOfByte.length);
        Inflater decompressor = new Inflater();
        try {
            decompressor.setInput(paramArrayOfByte);
            final byte[] buf = new byte[1024];
            while (!decompressor.finished()) {
                int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            }
            unCompressed = bos.toByteArray();
            bos.close();
            return new String(unCompressed, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            decompressor.end();
        }
        // String test = bos.toString();
        // return unCompressed;
        return null;
    }

    //	public java.lang.String decompress(byte[] paramArrayOfByte){
//
//		byte[] unCompressed = null;
//        ByteArrayOutputStream bos = new ByteArrayOutputStream(paramArrayOfByte.length);
//        Inflater decompressor = new Inflater();
//        try {
//            decompressor.setInput(paramArrayOfByte);
//            final byte[] buf = new byte[1024];
//            while (!decompressor.finished()) {
//                int count = decompressor.inflate(buf);
//                bos.write(buf, 0, count);
//            }
//
//            unCompressed = bos.toByteArray();
//            bos.close();
//
//            return new String(unCompressed, "utf-8");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        finally {
//            decompressor.end();
//        }
//        // String test = bos.toString();
//        // return unCompressed;
//        return null;
//
//	}


    public static String notEmpty(String src) {
        return (src == null || src == "null") ? "" : src.toString();
    }

    /**
     * 检查指定字符串是否为空或长度为0。
     *
     * @param str
     *            需要检查的字符串
     * @return 字符串为空或长度为0，返回true，否则返回false。
     */
    public static boolean isEmpty(String str) {

        if (str == null)
            return true;

        return str.length() == 0 ? true : false;
    }
}
