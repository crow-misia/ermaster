package org.insightech.er.extention;

import org.eclipse.jface.action.IAction;
import org.insightech.er.editor.ERDiagramEditor;

/**
 * 拡張ポイントから読み込むクラスのインターフェイス
 */
public interface IExtendAction {

	/**
	 * IActionを実装したクラスを返す
	 * 
	 * @param editor
	 * @param name
	 * @return
	 */
	public IAction createIAction(ERDiagramEditor editor, String name);

	/**
	 * ActionRegisterからIActionを取り出すときのIDを返す
	 * 
	 * @return ActionRegisterで用いられるID
	 */
	public String getId();

}
