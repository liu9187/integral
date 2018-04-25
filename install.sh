#!/usr/bin/env bash
sudo cp -r ./mx_integral/bin/mx_integral /etc/init.d/.
sudo chmod +x /etc/init.d/mx_integral
mv mx_integral /home/ewhine/deploy/.
sudo chmod +x /home/ewhine/deploy/mx_integral/bin/mx_integral
