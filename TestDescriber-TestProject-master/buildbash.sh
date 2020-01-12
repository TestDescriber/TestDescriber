#!/bin/bash

if [ ! -d "SME19_fork" ]
then
  	git clone https://github.com/flamegiraffe/SME19_fork.git
	echo 'getting a fork!'
else
	git -C SME19_fork fetch --all
	git -C SME19_fork reset --hard origin/master
	echo 'already got a fork'
fi