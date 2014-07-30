#!/bin/sh

# adding
git add .
read -p "Press any key..."
# removing
git add -u
read -p "Press any key..."
# commit local
git commit -m "version 1.11"
read -p "Press any key..."
# commit server
git push AliView master
read -p "Press any key..."
