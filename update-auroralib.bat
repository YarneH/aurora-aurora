@echo off
Rem Copy the auroralib folder to '../auroralib'
Rem You have to set up the git repository in this folder!

if NOT exist ..\auroralib (
    echo Cloning...

    cd ..
    git clone https://github.ugent.be/Aurora/auroralib.git

    cd aurora
    echo Please rerun the command
) else (
    xcopy /S auroralib ..\auroralib

    cd ..\auroralib\


    git status | find /i "fatal: not a git repository (or any of the parent directories): .git"

    if errorlevel 0 (
        echo here
        git add --all
        git commit
        git push
    ) else (
        echo The folder was not a git repository.
        echo Cloning...

        cd ..
        rmdir /s /q auroralib\
        git clone https://github.ugent.be/Aurora/auroralib.git

        cd aurora
        echo Please rerun the command
    )
    
)