package org.opentosca.planbuilder.model.plan;

import java.util.Collection;
import java.util.Set;

import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;
import org.opentosca.planbuilder.model.tosca.AbstractServiceTemplate;

/**
 * Copyright 2017 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public abstract class AbstractPlan {

    // general categories
    public enum PlanType {
        BUILD, MANAGE, TERMINATE;

        public String getString() {
            switch (this) {
                case BUILD:
                    return "http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/BuildPlan";
                case TERMINATE:
                    return "http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/TerminationPlan";
                default:
                    // every other plan is a management plan
                case MANAGE:
                    return "http://docs.oasis-open.org/tosca/ns/2011/12/PlanTypes/ManagementPlan";
            }
        }
    }

    public static class Link {
        private final AbstractActivity srcActiv;
        private final AbstractActivity trgActiv;

        public Link(final AbstractActivity srcActiv, final AbstractActivity trgActiv) {
            this.srcActiv = srcActiv;
            this.trgActiv = trgActiv;
        }

        public AbstractActivity getSrcActiv() {
            return this.srcActiv;
        }

        public AbstractActivity getTrgActiv() {
            return this.trgActiv;
        }

        @Override
        public String toString() {
            return "{Src: " + this.srcActiv.getId() + " Trgt: " + this.trgActiv.getId() + "}";
        }

    }


    private final AbstractServiceTemplate serviceTemplate;

    private PlanType type;

    private final AbstractDefinitions definitions;

    private final Collection<AbstractActivity> activites;

    private final Collection<Link> links;

    private final String id;


    public AbstractPlan(final String id, final PlanType type, final AbstractDefinitions definitions,
                        final AbstractServiceTemplate serviceTemplate, final Collection<AbstractActivity> activities,
                        final Collection<Link> links) {
        this.id = id;
        this.type = type;
        this.definitions = definitions;
        this.serviceTemplate = serviceTemplate;
        this.activites = activities;
        this.links = links;
    }

    public String getId() {
        return this.id;
    }

    /**
     * @return the type
     */
    public PlanType getType() {
        return this.type;
    }

    /**
     * @type the type to set
     */
    public void setType(final PlanType type) {
        this.type = type;
    }

    /**
     * Returns the definitions document this AbstractPlan belongs to. The ServiceTemplate this BuildPlan
     * provisions must be contained in the given AbstractDefinitions.
     *
     * @return an AbstractDefinitions
     */
    public AbstractDefinitions getDefinitions() {
        return this.definitions;
    }

    /**
     * Returns the AbstractServiceTemplate of the ServiceTemplate this AbstractPlan belongs to
     *
     * @return a AbstractServiceTemplate
     */
    public AbstractServiceTemplate getServiceTemplate() {
        return this.serviceTemplate;
    }

    public Collection<AbstractActivity> getActivites() {
        return this.activites;
    }

    public Collection<Link> getLinks() {
        return this.links;
    }

    @Override
    public String toString() {
        String toString =
            "Plan: " + this.id + " Type: " + this.type + " ServiceTemplate: " + this.serviceTemplate.getId();

        toString += "Activities: ";

        for (final AbstractActivity actic : this.activites) {
            toString += "{Id: " + actic.getId() + " Type: " + actic.getType() + "}";
        }

        toString += "Links: ";

        for (final Link link : this.links) {
            toString += link.toString();
        }

        return toString;
    }

}
