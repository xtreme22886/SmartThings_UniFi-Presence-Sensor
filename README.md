# SmartThings <-> Unifi Presence Sensor

Integration for SmartThings to use wireless Unifi clients as presence sensors

This will allow you to select from a list of known Unifi wireless clients to monitor their presence. By selecting a device(s) to monitor, a script will run every 5 seconds to check the Unifi controller's current status of the monitored device(s). If the device's `last_seen` time is greater than 30 seconds then the device is reported offline. A device is "offline" when it is not connected to an Unifi wireless network.

**NOTE:** There is a bug of sorts in the Unifi Controller in that when a wireless device is no longer connected to the network, it is still updating the devices `last_seen` value with the current time. Because of this, we can't use the `last_seen` value to check if a device is connected/disconnected to the network. Instead, what I've seen happen is that when a wireless device is no longer connected to the network, the Unifi controller changes the devices `isWired` status from **False** to **True**. So we are monitoring for this change in the `isWired` value to determine when a wireless device is connected to the network or not.

## Getting Started

These instructions will help you get this solution implemented. We are going to assume a working local Unifi Controller already exist on the network.

### What are we going to be setting up / installing

Here are the things that we will need to install / configure

- Unifi Bridge (REST API server to facilitate communication between SmartThings and the Unifi Controller)
  - This can be installed from [here](https://github.com/xtreme22886/SmartThings_Unifi-Presence-REST)
- Unifi Wireless Presence (SmartThings SmartApp)
- Unifi Presence Sensor (SmartThings Device Handler)

### Installing

There are two ways you can install the SmartThings SmartApp and Device Handler.
1. VIA the SmartThings IDE GitHub integration
   - Owner = xtreme22886
   - Name = SmartThings_Unifi-Presence-Sensor
   - Branch = master
2. VIA copying / pasting the code in the SmartThinge IDE
   - SmartApp = unifi-wireless-presence.groovy
   - Device Handler = unifi-presence-sensor.groovy

Instead of describing the two methods of installtion here, I'm going to refer you to the documentation for installing the [SmartThings Community Installer](http://thingsthataresmart.wiki/index.php?title=Community_Installer_(Free_Marketplace)) as it's well laid out. You will need to be familiar with "Installation" section, specifically installing from code, installing from GitHub and OAuth setup.

```
Give the example
```

And repeat

```
until finished
```

End with an example of getting some data out of the system or using it for a little demo

## Running the tests

Explain how to run the automated tests for this system

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Built With

* [FastAPI](https://fastapi.tiangolo.com/) - Web framework for building APIs


To get started:

- You will need to be running the Docker image located here (https://github.com/xtreme22886/SmartThings_Unifi-Presence-REST). This is the Unifi Bridge that will act as the middle man between SmartThings and the Unifi Controller.
- Install the Unifi Wireless Presence SmartApp into the SmartThings IDE
-- Be sure to enable OAuth for this app. Publish the app.
- Install the Unifi Presence Sensor Device Handler into the SmartThings IDE
-- Publish the device handler
- Configure the Unifi Wireless Presence SmartApp and click Save
- Go back into the Unifi Wireless Presence SmartApp and select which wireless clients you want to monitor
