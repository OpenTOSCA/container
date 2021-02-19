package org.opentosca.planbuilder.importer;

import javax.inject.Inject;

import org.eclipse.winery.repository.TestWithGitBackedRepository;

import org.junit.jupiter.api.Test;
import org.opentosca.container.core.impl.service.CsarStorageServiceImpl;
import org.opentosca.container.core.model.csar.CsarId;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.model.tosca.AbstractDefinitions;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ImporterTest extends TestWithGitBackedRepository {

    @Inject
    PluginRegistry pluginRegistry;

    @Test
    void testImport() throws Exception {
        this.setRevisionTo("origin/plain");

        CsarStorageServiceImpl storage = new CsarStorageServiceImpl(this.repository.getRepositoryRoot());
//        List<IPlanBuilderPlugin> plugins = new ArrayList<>();
//        plugins.add(new )
//        PluginRegistry pluginRegistry = new PluginRegistry(plugins);

        Importer importer = new Importer(pluginRegistry, storage);
        AbstractDefinitions context = importer.createContext(new CsarId("TestableWebshop_w1-wip1-mimicTest-w1-wip1.csar"));

        assertNotNull(context);
    }
}
