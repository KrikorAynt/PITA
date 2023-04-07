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
