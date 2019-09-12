Friends of Swifts 
------------------------

This is an Live broadcast automation Script with Gui.
Before Using please make sure you have installed your Ip cameras on your local network and you have their IP addresses,
otherwise see instructions on installing your cameras on your local network here: __LINK__

Usage Instructions:
----------------------
Initializing:(do it once)
----------------------
1.download and install AHK on your pc at https://www.autohotkey.com/ 

2.download and install OBS studio on your pc at https://obsproject.com/download 

3.add all camera rtmp streams to OBS and add a scene for each camera and add a Scene to each camera, watch video at __LINK__ 
  
  step 1: get all camera Ip addresses from your loacal network , replace __CameraIPAddress__ string with them : rtsp://admin:admin@CameraIPAddress:554/11
  
  step 2: in OBS studio
  Adding a scene: click +(scenes)->enter scene name in the field -> click OK
  
  Adding a camera stream: click +(sources)->click media source ->click create new / add existing ->
  enter in input field: rtsp://admin:admin@CameraIPAddress:554/11 -> uncheck all other fields ->click OK
  
  _Remember_ your Total upload speed is important factor of broadcast bitrate of each camera, preform a speed test at https://www.speedtest.net/ and get yor upload speed
  calculate the birate: bitrate = uploadSpeed/numberOfcameras
  
  setp 3: click on settings ->click output -> in bitrate field enter bitrate youv'e caculated -> click apply ->click OK
		  
  step 4: set Frames per Second 10-30 dependingon latency -> click apply -> click OK
  
		  click on settings -> click on Video ->in FPS field enter desired FPS 
			

4.make sure Live streaming on YouTube is enabled on your account if not please enable it, watch video at __LINK__ 

5.create a new live event on YouTube and go to ingestion setting and add the amount of cameras needed, watch video at __LINK__ 

6.make sure to generate 1-6 reusable Streaming key depending on the amount of cameras needed for broadcast, watch video at __LINK__ 


Before running script:(before starting broadcast)
----------------------
1.open OBS studio with number of instances depending on number of cameras needed for broadcast

2.at every instance of OBS studio go to Settings->stream and at the stream key field enter one reusable key that you generated

3.set every instance to a scene of a different camera

4.press start streaming at every instance of OBS studio

5.run the script 

watch video showing how __LINK__

Using the script:
----------------------
1.first enter the amount of cameras that are streaming (1-6, and can be changed later)

2.then enter the interval length wanted for interval broadcasting in minutes (can be left vacant and can be set later)

3.while APP is running you can choose to start broadcasting Now or Later , change interval length and cameras

4.you can choose between An interval broadcast (starts and ends automatically) or can be done manually

5.to exit app press ctrl+q

watch video showing how __LINK__

written by: Evgeny Geyfman
-----------------------


