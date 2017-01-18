package org.opentosca.model.instancedata;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.namespace.QName;

import org.eclipse.persistence.annotations.CascadeOnDelete;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.eclipse.persistence.annotations.Converters;
import org.opentosca.core.model.csar.id.CSARID;
import org.opentosca.settings.Settings;
import org.w3c.dom.Document;

/**
 *
 * Model class representing a ServiceInstance
 *
 * @author Marcus Eisele - marcus.eisele@gmail.com
 */

@Entity
@Converters({@Converter(name = "QNameConverter", converterClass = org.opentosca.util.jpa.converters.QNameConverter.class), @Converter(name = "DOMDocumentConverter", converterClass = org.opentosca.util.jpa.converters.DOMDocumentConverter.class)})
@NamedQueries({@NamedQuery(name = ServiceInstance.getServiceInstances, query = ServiceInstance.getServiceInstancesQuery)})
public class ServiceInstance {
	
	
	// Query to retrieve ServiceInstances identified by a some parameters
	public final static String getServiceInstances = "ServiceInstance.getServiceInstancesQuery";
	protected final static String getServiceInstancesQuery = "select s from ServiceInstance s where" + " s.id = COALESCE(:id, s.id) AND" + " s.serviceTemplateName = COALESCE(:serviceTemplateName, s.serviceTemplateName) AND" + " s.serviceTemplateID = COALESCE(:serviceTemplateID, s.serviceTemplateID)";
	
	// the internal ID (Database) of the ServiceInstance
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	// the external ID (used in all contexts BUT in the Database)
	// it is separated because there is no need to save BOTH into the DB!
	@Transient
	private URI serviceInstanceID;
	
	@Convert("QNameConverter")
	private QName serviceTemplateID;
	
	// the name of the corresponding ServiceTemplate
	private String serviceTemplateName;
	
	@Temporal(TemporalType.TIMESTAMP)
	// the creation date of a ServiceInstance
	private Date created;
	
	@Transient
	private CSARID csarID;
	
	@Column(name = "csarID")
	private String csarID_DB;
	
	@Column(name = "properties", columnDefinition = "VARCHAR(4096)")
	@Convert("DOMDocumentConverter")
	Document properties;
	
	
	// This empty constructor is required by JPA
	@SuppressWarnings("unused")
	private ServiceInstance() {
	}
	
	
	@OneToMany(mappedBy = "serviceInstance", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	// cascade on delete tells the JPA Framework to let the DB handle the
	// deletion (if serviceInstance is deleted => delete also all nodeInstances
	// who reference it!)
	@CascadeOnDelete
	private List<NodeInstance> nodeInstances;
	
	
	/**
	 * Creates a new instance of a ServiceTemplate. ID and creation date will be
	 * set automatically.
	 *
	 * @param serviceTemplateID - the serviceTemplateID specified by the
	 *            Namespace and the ID value of the ServiceTemplate
	 * @param serviceTemplateName - the name of the ServiceTemplate
	 */
	public ServiceInstance(CSARID csarID, QName serviceTemplateID, String serviceTemplateName) {
		super();
		this.csarID = csarID;
		// needed to persist the object
		csarID_DB = csarID.getFileName();
		
		setServiceTemplateID(serviceTemplateID);
		this.serviceTemplateName = serviceTemplateName;
		created = new Date();
		properties = null;
	}
	
	public String getServiceTemplateName() {
		return serviceTemplateName;
	}
	
	public int getDBId() {
		return id;
	}
	
	public URI getServiceInstanceID() {
		return serviceInstanceID;
	}
	
	public QName getToscaID() {
		return getServiceTemplateID();
	}
	
	public Date getCreated() {
		return created;
	}
	
	public void setServiceTemplateName(String serviceTemplateName) {
		this.serviceTemplateName = serviceTemplateName;
	}
	
	public CSARID getCSAR_ID() {
		return csarID;
	}
	
	/**
	 * The ID persisted in the database is "only" an integer. To the outside, we
	 * need the ID to be an URI. To avoid storing two IDs in the database we
	 * generate the URI ID out of the integer ID. Therefore, when reading a
	 * ServiceInstance object from the database we need to set the URI ID
	 * accordingly.
	 */
	@PostLoad
	@PostPersist
	private void setIDs() {
		try {
			serviceInstanceID = new URI(Settings.CONTAINER_API + IdConverter.serviceInstancePath + id);
			csarID = new CSARID(csarID_DB);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public void setProperties(Document props) {
		properties = props;
	}
	
	public Document getProperties() {
		return properties;
	}
	
	@Override
	public String toString() {
		return "id:" + id + " created:" + created + " sID:" + serviceInstanceID + " templateID: " + getToscaID().toString() + " template name: " + serviceTemplateName;
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return id;
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ServiceInstance)) {
			return false;
		}
		ServiceInstance other = (ServiceInstance) obj;
		if (created == null) {
			if (other.created != null) {
				return false;
			}
		} else if (!created.equals(other.created)) {
			return false;
		}
		if (csarID == null) {
			if (other.csarID != null) {
				return false;
			}
		} else if (!csarID.equals(other.csarID)) {
			return false;
		}
		if (csarID_DB == null) {
			if (other.csarID_DB != null) {
				return false;
			}
		} else if (!csarID_DB.equals(other.csarID_DB)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (serviceInstanceID == null) {
			if (other.serviceInstanceID != null) {
				return false;
			}
		} else if (!serviceInstanceID.equals(other.serviceInstanceID)) {
			return false;
		}
		if (getServiceTemplateID() == null) {
			if (other.getServiceTemplateID() != null) {
				return false;
			}
		} else if (!getServiceTemplateID().equals(other.getServiceTemplateID())) {
			return false;
		}
		if (serviceTemplateName == null) {
			if (other.serviceTemplateName != null) {
				return false;
			}
		} else if (!serviceTemplateName.equals(other.serviceTemplateName)) {
			return false;
		}
		return true;
	}
	
	public QName getServiceTemplateID() {
		return serviceTemplateID;
	}
	
	public void setServiceTemplateID(QName serviceTemplateID) {
		this.serviceTemplateID = serviceTemplateID;
	}
	
	public List<NodeInstance> getNodeInstances() {
		return nodeInstances;
	}
	
}
