import matplotlib.pyplot as plt
import numpy as np
import asyncio
import time
import struct 
import pandas as pd
from bleak import BleakScanner, BleakClient


async def main():
    # if not working check device name and uuid
    uuid_battery_level = "00002a19-0000-1000-8000-00805f9b34fb" 

    # choose display interval and run time in seconds
    interval = 5
    run_time = 8


    table = {'first2': [],
             'second2': [],
            'Time': []}
    input_range = [0,0]

    # input name
    # name = input("Name the type of data your collecting: ") 
    name = "test"


    print("Searching for Device ...")
    devices = await BleakScanner.discover()
    found = False
    for d in devices:
        print(d.name)
        if d.name == "ShankMonitor":
            found = True
            shank = d
    if found:
        print("Connecting ...")
        async with BleakClient(shank.address) as client:
            data = await client.read_gatt_char(uuid_battery_level)
            print(type(data),len(data))

            timeout = time.time() + run_time   
            while time.time() < timeout:
                # add to data
                data = await client.read_gatt_char(uuid_battery_level)
                next_time = time.time()
                table['Time'].append(next_time)
                next_input = (struct.unpack('h'*(len(data)//2), data))
                print(next_input)
                table['first2'].append(next_input[0])
                table['second2'].append(next_input[1])

                
                # add to graph
                # set bounds of x axis
                if next_time < table['Time'][0]+interval:
                    plt.xlim(table['Time'][0],table['Time'][0]+interval)
                else:
                    plt.xlim(next_time-interval,next_time)
                # set bounds of y axis
                if  next_input[0] < input_range[0]:
                    input_range[0] = next_input[0]
                elif next_input[0] > input_range[1]:
                    input_range[1] = next_input[0]
                if  next_input[1] < input_range[0]:
                    input_range[0] = next_input[1]
                elif next_input[1] > input_range[1]:
                    input_range[1] = next_input[1]
                plt.ylim(input_range[0],input_range[1])
                
                # plot
                plt.plot(table['Time'],table['first2'], 'blue')
                plt.plot(table['Time'],table['second2'], 'green')
                plt.pause(.001)


            plt.show()

        df = pd.DataFrame.from_dict(table ,orient='index').transpose()
        df.to_csv(name+".csv", index=False) # publish name and duration of test in minutes

    else:
        print("Device not Found ...")





asyncio.run(main())