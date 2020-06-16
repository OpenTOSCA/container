package org.opentosca.bus.management.api.resthttp.model;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Map that manages the status of the requests. RequestID is used as <tt>key</tt> of the map. The
 * <tt>value</tt> of the map indicates if the invocation has finished or not.
 *
 * @author Michael Zimmermann - zimmerml@iaas.uni-stuttgart.de
 */
public class QueueMap {

  final private static Logger LOG = LoggerFactory.getLogger(QueueMap.class);

  private static ConcurrentHashMap<String, Boolean> queue = new ConcurrentHashMap<>();

  /**
   * Inserts an entry into the queue (if not already existing) and set it to finished.
   *
   * @param id of the request
   */
  public static void finished(final String id) {

    QueueMap.LOG.debug("Request with ID: {} has finished.", id);

    queue.put(id, true);
  }

  /**
   * Inserts an entry into the queue and set it to notFinished. Only if the id not already exists.
   *
   * @param id of the request
   */
  public static void notFinished(final String id) {

    QueueMap.LOG.debug("Request with ID: {} hasn't finished yet.", id);

    queue.putIfAbsent(id, false);
  }

  /**
   * Inserts an entry into the queue.
   *
   * @param id         of the request
   * @param isFinished specifies if the invocation has finished or not
   */
  public static void put(final String id, final Boolean isFinished) {

    QueueMap.LOG.debug("RequestID: {}, isFinished: {}", id, isFinished);

    queue.put(id, isFinished);
  }

  /**
   * @param id of the request
   * @return <tt>true</tt> if the invocation has finished. Otherwise <tt>false</tt>
   */
  public static boolean hasFinished(final String id) {

    return queue.get(id);
  }

  /**
   * @param id of the request
   * @return <tt>true</tt> if the queue contains the specified requestID. Otherwise <tt>false</tt>
   */
  public static boolean containsID(final String id) {
    return queue.containsKey(id);
  }

  /**
   * Removes the entry with the specified requestID from the queue.
   *
   * @param id of the request
   */
  public static void remove(final String id) {
    queue.remove(id);
  }

}
