# SmartThings_Unifi-Presence-Sensor
Integration for SmartThings to use wireless Unifi clients as presence sensors

This will allow you to select from a list of known Unifi wireless clients to monitor their presence. By selecting a device(s) to monitor, a script will run every 5 seconds to check the Unifi controller's current status of the monitored device(s). If the device's 'last_seen' time is greater than 30 seconds then the device is reported offline. A device is 'offline' when it is not connected to an Unifi wireless network.

NOTE: There is a bug of sorts in the Unifi Controller in that when a wireless device is no longer connected to the network, it is still updating the devices 'last_seen' value with the current time. Because of this, we can't use the 'last_seen' value to check if a device is connected/disconnected to the network. Instead, what I've seen happen is that when a wireless device is no longer connected to the network, the Unifi controller changes the devicse 'isWired' status from 'False' to 'True'. So we are monitoring for this change in the 'isWired' value to determine when a wireless device is connected to the network or not.

Once you have selected which devices you want to monitor, the SmartThings SmartApp will either create or delete devices as needed as well as tell the bridge which devices to monitor.

To get started:

- You will need to be running the Docker container located here (). This is the Unifi Bridge that will act as the middle man between SmartThings and the Unifi Controller.
- Install the Unifi Wireless Presence SmartApp into the SmartThings IDE
- Install the Unifi Presence Sensor Device Handler into the SmartThings IDE
- Configure the Unifi Wireless Presence SmartApp and click Save
- Go back into the Unifi Wireless Presence SmartApp and select which wireless clients you wnat to monitor
