package ru.surfstudio.codegenerator.plugin;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * Preference for program used by ContextMenuPlugin
 */
public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String P_AUTO_TYPE_RECOGNITION = "autoTypeRecognition";

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Set up your file context menu application.");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {

		addField(new BooleanFieldEditor(P_AUTO_TYPE_RECOGNITION, "&Allow automatic type recognition:", getFieldEditorParent()));
	}

	public void init(IWorkbench arg0) {
		// TODO Auto-generated method stub

	}
}