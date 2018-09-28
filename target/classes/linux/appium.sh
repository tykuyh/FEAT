appium  -a 127.0.0.1 -p $1 -U $2 --log-level info:error --log $3  --session-override -bp $5 | tee $4 
#appium  -a 127.0.0.1 -p $1 -U $2 --log-timestamp --local-timezone --log-no-colors --log-level info:error --log $3  --session-override -bp $5 | tee $4
#perl appium  -a 127.0.0.1 -p $1 -U $2 --log-level info:error --log $3  --session-override -bp $5 | tee $4 