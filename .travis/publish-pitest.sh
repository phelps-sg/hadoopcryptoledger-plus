#!/bin/bash
#
# Copyright 2016 ZuInnoTe (Jörn Franke) <zuinnote@gmail.com>
#   <p>
#   Licensed under the Apache License, Version 2.0 (the "License");
#   you may not use this file except in compliance with the License.
#   You may obtain a copy of the License at
#   <p>
#   http://www.apache.org/licenses/LICENSE-2.0
#   <p>
#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.
#

if [ "$TRAVIS_REPO_SLUG" == "ZuInnoTe/hadoopcryptoledger" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then

echo -e "Publishing Pitest mutation testing results...\n"

# copy to home
mkdir -p $HOME/inputformat/pitest
cp -R inputformat/build/reports/pitest $HOME/inputformat/pitest
mkdir -p $HOME/hiveserde/pitest
cp -R hiveserde/build/reports/pitest $HOME/hiveserde/pitest
mkdir -p $HOME/hiveudf/pitest
cp -R hiveudf/build/reports/pitest $HOME/hiveudf/pitest
mkdir -p $HOME/flinkdatasource/pitest
cp -R flinkdatasource/build/reports/pitest $HOME/flinkdatasource/pitest

# Get to the Travis build directory, configure git and clone the repo
cd $HOME
git config --global user.email "travis@travis-ci.org"
git config --global user.name "travis-ci"
git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/ZuInnoTe/hadoopcryptoledger gh-pages > /dev/null

# Commit and Push the Changes
cd gh-pages
git rm -rf ./pitest/inputformat
mkdir -p ./pitest/inputformat
cp -Rf $HOME/inputformat/pitest ./pitest/inputformat
git rm -rf ./pitest/hiveserde
mkdir -p ./pitest/hiveserde
cp -Rf $HOME/pitest/dependencycheck ./pitest/hiveserde
git rm -rf ./pitest/hiveudf
mkdir -p ./pitest/hiveudf
cp -Rf $HOME/hiveudf/pitest ./pitest/hiveudf
git rm -rf ./pitest/flinkdatasource
mkdir -p ./pitest/flinkdatasource
cp -Rf $HOME/flinkdatasource/pitest ./pitest/flinkdatasource
git add -f .
git commit -m "Lastest test mutation testing on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
git push -fq origin gh-pages > /dev/null

fi
