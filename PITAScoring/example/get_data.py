import numpy as np
import pandas as pd
import matplotlib
import matplotlib.pyplot as plt

user_data = pd.read_csv("https://raw.githubusercontent.com/ramzes-hk/datadump/main/20230215_190706_00_Skeleton.txt")

user_skeleton = user_data[[col for col in user_data.columns if col.startswith('PositionX')
                           or col.startswith('PositionY')]]

trainer_data = pd.read_csv("https://raw.githubusercontent.com/ramzes-hk/datadump/main/capstone_sample%20"
                           "-%20bicep_curl_test_Skeleton.csv")

trainer_skeleton = user_data[[col for col in user_data.columns if col.startswith('PositionX')
                              or col.startswith('PositionY')]]


