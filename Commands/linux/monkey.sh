adb -s $1 shell monkey -p $2 -s 100 --throttle 1000 -v -v -v 3600    > $3
