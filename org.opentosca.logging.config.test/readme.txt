This OSGi fragment includes:

Logback configuration XML with logging strategy for TEST USE (logback-test.xml),
see: https://redmine.flupp.de/projects/stupro2011-12/wiki/Logging

For doing logging with this config it must be activated/set to start in run configuration!

If productivity config fragment AND test config fragment are activated in run configuration, 
test config will be used.

Fragment host is ch.qos.logback.classic.

To resolve dependencies for logging (includes fragment host) TargetPlatformDefinition must 
be set as active target platform.

Non-plugin-projects are not supported!