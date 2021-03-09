import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.eclipse.winery.accountability.exceptions.AccountabilityException;
import org.eclipse.winery.common.configuration.RepositoryConfigurationObject;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opentosca.container.api.service.CsarService;
import org.opentosca.container.control.OpenToscaControlService;
import org.opentosca.container.core.common.SystemException;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.service.CsarStorageService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/root-context.xml"})
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

        this.fetchCSARFromPublicRepository(RepositoryConfigurationObject.RepositoryProvider.FILE, new QName("http://opentosca.org/servicetemplates", "MyTinyToDo_Bare_Docker"), this.storage);

        Assert.assertNotNull(storage);
        Assert.assertNotNull(control);
        Assert.assertNotNull(repository);

        Assert.assertTrue(this.csarService.generatePlans(this.csar));

        final List<TServiceTemplate> serviceTemplates = this.csar.serviceTemplates();
        for (final TServiceTemplate serviceTemplate : serviceTemplates) {
            this.control.invokePlanDeployment(this.csar.id(), serviceTemplate);
        }



        this.control.deleteCsar(this.csar.id());
    }
}
