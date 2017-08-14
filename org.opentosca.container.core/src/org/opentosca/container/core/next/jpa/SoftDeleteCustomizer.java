package org.opentosca.container.core.next.jpa;

import java.util.Date;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.DeleteObjectQuery;
import org.eclipse.persistence.queries.QueryRedirector;
import org.eclipse.persistence.queries.UpdateObjectQuery;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;
import org.opentosca.container.core.next.model.PersistenceObject;

public class SoftDeleteCustomizer implements DescriptorCustomizer {

  @Override
  public void customize(final ClassDescriptor descriptor) throws Exception {
    descriptor.setDefaultDeleteObjectQueryRedirector(new SoftDeleteRedirector());
    descriptor.getQueryManager().setAdditionalCriteria("this.deletedAt IS NULL");
  }

  private static class SoftDeleteRedirector implements QueryRedirector {

    private static final long serialVersionUID = -5508649536294092802L;

    @Override
    public Object invokeQuery(final DatabaseQuery query, final Record arguments,
        final Session session) {
      final ClassDescriptor descriptor = session.getDescriptor(query.getReferenceClass());
      final DeleteObjectQuery deleteObjectQuery = (DeleteObjectQuery) query;
      final PersistenceObject entity = (PersistenceObject) deleteObjectQuery.getObject();
      entity.setDeletedAt(new Date());
      final UpdateObjectQuery updateObjectQuery = new UpdateObjectQuery(entity);
      updateObjectQuery.setDescriptor(descriptor);
      deleteObjectQuery.setDescriptor(updateObjectQuery.getDescriptor());
      return updateObjectQuery.execute((AbstractSession) session, (AbstractRecord) arguments);
    }
  }
}
