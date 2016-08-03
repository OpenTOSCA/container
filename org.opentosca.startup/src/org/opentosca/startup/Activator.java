package org.opentosca.startup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    private static BundleContext context;

    static BundleContext getContext() {
	return context;
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext bundleContext) throws Exception {
	Activator.context = bundleContext;
	deleteOpenTOSCADir();
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext bundleContext) throws Exception {
	Activator.context = null;
    }

    private void deleteOpenTOSCADir(){

	Path path = Paths.get(System.getProperty("java.io.tmpdir") + File.separator + "openTOSCA");
	System.out.println("This is the first run, thus, delete the temporary directory: "+ path);
	try {
	    FileUtils.deleteDirectory(path.toFile());
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

}
