package org.opentosca.containerapi.resources.utilities;


/**
 * Provides functionality concerning the "template-business"<br>
 * <br>
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author Markus Fischer - fischema@studi.informatik.uni-stuttgart.de
 * @author Rene Trefft - trefftre@studi.informatik.uni-stuttgart.de
 * 
 */
public class TemplateResourceUtilities {
	
	// /**
	// * Compares the id with the ids of the children. Then creates an
	// appropriate
	// * AbstractToscaResourceObject.<br>
	// *
	// * This method is needed due to the fact. That the class of the children
	// of
	// * a TopologyTemplate or GroupTemplate are not known by default.<br>
	// *
	// * @param children List<TExtensibleElements> list of elements to compare
	// to
	// * the id
	// * @param id String the id to compare the elements to
	// * @param uriInfo
	// * @return AbstractToscaResource
	// */
	// public static AbstractToscaResource
	// getTemplateChildResource(List<TExtensibleElements> children, String id,
	// UriInfo uriInfo) {
	// for (TExtensibleElements child : children) {
	// if (child instanceof TNodeTemplate) {
	// TNodeTemplate nt = (TNodeTemplate) child;
	// if (nt.getId().equals(id)) {
	// return new NodeTemplateResource(uriInfo, nt);
	// }
	// }
	//
	// if (child instanceof TRelationshipTemplate) {
	// TRelationshipTemplate rt = (TRelationshipTemplate) child;
	// if (rt.getId().equals(id)) {
	// return new RelationshipTemplateResource(uriInfo, rt);
	// }
	// }
	//
	// // if (child instanceof TGroupTemplate) {
	// // TGroupTemplate gt = (TGroupTemplate) child;
	// // if (gt.getId().equals(id)) {
	// // return new GroupTemplateResource(uriInfo, gt);
	// // }
	// // }
	// }
	// return null;
	// }
}
