package ru.surfstudio.codegenerator.logic;

import java.util.HashSet;
import java.util.Set;

import ru.surfstudio.codegenerator.data.Constants;
import ru.surfstudio.codegenerator.data.TypesAdapter;
import ru.surfstudio.codegenerator.data.WidgetResource;
import ru.surfstudio.codegenerator.serialization.Type;

public class AdapterGenerator {

	/** string pattern */
	/** import package */
	private final static String IMPORT_PATTERN = "import %s;\n";
	/** package */
	private final static String PACKAGE_PATTERN = "package %s;\n";
	/** root pattern*/
	private static final String HEADER_PATTERN = "public class %1$sAdapter extends ArrayAdapter<String>{";
	/** fields pattern*/ 
	private static final String FIELDS_PATTERN = "\tprivate Context context;\n\tprivate LayoutInflater inflater;\n\n";
	/** constructor pattern*/
	private static final String CONSTRUCTOR_PATTERN = "\tpublic %1$sAdapter(Context context, List<String> objects) {\n\t\tsuper(context, %2$s, objects);\n\t\tinflater = LayoutInflater.from(context);\n\t\tthis.context = context;\n\t}\n";
	/** getView pattern*/
	private static final String GET_VIEW_PATTERN = "\t@Override\n"
		+"\tpublic View getView(int position, View convertView, ViewGroup parent) {\n"
		+"\t\tViewHolder holder;\n"
		+"\t\tif (convertView == null){\n"
		+"\t\t\tconvertView = inflater.inflate(%s, parent, false);\n"
		+"\t\t\tholder = new ViewHolder(convertView);\n"
		+"\t\t\tconvertView.setTag(holder);\n"
		+"\t\t} else {\n"
		+"\t\t\tholder = (ViewHolder) convertView.getTag();\n"
		+"\t\t}\n\n"
		+"\t\tString item = getItem(position);\n"
		+"\t\tif (item != null){\n"
		+"\t\t\tholder.populateForm(item);\n"
		+"\t\t}\n"
		+"\t\treturn convertView;\n"
		+"\t}\n";
	/** view holder pattern*/
	private static final String VIEW_HOLDER_PATTERN = "\tprivate class ViewHolder{\n\n%1$s\n\t\tpublic ViewHolder(View v){\n%2$s\t\t}\n%3$s\t}\n";
	/** tag pattern */
	private final static String TAG_PATTERN = "\tprivate static final String TAG = %1$sAdapter.class.getSimpleName();\n";
	
	protected TypesAdapter typesAdapter;
	private String packageName;
	private String adapterName;
	private String itemName;
	private Set<Type> imports = new HashSet<Type>();
	private ViewsGenerator viewGenerator;
	
	public AdapterGenerator(TypesAdapter pTypesAdapter, String adapterName, String basePackage){
		typesAdapter = pTypesAdapter;
		this.adapterName = adapterName;
		viewGenerator = new ViewsGenerator(typesAdapter, basePackage);
		imports.add(typesAdapter.getType("View"));
		imports.add(typesAdapter.getType("List"));
		imports.add(typesAdapter.getType("Context"));
		imports.add(typesAdapter.getType("LayoutInflater"));
		imports.add(typesAdapter.getType("ViewGroup"));
		imports.add(typesAdapter.getType("Typeface"));
		imports.add(typesAdapter.getType("ArrayAdapter"));
		imports.add(Constants.getR(basePackage));
	}
	
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	public void addWidget(WidgetResource widgetResource, String typeName) {
		viewGenerator.addWidget(widgetResource, typeName);
	}
	
	public String generate() {
		StringBuilder stringBuilder = new StringBuilder();
		if (packageName != null && !packageName.isEmpty()) {
			stringBuilder.append(getPackage());
			stringBuilder.append("\n");
		}
		stringBuilder.append(getImports());
		stringBuilder.append("\n");
		stringBuilder.append(getHeader());
		stringBuilder.append("\n");
		stringBuilder.append(getTag());
		stringBuilder.append("\n");
		stringBuilder.append(FIELDS_PATTERN);
		stringBuilder.append("\n");
		stringBuilder.append(getConstructor());
		stringBuilder.append("\n");
		stringBuilder.append(getGetView());
		stringBuilder.append("\n");
		stringBuilder.append(getHolder());
		stringBuilder.append("}");
		stringBuilder.append("\n");
		return stringBuilder.toString();
	}
	
	public String getCallsMethod(){
		StringBuilder builder = new StringBuilder();
		builder.append(viewGenerator.getCallInnerInitViewMethod());
		builder.append(viewGenerator.getCallSetFontsMethod(true));
		return builder.toString();
	}
	
	public String getItemName(){
		return String.format("R.layout.%s", itemName.replace(".xml", ""));
	}
	
	public String getFields(){
		return viewGenerator.getFields(true);
	}
	
	public String getMethods(){
		StringBuilder builder = new StringBuilder();
		builder.append(viewGenerator.getInitViewsMethod(true));
		builder.append(viewGenerator.getSetFontsMethod(true));
		builder.append(viewGenerator.getPopulateForm());
		return builder.toString();
	}
	
	public String getHolder(){
		return String.format(VIEW_HOLDER_PATTERN, getFields(), getCallsMethod(), getMethods());
	}
	
	public String getGetView(){
		return String.format(GET_VIEW_PATTERN, getItemName());
	}
	
	public String getPackage() {
		return String.format(PACKAGE_PATTERN, packageName);
	}
	
	public String getConstructor(){
		return String.format(CONSTRUCTOR_PATTERN, adapterName, getItemName());
	}
	
	public String getImports() {
		StringBuilder stringBuilder = new StringBuilder();
		for (Type widgetName : imports) {
			String importLine = String.format(IMPORT_PATTERN, TypesAdapter.getTypeFullName(widgetName));
			stringBuilder.append(importLine);
		}
		stringBuilder.append(viewGenerator.getImports());
		return stringBuilder.toString();
	}
	
	public String getHeader() {
		return String.format(HEADER_PATTERN, adapterName);
	}
	
	public String getTag() {
		return String.format(TAG_PATTERN, adapterName);
	}
	
}
