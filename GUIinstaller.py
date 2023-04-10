import os

# Install choco
os.system("@powershell -NoProfile -ExecutionPolicy unrestricted -Command \"iex ((new-object net.webclient).DownloadString('https://chocolatey.org/install.ps1'))\"")

# Install ffmpeg 5.1.2
os.system("choco install ffmpeg --version 5.1.2 -y")

# Install KinectXEFTools from GitHub
os.system("choco install git -y")
os.chdir("C:/Program Files/Git/bin/")
os.system("git clone https://github.com/Isaac-W/KinectXEFTools.git")
os.chdir("KinectXEFTools")
os.system("dotnet build")

# Install vlc
os.system("choco install vlc -y")

import subprocess

# Install chocolatey package manager
subprocess.run(['powershell', '-Command', '(Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope LocalMachine)'])
subprocess.run(['powershell', '-Command', r"iex ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))"])

# Install Java using chocolatey
subprocess.run(['choco', 'install', 'jdk8', '-y'])

# Add Java to system path
subprocess.run(['setx', 'PATH', r'%PATH%;C:\Program Files\Java\jdk1.8.0_281\bin'])

