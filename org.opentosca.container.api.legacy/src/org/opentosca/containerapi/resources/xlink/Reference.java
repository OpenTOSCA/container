package org.opentosca.containerapi.resources.xlink;

import com.google.gson.JsonObject;

/**
 * Provides xml for XLink-References.<br>
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Markus Fischer - fischema@studi.informatik.uni-stuttgart.de
 * @author Christian Endres - christian.endres@iaas.uni-stuttgart.de
 */
public class Reference {
	
	
	private String absPath = null;
	private String xtype = null;
	private String xhref = null;
	private String xtitle = null;
	
	
	public Reference() {
	}
	
	public Reference(String xhref, String xtype, String xtitle) {
		setXhref(xhref);
		setXtitle(xtitle);
		setXtype(xtype);
	}
	
	public Reference(String xhref, String xtype, String xtitle, String absPath) {
		setXhref(xhref);
		setXtitle(xtitle);
		setXtype(xtype);
		setAbsPath(absPath);
	}
	
	/**
	 * @return the absPath
	 */
	public String getAbsPath() {
		return absPath;
	}
	
	/**
	 * @param absPath the absPath to set
	 */
	public void setAbsPath(String absPath) {
		this.absPath = absPath;
	}
	
	public String getXtype() {
		if (xtype == null) {
			return "";
		}
		return xtype;
	}
	
	public void setXtype(String type) {
		xtype = type;
	}
	
	public String getXhref() {
		if (xhref == null) {
			return "";
		}
		return xhref;
	}
	
	public void setXhref(String xhref) {
		this.xhref = xhref;
	}
	
	public String getXtitle() {
		if (xtitle == null) {
			return "";
		}
		return xtitle;
	}
	
	public void setXtitle(String xtitle) {
		this.xtitle = xtitle;
	}
	
	public String toXml() {
		StringBuilder xml = new StringBuilder("");
		xml.append("<");
		xml.append(XLinkConstants.REFERENCE);
		xml.append(" ");
		
		if (getAbsPath() != null) {
			xml.append(XLinkConstants.ABSPATH);
			xml.append("=\"");
			xml.append(getAbsPath());
			xml.append("\" ");
		}
		
		xml.append(XLinkConstants.XTYPE);
		xml.append("=\"");
		xml.append(getXtype());
		xml.append("\" ");
		xml.append(XLinkConstants.XHREF);
		xml.append("=\"");
		xml.append(getXhref());
		xml.append("\" ");
		xml.append(XLinkConstants.XTITLE);
		xml.append("=\"");
		xml.append(getXtitle());
		xml.append("\" ");
		xml.append("/>");
		return xml.toString();
		
	}
	
	public JsonObject toJson() {
		
		JsonObject ref = new JsonObject();
		ref.addProperty("type", getXtype());
		ref.addProperty("href", getXhref());
		ref.addProperty("title", getXtitle());
		
		return ref;
	}
	
}