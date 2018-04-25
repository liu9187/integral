#!/usr/bin/env bash
source ~/.bash_profile
mvn  clean
mvn package -Dbuild.version=$1-$2
mv ./target/ewhine_pkg/ .
chmod +x build.rb
chmod +x install.sh
cp -r build.rb ./ewhine_pkg/.
cp -r install.sh ./ewhine_pkg/.
tar zcvf mx_integral-$1-$2.tar.gz ewhine_pkg
mv  mx_integral-$1-$2.tar.gz /home/ewhine/build/dist/docview/.
echo http://apps.dehuinet.com:83/docview/mx_integral-$1-$2.tar.gz
