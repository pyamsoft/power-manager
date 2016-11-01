# Power Manager

Power Manager Android application by pyamsoft

[![Get it on Google Play](art/google-play-badge.png)][1]

## What is Power Manager

Power Manager smartly manages the WiFi, Cellular Data*, Bluetooth, and Sync  
on your Android device. When the device's screen is off and the device is  
idle, Power Manager will automatically turn off the interfaces that you ask  
it to manage. The application can be configured to start up when your device  
starts, and can even be configured to periodically turn your device on to  
check for things like messages and emails. Power Manager does not require  
root to run any of its functions to save your device battery.

Power Manager is also able to force a quicker Doze mode on devices running  
Android Marshmallow. To enable this functionality, manual action must be  
taken by the user to grant Power Manager the ability to set the current  
Doze mode state. To learn more, see the Settings page in the application. 

Power Manager is also able of managing Airplane Mode automatically on devices  
which have root.

* Power Manager can only manage Cellular Data on Android Lollipop and upwards  
if you have access to root. Power Manager will make a call to the `su` binary  
when it attempts to toggle Cellular Data on these newer Android platforms.  
This is a limitation of the Android operating system, and there is unfortunately  
nothing that can be done from a third party application to change this.

For functionality of Power Manager which requires root, be sure to toggle the  
`Enable Superuser` option On in the Settings module.

## Permissions

Power Manager uses the requested permissions in order to control and manage  
the various interfaces on your phone or tablet. Rest assured that there is  
absolutely NO third party storage or usage of user data. Power Manager does  
not collect nor will it ever ask for any of your personal information. The  
cache used by Power Manager is used to store user preferences, and nothing  
more.

## Development

Power Manager is developed in the Open on GitHub at:  
```
https://github.com/pyamsoft/power-manager
```
If you know a few things about Android programming and are wanting to help  
out with development you can do so by creating issue tickets to squash bugs,  
and propose feature requests for future inclusion.

Power Manager has a publicly view able Trello board available at:  
```
https://trello.com/b/44nuGT7D
```

# Issues or Questions

Please post any issues with the code in the Issues section on GitHub. Pull  
Requests will be accepted on GitHub only after extensive reading and as long  
as the request goes in line with the design of the application. Pull Requests  
will only be accepted for new features of the application, for general  
purpose bug fixes, creating an issue is simply faster.

[1]: https://play.google.com/store/apps/details?id=com.pyamsoft.powermanager
