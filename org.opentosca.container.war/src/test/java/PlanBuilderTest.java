import javax.inject.Inject;

import org.eclipse.winery.repository.TestWithGitBackedRepository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opentosca.planbuilder.importer.Importer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations={"classpath:/spring/root-context.xml"})
public class PlanBuilderTest{

    @Inject
    Importer importer;

    @Test
    public void test() {
        Assert.assertNotNull(importer);
    }
}
