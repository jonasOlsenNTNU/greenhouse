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

## The underlying transport protocol

TODO - what transport-layer protocol do you use? TCP? UDP? What port number(s)? Why did you
choose this transport layer protocol?

Sensors UDP- Sensors are programmed using the UDP protocol for sending data, because of the frequent sending of data UDP would be the
fast and most natural protocol to implement in this case scenario. If data gets lost its no problem as newer
and more accurate in real time data is coming soon after. // Kan også implementere TCP connection om control panel,
f.eks ikke får ny data innenfor en viss tidsperiode.

//TODO Actuators

Communication Node TCP-

Control Panel TCP - Control panel is programmed using the TCP protocol for sending commands. Because of the importance
of a command being correctly sent and acknowledged by the communication nodes the TCP protocol is perfect to use in
this case scenario.





## The architecture

TODO - show the general architecture of your network. Which part is a server? Who are clients?
Do you have one or several servers? Perhaps include a picture here.



## The flow of information and events

TODO - describe what each network node does and when. Some periodic events? Some reaction on
incoming packets? Perhaps split into several subsections, where each subsection describes one
node type (For example: one subsection for sensor/actuator nodes, one for control panel nodes).

Sensor node -  Sense their surroundings every second and send back the information they gather back to the
Communication node. For example if it´s a sensor node with temperature and humidity sensor the sensors send their
information back to the Communication node

Actuators Node-

Communication Nodes

Control Panel

## Connection and state

TODO - is your communication protocol connection-oriented or connection-less? Is it stateful or
stateless?

## Types, constants

TODO - Do you have some specific value types you use in several messages? They you can describe
them here.

## Message format

TODO - describe the general format of all messages. Then describe specific format for each
message type in your protocol.

### Error messages

TODO - describe the possible error messages that nodes can send in your system.

## An example scenario
TODO - describe a typical scenario. How would it look like from communication perspective? When
are connections established? Which packets are sent? How do nodes react on the packets? An
example scenario could be as follows:

1. Control Panel with name CP1 tells Communication node with the name CNode1 to close Vent1
2. CNode1 sends the command to Vent1 and waits for the ACK
3. The vent closes and sends ACK to CNode1
4. CNode1 sends information about the vent closing to CP1
5. Sensor with the name of Sens1 sends information to CNode 1
6. CNode1 sends the information to CP1 and waits for ACK
7. CNode1 doesnt receive ACK from CP1, sends the information again and waits for ACK
8. CP1 correctly receives the information from the node and sends back an ACK


## Reliability and security

TODO - describe the reliability and security mechanisms your solution supports.
