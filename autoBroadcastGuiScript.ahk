#NoEnv  ; Recommended for performance and compatibility with future AutoHotkey releases.
; #Warn  ; Enable warnings to assist with detecting common errors.
SendMode Input  ; Recommended for new scripts due to its superior speed and reliability.
#Persistent
#SingleInstance,Force
SetTitleMatchMode, 2 ; match start of the title

;----Init Vars----------------------------------------------------
intMins:= 0
intHrs:= 0
cam:=setCamNum()
while(cam==null)
	cam:=setCamNum()
interval:=setRInterval(interval)
while(interval==null)
	interval:=setRInterval(interval)
intervalTime:= toTimeObject(interval)
streaming := 0
broadcasting:=0
StartTime:=0
StopTime:=0

;----GUI----------------------------------------------------------
Gui, 1:destroy
GUI, 2:Show,w500 h300 vG2, Broadcast Control panel 
Gui, 2:font, cblack
Gui, 2:add, text, x150 s30 ,welcome to Broadcast control panel 
Gui, 2:add, button,x15 w100 h30 gaddCam, ADD CAMERA
Gui, 2:add, button,x+2 w100 h30 gremCam, REMOVE CAMERA
Gui, 2:add, text, x+40 y35 vCamNum,Current Camera Num is %cam%
Gui, 2:add, button,x15 w200 h30 gsetInterval, CHANGE RECORDING INTERVAL
Gui, 2:add, text, x+40 vIntervalText,Current Recording Interval is %interval% minutes
Gui, 2:add, button,x15 w200 h30 gstartBrd, START BROADCASTING
Gui, 2:add, text, x+40 vbrdText,Live Broadcast Is OFF
Gui, 2:add, button,x15 w200 h30 gstopBrd, STOP BROADCASTING
Gui, 2:add, button,x15 w200 h30 gstartIntBrd, START INTERVAL BROADCASTING
Gui, 2:add, text, x+40 vbrdIntText,Interval Broadcast Is OFF
Gui, 2:add, button,x15 w200 h30 gstopIntBrd, STOP INTERVAL BROADCASTING
Gui, 2:add, text, x+40 vIntervalTime, current Inetval start and stop time is not defined
Gui,2: +AllwaysOnTop

return
;----labels-------------------------------------------------------
addCam:
cam:=AddCamNum(cam)
GuiControl,,CamNum,Current Camera Num is %cam% 
return

setInterval:
interval:=setRInterval(interval)
intervalTime:= toTimeObject(interval)
GuiControl,,IntervalText,Current Recording Interval is %Interval% Minutes
return

remCam:
cam:=RemCam(cam)
GuiControl,,CamNum,Current Camera Num is %cam% 
return

startBrd:
if(broadcasting==1){
 MsgBox, already Broadcasting
 return
}
broadcasting=1
startBroadcasting(cam)
GuiControl,,brdText,Live Broadcast Is ON
return

stopBrd:
if(broadcasting==0){
 MsgBox,stream is not broadcasting
 return
}
broadcasting=0
stopBroadcasting()
GuiControl,,brdText,Live Broadcast Is OFF
return

startIntBrd:
if(broadcasting==1){
 MsgBox, already Broadcasting
 return
}
broadcasting=1
StartTime:= A_Hour . A_Min
SetTimer, checkTime, 500
StopTime:=addInterval(StartTime,intervalTime)
MsgBox,in srtINTbrd ,start time: %StartTime% ,stop time: %StopTime% ,steaming:%steaming% , broadcasting:%broadcasting%
GuiControl,,IntervalTime,Inetval start time: %StartTime% stop time: %StopTime%  
GuiControl,,brdIntText, Interval Live Broadcast Is ON
MsgBox,in srtINTbrd
return

stopIntBrd:
if(broadcasting==0){
 MsgBox,stream is not broadcasting
 return
}
broadcasting=0
SetTimer , checkTime, OFF
MsgBox, timer off
stopBroadcasting()
GuiControl,,brdIntText,Interval Live Broadcast Is OFF
return 

checkTime:
time := A_Hour . A_Min
If (time = StartTime && !streaming) {
	streaming := 1
	startBroadcasting(cam)	;start the stream
	GuiControl,,brdIntText,Live Broadcast Is ON
	TrayTip STREAMING, Starting the Stream
}
If (time = StopTime && streaming) {
	streaming := 0
	stopBroadcasting()  ;stop the stream
	StartTime:=time
	StopTime:=:=addInterval(StartTime,intervalTime)
	GuiControl,,IntervalTime,Inetval start time: %StartTime% stop time: %StopTime% 
	TrayTip STREAMING, Stoping the stream start:%StartTime%  stop%StopTime% streaming:%streaming%
}

return
;----functions----------------------------------------------------
/* 
this method ends a live stream evnent and closes the proccess
input:cams
*/
stopBroadcasting(){
	MouseMove, 277,369
	sleep, 2000
	click
	MouseMove, 787, 194
	sleep, 5000
	MouseMove, 1348, 23
	sleep, 2000
	click
	TrayTip,STREAMING, broadcasting has stopped
	return
}
/* 
this method starts a live stream evnent and add cameras with regard to input
input:cams
*/
startBroadcasting(cam){
	TrayTip IN SRTBRD,stratbrd func
	CoordMode, mouse ,screen
	run https://www.youtube.com/my_live_events?o=U&ar=1566140058078	;goto live event link
	Sleep, 2000
	CoordMode, mouse ,screen
	MouseMove , 287, 379																;create new live event
	Sleep, 2000
	click
	Sleep, 4000
	if(cam==1){
		MouseMove 465, 201
		sleep, 2000
		click
		goto, end
		
	}
	click ,243, 199																	;go to ingestion settings
	Sleep, 5000
	click ,230, 199
	Sleep, 5000
	click ,316, 323																	;add camera 2
	Sleep, 8000
	send ,{pgdn}
	sleep, 2000
	click, 268, 505 																;open checkbox
	sleep, 2000
	click, 275, 699																	;add camera 2 key stream
	sleep, 2000
	if(cam==2)
		goto, end
	click, 383, 360																	;add camera 3
	sleep, 2000
	click, 383, 360	
	Sleep, 8000
	send ,{pgdn}
	sleep, 2000
	click, 268, 505 																;open checkbox
	sleep, 2000
	click, 292, 682																	;add camera 3 key stream
	sleep, 2000
	if(cam==3)
		goto, end 
	
	click, 456, 361																	;add camera 4
	sleep, 2000
	click, 456, 361	
	Sleep, 8000
	send ,{pgdn}
	sleep, 2000
	click, 268, 505 																;open checkbox
	sleep, 4000
	click, 272, 654																	;add camera 4 key stream
	sleep, 2000
	if(cam==4)
		goto, end
	
	click, 520, 361																	;add camera 5
	sleep, 2000
	click, 520, 361	
	Sleep, 8000
	send ,{pgdn}
	sleep, 2000
	click, 268, 505 																;open checkbox
	sleep, 4000
	click, 273, 628																;add camera 5 key stream
	sleep, 2000
	if(cam==5)
		goto, end
				
	CoordMode, mouse ,screen
	click, 588, 361																	;add camera 6
	sleep, 2000
	click, 588, 361	
	Sleep, 8000
	send ,{pgdn}
	sleep, 2000
	click, 268, 505 																;open checkbox
	sleep, 4000
	click, 275, 602																;add camera 6 key stream
	sleep, 2000
	
	end:
	click, 636, 200																	;goto live control room
	sleep, 2000
	click, 636, 200
}

/*
adds a camera to next broadcat
*/
AddCamNum(cam){
	if(cam<6 && cam>0){
		MsgBox, camera will be added to next recording session 
		return cam+1
	}
	else{
		MsgBox,maximum camera limit reached
		return cam
	}
}
/*
sets interval time to next broadcat
*/
setRInterval(interval){
	tmp:=0
	InputBox, tmp,`t,please enter intrval time in minutes between 1 and 360
	if ErrorLevel{
		MsgBox, request cancelled
		return interval
	}
	if(tmp<=600 && tmp>=1){
		MsgBox, interval was set to %tmp% minutes
		return tmp
	}
	else{
		MsgBox, wrong input interval wasn't changed
		return setRInterval(interval)
	}
}
/*
set initial camera number
*/
setCamNum(){
	tmp:=0
	InputBox, tmp,`t,please enter number of cameras you would like to broadcast 1 and 6
	if ErrorLevel{
		MsgBox, request cancelled
		return
	}
	if(tmp<=6 && tmp>=1){
		return tmp
	}
	else{
		MsgBox, wrong number entered please try again
		return setCamNum()
	}
}
/*
removes a camera to next broadcat
*/ 
RemCam(cam){
	if(cam>1){
		MsgBox,camera will be removed from next recording session
		return cam-1 
	}
	else{
		MsgBox,minimum camera limit reached
		return cam
	}
		
}

/*
helper method for setting start and stop broadcast time
input :  time in minutes 
returns :time in HHMM format note HH could be 0
*/
toTimeObject(interval){
	hours:=interval/60			;get hour count
	if(Round(hours)>hours)
		hours:=Round(hours)-1   ;if rounded up decrease 1
	else
		hours:=Round(hours)
	minutes:=mod(interval,60)	;get minute count
	MsgBox hours:%hours% mins:%minutes% 
	tmp:= 100*hours
	tmp+=minutes
	MsgBox time object returned:%tmp%
	return tmp
		
}
/*
helper method to generate a new stop time with regard to current start time and interval time
*/
addInterval(StartTime,intervalTime){
if(round(StartTime/100)>StartTime/100)	 ; get start Hrs
	startHrs:=round( StartTime/100 )-1
else
	startHrs:=round( StartTime/100 )
if(round( intervalTime/100 )>intervalTime/100 ) ; get interval Hrs
	intHrs:=round( intervalTime/100 )-1
else
	intHrs:=round( intervalTime/100 )
startMins:= StartTime - (startHrs*100)
intMins:=intervalTime - (intHrs*100)
stopHrs:=Mod(startHrs+intHrs,24)		
stopMIns:=Mod(startMins+intMins,60)
if(startMins+intMins>59)
	stopHrs++
MsgBox StartTime:%StartTime% intervalTime:%intervalTime% startHrs%startHrs% intHrs%intHrs% startMins:%startMins% intMins:%intMins% stopHrs:%stopHrs% stopMIns:%stopMIns%
stoptime:=(stopHrs*100) + stopMIns
 return stoptime
}
exitApp
^q::ExitApp
^w::startBroadcasting(cam)
