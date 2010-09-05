package org.insightech.er.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.insightech.er.Activator;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.settings.TranslationSetting;
import org.insightech.er.preference.PreferenceInitializer;

public class TranslationResources {

	private Map<String, String> translationMap;

	public TranslationResources(TranslationSetting translationSettings) {
		this.translationMap = new LinkedHashMap<String, String>();

		String defaultFileName = ResourceString
				.getResourceString("label.translation.default");

		if (translationSettings.isUse()) {
			for (String translation : translationSettings
					.getAllUserTranslations()) {
				if (translationSettings.isSelected(translation)) {
					File file = new File(PreferenceInitializer
							.getTranslationPath(translation));

					if (file.exists()) {
						FileInputStream in = null;

						try {
							in = new FileInputStream(file);
							load(in);

						} catch (IOException e) {
							Activator.showExceptionDialog(e);

						} finally {
							if (in != null) {
								try {
									in.close();
								} catch (IOException e) {
									Activator.showExceptionDialog(e);
								}
							}
						}
					}

				}
			}

			if (translationSettings.isSelected(defaultFileName)) {
				InputStream in = this.getClass().getResourceAsStream(
						"/translation.txt");
				try {
					load(in);

				} catch (IOException e) {
					Activator.showExceptionDialog(e);

				} finally {
					try {
						in.close();
					} catch (IOException e) {
						Activator.showExceptionDialog(e);
					}
				}

			}
		}
	}

	private void load(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in,
				"UTF-8"));

		String line = null;

		while ((line = reader.readLine()) != null) {
			int index = line.indexOf(",");
			if (index == -1 || index == line.length() - 1) {
				continue;
			}

			String key = line.substring(0, index).trim();
			if ("".equals(key)) {
				continue;
			}

			String value = line.substring(index + 1).trim();
			this.translationMap.put(key, value);
			this.translationMap.put(key.replaceAll("[aiueo]", ""), value);
		}
	}

	/**
	 * ERDiagram.properties の指定されたキーに対応する値を返します
	 * 
	 * @param key
	 *            ERDiagram.properties で定義されたキー
	 * @return ERDiagram.properties の指定されたキーに対応する値
	 */
	public String translate(String str) {
		for (Entry<String, String> entry : translationMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			str = str.replaceAll("\\_*" + key + "\\_*", value);
			str = str.replaceAll("\\_*" + key.toUpperCase() + "\\_*", value);
		}

		return str;
	}

	public boolean contains(String key) {
		return this.translationMap.containsKey(key);
	}
}
