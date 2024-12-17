import matplotlib.pyplot as plt
import pandas as pd


df = pd.read_csv('test.csv')



plt.xlim(df.min(axis=0)['Time'],df.max(axis=0)['Time'])
plt.ylim(df.min(axis=0)['first2'],df.max(axis=0)['first2'])   



plt.plot(df['Time'],df['first2'], 'black')

#plt.plot(df['Time'],data['x2'], 'blue')
#data['x1'].extend(data['x2'])




plt.show()