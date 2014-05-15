#!/bin/sh

#prepare install-file for linux
rsync -av /home/anders/maven/AliView/deployed/linux_package_files/* /home/anders/maven/AliView/target/linux-version*/aliview/

cd /home/anders/maven/AliView/target/linux-version*/aliview/

makeself . ../aliview.install.run "Installer for AliView" ./install.sh 
makeself . ../aliview.install.run "Installer for AliView" ./install.sh 

# make standard archive for linux
tar -czvf ../aliview.tgz *

# create setup for windows
# make link to latest version dir (this is what setup-script wants)
LATEST_WIN_PATH=$(ls -d /home/anders/maven/AliView/target/windows-version-*)
ln -s -f -T $LATEST_WIN_PATH /home/anders/maven/AliView/target/windows-latest

wine "/home/anders/.wine/drive_c/Program Files/Inno Setup 5/iscc" "Z:\home\anders\maven\AliView\innosetupfil_for_Aliview_win.iss"

# copy one version to local
rsync -av -e 'ssh -i /home/anders/.ssh/id_rsa  -p 64535' /home/anders/maven/AliView/target/windows-version* /home/anders/maven/AliView/deployed/

rsync -av -e 'ssh -i /home/anders/.ssh/id_rsa  -p 64535' /home/anders/maven/AliView/target/linux-version* /home/anders/maven/AliView/deployed/

rsync -av -e 'ssh -i /home/anders/.ssh/id_rsa  -p 64535' /home/anders/maven/AliView/target/AliView*-app.zip /home/anders/maven/AliView/deployed/

# copy one version to local
rsync -av -e 'ssh -i /home/anders/.ssh/id_rsa  -p 64535' /home/anders/maven/AliView/target/windows-version* /home/anders/maven/AliView/deployed/

rsync -av -e 'ssh -i /home/anders/.ssh/id_rsa  -p 64535' /home/anders/maven/AliView/target/linux-version* /home/anders/maven/AliView/deployed/

rsync -av -e 'ssh -i /home/anders/.ssh/id_rsa  -p 64535' /home/anders/maven/AliView/target/AliView*-app.zip /home/anders/maven/AliView/deployed/

# and one version to server
rsync -av -e 'ssh -i /home/anders/.ssh/id_rsa  -p 64535' /home/anders/maven/AliView/target/windows-version* anders@ormbunkar.se:/srv/www/htdocs/aliview/downloads/windows

rsync -av -e 'ssh -i /home/anders/.ssh/id_rsa  -p 64535' /home/anders/maven/AliView/target/linux-version* anders@ormbunkar.se:/srv/www/htdocs/aliview/downloads/linux

rsync -av -e 'ssh -i /home/anders/.ssh/id_rsa  -p 64535' /home/anders/maven/AliView/target/AliView*-app.zip anders@ormbunkar.se:/srv/www/htdocs/aliview/downloads/mac/

# copy the web

sh /home/anders/maven/AliView/copy_web.sh




