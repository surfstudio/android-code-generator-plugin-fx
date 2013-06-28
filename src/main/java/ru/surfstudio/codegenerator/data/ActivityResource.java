package ru.surfstudio.codegenerator.data;

public class ActivityResource {

	private String activityName;
	private String layoutReference;
	
	public ActivityResource(String id) {
		layoutReference = getReferenceFromID(id);
		activityName = getActivityNameFromId(id);
	}

	public String getVariableName() {
		return activityName;
	}

	public String getReference() {
		return layoutReference;
	}
	
	public String getActivityNameFromId(String pLayoutName) {
		pLayoutName = pLayoutName.replace("activity_", "");
		String[] words = pLayoutName.split("_");
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			stringBuilder.append(Character.toUpperCase(word.charAt(0)) + word.substring(1));
		}
		return stringBuilder.toString() + "Activity";
	}

	public String getActivityNameFromVariable(String variableName) {
		return Character.toUpperCase(variableName.charAt(0)) + variableName.substring(1) + "Activity";
	}	
	
	private String getReferenceFromID(String id) {
		return new StringBuilder("R.layout.").append(id).toString();
	}
}
