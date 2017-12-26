
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
  
    Will be created 1 master for job disribution and 4 workers.
    2 workers per cluster node

    Each worker will get job from master and execute one with spring batch

4. cd etl; mvn clean compile exec:java

5. Ctrl+C to exit after end of the process

Performance metrics

Find lines for every node in the cluster in the log/akka.log : job COMPLETED rate ....

Intel i5 4 core 2.4Gz ~ 3000 msg/sec

Problems
  Port 25** is already in use - Kill previous java instance

Notes   
   Task 1 , 2  - done
   Task 3  - no
   Task 4 (data mining) - almost all data mining queries could be executed on mongo 3.6


