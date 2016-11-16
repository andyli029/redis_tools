Java redislap tool, support jedis currently
============================================
   
Getting started. 

1. package this project with maven

    mvn clean package

2. run the benchmark

    cd target/
    java -jar redislap-1.0-SNAPSHOT-jar-with-dependencies.jar
        -h 10.150.110.175 -p 8005 -t 2 -r 10000 -vl 15 -c 100 -op set -opTimeout 500 -enableCluster false 	  	    -passwd 111111 -kp lizhiang

3. help

    java -jar redislap-1.0-SNAPSHOT-jar-with-dependencies.jar -help
    
	 -c,--connection <arg>                       Specifies connection pool
	                                             size.
	 -enableCluster,--enable Cluster <arg>       Specifies true or false.
	 -enablestentinel,--enable stentinel <arg>   Specifies true or false.
	 -h,--host <arg>                             Specifies a host to contact
	                                             over the network.
	 -help,--help                                show help.
	 -kp,--key prefix <arg>                      prefix of the key
	 -op,--operation <arg>                       Specifies set or get
	                                             operation.
	 -opTimeout,--operation timeout <arg>        Specifies set or get
	                                             operation timeout.
	 -p,--port <arg>                             Specifies a port number to
	                                             contact.
	 -passwd,--passwd <arg>                      passwd for master
	 -r,--repeat <arg>                           Specifies repeat count per
	                                             thread.
	 -sentinels,--sentinels <arg>                sentinels ip:host,ip:host
	 -t,--thread <arg>                           Specifies thread count to
	                                             run.
	 -vl,--value length <arg> 