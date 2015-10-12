package com.bargetor.service.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class ReflectUtil {
	
	/**
	 * getCollectionActualClass(��ȡ�������͵ķ�������)
	 * (����������������������� �C ��ѡ)
	 * @param collectionField
	 * @return
	 *Class<?>
	 * @exception
	 * @since  1.0.0
	*/
	public static Type getCollectionActualType(Field collectionField){
		if(!isCollection(collectionField.getType()))return null;
		Type[] types = getFieldActualTypeArguments(collectionField);
		if(types == null)return null;
		if(types.length != 1)return null;
		return types[0];
	}
	
	/**
	 * getCollectionActualType(��ȡ�������͵ķ�������)
	 * (����������������������� �C ��ѡ)
	 * @param collectionType
	 * @return
	 *Type
	 * @exception
	 * @since  1.0.0
	*/
	public static Type getCollectionActualType(Type collectionType){
		if(!isCollection(collectionType))return null;
		Type[] types = getTypeActualTypeArguments(collectionType);
		if(types == null)return null;
		if(types.length != 1)return null;
		return types[0];
	}
	
	/**
	 * isCollection(�ж������Ƿ�Ϊ���϶���)
	 * (����������������������� �C ��ѡ)
	 * @param type
	 * @return
	 *boolean
	 * @exception
	 * @since  1.0.0
	*/
	public static boolean isCollection(Type type){
		if(type instanceof Class){
			return isCollection((Class<?>)type);
		}else if(type instanceof ParameterizedType){
			return isCollection(((ParameterizedType)type).getRawType());
		}
		return false;
	}
	
	/**
	 * isCollection(�ж����Ƿ�Ϊ���϶���)
	 * (����������������������� �C ��ѡ)
	 * @param clazz
	 * @return
	 *boolean
	 * @exception
	 * @since  1.0.0
	*/
	public static boolean isCollection(Class<?> clazz){
		return Collection.class.isAssignableFrom(clazz);
	}
	
    /**
     *<p>Title: setProperty</p>
     *<p>Description:�������setter���������и�ֵ</p>
     * @param @param bean��Ҫ��ֵ��ʵ��
     * @param @param propertyName��������
     * @param @param value������ֵ
     * @param @return �趨�ļ�
     * @return  boolean ��������
     * @throws
    */
    public static boolean setProperty(Object bean, String propertyName,Object value) {
        Class<?> clazz = bean.getClass();
        try {
            Field field = getField(clazz, propertyName);
            if(field == null)return false;
            Method method = getSetterMethod(propertyName, clazz, new Class[] { field.getType() });
            if (method == null) {
				return false;
			} else {
				Object newValue = value;
				BaseType baseType = whichBaseType(field.getType().getName());
				if(baseType != null){
					newValue = baseType.valueOf(value);
				}
				method.invoke(bean, new Object[] { newValue } );
				return true;
			}
        } catch (Exception e) {
        	e.printStackTrace();
        	return false;
        }
    }

    /**
     *<p>Title: getProperty</p>
     *<p>Description:�������getter�������õ�field��ֵ</p>
     * @param @param bean
     * @param @param propertyName
     * @param @return �趨�ļ�
     * @return  Object ��������
     * @throws
    */
    public static Object getProperty(Object bean, String propertyName) { 
    	Class<?> clazz = bean.getClass();
        try {
            Method method = getGetterMethod(propertyName, clazz);
            return (method == null)?null:method.invoke(bean, new Object[] {});
        } catch (Exception e) {
        }
        return null;
    }
    
    /**
     *<p>Title: getSetterMethod</p>
     *<p>Description:��ȡSETTER����</p>
     * @param @param propertyName
     * @param @param clazz
     * @param @param paramClasses
     * @param @return �趨�ļ�
     * @return  Method ��������
     * @throws
    */
    public static Method getSetterMethod(String propertyName,Class<?> clazz,Class<?>[] paramClasses){
    	return getMethod(clazz, getSetterName(propertyName), paramClasses);
    }
    
    /**
     *<p>Title: getGetterMethod</p>
     *<p>Description:��ȡgetter����</p>
     * @param @param propertyName
     * @param @param clazz
     * @param @return �趨�ļ�
     * @return  Method ��������
     * @throws
    */
    public static Method getGetterMethod(String propertyName,Class<?> clazz){
    	return getMethod(clazz, getGetterName(propertyName), new Class[]{});
    }

    /**
     *<p>Title: getSetterName</p>
     *<p>Description:�õ�field��get����</p>
     * @param @param propertyName
     * @param @return �趨�ļ�
     * @return  String ��������
     * @throws
    */
    public static String getGetterName(String propertyName) {
        String method = "get" + propertyName.substring(0, 1).toUpperCase()+ propertyName.substring(1);
        return method;
    }

    /**
     *<p>Title: getSetterName</p>
     *<p>Description:�õ�field��set����</p>
     * @param @param propertyName
     * @param @return �趨�ļ�
     * @return  String ��������
     * @throws
    */
    public static String getSetterName(String propertyName) {
        String method = "set" + propertyName.substring(0, 1).toUpperCase()+ propertyName.substring(1);
        return method;
    }
    
    /**
     *<p>Title: getMethod</p>
     *<p>Description:���ݷ��������ط����������Ӹ��ڵ�̳�</p>
     * @param @param clazz ��
     * @param @param methodName ������
     * @param @param parameterClass ��������
     * @param @return �趨�ļ�
     * @return  Method ��������
     * @throws
    */
    public static Method getMethod(Class<?> clazz,String methodName,Class<?>[] parameterClass){
			Method method;
			if(clazz == null || clazz == Object.class)return null;
			try {
				method = clazz.getDeclaredMethod(methodName, parameterClass);
				return method;
			} catch (SecurityException e) {
				// ��������
				return null;
			} catch (NoSuchMethodException e) {
				//�޷��ҵ���Ӧ�ķ���
				return getMethod(clazz.getSuperclass(), methodName, parameterClass);
			}
    }
    
    /**
     *<p>Title: getMethodForName</p>
     *<p>Description:ֻ���ݷ������ƻ�ȡ�����������ǲ���</p>
     * @param @param clazz
     * @param @param methodName
     * @param @return �趨�ļ�
     * @return  Method ��������
     * @throws
    */
    public static Method getMethodForName(Class<?> clazz,String methodName){
    	for(Method method : clazz.getMethods()){
    		if(method.getName().equals(methodName))return method;
    	}
    	return null;
    }
    
    /**
     * getFieldActualTypeArguments(��ȡ���Եķ������ͼ�)
     * (����������������������� �C ��ѡ)
     * @param field
     * @return
     *Type[]
     * @exception
     * @since  1.0.0
    */
    public static Type[] getFieldActualTypeArguments(Field field){
    	if(field == null)return null;
    	Type t = field.getGenericType();
		return getTypeActualTypeArguments(t);
    }
    
    /**
     * getTypeActualTypeArguments(��ȡType�ķ������ͼ�)
     * (������͵����Ͳ����壬�磺T,V,K �ȵȣ���ȡ����Ҳ��T)
     * @param type
     * @return
     *Type[]
     * @exception
     * @since  1.0.0
    */
    public static Type[] getTypeActualTypeArguments(Type type){
    	if(type == null)return null;
		if (isParameterizedType(type)) {
			return ((ParameterizedType) type).getActualTypeArguments();
		}
		return null;
    }
    
    /**
     * isParameterizedType(�ж��Ƿ���)
     * (����������������������� �C ��ѡ)
     * @param type
     * @return
     *boolean
     * @exception
     * @since  1.0.0
    */
    public static boolean isParameterizedType(Type type){
    	if(type == null)return false;
		if (ParameterizedType.class.isAssignableFrom(type.getClass())) {
			return true;
		}
		return false;
    }
    
    /**
     *<p>Title: isInterfaceToAchieve</p>
     *<p>Description:������Ƿ�ʵ����ĳ���ӿ�</p>
     * @param @param clazz
     * @param @param interfaceName
     * @param @return �趨�ļ�
     * @return  boolean ��������
     * @throws
    */
    public static boolean isInterfaceToAchieve(Class<?> clazz,String interfaceName){
    	if(clazz == null) return false;
    	if(interfaceName == null) return false;
    	if("".equals(interfaceName))return false;
    	Class<?>[] theInterfaces = clazz.getInterfaces();
    	for(Class<?> c: theInterfaces){
    		String name = c.getName();
    		if(name.equals(interfaceName) || name.endsWith("."+interfaceName)||name.endsWith("$"+interfaceName))return true;
    	}
		if(isInterfaceToAchieve(clazz.getSuperclass(), interfaceName))return true;
    	return false;
    }
    
	/**
	 *<p>Title: isExtendsFrom</p>
	 *<p>Description:�ж����Ƿ�̳���ĳ����</p>
	 * @param @param <T>
	 * @param @param clazz Ѱ���Ů
	 * @param @param superName ����ʮ�˴�����
	 * @param @return �趨�ļ�
	 * @return  boolean ��������
	 * @throws
	*/
	public static boolean isExtendsFrom(Class<?> clazz,String superName){
		if(clazz ==null || superName == null)return false;
    	if("".equals(superName))return false;
		Class<?> superClass = (Class<?>) clazz.getSuperclass();
		if(superClass == null) return false;
		if(superClass.getName().endsWith("."+superName)||superClass.getName().equals(superName))return true;
		if(isExtendsFrom(superClass, superName))return true;
		return false;
	}
	
    /**
     *<p>Title: isBaseType</p>
     *<p>Description:�ж�OBJECT�Ƿ�Ϊ��������</p>
     * @param @param obj
     * @param @return �趨�ļ�
     * @return  boolean ��������
     * @throws
    */
    public static <T>boolean isBaseType(T obj){
    	if(obj == null) return true;
    	String propertyType = obj.getClass().getName();
    	return isBaseType(propertyType);
    }
    
    /**
     *<p>Title: isBaseType</p>
     *<p>Description:�ж�FIELD�Ƿ�Ϊ��������</p>
     * @param @param field
     * @param @return �趨�ļ�
     * @return  boolean ��������
     * @throws
    */
    public static boolean isBaseType(Field field){
    	if(field == null)return true;
    	String fieldType = field.getType().getName();
    	return isBaseType(fieldType);
    }
    
    public static boolean isBaseType(Class<?> clazz){
    	if(clazz == null)return true;
    	return isBaseType(clazz.toString());
    }
    
    public static boolean isDate(Field field){
    	return isDate(field.getType());
    }
    
    public static boolean isDate(Class<?> clazz){
    	return Date.class.equals(clazz);
    }
    
    /**
     * whichBaseType(�ж�����һ����������)
     * (����������������������� �C ��ѡ)
     * @param type
     * @return
     *BaseType
     * @exception
     * @since  1.0.0
    */
    public static BaseType whichBaseType(Class<?> clazz){
    	return whichBaseType(clazz.getName());
    }
    
    /**
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     * @throws ClassNotFoundException 
     *<p>Title: newInstance</p>
     *<p>Description:���������ʵ��</p>
     * @param @param className
     * @param @return 
     * @return  Object 
     * @throws
    */
    public static Object newInstance(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		Class<?> clazz = getClassForName(className);
		return newInstance(clazz);
    }
    
    /**
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     *<p>Title: newInstance</p>
     *<p>Description:���������ʵ��</p>
     * @param @param <T>
     * @param @param clazz
     * @param @return 
     * @return  T 
     * @throws
    */
    public static<T> T newInstance(Class<T> clazz){
    	try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    
    public static Class<?> getClassForName(String className) throws ClassNotFoundException{
    	return Class.forName(className);
    }
    
    /**
     * getField(��ȡ���ԣ������Ӹ��ڵ��ȡ)
     * (����������������������� �C ��ѡ)
     * @param clazz
     * @param propertyName
     * @return
     *Field
     * @exception
     * @since  1.0.0
    */
    public static Field getField(Class<?> clazz, String propertyName){
    	if(clazz == null || clazz == Object.class)return null;
    	try {
			Field field = clazz.getDeclaredField(propertyName);
			return field;
		} catch (NoSuchFieldException e) {
			return getField(clazz.getSuperclass(), propertyName);
		} catch (SecurityException e) {
			return null;
		}
    }
    
    /**
	 * getAllFields(��ȡ���е����ԣ���������)
	 * (����������������������� �C ��ѡ)
	 * @param clazz
	 * @return
	 *Field[]
	 * @exception
	 * @since  1.0.0
	*/
	public static <T>Field[] getAllFields(Class<T> clazz){
		if(clazz ==null)return null;
		List<Field> fields = new ArrayList<>();
		List<String> fieldNamesList = new ArrayList<>();
		return getAllFields(clazz, fields, fieldNamesList);
	}
	
	/**
	 * getAllFields(������һ�仰�����������������)
	 * (����������������������� �C ��ѡ)
	 * @param clazz
	 * @param fieldsResult
	 * @param fieldNamesList Ԥ������������������Ϊ׼
	 * @return
	 *Field[]
	 * @exception
	 * @since  1.0.0
	*/
	private static <T>Field[] getAllFields(Class<T> clazz, List<Field> fieldsResult, List<String> fieldNamesList){
		if(clazz ==null)return null;
		if(!Object.class.isAssignableFrom(clazz))return null;
		for(Field field : clazz.getDeclaredFields()){
			if(!fieldNamesList.contains(field.getName())){
				fieldNamesList.add(field.getName());
				fieldsResult.add(field);				
			}
		}
		Class<?> superClass = (Class<?>) clazz.getSuperclass();
		
		//TODO runtime exception �ų�
		if(RuntimeException.class.equals(superClass))return fieldsResult.toArray(new Field[]{});
		
		if(superClass != null){
			getAllFields(superClass, fieldsResult, fieldNamesList);
		} 
		return fieldsResult.toArray(new Field[]{});
	}
	
	/**
	 * getFieldRealType(��ȡ������ʵ����)
	 * (���Ա���ֱ�Ӷ���������)
	 * @param clazz
	 * @param field
	 * @return
	 *Class<?>
	 * @exception
	 * @since  1.0.0
	 * TODO ��������ƺ���̫ͨ�ã�1.û�б������࣬2.���ڱ����ͷ��͵�û�п���
	*/
	public static Class<?> getFieldRealType(Class<?> clazz, Field field){
		Type type = field.getGenericType();
		if(type instanceof Class<?>)return (Class<?>) type;
		//��ȡ���䶨��ķ���
		Type[] actualTypes = getTypeActualTypeArguments(clazz.getGenericSuperclass());
		TypeVariable<?>[] superClassTypeVariable =   clazz.getSuperclass().getTypeParameters();
		for(int i = 0, len = superClassTypeVariable.length; i < len; i++){
			TypeVariable<?> typeVariable = superClassTypeVariable[i];
			if(type.toString().equals(typeVariable.getName()))return (Class<?>) actualTypes[i];
		}
		return field.getType();
	}
    
    /**
     * <p>description: ��������</p>
     * <p>Date: 2013-9-19 ����02:10:52</p>
     * <p>modify��</p>
     * @author: Madgin
     * @version: 1.0
     */
    public enum BaseType{
    	String,Boolean,Char,Int,Byte,Short,Double,Long,Float;
    	public Object valueOf(Object obj){
    		switch (this) {
    		case String:
    			return java.lang.String.valueOf(obj.toString());
    		case Boolean:
    			return java.lang.Boolean.valueOf(obj.toString());
    		case Byte:
    			return java.lang.Byte.valueOf(obj.toString());
    		case Char:
    			return java.lang.Character.valueOf(obj.toString().charAt(0));
    		case Double:
    			return java.lang.Double.valueOf(obj.toString());
    		case Float:
    			return java.lang.Float.valueOf(obj.toString());
    		case Int:
    			return java.lang.Integer.valueOf(obj.toString());
    		case Long:
    			return java.lang.Long.valueOf(obj.toString());
    		case Short:
    			return java.lang.Short.valueOf(obj.toString());
			default:
				return null;
			}
    	}
    }
    
    /**
     *<p>Title: isBaseType</p>
     *<p>Description:�ж�OBJECT�Ƿ�Ϊ��������</p>
     * @param @param type
     * @param @return �趨�ļ�
     * @return  boolean ��������
     * @throws
    */
    private static boolean isBaseType(String type){
    	if(type == null) return false;
    	if(whichBaseType(type) != null)return true;
    	return false;
    }
    

    
    /**
     *<p>Title: whichBaseType</p>
     *<p>Description:�жϻ�������</p>
     * @param @param className
     * @param @return �趨�ļ�
     * @return  BaseType ��������
     * @throws
    */
    private static BaseType whichBaseType(String className){
    	if(className == null) return null;
    	String propertyType = className;
    	if (propertyType.endsWith(".String")){
    		return BaseType.String;
    	}
    	if(propertyType.endsWith(".Character") || propertyType.equals("char")){
    		return BaseType.Char;
    	}
    	
    	if(propertyType.endsWith(".Byte") || propertyType.equals("byte")){
    		return BaseType.Byte;
    	}
    	
    	if(propertyType.endsWith(".Float") || propertyType.equals("float")){
    		return BaseType.Float;
    	}
    	
    	if(propertyType.endsWith(".Integer") || propertyType.equals("int")){
    		return BaseType.Int;
    	}
    	
    	if(propertyType.endsWith(".Boolean") || propertyType.equals("boolean")){
    		return BaseType.Boolean;
    	}
    	
    	if(propertyType.endsWith(".Double") || propertyType.equals("double")){
    		return BaseType.Double;
    	}
    	
    	if(propertyType.endsWith(".Short") || propertyType.equals("short")){
    		return BaseType.Short;
    	}
    	
    	if(propertyType.endsWith(".Long") || propertyType.equals("long")){
    		return BaseType.Long;
    	}
    	return null;
    }
    

}
