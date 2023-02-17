# -*- coding: utf-8 -*-
import numpy as np
import pandas as pd
import math
from enum import Enum


# sample code for the scoring algorithm

class BodyParts:
    spine_base = 0
    spine_mid = 2
    neck = 4
    head = 6
    shoulder_left = 8
    elbow_left = 10
    wrist_left = 12
    hand_left = 14
    shoulder_right = 16
    elbow_right = 18
    wrist_right = 20
    hand_right = 22
    hip_left = 24
    knee_left = 26
    ankle_left = 28
    foot_left = 30
    hip_right = 32
    knee_right = 34
    ankle_right = 36
    foot_right = 38
    spine_shoulder = 40
    hand_tip_left = 42
    thumb_left = 44
    hand_tip_right = 46
    thumb_right = 48


class Body:
    def __init__(self, joints):
        self.zero_mean_data = None
        self.norm_data = None
        raw = np.array(joints)
        self.joints[BodyParts.head] = [raw[BodyParts.head], raw[BodyParts.head + 1]]
        self.joints[BodyParts.neck] = [raw[BodyParts.neck], raw[BodyParts.neck + 1]]
        self.joints[BodyParts.spine_shoulder] = [raw[BodyParts.spine_shoulder], raw[BodyParts.spine_shoulder + 1]]
        self.joints[BodyParts.shoulder_right] = [raw[BodyParts.shoulder_right], raw[BodyParts.shoulder_right + 1]]
        self.joints[BodyParts.elbow_right] = [raw[BodyParts.elbow_right], raw[BodyParts.elbow_right + 1]]
        self.joints[BodyParts.wrist_right] = [raw[BodyParts.wrist_right], raw[BodyParts.wrist_right + 1]]
        self.joints[BodyParts.hand_right] = [raw[BodyParts.hand_right], raw[BodyParts.hand_right + 1]]
        self.joints[BodyParts.spine_mid] = [raw[BodyParts.spine_mid], raw[BodyParts.spine_mid + 1]]
        self.joints[BodyParts.spine_base] = [raw[BodyParts.spine_base], raw[BodyParts.spine_base + 1]]
        self.joints[BodyParts.hip_right] = [raw[BodyParts.hip_right], raw[BodyParts.hip_right + 1]]
        self.joints[BodyParts.knee_right] = [raw[BodyParts.knee_right], raw[BodyParts.knee_right + 1]]
        self.joints[BodyParts.ankle_right] = [raw[BodyParts.ankle_right], raw[BodyParts.ankle_right + 1]]
        self.joints[BodyParts.foot_right] = [raw[BodyParts.foot_right], raw[BodyParts.foot_right + 1]]
        self.joints[BodyParts.hip_left] = [raw[BodyParts.hip_left], raw[BodyParts.hip_left + 1]]
        self.joints[BodyParts.knee_left] = [raw[BodyParts.knee_left], raw[BodyParts.knee_left + 1]]
        self.joints[BodyParts.ankle_left] = [raw[BodyParts.ankle_left], raw[BodyParts.ankle_left + 1]]
        self.joints[BodyParts.foot_left] = [raw[BodyParts.foot_left], raw[BodyParts.foot_left + 1]]
        self.joints[BodyParts.shoulder_left] = [raw[BodyParts.shoulder_left], raw[BodyParts.shoulder_left + 1]]
        self.joints[BodyParts.elbow_left] = [raw[BodyParts.elbow_left], raw[BodyParts.elbow_left + 1]]
        self.joints[BodyParts.wrist_left] = [raw[BodyParts.wrist_left], raw[BodyParts.wrist_left + 1]]
        self.joints[BodyParts.hand_left] = [raw[BodyParts.hand_left], raw[BodyParts.hand_left + 1]]

    def scale(self):
        self.zero_mean_data = (self.joints - np.average(self.joint, axis=1)) / np.std(self.joints, axis=1)
        return self.zero_mean_data

        # WIP

    def normalization(self, trainer):
        coef = np.array
        # Set the head as the origin
        self.joints = self.joints - self.joints[BodyParts.head]
        trainer.joints = trainer.joints - trainer.joints[BodyParts.head]
        # Normalize the length of the limbs
        self.joints[BodyParts.neck] = rescale(self.joints[BodyParts.head], self.joints[BodyParts.neck],
                                              trainer.joints[BodyParts.head], trainer.joints[BodyParts.neck])

        self.joints[BodyParts.spine_shoulder] = rescale(self.joints[BodyParts.neck],
                                                        self.joints[BodyParts.spine_shoulder],
                                                        trainer.joints[BodyParts.neck],
                                                        trainer.joints[BodyParts.spine_shoulder])

        self.joints[BodyParts.shoulder_right] = rescale(self.joints[BodyParts.spine_shoulder],
                                                        self.joints[BodyParts.shoulder_right],
                                                        trainer.joints[BodyParts.spine_shoulder],
                                                        trainer.joints[BodyParts.shoulder_right])

        self.joints[BodyParts.elbow_right] = rescale(
            self.joints[BodyParts.shoulder_right],
            self.joints[BodyParts.elbow_right],
            trainer.joints[BodyParts.shoulder_right],
            trainer.joints[BodyParts.elbow_right])

        self.joints[BodyParts.wrist_right] = rescale(
            self.joints[BodyParts.elbow_right],
            self.joints[BodyParts.wrist_right],
            trainer.joints[BodyParts.elbow_right],
            trainer.joints[BodyParts.wrist_right])

        self.joints[BodyParts.hand_right] = rescale(
            self.joints[BodyParts.wrist_right],
            self.joints[BodyParts.hand_right],
            trainer.joints[BodyParts.wrist_right],
            trainer.joints[BodyParts.hand_right])

        self.joints[BodyParts.shoulder_left] = rescale(self.joints[BodyParts.spine_shoulder],
                                                       self.joints[BodyParts.shoulder_left],
                                                       trainer.joints[BodyParts.spine_shoulder],
                                                       trainer.joints[BodyParts.shoulder_left])

        self.joints[BodyParts.elbow_left] = rescale(
            self.joints[BodyParts.shoulder_left],
            self.joints[BodyParts.elbow_left],
            trainer.joints[BodyParts.shoulder_left],
            trainer.joints[BodyParts.elbow_left])

        self.joints[BodyParts.wrist_left] = rescale(
            self.joints[BodyParts.elbow_left],
            self.joints[BodyParts.wrist_left],
            trainer.joints[BodyParts.elbow_left],
            trainer.joints[BodyParts.wrist_left])

        self.joints[BodyParts.hand_left] = rescale(
            self.joints[BodyParts.wrist_left],
            self.joints[BodyParts.hand_left],
            trainer.joints[BodyParts.wrist_left],
            trainer.joints[BodyParts.hand_left])

        self.joints[BodyParts.spine_mid] = rescale(self.joints[BodyParts.spine_shoulder],
                                                   self.joints[BodyParts.spine_mid],
                                                   trainer.joints[BodyParts.spine_shoulder],
                                                   trainer.joints[BodyParts.spine_mid])

        self.joints[BodyParts.spine_base] = rescale(self.joints[BodyParts.spine_mid],
                                                    self.joints[BodyParts.spine_base],
                                                    trainer.joints[BodyParts.spine_mid],
                                                    trainer.joints[BodyParts.spine_base])

        self.joints[BodyParts.hip_right] = rescale(self.joints[BodyParts.spine_base],
                                                   self.joints[BodyParts.hip_right],
                                                   trainer.joints[BodyParts.spine_base],
                                                   trainer.joints[BodyParts.hip_right])

        self.joints[BodyParts.knee_right] = rescale(
            self.joints[BodyParts.hip_right],
            self.joints[BodyParts.knee_right],
            trainer.joints[BodyParts.hip_right],
            trainer.joints[BodyParts.knee_right])

        self.joints[BodyParts.ankle_right] = rescale(
            self.joints[BodyParts.knee_right],
            self.joints[BodyParts.ankle_right],
            trainer.joints[BodyParts.knee_right],
            trainer.joints[BodyParts.ankle_right])

        self.joints[BodyParts.foot_right] = rescale(
            self.joints[BodyParts.ankle_right],
            self.joints[BodyParts.foot_right],
            trainer.joints[BodyParts.ankle_right],
            trainer.joints[BodyParts.foot_right])

        self.joints[BodyParts.hip_left] = rescale(self.joints[BodyParts.spine_base],
                                                  self.joints[BodyParts.hip_left],
                                                  trainer.joints[BodyParts.spine_base],
                                                  trainer.joints[BodyParts.hip_left])

        self.joints[BodyParts.knee_left] = rescale(
            self.joints[BodyParts.hip_left],
            self.joints[BodyParts.knee_left],
            trainer.joints[BodyParts.hip_left],
            trainer.joints[BodyParts.knee_left])

        self.joints[BodyParts.ankle_left] = rescale(
            self.joints[BodyParts.knee_left],
            self.joints[BodyParts.ankle_left],
            trainer.joints[BodyParts.knee_left],
            trainer.joints[BodyParts.ankle_left])

        self.joints[BodyParts.foot_left] = rescale(
            self.joints[BodyParts.ankle_left],
            self.joints[BodyParts.foot_left],
            trainer.joints[BodyParts.ankle_left],
            trainer.joints[BodyParts.foot_left])
        return self.joints, trainer.joints


# distance between 2 points = sqrt((x0 - x1)^2 + (y0 - y1)^2)
def length(j1, j2):
    return math.sqrt((j1[0] - j2[0]) ** 2 + (j1[1] - j2[1]) ** 2)


def coefficient(user_joint1, user_joint2, trainer_joint1, trainer_joint2):
    return np.absolute(length(user_joint1, user_joint2) - length(trainer_joint1, trainer_joint2)) / length(
        trainer_joint1, trainer_joint2)


def rescale(user_origin_joint, user_target_joint, trainer_origin_joint, trainer_target_joint):
    coeff = coefficient(user_origin_joint, user_target_joint, trainer_origin_joint, trainer_target_joint)
    return (1 - coeff) * user_origin_joint + coeff * user_target_joint


def scoring(user, trainer):
    score = 100
    sampling_rate = 10
    window_weight = 100 / round(np.shape(user)[1] / sampling_rate)
    acceptable_error = 0.05
    diff = np.absolute(user - trainer)
    for i in round(np.shape(user)[1] / sampling_rate):
        diff_sample = np.average(user[:][i: sampling_rate * i])
        score -= np.amax(diff_sample, axis=1) * window_weight * (1 - acceptable_error)
    return score


def get_data(url1):
    user_data = pd.read_csv(url1)

    user_skeleton = user_data[[col for col in user_data.columns if col.startswith('PositionX')
                               or col.startswith('PositionY')]]
    return user_skeleton


if __name__ == '__main__':
    url1 = "https://raw.githubusercontent.com/ramzes-hk/datadump/main/capstone_sample%20-%20bicep_curl_test_Skeleton.csv"
    url2 = "https://raw.githubusercontent.com/ramzes-hk/datadump/main/20230215_190706_00_Skeleton.txt"
    user, trainer = Body(get_data(url1)).normalization(Body(get_data(url2)))
    print(scoring(user, trainer))
