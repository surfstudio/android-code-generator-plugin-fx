package ru.surfstudio.codegenerator.logic;

import java.util.HashSet;
import java.util.Set;

import ru.surfstudio.codegenerator.data.ActivityResource;
import ru.surfstudio.codegenerator.data.TypesAdapter;
import ru.surfstudio.codegenerator.data.WidgetResource;
import ru.surfstudio.codegenerator.serialization.Type;

public class ActivityGenerator{

	/** string patterns for generation */
	/** import package */
	private final static String IMPORT_PATTERN = "import %s;\n";
	/** package */
	private final static String PACKAGE_PATTERN = "package %s;\n";
	/** header pattern */
	private final static String HEADER_PATTERN = "public class %1$s extends Activity {\n%2$s}\n";
	/** method pattern */
	private final static String METHOD_VOID_PATTERN = "\tprivate void %1$s(){\n%2$s\t}\n";
	/** on_create pattern */
	private final static String ONCREATE_PATTERN = "\t@Override\n\tpublic void onCreate(Bundle savedInstanceState) {\n\t\tsuper.onCreate(savedInstanceState);\n\t\tsetContentView(%1$s);\n\n\t\tinitActionBar();\n%2$s\t}\n";
	/** tag pattern */
	private final static String TAG_PATTERN = "\tprivate static final String TAG = %1$s.class.getSimpleName();\n";
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	/** names of methods */
	private final static String INIT_ACTION_BAR = "initActionBar";

	protected TypesAdapter typesAdapter;
	private String packageName;
	private ActivityResource activityResource;
	private Set<Type> imports;
	private ViewsGenerator viewGenerator;

	public ActivityGenerator(TypesAdapter typesAdapter, String basePackage) {
		this.typesAdapter = typesAdapter;
		imports = new HashSet<Type>();
		viewGenerator = new ViewsGenerator(typesAdapter, basePackage);
		imports.add(typesAdapter.getType("Activity"));
		imports.add(typesAdapter.getType("Bundle"));
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPackage() {
		return String.format(PACKAGE_PATTERN, packageName);
	}

	public void setActivityResource(ActivityResource activityResource) {
		this.activityResource = activityResource;
	}

	public String getImports() {
		StringBuilder stringBuilder = new StringBuilder();
		for (Type widgetName : imports) {
			String importLine = String.format(IMPORT_PATTERN, TypesAdapter.getTypeFullName(widgetName));
			stringBuilder.append(importLine);
		}
		return stringBuilder.toString();
	}

	public void addWidget(WidgetResource widgetResource, String typeName) {
		viewGenerator.addWidget(widgetResource, typeName);
	}

	public String getTag() {
		return String.format(TAG_PATTERN, activityResource.getVariableName());
	}

	public String getCreateMethod() {
		StringBuilder builder = new StringBuilder();
		builder.append(viewGenerator.getCallInitViewMethod(false));
		builder.append(viewGenerator.getCallSetFontsMethod(false));
		builder.append(viewGenerator.getCallSetListenersMethod(false));
		return String.format(ONCREATE_PATTERN, activityResource.getReference(), builder.toString());
	}

	public String generate() {
		StringBuilder stringBuilder = new StringBuilder();
		if (packageName != null && !packageName.isEmpty()) {
			stringBuilder.append(getPackage());
			stringBuilder.append("\n");
		}
		stringBuilder.append(getImports());
		stringBuilder.append(viewGenerator.getImports());
		stringBuilder.append("\n");
		
		StringBuilder innerBuilder = new StringBuilder();
		innerBuilder.append(getTag());
		innerBuilder.append("\n");
		innerBuilder.append(viewGenerator.getFields(false));
		innerBuilder.append("\n");
		innerBuilder.append(getCreateMethod());
		innerBuilder.append("\n");
		innerBuilder.append(getInitActionBarMethod());
		innerBuilder.append("\n");
		innerBuilder.append(viewGenerator.getInitViewsMethod(false));
		innerBuilder.append("\n");
		innerBuilder.append(viewGenerator.getSetFontsMethod(false));
		innerBuilder.append("\n");
		innerBuilder.append(viewGenerator.getSetListenersMethod(false));
		stringBuilder.append(String.format(HEADER_PATTERN, activityResource.getVariableName(), innerBuilder.toString()));
		return stringBuilder.toString();
	}

	public String getInitActionBarMethod() {
		return String.format(METHOD_VOID_PATTERN, INIT_ACTION_BAR, "");
	}

}
