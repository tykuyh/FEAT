adb -s $1 shell dumpsys package $2 | grep userId= | cut -d '=' -f 2