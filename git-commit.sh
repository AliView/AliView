#!/bin/bash

# adding
git add .
read -p "Press any key to continue... " -n1 -s
# removing
git add -u
read -p "Press any key to continue... " -n1 -s
# commit local
git commit -m "version 1.11"
read -p "Press any key to continue... " -n1 -s
# commit server
git push AliView master
read -p "Press any key to continue... " -n1 -s
