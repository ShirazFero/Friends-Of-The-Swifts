import time
from pynput.keyboard import Key, Controller

def refresh():
    keyboard = Controller()
    keyboard.press(Key.ctrl)
    keyboard.press(Key.alt)
    keyboard.press(Key.shift)
    keyboard.press('h')
    time.sleep(0.1)
    keyboard.release('h')
    time.sleep(0.2)
    keyboard.press('s')
    time.sleep(0.1)
    keyboard.release(Key.shift)
    keyboard.release(Key.ctrl)
    keyboard.release(Key.alt)
    keyboard.release('s')

while True:
    seconds=600
    print("auto refresh ... every "+str(seconds))
    time.sleep(seconds)
    refresh()