package com.tlcsdm.eclipse.fixcnchar;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.tlcsdm.eclipse.fixcnchar.listener.FixCharEditorAttacher;

public class Activator extends AbstractUIPlugin {
	/** The plug-in ID */
	public static final String PLUGIN_ID = "com.tlcsdm.eclipse.fixcnchar"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		PlatformUI.getWorkbench().getDisplay().asyncExec(FixCharEditorAttacher::attachToAllEditors);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
