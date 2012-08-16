package org.insightech.er.editor.view.action.option.notation;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.LabelRetargetAction;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.AbstractBaseAction;

public class GridSnapAction extends AbstractBaseAction {

	public static final String ID = TooltipAction.class.getName();

	public GridSnapAction(ERDiagramEditor editor) {
		super(ID, null, IAction.AS_CHECK_BOX, editor);
		this.setText(ResourceString
				.getResourceString("action.title.display.grid.snap"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Event event) {
		ERDiagram diagram = this.getDiagram();

	}

	public static class GridSnapRetargetAction extends LabelRetargetAction {
		public GridSnapRetargetAction() {
			super(ID, ResourceString
					.getResourceString("action.title.grid.snap"));

			this.setImageDescriptor(Activator
					.getImageDescriptor(ImageKey.GRID_SNAP));
			this.setToolTipText(ResourceString
					.getResourceString("action.title.grid.snap"));
		}
	}
}
