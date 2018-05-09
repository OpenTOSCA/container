package org.opentosca.container.core.model.csar;

import java.util.List;

import org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFile;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TExportedOperation;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.opentosca.container.core.model.AbstractFile;

public interface Csar {
    
    public CsarId id();
    
    public List<TArtifactTemplate> artifactTemplates();

    public List<TServiceTemplate> serviceTemplates();
    
    // or force pass through TOSCAMetaFile and traverse the graph? 
    public TServiceTemplate entryServiceTemplate();
    
    public List<TDefinitions> definitions();
    public List<TExportedOperation> exportedOperations();
    
    public List<TPlan> plans();
    public List<TNodeType> nodeTypes();

    // FIXME does this even exist in the CSAR we imported?? and if it does... WHY?
    public TOSCAMetaFile toscaMetadata();
    // FIXME check usage in SmartServicesResource whether this matches toscaMetadata
    public AbstractFile getRootTosca();
    
    public String description();
    // FIXME decide on Path / File / Binary Representation / ??
    public AbstractFile topologyPicture();

}
