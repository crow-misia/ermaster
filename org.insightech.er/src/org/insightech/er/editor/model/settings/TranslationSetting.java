package org.insightech.er.editor.model.settings;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.insightech.er.Activator;
import org.insightech.er.ResourceString;
import org.insightech.er.preference.PreferenceInitializer;

public class TranslationSetting implements Serializable, Cloneable {

	private static final long serialVersionUID = -7691417386790834828L;

	private List<String> selectedTranslations;

	private boolean use;

	public TranslationSetting() {
		this.selectedTranslations = new ArrayList<String>();
	}

	/**
	 * allTranslations ‚ğæ“¾‚µ‚Ü‚·.
	 * 
	 * @return allTranslations
	 */
	public List<String> getAllTranslations() {
		List<String> list = getAllUserTranslations();

		list.add(ResourceString.getResourceString("label.translation.default"));

		return list;
	}

	/**
	 * allTranslations ‚ğæ“¾‚µ‚Ü‚·.
	 * 
	 * @return allTranslations
	 */
	public List<String> getAllUserTranslations() {
		String str = Activator.getDefault().getPreferenceStore().getString(
				PreferenceInitializer.TRANSLATION_FILE_LIST);

		StringTokenizer st = new StringTokenizer(str, "/");
		List<String> list = new ArrayList<String>();
		Set<String> names = new HashSet<String>();

		while (st.hasMoreElements()) {
			String fileName = st.nextToken();

			File file = new File(PreferenceInitializer
					.getTranslationPath(fileName));
			if (file.exists() && !names.contains(fileName)) {
				list.add(fileName);
				names.add(fileName);
			}
		}

		return list;
	}

	/**
	 * selectedTranslations ‚ğæ“¾‚µ‚Ü‚·.
	 * 
	 * @return selectedTranslations
	 */
	public List<String> getSelectedTranslations() {
		return selectedTranslations;
	}

	/**
	 * selectedTranslations ‚ğİ’è‚µ‚Ü‚·.
	 * 
	 * @param selectedTranslations
	 *            selectedTranslations
	 */
	public void setSelectedTranslations(List<String> selectedTranslations) {
		this.selectedTranslations = selectedTranslations;
	}

	/**
	 * selectedTranslations ‚ğİ’è‚µ‚Ü‚·.
	 * 
	 * @param selectedTranslations
	 *            selectedTranslations
	 */
	public void selectDefault() {
		this.selectedTranslations.add(ResourceString
				.getResourceString("label.translation.default"));
	}

	/**
	 * use ‚ğæ“¾‚µ‚Ü‚·.
	 * 
	 * @return use
	 */
	public boolean isUse() {
		return use;
	}

	/**
	 * use ‚ğİ’è‚µ‚Ü‚·.
	 * 
	 * @param use
	 *            use
	 */
	public void setUse(boolean use) {
		this.use = use;
	}

	public boolean isSelected(String translationName) {
		for (String translation : this.selectedTranslations) {
			if (translation.equals(translationName)) {
				return true;
			}
		}

		return false;
	}

	public void addTranslationAsSelected(String translation) {
		this.selectedTranslations.add(translation);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object clone() {
		try {
			TranslationSetting settings = (TranslationSetting) super.clone();
			settings.selectedTranslations = new ArrayList<String>();

			for (String selectedTranslation : this.selectedTranslations) {
				settings.selectedTranslations.add(selectedTranslation);
			}

			return settings;

		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public void load() {
	}

	protected void parseString(String stringList) {
	}
}
