#!/usr/bin/env ruby
#encoding=utf-8
PROJECT="mx_integral"
system "rm -rf /home/ewhine/deploy/#{PROJECT}/old"
system "mkdir /home/ewhine/deploy/#{PROJECT}/old"
system "mv /home/ewhine/deploy/#{PROJECT}/*.jar /home/ewhine/deploy/#{PROJECT}/old/."
system "cp -r /home/ewhine/ewhine_pkg/#{PROJECT}/*.jar /home/ewhine/deploy/#{PROJECT}/."
system "cp -r /home/ewhine/ewhine_pkg/#{PROJECT}/bin/#{PROJECT} /home/ewhine/deploy/#{PROJECT}/bin/#{PROJECT}"
system "cp -r /home/ewhine/ewhine_pkg/#{PROJECT}/version.txt /home/ewhine/deploy/#{PROJECT}/version.txt"
system "rm -rf /home/ewhine/ewhine_pkg"
system "/etc/init.d/#{PROJECT} stop"
system "/etc/init.d/#{PROJECT} update"
system "/etc/init.d/#{PROJECT} start"
puts "complete"

