package org.insightech.er.editor.model.settings;

import java.io.Serializable;

import org.insightech.er.editor.model.dbexport.ddl.DDLTarget;

public class ExportSetting implements Serializable, Cloneable {

	private static final long serialVersionUID = 3669486436464233526L;

	private String excelTemplate;

	private String excelOutput;

	private String imageOutput;

	private String ddlOutput;

	private String javaOutput;

	private String packageName;

	private String classNameSuffix;

	private String srcFileEncoding;

	private boolean useLogicalNameAsSheet;

	private boolean putERDiagramOnExcel;

	private String categoryNameToExport;

	private DDLTarget ddlTarget = new DDLTarget();

	public DDLTarget getDdlTarget() {
		return ddlTarget;
	}

	public void setDdlTarget(DDLTarget ddlTarget) {
		this.ddlTarget = ddlTarget;
	}

	/**
	 * excelOutput を取得します.
	 * 
	 * @return excelOutput
	 */
	public String getExcelOutput() {
		return excelOutput;
	}

	/**
	 * excelOutput を設定します.
	 * 
	 * @param excelOutput
	 *            excelOutput
	 */
	public void setExcelOutput(String excelOutput) {
		this.excelOutput = excelOutput;
	}

	/**
	 * imageOutput を取得します.
	 * 
	 * @return imageOutput
	 */
	public String getImageOutput() {
		return imageOutput;
	}

	/**
	 * imageOutput を設定します.
	 * 
	 * @param imageOutput
	 *            imageOutput
	 */
	public void setImageOutput(String imageOutput) {
		this.imageOutput = imageOutput;
	}

	/**
	 * excelTemplate を取得します.
	 * 
	 * @return excelTemplate
	 */
	public String getExcelTemplate() {
		return excelTemplate;
	}

	/**
	 * excelTemplate を設定します.
	 * 
	 * @param excelTemplate
	 *            excelTemplate
	 */
	public void setExcelTemplate(String excelTemplate) {
		this.excelTemplate = excelTemplate;
	}

	/**
	 * useLogicalNameAsSheet を取得します.
	 * 
	 * @return useLogicalNameAsSheet
	 */
	public boolean isUseLogicalNameAsSheet() {
		return useLogicalNameAsSheet;
	}

	/**
	 * useLogicalNameAsSheet を設定します.
	 * 
	 * @param useLogicalNameAsSheet
	 *            useLogicalNameAsSheet
	 */
	public void setUseLogicalNameAsSheet(boolean useLogicalNameAsSheet) {
		this.useLogicalNameAsSheet = useLogicalNameAsSheet;
	}

	/**
	 * putERDiagramOnExcel を取得します.
	 * 
	 * @return putERDiagramOnExcel
	 */
	public boolean isPutERDiagramOnExcel() {
		return putERDiagramOnExcel;
	}

	/**
	 * putERDiagramOnExcel を設定します.
	 * 
	 * @param putERDiagramOnExcel
	 *            putERDiagramOnExcel
	 */
	public void setPutERDiagramOnExcel(boolean putERDiagramOnExcel) {
		this.putERDiagramOnExcel = putERDiagramOnExcel;
	}

	/**
	 * categoryNameToExportを取得します。
	 * 
	 * @return categoryNameToExport
	 */
	public String getCategoryNameToExport() {
		return categoryNameToExport;
	}

	/**
	 * categoryNameToExportを設定します。
	 * 
	 * @param categoryNameToExport
	 *            categoryNameToExport
	 */
	public void setCategoryNameToExport(String categoryNameToExport) {
		this.categoryNameToExport = categoryNameToExport;
	}

	public String getDdlOutput() {
		return ddlOutput;
	}

	public void setDdlOutput(String ddlOutput) {
		this.ddlOutput = ddlOutput;
	}

	public String getJavaOutput() {
		return javaOutput;
	}

	public void setJavaOutput(String javaOutput) {
		this.javaOutput = javaOutput;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getClassNameSuffix() {
		return classNameSuffix;
	}

	public void setClassNameSuffix(String classNameSuffix) {
		this.classNameSuffix = classNameSuffix;
	}

	public String getSrcFileEncoding() {
		return srcFileEncoding;
	}

	public void setSrcFileEncoding(String srcFileEncoding) {
		this.srcFileEncoding = srcFileEncoding;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExportSetting other = (ExportSetting) obj;
		if (categoryNameToExport == null) {
			if (other.categoryNameToExport != null)
				return false;
		} else if (!categoryNameToExport.equals(other.categoryNameToExport))
			return false;
		if (classNameSuffix == null) {
			if (other.classNameSuffix != null)
				return false;
		} else if (!classNameSuffix.equals(other.classNameSuffix))
			return false;
		if (ddlOutput == null) {
			if (other.ddlOutput != null)
				return false;
		} else if (!ddlOutput.equals(other.ddlOutput))
			return false;
		if (ddlTarget == null) {
			if (other.ddlTarget != null)
				return false;
		} else if (!ddlTarget.equals(other.ddlTarget))
			return false;
		if (excelOutput == null) {
			if (other.excelOutput != null)
				return false;
		} else if (!excelOutput.equals(other.excelOutput))
			return false;
		if (excelTemplate == null) {
			if (other.excelTemplate != null)
				return false;
		} else if (!excelTemplate.equals(other.excelTemplate))
			return false;
		if (imageOutput == null) {
			if (other.imageOutput != null)
				return false;
		} else if (!imageOutput.equals(other.imageOutput))
			return false;
		if (javaOutput == null) {
			if (other.javaOutput != null)
				return false;
		} else if (!javaOutput.equals(other.javaOutput))
			return false;
		if (packageName == null) {
			if (other.packageName != null)
				return false;
		} else if (!packageName.equals(other.packageName))
			return false;
		if (putERDiagramOnExcel != other.putERDiagramOnExcel)
			return false;
		if (srcFileEncoding == null) {
			if (other.srcFileEncoding != null)
				return false;
		} else if (!srcFileEncoding.equals(other.srcFileEncoding))
			return false;
		if (useLogicalNameAsSheet != other.useLogicalNameAsSheet)
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExportSetting clone() {
		try {
			ExportSetting setting = (ExportSetting) super.clone();

			setting.setDdlTarget(this.ddlTarget.clone());

			return setting;

		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
