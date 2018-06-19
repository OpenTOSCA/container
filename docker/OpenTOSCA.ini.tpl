-startup
plugins/org.eclipse.equinox.launcher_1.3.201.v20161025-1711.jar
--launcher.library
plugins/org.eclipse.equinox.launcher.gtk.linux.x86_64_1.1.401.v20161122-1740
-consoleLog
-console
--launcher.secondThread
-vmargs
-Declipse.ignoreApp=true
-Dosgi.noShutdown=true
-Dorg.osgi.service.http.port=1337
-DREFRESH_BUNDLES=false
{{ .Env.CONTAINER_JAVA_OPTS }}
