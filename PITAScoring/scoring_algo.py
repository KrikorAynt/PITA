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

        # self.joints[BodyParts.neck] = rescale(
        #     self.joints[BodyParts.head],
        #     self.joints[BodyParts.neck],
        #     trainer.joints[BodyParts.head],
        #     trainer.joints[BodyParts.neck])
        #
        # self.joints[BodyParts.spine_shoulder] = rescale(self.joints[BodyParts.neck],
        #                                                 self.joints[BodyParts.spine_shoulder],
        #                                                 trainer.joints[BodyParts.neck],
        #                                                 trainer.joints[BodyParts.spine_shoulder])
        #
        # self.joints[BodyParts.shoulder_right] = rescale(self.joints[BodyParts.spine_shoulder],
        #                                                 self.joints[BodyParts.shoulder_right],
        #                                                 trainer.joints[BodyParts.spine_shoulder],
        #                                                 trainer.joints[BodyParts.shoulder_right])
        #
        # self.joints[BodyParts.elbow_right] = rescale(
        #     self.joints[BodyParts.shoulder_right],
        #     self.joints[BodyParts.elbow_right],
        #     trainer.joints[BodyParts.shoulder_right],
        #     trainer.joints[BodyParts.elbow_right])
        #
        # self.joints[BodyParts.wrist_right] = rescale(
        #     self.joints[BodyParts.elbow_right],
        #     self.joints[BodyParts.wrist_right],
        #     trainer.joints[BodyParts.elbow_right],
        #     trainer.joints[BodyParts.wrist_right])
        #
        # self.joints[BodyParts.hand_right] = rescale(
        #     self.joints[BodyParts.wrist_right],
        #     self.joints[BodyParts.hand_right],
        #     trainer.joints[BodyParts.wrist_right],
        #     trainer.joints[BodyParts.hand_right])
        #
        # self.joints[BodyParts.shoulder_left] = rescale(self.joints[BodyParts.spine_shoulder],
        #                                                self.joints[BodyParts.shoulder_left],
        #                                                trainer.joints[BodyParts.spine_shoulder],
        #                                                trainer.joints[BodyParts.shoulder_left])
        #
        # self.joints[BodyParts.elbow_left] = rescale(
        #     self.joints[BodyParts.shoulder_left],
        #     self.joints[BodyParts.elbow_left],
        #     trainer.joints[BodyParts.shoulder_left],
        #     trainer.joints[BodyParts.elbow_left])
        #
        # self.joints[BodyParts.wrist_left] = rescale(
        #     self.joints[BodyParts.elbow_left],
        #     self.joints[BodyParts.wrist_left],
        #     trainer.joints[BodyParts.elbow_left],
        #     trainer.joints[BodyParts.wrist_left])
        #
        # self.joints[BodyParts.hand_left] = rescale(
        #     self.joints[BodyParts.wrist_left],
        #     self.joints[BodyParts.hand_left],
        #     trainer.joints[BodyParts.wrist_left],
        #     trainer.joints[BodyParts.hand_left])
        #
        # self.joints[BodyParts.spine_mid] = rescale(self.joints[BodyParts.spine_shoulder],
        #                                            self.joints[BodyParts.spine_mid],
        #                                            trainer.joints[BodyParts.spine_shoulder],
        #                                            trainer.joints[BodyParts.spine_mid])
        #
        # self.joints[BodyParts.spine_base] = rescale(self.joints[BodyParts.spine_mid],
        #                                             self.joints[BodyParts.spine_base],
        #                                             trainer.joints[BodyParts.spine_mid],
        #                                             trainer.joints[BodyParts.spine_base])
        #
        # self.joints[BodyParts.hip_right] = rescale(self.joints[BodyParts.spine_base],
        #                                            self.joints[BodyParts.hip_right],
        #                                            trainer.joints[BodyParts.spine_base],
        #                                            trainer.joints[BodyParts.hip_right])
        #
        # self.joints[BodyParts.knee_right] = rescale(
        #     self.joints[BodyParts.hip_right],
        #     self.joints[BodyParts.knee_right],
        #     trainer.joints[BodyParts.hip_right],
        #     trainer.joints[BodyParts.knee_right])
        #
        # self.joints[BodyParts.ankle_right] = rescale(
        #     self.joints[BodyParts.knee_right],
        #     self.joints[BodyParts.ankle_right],
        #     trainer.joints[BodyParts.knee_right],
        #     trainer.joints[BodyParts.ankle_right])
        #
        # self.joints[BodyParts.foot_right] = rescale(
        #     self.joints[BodyParts.ankle_right],
        #     self.joints[BodyParts.foot_right],
        #     trainer.joints[BodyParts.ankle_right],
        #     trainer.joints[BodyParts.foot_right])
        #
        # self.joints[BodyParts.hip_left] = rescale(self.joints[BodyParts.spine_base],
        #                                           self.joints[BodyParts.hip_left],
        #                                           trainer.joints[BodyParts.spine_base],
        #                                           trainer.joints[BodyParts.hip_left])
        #
        # self.joints[BodyParts.knee_left] = rescale(
        #     self.joints[BodyParts.hip_left],
        #     self.joints[BodyParts.knee_left],
        #     trainer.joints[BodyParts.hip_left],
        #     trainer.joints[BodyParts.knee_left])
        #
        # self.joints[BodyParts.ankle_left] = rescale(
        #     self.joints[BodyParts.knee_left],
        #     self.joints[BodyParts.ankle_left],
        #     trainer.joints[BodyParts.knee_left],
        #     trainer.joints[BodyParts.ankle_left])
        #
        # self.joints[BodyParts.foot_left] = rescale(
        #     self.joints[BodyParts.ankle_left],
        #     self.joints[BodyParts.foot_left],
        #     trainer.joints[BodyParts.ankle_left],
        #     trainer.joints[BodyParts.foot_left])
        # return self.joints, trainer.joints


# passed test
# distance between 2 points = sqrt((x0 - x1)^2 + (y0 - y1)^2)
def length(j1, j2):
    return np.sqrt(np.sum(np.power(j1 - j2, 2), axis=1))


# not working
# shapes mismatch: implement frame matching
def coefficient(user_joint1, user_joint2, trainer_joint1, trainer_joint2):
    return np.absolute(length(user_joint1, user_joint2) - length(trainer_joint1, trainer_joint2)) / length(
        trainer_joint1, trainer_joint2)


def frame_matching(user, trainer):
    frame_threshold = 20
    if user.shape[1] <= frame_threshold or trainer.shape[1] <= frame_threshold:
        return user, trainer


def rescale(user_origin_joint, user_target_joint, trainer_origin_joint, trainer_target_joint):
    coef = coefficient(user_origin_joint, user_target_joint, trainer_origin_joint, trainer_target_joint)
    return (1 - coef) * user_origin_joint + coef * user_target_joint


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
    print(coefficient(user_body[BodyParts.hand_right], user_body[BodyParts.wrist_right],
                      trainer_body[BodyParts.hand_right], trainer_body[BodyParts.wrist_right]))
