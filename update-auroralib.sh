#!/bin/bash

if [ ! -d "../auroralib" ]; then
    echo Cloning...

    cd ..
    rm -rf auroralib\
    git clone https://github.ugent.be/Aurora/auroralib.git

    echo Please rerun the command
else
    cp -rf auroralib ..
    cd ../auroralib

    if ! git status | grep -q "fatal: not a git repository (or any of the parent directories): .git"; then
        git add --all
        git commit
        git push
    else
        echo The folder was not a git repository.
        echo Cloning...

        cd ..
        rm -rf auroralib\
        git clone https://github.ugent.be/Aurora/auroralib.git

        echo Please rerun the command
    fi
fi