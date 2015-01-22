#Unpack archive and run install.sh

#install.sh will copy files to:
/usr/bin
/usr/share

#run program with command:
aliview [alignment-file]

#aliview is a shell script that runs the aliview.jar with command:
java -Xmx1024M -Xms512M -jar $DIRECTORY_OF_PROGRAM/aliview.jar $@

#the self extracting installer were created with following command:
makeself aliview-linux/ aliview.install.run "Installer for AliView" ./install.sh 
