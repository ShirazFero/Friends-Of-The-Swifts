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
while(cam==null)
	cam:=setCamNum()
interval:=setRInterval(interval)				;initiate interval size
while(interval==null)
	interval:=setRInterval(interval)
intervalTime:= toTimeObject(interval)

;----GUI----------------------------------------------------------
GUI, 2:Show,w500 h300 vG2, Broadcast Control panel 
Gui, 2:font, cblack s12
Gui, 2:add, text, x130 y+1 s30 ,welcome to Broadcast control panel 
Gui, 2:font, cblack s8
Gui, 2:add, button,x15 w100 h30 gaddCam, ADD CAMERA
Gui, 2:add, button,x+2 w100 h30 gremCam, REMOVE CAMERA
Gui, 2:add, button,x15 w200 h30 gsetInterval, CHANGE RECORDING INTERVAL
Gui, 2:add, button,x15 w200 h30 gstartIntBrd, START INTERVAL BROADCASTING
Gui, 2:add, button,x15 w200 h30 gstopIntBrd, STOP INTERVAL BROADCASTING
Gui, 2:add, button,x15 w200 h30 gstartBrd, START BROADCASTING
Gui, 2:add, button,x15 w200 h30 gstopBrd, STOP BROADCASTING
Gui, 2:font, cblack s10
Gui, 2:add, text,s10 x+20 y35 vCamNum,Current Camera Num is: %cam%
Gui, 2:add, text,  y+25 vIntervalText,Current Recording Interval is: %interval% minutes
Gui, 2:add, text,  y+25 vbrdIntText,Interval Broadcast Is OFF
Gui, 2:add, text,  y+23 vIntervalTime,Inetval start and stop time is not defined
Gui, 2:add, text,  y+22 vbrdText,Live Broadcast Is OFF
Gui, 2:add, text,  y+22 vbrdTimer,Broadcast Timer 0%intMins%:0%intSecs%
Gui, 2:add, Picture,x390 y180 w100 h100 vpic,C:\Users\Jos pc\Downloads\swift.jpg
Gui,2: +AllwaysOnTop

return
;----labels-------------------------------------------------------

addCam:
	cam:=AddCamNum(cam)															;set cam num
	GuiControl,,CamNum,Current Camera Num is %cam% 								;print to gui
return

setInterval:
	interval:=setRInterval(interval)										  	;set interval num	in minutes			
	intervalTime:= toTimeObject(interval)								    	;convert intreval time to HH:MM format
	if(broadcasting){
		GuiControl,2:,IntervalText,Next Interval will be %Interval% Minutes 	;print to gui
	return
	}
	GuiControl,2:,IntervalText,Current Recording Interval is %Interval% Minutes 	;print to gui
return

remCam:
	cam:=RemCam(cam)														  	;remove camera for next broadcast
	GuiControl,,CamNum,Current Camera Num is %cam% 
return

startBrd:										
	if(broadcasting==1){														;check if already broadcasting
	 MsgBox, already Broadcasting
	 return
	}
	broadcasting=1																;set flags
	brdFlag=1
	;startBroadcasting(cam)														;start brd
	GuiControl,,brdText,Live Broadcast Is ON									;print to gui
	SetTimer, brodtimer,1000													;start timer
return

brodtimer:
	;TrayTip BRDtimer,here every sec
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

stopBrd:
	if(broadcasting==0){														;check flags
	 MsgBox,stream is not broadcasting
	 return
	}
	if(intbrdFlag){
	 MsgBox,inteval broadcasting is On
	 return
	}
	broadcasting=0																;set flags
	brdFlag=0
	;stopBroadcasting()															;stop brd
	GuiControl,2:,brdText,Live Broadcast Is OFF									;print to gui
	SetTimer, brodtimer,off														;stop time
	intMins:=0																	;reset time vars
	intSecs:=0
return

startIntBrd:
	if(broadcasting==1){														;check flags
	 MsgBox, already Broadcasting
	 return
	}
	intbrdFlag:=1																;set flags
	broadcasting=1
	StartTime:= A_Hour . A_Min													;set start time
	SetTimer, checkTime, 500													;start interval timer
	StopTime:=addInterval(StartTime,intervalTime)								;set stop time
	;MsgBox,in srtINTbrd ,start time: %StartTime% ,stop time: %StopTime% ,steaming:%steaming% , broadcasting:%broadcasting%
	GuiControl,2:,IntervalTime,Inetval start time: %StartTime% stop time: %StopTime%  ;print to gui
	GuiControl,2:,brdIntText, Interval Live Broadcast Is ON
	;MsgBox,in srtINTbrd
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
	SetTimer , checkTime, OFF													;set iterval timer off
	;MsgBox, timer off
	stopBroadcasting()															;stop brd				
	SetTimer, brodtimer,off														;set minute timer off	
	intMins:=0																	;reset vars
	intSecs:=0
	GuiControl,2:,brdIntText,Interval Live Broadcast is OFF						;print to gui
return 

checkTime:
	time := A_Hour . A_Min														;get current time
	If (time = StartTime && !streaming) {										;check if its start time
		streaming := 1															;set flag
		;startBroadcasting(cam)													;start the stream			
		SetTimer, brodtimer,1000												;set minute counter
		GuiControl,2:,brdIntText,Live Broadcast Is ON							;print to gui
		TrayTip STREAMING, Starting the Stream
	}
	If (time = StopTime && streaming) {											;check if it's stop time
		streaming := 0															;set flag
		;stopBroadcasting() 					 									;stop the stream
		SetTimer, brodtimer,off													;stop minute counter
		intMins:=0																;reset vars
		intSecs:=0
		StartTime:=time															;set next interval start time
		StopTime:=addInterval(StartTime,intervalTime)							;set next interval stop time
		GuiControl,2:,IntervalTime,Inetval start time: %StartTime% stop time: %StopTime% 		;print to gui
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
	
	CoordMode, mouse ,screen
	run https://www.youtube.com/my_live_events?o=U&ar=1566140058078						;goto live event link
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
	tmp:= 100*hours
	tmp+=minutes
	;MsgBox time object returned:%tmp%
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
	;MsgBox StartTime:%StartTime% intervalTime:%intervalTime% startHrs%startHrs% intHrs%intHrs% startMins:%startMins% intMins:%intMins% stopHrs:%stopHrs% stopMIns:%stopMIns%
	stoptime:=(stopHrs*100) + stopMIns
	 return stoptime
}

exitApp
^q::ExitApp
^w::startBroadcasting(cam)
