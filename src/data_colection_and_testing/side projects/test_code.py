data_tuple = (1,2,3)
print(len(data_tuple))

byte_array = ['\x04','\xd2']
print((byte_array))

data = int.from_bytes(b'\x04\xd2', byteorder='big')
print(data)

import struct
a = 1234
s = struct.pack('>H', a)
print(s)