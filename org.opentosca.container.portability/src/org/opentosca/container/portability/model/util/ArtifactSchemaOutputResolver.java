package org.opentosca.container.portability.model.util;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

public class ArtifactSchemaOutputResolver extends SchemaOutputResolver {
	
	@Override
	public Result createOutput(final String namespaceUri, final String suggestedFileName) throws IOException {
		final File file = new File(suggestedFileName);
		final StreamResult result = new StreamResult(file);
		result.setSystemId(file.toURI().toURL().toString());
		return result;
	}
	
}
