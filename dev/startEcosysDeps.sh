#!/bin/bash
docker run -p 8090:8080 --name dev_iaengine -d -it opentosca/engine-ia 
docker run -p 9763:9763 --name dev_planengine -d -it opentosca/ode
# For BPS, don't forget to change org.opentosca.container.product/config.ini accordingly
# docker run -p 9763:9763 -it opentosca/bps -name dev_planengine
docker run -p 8088:8080 --name dev_ui -d -it opentosca/ui
