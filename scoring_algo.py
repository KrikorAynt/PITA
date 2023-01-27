# -*- coding: utf-8 -*-
"""capstone.ipynb

Automatically generated by Colaboratory.

Original file is located at
    https://colab.research.google.com/drive/1DDI5Z2YOs4ZMHnGyWwrdud036BYmxM9n
"""

import numpy as np
import pandas as pd
import math

#sample code for the scoring algorithm

class Body:
  def __init__(self, joints):
    self.joints = np.array(joints)
    self.head = joints["Head"]
    self.shoulderCenter = joints["ShoulderCenter"]
    self.handLeft = joints["handLeft"]
    self.wristLeft = joints["wristLeft"]
    self.elbowLeft = joints["elbowLeft"]
    self.shoulderLeft = joints["shoulderLeft"]
    self.handRight = joints["handRIght"]
    self.wristRigth = joints["Wrists"]
    self.elbowRight = joints["elbowRight"]
    self.shoulderRight = joints["shoulderRight"]
    self.Spine = joints["Spine"]
    self.hipCenter = joints["hipCenter"]
    self.hipRight = joints["hipRight"]
    self.hipLeft = joints["hipLeft"]
    self.kneeRight = joints["kneeRight"]
    self.kneeLeft = joints["kneeLeft"]
    self.ankleRight = joints["ankleRight"]
    self.ankleLeft = joints["ankleLeft"] 
    self.footRight = joints["footRight"]
    self.footLeft = joints["footLeft"]
    self.sdata
    self.ndata
  
  def scale(self):
    self.sdata = (self.joints - np.average(self.joint, axis = 1))/np.std(self.joints, axis = 1)
    return self.sdata 

  #WIP 
  def normalization(self, user, trainer):
    coeff = pd.DataFrame
    coeff["kneeRight"] = np.absolute(average_limb(user, "ankleRight", "kneeRight") - average_limb(user, "ankleRight", "kneeRight"))/average_limb(user, "ankleRight", "kneeRight")
    self.ndata["kneeRight"] = coeff["kneeRight"] * np.absolute(user["kneRight"] - user["ankleRight"]) + user["ankleRight"]

    coeff["hipRight"] = np.absolute(average_limb(user, "kneeRight", "hipRight") - average_limb(user, "kneeRight", "hipRight"))/average_limb(user, "kneeRight", "hipRight")
    self.ndata["hipRight"] = coeff["hipRight"] * np.absolute(user["hipRight"] - user["kneeRight"]) + user["kneeRight"]

    coeff["hipCenter"] = np.absolute(average_limb(user, "hipRight", "hipCenter") - average_limb(user, "hipRight", "hipCenter"))/average_limb(user, "hipRight", "hipCenter")
    self.ndata["hipCenter"] = coeff["hipCenter"] * np.absolute(user["hipCenter"] - user["hipRigth"]) + user["hipRight"]

    coeff["spine"] = np.absolute(average_limb(user, "hipCenter", "spine") - average_limb(user, "hipCenter", "spine"))/average_limb(user, "hipCenter", "spine")
    self.ndata["hipCenter"] = coeff["spine"] * np.absolute(user["spine"] - user["hipCenter"]) + user["hipCenter"]


#distance between 2 points = sqrt((x0 - x1)^2 + (y0 - y1)^2)
def length(j1, j2):
  return math.sqrt((j1[0] - j2[0]) ** 2 + (j1[1] - j2[1]) ** 2)

def average_limb(data, name1, name2):
    avr = int
    if "Right" in name1:
      avr = (length(data[name1], data[name2]) + length(data[name1["Right", "Left"], name2["Right", "Left"]]))/2
    if "Left" in name1:
      avr = (length(data[name1], data[name2]) + length(data[name1["Left", "Right"], name2["Left", "Right"]]))/2
    return avr;    

def scoring(user, trainer):
  score = 100
  sampling_rate = 30
  window_weight = 100/round(np.shape(user)[1]/sampling_rate)
  acceptable_error = 0.05
  diff = np.absolute(user - trainer)
  for i in round(np.shape(user)[1]/sampling_rate):
    diff_sample = np.average(user[:][i : sampling_rate * i])
    score -= np.amax(diff_sample, axis = 1) * window_weight * (1 - acceptable_error)