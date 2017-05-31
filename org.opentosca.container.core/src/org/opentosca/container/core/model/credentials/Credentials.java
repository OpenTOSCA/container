package org.opentosca.container.core.model.credentials;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Credentials for a storage provider. A credentials consists of an identity and
 * a key. Used by the Credentials Service, a storage provider and in the
 * Container API for a Credentials JAXB model (thus it has JAXB annotations).
 */
@XmlRootElement(name = "Credentials")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity(name = Credentials.tableName)
@Table(name = Credentials.tableName, uniqueConstraints = @UniqueConstraint(name = "credentialsUniqueConstraints", columnNames = {"\"storageProviderID\"", "\"identity\""}))
@NamedQueries({@NamedQuery(name = Credentials.getCredentialsByID, query = Credentials.getCredentialsByIDQuery), @NamedQuery(name = Credentials.getCredentialsIDs, query = Credentials.getCredentialsIDsQuery), @NamedQuery(name = Credentials.getAllCredentials, query = Credentials.getAllCredentialsQuery), @NamedQuery(name = Credentials.removeCredentialsByID, query = Credentials.removeCredentialsByIDQuery), @NamedQuery(name = Credentials.removeAllCredentials, query = Credentials.removeAllCredentialsQuery), @NamedQuery(name = Credentials.getAllCredentialsByStorageProviderID, query = Credentials.getAllCredentialsByStorageProviderIDQuery)})
public class Credentials {
	
	protected static final String tableName = "Credentials";
	
	/*
	 * JPQL Queries
	 */
	public static final String getCredentialsByID = Credentials.tableName + ".getCredentialsByID";
	protected static final String getCredentialsByIDQuery = "SELECT t FROM " + Credentials.tableName + " t WHERE t.id = :id";
	
	public static final String getCredentialsIDs = Credentials.tableName + ".getCredentialsIDs";
	protected static final String getCredentialsIDsQuery = "SELECT t.id FROM " + Credentials.tableName + " t";
	
	public static final String getAllCredentials = Credentials.tableName + ".getAllCredentials";
	protected static final String getAllCredentialsQuery = "SELECT t FROM " + Credentials.tableName + " t";
	
	public static final String removeCredentialsByID = Credentials.tableName + ".removeCredentialsByID";
	protected static final String removeCredentialsByIDQuery = "DELETE FROM " + Credentials.tableName + " t WHERE t.id = :id";
	
	public static final String removeAllCredentials = Credentials.tableName + ".removeAllCredentials";
	protected static final String removeAllCredentialsQuery = "DELETE FROM " + Credentials.tableName + " t";
	
	public static final String getAllCredentialsByStorageProviderID = Credentials.tableName + ".getCredentialsByStorageProviderID";
	protected static final String getAllCredentialsByStorageProviderIDQuery = "SELECT t FROM " + Credentials.tableName + " t WHERE t.storageProviderID = :storageProviderID";
	
	/**
	 * Identifies these credentials.<br />
	 * Will be automatically generated if an object of this class is persisted
	 * using Eclipse Link.
	 */
	@XmlAttribute(name = "id")
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "credentialsSequence")
	@SequenceGenerator(name = "credentialsSequence", sequenceName = "credentialsSequence")
	private Long id;
	
	/**
	 * ID of the storage provider for that these credentials are indented for.
	 */
	@XmlElement(name = "StorageProviderID")
	@Column(name = "storageProviderID")
	private String storageProviderID;
	
	/**
	 * Identity of these credentials.<br />
	 * E.g. on Amazon S3 it's the Access Key ID.
	 *
	 */
	@XmlElement(name = "Identity")
	@Column(name = "\"identity\"")
	private String identity;
	
	/**
	 * Key of these credentials.<br />
	 * E.g. on Amazon S3 it's the Secret Access Key.
	 */
	@XmlElement(name = "Key")
	@Column(name = "\"key\"")
	private String key;
	
	/**
	 * Optional description of these credentials.
	 */
	@XmlElement(name = "Description")
	@Column(name = "description")
	private String description;
	
	
	/**
	 * Needed by Eclipse Link.
	 */
	protected Credentials() {
	}
	
	/**
	 * Creates {@link Credentials}.
	 *
	 * @param storageProviderID of the storage provider for that the credentials
	 *            are indented for.
	 * @param identity of the credentials.
	 * @param key of the credentials.
	 * @param description of the credentials (optional)
	 */
	public Credentials(final String storageProviderID, final String identity, final String key, final String description) {
		this.storageProviderID = storageProviderID;
		this.identity = identity;
		this.key = key;
		this.description = description;
	}
	
	/**
	 * @return ID of these credentials. If no ID was set yet {@code null}.
	 */
	public Long getID() {
		return this.id;
	}
	
	/**
	 * Sets the ID of these credentials.<br />
	 *
	 * @param id to set.
	 */
	protected void setID(final long id) {
		this.id = id;
	}
	
	/**
	 *
	 * @return ID of the storage provider for that these credentials are
	 *         intended for.
	 */
	public String getStorageProviderID() {
		return this.storageProviderID;
	}
	
	/**
	 * Sets the ID of the storage provider for that these credentials are
	 * intended for.
	 *
	 * @param storageProviderID to set.
	 */
	public void setStorageProviderID(final String storageProviderID) {
		this.storageProviderID = storageProviderID;
	}
	
	/**
	 * @return Identity of these credentials.
	 */
	public String getIdentity() {
		return this.identity;
	}
	
	/**
	 * Sets the identity of these credentials.
	 *
	 * @param identity to set
	 */
	public void setIdentity(final String identity) {
		this.identity = identity;
	}
	
	/**
	 * @return Key of these credentials.
	 */
	public String getKey() {
		return this.key;
	}
	
	/**
	 * Sets the key of these credentials.
	 *
	 * @param key to set.
	 */
	public void setKey(final String key) {
		this.key = key;
	}
	
	/**
	 * @return Description of these credentials.<br />
	 *         It's optional. If no description exists {@code null}.
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Sets the description of these credentials.
	 *
	 * @param description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}
	
	@Override
	public boolean equals(final Object obj) {
		
		if (!(obj instanceof Credentials)) {
			return false;
		}
		
		final Credentials credentials = (Credentials) obj;
		
		// null safe equals
		if (!Objects.equals(this.getID(), credentials.getID())) {
			return false;
		}
		
		if (!Objects.equals(this.getStorageProviderID(), credentials.getStorageProviderID())) {
			return false;
		}
		
		if (!Objects.equals(this.getIdentity(), credentials.getIdentity())) {
			return false;
		}
		
		if (!Objects.equals(this.getKey(), credentials.getKey())) {
			return false;
		}
		
		if (!Objects.equals(this.getDescription(), credentials.getDescription())) {
			return false;
		}
		
		return true;
		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.description == null) ? 0 : this.description.hashCode());
		result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
		result = (prime * result) + this.identity.hashCode();
		result = (prime * result) + this.key.hashCode();
		result = (prime * result) + this.storageProviderID.hashCode();
		return result;
	}
}
