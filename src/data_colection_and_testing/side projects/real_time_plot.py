import matplotlib.pyplot as plt
import numpy as np
import random 
import time
from bleak import BleakScanner, BleakClient



# choose display interval and run time in seconds
interval = 5 
run_time = 30 


table = {'Input': [],
        'Time': []}
input_range = [0,0]



timeout = time.time() + run_time   
while time.time() < timeout:
    # add to data
    next_time = time.time()
    table['Time'].append(next_time)
    next_input = random.randint(-10,10)
    table['Input'].append(next_input)

    
    # add to graph
    # set bounds of x axis
    if next_time < table['Time'][0]+interval:
        plt.xlim(table['Time'][0],table['Time'][0]+interval)
    else:
        plt.xlim(next_time-interval,next_time)
    # set bounds of y axis
    if  next_input < input_range[0]:
        input_range[0] = next_input
    elif next_input > input_range[1]:
        input_range[1] = next_input
    plt.ylim(input_range[0],input_range[1])
    
    # plot
    plt.plot(table['Time'],table['Input'], 'black')
    plt.pause(.001)


plt.show()


