package com.tlcsdm.eclipse.fixcnchar.handler;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.tlcsdm.eclipse.fixcnchar.Activator;
import com.tlcsdm.eclipse.fixcnchar.preferences.FixCharPreferencePage;

public class ReplaceHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (!(part instanceof ITextEditor)) {
			return null; // 当前不是文本编辑器
		}
		ITextEditor editor = (ITextEditor) part;

		IDocument document = getDocument(editor);
		if (document == null) {
			return null;
		}

		ITextSelection selection = (ITextSelection) editor.getSelectionProvider().getSelection();

		// 获取替换规则
		Map<String, String> rules = loadRules();

		// 支持撤销/重做
		IRewriteTarget rewriteTarget = editor.getAdapter(IRewriteTarget.class);
		if (rewriteTarget != null) {
			rewriteTarget.beginCompoundChange();
		}

		try {
			if (selection.getLength() > 0) {
				// 仅替换选中部分
				String text = selection.getText();
				String replaced = applyRules(text, rules);
				document.replace(selection.getOffset(), selection.getLength(), replaced);
			} else {
				// 替换整个文档
				String text = document.get();
				String replaced = applyRules(text, rules);
				document.set(replaced);
			}
		} catch (BadLocationException e) {
			throw new ExecutionException("Error replacing text", e);
		} finally {
			if (rewriteTarget != null) {
				rewriteTarget.endCompoundChange();
			}
		}

		return null;
	}

	private IDocument getDocument(ITextEditor editor) {
		IDocumentProvider provider = editor.getDocumentProvider();
		if (provider == null)
			return null;
		return provider.getDocument(editor.getEditorInput());
	}

	private Map<String, String> loadRules() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String rulesStr = store.getString(FixCharPreferencePage.PREF_RULES); // 和 RuleTableEditor 中的 key 保持一致
		Map<String, String> map = new LinkedHashMap<>();

		if (rulesStr != null && !rulesStr.isEmpty()) {
			// 先把 "\\n" 替换成真正的换行符
			rulesStr = rulesStr.replace("\\n", "\n");
			String[] lines = rulesStr.split("\\n");
			for (String line : lines) {
				if (line.contains("=")) {
					String[] parts = line.split("=", 2);
					map.put(parts[0].trim(), parts[1].trim());
				}
			}
		}
		return map;
	}

	private String applyRules(String text, Map<String, String> rules) {
		String result = text;
		for (Map.Entry<String, String> entry : rules.entrySet()) {
			result = result.replace(entry.getKey(), entry.getValue());
		}
		return result;
	}
}
