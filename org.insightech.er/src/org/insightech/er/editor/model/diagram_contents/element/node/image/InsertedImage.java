package org.insightech.er.editor.model.diagram_contents.element.node.image;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.codec.binary.Base64;
import org.insightech.er.Activator;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.util.io.IOUtils;

public class InsertedImage extends NodeElement {

	private static final long serialVersionUID = -2035035973213266486L;

	private String base64EncodedData;

	public String getBase64EncodedData() {
		return base64EncodedData;
	}

	public void setBase64EncodedData(String base64EncodedData) {
		this.base64EncodedData = base64EncodedData;
	}

	public String getDescription() {
		return null;
	}

	public String getName() {
		return null;
	}

	public String getObjectType() {
		return "image";
	}

	public void setImageFilePath(String imageFilePath) {
		InputStream in = null;

		try {
			in = new BufferedInputStream(new FileInputStream(imageFilePath));

			byte[] data = IOUtils.toByteArray(in);

			String encodedData = new String(Base64.encodeBase64(data));
			this.setBase64EncodedData(encodedData);

		} catch (Exception e) {
			Activator.showExceptionDialog(e);

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					Activator.showExceptionDialog(e);
				}
			}
		}
	}
}
