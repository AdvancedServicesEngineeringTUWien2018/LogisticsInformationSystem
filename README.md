This is a prototype for a university project in Advanced Services Engineering. It is in the field of transport and logistics.

The idea is to gather GPS data from vehicles that visit customers on their daily tour, and transform and enrich this data into information to reason about how long it is expected that a deliverer needs to wait at the customer location at a specific time and day. The problem of long waiting times exists, amongst others because there are many suppliers that deliver to the same customers and nobody of them knows when the other is arriving.

The mini project is split into 5 modules:
- Common
- Simulation
- Edge Stream Processing
- Cloud Stream Processing
- Simple Waiting Time Predictor

Homework:
1. Add a more complex machine learning based waiting time predictor based on the data stored in BigQuery!
2. Make a gateway microservice for the predictor services and let the client choose which service they want to use
3. Migrate the waiting time predictors as Microservices on Kubernetes and let them scale automatically

### Common
The common module provides common classes like models, events, calculations and service request/response that might be interesting to multiple modules or components.

### Simulation
The simulation module tries to mimic the real mechanisms of a vehicle queue at a customer location, all waiting to get a slot to unload their goods and then get the goods checked by the goods takeover person. The simulation takes quality of simulation data serious, in a sense, that a machine learning predictor can detect patterns and more accurately predict the expected waiting time. It takes Austrian holidays from 2015 to 2018 into account and supports different customer demand behaviours like varying demand on different weekdays, on number of week (e.g. Christmas time?), begin of month (e.g. people got their salary and burn it at the next party?), before closed days (e.g. food?), end of month (e.g. some other good reason?) that would affect the waiting time on different days.

### Edge Stream Processing
The edge stream processing module contains a stream processing application written with the edge device stream processing framework Apache Edgent, which is currently in the Apache Incubator phase. An instance of such an `EdgentEdgeDevice` basically receives GPS data from a sensor (in the real world: from the vehicle driver's Smartphone), detects Arrivals and Departures at a vehicles destinations based on their daily tour, and sends only the visit information further to the cloud (in our case, to a Kafka instance). With this, the privacy of the vehicle driver can be increased, the network capacity can be saved and network connection problems can be handled without much notice.

### Cloud Stream Processing
The cloud stream processing module is implemented with Apache Flink. It reads the data (visit information of each vehicle visit at a tour destination) from Apache Kafka, and handles possibly delayed or out-of-order events and aggregate them to hourly average waiting time at each customer location. This `AvgVisitDuration` data stream is then ingested into Google BigQuery.


### Simple Waiting Time Predictor
The simple waiting time predictor module is a RESTful web service which queries Google BigQuery and makes a simple average over the `AvgVisitDuration`s for every customer location at every weekday and arriving hour. It only runs locally.


How to setup
------------

### Pre-requesites
- `Google Cloud SDK` installed (see [Google Cloud SDK](https://cloud.google.com/sdk/))
- `kubectl` installed (see [Kubernetes Documentation](https://kubernetes.io/docs/tasks/tools/install-kubectl/))
- `Java Development Kit (JDK) 8` (tested with Oracle Java)

### Build project
Execute in the bash:
- `cd <project-directory>`
- `./mvnw clean install`

### Create Kubernetes cluster
- Go to [Google Cloud Platform --> Kubernetes Engine](https://console.cloud.google.com/kubernetes) and fill the web form to create a Kubernetes cluster. Chose a region that is close to you. The default image resources (`3 nodes` with each `3.75 GB RAM` and `1 CPU`) should be sufficient.

### Configure local Kubernetes tool
Execute in the bash:
```
gcloud container clusters get-credentials <CLUSTER_ID> --zone <ZONE> --project <PROJECT_ID>
```
the exact command can be copied when selecting the Kubernetes cluster and press the `Connect` or `Verbinden` button.

Now `kubectl` should be configured to work with your Kubernetes cluster on the Google Cloud Platform. 


### Deploy Kafka on Kubernetes

``` bash
cd kubernetes/kafka
kubectl create -f zookeeper-services.yaml
kubectl create -f zookeeper-cluster.yaml
kubectl create -f kafka-service.yaml
```

Extract external Kafka loadbalancer IP with `kubectl get services` and adapt the `KAFKA_ADVERTISED_HOST_NAME` property in `kafka-cluster.yaml` accordingly

``` bash
kubectl create -f kafka-cluster.yaml
```

**NOTICE**: By providing this external IP address, anybody can connect and misuse your paid cluster resources. This is just for demonstration purpose.

### Deploy Flink on Kubernetes

```
cd kubernetes/flink
kubectl create -f jobmanager-loadBalancer.yaml
kubectl create -f jobmanager-service.yaml
```

Extract external Flink loadbalancer IP with `kubectl get services` and adapt the `JOB_MANAGER_RPC_ADDRESS` property in `jobmanager-deployment.yaml` and `taskmanager-deployment.yaml` accordingly.

```
kubectl create -f jobmanager-deployment.yaml
kubectl create -f taskmanager-deployment.yaml
```

**NOTICE**: By providing this external IP address, anybody can connect and misuse your paid cluster resources. This is just for demonstration purpose.


### Setup BigQuery

#### Create credentials and grant access
Go to the [Google Cloud Platform Service Accounts page](https://console.cloud.google.com/iam-admin/serviceaccounts) and create a new service account with the `bigquery.dataEditor` role and download the private key in JSON format. Put it on a save place (don't loose it, don't leak it!) and set the environment variable `GOOGLE_CLOUD_CREDENTIALS` to the absolute path of the JSON file, i.e. `export GOOGLE_CLOUD_CREDENTIALS=<PATH_TO_JSON>` or configure it in your IDE if you run your application from there.

#### Create dataset for your country
Go to [BigQuery](https://bigquery.cloud.google.com/) and create a new dataset near to the zone your Kubernetes cluster runs. In our case, name it `LogisticsInformationSystem_AUT` in order to run the example out-of-the-box.

All tables that are created in a dataset are automatically located in the same datacenter.

#### Create `Holidays` table
- Press `create new table` button at the `LogisticsInformationSystem_AUT` dataset
- Choose `Create from source`
- Choose `File upload` and select the `data/holidays_AUT_2015-2018.csv` file
- Set `table name` to `Holidays`
- Add two fields:
  - `name` with type `STRING` and `REQUIRED`
  - `date` with type `DATE` and `REQUIRED`
- Press `Create Table`


#### Create empty `AvgVisitDurations` table
- Press `create new table` button at the `LogisticsInformationSystem_AUT` dataset
- Choose `Create empty table`
- Add fields (all with mode `REQUIRED`):
  - `locationId` of type `INTEGER`
  - `locationName` of type `STRING`
  - `arrivalHourTimestamp` of type `DATETIME`
  - `arrivalHour` of type `INTEGER`
  - `weekday` of type `INTEGER`
  - `arrivalHourLocalString` of type `STRING`
  - `avgVisitDuration` of type `INTEGER`
- Chose `Partitioning Type` of `None`
  - Reason: Performance-wise this would be reasonable. However, it restricts the value range the chosen partitioning field of new data times are allowed to have. In our case, it would not be possible to ingest data from simulating years 2015 to 2017.


How to run
----------

First we need to deploy our platform services `Kafka` and `Flink` and setup `BigQuery` (see `How to setup` section above).

Retrieve the external IP address of `Kafka` and `Flink` via `kubectl get services`. They will be needed multiple times.

### Fill BigQuery with simulation data

With `<EXTERNAL_FLINK_IP>:8081` you can access the Flink GUI. Go to `Submit new Job` and upload the streaming job from `cloud-stream-processing/target/cloudStreamProcessingJob.jar`. Submit it with program arguments `<EXTERNAL_KAFKA_IP>:9092`.


Start the simulation with 
```
java -jar simulation/target/simulation-0.0.1-SNAPSHOT-jar-with-dependencies.jar <START_DATE> <END_DATE> <KAFKA_EXTERNAL_IP>:9092
```

Start date and end date must be between `2015-01-01` and `2017-12-31` and the date format is `yyyy-MM-dd`.


### Predict waiting time

```
cd batch-waiting-time-predictor
./mvnw clean spring-boot:run
```

Test it by using a REST client (like the [Advanced REST Client](https://chrome.google.com/webstore/detail/advanced-rest-client/hgmloofddffdnphfgcellkdfbfbjeloo?hl=de) extension of [Google Chrome](https://www.google.com/chrome/)):
```
GET http://localhost:8080/predict?locationId=10&date=2018-06-22&arrivalHour=11
```
All request params are required:
- `locationId`: int (look up location ID in [BigQuery](https://bigquery.cloud.google.com/) or in the [source code](simulation/src/main/kotlin/micc/ase/logistics/simulation/Simulation.kt))
- `date`: format `yyyy-MM-dd`, 2015-01-01..2017-12-31
- `arrivalHour`: 0..23


How to Stop / clean up
----------------------

Just `Ctrl+C` the simulation or the waiting time predictor.


A Kubernetes resource (`Kafka`, `Flink`) can be deleted with the following command:
```
kubectl delete -f <CONFIG.yaml>
```


To save even more Google Cloud Platform resources, delete the [Kubernetes cluster](https://console.cloud.google.com/kubernetes/).



Contributors
------------
- Michael Höller

