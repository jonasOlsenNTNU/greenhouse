# Communication protocol

This document describes the protocol used for communication between the different nodes of the
distributed application.

## Terminology

* Sensor - a device which senses the environment and describes it with a value (an integer value in
  the context of this project). Examples: temperature sensor, humidity sensor.
* Actuator - a device which can influence the environment. Examples: a fan, a window opener/closer,
  door opener/closer, heater.
* Sensor and actuator node - a computer which has direct access to a set of sensors, a set of
  actuators and is connected to the Internet.
* Control-panel node - a device connected to the Internet which visualizes status of sensor and
  actuator nodes and sends control commands to them.
* Graphical User Interface (GUI) - A graphical interface where users of the system can interact with
  it.
* Message - A string of letters/symbols following a specific format used to enable transfer of information in a repeatable fashion. 

## The underlying transport protocol

All connections are using TCP. We made the decision to use TCP because we chose to design a stateful system. TCP is inherently stateful because the session is maintained while connected. The state of the system is dependant on reliable communication, which TCP also supports.
We chose to use port number 8585. It is easy to remember and is not assigned. https://en.wikipedia.org/wiki/List_of_TCP_and_UDP_port_numbers

## The architecture

The network consists of one server and any number of clients. 
The clients are the sensor actuator nodes and the control panels.
Clients inform the server about what type of client they are so that data can be routed to the correctly.

## The flow of information and events

#### Sensor Actuator Node
- When the state of a sensor actuator node changes a multicast-message is sent to the server. The end receiver for this message is all the controlpanels connected to the server. The state changes periodically on sensor readings (every second) or when an event is triggered. 
- While connected, the sensor actuator node listens to the communication channel. When it receives a message the messagehandler parses the message and triggers an event.
- After connecting 

#### Control Panel
- Control panels listens to the communication channel. When it receives a message the messagehandler parses the message and triggers an event. Events typically changes the state of the local node representations and updates the GUI to show the new information.
- When a user interacts with a node in the GUI this triggers an event. When the state of a single node is changed in the GUI, a message is sent to the corresponding node on the network via the server. When the state of all nodes are changed in the Global tab, a multicast message is sent to all nodes on the network via the server.

## Connection and state

Our communication protocol is connection-oriented because the sockets between clients are always connected. It is also stateful because the server keeps track of the state of all connections. 

## Types, constants

We have a set of static global variables we use for both serialization and de-serialization/parsing of messages.
We call them splitters and there are currently six of them. 
- MESSAGE_SPLITTER: separates the head and body of a message.
- HEAD_SPLITTER: separates the values in the message head.
- TYPE_SPLITTER: separates the message type from the values in the message body
- BODY_SPLITTER: separates values in the message body.
- LIST_SPLITTER: separates elements in a list
- VALUES_SPLITTER: separates values of elements in a list. 

## Message format

#### General message format

All messages are separated into two parts: a head and a body. All data in the message is of type String.
- The head contains information about the receiver of the message that the server can use to route the message.
   - The receiver information contains a client type, and an id number. If no id number is present the message will be handled as a multicast to all clients of that type. 
- The body contains information about the message type and data.
  - The message type gives information to help the parser make sense of the data.
  - The data contains the actual information that the receiver will interpret and executed.
- When serialized, the head and body are concatenated, but separated by a MESSAGE_SPLITTER.

#### Control Panel Messages
This section describes the specific format for each message that can be sent by the control panels.

##### Actuator Change Message
- HEAD: "node" + HEAD_SPLITTER + nodeID 
- BODY: "ActuatorChangeMessage" + TYPE_SPLITTER + actuatorID + BODY_SPLITTER + isOn
##### Control Panel Connection Message
- HEAD: "server"
- BODY: "ControlPanelConnectionMessage" + TYPE_SPLITTER + connecting
##### Request Nodes Message
- HEAD: "server"
- BODY: "RequestNodesMessage"
##### Update Actuator By Type Message
- HEAD: "server"
- BODY: "UpdateActuatorByTypeMessage" + TYPE_SPLITTER + actuatorType + BODY_SPLITTER + isOn
#### Sensor Actuator Node Messages
This section describes the specific format for each message that can be sent by the sensor actuator nodes. 
##### Actuator Update Message
- HEAD: "controlpanel"
- BODY: "ActuatorUpdateMessage" + TYPE_SPLITTER + nodeID + BODY_SPLITTER + actuatorID + BODY_SPLITTER + isOn
##### Add Node Message (Multicast / Unicast)
- HEAD: "controlpanel" (if unicast: + HEAD_SPLITTER + clientHandlerID)
- BODY: "AddNodeMessage" + TYPE_SPLITTER + nodeID + BODY_SPLITTER + for:actuator{ actuatorID + VALUES_SPLITTER + actuatorType + VALUES_SPLITTER isOn + LIST_SPLITTER}
##### Node Connection Message
- HEAD: "server"
- BODY: "NodeConnectionMessage" + TYPE_SPLITTER + connecting + BODY_SPLITTER + nodeID
##### Sensor Update Message
- HEAD: "controlpanel"
- BODY: "SensorUpdateMessage" + TYPE_SPLITTER + nodeID + BODY_SPLITTER + for:sensorReading { readingType + VALUES_SPLITTER + readingValue + VALUES_SPLITTER + readingUnit + VALUES_SPLITTER + LIST_SPLITTER }
#### Server Messages
This section describes the specific format for each message that can be sent by the server. 
##### Remove Node Message
- HEAD: "controlpanel"
- BODY: "RemoveNodeMessage" + TYPE_SPLITTER + nodeID
##### Request Nodes Message
- HEAD: "node"
- BODY: "RequestNodesMessage" + TYPE_SPLITTER + clientHandlerID

### Error messages

If a message cannot be parsed correctly an error will be logged.
If a connection is abruptly closed an error will be logged.

## An example scenario

1. Server is started. Opens a listening socket on TCP port 8585. 
2. Control Panel with the name CP1 starts up and tries to connect to the server and waits for the ACK. Server accepts the connection and responds with SYN ACK. CP1 reponds with another ACK. (Three way handshake).
3. Server starts handling the client connection on a new thread. Resumes listening on port 8585
4. Control Panel sends a message to the server, identifying the client as a control panel. Waits for ACK. Server receives the message and sends ACK.
5. Server places client handler in a map to enable routing to client.
6. SensorActuatorNode SAN1 is started. Goes through the same procedure as steps 2-5, but identifies the client as a node.
7. SAN1 sends node information message to the server with target: all controlpanels. Waits for ACK. SAN1 doesn't receive ACK from server. Sends the data again and waits for ACK. Server receives the message and sends ACK.
8. Server sends the message to the server message handler. Message handler reads message head. For every control panel with an active connection to the server: The server sends the message to the control panel and waits for ACK. Control Panel receives the message and sends ACK. Server repeats for next control panel.

## Reliability and security

Because we are using TCP, messages that don't get acknowledged by the receiver will be re-sent. This ensures reliable data transfer over the network. 
We have implemented error handling for most cases so that the system will never crash due to unexpected behaviour. 
Unexpected behaviour is logged with descriptions so that we can easily investigate them.
