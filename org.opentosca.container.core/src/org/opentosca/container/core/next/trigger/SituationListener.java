package org.opentosca.container.core.next.trigger;

import javax.persistence.PostUpdate;

import org.opentosca.container.core.next.model.Situation;
import org.opentosca.container.core.next.repository.SituationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kalmankepes
 *
 */
public class SituationListener {

    final private static Logger LOG = LoggerFactory.getLogger(SituationListener.class);

    private final SituationRepository sitRepo = new SituationRepository();

    @PostUpdate
    public void situationAfterUpdate(final Situation situation) {
        LOG.info("Updated situation with template " + situation.getSituationTemplateId() + " and thing "
            + situation.getThingId());


    }

}