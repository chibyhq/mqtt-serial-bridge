# MQTT Serial Bridge
[![IDE Version](https://jitpack.io/v/chibyhq/mqtt-serial-bridge.svg)](https://jitpack.io/#chibyhq/mqtt-serial-bridge) 
[![GitHub Actions Status](https://github.com/chibyhq/mqtt-serial-bridge/workflows/Java%20CI/badge.svg)](https://github.com/chibyhq/mqtt-serial-bridge/actions)
[![Java 11](https://img.shields.io/badge/Java-11-green "Java 11")](https://java.com)

A serial port to MQTT bridge in Java with Spring Boot affinities and support for multiple MQTT naming conventions and message formats.

## Current Features

* 🛠️ Supports YAML configuration / Spring cloud client-friendly.
* 🌍️ Deployed to jitpack.io for easy access.
* 🍽️ Provides embedded MQTT broker support via Moquette.
* 🏠️ Provides MQTT Homie naming convention adapters [https://homieiot.github.io/].
* 🔌️ Provides pluggable message formatters (JSON, line protocol)

## Planned Features

* 👷️ Expose JMX MBeans for monitoring
* 🔌️ Support MQTT RPC for remote serial port control

### How to use the bridge library

#### The Jitpack way

* Download the Spring Boot service distribution, for instance version ```0.1``` :

```wget https://jitpack.io/com/github/chibyhq/mqtt-serial-bridge/msb-service/0.1/msb-service-0.1.jar```

:warning: Please remember to replace 0.1 in the command above by the version number you wish to use.

* Create a Spring Boot configuration in ```application.yml``` :

```yaml
msb.serial:
  port: ttyACM0
  baudrate: 115200
msb.mqtt:
  server.enable: true
  forward: true
  client:
    id: ${random.uuid}
    server.uri: "tcp://localhost:1883"
```

* Connect your serial device using the port name and baud rate indicated in your configuration

* Fire up your MQTT client (e.g. MQTT Explorer) - you should see the following topics, for instance :
   * ```/homie/<hostname>/serial/ttyACM0/in``` for messages you want to send to your serial device.
   * ```/homie/<hostname>/serial/ttyACM0/out``` for messages you want to collect from your serial device

### Samples

* Spring-boot based service example.
* Microbit serial line protocol to JSON websocket MQTT sample application.
