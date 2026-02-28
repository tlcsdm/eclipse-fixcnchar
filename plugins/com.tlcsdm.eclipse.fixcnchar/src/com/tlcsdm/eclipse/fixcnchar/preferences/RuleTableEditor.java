package com.tlcsdm.eclipse.fixcnchar.preferences;

import java.util.Map;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.tlcsdm.eclipse.fixcnchar.util.RuleParser;

/**
 * 可编辑规则表格 FieldEditor 显示中文符号 → 英文符号替换规则
 */
public class RuleTableEditor extends FieldEditor {

	private Table table;
	private Composite parentComposite;

	public RuleTableEditor(String name, String labelText, Composite parent) {
		init(name, labelText);
		createControl(parent);
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
		if (table != null) {
			((GridData) table.getLayoutData()).horizontalSpan = numColumns;
		}
	}

	@Override
	protected void doLoad() {
		IPreferenceStore store = getPreferenceStore();
		String rulesStr = store.getString(getPreferenceName());
		loadRulesFromString(rulesStr);
	}

	@Override
	protected void doLoadDefault() {
		IPreferenceStore store = getPreferenceStore();
		String defaultStr = store.getDefaultString(getPreferenceName());
		loadRulesFromString(defaultStr);
	}

	@Override
	protected void doStore() {
		IPreferenceStore store = getPreferenceStore();
		StringBuilder sb = new StringBuilder();
		for (TableItem item : table.getItems()) {
			sb.append(item.getText(0)).append("=").append(item.getText(1)).append("\n");
		}
		store.setValue(getPreferenceName(), sb.toString());
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		parentComposite = parent;

		// 表格控件
		table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn col1 = new TableColumn(table, SWT.LEFT);
		col1.setText("中文符号");
		col1.setWidth(100);

		TableColumn col2 = new TableColumn(table, SWT.LEFT);
		col2.setText("英文符号");
		col2.setWidth(100);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns;
		gd.heightHint = 150;
		table.setLayoutData(gd);

		// 按钮条
		Composite buttonComp = new Composite(parent, SWT.NONE);
		GridData gdBtn = new GridData(GridData.FILL_HORIZONTAL);
		gdBtn.horizontalSpan = numColumns;
		buttonComp.setLayoutData(gdBtn);

		RowLayout layout = new RowLayout();
		layout.justify = true;
		layout.spacing = 5;
		buttonComp.setLayout(layout);

		Button addBtn = new Button(buttonComp, SWT.PUSH);
		addBtn.setText("Add");
		addBtn.addListener(SWT.Selection, e -> addRule());

		Button editBtn = new Button(buttonComp, SWT.PUSH);
		editBtn.setText("Edit");
		editBtn.addListener(SWT.Selection, e -> editRule());

		Button removeBtn = new Button(buttonComp, SWT.PUSH);
		removeBtn.setText("Remove");
		removeBtn.addListener(SWT.Selection, e -> removeRule());
	}

	private void addRule() {
		InputDialog dlg = new InputDialog(parentComposite.getShell(), "Add Rule", "Enter rule as 中文符号=英文符号", "", null);
		if (dlg.open() == Window.OK) {
			String input = dlg.getValue().trim();
			if (input.contains("=")) {
				String[] parts = input.split("=", 2);
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(new String[] { parts[0].trim(), parts[1].trim() });
			}
		}
	}

	private void editRule() {
		int idx = table.getSelectionIndex();
		if (idx < 0)
			return;

		TableItem item = table.getItem(idx);
		String current = item.getText(0) + "=" + item.getText(1);
		InputDialog dlg = new InputDialog(parentComposite.getShell(), "Edit Rule", "Edit rule as 中文符号=英文符号", current,
				null);
		if (dlg.open() == Window.OK) {
			String input = dlg.getValue().trim();
			if (input.contains("=")) {
				String[] parts = input.split("=", 2);
				item.setText(new String[] { parts[0].trim(), parts[1].trim() });
			}
		}
	}

	private void removeRule() {
		int idx = table.getSelectionIndex();
		if (idx >= 0) {
			table.remove(idx);
		}
	}

	private void loadRulesFromString(String rulesStr) {
		table.removeAll();
		Map<String, String> rules = RuleParser.parseRules(rulesStr);
		for (Map.Entry<String, String> entry : rules.entrySet()) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] { entry.getKey(), entry.getValue() });
		}
	}

	@Override
	public int getNumberOfControls() {
		return 2; // 表格 + 按钮条
	}
}
