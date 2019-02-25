package org.opentosca.container.core.model.csar.backwards;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TPlan;
import org.opentosca.container.core.common.uri.UriUtil;
import org.opentosca.container.core.model.AbstractArtifact;
import org.opentosca.container.core.model.AbstractDirectory;
import org.opentosca.container.core.model.AbstractFile;
import org.opentosca.container.core.model.csar.Csar;

public class ArtifactResolver {

    public static Function<TServiceTemplate, Path> resolveServiceTemplate = (st) -> Paths.get("servicetemplates", UriUtil.encodePathSegment(st.getTargetNamespace()), st.getName());
    public static BiFunction<TServiceTemplate, TPlan, Path> resolvePlan = (st, plan) -> resolveServiceTemplate.apply(st).resolve("plans").resolve(plan.getId()); 
    
    public static AbstractArtifact resolveArtifact(Csar csar, Path... pathFragments) {
        // FIXME do not rely on save-location
        Path location = csar.getSaveLocation();
        for (final Path step : pathFragments) {
            location = location.resolve(step);
        }
        return new PathArtifact(location.toAbsolutePath());
    }
    
    private static class PathArtifact extends AbstractArtifact {

        private final Path path;
        
        public PathArtifact(Path path) {
            super(path.toString(), Collections.emptySet(), Collections.emptySet());
            this.path = path;
        }
        
        private Path artifactRoot() {
            return isFileArtifact() ? path.getParent() : path;
        }
        @Override
        protected AbstractDirectory getArtifactRoot() {
            return new FileSystemDirectory(artifactRoot());
        }

        @Override
        public boolean isFileArtifact() {
            return Files.isRegularFile(path);
        }
        
        @Override
        public AbstractFile getFile(String name) {
            if (isFileArtifact()) {
                return new FileSystemFile(path);
            }
            if (name.isEmpty()) {
                // no way to resolve an empty name
                return null;
            }
            return new FileSystemFile(artifactRoot().resolve(name));
        }
    }
}
