import serial,time
while True:
  s = serial.Serial("/dev/ttyACM0")
  s.baudrate = 115200
  data = s.readline()
  print(data)
  time.sleep(0.2)
