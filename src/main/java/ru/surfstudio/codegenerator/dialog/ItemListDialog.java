package ru.surfstudio.codegenerator.dialog;

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import ru.surfstudio.codegenerator.plugin.XmlLayoutParser.OnItemSelectedListener;

public class ItemListDialog extends Dialog {

	private static final String TITLE_PATTERN = "Выберите файл для адаптера %s";

	private List list;
	private String activityName;
	private OnItemSelectedListener onItemSelectedListener;
	private File[] files;

	public ItemListDialog(Shell parentShell, String activityName) {
		super(parentShell);
		this.activityName = activityName;
	}

	public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
		this.onItemSelectedListener = onItemSelectedListener;
	}

	public void setFiles(File[] files) {
		this.files = files;
	}

	protected void setShellStyle(int arg0) {
		super.setShellStyle(SWT.TITLE);
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.getShell().setText(activityName);
		try {
			composite.setLayout(new FormLayout());
			{
				createLabel(composite);
				createListField(composite);
				createButton(composite);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Set the size of the parent shell
		composite.getShell().setSize(400, 530);
		// Set the dialog position in the middle of the monitor
		setDialogLocation();
		return composite;
	}

	protected Button createButton(Composite arg0, int arg1, String arg2, boolean arg3) {
		// Retrun null so that no default buttons like 'OK' and 'Cancel' will be
		// created
		return null;
	}

	private void createLabel(Composite composite) {
		Label label = new Label(composite, SWT.None);
		label.setText(String.format(TITLE_PATTERN, activityName));
		FormData lblData = new FormData();
		lblData.width = 300;
		lblData.height = 20;
		lblData.left = new FormAttachment(0, 1000, 20);// x co-ordinate
		lblData.top = new FormAttachment(0, 1000, 17);// y co-ordinate
		label.setLayoutData(lblData);
	}

	private void createListField(Composite composite) {
		list = new List(composite, SWT.V_SCROLL);
		for (int i = 0; i < files.length; i++){
			list.add(files[i].getName());
		}
		FormData txtData = new FormData();
		txtData.width = 300;
		txtData.height = 350;
		txtData.left = new FormAttachment(0, 1000, 20);// x co-ordinate
		txtData.top = new FormAttachment(0, 1000, 50);// y co-ordinate
		list.setLayoutData(txtData);
	}

	private void createButton(Composite composite) {
		Button btn = new Button(composite, SWT.PUSH);
		btn.setText("OK");
		FormData btnData = new FormData();
		btnData.width = 90;
		btnData.height = 40;
		btnData.left = new FormAttachment(0, 1000, 100);// x co-ordinate
		btnData.top = new FormAttachment(0, 1000, 440);// y co-ordinate
		btn.setLayoutData(btnData);
		// Write listener for button
		btn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				if (list.getSelectionCount() == 1) {
					onItemSelectedListener.onItemSelected(list.getSelection()[0]);
					close();
				}
			}
		});
	}

	// ~~ Utility methods

	/**
	 * Method used to set the dialog in the centre of the monitor
	 * 
	 * @author Debadatta Mishra(PIKU)
	 */
	private void setDialogLocation() {
		Rectangle monitorArea = getShell().getDisplay().getPrimaryMonitor().getBounds();
		Rectangle shellArea = getShell().getBounds();
		int x = monitorArea.x + (monitorArea.width - shellArea.width) / 2;
		int y = monitorArea.y + (monitorArea.height - shellArea.height) / 2;
		getShell().setLocation(x, y);
	}

}
