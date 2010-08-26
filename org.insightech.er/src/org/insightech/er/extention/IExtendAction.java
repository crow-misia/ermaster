package org.insightech.er.extention;

import org.eclipse.jface.action.IAction;
import org.insightech.er.editor.ERDiagramEditor;

/**
 * 拡張ポイントから読み込むクラスのインターフェイス
 * @author insight
 *
 */
public interface IExtendAction {
	
	/**
	 * IActionを実装したクラスを返す
	 * @param editor
	 * @param name
	 * @return
	 */
	public IAction createIAction(ERDiagramEditor editor, String name);
	
	/**
	 * ActionRegisterからIActionを取り出すときのIndexを返す
	 * @return ActionRegisterで用いられるIndex
	 */
	public String getId();
	
}
