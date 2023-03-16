# -*- coding: utf-8 -*-
import numpy as np
import pandas as pd
import math
from enum import Enum

from PITAScoring.body_parts import BodyParts


# sample code for the scoring algorithm


class Body:
    # passed test
    def __init__(self, joints):
        self.zero_mean_data = None
        self.norm_data = None
        raw = np.array(joints)
        self.joints = np.array(joints.to_numpy().reshape(joints.shape[0], 25, 2))

    # passed test
    def reset_origin(self):
        # Set the head as the origin
        # Normalize the length of the limbs
        self.joints = np.array([self.joints[:, i, :] - self.joints[:, BodyParts.head, :] for i in range(25)])
        return self.joints


def normalize(user, trainer):
    # Normalize the length of the limbs
    for i in BodyParts.limbs:
        user[i[1], :, :] = rescale(user[i[0], :, :], user[i[1], :, :], trainer[i[0], :, :], trainer[i[1], :, :])
    return user


# passed test
# distance between 2 points = sqrt((x0 - x1)^2 + (y0 - y1)^2)
def length(j1, j2):
    return np.sqrt(np.sum(np.power(j1 - j2, 2), axis=1))


# not working
# shapes mismatch: implement frame matching
def coefficient(user_joint1, user_joint2, trainer_joint1, trainer_joint2):
    return np.divide(np.absolute(np.subtract(length(user_joint1, user_joint2), length(trainer_joint1, trainer_joint2))),
                     length(trainer_joint1, trainer_joint2))


def frame_matching(user_, trainer_):
    frame_threshold = 20
    if user_.shape[1] <= frame_threshold or trainer_.shape[1] <= frame_threshold:
        return user_, trainer_
    if user_.shape[1] > trainer_.shape[1]:
        user_ = user_[:, :trainer_.shape[1]]
    elif user_.shape[1] < trainer_.shape[1]:

        trainer_ = trainer_[:, :user_.shape[1]]
    return user_, trainer_


def rescale(user_origin_joint, user_target_joint, trainer_origin_joint, trainer_target_joint):
    coef = coefficient(user_origin_joint, user_target_joint, trainer_origin_joint, trainer_target_joint)
    coef = coef.reshape((coef.shape[0], 1))
    return np.multiply((1 - coef).reshape((coef.shape[0], 1)), user_origin_joint) + np.multiply(coef, user_target_joint)


def scoring(user_, trainer_):
    score = 100
    sampling_rate = 10
    window_weight = 100 / round(np.shape(user_)[1] / sampling_rate)
    acceptable_error = 0.05
    user_[BodyParts.head, :, :] = np.ones([np.shape(trainer_)[1], 2])
    trainer_[BodyParts.head, :, :] = np.ones([np.shape(trainer_)[1], 2])
    diff = np.absolute(user_ - trainer_)/np.fmax(np.absolute(user_), np.absolute(trainer_))
    for i in range(0, np.shape(diff)[1], sampling_rate):
        diff_sample = diff[:, i, :]
        score -= np.amax(diff_sample) * window_weight * (1 - acceptable_error)
    return score


def get_data(url):
    user_data = pd.read_csv(url)
    user_skeleton = user_data[[col for col in user_data.columns if col.startswith('PositionX')
                               or col.startswith('PositionY')]]
    return user_skeleton


if __name__ == '__main__':
    url1 = "https://raw.githubusercontent.com/ramzes-hk/datadump/main/capstone_sample%20-%20bicep_curl_test_Skeleton.csv"
    url2 = "https://raw.githubusercontent.com/ramzes-hk/datadump/main/20230215_190706_00_Skeleton.txt"
    user = get_data(url1)
    trainer = get_data(url2)
    user_body = Body(user).reset_origin()
    trainer_body = Body(trainer).reset_origin()
    user_body, trainer_body = frame_matching(user_body, trainer_body)
    user_body = normalize(user_body, trainer_body)
    print(scoring(user_body, trainer_body))
