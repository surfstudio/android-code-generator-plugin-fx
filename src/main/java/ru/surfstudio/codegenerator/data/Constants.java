package ru.surfstudio.codegenerator.data;

import ru.surfstudio.codegenerator.serialization.Type;

public class Constants {

	public static String getAdapterPackage(String rootPackage){
		return new StringBuilder(rootPackage).append(".adapter").toString();
	}
	
	public static String getActivityPackage(String rootPackage){
		return new StringBuilder(rootPackage).append(".activity").toString();
	}
	
	public static Type getR(String basePackage){
		Type type = new Type();
		type.setPackage(basePackage);
		type.setName("R");
		return type;
	}
}
