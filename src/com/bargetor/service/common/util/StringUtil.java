package com.bargetor.service.common.util;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

/**
 * <p>description: �ַ�������</p>
 * <p>Date: 2012-6-20 ����02:43:19</p>
 * <p>modify��</p>
 * @author: majin
 * @version: 1.0
 */
public class StringUtil {
	
	public static String getUUID(){ 
        String s = UUID.randomUUID().toString(); 
        //ȥ����-������ 
        return s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24); 
    }
	
	/**
	 *<p>Title: isNullStr</p>
	 *<p>Description:�ַ����Ƿ�Ϊ��</p>
	 * @param @param str
	 * @param @return 
	 * @return  boolean 
	 * @throws
	*/
	public static boolean isNullStr(String str){
		if(str != null){
			return str.length() <= 0;
		}else{
			return true;
		}
	}
	
	/**
	 *<p>Title: isNullStr</p>
	 *<p>Description:�ַ����Ƿ�Ϊ��</p>
	 * @param @param str
	 * @param @return 
	 * @return  boolean 
	 * @throws
	*/
	public static boolean isNotNullStr(String str){
		return !isNullStr(str);
	}
	
	/**
	 *<p>Title: changeCharset</p>
	 *<p>Description:�ı��ַ�������</p>
	 * @param str
	 * @param charsetName
	 * @return
	 * @return String ��������
	 * @throws UnsupportedEncodingException 
	*/
	public static String changeCharset(String str,String charsetName) throws UnsupportedEncodingException{
		if(isNullStr(str) || isNullStr(charsetName))return str;
		return new String(str.getBytes(),charsetName);
	}
	
	/**
	 * joinList(�����ӷ������ַ�������)
	 * (����������������������� �C ��ѡ)
	 * @param list
	 * @param joinStr
	 * @return
	 *String
	 * @exception
	 * @since  1.0.0
	*/
	public static String joinList(List<String> list, String joinStr){
		if(list == null || list.size() <= 0)return "";
		if(isNullStr(joinStr))return "";
		StringBuffer buffer = new StringBuffer(list.get(0)); 
		for(int i = 1, len = list.size(); i < len; i++){
			buffer.append(joinStr);
			buffer.append(list.get(i));
		}
		return buffer.toString();
	}

}
