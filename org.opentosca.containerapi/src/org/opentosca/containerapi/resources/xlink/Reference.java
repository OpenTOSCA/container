package org.opentosca.containerapi.resources.xlink;

/**
 * Provides xml for XLink-References.<br>
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Markus Fischer - fischema@studi.informatik.uni-stuttgart.de
 */
public class Reference {
	
	private String absPath = null;
	private String xtype = null;
	private String xhref = null;
	private String xtitle = null;
	
	
	public Reference() {
	}
	
	public Reference(String xhref, String xtype, String xtitle) {
		this.setXhref(xhref);
		this.setXtitle(xtitle);
		this.setXtype(xtype);
	}
	
	public Reference(String xhref, String xtype, String xtitle, String absPath) {
		this.setXhref(xhref);
		this.setXtitle(xtitle);
		this.setXtype(xtype);
		this.setAbsPath(absPath);
	}
	
	/**
	 * @return the absPath
	 */
	public String getAbsPath() {
		return this.absPath;
	}
	
	/**
	 * @param absPath the absPath to set
	 */
	public void setAbsPath(String absPath) {
		this.absPath = absPath;
	}
	
	public String getXtype() {
		if (this.xtype == null) {
			return "";
		}
		return this.xtype;
	}
	
	public void setXtype(String type) {
		this.xtype = type;
	}
	
	public String getXhref() {
		if (this.xhref == null) {
			return "";
		}
		return this.xhref;
	}
	
	public void setXhref(String xhref) {
		this.xhref = xhref;
	}
	
	public String getXtitle() {
		if (this.xtitle == null) {
			return "";
		}
		return this.xtitle;
	}
	
	public void setXtitle(String xtitle) {
		this.xtitle = xtitle;
	}
	
	public String toXml() {
		StringBuilder xml = new StringBuilder("");
		xml.append("<");
		xml.append(XLinkConstants.REFERENCE);
		xml.append(" ");
		
		if (this.getAbsPath() != null) {
			xml.append(XLinkConstants.ABSPATH);
			xml.append("=\"");
			xml.append(this.getAbsPath());
			xml.append("\" ");
		}
		
		xml.append(XLinkConstants.XTYPE);
		xml.append("=\"");
		xml.append(this.getXtype());
		xml.append("\" ");
		xml.append(XLinkConstants.XHREF);
		xml.append("=\"");
		xml.append(this.getXhref());
		xml.append("\" ");
		xml.append(XLinkConstants.XTITLE);
		xml.append("=\"");
		xml.append(this.getXtitle());
		xml.append("\" ");
		xml.append("/>");
		return xml.toString();
		
	}
	
}