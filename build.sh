#!/usr/bin/env bash
PROJECT=mx_integral
source ~/.bash_profile
mvn  clean
mvn package -Dbuild.version=$1
mv ./target/ewhine_pkg .
chmod +x build.rb
chmod +x install.sh
cp -r build.rb ./ewhine_pkg/.
cp -r install.sh ./ewhine_pkg/.
cp -r version.txt ./ewhine_pkg/$PROJECT/.
tar zcvf $1.tar.gz ewhine_pkg
mv  $1.tar.gz /home/ewhine/build/dist/docview/.
echo http://apps.dehuinet.com:83/docview/$1.tar.gz

