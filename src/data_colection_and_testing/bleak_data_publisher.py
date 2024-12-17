"""
Python data publisher for shank protype gait data

Thi program uses the sdk @
https://github.com/hbldh/bleak

use intructions: 
$ pip install bleak
to instal 
then run the program

"""

import time
import struct 
import asyncio
import pandas as pd
from bleak import BleakScanner, BleakClient


async def main():
    # Services
    uuid_generic_access_profile = "00001800-0000-1000-8000-00805f9b34fb"
    uuid_generic_attribute_profile = "00001801-0000-1000-8000-00805f9b34fb"
    uuid_battery_service = "0000180f-0000-1000-8000-00805f9b34fb"

    # Characteristics
    uuid_device_name = "00002a00-0000-1000-8000-00805f9b34fb"
    uuid_appearance = "00002a01-0000-1000-8000-00805f9b34fb"
    uuid_service_changed = "00002a05-0000-1000-8000-00805f9b34fb"   # Handle: 7     Read Not Permitted
    uuid_battery_level = "00002a19-0000-1000-8000-00805f9b34fb"     # identity of the code output

    # Descriptors
    uuid_client_characteristic_configuration = "00002902-0000-1000-8000-00805f9b34fb"   # Handle: 9
    uuid_client_characteristic_configuration = "00002902-0000-1000-8000-00805f9b34fb"   # Handle: 11    Does not exist?

    #name = input("Name the type of data your collecting: ") 
    name = "test"
    table = {'first2': [],
            'second2': [],
            'Time': []}
    
    print("Searching for Device ...")
    devices = await BleakScanner.discover()
    found = False
    for d in devices:
        if d.name == "ShankMonitor":
            found = True
            shank = d
    if found:
        print("Connecting ...")
        async with BleakClient(shank.address) as client:
            
            data = await client.read_gatt_char(uuid_battery_level)
            print(type(data),len(data))

            timeout = time.time() + 120    # timeout set as current time + seconds  
            while time.time() < timeout:
                    data = await client.read_gatt_char(uuid_battery_level)
                    # '>' = big-endian, '<' = little endian, '=' = native
                    tuple_of_shorts = struct.unpack('h'*(len(data)//2), data) 
                    print(tuple_of_shorts)
                    table['first2'].append(tuple_of_shorts[0])
                    table['second2'].append(tuple_of_shorts[1])
                    table["Time"].append(time.time())
                  
    else:
        print("Device not Found ...")

    df = pd.DataFrame.from_dict(table ,orient='index').transpose()
    df.to_csv(name+".csv", index=False)


asyncio.run(main())


