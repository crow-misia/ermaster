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
 * @author insight
 *
 */
public class Extensionloader {
	
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
	 *�@�ǉ�����A�N�V�����̖��O���w�肷��Attribute���̖̂��O
	 */
	public static final String ATTRIBUTE_NAME = "popupMenuName";
	/**
	 * �ǉ�����A�N�V�����̃N���X���w�肷��Attribute���̖̂��O
	 */
	public static final String ATTRIBUTE_CLASS = "class";
	
	// �ǉ�����|�b�v�A�b�v���j���[���ڂ̃A�N�V��������сA���̔z�u�̕ێ�
	List NameList = new ArrayList();
	Map ObjMap = new HashMap();
	Map PathMap = new HashMap();
	
	/**
	 * plugin.xml����^�O��ǂݍ���
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
	 * �ǂݍ��񂾃^�O���}�b�v�ɕێ�
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
	 * @return�@plugin.xml����ǂݍ��񂾃N���X�̃}�b�v
	 */
	public Map getObjMap(){
		return ObjMap;
	}
	
	/**
	 * 
	 * @return plugin.xml����ǂݍ���Name�̃��X�g�iMap��Index�ɂȂ��Ă���j
	 */
	public List getNameList(){
		return NameList;
	}
	
	/**
	 * 
	 * @return plugin.xml����ǂݍ��񂾃p�X�̃}�b�v
	 */
	public Map getPathMap(){
		return PathMap;
	}
}
