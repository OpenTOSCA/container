package org.opentosca.container.core.engine.next;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.winery.model.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TRelationshipType;

import org.opentosca.container.core.engine.ResolvedArtifacts;
import org.opentosca.container.core.engine.xml.IXMLSerializerService;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Implements Tosca-Engine-like operations for the model classes available under {@link
 * org.opentosca.container.core.next.model}
 */
@Component
public final class ContainerEngine {

    private static final Logger LOG = LoggerFactory.getLogger(ContainerEngine.class);

    private final IXMLSerializerService xmlSerializerService;

    @Inject
    public ContainerEngine(IXMLSerializerService xmlSerializerService) {
        this.xmlSerializerService = xmlSerializerService;
    }

    public static NodeTemplateInstance resolveRelationshipOperationTarget(RelationshipTemplateInstance relationshipInstance,
                                                                          TRelationshipType relationshipType,
                                                                          String interfaceName, String operationName) {
        boolean operationIsAttachedToSource = Optional.ofNullable(relationshipType.getSourceInterfaces())
            .orElse(Collections.emptyList()).stream()
            .filter(iface -> interfaceName == null || iface.getName().equals(interfaceName))
            .flatMap(iface -> iface.getOperations().stream())
            .anyMatch(op -> op.getName().equals(operationName));
        if (operationIsAttachedToSource) {
            return relationshipInstance.getSource();
        } else {
            return relationshipInstance.getTarget();
        }
    }

    public ResolvedArtifacts resolvedDeploymentArtifacts(Csar context, TNodeTemplate nodeTemplate) {
        final ResolvedArtifacts result = new ResolvedArtifacts();
        result.setDeploymentArtifacts(resolvedDeploymentArtifactsForNodeTemplate(context, nodeTemplate));
        return result;
    }

    public ResolvedArtifacts resolvedDeploymentArtifactsOfNodeTypeImpl(Csar context, TNodeTypeImplementation nodeTemplate) {
        LOG.debug("Trying to fetch DAs of NodeTypeImplementation {}", nodeTemplate.getName());
        final ResolvedArtifacts result = new ResolvedArtifacts();

        List<ResolvedArtifacts.ResolvedDeploymentArtifact> collect = nodeTemplate.getDeploymentArtifacts().stream()
            .map(da -> resolveDA(context, da))
            .collect(Collectors.toList());

        result.setDeploymentArtifacts(collect);
        return result;
    }

    public List<ResolvedArtifacts.ResolvedDeploymentArtifact> resolvedDeploymentArtifactsForNodeTemplate(Csar context, TNodeTemplate nodeTemplate) {
        LOG.debug("Trying to fetch DAs of NodeTemplate {}", nodeTemplate.getName());
        if (nodeTemplate.getDeploymentArtifacts() == null
            || nodeTemplate.getDeploymentArtifacts().isEmpty()) {
            LOG.info("NodeTemplate {} has no deployment artifacts", nodeTemplate.getName());
            return Collections.emptyList();
        }

        return nodeTemplate.getDeploymentArtifacts().stream()
            .map(da -> resolveDA(context, da))
            .collect(Collectors.toList());
    }

    private ResolvedArtifacts.ResolvedDeploymentArtifact resolveDA(Csar context, TDeploymentArtifact da) {

        final ResolvedArtifacts.ResolvedDeploymentArtifact result = new ResolvedArtifacts.ResolvedDeploymentArtifact();
        result.setName(da.getName());
        result.setType(da.getArtifactType());
        // assumption: there is artifactSpecificContent OR an artifactTemplateRef
        if (Objects.isNull(da.getArtifactRef())) {
            result.setArtifactSpecificContent(readArtifactSpecificContent(da));
            result.setReferences(Collections.emptyList());
            return result;
        }

        TArtifactTemplate template = (TArtifactTemplate) context.queryRepository(new ArtifactTemplateId(da.getArtifactRef()));
        final List<String> references = new ArrayList<>();
        for (final TArtifactReference artifactReference : Optional.ofNullable(template.getArtifactReferences()).orElse(Collections.emptyList())) {
            // if there is no include patterns, just add the reference
            if (artifactReference.getIncludeOrExclude().isEmpty()) {
                references.add(artifactReference.getReference());
                continue;
            }
            artifactReference.getIncludeOrExclude().stream()
                .filter(o -> o instanceof TArtifactReference.Include)
                .map(TArtifactReference.Include.class::cast)
                .forEach(includePattern -> references.add(artifactReference.getReference() + "/" + includePattern.getPattern()));
        }
        result.setReferences(references);

        return result;
    }

    private Document getArtifactSpecificContent(final List<TDeploymentArtifact> artifacts, final String deploymentArtifactName) {
        // if there are ImplementationArtifacts
        if (artifacts == null) {
            return null;
        }
        Optional<TDeploymentArtifact> artifact = artifacts.stream()
            .filter(da -> da.getName().equals(deploymentArtifactName))
            .findFirst();
        if (artifact.isEmpty()) {
            LOG.info("Requested artifact {} was not found.", deploymentArtifactName);
            return null;
        }
        return readArtifactSpecificContent(artifact.get());
    }

    private Document readArtifactSpecificContent(TDeploymentArtifact artifact) {
        final List<Element> listOfAnyElements = new ArrayList<>();
        for (final Object obj : artifact.getAny()) {
            if (obj instanceof Element) {
                listOfAnyElements.add((Element) obj);
            } else {
                LOG.error("There is content inside of the DeploymentArtifact [{}] which is not a processable DOM Element.", artifact.getName());
                return null;
            }
        }
        return xmlSerializerService.getXmlSerializer().elementsIntoDocument(listOfAnyElements, "DeploymentArtifactSpecificContent");
    }
}
