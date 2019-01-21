# Recent Swipo
An Android app to mimic the stock ZUI swipe left/right (navigate in app history) behaviour for Custom ROMs with android **PIE**

## How to Use
* Flash the newest zip from releases
* Open settings and set swipe actions to activity
`Recent Swipo > in.abhi9.recentswipo.SwipeLeftActivity/SwipeRightActivity` accordingly.
  * In case these options are missing for you (like left swipe on aex) you can do the following:
  * Use a root explorer like fx explorer, to manually add the lines "key 249 MENU" (right swipe) and "key 254 SEARCH" (left swipe) to /vendor/usr/keylayout/fpc1020tp.kl, if a line for the number does not already exist. You can also use different actions, if you want.
  * Then download a key remapping app from the playstore and map these keys to the swipe left and swipe right activity. I use the app https://play.google.com/store/apps/details?id=com.irishin.buttonsremapper, because it can automatically detect the pressed button and allows two remaps in the free version.
* Reboot the phone and Enjoy.

## Usage Tip
 * This works well with the following other actions:
 * Tap home: Back
 * Tap home long: recents menu
 * Press home: home
 * Press home long: close app / menu / ...


## Tested Device
* I only tested this on AEX 6.2 (Android 9) on the z2 pro, as this is what I use.

## Found a Bug?
Report a github issue and don't forget to attach a logcat.

## Changelog
* v2.0
  * fix for pie
  * new animation
  * loop option
  * icon
* v0.1
  * Initial release


## Credit
- Main app: inabhi9
