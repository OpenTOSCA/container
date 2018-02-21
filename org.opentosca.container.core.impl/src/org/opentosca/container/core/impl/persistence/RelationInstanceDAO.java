package org.opentosca.container.core.impl.persistence;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.opentosca.container.core.common.jpa.DocumentConverter;
import org.opentosca.container.core.model.instance.RelationInstance;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.RelationshipTemplateInstanceProperty;
import org.opentosca.container.core.next.model.RelationshipTemplateInstanceState;
import org.opentosca.container.core.next.repository.RelationshipTemplateInstanceRepository;
import org.opentosca.container.core.next.utils.Enums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class RelationInstanceDAO {

    private static Logger logger = LoggerFactory.getLogger(RelationInstanceDAO.class);

    private final RelationshipTemplateInstanceRepository repository = new RelationshipTemplateInstanceRepository();

    public void deleteRelationInstance(final RelationInstance si) {
        try {
            logger.info("RelationInstance: {}", si.toString());
            final Optional<RelationshipTemplateInstance> o = this.repository.find(DaoUtil.toLong(si.getId()));
            if (o.isPresent()) {
                final RelationshipTemplateInstance nti = o.get();
                nti.setState(RelationshipTemplateInstanceState.DELETED);
                this.repository.update(nti);
                this.repository.remove(nti);
                logger.debug("Deleted RelationInstance with ID: " + si.getId());
            } else {
                logger.info("NOT FOUND");
            }
        }
        catch (final Exception e) {
            logger.error("Could not delete relation instance: {}", e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public RelationInstance saveRelationInstance(final RelationInstance relationInstance) {

        try {
            logger.info("RelationInstance: {}", relationInstance.toString());
            final RelationshipTemplateInstance nti = Converters.convert(relationInstance);
            try {
                this.repository.add(nti);
            }
            catch (final Exception ex) {
                logger.info("Object already added, trying to update");
                this.repository.update(nti);
            }
            return Converters.convert(nti);
        }
        catch (final Exception e) {
            logger.error("Could not save relation instance: {}", e.getMessage(), e);
            e.printStackTrace();
        }
        return relationInstance;
    }

    public void setProperties(final RelationInstance relationInstance, final Document properties) {
        try {
            logger.info("RelationInstance: {}", relationInstance.toString());
            final DocumentConverter converter = new DocumentConverter();
            final Optional<RelationshipTemplateInstance> o =
                this.repository.find(DaoUtil.toLong(relationInstance.getId()));
            if (o.isPresent()) {
                final RelationshipTemplateInstance nti = o.get();
                if (properties != null) {
                    final String value = (String) converter.convertObjectValueToDataValue(properties, null);
                    logger.info("XML: {}", value);
                    final RelationshipTemplateInstanceProperty prop = new RelationshipTemplateInstanceProperty();
                    prop.setName("xml");
                    prop.setType("xml");
                    prop.setValue(value);
                    nti.addProperty(prop);
                }
                this.repository.update(nti);
            } else {
                logger.info("NOT FOUND");
            }
        }
        catch (final Exception e) {
            logger.error("Could not update relation instance: {}", e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public void setState(final RelationInstance relationInstance, final String state) {
        try {
            logger.info("RelationInstance: {}", relationInstance.toString());
            final Optional<RelationshipTemplateInstance> o =
                this.repository.find(DaoUtil.toLong(relationInstance.getId()));
            if (o.isPresent()) {
                final RelationshipTemplateInstance nti = o.get();
                nti.setState(Enums.valueOf(RelationshipTemplateInstanceState.class, state,
                                           RelationshipTemplateInstanceState.ERROR));
                this.repository.update(nti);
            } else {
                logger.info("NOT FOUND");
            }
        }
        catch (final Exception e) {
            logger.error("Could not update relation instance: {}", e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public List<RelationInstance> getRelationInstances(final URI serviceInstanceID, final QName relationshipTemplateID,
                                                       final String relationshipTemplateName,
                                                       final URI relationInstanceID) {
        logger.info("Not Implemented: Relation instances cannot be queried");
        return new ArrayList<>();
    }
}
