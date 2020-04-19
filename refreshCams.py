import time
import easygui
from pynput.keyboard import Key, Controller

def refresh():
    keyboard = Controller()
    keyboard.press(Key.ctrl)
    keyboard.press(Key.alt)
    keyboard.press(Key.shift)
    keyboard.press('h')
    time.sleep(0.5)
    keyboard.release('h')
    time.sleep(1)
    keyboard.press('s')
    time.sleep(0.5)
    keyboard.release(Key.shift)
    keyboard.release(Key.ctrl)
    keyboard.release(Key.alt)
    keyboard.release('s')

 
myvar = easygui.enterbox("please enter refresh interval in seconds 500-30")
while int(myvar)>=500 or int(myvar)<30 :
	myvar = easygui.enterbox("please enter refresh interval in seconds 500-30")
print("Please don't close this window ,auto refresh every "+str(myvar) +" seconds")
while True:
    seconds=int(myvar)
    time.sleep(seconds)
    refresh()