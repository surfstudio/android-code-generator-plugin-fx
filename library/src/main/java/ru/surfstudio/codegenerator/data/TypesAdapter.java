package ru.surfstudio.codegenerator.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.surfstudio.codegenerator.serialization.Method;
import ru.surfstudio.codegenerator.serialization.Type;
import ru.surfstudio.codegenerator.serialization.Type.Require.Listener;
import ru.surfstudio.codegenerator.serialization.Types;


public class TypesAdapter {

	private Map<String, Type> typesMap = new HashMap<String, Type>();

	private boolean autoTypeRecognition;

	public TypesAdapter(Types types) {
		super();
		for (Type type : types.getType()) {
			String typeFullName = type.getName();
			convertType(type, typeFullName);
			addType(type);
		}
	}

	private Type addType(Type type) {
		return typesMap.put(type.getName(), type);
	}

	protected void convertType(Type type, String typeFullName) {
		int lastDot = typeFullName.lastIndexOf(".");
		if (lastDot != -1) {
			type.setName(typeFullName.substring(lastDot + 1));
			type.setPackage(typeFullName.substring(0, lastDot));
		}
	}

	protected String getShortName(String typeName) {
		int lastDot = typeName.lastIndexOf(".");
		if (lastDot != -1) {
			return typeName.substring(lastDot + 1);
		}
		return typeName;
	}

	protected String getPackageName(String typeName) {
		int lastDot = typeName.lastIndexOf(".");
		if (lastDot != -1) {
			return typeName.substring(0, lastDot);
		}
		return null;
	}

	public void setAutoTypeRecognition(boolean autoTypeRecognition) {
		this.autoTypeRecognition = autoTypeRecognition;
	}

	public Type getType(String typeName) {
		Type type = typesMap.get(getShortName(typeName));
		if ((type == null || getPackageName(typeName) != null) && autoTypeRecognition) {
			type = new Type();
			if (getPackageName(typeName) != null) {
				convertType(type, typeName);
			} else {
				type.setName(typeName);
				type.setPackage("android.widget");
			}
		}
		return type;
	}

	public static String getTypeFullName(Type type) {
		String typeName = new String();
		if (type.getPackage() != null) {
			typeName += type.getPackage() + ".";

		}
		return typeName + type.getName();
	}

	public List<Method> getTypeMethods(Type type) {
		if (type != null && type.getImplements() != null) {
			return type.getImplements().getMethod();
		}
		return null;
	}

	public Set<Type> getTypesFromListener(String interfaceName) {
		Set<Type> connectedTypes = new HashSet<Type>();
		for (Type type : typesMap.values()) {
			if (type.getRequire() != null) {
				for (Listener listener : type.getRequire().getListener()) {
					if (interfaceName.equals(listener.getType())) {
						connectedTypes.add(type);
					}
				}
			}
		}
		return connectedTypes;
	}
}
