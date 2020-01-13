/**
 *  Unifi Presence Sensor
 *
 *  Copyright 2019 xtreme
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

metadata {
	definition (name: "Unifi Presence Sensor", namespace: "xtreme22886", author: "xtreme", ocfDeviceType: "x.com.st.d.mobile.presence") {
		capability "Presence Sensor"
		capability "Sensor"
	}

	tiles {
		standardTile("presence", "device.presence", width: 2, height: 2, canChangeBackground: true) {
			state("present", labelIcon:"st.presence.tile.mobile-present", backgroundColor:"#00A0DC")
			state("not present", labelIcon:"st.presence.tile.mobile-not-present", backgroundColor:"#ffffff")
		}
		main "presence"
		details "presence"
	}
}

def setPresence(status) {	
    if (status == false) {
        status = "not present"
    } else {
        status = "present"
    }
    
    def old = device.latestValue("presence")
    
    // Do nothing if already in that state
    if ( old != status) {
	sendEvent(displayed: true,  isStateChange: true, name: "presence", value: status, descriptionText: "$device.displayName is $status")
    } 
}
