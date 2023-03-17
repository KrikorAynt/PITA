# -*- coding: utf-8 -*-
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import sys


# sample code for the scoring algorithm

class BodyParts:
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
    hand_tip_right = 23
    thumb_right = 24
    limbs = [[head, neck], [neck, spine_shoulder], [spine_shoulder, shoulder_right], [shoulder_right, elbow_right],
             [elbow_right, wrist_right], [wrist_right, hand_right], [spine_shoulder, shoulder_left],
             [shoulder_left, elbow_left], [elbow_left, wrist_left], [wrist_left, hand_left],
             [spine_shoulder, spine_mid], [spine_mid, spine_base], [spine_base, hip_right], [hip_right, knee_right],
             [knee_right, ankle_right], [spine_base, hip_left], [hip_left, knee_left], [knee_left, ankle_left]]


class Body:
    def __init__(self, joints):
        self.zero_mean_data = None
        self.norm_data = None
        raw = np.array(joints)
        self.joints = np.array(joints.to_numpy().reshape(joints.shape[0], 25, 2))

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
    acceptable_error = 0.1
    user_[BodyParts.head, :, :] = np.ones([np.shape(trainer_)[1], 2])
    trainer_[BodyParts.head, :, :] = np.ones([np.shape(trainer_)[1], 2])
    diff = length_full_set(user_, trainer_) / 75
    for i in range(0, np.shape(diff)[1], sampling_rate):
        score -= window_weight * np.amax(diff[:, i]) * (1 - acceptable_error)
        scores[i] = score
    return scores


def find_start_exercise(user, trainer):
    # Set a threshold for how similar the positions need to be
    similarity_threshold = 0.09

    # Get the body parts data for the two users
    user_data = Body(user).reset_origin()
    trainer_data = Body(trainer).reset_origin()

    # Make sure the two data sets have the same number of frames
    user_data, trainer_data = frame_matching(user_data, trainer_data)

    # Compare the positions of each body part at each frame
    similarities = []
    for i in range(user_data.shape[1]):
        similarities.append(np.mean(np.equal(user_data[:, i, :], trainer_data[:, i, :])))

    # Find the first frame where the positions are similar enough
    for i in range(len(similarities)):
        if similarities[i] >= similarity_threshold:
            return i

    # If no similar positions are found, return None


def get_data(url):
    user_data = pd.read_csv(url)
    user_skeleton = user_data[[col for col in user_data.columns if col.startswith('PositionX')
                               or col.startswith('PositionY')]]
    return user_skeleton


def run(url1, url2):
    user = get_data(url1)
    trainer = get_data(url2)
    start_frame = find_start_exercise(user, trainer)
    user = user.iloc[start_frame:, :]
    trainer = trainer.iloc[start_frame:, :]
    user_body = Body(user).reset_origin()
    trainer_body = Body(trainer).reset_origin()
    user_body, trainer_body = frame_matching(user_body, trainer_body)
    user_body = normalize(user_body, trainer_body)
    username = "User"
    exercise = "Exercise"
    if len(sys.argv) > 1:
        username = sys.argv[1]
        exercise = sys.argv[2]
    plt.plot(range(np.shape(user_body)[1]), scoring(user_body, trainer_body))
    plt.title(f"{username}\n{exercise}")
    plt.xlabel("Frame")
    plt.ylabel("Score")
    plt.savefig("output.jpg")
    plt.xlim(0, np.shape(user_body)[1])
    plt.ylim(0, 105)
    plt.show()
    return scoring(user_body, trainer_body)


if __name__ == '__main__':
    url1 = "https://raw.githubusercontent.com/ramzes-hk/datadump/main/BicepRefernce.csv"
    url2 = "https://raw.githubusercontent.com/ramzes-hk/datadump/main/Bicep2.csv"
    print(run(url1, url2))
