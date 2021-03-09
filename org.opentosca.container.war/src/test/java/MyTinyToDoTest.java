import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.eclipse.winery.accountability.exceptions.AccountabilityException;
import org.eclipse.winery.common.configuration.FileBasedRepositoryConfiguration;
import org.eclipse.winery.common.configuration.GitBasedRepositoryConfiguration;
import org.eclipse.winery.common.configuration.RepositoryConfigurationObject;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.repository.TestWithGitBackedRepository;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.AbstractFileBasedRepository;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;
import org.eclipse.winery.repository.export.CsarExporter;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.control.OpenToscaControlService;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.impl.service.CsarStorageServiceImpl;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.service.CsarStorageService;
import org.opentosca.planbuilder.importer.Importer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations={"classpath:/spring/root-context.xml"})
public class MyTinyToDoTest extends CSARTest {

    @Inject
    OpenToscaControlService control;
    @Inject
    CsarService csarService;
    @Inject
    CsarStorageService storage;

    public MyTinyToDoTest() {
    }

    @Test
    public void test() throws SystemException, UserException, InterruptedException, ExecutionException, RepositoryCorruptException, AccountabilityException, IOException {

        this.fetchCSARFromPublicRepository(RepositoryConfigurationObject.RepositoryProvider.FILE, new QName("http://opentosca.org/servicetemplates","MyTinyToDo_Bare_Docker"),this.storage);

        Assert.assertNotNull(storage);
        Assert.assertNotNull(control);
        Assert.assertNotNull(repository);

        Assert.assertTrue(this.csarService.generatePlans(this.csar));

        this.storage.deleteCSAR(this.csar.id());
    }




}
