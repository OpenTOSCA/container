package org.opentosca.container.core.next.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import javax.xml.namespace.QName;

import org.junit.Test;

import com.google.common.collect.Iterables;

public class RelationshipTemplateInstanceTest {

  @Test
  public void testRelationBetweenNodes() {

    /*
     * Create objects...
     */
    final NodeTemplateInstance source = new NodeTemplateInstance();
    source.setId(1L);
    source.setState(NodeTemplateInstanceState.CONFIGURED);
    source.setTemplateId("Source");
    source.setTemplateType(QName.valueOf("TestType"));

    final NodeTemplateInstance target = new NodeTemplateInstance();
    target.setId(2L);
    target.setState(NodeTemplateInstanceState.CONFIGURED);
    target.setTemplateId("Source");
    target.setTemplateType(QName.valueOf("TestNodeType"));

    final RelationshipTemplateInstance relation = new RelationshipTemplateInstance();
    relation.setId(1L);
    relation.setState(RelationshipTemplateInstanceState.CREATED);
    relation.setTemplateId("HostedOn");
    relation.setTemplateType(QName.valueOf("TestRelationType"));

    /*
     * Wire the nodes...
     */
    relation.setSource(source);
    relation.setTarget(target);

    /*
     * Verify...
     */
    assertThat(source.getOutgoingRelations().size(), is(1));
    assertThat(target.getIncomingRelations().size(), is(1));
    assertThat(Iterables.getFirst(source.getOutgoingRelations(), null), is(relation));
    assertThat(Iterables.getFirst(target.getIncomingRelations(), null), is(relation));
    assertThat(Iterables.getFirst(source.getOutgoingRelations(), null).getSource(), is(source));
    assertThat(Iterables.getFirst(source.getOutgoingRelations(), null).getTarget(), is(target));
    assertThat(Iterables.getFirst(target.getIncomingRelations(), null).getSource(), is(source));
    assertThat(Iterables.getFirst(target.getIncomingRelations(), null).getTarget(), is(target));
  }
}
