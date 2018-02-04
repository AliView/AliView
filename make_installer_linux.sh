#!/bin/sh
set -e

echo "Make installer fow linux"

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

