#!/bin/bash
set -e

echo "Make installer fow windows"

# First make link to latest version dir (this is what this setup-script wants later)
LATEST_WIN_PATH=$(ls -d $PWD/target/windows-version-*)
ln -s -f -T $LATEST_WIN_PATH $PWD/target/windows-latest


#---------------------------------
#
# First Create exe with launch4j
#
#---------------------------------
$PWD/launch4j/launch4j $PWD/launch4j.config.xml


#----------------------------------
#
# Second Create Windows installer with Inno Setup
#
#----------------------------------
INNO_SETUP_PROGRAM="$HOME/.wine/drive_c/Program Files/Inno Setup 5/iscc"

wine "$INNO_SETUP_PROGRAM" "innosetupfil_for_Aliview_win.iss"

# move static files
rsync -av aliview-windows/* target/windows-latest/

# make sure all files have right permissions
chmod 755 -R target

# move non installer files fo other dir
mkdir target/windows-latest/without_installer_version
mv target/windows-latest/AliView.exe target/windows-latest/without_installer_version
mv target/windows-latest/aliview.jar target/windows-latest/without_installer_version

# and windows install instr to package-dir
rsync -av htaccess-files/windows-install-dir/.htaccess target/windows-version*

