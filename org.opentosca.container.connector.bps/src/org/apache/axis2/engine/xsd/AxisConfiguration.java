
/**
 * AxisConfiguration.java
 *
 * This file was auto-generated from WSDL by the Apache Axis2 version: SNAPSHOT Built on : Nov 10,
 * 2010 (06:34:21 UTC)
 */


package org.apache.axis2.engine.xsd;


/**
 * AxisConfiguration bean class
 */

public class AxisConfiguration implements org.apache.axis2.databinding.ADBBean {
    /*
     * This type was generated from the piece of schema that had name = AxisConfiguration Namespace URI
     * = http://engine.axis2.apache.org/xsd Namespace Prefix = ns8
     */


    /**
     *
     */
    private static final long serialVersionUID = -4972409878060057379L;

    /**
     * field for ChildFirstClassLoading
     */


    protected boolean localChildFirstClassLoading;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localChildFirstClassLoadingTracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getChildFirstClassLoading() {
        return this.localChildFirstClassLoading;
    }



    /**
     * Auto generated setter method
     *
     * @param param ChildFirstClassLoading
     */
    public void setChildFirstClassLoading(final boolean param) {

        // setting primitive attribute tracker to true
        this.localChildFirstClassLoadingTracker = true;

        this.localChildFirstClassLoading = param;


    }


    /**
     * field for ClusteringAgent
     */


    protected org.apache.axis2.clustering.xsd.ClusteringAgent localClusteringAgent;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localClusteringAgentTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.clustering.xsd.ClusteringAgent
     */
    public org.apache.axis2.clustering.xsd.ClusteringAgent getClusteringAgent() {
        return this.localClusteringAgent;
    }



    /**
     * Auto generated setter method
     *
     * @param param ClusteringAgent
     */
    public void setClusteringAgent(final org.apache.axis2.clustering.xsd.ClusteringAgent param) {
        this.localClusteringAgentTracker = true;

        this.localClusteringAgent = param;


    }


    /**
     * field for Configurator
     */


    protected org.apache.axis2.engine.xsd.AxisConfigurator localConfigurator;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localConfiguratorTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.engine.xsd.AxisConfigurator
     */
    public org.apache.axis2.engine.xsd.AxisConfigurator getConfigurator() {
        return this.localConfigurator;
    }



    /**
     * Auto generated setter method
     *
     * @param param Configurator
     */
    public void setConfigurator(final org.apache.axis2.engine.xsd.AxisConfigurator param) {
        this.localConfiguratorTracker = true;

        this.localConfigurator = param;


    }


    /**
     * field for FaultyModules This was an Array!
     */


    protected java.lang.String[] localFaultyModules;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localFaultyModulesTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String[]
     */
    public java.lang.String[] getFaultyModules() {
        return this.localFaultyModules;
    }



    /**
     * validate the array for FaultyModules
     */
    protected void validateFaultyModules(final java.lang.String[] param) {

    }


    /**
     * Auto generated setter method
     *
     * @param param FaultyModules
     */
    public void setFaultyModules(final java.lang.String[] param) {

        validateFaultyModules(param);

        this.localFaultyModulesTracker = true;

        this.localFaultyModules = param;
    }



    /**
     * Auto generated add method for the array for convenience
     *
     * @param param java.lang.String
     */
    public void addFaultyModules(final java.lang.String param) {
        if (this.localFaultyModules == null) {
            this.localFaultyModules = new java.lang.String[] {};
        }


        // update the setting tracker
        this.localFaultyModulesTracker = true;


        final java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(this.localFaultyModules);
        list.add(param);
        this.localFaultyModules = (java.lang.String[]) list.toArray(new java.lang.String[list.size()]);

    }


    /**
     * field for FaultyServices This was an Array!
     */


    protected java.lang.String[] localFaultyServices;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localFaultyServicesTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String[]
     */
    public java.lang.String[] getFaultyServices() {
        return this.localFaultyServices;
    }



    /**
     * validate the array for FaultyServices
     */
    protected void validateFaultyServices(final java.lang.String[] param) {

    }


    /**
     * Auto generated setter method
     *
     * @param param FaultyServices
     */
    public void setFaultyServices(final java.lang.String[] param) {

        validateFaultyServices(param);

        this.localFaultyServicesTracker = true;

        this.localFaultyServices = param;
    }



    /**
     * Auto generated add method for the array for convenience
     *
     * @param param java.lang.String
     */
    public void addFaultyServices(final java.lang.String param) {
        if (this.localFaultyServices == null) {
            this.localFaultyServices = new java.lang.String[] {};
        }


        // update the setting tracker
        this.localFaultyServicesTracker = true;


        final java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(this.localFaultyServices);
        list.add(param);
        this.localFaultyServices = (java.lang.String[]) list.toArray(new java.lang.String[list.size()]);

    }


    /**
     * field for FaultyServicesDuetoModules
     */


    protected authclient.java.util.xsd.Map localFaultyServicesDuetoModules;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localFaultyServicesDuetoModulesTracker = false;


    /**
     * Auto generated getter method
     *
     * @return authclient.java.util.xsd.Map
     */
    public authclient.java.util.xsd.Map getFaultyServicesDuetoModules() {
        return this.localFaultyServicesDuetoModules;
    }



    /**
     * Auto generated setter method
     *
     * @param param FaultyServicesDuetoModules
     */
    public void setFaultyServicesDuetoModules(final authclient.java.util.xsd.Map param) {
        this.localFaultyServicesDuetoModulesTracker = true;

        this.localFaultyServicesDuetoModules = param;


    }


    /**
     * field for GlobalModules
     */


    protected java.lang.Object localGlobalModules;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localGlobalModulesTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getGlobalModules() {
        return this.localGlobalModules;
    }



    /**
     * Auto generated setter method
     *
     * @param param GlobalModules
     */
    public void setGlobalModules(final java.lang.Object param) {
        this.localGlobalModulesTracker = true;

        this.localGlobalModules = param;


    }


    /**
     * field for GlobalOutPhase
     */


    protected java.lang.Object localGlobalOutPhase;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localGlobalOutPhaseTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getGlobalOutPhase() {
        return this.localGlobalOutPhase;
    }



    /**
     * Auto generated setter method
     *
     * @param param GlobalOutPhase
     */
    public void setGlobalOutPhase(final java.lang.Object param) {
        this.localGlobalOutPhaseTracker = true;

        this.localGlobalOutPhase = param;


    }


    /**
     * field for InFaultFlowPhases
     */


    protected java.lang.Object localInFaultFlowPhases;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localInFaultFlowPhasesTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getInFaultFlowPhases() {
        return this.localInFaultFlowPhases;
    }



    /**
     * Auto generated setter method
     *
     * @param param InFaultFlowPhases
     */
    public void setInFaultFlowPhases(final java.lang.Object param) {
        this.localInFaultFlowPhasesTracker = true;

        this.localInFaultFlowPhases = param;


    }


    /**
     * field for InFaultPhases
     */


    protected java.lang.Object localInFaultPhases;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localInFaultPhasesTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getInFaultPhases() {
        return this.localInFaultPhases;
    }



    /**
     * Auto generated setter method
     *
     * @param param InFaultPhases
     */
    public void setInFaultPhases(final java.lang.Object param) {
        this.localInFaultPhasesTracker = true;

        this.localInFaultPhases = param;


    }


    /**
     * field for InFlowPhases
     */


    protected java.lang.Object localInFlowPhases;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localInFlowPhasesTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getInFlowPhases() {
        return this.localInFlowPhases;
    }



    /**
     * Auto generated setter method
     *
     * @param param InFlowPhases
     */
    public void setInFlowPhases(final java.lang.Object param) {
        this.localInFlowPhasesTracker = true;

        this.localInFlowPhases = param;


    }


    /**
     * field for InPhasesUptoAndIncludingPostDispatch
     */


    protected java.lang.Object localInPhasesUptoAndIncludingPostDispatch;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localInPhasesUptoAndIncludingPostDispatchTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getInPhasesUptoAndIncludingPostDispatch() {
        return this.localInPhasesUptoAndIncludingPostDispatch;
    }



    /**
     * Auto generated setter method
     *
     * @param param InPhasesUptoAndIncludingPostDispatch
     */
    public void setInPhasesUptoAndIncludingPostDispatch(final java.lang.Object param) {
        this.localInPhasesUptoAndIncludingPostDispatchTracker = true;

        this.localInPhasesUptoAndIncludingPostDispatch = param;


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
     * field for LocalPolicyAssertions This was an Array!
     */


    protected java.lang.Object[] localLocalPolicyAssertions;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localLocalPolicyAssertionsTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object[]
     */
    public java.lang.Object[] getLocalPolicyAssertions() {
        return this.localLocalPolicyAssertions;
    }



    /**
     * validate the array for LocalPolicyAssertions
     */
    protected void validateLocalPolicyAssertions(final java.lang.Object[] param) {

    }


    /**
     * Auto generated setter method
     *
     * @param param LocalPolicyAssertions
     */
    public void setLocalPolicyAssertions(final java.lang.Object[] param) {

        validateLocalPolicyAssertions(param);

        this.localLocalPolicyAssertionsTracker = true;

        this.localLocalPolicyAssertions = param;
    }



    /**
     * Auto generated add method for the array for convenience
     *
     * @param param java.lang.Object
     */
    public void addLocalPolicyAssertions(final java.lang.Object param) {
        if (this.localLocalPolicyAssertions == null) {
            this.localLocalPolicyAssertions = new java.lang.Object[] {};
        }


        // update the setting tracker
        this.localLocalPolicyAssertionsTracker = true;


        final java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(
            this.localLocalPolicyAssertions);
        list.add(param);
        this.localLocalPolicyAssertions = list.toArray(new java.lang.Object[list.size()]);

    }


    /**
     * field for ModuleClassLoader
     */


    protected java.lang.Object localModuleClassLoader;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localModuleClassLoaderTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getModuleClassLoader() {
        return this.localModuleClassLoader;
    }



    /**
     * Auto generated setter method
     *
     * @param param ModuleClassLoader
     */
    public void setModuleClassLoader(final java.lang.Object param) {
        this.localModuleClassLoaderTracker = true;

        this.localModuleClassLoader = param;


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
     * field for ObserversList
     */


    protected java.lang.Object localObserversList;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localObserversListTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getObserversList() {
        return this.localObserversList;
    }



    /**
     * Auto generated setter method
     *
     * @param param ObserversList
     */
    public void setObserversList(final java.lang.Object param) {
        this.localObserversListTracker = true;

        this.localObserversList = param;


    }


    /**
     * field for OutFaultFlowPhases
     */


    protected java.lang.Object localOutFaultFlowPhases;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localOutFaultFlowPhasesTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getOutFaultFlowPhases() {
        return this.localOutFaultFlowPhases;
    }



    /**
     * Auto generated setter method
     *
     * @param param OutFaultFlowPhases
     */
    public void setOutFaultFlowPhases(final java.lang.Object param) {
        this.localOutFaultFlowPhasesTracker = true;

        this.localOutFaultFlowPhases = param;


    }


    /**
     * field for OutFaultPhases
     */


    protected java.lang.Object localOutFaultPhases;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localOutFaultPhasesTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getOutFaultPhases() {
        return this.localOutFaultPhases;
    }



    /**
     * Auto generated setter method
     *
     * @param param OutFaultPhases
     */
    public void setOutFaultPhases(final java.lang.Object param) {
        this.localOutFaultPhasesTracker = true;

        this.localOutFaultPhases = param;


    }


    /**
     * field for OutFlowPhases
     */


    protected java.lang.Object localOutFlowPhases;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localOutFlowPhasesTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getOutFlowPhases() {
        return this.localOutFlowPhases;
    }



    /**
     * Auto generated setter method
     *
     * @param param OutFlowPhases
     */
    public void setOutFlowPhases(final java.lang.Object param) {
        this.localOutFlowPhasesTracker = true;

        this.localOutFlowPhases = param;


    }


    /**
     * field for PhasesInfo
     */


    protected org.apache.axis2.deployment.util.xsd.PhasesInfo localPhasesInfo;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localPhasesInfoTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.deployment.util.xsd.PhasesInfo
     */
    public org.apache.axis2.deployment.util.xsd.PhasesInfo getPhasesInfo() {
        return this.localPhasesInfo;
    }



    /**
     * Auto generated setter method
     *
     * @param param PhasesInfo
     */
    public void setPhasesInfo(final org.apache.axis2.deployment.util.xsd.PhasesInfo param) {
        this.localPhasesInfoTracker = true;

        this.localPhasesInfo = param;


    }


    /**
     * field for Repository
     */


    protected authclient.java.net.xsd.URL localRepository;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localRepositoryTracker = false;


    /**
     * Auto generated getter method
     *
     * @return authclient.java.net.xsd.URL
     */
    public authclient.java.net.xsd.URL getRepository() {
        return this.localRepository;
    }



    /**
     * Auto generated setter method
     *
     * @param param Repository
     */
    public void setRepository(final authclient.java.net.xsd.URL param) {
        this.localRepositoryTracker = true;

        this.localRepository = param;


    }


    /**
     * field for SecretResolver
     */


    protected org.wso2.securevault.xsd.SecretResolver localSecretResolver;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localSecretResolverTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.wso2.securevault.xsd.SecretResolver
     */
    public org.wso2.securevault.xsd.SecretResolver getSecretResolver() {
        return this.localSecretResolver;
    }



    /**
     * Auto generated setter method
     *
     * @param param SecretResolver
     */
    public void setSecretResolver(final org.wso2.securevault.xsd.SecretResolver param) {
        this.localSecretResolverTracker = true;

        this.localSecretResolver = param;


    }


    /**
     * field for ServiceClassLoader
     */


    protected java.lang.Object localServiceClassLoader;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localServiceClassLoaderTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getServiceClassLoader() {
        return this.localServiceClassLoader;
    }



    /**
     * Auto generated setter method
     *
     * @param param ServiceClassLoader
     */
    public void setServiceClassLoader(final java.lang.Object param) {
        this.localServiceClassLoaderTracker = true;

        this.localServiceClassLoader = param;


    }


    /**
     * field for ServiceGroups
     */


    protected authclient.java.util.xsd.Iterator localServiceGroups;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localServiceGroupsTracker = false;


    /**
     * Auto generated getter method
     *
     * @return authclient.java.util.xsd.Iterator
     */
    public authclient.java.util.xsd.Iterator getServiceGroups() {
        return this.localServiceGroups;
    }



    /**
     * Auto generated setter method
     *
     * @param param ServiceGroups
     */
    public void setServiceGroups(final authclient.java.util.xsd.Iterator param) {
        this.localServiceGroupsTracker = true;

        this.localServiceGroups = param;


    }


    /**
     * field for Services
     */


    protected java.lang.Object localServices;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localServicesTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getServices() {
        return this.localServices;
    }



    /**
     * Auto generated setter method
     *
     * @param param Services
     */
    public void setServices(final java.lang.Object param) {
        this.localServicesTracker = true;

        this.localServices = param;


    }


    /**
     * field for Start
     */


    protected boolean localStart;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localStartTracker = false;


    /**
     * Auto generated getter method
     *
     * @return boolean
     */
    public boolean getStart() {
        return this.localStart;
    }



    /**
     * Auto generated setter method
     *
     * @param param Start
     */
    public void setStart(final boolean param) {

        // setting primitive attribute tracker to true
        this.localStartTracker = true;

        this.localStart = param;


    }


    /**
     * field for SystemClassLoader
     */


    protected java.lang.Object localSystemClassLoader;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localSystemClassLoaderTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.Object
     */
    public java.lang.Object getSystemClassLoader() {
        return this.localSystemClassLoader;
    }



    /**
     * Auto generated setter method
     *
     * @param param SystemClassLoader
     */
    public void setSystemClassLoader(final java.lang.Object param) {
        this.localSystemClassLoaderTracker = true;

        this.localSystemClassLoader = param;


    }


    /**
     * field for TargetResolverChain
     */


    protected org.apache.axis2.util.xsd.TargetResolver localTargetResolverChain;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localTargetResolverChainTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.util.xsd.TargetResolver
     */
    public org.apache.axis2.util.xsd.TargetResolver getTargetResolverChain() {
        return this.localTargetResolverChain;
    }



    /**
     * Auto generated setter method
     *
     * @param param TargetResolverChain
     */
    public void setTargetResolverChain(final org.apache.axis2.util.xsd.TargetResolver param) {
        this.localTargetResolverChainTracker = true;

        this.localTargetResolverChain = param;


    }


    /**
     * field for TransactionConfig
     */


    protected org.apache.axis2.transaction.xsd.TransactionConfiguration localTransactionConfig;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localTransactionConfigTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.transaction.xsd.TransactionConfiguration
     */
    public org.apache.axis2.transaction.xsd.TransactionConfiguration getTransactionConfig() {
        return this.localTransactionConfig;
    }



    /**
     * Auto generated setter method
     *
     * @param param TransactionConfig
     */
    public void setTransactionConfig(final org.apache.axis2.transaction.xsd.TransactionConfiguration param) {
        this.localTransactionConfigTracker = true;

        this.localTransactionConfig = param;


    }


    /**
     * field for TransactionConfiguration
     */


    protected org.apache.axis2.transaction.xsd.TransactionConfiguration localTransactionConfiguration;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localTransactionConfigurationTracker = false;


    /**
     * Auto generated getter method
     *
     * @return org.apache.axis2.transaction.xsd.TransactionConfiguration
     */
    public org.apache.axis2.transaction.xsd.TransactionConfiguration getTransactionConfiguration() {
        return this.localTransactionConfiguration;
    }



    /**
     * Auto generated setter method
     *
     * @param param TransactionConfiguration
     */
    public void setTransactionConfiguration(final org.apache.axis2.transaction.xsd.TransactionConfiguration param) {
        this.localTransactionConfigurationTracker = true;

        this.localTransactionConfiguration = param;


    }


    /**
     * field for TransportsIn This was an Array!
     */


    protected java.lang.String[] localTransportsIn;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localTransportsInTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String[]
     */
    public java.lang.String[] getTransportsIn() {
        return this.localTransportsIn;
    }



    /**
     * validate the array for TransportsIn
     */
    protected void validateTransportsIn(final java.lang.String[] param) {

    }


    /**
     * Auto generated setter method
     *
     * @param param TransportsIn
     */
    public void setTransportsIn(final java.lang.String[] param) {

        validateTransportsIn(param);

        this.localTransportsInTracker = true;

        this.localTransportsIn = param;
    }



    /**
     * Auto generated add method for the array for convenience
     *
     * @param param java.lang.String
     */
    public void addTransportsIn(final java.lang.String param) {
        if (this.localTransportsIn == null) {
            this.localTransportsIn = new java.lang.String[] {};
        }


        // update the setting tracker
        this.localTransportsInTracker = true;


        final java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(this.localTransportsIn);
        list.add(param);
        this.localTransportsIn = (java.lang.String[]) list.toArray(new java.lang.String[list.size()]);

    }


    /**
     * field for TransportsOut This was an Array!
     */


    protected java.lang.String[] localTransportsOut;

    /*
     * This tracker boolean wil be used to detect whether the user called the set method for this
     * attribute. It will be used to determine whether to include this field in the serialized XML
     */
    protected boolean localTransportsOutTracker = false;


    /**
     * Auto generated getter method
     *
     * @return java.lang.String[]
     */
    public java.lang.String[] getTransportsOut() {
        return this.localTransportsOut;
    }



    /**
     * validate the array for TransportsOut
     */
    protected void validateTransportsOut(final java.lang.String[] param) {

    }


    /**
     * Auto generated setter method
     *
     * @param param TransportsOut
     */
    public void setTransportsOut(final java.lang.String[] param) {

        validateTransportsOut(param);

        this.localTransportsOutTracker = true;

        this.localTransportsOut = param;
    }



    /**
     * Auto generated add method for the array for convenience
     *
     * @param param java.lang.String
     */
    public void addTransportsOut(final java.lang.String param) {
        if (this.localTransportsOut == null) {
            this.localTransportsOut = new java.lang.String[] {};
        }


        // update the setting tracker
        this.localTransportsOutTracker = true;


        final java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(this.localTransportsOut);
        list.add(param);
        this.localTransportsOut = (java.lang.String[]) list.toArray(new java.lang.String[list.size()]);

    }



    /**
     *
     * @param parentQName
     * @param factory
     * @return org.apache.axiom.om.OMElement
     */
    @Override
    public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                    final org.apache.axiom.om.OMFactory factory)
        throws org.apache.axis2.databinding.ADBException {



        final org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
            parentQName);
        return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);

    }

    @Override
    public void serialize(final javax.xml.namespace.QName parentQName, final javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
        serialize(parentQName, xmlWriter, false);
    }

    @Override
    public void serialize(final javax.xml.namespace.QName parentQName, final javax.xml.stream.XMLStreamWriter xmlWriter,
                    final boolean serializeType)
        throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {



        java.lang.String prefix = null;
        java.lang.String namespace = null;


        prefix = parentQName.getPrefix();
        namespace = parentQName.getNamespaceURI();
        writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);

        if (serializeType) {


            final java.lang.String namespacePrefix = registerPrefix(xmlWriter, "http://engine.axis2.apache.org/xsd");
            if (namespacePrefix != null && namespacePrefix.trim().length() > 0) {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                    namespacePrefix + ":AxisConfiguration", xmlWriter);
            } else {
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "AxisConfiguration",
                    xmlWriter);
            }


        }
        if (this.localChildFirstClassLoadingTracker) {
            namespace = "http://engine.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "childFirstClassLoading", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("childFirstClassLoading cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localChildFirstClassLoading));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localClusteringAgentTracker) {
            if (this.localClusteringAgent == null) {

                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "clusteringAgent", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localClusteringAgent.serialize(
                    new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "clusteringAgent"), xmlWriter);
            }
        }
        if (this.localConfiguratorTracker) {
            if (this.localConfigurator == null) {

                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "configurator", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localConfigurator.serialize(
                    new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "configurator"), xmlWriter);
            }
        }
        if (this.localFaultyModulesTracker) {
            if (this.localFaultyModules != null) {
                namespace = "http://engine.axis2.apache.org/xsd";
                for (final String localFaultyModule : this.localFaultyModules) {

                    if (localFaultyModule != null) {

                        writeStartElement(null, namespace, "faultyModules", xmlWriter);


                        xmlWriter.writeCharacters(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFaultyModule));

                        xmlWriter.writeEndElement();

                    } else {

                        // write null attribute
                        namespace = "http://engine.axis2.apache.org/xsd";
                        writeStartElement(null, namespace, "faultyModules", xmlWriter);
                        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                        xmlWriter.writeEndElement();

                    }

                }
            } else {

                // write the null attribute
                // write null attribute
                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "faultyModules", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }

        }
        if (this.localFaultyServicesTracker) {
            if (this.localFaultyServices != null) {
                namespace = "http://engine.axis2.apache.org/xsd";
                for (final String localFaultyService : this.localFaultyServices) {

                    if (localFaultyService != null) {

                        writeStartElement(null, namespace, "faultyServices", xmlWriter);


                        xmlWriter.writeCharacters(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFaultyService));

                        xmlWriter.writeEndElement();

                    } else {

                        // write null attribute
                        namespace = "http://engine.axis2.apache.org/xsd";
                        writeStartElement(null, namespace, "faultyServices", xmlWriter);
                        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                        xmlWriter.writeEndElement();

                    }

                }
            } else {

                // write the null attribute
                // write null attribute
                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "faultyServices", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }

        }
        if (this.localFaultyServicesDuetoModulesTracker) {
            if (this.localFaultyServicesDuetoModules == null) {

                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "faultyServicesDuetoModules", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localFaultyServicesDuetoModules.serialize(
                    new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "faultyServicesDuetoModules"),
                    xmlWriter);
            }
        }
        if (this.localGlobalModulesTracker) {

            if (this.localGlobalModules != null) {
                if (this.localGlobalModules instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localGlobalModules).serialize(
                        new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "globalModules"), xmlWriter,
                        true);
                } else {
                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "globalModules", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localGlobalModules,
                        xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "globalModules", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localGlobalOutPhaseTracker) {

            if (this.localGlobalOutPhase != null) {
                if (this.localGlobalOutPhase instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localGlobalOutPhase).serialize(
                        new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "globalOutPhase"),
                        xmlWriter, true);
                } else {
                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "globalOutPhase", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localGlobalOutPhase,
                        xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "globalOutPhase", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localInFaultFlowPhasesTracker) {

            if (this.localInFaultFlowPhases != null) {
                if (this.localInFaultFlowPhases instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localInFaultFlowPhases).serialize(
                        new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "inFaultFlowPhases"),
                        xmlWriter, true);
                } else {
                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "inFaultFlowPhases", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localInFaultFlowPhases,
                        xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "inFaultFlowPhases", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localInFaultPhasesTracker) {

            if (this.localInFaultPhases != null) {
                if (this.localInFaultPhases instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localInFaultPhases).serialize(
                        new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "inFaultPhases"), xmlWriter,
                        true);
                } else {
                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "inFaultPhases", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localInFaultPhases,
                        xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "inFaultPhases", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localInFlowPhasesTracker) {

            if (this.localInFlowPhases != null) {
                if (this.localInFlowPhases instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localInFlowPhases).serialize(
                        new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "inFlowPhases"), xmlWriter,
                        true);
                } else {
                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "inFlowPhases", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localInFlowPhases,
                        xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "inFlowPhases", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localInPhasesUptoAndIncludingPostDispatchTracker) {

            if (this.localInPhasesUptoAndIncludingPostDispatch != null) {
                if (this.localInPhasesUptoAndIncludingPostDispatch instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localInPhasesUptoAndIncludingPostDispatch).serialize(
                        new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                            "inPhasesUptoAndIncludingPostDispatch"),
                        xmlWriter, true);
                } else {
                    writeStartElement(null, "http://engine.axis2.apache.org/xsd",
                        "inPhasesUptoAndIncludingPostDispatch", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(
                        this.localInPhasesUptoAndIncludingPostDispatch, xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "inPhasesUptoAndIncludingPostDispatch",
                    xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localKeyTracker) {

            if (this.localKey != null) {
                if (this.localKey instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localKey).serialize(
                        new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "key"), xmlWriter, true);
                } else {
                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "key", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localKey, xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "key", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localLocalPolicyAssertionsTracker) {

            if (this.localLocalPolicyAssertions != null) {
                for (final Object localLocalPolicyAssertion : this.localLocalPolicyAssertions) {
                    if (localLocalPolicyAssertion != null) {

                        if (localLocalPolicyAssertion instanceof org.apache.axis2.databinding.ADBBean) {
                            ((org.apache.axis2.databinding.ADBBean) localLocalPolicyAssertion).serialize(
                                new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                    "localPolicyAssertions"),
                                xmlWriter, true);
                        } else {
                            writeStartElement(null, "http://engine.axis2.apache.org/xsd", "localPolicyAssertions",
                                xmlWriter);
                            org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(localLocalPolicyAssertion,
                                xmlWriter);
                            xmlWriter.writeEndElement();
                        }

                    } else {

                        // write null attribute
                        writeStartElement(null, "http://engine.axis2.apache.org/xsd", "localPolicyAssertions",
                            xmlWriter);

                        // write the nil attribute
                        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                        xmlWriter.writeEndElement();

                    }
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "localPolicyAssertions", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }

        }
        if (this.localModuleClassLoaderTracker) {

            if (this.localModuleClassLoader != null) {
                if (this.localModuleClassLoader instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localModuleClassLoader).serialize(
                        new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "moduleClassLoader"),
                        xmlWriter, true);
                } else {
                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "moduleClassLoader", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localModuleClassLoader,
                        xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "moduleClassLoader", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localModulesTracker) {

            if (this.localModules != null) {
                if (this.localModules instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localModules).serialize(
                        new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "modules"), xmlWriter,
                        true);
                } else {
                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "modules", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localModules, xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "modules", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localObserversListTracker) {

            if (this.localObserversList != null) {
                if (this.localObserversList instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localObserversList).serialize(
                        new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "observersList"), xmlWriter,
                        true);
                } else {
                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "observersList", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localObserversList,
                        xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "observersList", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localOutFaultFlowPhasesTracker) {

            if (this.localOutFaultFlowPhases != null) {
                if (this.localOutFaultFlowPhases instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localOutFaultFlowPhases).serialize(
                        new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "outFaultFlowPhases"),
                        xmlWriter, true);
                } else {
                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "outFaultFlowPhases", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localOutFaultFlowPhases,
                        xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "outFaultFlowPhases", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localOutFaultPhasesTracker) {

            if (this.localOutFaultPhases != null) {
                if (this.localOutFaultPhases instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localOutFaultPhases).serialize(
                        new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "outFaultPhases"),
                        xmlWriter, true);
                } else {
                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "outFaultPhases", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localOutFaultPhases,
                        xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "outFaultPhases", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localOutFlowPhasesTracker) {

            if (this.localOutFlowPhases != null) {
                if (this.localOutFlowPhases instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localOutFlowPhases).serialize(
                        new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "outFlowPhases"), xmlWriter,
                        true);
                } else {
                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "outFlowPhases", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localOutFlowPhases,
                        xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "outFlowPhases", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localPhasesInfoTracker) {
            if (this.localPhasesInfo == null) {

                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "phasesInfo", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localPhasesInfo.serialize(
                    new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "phasesInfo"), xmlWriter);
            }
        }
        if (this.localRepositoryTracker) {
            if (this.localRepository == null) {

                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "repository", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localRepository.serialize(
                    new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "repository"), xmlWriter);
            }
        }
        if (this.localSecretResolverTracker) {
            if (this.localSecretResolver == null) {

                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "secretResolver", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localSecretResolver.serialize(
                    new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "secretResolver"), xmlWriter);
            }
        }
        if (this.localServiceClassLoaderTracker) {

            if (this.localServiceClassLoader != null) {
                if (this.localServiceClassLoader instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localServiceClassLoader).serialize(
                        new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "serviceClassLoader"),
                        xmlWriter, true);
                } else {
                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "serviceClassLoader", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localServiceClassLoader,
                        xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "serviceClassLoader", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localServiceGroupsTracker) {
            if (this.localServiceGroups == null) {

                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "serviceGroups", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localServiceGroups.serialize(
                    new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "serviceGroups"), xmlWriter);
            }
        }
        if (this.localServicesTracker) {

            if (this.localServices != null) {
                if (this.localServices instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localServices).serialize(
                        new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "services"), xmlWriter,
                        true);
                } else {
                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "services", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localServices, xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "services", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localStartTracker) {
            namespace = "http://engine.axis2.apache.org/xsd";
            writeStartElement(null, namespace, "start", xmlWriter);

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("start cannot be null!!");

            } else {
                xmlWriter.writeCharacters(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localStart));
            }

            xmlWriter.writeEndElement();
        }
        if (this.localSystemClassLoaderTracker) {

            if (this.localSystemClassLoader != null) {
                if (this.localSystemClassLoader instanceof org.apache.axis2.databinding.ADBBean) {
                    ((org.apache.axis2.databinding.ADBBean) this.localSystemClassLoader).serialize(
                        new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "systemClassLoader"),
                        xmlWriter, true);
                } else {
                    writeStartElement(null, "http://engine.axis2.apache.org/xsd", "systemClassLoader", xmlWriter);
                    org.apache.axis2.databinding.utils.ConverterUtil.serializeAnyType(this.localSystemClassLoader,
                        xmlWriter);
                    xmlWriter.writeEndElement();
                }
            } else {

                // write null attribute
                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "systemClassLoader", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }


        }
        if (this.localTargetResolverChainTracker) {
            if (this.localTargetResolverChain == null) {

                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "targetResolverChain", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localTargetResolverChain.serialize(
                    new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "targetResolverChain"),
                    xmlWriter);
            }
        }
        if (this.localTransactionConfigTracker) {
            if (this.localTransactionConfig == null) {

                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "transactionConfig", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localTransactionConfig.serialize(
                    new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "transactionConfig"),
                    xmlWriter);
            }
        }
        if (this.localTransactionConfigurationTracker) {
            if (this.localTransactionConfiguration == null) {

                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "transactionConfiguration", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();
            } else {
                this.localTransactionConfiguration.serialize(
                    new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "transactionConfiguration"),
                    xmlWriter);
            }
        }
        if (this.localTransportsInTracker) {
            if (this.localTransportsIn != null) {
                namespace = "http://engine.axis2.apache.org/xsd";
                for (final String element : this.localTransportsIn) {

                    if (element != null) {

                        writeStartElement(null, namespace, "transportsIn", xmlWriter);


                        xmlWriter.writeCharacters(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(element));

                        xmlWriter.writeEndElement();

                    } else {

                        // write null attribute
                        namespace = "http://engine.axis2.apache.org/xsd";
                        writeStartElement(null, namespace, "transportsIn", xmlWriter);
                        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                        xmlWriter.writeEndElement();

                    }

                }
            } else {

                // write the null attribute
                // write null attribute
                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "transportsIn", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }

        }
        if (this.localTransportsOutTracker) {
            if (this.localTransportsOut != null) {
                namespace = "http://engine.axis2.apache.org/xsd";
                for (final String element : this.localTransportsOut) {

                    if (element != null) {

                        writeStartElement(null, namespace, "transportsOut", xmlWriter);


                        xmlWriter.writeCharacters(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(element));

                        xmlWriter.writeEndElement();

                    } else {

                        // write null attribute
                        namespace = "http://engine.axis2.apache.org/xsd";
                        writeStartElement(null, namespace, "transportsOut", xmlWriter);
                        writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                        xmlWriter.writeEndElement();

                    }

                }
            } else {

                // write the null attribute
                // write null attribute
                writeStartElement(null, "http://engine.axis2.apache.org/xsd", "transportsOut", xmlWriter);

                // write the nil attribute
                writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                xmlWriter.writeEndElement();

            }

        }
        xmlWriter.writeEndElement();


    }

    private static java.lang.String generatePrefix(final java.lang.String namespace) {
        if (namespace.equals("http://engine.axis2.apache.org/xsd")) {
            return "ns8";
        }
        return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
    }

    /**
     * Utility method to write an element start tag.
     */
    private void writeStartElement(java.lang.String prefix, final java.lang.String namespace,
                    final java.lang.String localPart, final javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
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
                    final javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
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
                    final java.lang.String attValue, final javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
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
                    final javax.xml.namespace.QName qname, final javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {

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

    private void writeQName(final javax.xml.namespace.QName qname, final javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {
        final java.lang.String namespaceURI = qname.getNamespaceURI();
        if (namespaceURI != null) {
            java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
            if (prefix == null) {
                prefix = generatePrefix(namespaceURI);
                xmlWriter.writeNamespace(prefix, namespaceURI);
                xmlWriter.setPrefix(prefix, namespaceURI);
            }

            if (prefix.trim().length() > 0) {
                xmlWriter.writeCharacters(
                    prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            } else {
                // i.e this is the default namespace
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }

        } else {
            xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
        }
    }

    private void writeQNames(final javax.xml.namespace.QName[] qnames, final javax.xml.stream.XMLStreamWriter xmlWriter)
        throws javax.xml.stream.XMLStreamException {

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
                        stringToWrite.append(prefix).append(":").append(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    } else {
                        stringToWrite.append(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
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
                    final java.lang.String namespace)
        throws javax.xml.stream.XMLStreamException {
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
    public javax.xml.stream.XMLStreamReader getPullParser(final javax.xml.namespace.QName qName)
        throws org.apache.axis2.databinding.ADBException {



        final java.util.ArrayList elementList = new java.util.ArrayList();
        final java.util.ArrayList attribList = new java.util.ArrayList();

        if (this.localChildFirstClassLoadingTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "childFirstClassLoading"));

            elementList.add(
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localChildFirstClassLoading));
        }
        if (this.localClusteringAgentTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "clusteringAgent"));


            elementList.add(this.localClusteringAgent == null ? null : this.localClusteringAgent);
        }
        if (this.localConfiguratorTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "configurator"));


            elementList.add(this.localConfigurator == null ? null : this.localConfigurator);
        }
        if (this.localFaultyModulesTracker) {
            if (this.localFaultyModules != null) {
                for (final String localFaultyModule : this.localFaultyModules) {

                    if (localFaultyModule != null) {
                        elementList.add(
                            new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "faultyModules"));
                        elementList.add(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFaultyModule));
                    } else {

                        elementList.add(
                            new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "faultyModules"));
                        elementList.add(null);

                    }


                }
            } else {

                elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "faultyModules"));
                elementList.add(null);

            }

        }
        if (this.localFaultyServicesTracker) {
            if (this.localFaultyServices != null) {
                for (final String localFaultyService : this.localFaultyServices) {

                    if (localFaultyService != null) {
                        elementList.add(
                            new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "faultyServices"));
                        elementList.add(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFaultyService));
                    } else {

                        elementList.add(
                            new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "faultyServices"));
                        elementList.add(null);

                    }


                }
            } else {

                elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "faultyServices"));
                elementList.add(null);

            }

        }
        if (this.localFaultyServicesDuetoModulesTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "faultyServicesDuetoModules"));


            elementList.add(this.localFaultyServicesDuetoModules == null ? null : this.localFaultyServicesDuetoModules);
        }
        if (this.localGlobalModulesTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "globalModules"));


            elementList.add(this.localGlobalModules == null ? null : this.localGlobalModules);
        }
        if (this.localGlobalOutPhaseTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "globalOutPhase"));


            elementList.add(this.localGlobalOutPhase == null ? null : this.localGlobalOutPhase);
        }
        if (this.localInFaultFlowPhasesTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "inFaultFlowPhases"));


            elementList.add(this.localInFaultFlowPhases == null ? null : this.localInFaultFlowPhases);
        }
        if (this.localInFaultPhasesTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "inFaultPhases"));


            elementList.add(this.localInFaultPhases == null ? null : this.localInFaultPhases);
        }
        if (this.localInFlowPhasesTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "inFlowPhases"));


            elementList.add(this.localInFlowPhases == null ? null : this.localInFlowPhases);
        }
        if (this.localInPhasesUptoAndIncludingPostDispatchTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                "inPhasesUptoAndIncludingPostDispatch"));


            elementList.add(
                this.localInPhasesUptoAndIncludingPostDispatch == null ? null
                                                                       : this.localInPhasesUptoAndIncludingPostDispatch);
        }
        if (this.localKeyTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "key"));


            elementList.add(this.localKey == null ? null : this.localKey);
        }
        if (this.localLocalPolicyAssertionsTracker) {
            if (this.localLocalPolicyAssertions != null) {
                for (final Object localLocalPolicyAssertion : this.localLocalPolicyAssertions) {

                    if (localLocalPolicyAssertion != null) {
                        elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                            "localPolicyAssertions"));
                        elementList.add(localLocalPolicyAssertion);
                    } else {

                        elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                            "localPolicyAssertions"));
                        elementList.add(null);

                    }

                }
            } else {

                elementList.add(
                    new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "localPolicyAssertions"));
                elementList.add(this.localLocalPolicyAssertions);

            }

        }
        if (this.localModuleClassLoaderTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "moduleClassLoader"));


            elementList.add(this.localModuleClassLoader == null ? null : this.localModuleClassLoader);
        }
        if (this.localModulesTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "modules"));


            elementList.add(this.localModules == null ? null : this.localModules);
        }
        if (this.localObserversListTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "observersList"));


            elementList.add(this.localObserversList == null ? null : this.localObserversList);
        }
        if (this.localOutFaultFlowPhasesTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "outFaultFlowPhases"));


            elementList.add(this.localOutFaultFlowPhases == null ? null : this.localOutFaultFlowPhases);
        }
        if (this.localOutFaultPhasesTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "outFaultPhases"));


            elementList.add(this.localOutFaultPhases == null ? null : this.localOutFaultPhases);
        }
        if (this.localOutFlowPhasesTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "outFlowPhases"));


            elementList.add(this.localOutFlowPhases == null ? null : this.localOutFlowPhases);
        }
        if (this.localPhasesInfoTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "phasesInfo"));


            elementList.add(this.localPhasesInfo == null ? null : this.localPhasesInfo);
        }
        if (this.localRepositoryTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "repository"));


            elementList.add(this.localRepository == null ? null : this.localRepository);
        }
        if (this.localSecretResolverTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "secretResolver"));


            elementList.add(this.localSecretResolver == null ? null : this.localSecretResolver);
        }
        if (this.localServiceClassLoaderTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "serviceClassLoader"));


            elementList.add(this.localServiceClassLoader == null ? null : this.localServiceClassLoader);
        }
        if (this.localServiceGroupsTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "serviceGroups"));


            elementList.add(this.localServiceGroups == null ? null : this.localServiceGroups);
        }
        if (this.localServicesTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "services"));


            elementList.add(this.localServices == null ? null : this.localServices);
        }
        if (this.localStartTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "start"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(this.localStart));
        }
        if (this.localSystemClassLoaderTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "systemClassLoader"));


            elementList.add(this.localSystemClassLoader == null ? null : this.localSystemClassLoader);
        }
        if (this.localTargetResolverChainTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "targetResolverChain"));


            elementList.add(this.localTargetResolverChain == null ? null : this.localTargetResolverChain);
        }
        if (this.localTransactionConfigTracker) {
            elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "transactionConfig"));


            elementList.add(this.localTransactionConfig == null ? null : this.localTransactionConfig);
        }
        if (this.localTransactionConfigurationTracker) {
            elementList.add(
                new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "transactionConfiguration"));


            elementList.add(this.localTransactionConfiguration == null ? null : this.localTransactionConfiguration);
        }
        if (this.localTransportsInTracker) {
            if (this.localTransportsIn != null) {
                for (final String element : this.localTransportsIn) {

                    if (element != null) {
                        elementList.add(
                            new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "transportsIn"));
                        elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(element));
                    } else {

                        elementList.add(
                            new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "transportsIn"));
                        elementList.add(null);

                    }


                }
            } else {

                elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "transportsIn"));
                elementList.add(null);

            }

        }
        if (this.localTransportsOutTracker) {
            if (this.localTransportsOut != null) {
                for (final String element : this.localTransportsOut) {

                    if (element != null) {
                        elementList.add(
                            new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "transportsOut"));
                        elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(element));
                    } else {

                        elementList.add(
                            new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "transportsOut"));
                        elementList.add(null);

                    }


                }
            } else {

                elementList.add(new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "transportsOut"));
                elementList.add(null);

            }

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
        public static AxisConfiguration parse(final javax.xml.stream.XMLStreamReader reader)
            throws java.lang.Exception {
            final AxisConfiguration object = new AxisConfiguration();

            int event;
            java.lang.String nillableValue = null;
            final java.lang.String prefix = "";
            final java.lang.String namespaceuri = "";
            try {

                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }


                if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                    final java.lang.String fullTypeName = reader.getAttributeValue(
                        "http://www.w3.org/2001/XMLSchema-instance", "type");
                    if (fullTypeName != null) {
                        java.lang.String nsPrefix = null;
                        if (fullTypeName.indexOf(":") > -1) {
                            nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                        }
                        nsPrefix = nsPrefix == null ? "" : nsPrefix;

                        final java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                        if (!"AxisConfiguration".equals(type)) {
                            // find namespace for the prefix
                            final java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                            return (AxisConfiguration) org.apache.axis2.transaction.xsd.ExtensionMapper.getTypeObject(
                                nsUri, type, reader);
                        }


                    }


                }



                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                final java.util.Vector handledAttributes = new java.util.Vector();



                reader.next();

                final java.util.ArrayList list4 = new java.util.ArrayList();

                final java.util.ArrayList list5 = new java.util.ArrayList();

                final java.util.ArrayList list14 = new java.util.ArrayList();

                final java.util.ArrayList list32 = new java.util.ArrayList();

                final java.util.ArrayList list33 = new java.util.ArrayList();


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                    "childFirstClassLoading").equals(reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setChildFirstClassLoading(
                        org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "clusteringAgent").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setClusteringAgent(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setClusteringAgent(
                            org.apache.axis2.clustering.xsd.ClusteringAgent.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "configurator").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setConfigurator(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setConfigurator(org.apache.axis2.engine.xsd.AxisConfigurator.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "faultyModules").equals(
                        reader.getName())) {



                    // Process the array and step past its final element's end.

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        list4.add(null);

                        reader.next();
                    } else {
                        list4.add(reader.getElementText());
                    }
                    // loop until we find a start element that is not part of this array
                    boolean loopDone4 = false;
                    while (!loopDone4) {
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
                            loopDone4 = true;
                        } else {
                            if (new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                "faultyModules").equals(reader.getName())) {

                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                    "nil");
                                if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                    list4.add(null);

                                    reader.next();
                                } else {
                                    list4.add(reader.getElementText());
                                }
                            } else {
                                loopDone4 = true;
                            }
                        }
                    }
                    // call the converter utility to convert and set the array

                    object.setFaultyModules((java.lang.String[]) list4.toArray(new java.lang.String[list4.size()]));

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "faultyServices").equals(
                        reader.getName())) {



                    // Process the array and step past its final element's end.

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        list5.add(null);

                        reader.next();
                    } else {
                        list5.add(reader.getElementText());
                    }
                    // loop until we find a start element that is not part of this array
                    boolean loopDone5 = false;
                    while (!loopDone5) {
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
                            loopDone5 = true;
                        } else {
                            if (new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                "faultyServices").equals(reader.getName())) {

                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                    "nil");
                                if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                    list5.add(null);

                                    reader.next();
                                } else {
                                    list5.add(reader.getElementText());
                                }
                            } else {
                                loopDone5 = true;
                            }
                        }
                    }
                    // call the converter utility to convert and set the array

                    object.setFaultyServices((java.lang.String[]) list5.toArray(new java.lang.String[list5.size()]));

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                    "faultyServicesDuetoModules").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setFaultyServicesDuetoModules(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setFaultyServicesDuetoModules(authclient.java.util.xsd.Map.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "globalModules").equals(
                        reader.getName())) {

                    object.setGlobalModules(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                        org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "globalOutPhase").equals(
                        reader.getName())) {

                    object.setGlobalOutPhase(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                        org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "inFaultFlowPhases").equals(
                        reader.getName())) {

                    object.setInFaultFlowPhases(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(
                        reader, org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "inFaultPhases").equals(
                        reader.getName())) {

                    object.setInFaultPhases(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                        org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "inFlowPhases").equals(
                        reader.getName())) {

                    object.setInFlowPhases(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                        org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                    "inPhasesUptoAndIncludingPostDispatch").equals(reader.getName())) {

                    object.setInPhasesUptoAndIncludingPostDispatch(
                        org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                            org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "key").equals(
                        reader.getName())) {

                    object.setKey(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                        org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                    "localPolicyAssertions").equals(reader.getName())) {



                    // Process the array and step past its final element's end.


                    boolean loopDone14 = false;
                    final javax.xml.namespace.QName startQname14 = new javax.xml.namespace.QName(
                        "http://engine.axis2.apache.org/xsd", "localPolicyAssertions");

                    while (!loopDone14) {
                        event = reader.getEventType();
                        if (javax.xml.stream.XMLStreamConstants.START_ELEMENT == event
                            && startQname14.equals(reader.getName())) {



                            nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                "nil");
                            if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                list14.add(null);
                                reader.next();
                            } else {
                                list14.add(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                                    org.apache.axis2.transaction.xsd.ExtensionMapper.class));
                            }
                        } else if (javax.xml.stream.XMLStreamConstants.START_ELEMENT == event
                            && !startQname14.equals(reader.getName())) {
                            loopDone14 = true;
                        } else if (javax.xml.stream.XMLStreamConstants.END_ELEMENT == event
                            && !startQname14.equals(reader.getName())) {
                            loopDone14 = true;
                        } else if (javax.xml.stream.XMLStreamConstants.END_DOCUMENT == event) {
                            loopDone14 = true;
                        } else {
                            reader.next();
                        }

                    }


                    object.setLocalPolicyAssertions(list14.toArray());

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "moduleClassLoader").equals(
                        reader.getName())) {

                    object.setModuleClassLoader(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(
                        reader, org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "modules").equals(
                        reader.getName())) {

                    object.setModules(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                        org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "observersList").equals(
                        reader.getName())) {

                    object.setObserversList(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                        org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "outFaultFlowPhases").equals(
                        reader.getName())) {

                    object.setOutFaultFlowPhases(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(
                        reader, org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "outFaultPhases").equals(
                        reader.getName())) {

                    object.setOutFaultPhases(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                        org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "outFlowPhases").equals(
                        reader.getName())) {

                    object.setOutFlowPhases(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                        org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "phasesInfo").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setPhasesInfo(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setPhasesInfo(org.apache.axis2.deployment.util.xsd.PhasesInfo.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "repository").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setRepository(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setRepository(authclient.java.net.xsd.URL.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "secretResolver").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setSecretResolver(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setSecretResolver(org.wso2.securevault.xsd.SecretResolver.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "serviceClassLoader").equals(
                        reader.getName())) {

                    object.setServiceClassLoader(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(
                        reader, org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "serviceGroups").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setServiceGroups(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setServiceGroups(authclient.java.util.xsd.Iterator.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "services").equals(
                        reader.getName())) {

                    object.setServices(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(reader,
                        org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "start").equals(
                        reader.getName())) {

                    final java.lang.String content = reader.getElementText();

                    object.setStart(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "systemClassLoader").equals(
                        reader.getName())) {

                    object.setSystemClassLoader(org.apache.axis2.databinding.utils.ConverterUtil.getAnyTypeObject(
                        reader, org.apache.axis2.transaction.xsd.ExtensionMapper.class));

                    reader.next();

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                    "targetResolverChain").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setTargetResolverChain(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setTargetResolverChain(org.apache.axis2.util.xsd.TargetResolver.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "transactionConfig").equals(
                        reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setTransactionConfig(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setTransactionConfig(
                            org.apache.axis2.transaction.xsd.TransactionConfiguration.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement() && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                    "transactionConfiguration").equals(reader.getName())) {

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        object.setTransactionConfiguration(null);
                        reader.next();

                        reader.next();

                    } else {

                        object.setTransactionConfiguration(
                            org.apache.axis2.transaction.xsd.TransactionConfiguration.Factory.parse(reader));

                        reader.next();
                    }
                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "transportsIn").equals(
                        reader.getName())) {



                    // Process the array and step past its final element's end.

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        list32.add(null);

                        reader.next();
                    } else {
                        list32.add(reader.getElementText());
                    }
                    // loop until we find a start element that is not part of this array
                    boolean loopDone32 = false;
                    while (!loopDone32) {
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
                            loopDone32 = true;
                        } else {
                            if (new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                "transportsIn").equals(reader.getName())) {

                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                    "nil");
                                if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                    list32.add(null);

                                    reader.next();
                                } else {
                                    list32.add(reader.getElementText());
                                }
                            } else {
                                loopDone32 = true;
                            }
                        }
                    }
                    // call the converter utility to convert and set the array

                    object.setTransportsIn((java.lang.String[]) list32.toArray(new java.lang.String[list32.size()]));

                } // End of if for expected property start element

                else {

                }


                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()
                    && new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd", "transportsOut").equals(
                        reader.getName())) {



                    // Process the array and step past its final element's end.

                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                        list33.add(null);

                        reader.next();
                    } else {
                        list33.add(reader.getElementText());
                    }
                    // loop until we find a start element that is not part of this array
                    boolean loopDone33 = false;
                    while (!loopDone33) {
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
                            loopDone33 = true;
                        } else {
                            if (new javax.xml.namespace.QName("http://engine.axis2.apache.org/xsd",
                                "transportsOut").equals(reader.getName())) {

                                nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                                    "nil");
                                if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                    list33.add(null);

                                    reader.next();
                                } else {
                                    list33.add(reader.getElementText());
                                }
                            } else {
                                loopDone33 = true;
                            }
                        }
                    }
                    // call the converter utility to convert and set the array

                    object.setTransportsOut((java.lang.String[]) list33.toArray(new java.lang.String[list33.size()]));

                } // End of if for expected property start element

                else {

                }

                while (!reader.isStartElement() && !reader.isEndElement()) {
                    reader.next();
                }

                if (reader.isStartElement()) {
                    // A start element we are not expecting indicates a trailing invalid property
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

