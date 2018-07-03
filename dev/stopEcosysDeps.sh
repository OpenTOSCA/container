#!/bin/bash
docker stop dev_iaengine
docker rm dev_iaengine
docker stop dev_planengine
docker rm dev_planengine
# For BPS, don't forget to change org.opentosca.container.product/config.ini accordingly
# docker run -p 9763:9763 -it opentosca/bps -name dev_planengine
docker stop dev_ui
docker rm dev_ui
