package com.tlcsdm.eclipse.fixcnchar.listener;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import com.tlcsdm.eclipse.fixcnchar.Activator;
import com.tlcsdm.eclipse.fixcnchar.preferences.FixCharPreferencePage;

/**
 * 实时输入时的中文符号替换监听器 (支持光标位置、多字符替换、撤销/重做、规则热更新)
 */
public class FixCharVerifyListener implements VerifyKeyListener {

	private static final FixCharVerifyListener INSTANCE = new FixCharVerifyListener();
	private final Map<String, String> rules = new HashMap<>();

	private FixCharVerifyListener() {
		loadRules();

		// 监听 Preferences 改变，自动刷新规则
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.addPropertyChangeListener(event -> {
			if (FixCharPreferencePage.PREF_RULES.equals(event.getProperty())) {
				loadRules();
			}
		});
	}

	public static FixCharVerifyListener getInstance() {
		return INSTANCE;
	}

	private void loadRules() {
		String rulesPref = Activator.getDefault().getPreferenceStore().getString(FixCharPreferencePage.PREF_RULES);

		rules.clear();
		if (rulesPref != null && !rulesPref.isEmpty()) {
			String[] lines = rulesPref.split("\\n");
			for (String line : lines) {
				if (line.contains("=")) {
					String[] parts = line.split("=", 2);
					if (parts.length == 2) {
						rules.put(parts[0].trim(), parts[1].trim());
					}
				}
			}
		}
	}

	@Override
	public void verifyKey(VerifyEvent event) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		boolean enabled = store.getBoolean(FixCharPreferencePage.PREF_ENABLE_REALTIME);
		if (!enabled) {
			return; // 未启用实时替换
		}

		if (event.character == 0) {
			return; // 忽略控制键
		}

		String input = String.valueOf(event.character);
		if (!rules.containsKey(input)) {
			return;
		}

		if (event.character == 0) {
			return; // 忽略控制键
		}

		if (!rules.containsKey(input)) {
			return;
		}

		event.doit = false; // 阻止原始输入

		String replacement = rules.get(input);

		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page != null) {
			IEditorPart editor = page.getActiveEditor();
			if (editor instanceof ITextEditor) {
				ITextEditor textEditor = (ITextEditor) editor;
				IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());

				ISourceViewer viewer = (ISourceViewer) textEditor.getAdapter(ITextViewer.class);
				if (viewer == null) {
					return;
				}

				int offset = viewer.getSelectedRange().x;

				// 用 IRewriteTarget 保证撤销/重做支持
				IRewriteTarget target = textEditor.getAdapter(IRewriteTarget.class);
				if (target != null) {
					target.beginCompoundChange();
				}

				try {
					document.replace(offset, 0, replacement);
					// 替换后光标移动到插入文本末尾
					viewer.setSelectedRange(offset + replacement.length(), 0);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (target != null) {
						target.endCompoundChange();
					}
				}
			}
		}
	}
}
