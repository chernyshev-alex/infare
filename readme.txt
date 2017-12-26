
How to run

1. install mongo 3.6 

docker pull mongo
docker run -d -p 27017:27017 mongo

2. start infare converter service 
java -jar currencyservice-0.1.0.jar

3. Config 
    \etl\src\main\resources\application.properties 
        Has mongo db URL, infare converter URL       

    \etl\src\main\resources\application
        Akka cluster config.

        nr-of-instances =4
        max-nr-of-instances-per-node = 2
  
    Will be created 1 master for job disribution and nr-of-instances workers as well.
    But no more that max-nr-of-instances-per-node workers per cluster node.

    In this demo, I start 3 node of the cluster in the save JVM, but it will work in
    the distributed environment as well. (small changes in the config)
    More physical machines can be added to the cluster. 
    Adjust these parameters and worker.min-nr-of-members.
   
    Each worker  will request job from master and execute it owns spring batch instance.

4. cd etl; mvn clean compile exec:java

5. Ctrl+C to exit after end of the process

Performance metrics

You should see  lines :

INFO [cluster-akka.actor.default-dispatcher-17] BatchConfiguration - records handled 1000
INFO [cluster-akka.actor.default-dispatcher-4] BatchConfiguration - records handled 4000
INFO [cluster-akka.actor.default-dispatcher-17] BatchConfiguration - records handled 2000
INFO [cluster-akka.actor.default-dispatcher-4] BatchConfiguration - records handled 5000
INFO [cluster-akka.actor.default-dispatcher-4] BatchConfiguration - job COMPLETED rate is 3681 msg/sec
INFO [cluster-akka.actor.default-dispatcher-4] SimpleJobLauncher - Job: [FlowJob: [name=exportToMongoJob]] complet

Every master reports about rate with tag  : job COMPLETED rate ..

On Intel i5 4 core 2.4Gz ~ 2000-4000 msg/sec per worker

Notes   
   Task 1 , 2  - done
   Task 3  - no
   Task 4 (data mining) - almost all data mining queries could be executed on mongo 3.6


