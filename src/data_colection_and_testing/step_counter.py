"""
Fortune E, Lugade V, Morrow M, Kaufman K. 
Validity of using tri-axial accelerometers to measure human movement - 
Part II: Step counts at a wide range of gait velocities. 
Med Eng Phys. 2014 Jun;36(6):659-69. 
doi: 10.1016/j.medengphy.2014.02.006. 
Epub 2014 Mar 20. PMID: 24656871; PMCID: PMC4030415.
"""


import matplotlib.pyplot as plt
import matplotlib.lines as mlines
import matplotlib.transforms as mtransforms
import pandas as pd
import os
import re

def main(max_in_step_threashold, directory):
    #'2_min_walk_tests'
    #'2_min_walking_stoping'
    #directory = '2_min_walk_tests'
    files = os.listdir(directory)

    perceived_step_counts = []
    predicted_step_counts = []
    max_in_step = float('-inf')
    #max_in_step_threashold = 500
    walking = False


    for file in files:
        perceived_step_counts.append(int(re.search('([0-9]*).?[0-9]?steps', file).group(1)))




    for i,file in enumerate(files):
        df = pd.read_csv(directory+'/'+file)
        predicted_step_counts.append(0)
        instep = False
        counted = False
        threashold = 0

        for point in df['first2']:
            if point > threashold: 
                instep = True
                if point > max_in_step:
                    max_in_step = point
            else:
                instep = False
                max_in_step = float('-inf')

            if max_in_step > max_in_step_threashold:
                walking = True
            else:
                walking = False

            if instep != counted:
                counted = instep
                if instep and walking:
                    predicted_step_counts[i] += 1



    return compare(perceived_step_counts, predicted_step_counts)




def compare(perceived_step_counts, predicted_step_counts):
    total_diference = 0
    total_perceived = 0
    total_predicted = 0

    for i in range(len(perceived_step_counts)):
        total_diference += predicted_step_counts[i]-perceived_step_counts[i]
        total_perceived += perceived_step_counts[i]
        total_predicted += predicted_step_counts[i]

    """print(f'difference:\t{total_diference}')
    print(f'perceived:\t{total_perceived}')
    print(f'predicte:\t{total_predicted}')"""
    if total_diference < 0:
        accuaracy = total_predicted/total_perceived
        #print('accuaracy:\t'+str(accuaracy))
    else:
        accuaracy = total_perceived/total_predicted
        #print('accuaracy:\t'+str(accuaracy))
    return accuaracy
    





max_accuaracy1 = [0, 0]
max_accuaracy2 = [0, 0]
accuaracy1 = 0
accuaracy2 = 0
max_in_step_threashold = 119
while (accuaracy1 < .90) or (accuaracy2 < .90):
    accuaracy1 = main(max_in_step_threashold,'2_min_walk_tests')
    accuaracy2 = main(max_in_step_threashold,'2_min_walking_stoping')

    if accuaracy1 > max_accuaracy1[1]:
        max_accuaracy1[1] = accuaracy1
        max_accuaracy1[0] = max_in_step_threashold
    if accuaracy2 > max_accuaracy2[1]:
        max_accuaracy2[1] = accuaracy2
        max_accuaracy2[0] = max_in_step_threashold


    max_in_step_threashold += 1


    if(max_in_step_threashold > 256):
        break

print(max_accuaracy1)
print(max_accuaracy2)



