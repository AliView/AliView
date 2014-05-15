# make self installer
makeself . ../aliview.install.run "Installer for AliView" ./install.sh 

# make standard archive
tar -czvf ../aliview.tgz *
