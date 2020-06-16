/**
 * TEventInfo.java
 * <p>
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.apache.www.ode.pmapi.types._2006._08._02;

public class TEventInfo implements java.io.Serializable {
    private java.lang.String name;

    private java.lang.String type;

    private int lineNumber;

    private java.util.Calendar timestamp;

    private javax.xml.namespace.QName processId;

    private javax.xml.namespace.QName processType;

    private java.lang.Long instanceId;

    private java.lang.Long scopeId;

    private java.lang.Long parentScopeId;

    private java.lang.String scopeName;

    private java.lang.Integer scopeDefinitionId;

    private java.lang.Long activityId;

    private java.lang.String activityName;

    private java.lang.String activityType;

    private java.lang.Integer activityDefinitionId;

    private java.lang.String activityFailureReason;

    private java.lang.String activityRecoveryAction;

    private java.lang.String variableName;

    private java.lang.String newValue;

    private javax.xml.namespace.QName portType;

    private java.lang.String operation;

    private java.lang.String correlationSet;

    private java.lang.String mexId;

    private java.lang.String correlationKey;

    private java.lang.String expression;

    private javax.xml.namespace.QName fault;

    private java.lang.Integer faultLineNumber;

    private java.lang.String explanation;

    private java.lang.String result;

    private java.lang.Long rootScopeId;

    private java.lang.Integer rootScopeDeclarationId;

    private java.lang.String partnerLinkName;

    private java.lang.Integer oldState;

    private java.lang.Integer newState;

    private java.lang.Boolean success;

    public TEventInfo() {
    }

    public TEventInfo(
        java.lang.String name,
        java.lang.String type,
        int lineNumber,
        java.util.Calendar timestamp,
        javax.xml.namespace.QName processId,
        javax.xml.namespace.QName processType,
        java.lang.Long instanceId,
        java.lang.Long scopeId,
        java.lang.Long parentScopeId,
        java.lang.String scopeName,
        java.lang.Integer scopeDefinitionId,
        java.lang.Long activityId,
        java.lang.String activityName,
        java.lang.String activityType,
        java.lang.Integer activityDefinitionId,
        java.lang.String activityFailureReason,
        java.lang.String activityRecoveryAction,
        java.lang.String variableName,
        java.lang.String newValue,
        javax.xml.namespace.QName portType,
        java.lang.String operation,
        java.lang.String correlationSet,
        java.lang.String mexId,
        java.lang.String correlationKey,
        java.lang.String expression,
        javax.xml.namespace.QName fault,
        java.lang.Integer faultLineNumber,
        java.lang.String explanation,
        java.lang.String result,
        java.lang.Long rootScopeId,
        java.lang.Integer rootScopeDeclarationId,
        java.lang.String partnerLinkName,
        java.lang.Integer oldState,
        java.lang.Integer newState,
        java.lang.Boolean success) {
        this.name = name;
        this.type = type;
        this.lineNumber = lineNumber;
        this.timestamp = timestamp;
        this.processId = processId;
        this.processType = processType;
        this.instanceId = instanceId;
        this.scopeId = scopeId;
        this.parentScopeId = parentScopeId;
        this.scopeName = scopeName;
        this.scopeDefinitionId = scopeDefinitionId;
        this.activityId = activityId;
        this.activityName = activityName;
        this.activityType = activityType;
        this.activityDefinitionId = activityDefinitionId;
        this.activityFailureReason = activityFailureReason;
        this.activityRecoveryAction = activityRecoveryAction;
        this.variableName = variableName;
        this.newValue = newValue;
        this.portType = portType;
        this.operation = operation;
        this.correlationSet = correlationSet;
        this.mexId = mexId;
        this.correlationKey = correlationKey;
        this.expression = expression;
        this.fault = fault;
        this.faultLineNumber = faultLineNumber;
        this.explanation = explanation;
        this.result = result;
        this.rootScopeId = rootScopeId;
        this.rootScopeDeclarationId = rootScopeDeclarationId;
        this.partnerLinkName = partnerLinkName;
        this.oldState = oldState;
        this.newState = newState;
        this.success = success;
    }

    /**
     * Gets the name value for this TEventInfo.
     *
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }

    /**
     * Sets the name value for this TEventInfo.
     *
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }

    /**
     * Gets the type value for this TEventInfo.
     *
     * @return type
     */
    public java.lang.String getType() {
        return type;
    }

    /**
     * Sets the type value for this TEventInfo.
     *
     * @param type
     */
    public void setType(java.lang.String type) {
        this.type = type;
    }

    /**
     * Gets the lineNumber value for this TEventInfo.
     *
     * @return lineNumber
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Sets the lineNumber value for this TEventInfo.
     *
     * @param lineNumber
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Gets the timestamp value for this TEventInfo.
     *
     * @return timestamp
     */
    public java.util.Calendar getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp value for this TEventInfo.
     *
     * @param timestamp
     */
    public void setTimestamp(java.util.Calendar timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the processId value for this TEventInfo.
     *
     * @return processId
     */
    public javax.xml.namespace.QName getProcessId() {
        return processId;
    }

    /**
     * Sets the processId value for this TEventInfo.
     *
     * @param processId
     */
    public void setProcessId(javax.xml.namespace.QName processId) {
        this.processId = processId;
    }

    /**
     * Gets the processType value for this TEventInfo.
     *
     * @return processType
     */
    public javax.xml.namespace.QName getProcessType() {
        return processType;
    }

    /**
     * Sets the processType value for this TEventInfo.
     *
     * @param processType
     */
    public void setProcessType(javax.xml.namespace.QName processType) {
        this.processType = processType;
    }

    /**
     * Gets the instanceId value for this TEventInfo.
     *
     * @return instanceId
     */
    public java.lang.Long getInstanceId() {
        return instanceId;
    }

    /**
     * Sets the instanceId value for this TEventInfo.
     *
     * @param instanceId
     */
    public void setInstanceId(java.lang.Long instanceId) {
        this.instanceId = instanceId;
    }

    /**
     * Gets the scopeId value for this TEventInfo.
     *
     * @return scopeId
     */
    public java.lang.Long getScopeId() {
        return scopeId;
    }

    /**
     * Sets the scopeId value for this TEventInfo.
     *
     * @param scopeId
     */
    public void setScopeId(java.lang.Long scopeId) {
        this.scopeId = scopeId;
    }

    /**
     * Gets the parentScopeId value for this TEventInfo.
     *
     * @return parentScopeId
     */
    public java.lang.Long getParentScopeId() {
        return parentScopeId;
    }

    /**
     * Sets the parentScopeId value for this TEventInfo.
     *
     * @param parentScopeId
     */
    public void setParentScopeId(java.lang.Long parentScopeId) {
        this.parentScopeId = parentScopeId;
    }

    /**
     * Gets the scopeName value for this TEventInfo.
     *
     * @return scopeName
     */
    public java.lang.String getScopeName() {
        return scopeName;
    }

    /**
     * Sets the scopeName value for this TEventInfo.
     *
     * @param scopeName
     */
    public void setScopeName(java.lang.String scopeName) {
        this.scopeName = scopeName;
    }

    /**
     * Gets the scopeDefinitionId value for this TEventInfo.
     *
     * @return scopeDefinitionId
     */
    public java.lang.Integer getScopeDefinitionId() {
        return scopeDefinitionId;
    }

    /**
     * Sets the scopeDefinitionId value for this TEventInfo.
     *
     * @param scopeDefinitionId
     */
    public void setScopeDefinitionId(java.lang.Integer scopeDefinitionId) {
        this.scopeDefinitionId = scopeDefinitionId;
    }

    /**
     * Gets the activityId value for this TEventInfo.
     *
     * @return activityId
     */
    public java.lang.Long getActivityId() {
        return activityId;
    }

    /**
     * Sets the activityId value for this TEventInfo.
     *
     * @param activityId
     */
    public void setActivityId(java.lang.Long activityId) {
        this.activityId = activityId;
    }

    /**
     * Gets the activityName value for this TEventInfo.
     *
     * @return activityName
     */
    public java.lang.String getActivityName() {
        return activityName;
    }

    /**
     * Sets the activityName value for this TEventInfo.
     *
     * @param activityName
     */
    public void setActivityName(java.lang.String activityName) {
        this.activityName = activityName;
    }

    /**
     * Gets the activityType value for this TEventInfo.
     *
     * @return activityType
     */
    public java.lang.String getActivityType() {
        return activityType;
    }

    /**
     * Sets the activityType value for this TEventInfo.
     *
     * @param activityType
     */
    public void setActivityType(java.lang.String activityType) {
        this.activityType = activityType;
    }

    /**
     * Gets the activityDefinitionId value for this TEventInfo.
     *
     * @return activityDefinitionId
     */
    public java.lang.Integer getActivityDefinitionId() {
        return activityDefinitionId;
    }

    /**
     * Sets the activityDefinitionId value for this TEventInfo.
     *
     * @param activityDefinitionId
     */
    public void setActivityDefinitionId(java.lang.Integer activityDefinitionId) {
        this.activityDefinitionId = activityDefinitionId;
    }

    /**
     * Gets the activityFailureReason value for this TEventInfo.
     *
     * @return activityFailureReason
     */
    public java.lang.String getActivityFailureReason() {
        return activityFailureReason;
    }

    /**
     * Sets the activityFailureReason value for this TEventInfo.
     *
     * @param activityFailureReason
     */
    public void setActivityFailureReason(java.lang.String activityFailureReason) {
        this.activityFailureReason = activityFailureReason;
    }

    /**
     * Gets the activityRecoveryAction value for this TEventInfo.
     *
     * @return activityRecoveryAction
     */
    public java.lang.String getActivityRecoveryAction() {
        return activityRecoveryAction;
    }

    /**
     * Sets the activityRecoveryAction value for this TEventInfo.
     *
     * @param activityRecoveryAction
     */
    public void setActivityRecoveryAction(java.lang.String activityRecoveryAction) {
        this.activityRecoveryAction = activityRecoveryAction;
    }

    /**
     * Gets the variableName value for this TEventInfo.
     *
     * @return variableName
     */
    public java.lang.String getVariableName() {
        return variableName;
    }

    /**
     * Sets the variableName value for this TEventInfo.
     *
     * @param variableName
     */
    public void setVariableName(java.lang.String variableName) {
        this.variableName = variableName;
    }

    /**
     * Gets the newValue value for this TEventInfo.
     *
     * @return newValue
     */
    public java.lang.String getNewValue() {
        return newValue;
    }

    /**
     * Sets the newValue value for this TEventInfo.
     *
     * @param newValue
     */
    public void setNewValue(java.lang.String newValue) {
        this.newValue = newValue;
    }

    /**
     * Gets the portType value for this TEventInfo.
     *
     * @return portType
     */
    public javax.xml.namespace.QName getPortType() {
        return portType;
    }

    /**
     * Sets the portType value for this TEventInfo.
     *
     * @param portType
     */
    public void setPortType(javax.xml.namespace.QName portType) {
        this.portType = portType;
    }

    /**
     * Gets the operation value for this TEventInfo.
     *
     * @return operation
     */
    public java.lang.String getOperation() {
        return operation;
    }

    /**
     * Sets the operation value for this TEventInfo.
     *
     * @param operation
     */
    public void setOperation(java.lang.String operation) {
        this.operation = operation;
    }

    /**
     * Gets the correlationSet value for this TEventInfo.
     *
     * @return correlationSet
     */
    public java.lang.String getCorrelationSet() {
        return correlationSet;
    }

    /**
     * Sets the correlationSet value for this TEventInfo.
     *
     * @param correlationSet
     */
    public void setCorrelationSet(java.lang.String correlationSet) {
        this.correlationSet = correlationSet;
    }

    /**
     * Gets the mexId value for this TEventInfo.
     *
     * @return mexId
     */
    public java.lang.String getMexId() {
        return mexId;
    }

    /**
     * Sets the mexId value for this TEventInfo.
     *
     * @param mexId
     */
    public void setMexId(java.lang.String mexId) {
        this.mexId = mexId;
    }

    /**
     * Gets the correlationKey value for this TEventInfo.
     *
     * @return correlationKey
     */
    public java.lang.String getCorrelationKey() {
        return correlationKey;
    }

    /**
     * Sets the correlationKey value for this TEventInfo.
     *
     * @param correlationKey
     */
    public void setCorrelationKey(java.lang.String correlationKey) {
        this.correlationKey = correlationKey;
    }

    /**
     * Gets the expression value for this TEventInfo.
     *
     * @return expression
     */
    public java.lang.String getExpression() {
        return expression;
    }

    /**
     * Sets the expression value for this TEventInfo.
     *
     * @param expression
     */
    public void setExpression(java.lang.String expression) {
        this.expression = expression;
    }

    /**
     * Gets the fault value for this TEventInfo.
     *
     * @return fault
     */
    public javax.xml.namespace.QName getFault() {
        return fault;
    }

    /**
     * Sets the fault value for this TEventInfo.
     *
     * @param fault
     */
    public void setFault(javax.xml.namespace.QName fault) {
        this.fault = fault;
    }

    /**
     * Gets the faultLineNumber value for this TEventInfo.
     *
     * @return faultLineNumber
     */
    public java.lang.Integer getFaultLineNumber() {
        return faultLineNumber;
    }

    /**
     * Sets the faultLineNumber value for this TEventInfo.
     *
     * @param faultLineNumber
     */
    public void setFaultLineNumber(java.lang.Integer faultLineNumber) {
        this.faultLineNumber = faultLineNumber;
    }

    /**
     * Gets the explanation value for this TEventInfo.
     *
     * @return explanation
     */
    public java.lang.String getExplanation() {
        return explanation;
    }

    /**
     * Sets the explanation value for this TEventInfo.
     *
     * @param explanation
     */
    public void setExplanation(java.lang.String explanation) {
        this.explanation = explanation;
    }

    /**
     * Gets the result value for this TEventInfo.
     *
     * @return result
     */
    public java.lang.String getResult() {
        return result;
    }

    /**
     * Sets the result value for this TEventInfo.
     *
     * @param result
     */
    public void setResult(java.lang.String result) {
        this.result = result;
    }

    /**
     * Gets the rootScopeId value for this TEventInfo.
     *
     * @return rootScopeId
     */
    public java.lang.Long getRootScopeId() {
        return rootScopeId;
    }

    /**
     * Sets the rootScopeId value for this TEventInfo.
     *
     * @param rootScopeId
     */
    public void setRootScopeId(java.lang.Long rootScopeId) {
        this.rootScopeId = rootScopeId;
    }

    /**
     * Gets the rootScopeDeclarationId value for this TEventInfo.
     *
     * @return rootScopeDeclarationId
     */
    public java.lang.Integer getRootScopeDeclarationId() {
        return rootScopeDeclarationId;
    }

    /**
     * Sets the rootScopeDeclarationId value for this TEventInfo.
     *
     * @param rootScopeDeclarationId
     */
    public void setRootScopeDeclarationId(java.lang.Integer rootScopeDeclarationId) {
        this.rootScopeDeclarationId = rootScopeDeclarationId;
    }

    /**
     * Gets the partnerLinkName value for this TEventInfo.
     *
     * @return partnerLinkName
     */
    public java.lang.String getPartnerLinkName() {
        return partnerLinkName;
    }

    /**
     * Sets the partnerLinkName value for this TEventInfo.
     *
     * @param partnerLinkName
     */
    public void setPartnerLinkName(java.lang.String partnerLinkName) {
        this.partnerLinkName = partnerLinkName;
    }

    /**
     * Gets the oldState value for this TEventInfo.
     *
     * @return oldState
     */
    public java.lang.Integer getOldState() {
        return oldState;
    }

    /**
     * Sets the oldState value for this TEventInfo.
     *
     * @param oldState
     */
    public void setOldState(java.lang.Integer oldState) {
        this.oldState = oldState;
    }

    /**
     * Gets the newState value for this TEventInfo.
     *
     * @return newState
     */
    public java.lang.Integer getNewState() {
        return newState;
    }

    /**
     * Sets the newState value for this TEventInfo.
     *
     * @param newState
     */
    public void setNewState(java.lang.Integer newState) {
        this.newState = newState;
    }

    /**
     * Gets the success value for this TEventInfo.
     *
     * @return success
     */
    public java.lang.Boolean getSuccess() {
        return success;
    }

    /**
     * Sets the success value for this TEventInfo.
     *
     * @param success
     */
    public void setSuccess(java.lang.Boolean success) {
        this.success = success;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TEventInfo)) return false;
        TEventInfo other = (TEventInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.name == null && other.getName() == null) ||
                (this.name != null &&
                    this.name.equals(other.getName()))) &&
            ((this.type == null && other.getType() == null) ||
                (this.type != null &&
                    this.type.equals(other.getType()))) &&
            this.lineNumber == other.getLineNumber() &&
            ((this.timestamp == null && other.getTimestamp() == null) ||
                (this.timestamp != null &&
                    this.timestamp.equals(other.getTimestamp()))) &&
            ((this.processId == null && other.getProcessId() == null) ||
                (this.processId != null &&
                    this.processId.equals(other.getProcessId()))) &&
            ((this.processType == null && other.getProcessType() == null) ||
                (this.processType != null &&
                    this.processType.equals(other.getProcessType()))) &&
            ((this.instanceId == null && other.getInstanceId() == null) ||
                (this.instanceId != null &&
                    this.instanceId.equals(other.getInstanceId()))) &&
            ((this.scopeId == null && other.getScopeId() == null) ||
                (this.scopeId != null &&
                    this.scopeId.equals(other.getScopeId()))) &&
            ((this.parentScopeId == null && other.getParentScopeId() == null) ||
                (this.parentScopeId != null &&
                    this.parentScopeId.equals(other.getParentScopeId()))) &&
            ((this.scopeName == null && other.getScopeName() == null) ||
                (this.scopeName != null &&
                    this.scopeName.equals(other.getScopeName()))) &&
            ((this.scopeDefinitionId == null && other.getScopeDefinitionId() == null) ||
                (this.scopeDefinitionId != null &&
                    this.scopeDefinitionId.equals(other.getScopeDefinitionId()))) &&
            ((this.activityId == null && other.getActivityId() == null) ||
                (this.activityId != null &&
                    this.activityId.equals(other.getActivityId()))) &&
            ((this.activityName == null && other.getActivityName() == null) ||
                (this.activityName != null &&
                    this.activityName.equals(other.getActivityName()))) &&
            ((this.activityType == null && other.getActivityType() == null) ||
                (this.activityType != null &&
                    this.activityType.equals(other.getActivityType()))) &&
            ((this.activityDefinitionId == null && other.getActivityDefinitionId() == null) ||
                (this.activityDefinitionId != null &&
                    this.activityDefinitionId.equals(other.getActivityDefinitionId()))) &&
            ((this.activityFailureReason == null && other.getActivityFailureReason() == null) ||
                (this.activityFailureReason != null &&
                    this.activityFailureReason.equals(other.getActivityFailureReason()))) &&
            ((this.activityRecoveryAction == null && other.getActivityRecoveryAction() == null) ||
                (this.activityRecoveryAction != null &&
                    this.activityRecoveryAction.equals(other.getActivityRecoveryAction()))) &&
            ((this.variableName == null && other.getVariableName() == null) ||
                (this.variableName != null &&
                    this.variableName.equals(other.getVariableName()))) &&
            ((this.newValue == null && other.getNewValue() == null) ||
                (this.newValue != null &&
                    this.newValue.equals(other.getNewValue()))) &&
            ((this.portType == null && other.getPortType() == null) ||
                (this.portType != null &&
                    this.portType.equals(other.getPortType()))) &&
            ((this.operation == null && other.getOperation() == null) ||
                (this.operation != null &&
                    this.operation.equals(other.getOperation()))) &&
            ((this.correlationSet == null && other.getCorrelationSet() == null) ||
                (this.correlationSet != null &&
                    this.correlationSet.equals(other.getCorrelationSet()))) &&
            ((this.mexId == null && other.getMexId() == null) ||
                (this.mexId != null &&
                    this.mexId.equals(other.getMexId()))) &&
            ((this.correlationKey == null && other.getCorrelationKey() == null) ||
                (this.correlationKey != null &&
                    this.correlationKey.equals(other.getCorrelationKey()))) &&
            ((this.expression == null && other.getExpression() == null) ||
                (this.expression != null &&
                    this.expression.equals(other.getExpression()))) &&
            ((this.fault == null && other.getFault() == null) ||
                (this.fault != null &&
                    this.fault.equals(other.getFault()))) &&
            ((this.faultLineNumber == null && other.getFaultLineNumber() == null) ||
                (this.faultLineNumber != null &&
                    this.faultLineNumber.equals(other.getFaultLineNumber()))) &&
            ((this.explanation == null && other.getExplanation() == null) ||
                (this.explanation != null &&
                    this.explanation.equals(other.getExplanation()))) &&
            ((this.result == null && other.getResult() == null) ||
                (this.result != null &&
                    this.result.equals(other.getResult()))) &&
            ((this.rootScopeId == null && other.getRootScopeId() == null) ||
                (this.rootScopeId != null &&
                    this.rootScopeId.equals(other.getRootScopeId()))) &&
            ((this.rootScopeDeclarationId == null && other.getRootScopeDeclarationId() == null) ||
                (this.rootScopeDeclarationId != null &&
                    this.rootScopeDeclarationId.equals(other.getRootScopeDeclarationId()))) &&
            ((this.partnerLinkName == null && other.getPartnerLinkName() == null) ||
                (this.partnerLinkName != null &&
                    this.partnerLinkName.equals(other.getPartnerLinkName()))) &&
            ((this.oldState == null && other.getOldState() == null) ||
                (this.oldState != null &&
                    this.oldState.equals(other.getOldState()))) &&
            ((this.newState == null && other.getNewState() == null) ||
                (this.newState != null &&
                    this.newState.equals(other.getNewState()))) &&
            ((this.success == null && other.getSuccess() == null) ||
                (this.success != null &&
                    this.success.equals(other.getSuccess())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        _hashCode += getLineNumber();
        if (getTimestamp() != null) {
            _hashCode += getTimestamp().hashCode();
        }
        if (getProcessId() != null) {
            _hashCode += getProcessId().hashCode();
        }
        if (getProcessType() != null) {
            _hashCode += getProcessType().hashCode();
        }
        if (getInstanceId() != null) {
            _hashCode += getInstanceId().hashCode();
        }
        if (getScopeId() != null) {
            _hashCode += getScopeId().hashCode();
        }
        if (getParentScopeId() != null) {
            _hashCode += getParentScopeId().hashCode();
        }
        if (getScopeName() != null) {
            _hashCode += getScopeName().hashCode();
        }
        if (getScopeDefinitionId() != null) {
            _hashCode += getScopeDefinitionId().hashCode();
        }
        if (getActivityId() != null) {
            _hashCode += getActivityId().hashCode();
        }
        if (getActivityName() != null) {
            _hashCode += getActivityName().hashCode();
        }
        if (getActivityType() != null) {
            _hashCode += getActivityType().hashCode();
        }
        if (getActivityDefinitionId() != null) {
            _hashCode += getActivityDefinitionId().hashCode();
        }
        if (getActivityFailureReason() != null) {
            _hashCode += getActivityFailureReason().hashCode();
        }
        if (getActivityRecoveryAction() != null) {
            _hashCode += getActivityRecoveryAction().hashCode();
        }
        if (getVariableName() != null) {
            _hashCode += getVariableName().hashCode();
        }
        if (getNewValue() != null) {
            _hashCode += getNewValue().hashCode();
        }
        if (getPortType() != null) {
            _hashCode += getPortType().hashCode();
        }
        if (getOperation() != null) {
            _hashCode += getOperation().hashCode();
        }
        if (getCorrelationSet() != null) {
            _hashCode += getCorrelationSet().hashCode();
        }
        if (getMexId() != null) {
            _hashCode += getMexId().hashCode();
        }
        if (getCorrelationKey() != null) {
            _hashCode += getCorrelationKey().hashCode();
        }
        if (getExpression() != null) {
            _hashCode += getExpression().hashCode();
        }
        if (getFault() != null) {
            _hashCode += getFault().hashCode();
        }
        if (getFaultLineNumber() != null) {
            _hashCode += getFaultLineNumber().hashCode();
        }
        if (getExplanation() != null) {
            _hashCode += getExplanation().hashCode();
        }
        if (getResult() != null) {
            _hashCode += getResult().hashCode();
        }
        if (getRootScopeId() != null) {
            _hashCode += getRootScopeId().hashCode();
        }
        if (getRootScopeDeclarationId() != null) {
            _hashCode += getRootScopeDeclarationId().hashCode();
        }
        if (getPartnerLinkName() != null) {
            _hashCode += getPartnerLinkName().hashCode();
        }
        if (getOldState() != null) {
            _hashCode += getOldState().hashCode();
        }
        if (getNewState() != null) {
            _hashCode += getNewState().hashCode();
        }
        if (getSuccess() != null) {
            _hashCode += getSuccess().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TEventInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "tEventInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lineNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "line-number"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timestamp");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "timestamp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("processId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "process-id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "QName"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("processType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "process-type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "QName"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("instanceId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "instance-id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scopeId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "scope-id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("parentScopeId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "parent-scope-id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scopeName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "scope-name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scopeDefinitionId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "scope-definition-id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("activityId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "activity-id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("activityName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "activity-name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("activityType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "activity-type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("activityDefinitionId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "activity-definition-id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("activityFailureReason");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "activity-failure-reason"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("activityRecoveryAction");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "activity-recovery-action"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("variableName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "variable-name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("newValue");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "new-value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("portType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "port-type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "QName"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("operation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "operation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("correlationSet");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "correlation-set"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mexId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "mex-id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("correlationKey");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "correlation-key"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expression");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "expression"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fault");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "fault"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "QName"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("faultLineNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "fault-line-number"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("explanation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "explanation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("result");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "result"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rootScopeId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "root-scope-id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rootScopeDeclarationId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "root-scope-declaration-id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("partnerLinkName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "partner-link-name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("oldState");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "old-state"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("newState");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "new-state"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("success");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.apache.org/ode/pmapi/types/2006/08/02/", "success"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
        java.lang.String mechType,
        java.lang.Class _javaType,
        javax.xml.namespace.QName _xmlType) {
        return
            new org.apache.axis.encoding.ser.BeanSerializer(
                _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
        java.lang.String mechType,
        java.lang.Class _javaType,
        javax.xml.namespace.QName _xmlType) {
        return
            new org.apache.axis.encoding.ser.BeanDeserializer(
                _javaType, _xmlType, typeDesc);
    }
}
