package org.opentosca.container.core.model.csar.backwards;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.function.Function;

import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.opentosca.container.core.common.UserException;
import org.opentosca.container.core.model.AbstractArtifact;
import org.opentosca.container.core.model.AbstractDirectory;
import org.opentosca.container.core.model.CSARArtifact;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.model.csar.CsarImpl;

public class ArtifactResolver {

    public static Function<TServiceTemplate, Path> resolveServiceTemplate = (st) -> Paths.get("servicetemplates", st.getTargetNamespace(), st.getName()); 
    
    public static AbstractArtifact resolveArtifact(Csar csar, Path... pathFragments) throws UserException {
        // bleh
        Path location = csar.id().getSaveLocation();
        for (final Path step : pathFragments) {
            location = location.resolve(step);
        }
        return new PathArtifact(location);
    }
    
    private static class PathArtifact extends AbstractArtifact {

        private final Path path;
        
        public PathArtifact(Path path) throws UserException {
            super(path.toString(), Collections.emptySet(), Collections.emptySet());
            this.path = path;
        }
        
        @Override
        protected AbstractDirectory getArtifactRoot() {
            return new FileSystemDirectory(isFileArtifact() ? path.getParent() : path);
        }

        @Override
        public boolean isFileArtifact() {
            return Files.isRegularFile(path);
        }
        
    }
}
