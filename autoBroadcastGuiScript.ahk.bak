#NoEnv  ; Recommended for performance and compatibility with future AutoHotkey releases.
; #Warn  ; Enable warnings to assist with detecting common errors.
SendMode Input  ; Recommended for new scripts due to its superior speed and reliability.
#Persistent
#SingleInstance,Force
SetTitleMatchMode, 2 ; match start of the title

;----Init Vars----------------------------------------------------
intSecs:= 0										;intercal second counter
intMins:= 0										;intercal minute counter
streaming := 0									;streaming flag 
broadcasting:=0									;general broadcasting flag
brdFlag=0										;regular brd flag
intbrdFlag=0									;interval brd flag
StartTime:=0									;brd start time
StopTime:=0										;brd stop time
cam:=setCamNum()								;initiate camera number
startlaterflag:=0								;start later flag
while(cam==null)
	cam:=setCamNum()
setRInterval(interval)							;initiate interval size
intervalTime:= toTimeObject(interval)	
;----GUI----------------------------------------------------------
GUI, 2:Show,w500 h300 vG2, Broadcast Control panel 
Gui, 2:font, cblack s12
Gui, 2:add, text, x130 y+1 s30 ,welcome to Broadcast control panel 
Gui, 2:font, cblack s8
Gui, 2:add, button,x15 w100 h30 gaddCam, ADD CAMERA
Gui, 2:add, button,x+2 w100 h30 gremCam, REMOVE CAMERA
Gui, 2:add, button,x15 w200 h30 gsetInterval, CHANGE BROADCAST INTERVAL
Gui, 2:add, button,x15 w200 h30 gstartIntBrd, START INTERVAL BROADCAST NOW
Gui, 2:add, button,x15 w200 h30 gstartLater, START INTERVAL BROADCAST LATER
Gui, 2:add, button,x15 w200 h30 gstopIntBrd, STOP INTERVAL BROADCASTING
Gui, 2:add, button,x15 w200 h30 gstartBrd, START BROADCASTING
Gui, 2:add, button,x15 w200 h30 gstopBrd, STOP BROADCASTING
Gui, 2:font, cblack s10
Gui, 2:add, text,s10 x+20 y35 vCamNum,Current Camera Num is: %cam%
Gui, 2:add, text,  y+25 vIntervalText,Current Interval is: %interval% minutes
Gui, 2:add, text,  y+25 vbrdIntText,Interval Broadcast Is OFF
Gui, 2:add, text,  y+23 vIntervalTime,Inetval start and stop time is not defined
Gui, 2:add, text,  y+22 vbrdText,Live Broadcast Is OFF
Gui, 2:add, text,  y+22 vbrdTimer,Broadcast Timer 0%intMins%:0%intSecs%
Gui, 2:add, Picture,x390 y180 w100 h100 vpic,C:\Users\Jos pc\Downloads\swift.jpg
Gui,2: +AllwaysOnTop

return
;----labels-------------------------------------------------------
addCam:
	AddCamNum(cam)																;set cam num
	GuiControl,,CamNum,Current Camera Num is: %cam%								;print to gui
return

setInterval:
	setRInterval(interval)										  				;set interval num	in minutes			
	if(interval==null){
		GuiControl,2:,IntervalText,interval isn't set 							;print to gui
		return
	}
	intervalTime:= toTimeObject(interval)								    	;convert intreval time to HH:MM format
	if(broadcasting){
		GuiControl,2:,IntervalText,Next Interval will be %Interval% Minutes 	;print to gui
	return
	}
	GuiControl,2:,IntervalText,Current Interval is %Interval% Minutes 			;print to gui
return

remCam:
	RemCam(cam)														  			;remove camera for next broadcast
	GuiControl,,CamNum,Current Camera Num is: %cam%	 
return

brodtimer:
	if(intSecs==59){															;check seconds
		intMins++
		if(intMins<0)
			intMins:=0 							;overflow case
	}
	intSecs++																	;increase seconds
	intSecs:=mod(intSecs,60)
	if(intSecs<10 && intMins<10)												;print to gui depending on case
		GuiControl,2:,brdTimer,Broadcast Timer 0%intMins%:0%intSecs%
	else if(intMins<10 && intSecs>0)
		GuiControl,2:,brdTimer,Broadcast Timer 0%intMins%:%intSecs%
	else if(intSecs<10)
		GuiControl,2:,brdTimer,Broadcast Timer %intMins%:0%intSecs%
	else
		GuiControl,2:,brdTimer,Broadcast Timer %intMins%:%intSecs%
return

startBrd:										
	if(broadcasting==1){														;check if already broadcasting
	 MsgBox, already Broadcasting
	 return
	} 
	if(intbrdFlag){
	 MsgBox,inteval broadcasting is On end it before staring regular broadcast
	 return
	}
	broadcasting=1																;set flags
	brdFlag=1
	TrayTip STREAMING, Starting the Stream Please Don't Touch Mouse or Keyboard Until The Signal
	startBroadcasting(cam)														;start brd
	TrayTip STREAMING, finished Starting the Stream 
	GuiControl,,brdText,Live Broadcast Is ON									;print to gui
	SetTimer, brodtimer,1000													;start timer
return

stopBrd:
	if(broadcasting==0){														;check flags
	 MsgBox,stream is not broadcasting
	 return
	}
	if(intbrdFlag){
	 MsgBox,regular broadcasting is On end it before staring inteval broadcast
	 return
	}
	broadcasting=0																;set flags
	brdFlag=0
	stopBroadcasting()															;stop brd
	GuiControl,2:,brdText,Live Broadcast Is OFF									;print to gui
	SetTimer, brodtimer,off														;stop time
	intMins:=0																	;reset time vars
	intSecs:=0
return
startLater:
	InputBox,StartTime,`t,Please enter requsted start time in HH:MM Format
	if(!isTimeFormat(StartTime))
		goto startLater
	startlaterflag:=1
startIntBrd:
	if(interval==null){
		goto setInterval
	}
	if(broadcasting==1){														;check flags
	 MsgBox, already Broadcasting
	 return
	}
	if(brdFlag){
	 MsgBox,broadcasting is already On
	 return
	}
	intbrdFlag:=1																;set flags
	broadcasting=1
	if(!startlaterflag)
		StartTime:= A_Hour . A_Min												;set start time
	StopTime:=addInterval(StartTime,intervalTime)								;set stop time
	SetTimer, checkTime, 500													;start interval timer
	;MsgBox,in srtINTbrd ,start time: %StartTime% ,stop time: %StopTime% ,steaming:%steaming% , broadcasting:%broadcasting% ;testing
	GuiControl,2:,IntervalTime,Inetval start time: %StartTime% stop time: %StopTime%  ;print to gui
	GuiControl,2:,brdIntText, Interval Broadcast Is ON
	;MsgBox,in srtINTbrd														;testing
return

stopIntBrd:
	if(broadcasting==0){														;check flags
	 MsgBox,stream is not broadcasting
	 return
	}
	if(brdFlag){
	 MsgBox,broadcasting is already On
	 return
	}
	broadcasting=0																;set flags
	intbrdFlag:=0
	startlaterflag:=0															
	SetTimer , checkTime, OFF													;set iterval timer off
	;MsgBox, timer off															;testing
	stopBroadcasting()															;stop brd				
	SetTimer, brodtimer,off														;set minute timer off	
	intMins:=0																	;reset vars
	intSecs:=0
	GuiControl,2:,brdIntText,Interval Broadcast is OFF							;print to gui
return 

checkTime:
	time := A_Hour . A_Min														;get current time
	If (time = StartTime && !streaming) {										;check if its start time
		streaming := 1															;set flag
		startBroadcasting(cam)													;start the stream			
		SetTimer, brodtimer,1000												;set minute counter
	}
	If (time = StopTime && streaming) {											;check if it's stop time
		streaming := 0															;set flag
		stopBroadcasting() 					 									;stop the stream
		SetTimer, brodtimer,off													;stop minute counter
		intMins:=0																;reset vars
		intSecs:=0
		StartTime:=time															;set next interval start time
		StopTime:=addInterval(StartTime,intervalTime)							;set next interval stop time
		GuiControl,2:,IntervalTime,Inetval start time: %StartTime% stop time: %StopTime% 	;print to gui
	}
return

GuiClose:
	exitApp
;----functions----------------------------------------------------
/* 
this method ends a live stream evnent and closes the proccess
input:cams
*/
stopBroadcasting(){
	TrayTip STREAMING, Stopping the Stream Please Don't Touch Mouse or Keyboard Until The Signal
	CoordMode, mouse ,screen
	MouseMove, 272, 371
	sleep, 2000
	click
	MouseMove, 787, 194
	sleep, 5000
	MouseMove, 1348, 23
	sleep, 2000
	click
	TrayTip,STREAMING, broadcasting has stopped
	return
	TrayTip STREAMING, finished Stopping the Stream 
}
/* 
this method starts a live stream evnent and add cameras with regard to input
input:cams
*/
startBroadcasting(cam){
	TrayTip STREAMING, Starting the Stream Please Don't Touch Mouse or Keyboard Until The Signal
	CoordMode, mouse ,screen
	run https://www.youtube.com/my_live_events											;goto live event link
	Sleep, 2000
	CoordMode, mouse ,screen
	MouseMove , 287, 570																;create new live event
	Sleep, 2000
	click
	Sleep, 4000
	if(cam==1){
		MouseMove 465, 395
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
	sleep,2000
	MouseMove 246, 372
	Click
	sleep,2000
	MouseMove 789, 197
	click
	sleep,25000
	MouseMove 208, 412
	sleep,1000
	click
	sleep,2000
	MouseMove 271, 371
	sleep,1000
	click
	sleep,2000
	MouseMove 783, 172
	sleep,1000
	click 
	TrayTip STREAMING, finished Starting the Stream 
}

/*
adds a camera to next broadcat
*/
AddCamNum(byref cam){
	if(cam<6 && cam>0){
		MsgBox, camera will be added to next recording session 
		cam++
		return
	}
	else{
		MsgBox,maximum camera limit reached
		return 
	}
}
/*
sets interval time to next broadcat
*/
setRInterval(byref interval){
	tmp:=0
	InputBox, tmp,`t,please enter intrval time in minutes between 5 and 360
	if ErrorLevel{
		MsgBox, interval hasn't been set
		interval:=null
		return 
	}
	if(tmp<=600 && tmp>=5){
		MsgBox, interval was set to %tmp% minutes
		interval:=tmp
		return 
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
RemCam(byref cam){
	if(cam>1){
		MsgBox,camera will be removed from next recording session
		cam--
		return  
	}
	else{
		MsgBox,minimum camera limit reached
		return 
	}
}

/*
helper method for setting start and stop broadcast time
input :  time in minutes 
returns :time in HHMM format note HH could be 0
*/
toTimeObject(interval){
	hours:=floor(interval/60)	;get hour count
	minutes:=mod(interval,60)	;get minute count
	tmp:= 100*hours				;compute time to HH:MM format
	tmp+=minutes
	;MsgBox time object returned:%tmp%
	return tmp					
}
/*
helper method to generate a new stop time with regard to current start time and interval time
*/
addInterval(StartTime,intervalTime){
	startHrs:=floor(StartTime/100)						;get start HH
	intHrs:=floor(intervalTime/100)						;get interval HH
	startMins:= StartTime - (startHrs*100)				;get start MM
	intMins:=intervalTime - (intHrs*100)				;get interval MM
	stopHrs:=Mod(startHrs+intHrs,24)					;calc stop HH
	stopMIns:=Mod(startMins+intMins,60)					;calc stop MM
	;MsgBox StartTime:%StartTime% intervalTime:%intervalTime% startHrs%startHrs% startMins:%startMins% intHrs%intHrs% intMins:%intMins% stopHrs:%stopHrs% stopMIns:%stopMIns%
	if(startMins+intMins>59)							;add 1 HH if needed
		stopHrs++
	stoptime:=(stopHrs*100) + stopMIns					;set stop time to HH:MM
	return stoptime
}
isTimeFormat(time){
	hours:=floor(time/100)			;get hour count
	minutes:=time - (hours*100)		;get minute count
	;MsgBox hours:%hours%minutes:%minutes%
	if(hours>24 || hours <0 || minutes>59 || minutes<0 )		;check if time is valid HH:MM format
		return 0
	return 1
}
exitApp

^q::exitApp
^w::startBroadcasting(cam)
