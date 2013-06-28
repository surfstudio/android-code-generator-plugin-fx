package ru.surfstudio.codegenerator.data;

public class WidgetResource {

	private String variableName;
	private String reference;

	public WidgetResource(String id) {
		variableName = getVariableNameFromId(id);
		reference = getReferenceFromID(id);
	}

	public String getVariableName() {
		return variableName;
	}

	public String getReference() {
		return reference;
	}
	
	private String getVariableNameFromId(String id) {
		String idName = getIdNameFromId(id);
		StringBuilder stringBuilder = new StringBuilder();
		String[] words = idName.split("_");
		stringBuilder.append(words[0]);
		for (int i = 1; i < words.length; i++) {
			String word = words[i];
			stringBuilder.append(Character.toUpperCase(word.charAt(0)) + word.substring(1));
		}
		return stringBuilder.toString();
	}

	private String getReferenceFromID(String id) {
		return new StringBuilder("R.id.").append(getIdNameFromId(id)).toString();
	}
	
	private String getIdNameFromId(String pId) {
		if (pId.startsWith("@+id/")) {
			return pId.substring(5);
		} else if (pId.startsWith("@id/")) {
			return pId.substring(4);
		}
		throw new IllegalArgumentException("Wrong id structure: " + pId);
	}
	
}
