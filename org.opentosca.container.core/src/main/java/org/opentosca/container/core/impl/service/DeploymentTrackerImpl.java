package org.opentosca.container.core.impl.service;

import java.util.Collection;
import java.util.List;

import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.container.core.next.model.DeploymentProcessInfo;
import org.opentosca.container.core.next.model.DeploymentProcessState;
import org.opentosca.container.core.next.model.IADeploymentInfo;
import org.opentosca.container.core.next.model.IADeploymentState;
import org.opentosca.container.core.next.model.PlanDeploymentInfo;
import org.opentosca.container.core.next.model.PlanDeploymentState;
import org.opentosca.container.core.next.repository.DeploymentProcessInfoRepository;
import org.opentosca.container.core.next.repository.IADeploymentInfoRepository;
import org.opentosca.container.core.next.repository.PlanDeploymentInfoRepository;
import org.opentosca.container.core.service.DeploymentTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DeploymentTrackerImpl implements DeploymentTracker {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentTrackerImpl.class);

    private final DeploymentProcessInfoRepository deploymentProcessInfoRepository;
    private final IADeploymentInfoRepository iaDeploymentInfoRepository;
    private final PlanDeploymentInfoRepository planDeploymentInfoRepository;

    public DeploymentTrackerImpl(DeploymentProcessInfoRepository deploymentProcessInfoRepository, IADeploymentInfoRepository iaDeploymentInfoRepository, PlanDeploymentInfoRepository planDeploymentInfoRepository) {
        this.deploymentProcessInfoRepository = deploymentProcessInfoRepository;
        this.iaDeploymentInfoRepository = iaDeploymentInfoRepository;
        this.planDeploymentInfoRepository = planDeploymentInfoRepository;
    }

    @Override
    public synchronized void storeDeploymentState(CsarId csar, DeploymentProcessState state) {
        LOGGER.trace("Storing deployment state {} for Csar {}.", state, csar.csarName());

        final DeploymentProcessInfo currentInformation = getDeploymentInfo(csar);
        if (currentInformation == null) {
            deploymentProcessInfoRepository.save(new DeploymentProcessInfo(csar, state));
        } else {
            currentInformation.setDeploymentProcessState(state);
            deploymentProcessInfoRepository.save(currentInformation);
        }
        LOGGER.debug("Completed storing deployment state {} for Csar {}", state, csar.csarName());
    }

    @Override
    public synchronized DeploymentProcessState getDeploymentState(CsarId csar) {
        final DeploymentProcessInfo info = getDeploymentInfo(csar);
        if (info == null) {
            LOGGER.warn("No deployment state for Csar {} found", csar.csarName());
            return null;
        }
        LOGGER.trace("Deployment state of Csar {} is {}", csar.csarName(), info.getDeploymentProcessState());
        return info.getDeploymentProcessState();
    }

    private synchronized DeploymentProcessInfo getDeploymentInfo(CsarId csar) {
        LOGGER.trace("Retrieving deployment information for Csar {} from database", csar.csarName());
        List<DeploymentProcessInfo> deploymentProcessInfoList = deploymentProcessInfoRepository.findByCsarID(csar);
        if (deploymentProcessInfoList.isEmpty()) {
            LOGGER.debug("No deployment information associated with Csar {} found", csar.csarName());
            return null;
        }
        if (deploymentProcessInfoList.size() > 1) {
            LOGGER.warn("Multiple deployment information results found for Csar {}", csar.csarName());
            return null;
        }
        return deploymentProcessInfoList.get(0);
    }

    @Override
    public synchronized void storeIADeploymentInfo(IADeploymentInfo info) {
        LOGGER.trace("Storing deployment state {} for IA \"{}\" of CSAR \"{}\"...",
            info.getDeploymentState(), info.getRelPath(), info.getCsarID().csarName());

        // check if deployment info for this IA already exists
        final IADeploymentInfo storedIA = getIADeploymentInfo(info.getCsarID(), info.getRelPath());

        // deployment info already exists
        if (storedIA != null) {
            LOGGER.info("Updating IA deployment info for IA [{}] of CSAR [{}].", info.getRelPath(), info.getCsarID().csarName());

            final IADeploymentState storedIADeployState = storedIA.getDeploymentState();
            final IADeploymentState newIADeployState = info.getDeploymentState();

            // if IA is deployed and will be now undeployed, reset the attempt counter to 0
            if (storedIADeployState.equals(IADeploymentState.IA_DEPLOYED)
                && newIADeployState.equals(IADeploymentState.IA_UNDEPLOYING)) {
                LOGGER.trace("Deployed IA [{}] of CSAR [{}] is now undeploying. Resetting attempt count.", info.getRelPath(), info.getCsarID().csarName());
                storedIA.setAttempt(0);
            }
            storedIA.setDeploymentState(newIADeployState);
            info = storedIA;
        }

        // if IA is now deploying or undeploying, increment attempt counter
        if (info.getDeploymentState().equals(IADeploymentState.IA_DEPLOYING)
            || info.getDeploymentState().equals(IADeploymentState.IA_UNDEPLOYING)) {
            LOGGER.trace("IA [{}] of CSAR [{}] is now deploying / undeploying. Incrementing attempt count.", info.getRelPath(), info.getCsarID());
            info.setAttempt(info.getAttempt() + 1);
        }

        iaDeploymentInfoRepository.save(info);
        LOGGER.debug("Stored deployment state {} for IA [{}] of CSAR [{}].", info.getDeploymentState(), info.getRelPath(), info.getCsarID().csarName());
    }

    // FIXME do not return IADeploymentInfo. Attempts are only used internally.
    //  Instead return the DeploymentState of a compound key encapsulating CsarId and RelPath
    @Override
    public synchronized IADeploymentInfo getIADeploymentInfo(CsarId csar, String iaRelPath) {
        LOGGER.trace("Retrieving IA Deployment info for IA [{}] in Csar {}", iaRelPath, csar.csarName());
        List<IADeploymentInfo> iaDeploymentInfoList = iaDeploymentInfoRepository.findByCsarIDAndRelPath(csar, iaRelPath);
        if (iaDeploymentInfoList.size() != 1) {
            LOGGER.warn("Unequal 1 IA deployment information stored for IA [{}] of Csar {}: {}", iaRelPath, csar.csarName(), iaDeploymentInfoList.size());
            return null;
        }
        return iaDeploymentInfoList.get(0);
    }

    @Override
    public synchronized Collection<IADeploymentInfo> getIADeployments(CsarId csar) {
        LOGGER.trace("Retrieving IA Deployment info for all IAs in Csar {}", csar.csarName());
        return iaDeploymentInfoRepository.findByCsarID(csar);
    }

    @Override
    public synchronized void storePlanDeploymentInfo(PlanDeploymentInfo info) {
        LOGGER.trace("Storing deployment state {} for Plan [{}] of Csar {}", info.getDeploymentState(), info.getRelPath(), info.getCsarID().csarName());

        // check if deployment info for this Plan already exists
        final PlanDeploymentInfo storedPlan = this.getPlanDeploymentInfo(info.getCsarID(), info.getRelPath());
        // deployment info already exists
        if (storedPlan != null) {
            LOGGER.debug("Overwriting Plan deployment info for Plan [{}] of Csar [{}].", info.getRelPath(), info.getCsarID().csarName());

            final PlanDeploymentState storedPlanDeployState = storedPlan.getDeploymentState();
            final PlanDeploymentState newPlanDeployState = info.getDeploymentState();

            // if Plan is deployed and will be now undeployed, reset the attempt counter to 0
            if (storedPlanDeployState.equals(PlanDeploymentState.PLAN_DEPLOYED)
                && newPlanDeployState.equals(PlanDeploymentState.PLAN_UNDEPLOYING)) {
                LOGGER.debug("Deployed Plan [{}] of Csar [{}] is now undeploying. Resetting attempt count.", info.getRelPath(), info.getCsarID().csarName());
                storedPlan.setAttempt(0);
            }

            storedPlan.setDeploymentState(newPlanDeployState);
            info = storedPlan;
        }

        // if Plan is now deploying or undeploying, increment attempt counter
        if (info.getDeploymentState().equals(PlanDeploymentState.PLAN_DEPLOYING)
            || info.getDeploymentState().equals(PlanDeploymentState.PLAN_UNDEPLOYING)) {
            LOGGER.debug("Plan [{}] of CSAR [{}] is now deploying / undeploying. Increase attempt count.", info.getRelPath(), info.getCsarID().csarName());
            info.setAttempt(info.getAttempt() + 1);
        }

        planDeploymentInfoRepository.save(info);
        LOGGER.info("Stored deployment state {} for Plan [{}] of Csar [{}].", info.getDeploymentState(), info.getRelPath(), info.getCsarID().csarName());
    }

    @Override
    public synchronized PlanDeploymentInfo getPlanDeploymentInfo(CsarId csar, String planRelPath) {
        LOGGER.trace("Retrieving plan deployment information for plan [{}] in Csar {}", planRelPath, csar.csarName());
        List<PlanDeploymentInfo> planDeploymentInfoList = planDeploymentInfoRepository.findByCsarIDAndRelPath(csar, planRelPath);
        if (planDeploymentInfoList.size() != 1) {
            LOGGER.warn("Unequal one plan deployment information stored for Plan [{}] in Csar {}: {}", planRelPath, csar.csarName(), planDeploymentInfoList.size());
            return null;
        }
        return planDeploymentInfoList.get(0);
    }

    @Override
    public synchronized Collection<PlanDeploymentInfo> getPlanDeployments(CsarId csar) {
        LOGGER.trace("Retrieving plan deployment information for all plans in Csar {}", csar.csarName());
        return planDeploymentInfoRepository.findByCsarID(csar);
    }

    @Override
    public synchronized void deleteDeploymentState(CsarId csar) {
        LOGGER.info("Deleting all deployment state associated with Csar {}", csar.csarName());
        try {
            final Collection<IADeploymentInfo> iaDeployments = getIADeployments(csar);
            LOGGER.trace("Marking {} IA deployments for removal", iaDeployments.size());
            iaDeploymentInfoRepository.deleteAll(iaDeployments);

            final Collection<PlanDeploymentInfo> planDeployments = getPlanDeployments(csar);
            LOGGER.trace("Marking {} Plan deployments for removal", planDeployments.size());
            planDeploymentInfoRepository.deleteAll(planDeployments);

            final DeploymentProcessInfo csarInfo = getDeploymentInfo(csar);
            LOGGER.trace("Marking Csar for removal");
            if (csarInfo != null) {
                deploymentProcessInfoRepository.delete(csarInfo);
            }
        } catch (Exception e) {
            throw e;
        }
        LOGGER.trace("Marked changes have been persisted");
    }
}
