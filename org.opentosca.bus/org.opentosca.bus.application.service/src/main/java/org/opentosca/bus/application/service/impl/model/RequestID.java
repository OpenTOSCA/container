package org.opentosca.bus.application.service.impl.model;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Manages the requestIDs needed to correlate the invocation-requests, the isFinished-requests as well as the
 * getResult-requests.
 *
 * @author Michael Zimmermann - zimmerml@studi.informatik.uni-stuttgart.de
 */
public class RequestID {

    private static AtomicLong incrementer = new AtomicLong(0);

    /**
     * @return requestID
     */
    public synchronized static Long getNextID() {

        final Long id = incrementer.getAndIncrement();

        // For the unlikely case, that MAX_Value is reached, begin with 0
        // again. Assumption: old requests were processed in the mean time.
        if (id == Long.MAX_VALUE) {
            incrementer.set(0);
        }

        return id;
    }
}
