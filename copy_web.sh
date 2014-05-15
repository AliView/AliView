#!/bin/sh

# copy webpage to server
rsync -av --update -e 'ssh -i /home/anders/.ssh/id_rsa  -p 64535' /home/anders/maven/AliView/web/* anders@ormbunkar.se:/srv/www/htdocs/aliview/

rsync -av --update -e 'ssh -i /home/anders/.ssh/id_rsa  -p 64535' anders@ormbunkar.se:/srv/www/htdocs/aliview/index.html /home/anders/maven/AliView/web/


