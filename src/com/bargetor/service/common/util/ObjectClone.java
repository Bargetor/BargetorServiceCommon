package com.bargetor.service.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Clob;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * <p>description: ��Object������ȿ�¡</p>
 * <p>Date: Feb 3, 2012 11:38:01 AM</p>
 * <p>modify��</p>
 * @author: majin
 * @version: 1.0
 * </p>Company: ��������������������������ι�˾</p>
 * <p>��һ��javaBean���������ȿ�¡</p>
 * <p>���ȼ���Ϊ,������->��������->��������¡->���л���¡->��������ʵ�ֵ�clone����->�ݹ��¡->null</p>
 */
@SuppressWarnings("unchecked")
public class ObjectClone {
	/** ������ */
	private ObjectClone.cloneFilter filter;
	private String model;
	
    /**
     *<p>Title: deepClone</p>
     *<p>Description:���õݹ���öԶ����������¡</p>
     * @param @param obj
     * @param @return
     * @param @throws Exception �趨�ļ�
     * @return  Object ��������
     * @throws
    */
	
    public <T>T deepClone(T obj) throws Exception {
    	if(obj == null) return null;
    	//������
    	if(this.filter != null && !this.filter.filter(obj)){
    		if(this.model == null) return null;
    		if(ObjectClone.cloneFilter.IMPORT_FILTER_RETURN.equals(this.model))return obj;
    		return null;    		
    	}
    	//����ǻ������ͣ�ֱ�ӷ���
    	if(ReflectUtil.isBaseType(obj))return obj;
    	
    	//��������¡
    	ObjectCloneForSpecial cloneForSpecial = new ObjectCloneForSpecial();
    	T newObjForSpecial = cloneForSpecial.cloneForSpecial(obj);
    	if(newObjForSpecial != null) return newObjForSpecial;
    	
    	//�������л���¡
    	T newObjByStrem = cloneByStrem(obj);
    	if(newObjByStrem != null) return newObjByStrem;
    	
    	//��������ʵ�ֿ�¡����
    	Method cloneMethod = ReflectUtil.getMethod(obj.getClass(), "clone", new Class[]{});
    	//�������ʵ����Cloneable��clone����������ø÷���
    	if(cloneMethod != null && ReflectUtil.isInterfaceToAchieve(obj.getClass(), "Cloneable")){
    		T newObjectByClone = (T) cloneMethod.invoke(obj, new Object[]{});
    		return newObjectByClone;
    	}
    	
    	//һ��JavaBean�����¡
		// getDeclaredFields�õ�object�ڶ��������field
        Field[] fields = obj.getClass().getDeclaredFields();
		// ����newInstance����������һ���յ�Object
        T newObj = (T) obj.getClass().newInstance();
        for (int i = 0, j = fields.length; i < j; i++) {
			String propertyName = fields[i].getName();
			// field��ֵ
			Object propertyValue = ReflectUtil.getProperty(obj, propertyName);
			if (propertyValue != null) {
				// ���field����8�ֻ������ͣ�����String����ֱ�Ӹ�ֵ
				if (ReflectUtil.isBaseType(propertyValue)) {
					ReflectUtil.setProperty(newObj, propertyName, propertyValue);
				} else {
					// ���field����������Object��û��ʵ��clone��������ݹ��¡
					Object newPropObj = deepClone(propertyValue);
					ReflectUtil.setProperty(newObj, propertyName, newPropObj);
				}
			}
		}
        return newObj;
    }
    
    /**
     *<p>Title: deepClone</p>
     *<p>Description:������������ȿ�¡</p>
     * @param @param <T>
     * @param @param obj
     * @param @param filter
     * @param @return
     * @param @throws Exception �趨�ļ�
     * @return  T ��������
     * @throws
    */
    public <T>T deepClone(T obj,ObjectClone.cloneFilter filter) throws Exception{
    	this.filter = filter;
    	return deepClone(obj);
    }
    
    /**
     *<p>Title: deepClone</p>
     *<p>Description:��ģʽ��������������ȿ�¡</p>
     * @param @param <T>
     * @param @param obj
     * @param @param filter
     * @param @param model
     * @param @return
     * @param @throws Exception �趨�ļ�
     * @return  T ��������
     * @throws
    */
    public <T>T deepClone (T obj,String model,ObjectClone.cloneFilter filter) throws Exception{
    	this.model = model;
    	return deepClone(obj,filter);
    }

    /**
     *<p>Title: basicClone</p>
     *<p>Description:ֻ�Զ��������������¡��һ���¡</p>
     * @param @param <T>
     * @param @param obj
     * @param @return
     * @param @throws Exception �趨�ļ�
     * @return  T ��������
     * @throws
    */
    public <T>T basicClone(T obj) throws Exception{
    	if(obj == null) return null;
    	//������
    	if(this.filter != null && !this.filter.filter(obj)){
    		if(this.model == null) return null;
    		if(ObjectClone.cloneFilter.IMPORT_FILTER_RETURN.equals(this.model))return obj;
    		return null;    		
    	}
    	//����ǻ������ͣ�ֱ�ӷ���
    	if(ReflectUtil.isBaseType(obj))return obj;
    	
    	//��������¡
    	ObjectCloneForSpecial cloneForSpecial = new ObjectCloneForSpecial();
    	T newObjForSpecial = cloneForSpecial.cloneForSpecial(obj);
    	if(newObjForSpecial != null) return newObjForSpecial;
    	
    	//һ��JavaBean�����¡
		// getDeclaredFields�õ�object�ڶ��������field
        Field[] fields = obj.getClass().getDeclaredFields();
		// ����newInstance����������һ���յ�Object
        T newObj = (T) obj.getClass().newInstance();
        for (int i = 0, j = fields.length; i < j; i++) {
			String propertyName = fields[i].getName();
			// field��ֵ
			Object propertyValue = ReflectUtil.getProperty(obj, propertyName);
			if (propertyValue != null) {
				// ���field����8�ֻ������ͣ�����String����ֱ�Ӹ�ֵ
				if (ReflectUtil.isBaseType(propertyValue)) {
					ReflectUtil.setProperty(newObj, propertyName, propertyValue);
				} else {
					// ���field����������Object��û��ʵ��clone��������ݹ��¡
					Object newPropObj = basicClone(propertyValue);
					ReflectUtil.setProperty(newObj, propertyName, newPropObj);
				}
			}
		}
        return newObj;
    }
    
    /**
     *<p>Title: basicClone</p>
     *<p>Description:������������ȿ�¡</p>
     * @param @param <T>
     * @param @param obj
     * @param @param filter
     * @param @return
     * @param @throws Exception �趨�ļ�
     * @return  T ��������
     * @throws
    */
    public <T>T basicClone(T obj,ObjectClone.cloneFilter filter) throws Exception{
    	this.filter = filter;
    	return basicClone(obj);
    }
    
    /**
     *<p>Title: basicClone</p>
     *<p>Description:��ģʽ��������������ȿ�¡</p>
     * @param @param <T>
     * @param @param obj
     * @param @param filter
     * @param @param model
     * @param @return
     * @param @throws Exception �趨�ļ�
     * @return  T ��������
     * @throws
    */
    public <T>T basicClone (T obj,String model,ObjectClone.cloneFilter filter) throws Exception{
    	this.model = model;
    	return basicClone(obj,filter);
    }
    /**
     *<p>Title: clone</p>
     *<p>Description:����ǳ�ȿ�¡</p>
     * @param @param obj
     * @param @return
     * @param @throws Exception �趨�ļ�
     * @return  Object ��������
     * @throws
    */
    public <T>T clone(T obj) throws Exception {
    	if(ReflectUtil.isBaseType(obj)) return obj;
        Field[] fields = obj.getClass().getDeclaredFields();
        T newObj = (T) obj.getClass().newInstance();
        for (int i = 0, j = fields.length; i < j; i++) {
            String propertyName = fields[i].getName();
            Object propertyValue = ReflectUtil.getProperty(obj, propertyName);
            ReflectUtil.setProperty(newObj, propertyName, propertyValue);
        }
        return newObj;
    }
    
    /**
     *<p>Title: getterToSetter</p>
     *<p>Description:�����ݴ�getterת�Ƶ�setter,��Ϊ�������ݵ�����ȿ�¡</p>
     * @param @param <T>
     * @param @param getter
     * @param @param setter
     * @param @return �趨�ļ�
     * @return  T ��������
     * @throws Exception 
     * @throws
    */
    public <T,V>void getterToSetter(T getter,V setter) throws Exception{
    	if(getter == null || setter == null) return;
    	Field[] fields = getter.getClass().getDeclaredFields();
    	for(int i = 0,len = fields.length;i<len;i++){
    		Object thisFieldValue = ReflectUtil.getProperty(getter, fields[i].getName());
    		if(thisFieldValue == null)continue;
    		thisFieldValue = deepClone(thisFieldValue);
    		ReflectUtil.setProperty(setter, fields[i].getName(), thisFieldValue);
    	}
    }
    
    /**
     *<p>Title: getterToSetter</p>
     *<p>Description:�����ݴ�getterת�Ƶ�setter,ֻ����������ת��(����Date)</p>
     * @param @param <T>
     * @param @param getter
     * @param @param setter
     * @param @return �趨�ļ�
     * @return  T ��������
     * @throws Exception 
     * @throws
    */
    public static <T, V>void getterToSetterForBase(T getter,V setter){
    	if(getter == null || setter == null) return;
    	Field[] fields = getter.getClass().getDeclaredFields();
    	for(int i = 0,len = fields.length;i<len;i++){
    		Field field = fields[i];
    		Object thisFieldValue = ReflectUtil.getProperty(getter, field.getName());
    		if(thisFieldValue == null)continue;
    		if(!ReflectUtil.isBaseType(thisFieldValue) && !ReflectUtil.isDate(field))continue;
    		ReflectUtil.setProperty(setter, fields[i].getName(), thisFieldValue);
    	}
    }
    
    /**
     *<p>Title: getterToSetterByMethod</p>
     *<p>Description:�����ݴ�getterת�Ƶ�setter,��Ϊ�������ݵ�����ȿ�¡</p>
     * @param @param <T>
     * @param @param <V>
     * @param @param getter
     * @param @param setter
     * @param @throws Exception �趨�ļ�
     * @return  void ��������
     * @throws
    */
    public <T,V>void getterToSetterByMethod(T getter,V setter) throws Exception{
    	if(getter == null || setter == null) return;
    	Method[] methods = getter.getClass().getMethods();
    	for(Method method : methods){
    		if(method.getParameterTypes().length > 0) continue;
    		Method set = ReflectUtil.getMethod(setter.getClass(), "set"+method.getName().substring(3), new Class[]{method.getReturnType()});
    		if(set == null)continue;
    		Object thisFieldValue = method.invoke(getter, new Object[]{});
    		if(thisFieldValue==null)continue;
    		thisFieldValue = deepClone(thisFieldValue);
    		set.invoke(setter, thisFieldValue);
    	}
    }
      
    /**
     *<p>Title: cloneByStrem</p>
     *<p>Description:�Զ���������л���¡</p>
     * @param @param src
     * @param @return �趨�ļ�
     * @return  Object ��������
     * @throws
    */
	private <T>T cloneByStrem(T src) {
		if(src == null)return null;
		if(!ReflectUtil.isInterfaceToAchieve(src.getClass(), "Serializable"))return null;
        T dst = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(out);
            oo.writeObject(src);

            ByteArrayInputStream in = new ByteArrayInputStream(
                    out.toByteArray());
            ObjectInputStream oi = new ObjectInputStream(in);
            dst = (T) oi.readObject();
            return  dst;
        } catch (NotSerializableException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        } catch (IOException e) {
			return null;
		}
    }

    
/****************************************** ObjectCloneForSpecial ********************************************************/
    /**
     * <p>description: �������Ŀ�¡</p>
     * <p>Date: Feb 3, 2012 2:01:51 PM</p>
     * <p>modify��</p>
     * @author: majin
     * @version: 1.0
     * </p>Company: ��������������������������ι�˾</p>
     */
    private class ObjectCloneForSpecial {
    	
    	/**
    	 *<p>Title: cloneForMap</p>
    	 *<p>Description:��Map���п�¡</p>
    	 * @param @param <T>
    	 * @param @param <V>
    	 * @param @param map
    	 * @param @return
    	 * @param @throws Exception �趨�ļ�
    	 * @return  Map<T,V> ��������
    	 * @throws
    	*/
    	public <T,V>Map<T,V> cloneForMap(Map<T,V> map) throws Exception{
    		if(map == null) return null;
    		Map<T,V> newMap = map.getClass().newInstance();
    		if(map.isEmpty()) return newMap;
    		Set<T> keySet = map.keySet();
    		for(T key:keySet){
    			T newKey = (T) deepClone(key);
    			V newValue = (V) deepClone(map.get(key));
    			newMap.put(newKey, newValue);
    		}
    		return newMap;
    	}
    	
    	/**
    	 *<p>Title: cloneForCollection</p>
    	 *<p>Description:�Լ��Ͻ��п�¡</p>
    	 * @param @param <T>
    	 * @param @param collection
    	 * @param @return
    	 * @param @throws Exception �趨�ļ�
    	 * @return  Collection ��������
    	 * @throws
    	*/
    	public <T>Collection<T> cloneForCollection (Collection<T> collection) throws Exception{
    		if(collection == null) return null;
    		Collection<T> newCollection = (Collection<T>) collection.getClass().newInstance();
    		 for(T obj:collection){
    			 newCollection.add((T) deepClone(obj));
    		 }
    		return newCollection;
    	}
    	
    	/**
    	 *<p>Title: cloneForClob</p>
    	 *<p>Description:��CLOB���п�¡</p>
    	 * @param @param clob
    	 * @param @return
    	 * @param @throws Exception �趨�ļ�
    	 * @return  Clob ��������
    	 * @throws
    	*/
    	public Clob cloneForClob(Clob clob) throws Exception{
    		return clob;
    	}
    	
    	/**
    	 *<p>Title: cloneForClob</p>
    	 *<p>Description:��CLOB���п�¡</p>
    	 * @param @param clob
    	 * @param @return
    	 * @param @throws Exception �趨�ļ�
    	 * @return  Clob ��������
    	 * @throws
    	*/
    	public Blob cloneForBlob(Blob blob) throws Exception{
    		return blob;
    	}
    	
    	/**
    	 *<p>Title: cloneForSpecial</p>
    	 *<p>Description:�������Ϳ�¡</p>
    	 *<p>List,Map,Set,Stack��</p>
    	 * @param @param <T>
    	 * @param @param obj
    	 * @param @return
    	 * @param @throws Exception �趨�ļ�
    	 * @return  T ��������
    	 * @throws
    	*/
    	public <T>T cloneForSpecial(T obj) throws Exception{
    		if(ReflectUtil.isInterfaceToAchieve(obj.getClass(), "Collection"))return (T) cloneForCollection((Collection<?>) obj);
    		if(ReflectUtil.isInterfaceToAchieve(obj.getClass(), "Map"))return (T)cloneForMap((Map<?,?>) obj);
    		if(ReflectUtil.isInterfaceToAchieve(obj.getClass(), "Clob"))return (T)cloneForClob((Clob) obj);
    		if(ReflectUtil.isInterfaceToAchieve(obj.getClass(), "Blob"))return (T)cloneForBlob((Blob) obj);
    		return null;
    	}
    
    }
    
    /**
     * <p>description: �������ӿ�,������,Ϊ���¡,Ϊ���޶���</p>
     * <p>Date: Feb 8, 2012 2:44:08 PM</p>
     * <p>modify��</p>
     * @author: majin
     * @version: 1.0
     * </p>Company: ��������������������������ι�˾</p>
     */
    public interface cloneFilter {
    	/** ����ʧ�ܺ��޶��� */
    	public final static String NULL_FILTER_RETURN = "0";
    	/** ����ʧ�ܺ󷵻ص�ǰ��¡�������� */
    	public final static String IMPORT_FILTER_RETURN = "1";
    	/**
    	 *<p>Title: filter</p>
    	 *<p>Description:������,Ϊ���¡,Ϊ���޶���</p>
    	 * @param @param <T>
    	 * @param @param obj
    	 * @param @return �趨�ļ�
    	 * @return  boolean ��������
    	 * @throws
    	*/    	
    	public <T>boolean filter(T obj);
    }
    
}
    
    
