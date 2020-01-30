Friends of Swifts 
------------------------
this is Shiraz readme
------------------------
This is a Live broadcast automation Script with Gui.
Before Using please make sure you have installed your Ip cameras on your local network and you have their IP addresses,
otherwise see instructions on installing your cameras on your local network here: __LINK__

Usage Instructions:
----------------------

Initializing:(do it once)
----------------------


1.download and install OBS studio on your pc at https://obsproject.com/download 

3.set OBS studio and set all camera rtmp streams to OBS and add a scene for each camera and add a Scene to each camera, watch video at __LINK__ 
  
  step 1: get all camera IP addresses from your local network , replace __CameraIPAddress__  with them : rtsp://admin:admin@__CameraIPAddress__:554/11
  
  step 2: open OBS studio
  
  step 3: Adding a scene

		  click +(scenes)->enter scene name in the field -> click OK
		  
  step 4: Adding a camera stream
  
		  click +(sources)->click media source ->click create new / add existing ->
		  enter in input field: rtsp://admin:admin@CameraIPAddress:554/11 -> uncheck all other fields ->click OK
		  
  step 5: set up bitrate 
  
  __Remember:__ Total upload speed is an important factor of broadcast bitrate of each camera.
  - preform a speed test at https://www.speedtest.net/ and get yor upload speed
  - calculate the birate: bitrate = uploadSpeed/numberOfcameras
  
		  click on settings ->click output -> in bitrate field enter bitrate youv'e caculated -> click apply ->click OK
		  
  step 6: set Frames per Second 10-30 depending on latency 
  
		  click on settings -> click on Video ->in FPS field enter desired FPS -> click apply -> click OK
			
3.make sure Live streaming on YouTube is enabled on your account if not please enable it, watch video at __LINK__ 

4.create a new live event on YouTube and go to ingestion setting and add the amount of cameras needed, watch video at __LINK__ 

5.make sure to generate reusable Streaming key depending on the amount of cameras needed for broadcast, watch video at __LINK__ 

6.make sure to set the output resolution same as base resolution:  

			click Settings -> click video -> set Base ressolution equal to Output resolution.


Before running App:(before starting broadcast)
----------------------
1.open OBS studio with number of instances depending on number of cameras needed for broadcast

2.at every instance of OBS studio go to Settings->stream and at the stream key field enter one reusable key that you generated

3.set every instance to a scene of a different camera

4.press start streaming at every instance of OBS studio

5.run the app

watch video showing how __LINK__

Using the App:
----------------------
//TODO

watch video showing how __LINK__

written by: Evgeny Geyfman
-----------------------


