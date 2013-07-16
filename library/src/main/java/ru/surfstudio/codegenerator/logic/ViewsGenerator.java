package ru.surfstudio.codegenerator.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ru.surfstudio.codegenerator.data.Constants;
import ru.surfstudio.codegenerator.data.TypesAdapter;
import ru.surfstudio.codegenerator.data.WidgetResource;
import ru.surfstudio.codegenerator.serialization.Type;

public class ViewsGenerator {

	/** find view by id */
	private final static String DECLARATION_PATTERN = "\t\t%2$s = (%1$s) findViewById(%3$s);\n";
	private final static String DECLARATION_INNER_PATTERN = "\t\t\t%2$s = (%1$s) v.findViewById(%3$s);\n";
	/** import package */
	private final static String IMPORT_PATTERN = "import %s;\n";
	/** field pattern */
	private final static String FIELD_PATTERN = "\tprivate %1$s %2$s;\n";
	private final static String FIELD_INNER_PATTERN = "\t\tprivate %1$s %2$s;\n";
	/** typeface pattern */
	private final static String TYPEFACE_PATTERN = "\t\t%1$s.setTypeface(roboto);\n";
	private final static String TYPEFACE_INNER_PATTERN = "\t\t\t%1$s.setTypeface(roboto);\n";
	/** onclick pattern */
	private final static String ONCLICK_PATTERN = "\t\t%1$s.setOnClickListener(new OnClickListener() {\n\t\t\t@Override\n\t\t\tpublic void onClick(View v) {\n\n\t\t\t}\n\t\t});\n";
	private final static String ONCLICK_INNER_PATTERN = "\t\t\t%1$s.setOnClickListener(new OnClickListener() {\n\t\t\t\t@Override\n\t\t\t\tpublic void onClick(View v) {\n\n\t\t\t\t}\n\t\t\t});\n";
	/** method pattern */
	private final static String METHOD_VOID_PATTERN = "\tprivate void %1$s(){\n%2$s\t}\n";	
	private final static String METHOD_VOID_INNER_PATTERN = "\t\tprivate void %1$s(){\n%2$s\t\t}\n\n";
	private final static String METHOD_VOID_INNER_VIEW_PATTERN = "\t\tprivate void %1$s(View v){\n%2$s\t\t}\n\n";
	/** method call pattern */
	private final static String METHOD_CALL_PATTERN = "\t\t%s();\n";	
	private final static String METHOD_CALL_INNER_PATTERN = "\t\t\t%s();\n";
	private final static String METHOD_CALL_INNER_INIT_VIEW_PATTERN = "\t\t\t%s(v);\n";
	
	private final static String ADAPTER_POPULATE_FORM = "\t\tpublic void populateForm(String item) {\n%1$s\n\t\t}\n";
	
	/** names of widgets */
	private final static String TEXT_VIEW = "TextView";
	private final static String BUTTON = "Button";
	private final static String IMAGE_BUTTON = "ImageButton";
	private final static String CHECK_BOX = "CheckBox";
	private final static String EDIT_TEXT = "EditText";
	private final static String IMAGE_VIEW = "ImageView";
	private final static String RELATIVE_LAYOUT = "RelativeLayout";
	private final static String VIEW_GROUP = "ViewGroup";
	private final static String LINEAR_LAYOUT = "LinearLayout";
	private final static String FRAME_LAYOUT = "FrameLayout";
	private final static String VIEW = "View";
	
	/** names of methods */
	private final static String INIT_VIEWS = "initViews";
	private final static String SET_LISTENERS = "setListeners";
	private final static String SET_FONT = "setFonts";	
	
	/** lists of groups */
	private final static ArrayList<String> TEXT_WIDGETS = new ArrayList<String>() {
		{
			add(TEXT_VIEW);
			add(EDIT_TEXT);
			add(CHECK_BOX);
		}
	};

	private final static ArrayList<String> BUTTON_WIDGETS = new ArrayList<String>() {
		{
			add(BUTTON);
			add(IMAGE_BUTTON);
		}
	};

	private final static ArrayList<String> EXCLUDE_WIDGETS = new ArrayList<String>() {
		{
			add(IMAGE_VIEW);
			add(RELATIVE_LAYOUT);
			add(VIEW_GROUP);
			add(LINEAR_LAYOUT);
			add(FRAME_LAYOUT);
			add(VIEW);
		}
	};
	
	private Map<WidgetResource, Type> widgetsTypes;
	private Set<Type> imports;
	private TypesAdapter typesAdapter;
	
	public ViewsGenerator(TypesAdapter typesAdapter, String basePackage){
		this.typesAdapter = typesAdapter;
		widgetsTypes = new HashMap<WidgetResource, Type>();
		imports = new HashSet<Type>();
		imports.add(Constants.getR(basePackage));
		imports.add(typesAdapter.getType("View"));
		imports.add(typesAdapter.getType("OnClickListener"));
		imports.add(typesAdapter.getType("Typeface"));
	}
	
	public void addWidget(WidgetResource widgetResource, String typeName) {
		Type type = typesAdapter.getType(typeName);
		if (type == null) {
			throw new IllegalArgumentException("Type: " + typeName + " is not defined.");
		}
		widgetsTypes.put(widgetResource, type);
		imports.add(type);
	}
	
	public String getFields(boolean innerClass) {
		StringBuilder stringBuilder = new StringBuilder();
		for (WidgetResource widgetResource : widgetsTypes.keySet()) {
			if (!EXCLUDE_WIDGETS.contains(widgetsTypes.get(widgetResource).getName())) {
				String fieldLine = String.format(innerClass ? FIELD_INNER_PATTERN : FIELD_PATTERN, widgetsTypes.get(widgetResource).getName(),
						widgetResource.getVariableName());
				stringBuilder.append(fieldLine);
			}
		}
		return stringBuilder.toString();
	}
	
	public String getImports() {
		StringBuilder stringBuilder = new StringBuilder();
		for (Type widgetName : imports) {
			String importLine = String.format(IMPORT_PATTERN, TypesAdapter.getTypeFullName(widgetName));
			stringBuilder.append(importLine);
		}
		return stringBuilder.toString();
	}
	
	public String getInitViewsMethod(boolean innerClass) {
		StringBuilder builder = new StringBuilder();
		for (WidgetResource widgetResource : widgetsTypes.keySet()) {
			if (!EXCLUDE_WIDGETS.contains(widgetsTypes.get(widgetResource).getName())) {
				builder.append(String.format(innerClass ? DECLARATION_INNER_PATTERN : DECLARATION_PATTERN, widgetsTypes.get(widgetResource).getName(),
						widgetResource.getVariableName(), widgetResource.getReference()));
			}
		}
		return String.format(innerClass ? METHOD_VOID_INNER_VIEW_PATTERN : METHOD_VOID_PATTERN, INIT_VIEWS, builder.toString());
	}
	
	public String getSetFontsMethod(boolean innerClass) {
		StringBuilder builder = new StringBuilder();
		builder.append(innerClass ? "\t\t\tTypeface roboto = null;//TODO init this by utils\n" : "\t\tTypeface roboto = null;//TODO init this by utils\n");
		for (WidgetResource widgetResource : widgetsTypes.keySet()) {
			if (TEXT_WIDGETS.contains(widgetsTypes.get(widgetResource).getName())) {
				builder.append(String.format(innerClass ? TYPEFACE_INNER_PATTERN : TYPEFACE_PATTERN, widgetResource.getVariableName()));
			}
		}
		return String.format(innerClass ? METHOD_VOID_INNER_PATTERN : METHOD_VOID_PATTERN, SET_FONT, builder.toString());
	}

	public String getSetListenersMethod(boolean innerClass) {
		String listeners = getListeners(innerClass);
		return String.format(innerClass ? METHOD_VOID_INNER_PATTERN : METHOD_VOID_PATTERN, SET_LISTENERS, listeners);
	}
	
	private String getListeners(boolean innerClass){
		StringBuilder builder = new StringBuilder();
		for (WidgetResource widgetResource : widgetsTypes.keySet()) {
			if (BUTTON_WIDGETS.contains(widgetsTypes.get(widgetResource).getName())) {
				builder.append(String.format(innerClass ? ONCLICK_INNER_PATTERN : ONCLICK_PATTERN, widgetResource.getVariableName()));
			}
		}
		return builder.toString();
	}
	
	
	public String getCallInitViewMethod(boolean innerClass){
		return String.format(innerClass ? METHOD_CALL_INNER_PATTERN : METHOD_CALL_PATTERN, INIT_VIEWS);
	}
	
	public String getCallInnerInitViewMethod(){
		return String.format(METHOD_CALL_INNER_INIT_VIEW_PATTERN, INIT_VIEWS);
	}
	
	public String getCallSetFontsMethod(boolean innerClass){
		return String.format(innerClass ? METHOD_CALL_INNER_PATTERN : METHOD_CALL_PATTERN, SET_FONT);
	}

	public String getCallSetListenersMethod(boolean innerClass){
		return String.format(innerClass ? METHOD_CALL_INNER_PATTERN : METHOD_CALL_PATTERN, SET_LISTENERS);	
	}
	
	public String getPopulateForm(){
		return String.format(ADAPTER_POPULATE_FORM, getListeners(true));
	}

}
