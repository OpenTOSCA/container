#!/bin/sh
java -Dosgi.compatibility.bootdelegation=true -XX:MaxPermSize=512m -jar lib/org.opentosca.targetplatform.container/Equinox/org.eclipse.osgi_3.7.2.v20120110-1415.jar -configuration config