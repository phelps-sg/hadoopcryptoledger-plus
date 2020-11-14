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

echo -e "Publishing test results...\n"

# copy to home
mkdir -p $HOME/inputformat/tests-latest
cp -R inputformat/build/reports/tests/test $HOME/inputformat/tests-latest
mkdir -p $HOME/hiveserde/tests-latest
cp -R hiveserde/build/reports/tests/test $HOME/hiveserde/tests-latest
mkdir -p $HOME/hiveudf/tests-latest
cp -R hiveudf/build/reports/tests/test $HOME/hiveudf/tests-latest
mkdir -p $HOME/flinkdatasource/tests-latest
cp -R flinkdatasource/build/reports/tests/test $HOME/flinkdatasource/tests-latest

# Get to the Travis build directory, configure git and clone the repo
cd $HOME
git config --global user.email "travis@travis-ci.org"
git config --global user.name "travis-ci"
git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/ZuInnoTe/hadoopcryptoledger gh-pages > /dev/null

# Commit and Push the Changes
cd gh-pages
git rm -rf ./tests/inputformat
mkdir -p ./tests/inputformat
cp -Rf $HOME/inputformat/tests-latest ./tests/inputformat
git rm -rf ./tests/hiveserde
mkdir -p ./tests/hiveserde
cp -Rf $HOME/hiveserde/tests-latest ./tests/hiveserde
git rm -rf ./tests/hiveudf
mkdir -p ./tests/hiveudf
cp -Rf $HOME/hiveudf/tests-latest ./tests/hiveudf
git rm -rf ./tests/flinkdatasource
mkdir -p ./tests/flinkdatasource
cp -Rf $HOME/flinkdatasource/tests-latest ./tests/flinkdatasource
git add -f .
git commit -m "Lastest javadoc on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
git push -fq origin gh-pages > /dev/null

fi
