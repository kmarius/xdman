#!/bin/bash

if [ $EUID -ne 0 ]
then
	gksu $0 &
	exit 1
fi

pipe=/tmp/xdmpipe

trap "rm -f $pipe" EXIT

if [[ ! -p $pipe ]]
then
	mkfifo $pipe
	chmod 777 $pipe
fi

while true
do
	if read line <$pipe
	then
		if [[ "$line" == 'quit' ]]
		then
			echo "exiting..."			
			break
		fi
		if [[ "$line" == 'shutdown' ]]
		then
			shutdown -h now
		fi
	fi
done
