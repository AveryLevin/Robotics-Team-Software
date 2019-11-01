@echo off

"C:\Users\Public\frc2019\tools\SmartDashboard.vbs"

cd "C:\Program Files (x86)\FRC Driver Station" 

start DriverStation.exe

"C:\Program Files (x86)\VideoLAN\VLC\vlc.exe" --network-caching 150 --clock-jitter 0 --clock-synchro 0 rtsp://admin@10.42.76.10/user=admin_password=tlJwpbo6_channel=1_stream=1.sdp

exit