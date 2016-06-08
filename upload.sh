#!/usr/bin/env bash

mkdir $HOME/daily/

cp -R widget/build/outputs/aar/* $HOME/daily/


# go to home/daily and setup git
cd $HOME/daily/

git config --global user.email "sarath.dr@gmail.com"
git config --global user.name "sarathdr"

# add, commit and push files
git init
git remote add origin https://github.com/sarathdr/sarathdr.github.io.git
git add -f .
git commit -m "Travis build $TRAVIS_BUILD_NUMBER pushed in daily channel"
git push -fq origin master > /dev/null



