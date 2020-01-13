# SmartThings <-> Unifi Presence Sensor

**Integration for SmartThings to use Unifi wireless clients as presence sensors**

This will allow you to select from a list of known Unifi wireless clients to monitor their presence. By selecting device(s) to monitor, a script will run every 5 seconds to check the Unifi controller's current status of the monitored device(s). If the device `last_seen` time is greater than 30 seconds then the device is reported offline. A device is "offline" when it is not connected to an Unifi wireless network.

**NOTE:** There is a bug of sorts in the Unifi Controller in that when a wireless device is no longer connected to the network, it is still updating the devices `last_seen` value with the current time. Because of this, we can't use the `last_seen` value to check if a device is connected/disconnected to the network. Instead, what I've seen happen is that when a wireless device is no longer connected to the network, the Unifi Controller changes the device `isWired` status from **False** to **True**. So I'm monitoring for this change in the `isWired` value to determine when a wireless device is connected to the network or not.

## Getting Started

These instructions will help you get this solution implemented. I'm going to assume a working local Unifi Controller already exist on the network.

### What are we going to be setting up / installing

Here are the things that we will need to install / configure

- Unifi Bridge (REST API server to facilitate communication between SmartThings and the Unifi Controller)
  - This can be installed from [here](https://github.com/xtreme22886/SmartThings_Unifi-Presence-REST)
- Unifi Wireless Presence (SmartThings SmartApp)
- Unifi Presence Sensor (SmartThings Device Handler)

## Installing SmartApp and Device Handler
**First things first. Get the Unifi Bridge up and running before proceeding**

There are two ways you can install the SmartThings SmartApp and Device Handler.
1. VIA the SmartThings IDE GitHub integration
   - Owner = xtreme22886
   - Name = SmartThings_Unifi-Presence-Sensor
   - Branch = master
2. VIA copying / pasting the code in the SmartThinge IDE
   - SmartApp = unifi-wireless-presence.groovy
   - Device Handler = unifi-presence-sensor.groovy

Instead of describing the two methods of installation here, I'm going to refer you to the documentation for installing the [SmartThings Community Installer](http://thingsthataresmart.wiki/index.php?title=Community_Installer_(Free_Marketplace)) as it's well laid out. You will need to be familiar with "Installation" section, specifically installing from code, installing from GitHub and OAuth setup.

Setup Steps:
1. Install SmartApp
   - Make sure to `Publish` and **enabled** `OAuth` for this app
2. Install Device Handler
   - Make sure to `Publish` this device handler
3. In the **SmartThings Classic** mobile app
   - Automation > SmartApps > Add a SmartApp
   - My Apps > Unifi Wireless Presence
   - Enter in information for **Bridge Address**, **Unifi Controller Address**, **Unifi Controller Username**, **Unifi Controller Password** and **Unifi Controller Site**
4. Click **Save** all the way out of the SmartApp
   
**NOTE:** The **Unifi Controller Site** is **NOT** the *name* of the site but rather the *id* of the site. Take "https://x.x.x.x:8443/manage/site/default/dashboard" as an example; the *site id* is what is listed directly after **/site/**. In this cause, it would be **default**.

After setup has been completed we can begin to monitor devices
1. Go back into the SmartApp
2. Click on **Unifi Client List**
3. Wait 5 seconds for the list of wireless clients to refresh and populate
4. Click on `Tap to select`
5. Select the device(s) you want to monitor
6. Click **Save** all the way out of the SmartApp
7. Go back to your list of *Things* and locate the new presence device that was created
   - Feel free to rename this device as needed
   
**Notes:**
- Anytime you add/remove devices to be monitored, the SmartApp with add/remove the associated *thing*

## Built With

* [FastAPI](https://fastapi.tiangolo.com/) - Web framework for building APIs
