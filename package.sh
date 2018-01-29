#!/bin/sh
set -e

INNO_SETUP_PROGRAM="$HOME/.wine/drive_c/Program Files/Inno Setup 5/iscc"

#----------------------------------
#prepare install-file for LINUX
#----------------------------------

rsync -av aliview-linux/* target/linux-version*/aliview/

# make sure all files have right permissions
chmod 755 -R target

# move into dir
cd target/linux-version*/aliview/

makeself . ../aliview.install.run "Installer for AliView" ./install.sh 

# make standard archive for linux
tar -czvf ../aliview.tgz *

# move back
cd ../../../

# and linux install instr to package-dir
rsync -av target/linux-version*/aliview/install.readme.txt target/linux-version*
rsync -av htaccess-files/linux-install-dir/.htaccess target/linux-version*


#----------------------------------
#prepare install-file for WINDOWS
#----------------------------------

# make link to latest version dir (this is what setup-script wants)
LATEST_WIN_PATH=$(ls -d $PWD/target/windows-version-*)
ln -s -f -T $LATEST_WIN_PATH $PWD/target/windows-latest

wine $INNO_SETUP_PROGRAM "innosetupfil_for_Aliview_win.iss"

# move static files
rsync -av aliview-windows/* target/windows-latest/

# move non installer files fo other dir
mkdir target/windows-latest/without_installer_version
mv target/windows-latest/AliView.exe target/windows-latest/without_installer_version
mv target/windows-latest/aliview.jar target/windows-latest/without_installer_version

# and windows install instr to package-dir
rsync -av htaccess-files/windows-install-dir/.htaccess target/windows-version*

