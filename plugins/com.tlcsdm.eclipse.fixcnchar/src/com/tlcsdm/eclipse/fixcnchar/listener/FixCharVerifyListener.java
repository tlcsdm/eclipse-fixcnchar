package com.tlcsdm.eclipse.fixcnchar.listener;

import java.util.Map;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import com.tlcsdm.eclipse.fixcnchar.Activator;
import com.tlcsdm.eclipse.fixcnchar.preferences.FixCharPreferencePage;
import com.tlcsdm.eclipse.fixcnchar.util.RuleParser;

/**
 * 实时输入时的中文符号替换监听器 (支持光标位置、多字符替换、撤销/重做、规则热更新)
 */
public class FixCharVerifyListener implements VerifyKeyListener {

	private static final ILog LOG = Platform.getLog(FixCharVerifyListener.class);
	private static final FixCharVerifyListener INSTANCE = new FixCharVerifyListener();
	private volatile Map<String, String> rules;

	private FixCharVerifyListener() {
		rules = RuleParser.loadRules();

		// 监听 Preferences 改变，自动刷新规则
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.addPropertyChangeListener(event -> {
			if (FixCharPreferencePage.PREF_RULES.equals(event.getProperty())) {
				rules = RuleParser.loadRules();
			}
		});
	}

	public static FixCharVerifyListener getInstance() {
		return INSTANCE;
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
		String replacement = rules.get(input);
		if (replacement == null) {
			return;
		}

		event.doit = false; // 阻止原始输入

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			return;
		}

		IWorkbenchPage page = window.getActivePage();
		if (page == null) {
			return;
		}

		IEditorPart editor = page.getActiveEditor();
		if (!(editor instanceof ITextEditor textEditor)) {
			return;
		}

		IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		ISourceViewer viewer = textEditor.getAdapter(ISourceViewer.class);
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
			LOG.error("Error replacing text at offset " + offset, e);
		} finally {
			if (target != null) {
				target.endCompoundChange();
			}
		}
	}
}
