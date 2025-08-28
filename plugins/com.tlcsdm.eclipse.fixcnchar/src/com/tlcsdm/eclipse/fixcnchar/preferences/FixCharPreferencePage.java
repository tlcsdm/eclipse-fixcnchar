package com.tlcsdm.eclipse.fixcnchar.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.tlcsdm.eclipse.fixcnchar.Activator;

public class FixCharPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String PREF_RULES = "replaceRules";
	public static final String PREF_ENABLE_REALTIME = "enableRealtimeReplace";

	public FixCharPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Configure replacement rules for Chinese punctuation.");
	}

	@Override
	protected void createFieldEditors() {
		// 实时替换开关
		addField(new BooleanFieldEditor(PREF_ENABLE_REALTIME, "Enable real-time replacement of Chinese punctuation",
				getFieldEditorParent()));

		// 替换规则表格
		addField(new RuleTableEditor(PREF_RULES, "Replacement Rules (中文符号 → 英文符号):", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
	}
}