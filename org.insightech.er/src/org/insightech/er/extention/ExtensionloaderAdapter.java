package org.insightech.er.extention;

import java.util.List;
import java.util.Map;

import org.eclipse.gef.ui.actions.ActionRegistry;
import org.insightech.er.editor.ERDiagramEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;

/**
 * ExtensionLoaderとERDaiagramEditorのアダプタ
 * @author 小川
 *
 */
public class ExtensionloaderAdapter {
	Extensionloader loader;
	
	public ExtensionloaderAdapter() {
		loader = new Extensionloader();
		loader.loadExtensions();
	}
	
	/**
	 * ExtensionLoaderから読み込んだクラスをERDiagramEditorのActionRegisterに追加する
	 * @param editor 追加されるERDiagramEditor
	 * @param registry ActionRegister
	 * @param selectionActionList 追加されるActionのIDリスト
	 */
	public void addActions(ERDiagramEditor editor,
			ActionRegistry registry,
			List<String> selectionActionList ){
		Map objMap = loader.getObjMap();
		List nameList = loader.getNameList();
		
		for(int i=0; i < nameList.size(); i++){
			IExtendAction exaction = (IExtendAction) objMap.get(nameList.get(i));
			
			IAction action = exaction.createIAction(editor, (String) nameList.get(i));
			selectionActionList.add(action.getId());
			registry.registerAction(action);			
		}
	}
	
	/**
	 * MenuManagerにExtentionLoaderから読み込んだクラスを追加
	 * @param menuMgr 追加されるポップアップメニューのマネジャー
	 * @param actionregistry ポップアップメニューに追加するアクションを読み込む
	 */
	public void addERDiagramPopupMenu(MenuManager menuMgr,
			ActionRegistry actionregistry){
		List nameList = loader.getNameList();
		Map pathMap = loader.getPathMap();
		Map objMap = loader.getObjMap();
		for(int i=0;i< nameList.size(); i++){
			try{
				IAction action = actionregistry.getAction(
						((IExtendAction) objMap.get(nameList.get(i))).getId());
				menuMgr.findMenuUsingPath((String) pathMap.get(nameList.get(i))).add(action);
			}catch(NullPointerException e){
				IAction action = actionregistry.getAction(
						((IExtendAction) objMap.get(nameList.get(i))).getId());
				menuMgr.add(action);							
			}
		}
	}	
	
	public Extensionloader getLoader(){
		return loader;
	}

}
