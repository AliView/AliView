#!/bin/sh

# first copy sh-bin
install -m755 aliview /usr/bin/aliview
if [ $? -ne 0 ]
then
    echo "!!! could not install files, need to be sudo? e.g. sudo ./aliview.install.run"
    exit 1
fi

# make dir (install automatically checks if needed)
install -d -m755 /usr/share/aliview/

install -v -m755 aliview.jar /usr/share/aliview/
install -v -m755 aliicon_128x128.png /usr/share/aliview/
install -v -m755 README.txt /usr/share/aliview/
install -v -m755 install.sh /usr/share/aliview/

# and program launcher
if [ -d ~/.local/share/applications ]; then
  install -v -m755 AliView.desktop ~/.local/share/applications/
else 
  if [ -d /usr/share/applications ]; then
    install -v -m755 AliView.desktop /usr/share/applications/
  fi
fi
