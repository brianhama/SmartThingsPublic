/**
 *  Door
 *
 *  Copyright 2016 Brian Hamachek
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
    name: "Door",
    namespace: "brianhama",
    author: "Brian Hamachek",
    description: "This is a door.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences
{
	section ("Auto-Lock...")
    	{
			input "contact0", "capability.contactSensor", title: "Which door?"
        	input "lock0","capability.lock", title: "Which lock?"
        	input "autolock_delay", "number", title: "Delay for auto-Lock after door is closed? (Seconds)"
    	} 
}

def installed()
{
	initialize()
}

def updated()
{
	log.debug "Updating"
    
	unsubscribe()
	unschedule()
	initialize()
}

def initialize()
{
	log.debug "Initializing"
    
	subscribe(contact0, "contact.closed", door_handler)
}

def door_handler(evt)
{
	if(evt.value == "closed")
    {
        state.lockattempts = 0
        
        if(autolock_delay == 0)
        {
        	lock_door()
        }
        else
        {
			runIn(autolock_delay, "lock_door")
        }
	}
}

def lock_door() // auto-lock specific
{
	if (contact0.latestValue("contact") == "closed")
	{
		lock0.lock()
        runIn(5000, "check_door_actually_locked")
	}
}

def check_door_actually_locked() // if locked, reset lock-attempt counter. If unlocked, try once, then notify the user
{
	if (lock0.latestValue("lock") == "locked")
    {
    	sendNotificationEvent("Door has been automatically locked.")
    	state.lockattempts = 0
    }
    else // if the door doesn't show locked, try again
    {
    	state.lockattempts = state.lockattempts + 1
        if ( state.lockattempts < 2 )
        {
        	lock_door()
		}
        else
        {
        	sendNotificationEvent("Sorry, door could not be automatically locked for some unknown reason!")
        }
	}
}