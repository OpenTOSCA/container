#!/bin/bash

command="cd www/container/\n"

# change into dir and delete old snapshots
command="${command}mkdir $TRAVIS_BRANCH\ncd $TRAVIS_BRANCH\nrm *.war\n"

command="${command}mput org.opentosca.container.product/target/products/*.zip\n"
command="${command}mput org.opentosca.planbuilder.service.product/target/products/*.zip\n"
command="${command}exit\n"

# now $command is complete

echo -e "$command" | sftp -P 443 builds_opentosca_org@builds.opentosca.org
