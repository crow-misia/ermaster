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
 * plugin.xml����^�O�̓ǂݍ��݂��s���B
 */
public class ExtensionLoader {

	/**
	 * �g���|�C���g�̖��O�ApoppuMenuPoint.exsd
	 */
	public static final String EXTENSION_POINT_ID = "org.insightech.er.popupMenuPoint";

	/**
	 * �g���|�C���g��element�̖��O
	 */
	public static final String EXTENSION_NAME = "extendPopupmenu";

	/**
	 * �ǉ�����O���[�v�̈ʒu���w�肷��Attribute���̖̂��O
	 */
	public static final String ATTRIBUTE_PATH = "popupMenuPath";

	/**
	 *�@�ǉ�����A�N�V�����̖��O���w�肷�� Attribute �̖��O
	 */
	public static final String ATTRIBUTE_NAME = "popupMenuName";

	/**
	 * �ǉ�����A�N�V�����̃N���X���w�肷�� Attribute �̖��O
	 */
	public static final String ATTRIBUTE_CLASS = "class";

	// �ǉ�����|�b�v�A�b�v���j���[���ڂ̃A�N�V��������сA���̔z�u�̕ێ�
	private List<String> nameList = new ArrayList<String>();
	private Map<String, IExtendAction> objMap = new HashMap<String, IExtendAction>();
	private Map<String, String> pathMap = new HashMap<String, String>();

	/**
	 * plugin.xml����^�O��ǂݍ���.
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
	 * �ǂݍ��񂾃^�O���}�b�v�ɕێ�
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
	 * @return�@plugin.xml����ǂݍ��񂾃N���X�̃}�b�v
	 */
	public Map<String, IExtendAction> getObjMap() {
		return objMap;
	}

	/**
	 * 
	 * @return plugin.xml����ǂݍ���Name�̃��X�g�iMap��Index�ɂȂ��Ă���j
	 */
	public List<String> getNameList() {
		return nameList;
	}

	/**
	 * 
	 * @return plugin.xml����ǂݍ��񂾃p�X�̃}�b�v
	 */
	public Map<String, String> getPathMap() {
		return pathMap;
	}
}
