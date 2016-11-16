#!/bin/bash
set -x

#java -jar target/redislap-1.0-SNAPSHOT-jar-with-dependencies.jar -h 10.127.91.91 -p 6379 -vl 10  -t 100 -r 10000  -c 100 -op get -opTimeout 5000  -kp 11 -enableCluster true 
java -jar target/redislap-1.0-SNAPSHOT-jar-with-dependencies.jar -kp ss -h 10.150.110.173 -p 6784  -vl 10  -t 100 -r 100000  -c 50 -op get -opTimeout 5000  -kp ss -enableCluster true
