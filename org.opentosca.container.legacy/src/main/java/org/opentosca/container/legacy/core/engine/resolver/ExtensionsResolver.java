package org.opentosca.container.legacy.core.engine.resolver.resolver;

import org.eclipse.winery.model.tosca.Definitions;

/**
 * The ExtensionsResolver resolves references inside of TOSCA Extensions according to the TOSCA
 * specification wd14. Each found element and the document in which the element is nested is stored
 * by the org.opentosca.core.model.toscareferencemapping.ToscaReferenceMapper.
 * <p>
 * Preconditions for resolving an Extensions: Definitions has to be valid in all kind of meanings.
 *
 * @author Christian Endres - endrescn@studi.informatik.uni-stuttgart.de
 */
public class ExtensionsResolver extends GenericResolver {

  /**
   * Instantiate an object of the Resolver to resolve references inside of Extensions. This
   * constructor sets the ReferenceMapper which search for references.
   *
   * @param referenceMapper
   */
  public ExtensionsResolver(final ReferenceMapper referenceMapper) {
    super(referenceMapper);
  }

  /**
   * Resolves an Extensions and stores the mapping into the ToscaReferenceMapper.
   *
   * @param definitions The Definitions object.
   * @return true if an error occurred, false if not
   */
  public boolean resolve(final Definitions def) {
    // nothing to do here
    return false;
  }

}
