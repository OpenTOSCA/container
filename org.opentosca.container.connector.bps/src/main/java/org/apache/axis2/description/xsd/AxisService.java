
/**
 * AxisService.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.apache.axis2.description.xsd;


/**
 * AxisService bean class
 */

public class AxisService implements org.apache.axis2.databinding.ADBBean {
  /*
   * This type was generated from the piece of schema that had name = AxisService Namespace URI =
   * http://description.axis2.apache.org/xsd Namespace Prefix = ns19
   */


  /**
   *
   */
  private static final long serialVersionUID = 4019102326459307370L;

  /**
   * field for EPRs This was an Array!
   */


  protected java.lang.String[] localEPRs;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localEPRsTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String[]
   */
  public java.lang.String[] getEPRs() {
    return this.localEPRs;
  }


  /**
   * validate the array for EPRs
   */
  protected void validateEPRs(final java.lang.String[] param) {

  }


  /**
   * Auto generated setter method
   *
   * @param param EPRs
   */
  public void setEPRs(final java.lang.String[] param) {

    validateEPRs(param);

    this.localEPRsTracker = true;

    this.localEPRs = param;
  }


  /**
   * Auto generated add method for the array for convenience
   *
   * @param param java.lang.String
   */
  public void addEPRs(final java.lang.String param) {
    if (this.localEPRs == null) {
      this.localEPRs = new java.lang.String[] {};
    }


    // update the setting tracker
    this.localEPRsTracker = true;


    final java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(this.localEPRs);
    list.add(param);
    this.localEPRs = (java.lang.String[]) list.toArray(new java.lang.String[list.size()]);

  }


  /**
   * field for WSAddressingFlag
   */


  protected java.lang.String localWSAddressingFlag;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localWSAddressingFlagTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getWSAddressingFlag() {
    return this.localWSAddressingFlag;
  }


  /**
   * Auto generated setter method
   *
   * @param param WSAddressingFlag
   */
  public void setWSAddressingFlag(final java.lang.String param) {
    this.localWSAddressingFlagTracker = true;

    this.localWSAddressingFlag = param;


  }


  /**
   * field for Active
   */


  protected boolean localActive;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localActiveTracker = false;


  /**
   * Auto generated getter method
   *
   * @return boolean
   */
  public boolean getActive() {
    return this.localActive;
  }


  /**
   * Auto generated setter method
   *
   * @param param Active
   */
  public void setActive(final boolean param) {

    // setting primitive attribute tracker to true
    this.localActiveTracker = true;

    this.localActive = param;


  }


  /**
   * field for AxisServiceGroup
   */


  protected org.apache.axis2.description.xsd.AxisServiceGroup localAxisServiceGroup;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localAxisServiceGroupTracker = false;


  /**
   * Auto generated getter method
   *
   * @return org.apache.axis2.description.xsd.AxisServiceGroup
   */
  public org.apache.axis2.description.xsd.AxisServiceGroup getAxisServiceGroup() {
    return this.localAxisServiceGroup;
  }


  /**
   * Auto generated setter method
   *
   * @param param AxisServiceGroup
   */
  public void setAxisServiceGroup(final org.apache.axis2.description.xsd.AxisServiceGroup param) {
    this.localAxisServiceGroupTracker = true;

    this.localAxisServiceGroup = param;


  }


  /**
   * field for BindingName
   */


  protected java.lang.String localBindingName;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localBindingNameTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getBindingName() {
    return this.localBindingName;
  }


  /**
   * Auto generated setter method
   *
   * @param param BindingName
   */
  public void setBindingName(final java.lang.String param) {
    this.localBindingNameTracker = true;

    this.localBindingName = param;


  }


  /**
   * field for ClassLoader
   */


  protected java.lang.Object localClassLoader;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localClassLoaderTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.Object
   */
  public java.lang.Object getClassLoader() {
    return this.localClassLoader;
  }


  /**
   * Auto generated setter method
   *
   * @param param ClassLoader
   */
  public void setClassLoader(final java.lang.Object param) {
    this.localClassLoaderTracker = true;

    this.localClassLoader = param;


  }


  /**
   * field for ClientSide
   */


  protected boolean localClientSide;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localClientSideTracker = false;


  /**
   * Auto generated getter method
   *
   * @return boolean
   */
  public boolean getClientSide() {
    return this.localClientSide;
  }


  /**
   * Auto generated setter method
   *
   * @param param ClientSide
   */
  public void setClientSide(final boolean param) {

    // setting primitive attribute tracker to true
    this.localClientSideTracker = true;

    this.localClientSide = param;


  }


  /**
   * field for ControlOperations
   */


  protected java.lang.Object localControlOperations;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localControlOperationsTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.Object
   */
  public java.lang.Object getControlOperations() {
    return this.localControlOperations;
  }


  /**
   * Auto generated setter method
   *
   * @param param ControlOperations
   */
  public void setControlOperations(final java.lang.Object param) {
    this.localControlOperationsTracker = true;

    this.localControlOperations = param;


  }


  /**
   * field for CustomSchemaNamePrefix
   */


  protected java.lang.String localCustomSchemaNamePrefix;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localCustomSchemaNamePrefixTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getCustomSchemaNamePrefix() {
    return this.localCustomSchemaNamePrefix;
  }


  /**
   * Auto generated setter method
   *
   * @param param CustomSchemaNamePrefix
   */
  public void setCustomSchemaNamePrefix(final java.lang.String param) {
    this.localCustomSchemaNamePrefixTracker = true;

    this.localCustomSchemaNamePrefix = param;


  }


  /**
   * field for CustomSchemaNameSuffix
   */


  protected java.lang.String localCustomSchemaNameSuffix;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localCustomSchemaNameSuffixTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getCustomSchemaNameSuffix() {
    return this.localCustomSchemaNameSuffix;
  }


  /**
   * Auto generated setter method
   *
   * @param param CustomSchemaNameSuffix
   */
  public void setCustomSchemaNameSuffix(final java.lang.String param) {
    this.localCustomSchemaNameSuffixTracker = true;

    this.localCustomSchemaNameSuffix = param;


  }


  /**
   * field for CustomWsdl
   */


  protected boolean localCustomWsdl;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localCustomWsdlTracker = false;


  /**
   * Auto generated getter method
   *
   * @return boolean
   */
  public boolean getCustomWsdl() {
    return this.localCustomWsdl;
  }


  /**
   * Auto generated setter method
   *
   * @param param CustomWsdl
   */
  public void setCustomWsdl(final boolean param) {

    // setting primitive attribute tracker to true
    this.localCustomWsdlTracker = true;

    this.localCustomWsdl = param;


  }


  /**
   * field for ElementFormDefault
   */


  protected boolean localElementFormDefault;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localElementFormDefaultTracker = false;


  /**
   * Auto generated getter method
   *
   * @return boolean
   */
  public boolean getElementFormDefault() {
    return this.localElementFormDefault;
  }


  /**
   * Auto generated setter method
   *
   * @param param ElementFormDefault
   */
  public void setElementFormDefault(final boolean param) {

    // setting primitive attribute tracker to true
    this.localElementFormDefaultTracker = true;

    this.localElementFormDefault = param;


  }


  /**
   * field for EnableAllTransports
   */


  protected boolean localEnableAllTransports;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localEnableAllTransportsTracker = false;


  /**
   * Auto generated getter method
   *
   * @return boolean
   */
  public boolean getEnableAllTransports() {
    return this.localEnableAllTransports;
  }


  /**
   * Auto generated setter method
   *
   * @param param EnableAllTransports
   */
  public void setEnableAllTransports(final boolean param) {

    // setting primitive attribute tracker to true
    this.localEnableAllTransportsTracker = true;

    this.localEnableAllTransports = param;


  }


  /**
   * field for EndpointName
   */


  protected java.lang.String localEndpointName;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localEndpointNameTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getEndpointName() {
    return this.localEndpointName;
  }


  /**
   * Auto generated setter method
   *
   * @param param EndpointName
   */
  public void setEndpointName(final java.lang.String param) {
    this.localEndpointNameTracker = true;

    this.localEndpointName = param;


  }


  /**
   * field for EndpointURL
   */


  protected java.lang.String localEndpointURL;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localEndpointURLTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getEndpointURL() {
    return this.localEndpointURL;
  }


  /**
   * Auto generated setter method
   *
   * @param param EndpointURL
   */
  public void setEndpointURL(final java.lang.String param) {
    this.localEndpointURLTracker = true;

    this.localEndpointURL = param;


  }


  /**
   * field for Endpoints
   */


  protected authclient.java.util.xsd.Map localEndpoints;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localEndpointsTracker = false;


  /**
   * Auto generated getter method
   *
   * @return authclient.java.util.xsd.Map
   */
  public authclient.java.util.xsd.Map getEndpoints() {
    return this.localEndpoints;
  }


  /**
   * Auto generated setter method
   *
   * @param param Endpoints
   */
  public void setEndpoints(final authclient.java.util.xsd.Map param) {
    this.localEndpointsTracker = true;

    this.localEndpoints = param;


  }


  /**
   * field for ExcludeInfo
   */


  protected org.apache.axis2.deployment.util.xsd.ExcludeInfo localExcludeInfo;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localExcludeInfoTracker = false;


  /**
   * Auto generated getter method
   *
   * @return org.apache.axis2.deployment.util.xsd.ExcludeInfo
   */
  public org.apache.axis2.deployment.util.xsd.ExcludeInfo getExcludeInfo() {
    return this.localExcludeInfo;
  }


  /**
   * Auto generated setter method
   *
   * @param param ExcludeInfo
   */
  public void setExcludeInfo(final org.apache.axis2.deployment.util.xsd.ExcludeInfo param) {
    this.localExcludeInfoTracker = true;

    this.localExcludeInfo = param;


  }


  /**
   * field for ExposedTransports This was an Array!
   */


  protected java.lang.String[] localExposedTransports;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localExposedTransportsTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String[]
   */
  public java.lang.String[] getExposedTransports() {
    return this.localExposedTransports;
  }


  /**
   * validate the array for ExposedTransports
   */
  protected void validateExposedTransports(final java.lang.String[] param) {

  }


  /**
   * Auto generated setter method
   *
   * @param param ExposedTransports
   */
  public void setExposedTransports(final java.lang.String[] param) {

    validateExposedTransports(param);

    this.localExposedTransportsTracker = true;

    this.localExposedTransports = param;
  }


  /**
   * Auto generated add method for the array for convenience
   *
   * @param param java.lang.String
   */
  public void addExposedTransports(final java.lang.String param) {
    if (this.localExposedTransports == null) {
      this.localExposedTransports = new java.lang.String[] {};
    }


    // update the setting tracker
    this.localExposedTransportsTracker = true;


    final java.util.List list =
      org.apache.axis2.databinding.utils.ConverterUtil.toList(this.localExposedTransports);
    list.add(param);
    this.localExposedTransports = (java.lang.String[]) list.toArray(new java.lang.String[list.size()]);

  }


  /**
   * field for FileName
   */


  protected authclient.java.net.xsd.URL localFileName;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localFileNameTracker = false;


  /**
   * Auto generated getter method
   *
   * @return authclient.java.net.xsd.URL
   */
  public authclient.java.net.xsd.URL getFileName() {
    return this.localFileName;
  }


  /**
   * Auto generated setter method
   *
   * @param param FileName
   */
  public void setFileName(final authclient.java.net.xsd.URL param) {
    this.localFileNameTracker = true;

    this.localFileName = param;


  }


  /**
   * field for ImportedNamespaces
   */


  protected java.lang.Object localImportedNamespaces;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localImportedNamespacesTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.Object
   */
  public java.lang.Object getImportedNamespaces() {
    return this.localImportedNamespaces;
  }


  /**
   * Auto generated setter method
   *
   * @param param ImportedNamespaces
   */
  public void setImportedNamespaces(final java.lang.Object param) {
    this.localImportedNamespacesTracker = true;

    this.localImportedNamespaces = param;


  }


  /**
   * field for Key
   */


  protected java.lang.Object localKey;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localKeyTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.Object
   */
  public java.lang.Object getKey() {
    return this.localKey;
  }


  /**
   * Auto generated setter method
   *
   * @param param Key
   */
  public void setKey(final java.lang.Object param) {
    this.localKeyTracker = true;

    this.localKey = param;


  }


  /**
   * field for LastUpdate
   */


  protected long localLastUpdate;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localLastUpdateTracker = false;


  /**
   * Auto generated getter method
   *
   * @return long
   */
  public long getLastUpdate() {
    return this.localLastUpdate;
  }


  /**
   * Auto generated setter method
   *
   * @param param LastUpdate
   */
  public void setLastUpdate(final long param) {

    // setting primitive attribute tracker to true
    this.localLastUpdateTracker = param != java.lang.Long.MIN_VALUE;

    this.localLastUpdate = param;


  }


  /**
   * field for LastupdateE
   */


  protected long localLastupdateE;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localLastupdateETracker = false;


  /**
   * Auto generated getter method
   *
   * @return long
   */
  public long getLastupdateE() {
    return this.localLastupdateE;
  }


  /**
   * Auto generated setter method
   *
   * @param param LastupdateE
   */
  public void setLastupdateE(final long param) {

    // setting primitive attribute tracker to true
    this.localLastupdateETracker = param != java.lang.Long.MIN_VALUE;

    this.localLastupdateE = param;


  }


  /**
   * field for MessageElementQNameToOperationMap This was an Array!
   */


  protected java.lang.Object[] localMessageElementQNameToOperationMap;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localMessageElementQNameToOperationMapTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.Object[]
   */
  public java.lang.Object[] getMessageElementQNameToOperationMap() {
    return this.localMessageElementQNameToOperationMap;
  }


  /**
   * validate the array for MessageElementQNameToOperationMap
   */
  protected void validateMessageElementQNameToOperationMap(final java.lang.Object[] param) {

  }


  /**
   * Auto generated setter method
   *
   * @param param MessageElementQNameToOperationMap
   */
  public void setMessageElementQNameToOperationMap(final java.lang.Object[] param) {

    validateMessageElementQNameToOperationMap(param);

    this.localMessageElementQNameToOperationMapTracker = true;

    this.localMessageElementQNameToOperationMap = param;
  }


  /**
   * Auto generated add method for the array for convenience
   *
   * @param param java.lang.Object
   */
  public void addMessageElementQNameToOperationMap(final java.lang.Object param) {
    if (this.localMessageElementQNameToOperationMap == null) {
      this.localMessageElementQNameToOperationMap = new java.lang.Object[] {};
    }


    // update the setting tracker
    this.localMessageElementQNameToOperationMapTracker = true;


    final java.util.List list =
      org.apache.axis2.databinding.utils.ConverterUtil.toList(this.localMessageElementQNameToOperationMap);
    list.add(param);
    this.localMessageElementQNameToOperationMap = list.toArray(new java.lang.Object[list.size()]);

  }


  /**
   * field for ModifyUserWSDLPortAddress
   */


  protected boolean localModifyUserWSDLPortAddress;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localModifyUserWSDLPortAddressTracker = false;


  /**
   * Auto generated getter method
   *
   * @return boolean
   */
  public boolean getModifyUserWSDLPortAddress() {
    return this.localModifyUserWSDLPortAddress;
  }


  /**
   * Auto generated setter method
   *
   * @param param ModifyUserWSDLPortAddress
   */
  public void setModifyUserWSDLPortAddress(final boolean param) {

    // setting primitive attribute tracker to true
    this.localModifyUserWSDLPortAddressTracker = true;

    this.localModifyUserWSDLPortAddress = param;


  }


  /**
   * field for Modules
   */


  protected java.lang.Object localModules;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localModulesTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.Object
   */
  public java.lang.Object getModules() {
    return this.localModules;
  }


  /**
   * Auto generated setter method
   *
   * @param param Modules
   */
  public void setModules(final java.lang.Object param) {
    this.localModulesTracker = true;

    this.localModules = param;


  }


  /**
   * field for Name
   */


  protected java.lang.String localName;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localNameTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getName() {
    return this.localName;
  }


  /**
   * Auto generated setter method
   *
   * @param param Name
   */
  public void setName(final java.lang.String param) {
    this.localNameTracker = true;

    this.localName = param;


  }


  /**
   * field for NameSpacesMap
   */


  protected authclient.java.util.xsd.Map localNameSpacesMap;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localNameSpacesMapTracker = false;


  /**
   * Auto generated getter method
   *
   * @return authclient.java.util.xsd.Map
   */
  public authclient.java.util.xsd.Map getNameSpacesMap() {
    return this.localNameSpacesMap;
  }


  /**
   * Auto generated setter method
   *
   * @param param NameSpacesMap
   */
  public void setNameSpacesMap(final authclient.java.util.xsd.Map param) {
    this.localNameSpacesMapTracker = true;

    this.localNameSpacesMap = param;


  }


  /**
   * field for NamespaceMap
   */


  protected authclient.java.util.xsd.Map localNamespaceMap;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localNamespaceMapTracker = false;


  /**
   * Auto generated getter method
   *
   * @return authclient.java.util.xsd.Map
   */
  public authclient.java.util.xsd.Map getNamespaceMap() {
    return this.localNamespaceMap;
  }


  /**
   * Auto generated setter method
   *
   * @param param NamespaceMap
   */
  public void setNamespaceMap(final authclient.java.util.xsd.Map param) {
    this.localNamespaceMapTracker = true;

    this.localNamespaceMap = param;


  }


  /**
   * field for ObjectSupplier
   */


  protected org.apache.axis2.engine.xsd.ObjectSupplier localObjectSupplier;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localObjectSupplierTracker = false;


  /**
   * Auto generated getter method
   *
   * @return org.apache.axis2.engine.xsd.ObjectSupplier
   */
  public org.apache.axis2.engine.xsd.ObjectSupplier getObjectSupplier() {
    return this.localObjectSupplier;
  }


  /**
   * Auto generated setter method
   *
   * @param param ObjectSupplier
   */
  public void setObjectSupplier(final org.apache.axis2.engine.xsd.ObjectSupplier param) {
    this.localObjectSupplierTracker = true;

    this.localObjectSupplier = param;


  }


  /**
   * field for Operations
   */


  protected authclient.java.util.xsd.Iterator localOperations;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localOperationsTracker = false;


  /**
   * Auto generated getter method
   *
   * @return authclient.java.util.xsd.Iterator
   */
  public authclient.java.util.xsd.Iterator getOperations() {
    return this.localOperations;
  }


  /**
   * Auto generated setter method
   *
   * @param param Operations
   */
  public void setOperations(final authclient.java.util.xsd.Iterator param) {
    this.localOperationsTracker = true;

    this.localOperations = param;


  }


  /**
   * field for OperationsNameList
   */


  protected java.lang.Object localOperationsNameList;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localOperationsNameListTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.Object
   */
  public java.lang.Object getOperationsNameList() {
    return this.localOperationsNameList;
  }


  /**
   * Auto generated setter method
   *
   * @param param OperationsNameList
   */
  public void setOperationsNameList(final java.lang.Object param) {
    this.localOperationsNameListTracker = true;

    this.localOperationsNameList = param;


  }


  /**
   * field for P2NMap
   */


  protected authclient.java.util.xsd.Map localP2NMap;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localP2NMapTracker = false;


  /**
   * Auto generated getter method
   *
   * @return authclient.java.util.xsd.Map
   */
  public authclient.java.util.xsd.Map getP2NMap() {
    return this.localP2NMap;
  }


  /**
   * Auto generated setter method
   *
   * @param param P2NMap
   */
  public void setP2NMap(final authclient.java.util.xsd.Map param) {
    this.localP2NMapTracker = true;

    this.localP2NMap = param;


  }


  /**
   * field for Parent
   */


  protected org.apache.axis2.description.xsd.AxisServiceGroup localParent;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localParentTracker = false;


  /**
   * Auto generated getter method
   *
   * @return org.apache.axis2.description.xsd.AxisServiceGroup
   */
  public org.apache.axis2.description.xsd.AxisServiceGroup getParent() {
    return this.localParent;
  }


  /**
   * Auto generated setter method
   *
   * @param param Parent
   */
  public void setParent(final org.apache.axis2.description.xsd.AxisServiceGroup param) {
    this.localParentTracker = true;

    this.localParent = param;


  }


  /**
   * field for PortTypeName
   */


  protected java.lang.String localPortTypeName;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localPortTypeNameTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getPortTypeName() {
    return this.localPortTypeName;
  }


  /**
   * Auto generated setter method
   *
   * @param param PortTypeName
   */
  public void setPortTypeName(final java.lang.String param) {
    this.localPortTypeNameTracker = true;

    this.localPortTypeName = param;


  }


  /**
   * field for PublishedOperations
   */


  protected java.lang.Object localPublishedOperations;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localPublishedOperationsTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.Object
   */
  public java.lang.Object getPublishedOperations() {
    return this.localPublishedOperations;
  }


  /**
   * Auto generated setter method
   *
   * @param param PublishedOperations
   */
  public void setPublishedOperations(final java.lang.Object param) {
    this.localPublishedOperationsTracker = true;

    this.localPublishedOperations = param;


  }


  /**
   * field for SchemaLocationsAdjusted
   */


  protected boolean localSchemaLocationsAdjusted;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localSchemaLocationsAdjustedTracker = false;


  /**
   * Auto generated getter method
   *
   * @return boolean
   */
  public boolean getSchemaLocationsAdjusted() {
    return this.localSchemaLocationsAdjusted;
  }


  /**
   * Auto generated setter method
   *
   * @param param SchemaLocationsAdjusted
   */
  public void setSchemaLocationsAdjusted(final boolean param) {

    // setting primitive attribute tracker to true
    this.localSchemaLocationsAdjustedTracker = true;

    this.localSchemaLocationsAdjusted = param;


  }


  /**
   * field for SchemaMappingTable
   */


  protected authclient.java.util.xsd.Map localSchemaMappingTable;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localSchemaMappingTableTracker = false;


  /**
   * Auto generated getter method
   *
   * @return authclient.java.util.xsd.Map
   */
  public authclient.java.util.xsd.Map getSchemaMappingTable() {
    return this.localSchemaMappingTable;
  }


  /**
   * Auto generated setter method
   *
   * @param param SchemaMappingTable
   */
  public void setSchemaMappingTable(final authclient.java.util.xsd.Map param) {
    this.localSchemaMappingTableTracker = true;

    this.localSchemaMappingTable = param;


  }


  /**
   * field for SchemaTargetNamespace
   */


  protected java.lang.String localSchemaTargetNamespace;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localSchemaTargetNamespaceTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getSchemaTargetNamespace() {
    return this.localSchemaTargetNamespace;
  }


  /**
   * Auto generated setter method
   *
   * @param param SchemaTargetNamespace
   */
  public void setSchemaTargetNamespace(final java.lang.String param) {
    this.localSchemaTargetNamespaceTracker = true;

    this.localSchemaTargetNamespace = param;


  }


  /**
   * field for SchemaTargetNamespacePrefix
   */


  protected java.lang.String localSchemaTargetNamespacePrefix;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localSchemaTargetNamespacePrefixTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getSchemaTargetNamespacePrefix() {
    return this.localSchemaTargetNamespacePrefix;
  }


  /**
   * Auto generated setter method
   *
   * @param param SchemaTargetNamespacePrefix
   */
  public void setSchemaTargetNamespacePrefix(final java.lang.String param) {
    this.localSchemaTargetNamespacePrefixTracker = true;

    this.localSchemaTargetNamespacePrefix = param;


  }


  /**
   * field for SchematargetNamespaceE
   */


  protected java.lang.String localSchematargetNamespaceE;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localSchematargetNamespaceETracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getSchematargetNamespaceE() {
    return this.localSchematargetNamespaceE;
  }


  /**
   * Auto generated setter method
   *
   * @param param SchematargetNamespaceE
   */
  public void setSchematargetNamespaceE(final java.lang.String param) {
    this.localSchematargetNamespaceETracker = true;

    this.localSchematargetNamespaceE = param;


  }


  /**
   * field for SchematargetNamespacePrefixE
   */


  protected java.lang.String localSchematargetNamespacePrefixE;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localSchematargetNamespacePrefixETracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getSchematargetNamespacePrefixE() {
    return this.localSchematargetNamespacePrefixE;
  }


  /**
   * Auto generated setter method
   *
   * @param param SchematargetNamespacePrefixE
   */
  public void setSchematargetNamespacePrefixE(final java.lang.String param) {
    this.localSchematargetNamespacePrefixETracker = true;

    this.localSchematargetNamespacePrefixE = param;


  }


  /**
   * field for Scope
   */


  protected java.lang.String localScope;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localScopeTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getScope() {
    return this.localScope;
  }


  /**
   * Auto generated setter method
   *
   * @param param Scope
   */
  public void setScope(final java.lang.String param) {
    this.localScopeTracker = true;

    this.localScope = param;


  }


  /**
   * field for ServiceDescription
   */


  protected java.lang.String localServiceDescription;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localServiceDescriptionTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getServiceDescription() {
    return this.localServiceDescription;
  }


  /**
   * Auto generated setter method
   *
   * @param param ServiceDescription
   */
  public void setServiceDescription(final java.lang.String param) {
    this.localServiceDescriptionTracker = true;

    this.localServiceDescription = param;


  }


  /**
   * field for ServiceLifeCycle
   */


  protected org.apache.axis2.engine.xsd.ServiceLifeCycle localServiceLifeCycle;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localServiceLifeCycleTracker = false;


  /**
   * Auto generated getter method
   *
   * @return org.apache.axis2.engine.xsd.ServiceLifeCycle
   */
  public org.apache.axis2.engine.xsd.ServiceLifeCycle getServiceLifeCycle() {
    return this.localServiceLifeCycle;
  }


  /**
   * Auto generated setter method
   *
   * @param param ServiceLifeCycle
   */
  public void setServiceLifeCycle(final org.apache.axis2.engine.xsd.ServiceLifeCycle param) {
    this.localServiceLifeCycleTracker = true;

    this.localServiceLifeCycle = param;


  }


  /**
   * field for SetEndpointsToAllUsedBindings
   */


  protected boolean localSetEndpointsToAllUsedBindings;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localSetEndpointsToAllUsedBindingsTracker = false;


  /**
   * Auto generated getter method
   *
   * @return boolean
   */
  public boolean getSetEndpointsToAllUsedBindings() {
    return this.localSetEndpointsToAllUsedBindings;
  }


  /**
   * Auto generated setter method
   *
   * @param param SetEndpointsToAllUsedBindings
   */
  public void setSetEndpointsToAllUsedBindings(final boolean param) {

    // setting primitive attribute tracker to true
    this.localSetEndpointsToAllUsedBindingsTracker = true;

    this.localSetEndpointsToAllUsedBindings = param;


  }


  /**
   * field for SoapNsUri
   */


  protected java.lang.String localSoapNsUri;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localSoapNsUriTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getSoapNsUri() {
    return this.localSoapNsUri;
  }


  /**
   * Auto generated setter method
   *
   * @param param SoapNsUri
   */
  public void setSoapNsUri(final java.lang.String param) {
    this.localSoapNsUriTracker = true;

    this.localSoapNsUri = param;


  }


  /**
   * field for TargetNamespace
   */


  protected java.lang.String localTargetNamespace;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localTargetNamespaceTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getTargetNamespace() {
    return this.localTargetNamespace;
  }


  /**
   * Auto generated setter method
   *
   * @param param TargetNamespace
   */
  public void setTargetNamespace(final java.lang.String param) {
    this.localTargetNamespaceTracker = true;

    this.localTargetNamespace = param;


  }


  /**
   * field for TargetNamespacePrefix
   */


  protected java.lang.String localTargetNamespacePrefix;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localTargetNamespacePrefixTracker = false;


  /**
   * Auto generated getter method
   *
   * @return java.lang.String
   */
  public java.lang.String getTargetNamespacePrefix() {
    return this.localTargetNamespacePrefix;
  }


  /**
   * Auto generated setter method
   *
   * @param param TargetNamespacePrefix
   */
  public void setTargetNamespacePrefix(final java.lang.String param) {
    this.localTargetNamespacePrefixTracker = true;

    this.localTargetNamespacePrefix = param;


  }


  /**
   * field for TypeTable
   */


  protected org.apache.axis2.description.java2wsdl.xsd.TypeTable localTypeTable;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localTypeTableTracker = false;


  /**
   * Auto generated getter method
   *
   * @return org.apache.axis2.description.java2wsdl.xsd.TypeTable
   */
  public org.apache.axis2.description.java2wsdl.xsd.TypeTable getTypeTable() {
    return this.localTypeTable;
  }


  /**
   * Auto generated setter method
   *
   * @param param TypeTable
   */
  public void setTypeTable(final org.apache.axis2.description.java2wsdl.xsd.TypeTable param) {
    this.localTypeTableTracker = true;

    this.localTypeTable = param;


  }


  /**
   * field for UseDefaultChains
   */


  protected boolean localUseDefaultChains;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localUseDefaultChainsTracker = false;


  /**
   * Auto generated getter method
   *
   * @return boolean
   */
  public boolean getUseDefaultChains() {
    return this.localUseDefaultChains;
  }


  /**
   * Auto generated setter method
   *
   * @param param UseDefaultChains
   */
  public void setUseDefaultChains(final boolean param) {

    // setting primitive attribute tracker to true
    this.localUseDefaultChainsTracker = true;

    this.localUseDefaultChains = param;


  }


  /**
   * field for UseUserWSDL
   */


  protected boolean localUseUserWSDL;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localUseUserWSDLTracker = false;


  /**
   * Auto generated getter method
   *
   * @return boolean
   */
  public boolean getUseUserWSDL() {
    return this.localUseUserWSDL;
  }


  /**
   * Auto generated setter method
   *
   * @param param UseUserWSDL
   */
  public void setUseUserWSDL(final boolean param) {

    // setting primitive attribute tracker to true
    this.localUseUserWSDLTracker = true;

    this.localUseUserWSDL = param;


  }


  /**
   * field for WsdlFound
   */


  protected boolean localWsdlFound;

  /*
   * This tracker boolean wil be used to detect whether the user called the set method for this
   * attribute. It will be used to determine whether to include this field in the serialized XML
   */
  protected boolean localWsdlFoundTracker = false;


  /**
   * Auto generated getter method
   *
   * @return boolean
   */
  public boolean getWsdlFound() {
    return this.localWsdlFound;
  }


  /**
   * Auto generated setter method
   *
   * @param param WsdlFound
   */
  public void setWsdlFound(final boolean param) {

    // setting primitive attribute tracker to true
    this.localWsdlFoundTracker = true;

    this.localWsdlFound = param;


  }


  /**
   *
   * @param parentQName
   * @param factory
   * @return org.apache.axiom.om.OMElement
   */
  @Override
  public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                                                    final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {


    final org.apache.axiom.om.OMDataSource dataSource =
      new org.apache.axis2.databinding.ADBDataSource(this, parentQName);
    return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);

  }

  @Override
  public void serialize(final javax.xml.namespace.QName parentQName,
                        final javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException,
    org.apache.axis2.databinding.ADBException {
    serialize(parentQName, xmlWriter, false);
  }

  @Override
  public void serialize(final javax.xml.namespace.QName parentQName, final javax.xml.stream.XMLStreamWriter xmlWriter,
                        final boolean serializeType) throws javax.xml.stream.XMLStreamException,
    org.apache.axis2.databinding.ADBException {


    java.lang.String prefix = null;
    java.lang.String namespace = null;


    prefix = parentQName.getPrefix();
    namespace = parentQName.getNamespaceURI();
    writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

    if (serializeType) {


      final java.lang.String namespacePrefix =
        registerPrefix(xmlWriter, "http://description.axis2.apache.org/xsd");
      if (namespacePrefix != null && namespacePrefix.trim().length() > 0) {
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
          namespacePrefix + ":AxisService", xmlWriter);
      } else {
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "AxisService", xmlWriter);
      }


    }
    if (this.localEPRsTracker) {
      if (this.localEPRs != null) {
        namespace = "http://description.axis2.apache.org/xsd";
        for (final String localEPR : this.localEPRs) {

          if (localEPR != null) {

            writeStartElement(null, namespace, "EPRs", xmlWriter);


            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localEPR));

            xmlWriter.writeEndElement();

          } else {

            // write null attribute
            namespace = "http://description.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "EPRs", xmlWriter);
            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
            xmlWriter.writeEndElement();

          }

        }
      } else {

        // write the null attribute
        // write null attribute
        writeStartElement(null, "http://description.axis2.apache.org/xsd", "EPRs", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();

      }

    }
    if (this.localWSAddressingFlagTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "WSAddressingFlag", xmlWriter);


      if (this.localWSAddressingFlag == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {


        xmlWriter.writeCharacters(this.localWSAddressingFlag);

      }

      xmlWriter.writeEndElement();
    }
    if (this.localActiveTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "active", xmlWriter);

      if (false) {

        throw new org.apache.axis2.databinding.ADBException("active cannot be null!!");

      } else {
        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localActive));
      }

      xmlWriter.writeEndElement();
    }
    if (this.localAxisServiceGroupTracker) {
      if (this.localAxisServiceGroup == null) {

        writeStartElement(null, "http://description.axis2.apache.org/xsd", "axisServiceGroup", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localAxisServiceGroup.serialize(new javax.xml.namespace.QName(
          "http://description.axis2.apache.org/xsd", "axisServiceGroup"), xmlWriter);
      }
    }
    if (this.localBindingNameTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "bindingName", xmlWriter);


      if (this.localBindingName == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {


        xmlWriter.writeCharacters(this.localBindingName);

      }

      xmlWriter.writeEndElement();
    }
    if (this.localClassLoaderTracker) {

      if (this.localClassLoader != null) {
        if (this.localClassLoader instanceof org.apache.axis2.databinding.ADBBean) {
          ((org.apache.axis2.databinding.ADBBean) this.localClassLoader).serialize(new javax.xml.namespace.QName(
            "http://description.axis2.apache.org/xsd", "classLoader"), xmlWriter, true);
        } else {
          writeStartElement(null, "http://description.axis2.apache.org/xsd", "classLoader", xmlWriter);
          org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localClassLoader, xmlWriter);
          xmlWriter.writeEndElement();
        }
      } else {

        // write null attribute
        writeStartElement(null, "http://description.axis2.apache.org/xsd", "classLoader", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();

      }


    }
    if (this.localClientSideTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "clientSide", xmlWriter);

      if (false) {

        throw new org.apache.axis2.databinding.ADBException("clientSide cannot be null!!");

      } else {
        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localClientSide));
      }

      xmlWriter.writeEndElement();
    }
    if (this.localControlOperationsTracker) {

      if (this.localControlOperations != null) {
        if (this.localControlOperations instanceof org.apache.axis2.databinding.ADBBean) {
          ((org.apache.axis2.databinding.ADBBean) this.localControlOperations).serialize(new javax.xml.namespace.QName(
            "http://description.axis2.apache.org/xsd", "controlOperations"), xmlWriter, true);
        } else {
          writeStartElement(null, "http://description.axis2.apache.org/xsd", "controlOperations", xmlWriter);
          org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localControlOperations,
            xmlWriter);
          xmlWriter.writeEndElement();
        }
      } else {

        // write null attribute
        writeStartElement(null, "http://description.axis2.apache.org/xsd", "controlOperations", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();

      }


    }
    if (this.localCustomSchemaNamePrefixTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "customSchemaNamePrefix", xmlWriter);


      if (this.localCustomSchemaNamePrefix == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {


        xmlWriter.writeCharacters(this.localCustomSchemaNamePrefix);

      }

      xmlWriter.writeEndElement();
    }
    if (this.localCustomSchemaNameSuffixTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "customSchemaNameSuffix", xmlWriter);


      if (this.localCustomSchemaNameSuffix == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {


        xmlWriter.writeCharacters(this.localCustomSchemaNameSuffix);

      }

      xmlWriter.writeEndElement();
    }
    if (this.localCustomWsdlTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "customWsdl", xmlWriter);

      if (false) {

        throw new org.apache.axis2.databinding.ADBException("customWsdl cannot be null!!");

      } else {
        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localCustomWsdl));
      }

      xmlWriter.writeEndElement();
    }
    if (this.localElementFormDefaultTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "elementFormDefault", xmlWriter);

      if (false) {

        throw new org.apache.axis2.databinding.ADBException("elementFormDefault cannot be null!!");

      } else {
        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localElementFormDefault));
      }

      xmlWriter.writeEndElement();
    }
    if (this.localEnableAllTransportsTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "enableAllTransports", xmlWriter);

      if (false) {

        throw new org.apache.axis2.databinding.ADBException("enableAllTransports cannot be null!!");

      } else {
        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localEnableAllTransports));
      }

      xmlWriter.writeEndElement();
    }
    if (this.localEndpointNameTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "endpointName", xmlWriter);


      if (this.localEndpointName == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {


        xmlWriter.writeCharacters(this.localEndpointName);

      }

      xmlWriter.writeEndElement();
    }
    if (this.localEndpointURLTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "endpointURL", xmlWriter);


      if (this.localEndpointURL == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {


        xmlWriter.writeCharacters(this.localEndpointURL);

      }

      xmlWriter.writeEndElement();
    }
    if (this.localEndpointsTracker) {
      if (this.localEndpoints == null) {

        writeStartElement(null, "http://description.axis2.apache.org/xsd", "endpoints", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localEndpoints.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "endpoints"), xmlWriter);
      }
    }
    if (this.localExcludeInfoTracker) {
      if (this.localExcludeInfo == null) {

        writeStartElement(null, "http://description.axis2.apache.org/xsd", "excludeInfo", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localExcludeInfo.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "excludeInfo"), xmlWriter);
      }
    }
    if (this.localExposedTransportsTracker) {
      if (this.localExposedTransports != null) {
        namespace = "http://description.axis2.apache.org/xsd";
        for (final String localExposedTransport : this.localExposedTransports) {

          if (localExposedTransport != null) {

            writeStartElement(null, namespace, "exposedTransports", xmlWriter);


            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localExposedTransport));

            xmlWriter.writeEndElement();

          } else {

            // write null attribute
            namespace = "http://description.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "exposedTransports", xmlWriter);
            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
            xmlWriter.writeEndElement();

          }

        }
      } else {

        // write the null attribute
        // write null attribute
        writeStartElement(null, "http://description.axis2.apache.org/xsd", "exposedTransports", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();

      }

    }
    if (this.localFileNameTracker) {
      if (this.localFileName == null) {

        writeStartElement(null, "http://description.axis2.apache.org/xsd", "fileName", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localFileName.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "fileName"), xmlWriter);
      }
    }
    if (this.localImportedNamespacesTracker) {

      if (this.localImportedNamespaces != null) {
        if (this.localImportedNamespaces instanceof org.apache.axis2.databinding.ADBBean) {
          ((org.apache.axis2.databinding.ADBBean) this.localImportedNamespaces).serialize(new javax.xml.namespace.QName(
            "http://description.axis2.apache.org/xsd", "importedNamespaces"), xmlWriter, true);
        } else {
          writeStartElement(null, "http://description.axis2.apache.org/xsd", "importedNamespaces", xmlWriter);
          org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localImportedNamespaces,
            xmlWriter);
          xmlWriter.writeEndElement();
        }
      } else {

        // write null attribute
        writeStartElement(null, "http://description.axis2.apache.org/xsd", "importedNamespaces", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();

      }


    }
    if (this.localKeyTracker) {

      if (this.localKey != null) {
        if (this.localKey instanceof org.apache.axis2.databinding.ADBBean) {
          ((org.apache.axis2.databinding.ADBBean) this.localKey).serialize(new javax.xml.namespace.QName(
            "http://description.axis2.apache.org/xsd", "key"), xmlWriter, true);
        } else {
          writeStartElement(null, "http://description.axis2.apache.org/xsd", "key", xmlWriter);
          org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localKey, xmlWriter);
          xmlWriter.writeEndElement();
        }
      } else {

        // write null attribute
        writeStartElement(null, "http://description.axis2.apache.org/xsd", "key", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();

      }


    }
    if (this.localLastUpdateTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "lastUpdate", xmlWriter);

      if (this.localLastUpdate == java.lang.Long.MIN_VALUE) {

        throw new org.apache.axis2.databinding.ADBException("lastUpdate cannot be null!!");

      } else {
        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localLastUpdate));
      }

      xmlWriter.writeEndElement();
    }
    if (this.localLastupdateETracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "lastupdate", xmlWriter);

      if (this.localLastupdateE == java.lang.Long.MIN_VALUE) {

        throw new org.apache.axis2.databinding.ADBException("lastupdate cannot be null!!");

      } else {
        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localLastupdateE));
      }

      xmlWriter.writeEndElement();
    }
    if (this.localMessageElementQNameToOperationMapTracker) {

      if (this.localMessageElementQNameToOperationMap != null) {
        for (final Object element : this.localMessageElementQNameToOperationMap) {
          if (element != null) {

            if (element instanceof org.apache.axis2.databinding.ADBBean) {
              ((org.apache.axis2.databinding.ADBBean) element).serialize(new javax.xml.namespace.QName(
                  "http://description.axis2.apache.org/xsd", "messageElementQNameToOperationMap"),
                xmlWriter, true);
            } else {
              writeStartElement(null, "http://description.axis2.apache.org/xsd",
                "messageElementQNameToOperationMap", xmlWriter);
              org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(element, xmlWriter);
              xmlWriter.writeEndElement();
            }

          } else {

            // write null attribute
            writeStartElement(null, "http://description.axis2.apache.org/xsd",
              "messageElementQNameToOperationMap", xmlWriter);

            // write the nil attribute
            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
            xmlWriter.writeEndElement();

          }
        }
      } else {

        // write null attribute
        writeStartElement(null, "http://description.axis2.apache.org/xsd", "messageElementQNameToOperationMap",
          xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();

      }

    }
    if (this.localModifyUserWSDLPortAddressTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "modifyUserWSDLPortAddress", xmlWriter);

      if (false) {

        throw new org.apache.axis2.databinding.ADBException("modifyUserWSDLPortAddress cannot be null!!");

      } else {
        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localModifyUserWSDLPortAddress));
      }

      xmlWriter.writeEndElement();
    }
    if (this.localModulesTracker) {

      if (this.localModules != null) {
        if (this.localModules instanceof org.apache.axis2.databinding.ADBBean) {
          ((org.apache.axis2.databinding.ADBBean) this.localModules).serialize(new javax.xml.namespace.QName(
            "http://description.axis2.apache.org/xsd", "modules"), xmlWriter, true);
        } else {
          writeStartElement(null, "http://description.axis2.apache.org/xsd", "modules", xmlWriter);
          org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localModules, xmlWriter);
          xmlWriter.writeEndElement();
        }
      } else {

        // write null attribute
        writeStartElement(null, "http://description.axis2.apache.org/xsd", "modules", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();

      }


    }
    if (this.localNameTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "name", xmlWriter);


      if (this.localName == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {


        xmlWriter.writeCharacters(this.localName);

      }

      xmlWriter.writeEndElement();
    }
    if (this.localNameSpacesMapTracker) {
      if (this.localNameSpacesMap == null) {

        writeStartElement(null, "http://description.axis2.apache.org/xsd", "nameSpacesMap", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localNameSpacesMap.serialize(new javax.xml.namespace.QName(
          "http://description.axis2.apache.org/xsd", "nameSpacesMap"), xmlWriter);
      }
    }
    if (this.localNamespaceMapTracker) {
      if (this.localNamespaceMap == null) {

        writeStartElement(null, "http://description.axis2.apache.org/xsd", "namespaceMap", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localNamespaceMap.serialize(new javax.xml.namespace.QName(
          "http://description.axis2.apache.org/xsd", "namespaceMap"), xmlWriter);
      }
    }
    if (this.localObjectSupplierTracker) {
      if (this.localObjectSupplier == null) {

        writeStartElement(null, "http://description.axis2.apache.org/xsd", "objectSupplier", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localObjectSupplier.serialize(new javax.xml.namespace.QName(
          "http://description.axis2.apache.org/xsd", "objectSupplier"), xmlWriter);
      }
    }
    if (this.localOperationsTracker) {
      if (this.localOperations == null) {

        writeStartElement(null, "http://description.axis2.apache.org/xsd", "operations", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localOperations.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "operations"), xmlWriter);
      }
    }
    if (this.localOperationsNameListTracker) {

      if (this.localOperationsNameList != null) {
        if (this.localOperationsNameList instanceof org.apache.axis2.databinding.ADBBean) {
          ((org.apache.axis2.databinding.ADBBean) this.localOperationsNameList).serialize(new javax.xml.namespace.QName(
            "http://description.axis2.apache.org/xsd", "operationsNameList"), xmlWriter, true);
        } else {
          writeStartElement(null, "http://description.axis2.apache.org/xsd", "operationsNameList", xmlWriter);
          org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localOperationsNameList,
            xmlWriter);
          xmlWriter.writeEndElement();
        }
      } else {

        // write null attribute
        writeStartElement(null, "http://description.axis2.apache.org/xsd", "operationsNameList", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();

      }


    }
    if (this.localP2NMapTracker) {
      if (this.localP2NMap == null) {

        writeStartElement(null, "http://description.axis2.apache.org/xsd", "p2nMap", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localP2NMap.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "p2nMap"), xmlWriter);
      }
    }
    if (this.localParentTracker) {
      if (this.localParent == null) {

        writeStartElement(null, "http://description.axis2.apache.org/xsd", "parent", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localParent.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "parent"), xmlWriter);
      }
    }
    if (this.localPortTypeNameTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "portTypeName", xmlWriter);


      if (this.localPortTypeName == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {


        xmlWriter.writeCharacters(this.localPortTypeName);

      }

      xmlWriter.writeEndElement();
    }
    if (this.localPublishedOperationsTracker) {

      if (this.localPublishedOperations != null) {
        if (this.localPublishedOperations instanceof org.apache.axis2.databinding.ADBBean) {
          ((org.apache.axis2.databinding.ADBBean) this.localPublishedOperations).serialize(new javax.xml.namespace.QName(
            "http://description.axis2.apache.org/xsd", "publishedOperations"), xmlWriter, true);
        } else {
          writeStartElement(null, "http://description.axis2.apache.org/xsd", "publishedOperations",
            xmlWriter);
          org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localPublishedOperations,
            xmlWriter);
          xmlWriter.writeEndElement();
        }
      } else {

        // write null attribute
        writeStartElement(null, "http://description.axis2.apache.org/xsd", "publishedOperations", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();

      }


    }
    if (this.localSchemaLocationsAdjustedTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "schemaLocationsAdjusted", xmlWriter);

      if (false) {

        throw new org.apache.axis2.databinding.ADBException("schemaLocationsAdjusted cannot be null!!");

      } else {
        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localSchemaLocationsAdjusted));
      }

      xmlWriter.writeEndElement();
    }
    if (this.localSchemaMappingTableTracker) {
      if (this.localSchemaMappingTable == null) {

        writeStartElement(null, "http://description.axis2.apache.org/xsd", "schemaMappingTable", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localSchemaMappingTable.serialize(new javax.xml.namespace.QName(
          "http://description.axis2.apache.org/xsd", "schemaMappingTable"), xmlWriter);
      }
    }
    if (this.localSchemaTargetNamespaceTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "schemaTargetNamespace", xmlWriter);


      if (this.localSchemaTargetNamespace == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {


        xmlWriter.writeCharacters(this.localSchemaTargetNamespace);

      }

      xmlWriter.writeEndElement();
    }
    if (this.localSchemaTargetNamespacePrefixTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "schemaTargetNamespacePrefix", xmlWriter);


      if (this.localSchemaTargetNamespacePrefix == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {


        xmlWriter.writeCharacters(this.localSchemaTargetNamespacePrefix);

      }

      xmlWriter.writeEndElement();
    }
    if (this.localSchematargetNamespaceETracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "schematargetNamespace", xmlWriter);


      if (this.localSchematargetNamespaceE == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {


        xmlWriter.writeCharacters(this.localSchematargetNamespaceE);

      }

      xmlWriter.writeEndElement();
    }
    if (this.localSchematargetNamespacePrefixETracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "schematargetNamespacePrefix", xmlWriter);


      if (this.localSchematargetNamespacePrefixE == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {


        xmlWriter.writeCharacters(this.localSchematargetNamespacePrefixE);

      }

      xmlWriter.writeEndElement();
    }
    if (this.localScopeTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "scope", xmlWriter);


      if (this.localScope == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {


        xmlWriter.writeCharacters(this.localScope);

      }

      xmlWriter.writeEndElement();
    }
    if (this.localServiceDescriptionTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "serviceDescription", xmlWriter);


      if (this.localServiceDescription == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {


        xmlWriter.writeCharacters(this.localServiceDescription);

      }

      xmlWriter.writeEndElement();
    }
    if (this.localServiceLifeCycleTracker) {
      if (this.localServiceLifeCycle == null) {

        writeStartElement(null, "http://description.axis2.apache.org/xsd", "serviceLifeCycle", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localServiceLifeCycle.serialize(new javax.xml.namespace.QName(
          "http://description.axis2.apache.org/xsd", "serviceLifeCycle"), xmlWriter);
      }
    }
    if (this.localSetEndpointsToAllUsedBindingsTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "setEndpointsToAllUsedBindings", xmlWriter);

      if (false) {

        throw new org.apache.axis2.databinding.ADBException("setEndpointsToAllUsedBindings cannot be null!!");

      } else {
        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localSetEndpointsToAllUsedBindings));
      }

      xmlWriter.writeEndElement();
    }
    if (this.localSoapNsUriTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "soapNsUri", xmlWriter);


      if (this.localSoapNsUri == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {


        xmlWriter.writeCharacters(this.localSoapNsUri);

      }

      xmlWriter.writeEndElement();
    }
    if (this.localTargetNamespaceTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "targetNamespace", xmlWriter);


      if (this.localTargetNamespace == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {


        xmlWriter.writeCharacters(this.localTargetNamespace);

      }

      xmlWriter.writeEndElement();
    }
    if (this.localTargetNamespacePrefixTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "targetNamespacePrefix", xmlWriter);


      if (this.localTargetNamespacePrefix == null) {
        // write the nil attribute

        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);

      } else {


        xmlWriter.writeCharacters(this.localTargetNamespacePrefix);

      }

      xmlWriter.writeEndElement();
    }
    if (this.localTypeTableTracker) {
      if (this.localTypeTable == null) {

        writeStartElement(null, "http://description.axis2.apache.org/xsd", "typeTable", xmlWriter);

        // write the nil attribute
        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
        xmlWriter.writeEndElement();
      } else {
        this.localTypeTable.serialize(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "typeTable"), xmlWriter);
      }
    }
    if (this.localUseDefaultChainsTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "useDefaultChains", xmlWriter);

      if (false) {

        throw new org.apache.axis2.databinding.ADBException("useDefaultChains cannot be null!!");

      } else {
        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localUseDefaultChains));
      }

      xmlWriter.writeEndElement();
    }
    if (this.localUseUserWSDLTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "useUserWSDL", xmlWriter);

      if (false) {

        throw new org.apache.axis2.databinding.ADBException("useUserWSDL cannot be null!!");

      } else {
        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localUseUserWSDL));
      }

      xmlWriter.writeEndElement();
    }
    if (this.localWsdlFoundTracker) {
      namespace = "http://description.axis2.apache.org/xsd";
      writeStartElement(null, namespace, "wsdlFound", xmlWriter);

      if (false) {

        throw new org.apache.axis2.databinding.ADBException("wsdlFound cannot be null!!");

      } else {
        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localWsdlFound));
      }

      xmlWriter.writeEndElement();
    }
    xmlWriter.writeEndElement();


  }

  private static java.lang.String generatePrefix(final java.lang.String namespace) {
    if (namespace.equals("http://description.axis2.apache.org/xsd")) {
      return "ns19";
    }
    return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
  }

  /**
   * Utility method to write an element start tag.
   */
  private void writeStartElement(java.lang.String prefix, final java.lang.String namespace,
                                 final java.lang.String localPart,
                                 final javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
    final java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
    if (writerPrefix != null) {
      xmlWriter.writeStartElement(namespace, localPart);
    } else {
      if (namespace.length() == 0) {
        prefix = "";
      } else if (prefix == null) {
        prefix = generatePrefix(namespace);
      }

      xmlWriter.writeStartElement(prefix, localPart, namespace);
      xmlWriter.writeNamespace(prefix, namespace);
      xmlWriter.setPrefix(prefix, namespace);
    }
  }

  /**
   * Util method to write an attribute with the ns prefix
   */
  private void writeAttribute(final java.lang.String prefix, final java.lang.String namespace,
                              final java.lang.String attName, final java.lang.String attValue,
                              final javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
    if (xmlWriter.getPrefix(namespace) == null) {
      xmlWriter.writeNamespace(prefix, namespace);
      xmlWriter.setPrefix(prefix, namespace);
    }
    xmlWriter.writeAttribute(namespace, attName, attValue);
  }

  /**
   * Util method to write an attribute without the ns prefix
   */
  private void writeAttribute(final java.lang.String namespace, final java.lang.String attName,
                              final java.lang.String attValue,
                              final javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
    if (namespace.equals("")) {
      xmlWriter.writeAttribute(attName, attValue);
    } else {
      registerPrefix(xmlWriter, namespace);
      xmlWriter.writeAttribute(namespace, attName, attValue);
    }
  }


  /**
   * Util method to write an attribute without the ns prefix
   */
  private void writeQNameAttribute(final java.lang.String namespace, final java.lang.String attName,
                                   final javax.xml.namespace.QName qname,
                                   final javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

    final java.lang.String attributeNamespace = qname.getNamespaceURI();
    java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
    if (attributePrefix == null) {
      attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
    }
    java.lang.String attributeValue;
    if (attributePrefix.trim().length() > 0) {
      attributeValue = attributePrefix + ":" + qname.getLocalPart();
    } else {
      attributeValue = qname.getLocalPart();
    }

    if (namespace.equals("")) {
      xmlWriter.writeAttribute(attName, attributeValue);
    } else {
      registerPrefix(xmlWriter, namespace);
      xmlWriter.writeAttribute(namespace, attName, attributeValue);
    }
  }

  /**
   * method to handle Qnames
   */

  private void writeQName(final javax.xml.namespace.QName qname,
                          final javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
    final java.lang.String namespaceURI = qname.getNamespaceURI();
    if (namespaceURI != null) {
      java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
      if (prefix == null) {
        prefix = generatePrefix(namespaceURI);
        xmlWriter.writeNamespace(prefix, namespaceURI);
        xmlWriter.setPrefix(prefix, namespaceURI);
      }

      if (prefix.trim().length() > 0) {
        xmlWriter.writeCharacters(prefix + ":"
          + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
      } else {
        // i.e this is the default namespace
        xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
      }

    } else {
      xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
    }
  }

  private void writeQNames(final javax.xml.namespace.QName[] qnames,
                           final javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

    if (qnames != null) {
      // we have to store this data until last moment since it is not possible to write any
      // namespace data after writing the charactor data
      final java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
      java.lang.String namespaceURI = null;
      java.lang.String prefix = null;

      for (int i = 0; i < qnames.length; i++) {
        if (i > 0) {
          stringToWrite.append(" ");
        }
        namespaceURI = qnames[i].getNamespaceURI();
        if (namespaceURI != null) {
          prefix = xmlWriter.getPrefix(namespaceURI);
          if (prefix == null || prefix.length() == 0) {
            prefix = generatePrefix(namespaceURI);
            xmlWriter.writeNamespace(prefix, namespaceURI);
            xmlWriter.setPrefix(prefix, namespaceURI);
          }

          if (prefix.trim().length() > 0) {
            stringToWrite.append(prefix).append(":")
              .append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
          } else {
            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
          }
        } else {
          stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
        }
      }
      xmlWriter.writeCharacters(stringToWrite.toString());
    }

  }


  /**
   * Register a namespace prefix
   */
  private java.lang.String registerPrefix(final javax.xml.stream.XMLStreamWriter xmlWriter,
                                          final java.lang.String namespace) throws javax.xml.stream.XMLStreamException {
    java.lang.String prefix = xmlWriter.getPrefix(namespace);
    if (prefix == null) {
      prefix = generatePrefix(namespace);
      while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
        prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
      }
      xmlWriter.writeNamespace(prefix, namespace);
      xmlWriter.setPrefix(prefix, namespace);
    }
    return prefix;
  }


  /**
   * databinding method to get an XML representation of this object
   *
   */
  @Override
  public javax.xml.stream.XMLStreamReader getPullParser(final javax.xml.namespace.QName qName) throws org.apache.axis2.databinding.ADBException {


    final java.util.ArrayList elementList = new java.util.ArrayList();
    final java.util.ArrayList attribList = new java.util.ArrayList();

    if (this.localEPRsTracker) {
      if (this.localEPRs != null) {
        for (final String localEPR : this.localEPRs) {

          if (localEPR != null) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
              "EPRs"));
            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localEPR));
          } else {

            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
              "EPRs"));
            elementList.add(null);

          }


        }
      } else {

        elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "EPRs"));
        elementList.add(null);

      }

    }
    if (this.localWSAddressingFlagTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "WSAddressingFlag"));

      elementList.add(this.localWSAddressingFlag == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localWSAddressingFlag));
    }
    if (this.localActiveTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "active"));

      elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localActive));
    }
    if (this.localAxisServiceGroupTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "axisServiceGroup"));


      elementList.add(this.localAxisServiceGroup == null ? null : this.localAxisServiceGroup);
    }
    if (this.localBindingNameTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "bindingName"));

      elementList.add(this.localBindingName == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localBindingName));
    }
    if (this.localClassLoaderTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "classLoader"));


      elementList.add(this.localClassLoader == null ? null : this.localClassLoader);
    }
    if (this.localClientSideTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "clientSide"));

      elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localClientSide));
    }
    if (this.localControlOperationsTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "controlOperations"));


      elementList.add(this.localControlOperations == null ? null : this.localControlOperations);
    }
    if (this.localCustomSchemaNamePrefixTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "customSchemaNamePrefix"));

      elementList.add(this.localCustomSchemaNamePrefix == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localCustomSchemaNamePrefix));
    }
    if (this.localCustomSchemaNameSuffixTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "customSchemaNameSuffix"));

      elementList.add(this.localCustomSchemaNameSuffix == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localCustomSchemaNameSuffix));
    }
    if (this.localCustomWsdlTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "customWsdl"));

      elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localCustomWsdl));
    }
    if (this.localElementFormDefaultTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "elementFormDefault"));

      elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localElementFormDefault));
    }
    if (this.localEnableAllTransportsTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "enableAllTransports"));

      elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localEnableAllTransports));
    }
    if (this.localEndpointNameTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "endpointName"));

      elementList.add(this.localEndpointName == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localEndpointName));
    }
    if (this.localEndpointURLTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "endpointURL"));

      elementList.add(this.localEndpointURL == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localEndpointURL));
    }
    if (this.localEndpointsTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "endpoints"));


      elementList.add(this.localEndpoints == null ? null : this.localEndpoints);
    }
    if (this.localExcludeInfoTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "excludeInfo"));


      elementList.add(this.localExcludeInfo == null ? null : this.localExcludeInfo);
    }
    if (this.localExposedTransportsTracker) {
      if (this.localExposedTransports != null) {
        for (final String localExposedTransport : this.localExposedTransports) {

          if (localExposedTransport != null) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
              "exposedTransports"));
            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localExposedTransport));
          } else {

            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
              "exposedTransports"));
            elementList.add(null);

          }


        }
      } else {

        elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "exposedTransports"));
        elementList.add(null);

      }

    }
    if (this.localFileNameTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "fileName"));


      elementList.add(this.localFileName == null ? null : this.localFileName);
    }
    if (this.localImportedNamespacesTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "importedNamespaces"));


      elementList.add(this.localImportedNamespaces == null ? null : this.localImportedNamespaces);
    }
    if (this.localKeyTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "key"));


      elementList.add(this.localKey == null ? null : this.localKey);
    }
    if (this.localLastUpdateTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "lastUpdate"));

      elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localLastUpdate));
    }
    if (this.localLastupdateETracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "lastupdate"));

      elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localLastupdateE));
    }
    if (this.localMessageElementQNameToOperationMapTracker) {
      if (this.localMessageElementQNameToOperationMap != null) {
        for (final Object element : this.localMessageElementQNameToOperationMap) {

          if (element != null) {
            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
              "messageElementQNameToOperationMap"));
            elementList.add(element);
          } else {

            elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
              "messageElementQNameToOperationMap"));
            elementList.add(null);

          }

        }
      } else {

        elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "messageElementQNameToOperationMap"));
        elementList.add(this.localMessageElementQNameToOperationMap);

      }

    }
    if (this.localModifyUserWSDLPortAddressTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "modifyUserWSDLPortAddress"));

      elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localModifyUserWSDLPortAddress));
    }
    if (this.localModulesTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "modules"));


      elementList.add(this.localModules == null ? null : this.localModules);
    }
    if (this.localNameTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "name"));

      elementList.add(this.localName == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localName));
    }
    if (this.localNameSpacesMapTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "nameSpacesMap"));


      elementList.add(this.localNameSpacesMap == null ? null : this.localNameSpacesMap);
    }
    if (this.localNamespaceMapTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "namespaceMap"));


      elementList.add(this.localNamespaceMap == null ? null : this.localNamespaceMap);
    }
    if (this.localObjectSupplierTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "objectSupplier"));


      elementList.add(this.localObjectSupplier == null ? null : this.localObjectSupplier);
    }
    if (this.localOperationsTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "operations"));


      elementList.add(this.localOperations == null ? null : this.localOperations);
    }
    if (this.localOperationsNameListTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "operationsNameList"));


      elementList.add(this.localOperationsNameList == null ? null : this.localOperationsNameList);
    }
    if (this.localP2NMapTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "p2nMap"));


      elementList.add(this.localP2NMap == null ? null : this.localP2NMap);
    }
    if (this.localParentTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "parent"));


      elementList.add(this.localParent == null ? null : this.localParent);
    }
    if (this.localPortTypeNameTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "portTypeName"));

      elementList.add(this.localPortTypeName == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localPortTypeName));
    }
    if (this.localPublishedOperationsTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "publishedOperations"));


      elementList.add(this.localPublishedOperations == null ? null : this.localPublishedOperations);
    }
    if (this.localSchemaLocationsAdjustedTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "schemaLocationsAdjusted"));

      elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localSchemaLocationsAdjusted));
    }
    if (this.localSchemaMappingTableTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "schemaMappingTable"));


      elementList.add(this.localSchemaMappingTable == null ? null : this.localSchemaMappingTable);
    }
    if (this.localSchemaTargetNamespaceTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "schemaTargetNamespace"));

      elementList.add(this.localSchemaTargetNamespace == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localSchemaTargetNamespace));
    }
    if (this.localSchemaTargetNamespacePrefixTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "schemaTargetNamespacePrefix"));

      elementList.add(this.localSchemaTargetNamespacePrefix == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localSchemaTargetNamespacePrefix));
    }
    if (this.localSchematargetNamespaceETracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "schematargetNamespace"));

      elementList.add(this.localSchematargetNamespaceE == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localSchematargetNamespaceE));
    }
    if (this.localSchematargetNamespacePrefixETracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "schematargetNamespacePrefix"));

      elementList.add(this.localSchematargetNamespacePrefixE == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localSchematargetNamespacePrefixE));
    }
    if (this.localScopeTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "scope"));

      elementList.add(this.localScope == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localScope));
    }
    if (this.localServiceDescriptionTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "serviceDescription"));

      elementList.add(this.localServiceDescription == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localServiceDescription));
    }
    if (this.localServiceLifeCycleTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "serviceLifeCycle"));


      elementList.add(this.localServiceLifeCycle == null ? null : this.localServiceLifeCycle);
    }
    if (this.localSetEndpointsToAllUsedBindingsTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "setEndpointsToAllUsedBindings"));

      elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localSetEndpointsToAllUsedBindings));
    }
    if (this.localSoapNsUriTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "soapNsUri"));

      elementList.add(this.localSoapNsUri == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localSoapNsUri));
    }
    if (this.localTargetNamespaceTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "targetNamespace"));

      elementList.add(this.localTargetNamespace == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localTargetNamespace));
    }
    if (this.localTargetNamespacePrefixTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "targetNamespacePrefix"));

      elementList.add(this.localTargetNamespacePrefix == null ? null
        : org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localTargetNamespacePrefix));
    }
    if (this.localTypeTableTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "typeTable"));


      elementList.add(this.localTypeTable == null ? null : this.localTypeTable);
    }
    if (this.localUseDefaultChainsTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
        "useDefaultChains"));

      elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localUseDefaultChains));
    }
    if (this.localUseUserWSDLTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "useUserWSDL"));

      elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localUseUserWSDL));
    }
    if (this.localWsdlFoundTracker) {
      elementList.add(new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd", "wsdlFound"));

      elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localWsdlFound));
    }

    return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
      attribList.toArray());


  }


  /**
   * Factory class that keeps the parse method
   */
  public static class Factory {


    /**
     * static method to create the object Precondition: If this object is an element, the current or
     * next start element starts this object and any intervening reader events are ignorable If this
     * object is not an element, it is a complex type and the reader is at the event just after the
     * outer start element Postcondition: If this object is an element, the reader is positioned at its
     * end element If this object is a complex type, the reader is positioned at the end element of its
     * outer element
     */
    public static AxisService parse(final javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
      final AxisService object = new AxisService();

      int event;
      java.lang.String nillableValue = null;
      final java.lang.String prefix = "";
      final java.lang.String namespaceuri = "";
      try {

        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }


        if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
          final java.lang.String fullTypeName =
            reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type");
          if (fullTypeName != null) {
            java.lang.String nsPrefix = null;
            if (fullTypeName.indexOf(":") > -1) {
              nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
            }
            nsPrefix = nsPrefix == null ? "" : nsPrefix;

            final java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

            if (!"AxisService".equals(type)) {
              // find namespace for the prefix
              final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
              return (AxisService) org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(nsUri,
                type,
                reader);
            }


          }


        }


        // Note all attributes that were handled. Used to differ normal attributes
        // from anyAttributes.
        final java.util.Vector handledAttributes = new java.util.Vector();


        reader.next();

        final java.util.ArrayList list1 = new java.util.ArrayList();

        final java.util.ArrayList list18 = new java.util.ArrayList();

        final java.util.ArrayList list24 = new java.util.ArrayList();


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "EPRs").equals(reader.getName())) {


          // Process the array and step past its final element's end.

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            list1.add(null);

            reader.next();
          } else {
            list1.add(reader.getElementText());
          }
          // loop until we find a start element that is not part of this array
          boolean loopDone1 = false;
          while (!loopDone1) {
            // Ensure we are at the EndElement
            while (!reader.isEndElement()) {
              reader.next();
            }
            // Step out of this element
            reader.next();
            // Step to next element event.
            while (!reader.isStartElement() && !reader.isEndElement()) {
              reader.next();
            }
            if (reader.isEndElement()) {
              // two continuous end elements means we are exiting the xml structure
              loopDone1 = true;
            } else {
              if (new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                "EPRs").equals(reader.getName())) {

                nillableValue =
                  reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                  list1.add(null);

                  reader.next();
                } else {
                  list1.add(reader.getElementText());
                }
              } else {
                loopDone1 = true;
              }
            }
          }
          // call the converter utility to convert and set the array

          object.setEPRs((java.lang.String[]) list1.toArray(new java.lang.String[list1.size()]));

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "WSAddressingFlag").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

            final java.lang.String content = reader.getElementText();

            object.setWSAddressingFlag(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

          } else {


            reader.getElementText(); // throw away text nodes if any.
          }

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "active").equals(reader.getName())) {

          final java.lang.String content = reader.getElementText();

          object.setActive(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "axisServiceGroup").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            object.setAxisServiceGroup(null);
            reader.next();

            reader.next();

          } else {

            object.setAxisServiceGroup(org.apache.axis2.description.xsd.AxisServiceGroup.Factory.parse(reader));

            reader.next();
          }
        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "bindingName").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

            final java.lang.String content = reader.getElementText();

            object.setBindingName(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

          } else {


            reader.getElementText(); // throw away text nodes if any.
          }

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "classLoader").equals(reader.getName())) {

          object.setClassLoader(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
            org.apache.axis2.transaction.xsd.ExtensionMapper.class));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "clientSide").equals(reader.getName())) {

          final java.lang.String content = reader.getElementText();

          object.setClientSide(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "controlOperations").equals(reader.getName())) {

          object.setControlOperations(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
            org.apache.axis2.transaction.xsd.ExtensionMapper.class));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "customSchemaNamePrefix").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

            final java.lang.String content = reader.getElementText();

            object.setCustomSchemaNamePrefix(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

          } else {


            reader.getElementText(); // throw away text nodes if any.
          }

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "customSchemaNameSuffix").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

            final java.lang.String content = reader.getElementText();

            object.setCustomSchemaNameSuffix(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

          } else {


            reader.getElementText(); // throw away text nodes if any.
          }

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "customWsdl").equals(reader.getName())) {

          final java.lang.String content = reader.getElementText();

          object.setCustomWsdl(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "elementFormDefault").equals(reader.getName())) {

          final java.lang.String content = reader.getElementText();

          object.setElementFormDefault(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "enableAllTransports").equals(reader.getName())) {

          final java.lang.String content = reader.getElementText();

          object.setEnableAllTransports(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "endpointName").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

            final java.lang.String content = reader.getElementText();

            object.setEndpointName(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

          } else {


            reader.getElementText(); // throw away text nodes if any.
          }

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "endpointURL").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

            final java.lang.String content = reader.getElementText();

            object.setEndpointURL(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

          } else {


            reader.getElementText(); // throw away text nodes if any.
          }

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "endpoints").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            object.setEndpoints(null);
            reader.next();

            reader.next();

          } else {

            object.setEndpoints(authclient.java.util.xsd.Map.Factory.parse(reader));

            reader.next();
          }
        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "excludeInfo").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            object.setExcludeInfo(null);
            reader.next();

            reader.next();

          } else {

            object.setExcludeInfo(org.apache.axis2.deployment.util.xsd.ExcludeInfo.Factory.parse(reader));

            reader.next();
          }
        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "exposedTransports").equals(reader.getName())) {


          // Process the array and step past its final element's end.

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            list18.add(null);

            reader.next();
          } else {
            list18.add(reader.getElementText());
          }
          // loop until we find a start element that is not part of this array
          boolean loopDone18 = false;
          while (!loopDone18) {
            // Ensure we are at the EndElement
            while (!reader.isEndElement()) {
              reader.next();
            }
            // Step out of this element
            reader.next();
            // Step to next element event.
            while (!reader.isStartElement() && !reader.isEndElement()) {
              reader.next();
            }
            if (reader.isEndElement()) {
              // two continuous end elements means we are exiting the xml structure
              loopDone18 = true;
            } else {
              if (new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
                "exposedTransports").equals(reader.getName())) {

                nillableValue =
                  reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                  list18.add(null);

                  reader.next();
                } else {
                  list18.add(reader.getElementText());
                }
              } else {
                loopDone18 = true;
              }
            }
          }
          // call the converter utility to convert and set the array

          object.setExposedTransports((java.lang.String[]) list18.toArray(new java.lang.String[list18.size()]));

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "fileName").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            object.setFileName(null);
            reader.next();

            reader.next();

          } else {

            object.setFileName(authclient.java.net.xsd.URL.Factory.parse(reader));

            reader.next();
          }
        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "importedNamespaces").equals(reader.getName())) {

          object.setImportedNamespaces(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
            org.apache.axis2.transaction.xsd.ExtensionMapper.class));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "key").equals(reader.getName())) {

          object.setKey(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
            org.apache.axis2.transaction.xsd.ExtensionMapper.class));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "lastUpdate").equals(reader.getName())) {

          final java.lang.String content = reader.getElementText();

          object.setLastUpdate(org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));

          reader.next();

        } // End of if for expected property start element

        else {

          object.setLastUpdate(java.lang.Long.MIN_VALUE);

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "lastupdate").equals(reader.getName())) {

          final java.lang.String content = reader.getElementText();

          object.setLastupdateE(org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));

          reader.next();

        } // End of if for expected property start element

        else {

          object.setLastupdateE(java.lang.Long.MIN_VALUE);

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "messageElementQNameToOperationMap").equals(reader.getName())) {


          // Process the array and step past its final element's end.


          boolean loopDone24 = false;
          final javax.xml.namespace.QName startQname24 = new javax.xml.namespace.QName(
            "http://description.axis2.apache.org/xsd", "messageElementQNameToOperationMap");

          while (!loopDone24) {
            event = reader.getEventType();
            if (javax.xml.stream.XMLStreamConstants.START_ELEMENT == event
              && startQname24.equals(reader.getName())) {


              nillableValue =
                reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
              if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                list24.add(null);
                reader.next();
              } else {
                list24.add(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                  org.apache.axis2.transaction.xsd.ExtensionMapper.class));
              }
            } else if (javax.xml.stream.XMLStreamConstants.START_ELEMENT == event
              && !startQname24.equals(reader.getName())) {
              loopDone24 = true;
            } else if (javax.xml.stream.XMLStreamConstants.END_ELEMENT == event
              && !startQname24.equals(reader.getName())) {
              loopDone24 = true;
            } else if (javax.xml.stream.XMLStreamConstants.END_DOCUMENT == event) {
              loopDone24 = true;
            } else {
              reader.next();
            }

          }


          object.setMessageElementQNameToOperationMap(list24.toArray());

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "modifyUserWSDLPortAddress").equals(reader.getName())) {

          final java.lang.String content = reader.getElementText();

          object.setModifyUserWSDLPortAddress(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "modules").equals(reader.getName())) {

          object.setModules(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
            org.apache.axis2.transaction.xsd.ExtensionMapper.class));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "name").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

            final java.lang.String content = reader.getElementText();

            object.setName(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

          } else {


            reader.getElementText(); // throw away text nodes if any.
          }

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "nameSpacesMap").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            object.setNameSpacesMap(null);
            reader.next();

            reader.next();

          } else {

            object.setNameSpacesMap(authclient.java.util.xsd.Map.Factory.parse(reader));

            reader.next();
          }
        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "namespaceMap").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            object.setNamespaceMap(null);
            reader.next();

            reader.next();

          } else {

            object.setNamespaceMap(authclient.java.util.xsd.Map.Factory.parse(reader));

            reader.next();
          }
        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "objectSupplier").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            object.setObjectSupplier(null);
            reader.next();

            reader.next();

          } else {

            object.setObjectSupplier(org.apache.axis2.engine.xsd.ObjectSupplier.Factory.parse(reader));

            reader.next();
          }
        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "operations").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            object.setOperations(null);
            reader.next();

            reader.next();

          } else {

            object.setOperations(authclient.java.util.xsd.Iterator.Factory.parse(reader));

            reader.next();
          }
        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "operationsNameList").equals(reader.getName())) {

          object.setOperationsNameList(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
            org.apache.axis2.transaction.xsd.ExtensionMapper.class));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "p2nMap").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            object.setP2NMap(null);
            reader.next();

            reader.next();

          } else {

            object.setP2NMap(authclient.java.util.xsd.Map.Factory.parse(reader));

            reader.next();
          }
        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "parent").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            object.setParent(null);
            reader.next();

            reader.next();

          } else {

            object.setParent(org.apache.axis2.description.xsd.AxisServiceGroup.Factory.parse(reader));

            reader.next();
          }
        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "portTypeName").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

            final java.lang.String content = reader.getElementText();

            object.setPortTypeName(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

          } else {


            reader.getElementText(); // throw away text nodes if any.
          }

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "publishedOperations").equals(reader.getName())) {

          object.setPublishedOperations(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
            org.apache.axis2.transaction.xsd.ExtensionMapper.class));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "schemaLocationsAdjusted").equals(reader.getName())) {

          final java.lang.String content = reader.getElementText();

          object.setSchemaLocationsAdjusted(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "schemaMappingTable").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            object.setSchemaMappingTable(null);
            reader.next();

            reader.next();

          } else {

            object.setSchemaMappingTable(authclient.java.util.xsd.Map.Factory.parse(reader));

            reader.next();
          }
        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "schemaTargetNamespace").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

            final java.lang.String content = reader.getElementText();

            object.setSchemaTargetNamespace(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

          } else {


            reader.getElementText(); // throw away text nodes if any.
          }

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "schemaTargetNamespacePrefix").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

            final java.lang.String content = reader.getElementText();

            object.setSchemaTargetNamespacePrefix(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

          } else {


            reader.getElementText(); // throw away text nodes if any.
          }

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "schematargetNamespace").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

            final java.lang.String content = reader.getElementText();

            object.setSchematargetNamespaceE(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

          } else {


            reader.getElementText(); // throw away text nodes if any.
          }

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "schematargetNamespacePrefix").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

            final java.lang.String content = reader.getElementText();

            object.setSchematargetNamespacePrefixE(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

          } else {


            reader.getElementText(); // throw away text nodes if any.
          }

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "scope").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

            final java.lang.String content = reader.getElementText();

            object.setScope(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

          } else {


            reader.getElementText(); // throw away text nodes if any.
          }

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "serviceDescription").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

            final java.lang.String content = reader.getElementText();

            object.setServiceDescription(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

          } else {


            reader.getElementText(); // throw away text nodes if any.
          }

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "serviceLifeCycle").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            object.setServiceLifeCycle(null);
            reader.next();

            reader.next();

          } else {

            object.setServiceLifeCycle(org.apache.axis2.engine.xsd.ServiceLifeCycle.Factory.parse(reader));

            reader.next();
          }
        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "setEndpointsToAllUsedBindings").equals(reader.getName())) {

          final java.lang.String content = reader.getElementText();

          object.setSetEndpointsToAllUsedBindings(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "soapNsUri").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

            final java.lang.String content = reader.getElementText();

            object.setSoapNsUri(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

          } else {


            reader.getElementText(); // throw away text nodes if any.
          }

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "targetNamespace").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

            final java.lang.String content = reader.getElementText();

            object.setTargetNamespace(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

          } else {


            reader.getElementText(); // throw away text nodes if any.
          }

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "targetNamespacePrefix").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if (!"true".equals(nillableValue) && !"1".equals(nillableValue)) {

            final java.lang.String content = reader.getElementText();

            object.setTargetNamespacePrefix(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

          } else {


            reader.getElementText(); // throw away text nodes if any.
          }

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "typeTable").equals(reader.getName())) {

          nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
          if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
            object.setTypeTable(null);
            reader.next();

            reader.next();

          } else {

            object.setTypeTable(org.apache.axis2.description.java2wsdl.xsd.TypeTable.Factory.parse(reader));

            reader.next();
          }
        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "useDefaultChains").equals(reader.getName())) {

          final java.lang.String content = reader.getElementText();

          object.setUseDefaultChains(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "useUserWSDL").equals(reader.getName())) {

          final java.lang.String content = reader.getElementText();

          object.setUseUserWSDL(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

          reader.next();

        } // End of if for expected property start element

        else {

        }


        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement() && new javax.xml.namespace.QName("http://description.axis2.apache.org/xsd",
          "wsdlFound").equals(reader.getName())) {

          final java.lang.String content = reader.getElementText();

          object.setWsdlFound(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

          reader.next();

        } // End of if for expected property start element

        else {

        }

        while (!reader.isStartElement() && !reader.isEndElement()) {
          reader.next();
        }

        if (reader.isStartElement()) {
          // A start element we are not expecting indicates a trailing invalid
          // property
          throw new org.apache.axis2.databinding.ADBException(
            "Unexpected subelement " + reader.getLocalName());
        }


      } catch (final javax.xml.stream.XMLStreamException e) {
        throw new java.lang.Exception(e);
      }

      return object;
    }

  }// end of factory class


}

