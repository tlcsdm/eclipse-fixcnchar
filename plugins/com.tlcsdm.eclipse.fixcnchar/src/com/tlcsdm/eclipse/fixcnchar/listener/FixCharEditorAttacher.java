package com.tlcsdm.eclipse.fixcnchar.listener;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * 将 FixCharVerifyListener 挂到所有 ITextEditor 的 StyledText 上， 实现实时输入替换功能。
 */
public class FixCharEditorAttacher {

	private static final FixCharVerifyListener listener = FixCharVerifyListener.getInstance();
	private static final String LISTENER_KEY = "fixcnchar.listener.attached";

	/**
	 * 给当前已打开的编辑器挂监听器，并注册 PartListener 监听未来新开的编辑器
	 */
	public static void attachToAllEditors() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null)
			return;

		IWorkbenchPage page = window.getActivePage();
		if (page == null)
			return;

		// 1. 给当前已打开的所有编辑器挂上
		for (IEditorReference ref : page.getEditorReferences()) {
			IEditorPart editor = ref.getEditor(false);
			attachToEditor(editor);
		}

		// 2. 监听后续新打开的编辑器
		page.addPartListener(new IPartListener2() {
			@Override
			public void partOpened(IWorkbenchPartReference partRef) {
				if (partRef instanceof IEditorReference) {
					IEditorPart editor = ((IEditorReference) partRef).getEditor(false);
					attachToEditor(editor);
				}
			}

			@Override
			public void partActivated(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partBroughtToTop(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partClosed(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partDeactivated(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partHidden(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partVisible(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partInputChanged(IWorkbenchPartReference partRef) {
			}
		});
	}

	/**
	 * 给单个编辑器挂监听器
	 */
	private static void attachToEditor(IEditorPart editor) {
		if (editor instanceof ITextEditor) {
			ISourceViewer viewer = (ISourceViewer) ((ITextEditor) editor).getAdapter(ITextViewer.class);
			if (viewer != null) {
				StyledText textWidget = viewer.getTextWidget();
				if (textWidget != null && !textWidget.isDisposed()) {
					// 避免重复添加
					if (textWidget.getData(LISTENER_KEY) == null) {
						textWidget.addVerifyKeyListener(listener);
						textWidget.setData(LISTENER_KEY, Boolean.TRUE);
					}
				}
			}
		}
	}
}