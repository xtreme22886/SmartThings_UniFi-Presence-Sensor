/**
 *  Unifi WiFi Presence
 *
 *  Copyright 2020 xtreme
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */

definition(
    name: "Unifi Wireless Presence",
    namespace: "xtreme22886",
    author: "xtreme",
    description: "Use Unifi wireless clients as presence sensor",
    category: "My Apps",
    singleInstance: true,
    iconUrl: "https://p7.hiclipart.com/preview/59/426/450/ubiquiti-networks-wireless-access-points-wi-fi-unifi-wifi.jpg",
    iconX2Url: "https://p7.hiclipart.com/preview/59/426/450/ubiquiti-networks-wireless-access-points-wi-fi-unifi-wifi.jpg",
    iconX3Url: "https://p7.hiclipart.com/preview/59/426/450/ubiquiti-networks-wireless-access-points-wi-fi-unifi-wifi.jpg",
    oauth: true
)


preferences {
	page(name: "mainPage")
	page(name: "unifiClientsPage")
}

def mainPage() {
    dynamicPage(name: "mainPage", title: "", nextPage: null, uninstall: true, install: true) {
   	section(""){
            input "bridgeAddress", "string", title: "Bridge Address", required: true, description:"ex: 192.168.0.100:30000"
            input "unifiAddress", "string", title: "Unifi Controller Address", required: true, description:"ex: 192.168.0.100:8443"
            input "unifiUsername", "string", title: "Unifi Controller Username", required: true, description:"Username for the Unifi Controller"
            input "unifiPassword", "string", title: "Unifi Controller Password", required: true, description:"Password for the Unifi Controller"
            input "unifiSite", "string", title: "Unifi Controller Site", required: true, description:"Unifi 'site' where devices are"
        }
        
        section() {
            paragraph "View a list of Unifi wireless clients available for monitoring"
          	href "unifiClientsPage", title: "Unifi Client List", description:""
       	}
        
       	//section() {
            //paragraph "View this SmartApp's configuration to use it in other places"
            //href url:"${apiServerUrl("/api/smartapps/installations/${app.id}/config?access_token=${state.accessToken}")}", style:"embedded", required:false, title:"Config", description:"Tap, select, copy, then click back"
       	//}
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    
    if (!state.accessToken) {
        createAccessToken()
    }

    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    initialize()
    
    if(state.monitored) {
        def oldList = state.monitored
        def newList = toMonitor
        log.debug "Old list: ${oldList}"
        log.debug "New list: ${newList}"
        def toAdd = null
        def toDelete = null
        if (newList) {
            toAdd = newList - oldList
            toDelete = oldList - newList
            if (toAdd) {
                addDevice(toAdd)
            }
            if (toDelete) {
                deleteDevice(toDelete)
            }
        } else {
            deleteDevice(oldList)
        }
        state.monitored = newList
    } else {
        state.monitored = toMonitor
        if (toMonitor) {
            addDevice(toMonitor)
        }
    }
	
    sendToUnifiBridge()
}

def initialize() {
    log.debug "initialize"
    
    def options = [
     	"method": "POST",
        "path": "/settings",
        "headers": [
            "HOST": settings.bridgeAddress,
            "Content-Type": "application/json"
        ],
        "body":[
            "app_url":"${apiServerUrl}/api/smartapps/installations/",
            "app_id":app.id,
            "access_token":state.accessToken,
            "unifiAddress":settings.unifiAddress,
            "unifiUsername":settings.unifiUsername,
            "unifiPassword":settings.unifiPassword,
            "unifiSite":settings.unifiSite.toLowerCase()
        ]
    ]
    
    def myhubAction = new physicalgraph.device.HubAction(options, null, [callback: null])
    sendHubCommand(myhubAction)
}

def unifiClientsPage() {
    log.debug "Getting list of Unifi wireless clients"
    
    def options = [
     	"method": "GET",
        "path": "/wificlients",
        "headers": [
            "HOST": settings.bridgeAddress,
            "Content-Type": "application/json"
        ]
    ]
    
    def myhubAction = new physicalgraph.device.HubAction(options, null, [callback: parseClients])
    sendHubCommand(myhubAction)
    
    dynamicPage(name: "unifiClientsPage", title:"Unifi Wireless Clients", refreshInterval:5) {
        section("") {
            input(name: "toMonitor", type: "enum", title: "Which client(s) to monitor?", options: state.unifiClients, multiple: true, required: false)
        }
    }
}

    def parseClients(physicalgraph.device.HubResponse hubResponse) {
    def msg = parseLanMessage(hubResponse.description)
    state.unifiClients = new groovy.json.JsonSlurper().parseText(msg.body)
}

def getLocationID() {
    def locationID = null
    try{ locationID = location.hubs[0].id }catch(err){}
    return locationID
}

def sendToUnifiBridge() {
    log.debug "Telling the Unifi Bridge to monitor the following device(s): ${toMonitor}"
       
    def options = [
     	"method": "POST",
        "path": "/monitor",
        "headers": [
            "HOST": settings.bridgeAddress,
            "Content-Type": "application/json"
        ],
        "body": ["toMonitor": toMonitor]
    ]
    
    def myhubAction = new physicalgraph.device.HubAction(options, null, [callback: null])
    sendHubCommand(myhubAction)
}

def renderConfig() {
    def configJson = new groovy.json.JsonOutput().toJson([
        description: "Unifi Bridge API",
        platforms: [
            [
                platform: "SmartThings Unifi Bridge",
                name: "Unifi Bridge",
                app_url: apiServerUrl("/api/smartapps/installations/"),
                app_id: app.id,
                access_token: state.accessToken
            ]
        ],
    ])

    def configString = new groovy.json.JsonOutput().prettyPrint(configJson)
    render contentType: "text/plain", data: configString
}

def getDeviceList() {
    log.debug "List device(s): ${toAdd}"
    def list = getChildDevices();
    def resultList = [];
    list.each { child ->
        log.debug "child device id $child.deviceNetworkId with label $child.label"
        def dni = child.deviceNetworkId
        resultList.push(dni)
    }
    
    def configString = new groovy.json.JsonOutput().toJson("devices":resultList)
    render contentType: "application/json", data: configString
}

def updateDevice() {
    def body = request.JSON
    log.debug "Received the following presence updates: ${body.update}"
    body.update.each { device ->
        def chlid = getChildDevice(device.id)
        chlid.setPresence(device.present)
    }
	
    def resultString = new groovy.json.JsonOutput().toJson("result":"success")
    render contentType: "application/json", data: resultString
}

def addDevice(List toAdd) {
    log.debug "Adding device(s): ${toAdd}"
    def chlid = getChildDevice(dni)
    if (!child){
        def dth = "Unifi Presence Sensor"
        toAdd.each{
            def mac = it.replaceAll(".*\\(|\\).*", "")
            def dni = "unifi-" + mac
            def name = it
            addChildDevice("xtreme22886", dth, dni, getLocationID(), ["label": name])    
         }
    }
}

def deleteDevice(List toDelete) {
    log.debug "Deleting device(s): ${toDelete}"
    def chlid = getChildDevice(dni)
    if (!child) {
        toDelete.each{
            def mac = it.replaceAll(".*\\(|\\).*", "")
            def dni = "unifi-" + mac
            log.debug "Deleting: ${dni}"
            deleteChildDevice(dni)
         }
    }
}

mappings {
    path("/config")                         { action: [GET: "renderConfig"]  }
    path("/list")                           { action: [GET: "getDeviceList"]  }
    path("/update")                         { action: [POST: "updateDevice"]  }
}
