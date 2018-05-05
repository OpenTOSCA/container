package org.opentosca.container.core.model.csar;

import java.util.List;

import org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFile;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TExportedOperation;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TServiceTemplate;

public interface Csar {
    
    public CsarId id();
    
    public List<TArtifactTemplate> artifactTemplates();

    // or force pass through TOSCAMetaFile and traverse the graph? 
    public List<TServiceTemplate> serviceTemplates();
    public TServiceTemplate entryServiceTemplate();
    
    public TDefinitions definitions();
    public List<TExportedOperation> exportedOperations();
    
    public List<TPlan> plans();
    public List<TNodeType> nodeTypes();

    public TOSCAMetaFile toscaMetadata();
    // FIXME check usage in SmartServicesResource whether this matches toscaMetadata
    public Object getRootTosca();
    
    public String description();
    // FIXME decide on Path / File / Binary Representation / ??
    public Object topologyPicture();

}
