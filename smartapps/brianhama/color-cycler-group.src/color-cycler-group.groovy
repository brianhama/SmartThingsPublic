/**
 *  Color Cycler
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
    name: "Color Cycler Group",
    namespace: "brianhama",
    author: "Brian Hamachek",
    description: "Cycles colors on hue lights",
    category: "Fun & Social",
	iconUrl: "http://mail.lgk.com/huetree1.png",
	iconX2Url:"http://mail.lgk.com/huetree2.png",
	iconX3Url: "http://mail.lgk.com/huetree2.png",)


preferences {

	section("Choose hue lights you wish to control?") {
            input "hues", "capability.colorControl", title: "Which Color Changing Bulbs?", multiple:true, required: true
	}
}
	
def installed() {
    unsubscribe()
    unschedule()
    
   	initialize()
}

def updated() {

    unsubscribe()
    unschedule()
    
    initialize()
}

private def initialize() {
    if(hues) 
    {
       	state.currentColor = "None"
    
    	subscribe(hues, "switch.on", changeColors)    
    
        runEvery5Minutes(changeColors)
    	subscribe(app,changeColors)
        
    	changeColors();
    }
}

def changeColors(evt) {

    if (hues)
    {
    	 def currSwitches = hues.currentSwitch
         def onHues = currSwitches.findAll { switchVal -> switchVal == "on" ? true : false }
         def numberon = onHues.size();
         def onstr = numberon.toString() 
         
       log.debug "found $onstr that were on!"
    
    	if ((onHues.size() > 0))
    	{
def newColor = ""
                def int nextValue = new Random().nextInt(11)
                def colorArray = ["Red","Orange","Yellow","Green","Turquoise","Aqua","Navy Blue","Blue","Indigo","Purple","Pink","Rasberry"]
                newColor = colorArray[nextValue]  

        	hues.each {n -> 
                sendcolor(n, newColor)
            }
      	}
   }
}

def sendcolor(light, color)
{
	//Initialize the hue and saturation
	def hueColor = 0
	def saturation = 100
    def brightnessLevel=100

	//Set the hue and saturation for the specified color.
	switch(color) {
		case "White":
			hueColor = 0
			saturation = 0
			break;
		case "Daylight":
			hueColor = 53
			saturation = 91
			break;
		case "Soft White":
			hueColor = 23
			saturation = 56
			break;
		case "Warm White":
			hueColor = 20
			saturation = 80 
			break;
        case "Navy Blue":
            hueColor = 61
            break;
		case "Blue":
			hueColor = 65
			break;
		case "Green":
			hueColor = 33
			break;
        case "Turquoise":
        	hueColor = 47
            break;
        case "Aqua":
            hueColor = 50
            break;
        case "Amber":
            hueColor = 13
            break;
		case "Yellow":
			//hueColor = 25
            hueColor = 17
			break; 
        case "Safety Orange":
            hueColor = 7
            break;
		case "Orange":
			hueColor = 10
			break;
        case "Indigo":
            hueColor = 73
            break;
		case "Purple":
			hueColor = 82
			saturation = 100
			break;
		case "Pink":
			hueColor = 90.78
			saturation = 67.84
			break;
        case "Rasberry":
            hueColor = 94
            break;
		case "Red":
			hueColor = 0
			break;
         case "Brick Red":
            hueColor = 4
            break;                
	}

	//Change the color of the light
	def newValue = [hue: hueColor, saturation: saturation, level: brightnessLevel]  
	light.setColor(newValue)
    state.currentColor = color

}