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

echo -e "Publishing javadoc...\n"

# copy to home
mkdir -p $HOME/inputformat/javadoc-latest
cp -R inputformat/build/docs/javadoc $HOME/inputformat/javadoc-latest
mkdir -p $HOME/hiveserde/javadoc-latest
cp -R hiveserde/build/docs/javadoc $HOME/hiveserde/javadoc-latest
mkdir -p $HOME/hiveudf/javadoc-latest
cp -R hiveudf/build/docs/javadoc $HOME/hiveudf/javadoc-latest
mkdir -p $HOME/flinkdatasource/javadoc-latest
cp -R flinkdatasource/build/docs/javadoc $HOME/flinkdatasource/javadoc-latest

# Get to the Travis build directory, configure git and clone the repo
cd $HOME
git config --global user.email "travis@travis-ci.org"
git config --global user.name "travis-ci"
git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/ZuInnoTe/hadoopcryptoledger gh-pages > /dev/null


# Commit and Push the Changes
cd gh-pages
git rm -rf ./javadoc/inputformat
mkdir -p ./javadoc/inputformat
cp -Rf $HOME/inputformat/javadoc-latest ./javadoc/inputformat
git rm -rf ./javadoc/hiveserde
mkdir -p ./javadoc/hiveserde
cp -Rf $HOME/hiveserde/javadoc-latest ./javadoc/hiveserde
git rm -rf ./javadoc/hiveudf
mkdir -p ./javadoc/hiveudf
cp -Rf $HOME/hiveudf/javadoc-latest ./javadoc/hiveudf
mkdir -p ./javadoc/flinkdatasource
cp -Rf $HOME/flinkdatasource/javadoc-latest ./javadoc/flinkdatasource
git add -f .
git commit -m "Lastest javadoc on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
git push -fq origin gh-pages > /dev/null

fi
