# -*- coding: utf-8 -*-
import numpy as np
import pandas as pd
import math
from enum import Enum


# sample code for the scoring algorithm

class BodyParts(Enum):
    spine_base = 0
    spine_mid = 1
    neck = 2
    head = 3
    shoulder_left = 4
    elbow_left = 5
    wrist_left = 6
    hand_left = 7
    shoulder_right = 8
    elbow_right = 9
    wrist_right = 10
    hand_right = 11
    hip_left = 12
    knee_left = 13
    ankle_left = 14
    foot_left = 15
    hip_right = 16
    knee_right = 17
    ankle_right = 18
    foot_right = 19
    spine_shoulder = 20
    hand_tip_left = 21
    thumb_left = 22
    hand_tip_left = 23
    thumb_left = 24


class Body:
    def __init__(self, joints):
        self.zero_mean_data = None
        self.norm_data = None
        self.joints = np.array(joints)

    def scale(self):
        self.zero_mean_data = (self.joints - np.average(self.joint, axis=1)) / np.std(self.joints, axis=1)
        return self.zero_mean_data

        # WIP

    def normalization(self, user, trainer):
        coeff = pd.DataFrame
        coeff["kneeRight"] = np.absolute(
            average_limb(user, "ankleRight", "kneeRight") - average_limb(user, "ankleRight",
                                                                         "kneeRight")) / average_limb(user,
                                                                                                      "ankleRight",
                                                                                                      "kneeRight")
        self.norm_data["kneeRight"] = coeff["kneeRight"] * np.absolute(user["kneRight"] - user["ankleRight"]) + user[
            "ankleRight"]

        coeff["hipRight"] = np.absolute(
            average_limb(user, "kneeRight", "hipRight") - average_limb(user, "kneeRight", "hipRight")) / average_limb(
            user, "kneeRight", "hipRight")
        self.norm_data["hipRight"] = coeff["hipRight"] * np.absolute(user["hipRight"] - user["kneeRight"]) + user[
            "kneeRight"]

        coeff["hipCenter"] = np.absolute(
            average_limb(user, "hipRight", "hipCenter") - average_limb(user, "hipRight", "hipCenter")) / average_limb(
            user, "hipRight", "hipCenter")
        self.norm_data["hipCenter"] = coeff["hipCenter"] * np.absolute(user["hipCenter"] - user["hipRigth"]) + user[
            "hipRight"]

        coeff["spine"] = np.absolute(
            average_limb(user, "hipCenter", "spine") - average_limb(user, "hipCenter", "spine")) / average_limb(user,
                                                                                                                "hipCenter",
                                                                                                                "spine")
        self.norm_data["hipCenter"] = coeff["spine"] * np.absolute(user["spine"] - user["hipCenter"]) + user["hipCenter"]


# distance between 2 points = sqrt((x0 - x1)^2 + (y0 - y1)^2)
def length(j1, j2):
    return math.sqrt((j1[0] - j2[0]) ** 2 + (j1[1] - j2[1]) ** 2)


def average_limb(data, name1, name2):
    avr = int
    if "Right" in name1:
        avr = (length(data[name1], data[name2]) + length(data[name1["Right", "Left"], name2["Right", "Left"]])) / 2
    if "Left" in name1:
        avr = (length(data[name1], data[name2]) + length(data[name1["Left", "Right"], name2["Left", "Right"]])) / 2
    return avr;


def scoring(user, trainer):
    score = 100
    sampling_rate = 30
    window_weight = 100 / round(np.shape(user)[1] / sampling_rate)
    acceptable_error = 0.05
    diff = np.absolute(user - trainer)
    for i in round(np.shape(user)[1] / sampling_rate):
        diff_sample = np.average(user[:][i: sampling_rate * i])
        score -= np.amax(diff_sample, axis=1) * window_weight * (1 - acceptable_error)
