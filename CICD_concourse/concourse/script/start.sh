#!/bin/sh

echo "Hello-TD!"

pwd
cd repo/concourse/script

java -jar TD-test.jar path.xml
