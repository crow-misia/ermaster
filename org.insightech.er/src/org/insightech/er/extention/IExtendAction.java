package org.insightech.er.extention;

import org.eclipse.jface.action.IAction;
import org.insightech.er.editor.ERDiagramEditor;

/**
 * �g���|�C���g����ǂݍ��ރN���X�̃C���^�[�t�F�C�X
 * @author insight
 *
 */
public interface IExtendAction {
	
	/**
	 * IAction�����������N���X��Ԃ�
	 * @param editor
	 * @param name
	 * @return
	 */
	public IAction createIAction(ERDiagramEditor editor, String name);
	
	/**
	 * ActionRegister����IAction�����o���Ƃ���Index��Ԃ�
	 * @return ActionRegister�ŗp������Index
	 */
	public String getId();
	
}
