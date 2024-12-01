# Project

Course project for the
course [IDATA2304 Computer communication and network programming (2023)](https://www.ntnu.edu/studies/courses/IDATA2304/2023).

Project theme: a distributed smart greenhouse application, consisting of:

* Sensor-actuator nodes
* Visualization nodes

See protocol description in [protocol.md](protocol.md).

## Getting started

There are several runnable classes in the project.

To run the greenhouse part (with sensor/actuator nodes):

* To be able to run the Greenhouse firstly you always need to run the `main` method inside the `ServerStarter`
* After the Server has been initialized you run the `main` inside the `GreenhouseHouseRun` class
* Lastly you run the `main` method inside the `ControlPanelStater` class to start the control panel from where you can 
* see the sensor data, and control the actuators.

## Simulating events

If you want to simulate fake communication (just some periodic events happening), you can run
both the greenhouse and control panel parts with a command line parameter `fake`. Check out
classes in the [`no.ntnu.run` package](src/main/java/no/ntnu/run) for more details. 