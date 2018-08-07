#!/usr/bin/env bash
PROJECT=mx_integral
sudo cp -r ./$PROJECT/bin/$PROJECT /etc/init.d/.
sudo chmod +x /etc/init.d/$PROJECT
mv $PROJECT /home/ewhine/deploy/.
sudo chmod +x /home/ewhine/deploy/$PROJECT/bin/$PROJECT
