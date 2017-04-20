package org.opentosca.core.service;

import java.util.List;
import java.util.Map;

import org.opentosca.core.model.capability.provider.ProviderType;

/**
 * This interface provides methods to store, get and delete capabilities of
 * different provider and provider types.<br>
 * <p>
 * The IAEngine needs this capabilities to decide if a Implementation Artifact
 * should be deployed or not.
 *
 * @see ProviderType
 */
public interface ICoreCapabilityService {

	/**
	 * Stores capabilities.
	 *
	 * @param capabilities to store.
	 * @param providerName Name of the provider (e.g. a plugin) where the
	 *            capabilities are from.
	 * @param providerType identifies if the capabilities are from the
	 *            Container, a PlanEnginePlugin or a IAEnginePlugin (see
	 *            {@link ProviderType}).
	 */
	public void storeCapabilities(List<String> capabilities, String providerName, ProviderType providerType);

	/**
	 * @param providerType identifies if the capabilities are from the
	 *            Container, a PlanEnginePlugin or a IAEnginePlugin (see
	 *            {@link ProviderType}).
	 * @return Map with all providers and their capabilities.
	 */
	public Map<String, List<String>> getCapabilities(ProviderType providerType);

	/**
	 * @param providerName Name of the provider (e.g. a plugin) where the
	 *            capabilities should be fetched from.
	 * @param providerType identifies if the capabilities are from the
	 *            Container, a PlanEnginePlugin or a IAEnginePlugin (see
	 *            {@link ProviderType}).
	 * @return List with all capabilities of given provider.
	 */
	public List<String> getCapabilities(String providerName, ProviderType providerType);

	/**
	 * Deletes all stored capabilities of a provider.
	 *
	 * @param providerName Name of the provider (e.g. a plugin) where the
	 *            capabilities should be deleted from.
	 */
	public void deleteCapabilities(String providerName);
}
