package org.opentosca.planbuilder.model.plan;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import org.opentosca.container.core.next.model.PlanType;

/**
 * Copyright 2017 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 */
public abstract class AbstractPlan {

    private final TServiceTemplate serviceTemplate;
    private final TDefinitions definitions;
    private final Collection<AbstractActivity> activites;
    private final Collection<Link> links;
    private final String id;
    private PlanType type;
    private int internalCounterId = 0;

    public AbstractPlan(final String id, final PlanType type, final TDefinitions definitions,
                        final TServiceTemplate serviceTemplate, final Collection<AbstractActivity> activities,
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
     * Returns the definitions document this AbstractPlan belongs to. The ServiceTemplate this BuildPlan provisions must
     * be contained in the given TDefinitions.
     *
     * @return an TDefinitions
     */
    public TDefinitions getDefinitions() {
        return this.definitions;
    }

    /**
     * Returns the TServiceTemplate of the ServiceTemplate this AbstractPlan belongs to
     *
     * @return a TServiceTemplate
     */
    public TServiceTemplate getServiceTemplate() {
        return this.serviceTemplate;
    }

    public Collection<AbstractActivity> getActivites() {
        return this.activites;
    }

    public Collection<Link> getLinks() {
        return this.links;
    }

    public Collection<AbstractActivity> getSinks() {
        final Collection<AbstractActivity> sinks = new HashSet<>();
        for (final AbstractActivity act : this.activites) {
            boolean isSink = true;
            for (final Link link : this.links) {
                if (link.getSrcActiv().equals(act)) {
                    isSink = false;
                    break;
                }
            }
            if (isSink) {
                sinks.add(act);
            }
        }

        return sinks;
    }

    public Collection<AbstractActivity> getSources() {
        final Collection<AbstractActivity> sources = new HashSet<>();
        for (final AbstractActivity act : this.activites) {
            boolean isSource = true;
            for (final Link link : this.links) {
                if (link.getTrgActiv().equals(act)) {
                    isSource = false;
                    break;
                }
            }
            if (isSource) {
                sources.add(act);
            }
        }

        return sources;
    }

    public AbstractActivity findRelationshipTemplateActivity(final TRelationshipTemplate relationshipTemplate,
                                                             final ActivityType type) {
        for (final AbstractActivity activity : findRelationshipTemplateActivities(relationshipTemplate)) {
            if (activity.getType().equals(type)) {
                return activity;
            }
        }
        return null;
    }

    public Collection<AbstractActivity> findNodeTemplateActivities(final TNodeTemplate nodeTemplate) {
        final Collection<AbstractActivity> foundActivities = new HashSet<>();
        for (final AbstractActivity activity : this.activites) {

            if (activity instanceof NodeTemplateActivity) {
                if (((NodeTemplateActivity) activity).getNodeTemplate().equals(nodeTemplate)) {
                    foundActivities.add(activity);
                }
            }
        }
        return foundActivities;
    }

    public Collection<AbstractActivity> findRelationshipTemplateActivities(final TRelationshipTemplate relationshipTemplate) {
        final Collection<AbstractActivity> foundActivities = new HashSet<>();
        for (final AbstractActivity activity : this.activites) {

            if (activity instanceof RelationshipTemplateActivity) {
                if (((RelationshipTemplateActivity) activity).getRelationshipTemplate().equals(relationshipTemplate)) {
                    foundActivities.add(activity);
                }
            }
        }
        return foundActivities;
    }

    public AbstractActivity findNodeTemplateActivity(final TNodeTemplate nodeTemplate, final ActivityType type) {
        for (final AbstractActivity activity : findNodeTemplateActivities(nodeTemplate)) {
            if (activity.getType().equals(type)) {
                return activity;
            }
        }
        return null;
    }

    /**
     * Returns a id for the plugins to make their declarations unique
     *
     * @return an Integer
     */
    public int getInternalCounterId() {
        return this.internalCounterId;
    }

    /**
     * Sets the id
     *
     * @param id an Integer
     */
    public void setInternalCounterId(final int id) {
        this.internalCounterId = id;
    }

    public int getIdForNamesAndIncrement() {
        final int idToReturn = this.getInternalCounterId();
        this.setInternalCounterId(idToReturn + 1);
        return idToReturn;
    }

    @Override
    public String toString() {
        String toString =
            "Plan: " + this.id + " Type: " + this.type + " ServiceTemplate: " + this.serviceTemplate.getId();

        toString += " Activities: ";

        for (final AbstractActivity actic : this.activites) {
            toString += "{Id: " + actic.getId() + " Type: " + actic.getType() + "}";
        }

        toString += " Links: ";

        for (final Link link : this.links) {
            toString += link.toString();
        }

        return toString;
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

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Link)) {
                return false;
            }
            Link oLink = (Link) o;
            return oLink.getSrcActiv().equals(this.getSrcActiv()) & oLink.getTrgActiv().equals(this.getTrgActiv());
        }

        @Override
        public int hashCode() {
            return this.toString().hashCode();
        }
    }
}
