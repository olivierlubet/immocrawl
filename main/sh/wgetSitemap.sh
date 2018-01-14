#!/bin/bash
rdom () { local IFS=\> ; read -d \< E C ;}

while rdom; do
    if [[ $ENTITY = "loc" ]] ; then
        echo $CONTENT;
    fi
done 