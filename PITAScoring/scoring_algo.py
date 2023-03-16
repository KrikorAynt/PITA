# -*- coding: utf-8 -*-
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

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


# distance between 2 points = sqrt((x0 - x1)^2 + (y0 - y1)^2)
def length(j1, j2):
    return np.sqrt(np.sum(np.power(j1 - j2, 2), axis=1))


def length_full_set(user_, trainer_):
    return np.sqrt(np.sum(np.power(user_ - trainer_, 2), axis=2))


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
    scores = np.empty(np.shape(user_)[1])
    score = 100
    sampling_rate = 1
    window_weight = 100 / round(np.shape(user_)[1] / sampling_rate)
    acceptable_error = 0.05
    user_[BodyParts.head, :, :] = np.ones([np.shape(trainer_)[1], 2])
    trainer_[BodyParts.head, :, :] = np.ones([np.shape(trainer_)[1], 2])
    diff = length_full_set(user_, trainer_)/75
    for i in range(0, np.shape(diff)[1], sampling_rate):
        score -= window_weight * np.amax(diff[:, i]) * (1 - acceptable_error)
        scores[i] = score
    return scores


def get_data(url):
    user_data = pd.read_csv(url)
    user_skeleton = user_data[[col for col in user_data.columns if col.startswith('PositionX')
                               or col.startswith('PositionY')]]
    return user_skeleton


def run(url1, url2):
    user = get_data(url1)
    trainer = get_data(url2)
    user_body = Body(user).reset_origin()
    trainer_body = Body(trainer).reset_origin()
    user_body, trainer_body = frame_matching(user_body, trainer_body)
    user_body = normalize(user_body, trainer_body)
    plt.plot(range(np.shape(user_body)[1]), scoring(user_body, trainer_body))
    plt.xlabel("Frame")
    plt.ylabel("Score")
    plt.savefig("output.jpg")
    plt.show()
    return scoring(user_body, trainer_body)


if __name__ == '__main__':
    url1 = "https://raw.githubusercontent.com/ramzes-hk/datadump/main/20230216_000032_00_Skeleton.txt"
    url2 = "https://raw.githubusercontent.com/ramzes-hk/datadump/main/20230314_025958_00_Skeleton.txt"
    print(run(url1, url2))
