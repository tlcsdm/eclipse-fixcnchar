package com.tlcsdm.eclipse.fixcnchar.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.tlcsdm.eclipse.fixcnchar.Activator;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		String rules = "，=,\\n。=.\\n；=;\\n：=:\\n“=\"\\n”=\"\\n‘='\\n’='\\n（=(\\n）=)\\n【=[\\n】=]\\n《=<\\n》=>";
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(FixCharPreferencePage.PREF_ENABLE_REALTIME, true);
		store.setDefault(FixCharPreferencePage.PREF_RULES, rules);
	}
}
