adb -s $1 logcat -c # clear buffer
## select the application pid and ActivityManager , ActivityThread
adb -s $1 logcat -v long > $2
