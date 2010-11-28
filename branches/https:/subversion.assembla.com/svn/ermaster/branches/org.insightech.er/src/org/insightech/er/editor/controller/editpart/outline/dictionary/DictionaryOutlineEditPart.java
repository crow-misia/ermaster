package org.insightech.er.editor.controller.editpart.outline.dictionary;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;

public class DictionaryOutlineEditPart extends AbstractOutlineEditPart {

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Dictionary.PROPERTY_CHANGE_DICTIONARY)) {
			refresh();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List getModelChildren() {
		Dictionary dictionary = (Dictionary) this.getModel();
		List<Word> list = dictionary.getWordList();

		Collections.sort(list);

		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refreshOutlineVisuals() {
		this
				.setWidgetText(ResourceString
						.getResourceString("label.dictionary"));
		this.setWidgetImage(Activator.getImage(ImageKey.DICTIONARY));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refreshChildren() {
		super.refreshChildren();

		for (Object child : this.getChildren()) {
			EditPart part = (EditPart) child;
			part.refresh();
		}
	}

}
