package org.opentosca.container.api.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.winery.model.tosca.TExportedOperation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import org.opentosca.container.core.tosca.extension.TParameter;

@XmlRootElement(name = "NodeOperation")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeOperationDTO {

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "interface")
    private String interfaceName;

    @XmlElement(name = "InputParameter")
    @XmlElementWrapper(name = "InputParameters")
    private List<TParameter> inputParameters = Lists.newArrayList();

    @XmlElement(name = "OutputParameter")
    @XmlElementWrapper(name = "OutputParameters")
    private List<TParameter> outputParameters = Lists.newArrayList();

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getInterfaceName() {
        return this.interfaceName;
    }

    public void setInterfaceName(final String interfaceName) {
        this.interfaceName = interfaceName;
    }

    @ApiModelProperty(name = "input_parameters")
    public List<TParameter> getInputParameters() {
        return this.inputParameters;
    }

    public void setInputParameters(final List<TParameter> inputParameters) {
        this.inputParameters = inputParameters;
    }

    @ApiModelProperty(name = "output_parameters")
    public List<TParameter> getOutputParameters() {
        return this.outputParameters;
    }

    public void setOutputParameters(final List<TParameter> outputParameters) {
        this.outputParameters = outputParameters;
    }

    public static class Converter {

        public static NodeOperationDTO convert(final TExportedOperation.NodeOperation o) {
            if (o == null) {
                return null;
            }

            final NodeOperationDTO dto = new NodeOperationDTO();

            dto.setInterfaceName(o.getInterfaceName());
            dto.setName(o.getOperationName());

            return dto;
        }

        public static TExportedOperation.NodeOperation convert(final NodeOperationDTO dto) {
            final TExportedOperation.NodeOperation o = new TExportedOperation.NodeOperation();

            o.setInterfaceName(dto.getInterfaceName());
            o.setOperationName(dto.getName());

            return o;
        }
    }
}
