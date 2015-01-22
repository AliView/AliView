#!/bin/sh

# first copy sh-bin
cp -v aliview /usr/bin/aliview
if [ $? -ne 0 ]
then
    echo "!!! could not copy files, need to be sudo? e.g. sudo ./aliview.install.run"
    exit 1
fi

# make dir if needed
if [ ! -d /usr/share/aliview ]; then
  mkdir -v /usr/share/aliview/
fi

# archive etc.

cp -v aliview.jar /usr/share/aliview/
cp -v aliicon_128x128.png /usr/share/aliview/
cp -v README.txt /usr/share/aliview/
cp -v install.sh /usr/share/aliview/

# and program launcher
if [ -d ~/.local/share/applications ]; then
  cp -v AliView.desktop ~/.local/share/applications/
else 
  if [ -d /usr/share/applications ]; then
    cp -v AliView.desktop /usr/share/applications/
  fi
fi


