#!/usr/bin/env ruby
#encoding=utf-8
system "rm -rf /home/ewhine/deploy/mx_integral/old"
system "mkdir /home/ewhine/deploy/mx_integral/old"
system "mv /home/ewhine/deploy/mx_integral/integral-*.jar /home/ewhine/deploy/mx_integral/old/."
system "cp -r /home/ewhine/ewhine_pkg/mx_integral/integral-*.jar /home/ewhine/deploy/mx_integral/."
system "cp -r /home/ewhine/ewhine_pkg/mx_integral/bin/mx_integral /home/ewhine/deploy/mx_integral/bin/mx_integral"
system "rm -rf /home/ewhine/ewhine_pkg"
system 'nohup bash -l -c "sleep 5 && /etc/init.d/mx_integral restart" > /dev/null  2>&1  &'
puts "complete"

