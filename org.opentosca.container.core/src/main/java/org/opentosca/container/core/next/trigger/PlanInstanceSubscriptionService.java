package org.opentosca.container.core.next.trigger;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

import org.opentosca.container.core.next.model.PersistenceObject;
import org.opentosca.container.core.next.model.PlanInstance;
import org.opentosca.container.core.next.model.PlanInstanceState;
import org.opentosca.container.core.next.repository.PlanInstanceRepository;
import org.springframework.stereotype.Component;

@Component
public class PlanInstanceSubscriptionService {

    private static Map<Long, Collection<PlanInstanceExpectedStateSubscription>> stateExpectedSubscriptions = Collections.synchronizedMap(new HashMap<>());
    private static Map<String, Collection<PlanInstanceAvailableSubscription>> instanceAvailableSubscriptions = Collections.synchronizedMap(new HashMap<>());

    private void remove(PlanInstanceSubscription planInstanceSubscription) {
        if (planInstanceSubscription instanceof PlanInstanceAvailableSubscription) {
            instanceAvailableSubscriptions.get(planInstanceSubscription.getInstance().getCorrelationId()).remove(planInstanceSubscription);
        } else if (planInstanceSubscription instanceof PlanInstanceExpectedStateSubscription) {
            stateExpectedSubscriptions.get(planInstanceSubscription.getInstance().getId()).remove(planInstanceSubscription);
        }
    }

    public SubscriptionRunner subscribeToStateChange(PlanInstance planInstance, PlanInstanceState expectedState) {
        PlanInstanceExpectedStateSubscription planInstanceSubscription = new PlanInstanceExpectedStateSubscription(planInstance, this, expectedState);

        if (stateExpectedSubscriptions.containsKey(planInstance.getId())) {
            stateExpectedSubscriptions.get(planInstance.getId()).add(planInstanceSubscription);
        } else {
            Collection<PlanInstanceExpectedStateSubscription> subs = Collections.synchronizedCollection(new HashSet<>());
            subs.add(planInstanceSubscription);
            stateExpectedSubscriptions.put(planInstance.getId(), subs);
        }
        SubscriptionRunner t = new SubscriptionRunner<>(planInstanceSubscription);
        t.start();
        return t;
    }

    public SubscriptionRunner subscribeToInstanceAvailable(String correlationId, PlanInstanceRepository planRepo) {
        PlanInstanceAvailableSubscription planInstanceAvailableSubscription = new PlanInstanceAvailableSubscription(planRepo.findByCorrelationId(correlationId), this, correlationId);
        if (instanceAvailableSubscriptions.containsKey(correlationId)) {
            instanceAvailableSubscriptions.get(correlationId).add(planInstanceAvailableSubscription);
        } else {
            Collection<PlanInstanceAvailableSubscription> subs = Collections.synchronizedCollection(new HashSet<>());
            subs.add(planInstanceAvailableSubscription);
            instanceAvailableSubscriptions.put(correlationId, subs);
        }
        SubscriptionRunner t = new SubscriptionRunner<>(planInstanceAvailableSubscription);
        t.start();
        return t;
    }

    @PostPersist
    void planInstanceAfterCreate(final PlanInstance planInstance) {
        System.out.println("post create method was called");
        Collection<PlanInstanceAvailableSubscription> subsInstanceAvailable = instanceAvailableSubscriptions.get(planInstance.getCorrelationId());
        if (subsInstanceAvailable != null && !subsInstanceAvailable.isEmpty()) {
            System.out.println("Notifying planinstance available subscribers");
            subsInstanceAvailable.forEach(sub -> {
                sub.updatePlanInstance(planInstance);
            });
        }
    }

    @PostUpdate
    void planInstanceAfterUpdate(final PlanInstance planInstance) {
        System.out.println("post update method was called");
        Collection<PlanInstanceExpectedStateSubscription> subsStateExpected = stateExpectedSubscriptions.get(planInstance.getId());

        if (subsStateExpected != null && !subsStateExpected.isEmpty()) {
            System.out.println("Notifying planinstance state expected subscribers");
            subsStateExpected.forEach(sub -> {
                sub.updatePlanInstance(planInstance);
            });
        }
    }

    public class SubscriptionRunner<T extends PlanInstanceSubscription> extends Thread {

        private T obj;

        public SubscriptionRunner(T obj) {
            super(obj);
            this.obj = obj;
        }

        public PersistenceObject joinAndGet() {
            try {
                this.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            obj.removeSubscription();
            return obj.getInstance();
        }

        public PersistenceObject joinAndGet(long timeout) {
            try {
                this.join(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            obj.removeSubscription();
            return obj.getInstance();
        }
    }

    public abstract class PlanInstanceSubscription implements Runnable {
        private PlanInstance planInstance;
        private PlanInstanceSubscriptionService service;

        public PlanInstanceSubscription(PlanInstance planInstance, PlanInstanceSubscriptionService service) {
            System.out.println("Created subscription");
            this.planInstance = planInstance;
            this.service = service;
        }

        public synchronized PlanInstance getInstance() {
            return this.planInstance;
        }

        public synchronized void updatePlanInstance(PlanInstance planInstance) {
            System.out.println("Received updated plan instance");
            System.out.println("InstanceState: " + planInstance.getState());
            this.planInstance = planInstance;
        }

        public void removeSubscription() {
            this.service.remove(this);
        }

        public abstract boolean conditionIsMet();

        @Override
        public void run() {
            System.out.println("Started subscription");
            while (!this.conditionIsMet()) {
                try {
                    Thread.sleep(1000);
                    //System.out.println("Current State: " + this.getInstance().getState() );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Ended subscription");
        }
    }

    public class PlanInstanceAvailableSubscription extends PlanInstanceSubscription {
        private String expectedCorrelation;

        public PlanInstanceAvailableSubscription(PlanInstance planInstance, PlanInstanceSubscriptionService service, String expectedCorrelation) {
            super(planInstance, service);
            this.expectedCorrelation = expectedCorrelation;
            System.out.println("Created subscription for corrId: " + this.expectedCorrelation);
        }

        public boolean conditionIsMet() {
            return !(this.getInstance() == null || !this.getInstance().getCorrelationId().equals(this.expectedCorrelation));
        }
    }

    public class PlanInstanceExpectedStateSubscription extends PlanInstanceSubscription {
        private PlanInstanceState expectedState;

        public PlanInstanceExpectedStateSubscription(PlanInstance planInstance, PlanInstanceSubscriptionService service, PlanInstanceState expectedState) {
            super(planInstance, service);
            System.out.println("InstanceState: " + planInstance.getState());
            this.expectedState = expectedState;
        }

        public boolean conditionIsMet() {
            return this.getInstance().getState().toString().equals(this.expectedState.toString());
        }
    }
}
