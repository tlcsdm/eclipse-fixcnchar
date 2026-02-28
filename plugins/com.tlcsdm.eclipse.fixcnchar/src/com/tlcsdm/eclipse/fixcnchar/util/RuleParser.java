package com.tlcsdm.eclipse.fixcnchar.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;

import com.tlcsdm.eclipse.fixcnchar.Activator;
import com.tlcsdm.eclipse.fixcnchar.preferences.FixCharPreferencePage;

/**
 * Shared utility for parsing replacement rules from the preference store.
 */
public final class RuleParser {

	private RuleParser() {
	}

	/**
	 * Load replacement rules from the preference store.
	 *
	 * @return an ordered map of replacement rules (source → target)
	 */
	public static Map<String, String> loadRules() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String rulesStr = store.getString(FixCharPreferencePage.PREF_RULES);
		return parseRules(rulesStr);
	}

	/**
	 * Parse replacement rules from a raw preference string.
	 * <p>
	 * Handles both literal {@code \n} (from default preferences) and actual
	 * newline characters (from user-saved preferences).
	 *
	 * @param rulesStr the raw rules string
	 * @return an ordered map of replacement rules (source → target)
	 */
	public static Map<String, String> parseRules(String rulesStr) {
		Map<String, String> map = new LinkedHashMap<>();
		if (rulesStr == null || rulesStr.isEmpty()) {
			return map;
		}
		// Normalize literal "\n" to actual newline, then split
		String normalized = rulesStr.replace("\\n", "\n");
		String[] lines = normalized.split("\n");
		for (String line : lines) {
			if (line.contains("=")) {
				String[] parts = line.split("=", 2);
				if (parts.length == 2) {
					map.put(parts[0].trim(), parts[1].trim());
				}
			}
		}
		return map;
	}
}
