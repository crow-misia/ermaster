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
 */
public class ExtensionLoader {

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
	 *　追加するアクションの名前を指定する Attribute の名前
	 */
	public static final String ATTRIBUTE_NAME = "popupMenuName";

	/**
	 * 追加するアクションのクラスを指定する Attribute の名前
	 */
	public static final String ATTRIBUTE_CLASS = "class";

	// 追加するポップアップメニュー項目のアクションおよび、その配置の保持
	private List<String> nameList = new ArrayList<String>();
	private Map<String, IExtendAction> objMap = new HashMap<String, IExtendAction>();
	private Map<String, String> pathMap = new HashMap<String, String>();

	/**
	 * plugin.xmlからタグを読み込む.
	 * 
	 * @throws CoreException
	 */
	public void loadExtensions() throws CoreException {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint(EXTENSION_POINT_ID);

		if (point == null) {
			return;
		}

		for (IExtension extension : point.getExtensions()) {
			for (IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {
				if (EXTENSION_NAME.equals(configurationElement.getName())) {
					this.processpopupmenu(configurationElement);
				}
			}
		}

	}

	/**
	 * 読み込んだタグをマップに保持
	 * 
	 * @param cfgElem
	 * @throws CoreException
	 */
	private void processpopupmenu(IConfigurationElement configurationElement)
			throws CoreException {
		String path = configurationElement.getAttribute(ATTRIBUTE_PATH);
		String name = configurationElement.getAttribute(ATTRIBUTE_NAME);
		Object obj = configurationElement
				.createExecutableExtension(ATTRIBUTE_CLASS);

		if (obj instanceof IExtendAction) {
			this.nameList.add(name);
			this.pathMap.put(name, path);
			this.objMap.put(name, (IExtendAction) obj);
		}
	}

	/**
	 * @return　plugin.xmlから読み込んだクラスのマップ
	 */
	public Map<String, IExtendAction> getObjMap() {
		return objMap;
	}

	/**
	 * 
	 * @return plugin.xmlから読み込んだNameのリスト（MapのIndexになっている）
	 */
	public List<String> getNameList() {
		return nameList;
	}

	/**
	 * 
	 * @return plugin.xmlから読み込んだパスのマップ
	 */
	public Map<String, String> getPathMap() {
		return pathMap;
	}
}
