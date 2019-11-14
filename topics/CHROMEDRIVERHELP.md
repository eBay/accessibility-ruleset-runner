# ChromeDriver Help

The appropriate version of <a href='http://chromedriver.chromium.org/'>ChromeDriver</a> needs to be downloaded individually.  The ChromeDriver version must be compatible with the Operating System (Mac/Unix/Windows) and the version of Chrome.

For example, when using version 73.0.3683.103 of Chrome, users may download the appopriate version of ChromeDriver (Mac/Unix/Windows) from <a href='https://chromedriver.storage.googleapis.com/index.html?path=73.0.3683.68/'>ChromeDriver 73.0.3683.68</a>.  Links for ChromeDriver downloads for all Chrome versions are listed <a href='http://chromedriver.storage.googleapis.com/index.html'>here</a>.

## Installing ChromeDriver on the System Path

<b>Note:</b> Git Bash is recommended for Windows users.

Open a terminal and navigate to the folder or directory containing <a href='http://chromedriver.chromium.org/'>ChromeDriver</a>.

Type the following to see the <a href='http://chromedriver.chromium.org/'>ChromeDriver</a> version:
```sh
chromedriver --version
```

Many of the examples provided will assume <a href='http://chromedriver.chromium.org/'>ChromeDriver</a> is placed within the bin folder of the home directory.  To see this exact location, type the following:
```sh
echo $HOME/bin
```

Make sure the "$HOME/bin" directory exists, it is on the system path and that <a href='http://chromedriver.chromium.org/'>ChromeDriver</a> is placed within the directory.

If setup correctly, the command to show the <a href='http://chromedriver.chromium.org/'>ChromeDriver</a> version will work from any directory.
