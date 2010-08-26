package org.insightech.er.extention;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * plugin.xmlからタグの読み込みを行う。 
 * @author insight
 *
 */
public class Extensionloader {
	
	/**
	 * 拡張ポイントの名前、poppuMenuPoint.exsd
	 */
	public static final String EXTENSION_POINT_ID = "org.insightech.er.popupMenuPoint";
	/**
	 * 拡張ポイントのelementの名前
	 */
	public static final String EXTENSION_NAME = "extendPopupmenu";
	/**
	 * 追加するグループの位置を指定するAttribute自体の名前
	 */
	public static final String ATTRIBUTE_PATH = "popupMenuPath";
	/**
	 *　追加するアクションの名前を指定するAttribute自体の名前
	 */
	public static final String ATTRIBUTE_NAME = "popupMenuName";
	/**
	 * 追加するアクションのクラスを指定するAttribute自体の名前
	 */
	public static final String ATTRIBUTE_CLASS = "class";
	
	// 追加するポップアップメニュー項目のアクションおよび、その配置の保持
	List NameList = new ArrayList();
	Map ObjMap = new HashMap();
	Map PathMap = new HashMap();
	
	/**
	 * plugin.xmlからタグを読み込む
	 */
	public void loadExtensions(){
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint(EXTENSION_POINT_ID);

		if (point == null) return;
		
		IExtension[] extensions = point.getExtensions();
		for (int i = 0; i < extensions.length; i++){
			IConfigurationElement[] cfgElems = extensions[i].getConfigurationElements();
			for (int j = 0; j < cfgElems.length; j++){
				IConfigurationElement cfgElem = cfgElems[j];				
				if (EXTENSION_NAME.equals(cfgElem.getName())){
					processpopupmenu(cfgElem);
				}
			}
		}

	}

	/**
	 * 読み込んだタグをマップに保持
	 * @param cfgElem
	 */
	@SuppressWarnings("unchecked")
	private void processpopupmenu(IConfigurationElement cfgElem) {
		// TODO Auto-generated method stub
		try{
			String path = cfgElem.getAttribute(ATTRIBUTE_PATH);
			String name = cfgElem.getAttribute(ATTRIBUTE_NAME);
			Object obj = cfgElem.createExecutableExtension(ATTRIBUTE_CLASS);
			if ( obj instanceof IExtendAction){
				NameList.add(name);
				PathMap.put(name, path);
				ObjMap.put(name, obj);
			}
		}
		catch(CoreException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * @return　plugin.xmlから読み込んだクラスのマップ
	 */
	public Map getObjMap(){
		return ObjMap;
	}
	
	/**
	 * 
	 * @return plugin.xmlから読み込んだNameのリスト（MapのIndexになっている）
	 */
	public List getNameList(){
		return NameList;
	}
	
	/**
	 * 
	 * @return plugin.xmlから読み込んだパスのマップ
	 */
	public Map getPathMap(){
		return PathMap;
	}
}
