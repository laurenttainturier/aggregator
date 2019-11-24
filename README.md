# Setup

# Start of the service
Currently, the start of the service requires that a MQTT server is already started on the local machine, on the default port (1887).

To start the application, simply install the aggregator ('cd aggregator; mvn clean install'), compile and execute the aggregator-socket ('cd aggregator-socket; mvn clean verify exec:java').

The application can be configured by sending commands through sockets as expressed in the [architectural documentation](https://log680-20193-15.logti.etsmtl.ca/plugins/docman/download/6]).

