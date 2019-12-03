# Setup

# Start of the service
Currently, the start of the service requires that a MQTT server is already started on the local machine, on the default port (1887).

To start the application, simply install the aggregator ('cd aggregator; mvn clean install'), compile and execute the aggregator-socket ('cd aggregator-socket; mvn clean verify exec:java').

The application can be configured by sending commands through sockets as expressed in the [architectural documentation](https://log680-20193-15.logti.etsmtl.ca/plugins/docman/download/6]).

# OPS : Telemetry of client use and automatic updates
To see a set of available aggregator, use : `python3 ops.py list  `  
You can only start to aggregate data on already known aggregator. To do so, use : `python3 ops.py subscribe {id}`  
You can stop to aggregate data on a node with : `python3 ops.py unsubscribe {id}`  
To update the list of aggregator based on the telemetry measure : `python3 ops.py update`