# -*- coding: utf-8 -*-
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import sys
import os
from sklearn.metrics import mean_squared_error


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


class Bicep:
    spine_base = 0  # 0
    spine_mid = 1  # 1
    neck = 2  # 2
    head = 3  # 3
    shoulder_left = 4  # 4
    elbow_left = 5  # 5
    wrist_left = 6  # 6
    hand_left = 7  # 7
    shoulder_right = 8  # 8
    elbow_right = 9  # 9
    wrist_right = 10  # 10
    hand_right = 11  # 11
    hip_left = 12  # 12

    hip_right = 16  # 13
    spine_shoulder = 20  # 14
    parts = [spine_base, spine_mid, neck, head, shoulder_left, elbow_left, wrist_left, hand_left, shoulder_right,
             elbow_right, wrist_right, hand_right, hip_left, hip_right, spine_shoulder]
    limbs = [[head, neck], [neck, 14], [14, shoulder_right], [shoulder_right, elbow_right],
             [elbow_right, wrist_right], [wrist_right, hand_right], [14, shoulder_left],
             [shoulder_left, elbow_left], [elbow_left, wrist_left], [wrist_left, hand_left],
             [14, spine_mid], [spine_mid, spine_base], [spine_base, 13],
             [spine_base, hip_left]]


class Body:
    def __init__(self, joints):
        self.joints = np.array(joints.to_numpy().reshape(joints.shape[0], 25, 2))

    def reset_origin(self):
        # Set the head as the origin
        self.joints = np.array([self.joints[:, i, :] - self.joints[:, BodyParts.head, :] for i in range(25)])
        return self.joints


def get_bicep(data):
    for i in reversed([j for j in range(data.shape[0]) if j not in Bicep.parts]):
        data = np.delete(data, i, 0)
    return data


def normalize(user, trainer):
    # Normalize the length of the limbs
    for i in Bicep.limbs:
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


def pose_score(user_, trainer_):
    score = 100
    sampling_rate = 10
    scores = np.array([])
    acceptable_error = 0.1

    window_weight = 100/(np.shape(user_)[1] / sampling_rate)
    user_[BodyParts.head, :, :] = np.ones([np.shape(trainer_)[1], 2])
    trainer_[BodyParts.head, :, :] = np.ones([np.shape(trainer_)[1], 2])
    for i in range(0, np.shape(user_)[1], sampling_rate):
        # 4 to 12 are the indexes of shoulders, elbows, wrists and hands
        score -= window_weight * mean_squared_error(trainer_[4:12, i, :], user_[4:12, i, :]) * (1 - acceptable_error)/4
        scores = np.append(scores, score)
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


def run(user_ref, trainer_ref):
    user = get_data(user_ref)
    trainer = get_data(trainer_ref)
    start_frame = find_start_exercise(user, trainer)
    user = user.iloc[start_frame:, :]
    trainer = trainer.iloc[start_frame:, :]
    user_body = Body(user).reset_origin()
    trainer_body = Body(trainer).reset_origin()
    user_body, trainer_body = frame_matching(user_body, trainer_body)
    user_body = get_bicep(user_body)
    trainer_body = get_bicep(trainer_body)

    # plt.scatter(user_body[:, 0, 0], user_body[:, 0, 1], label="non-normalized")

    user_body = normalize(user_body, trainer_body)

    # plt.scatter(trainer_body[:, 0, 0], trainer_body[:, 0, 1], label="trainer")
    # plt.scatter(user_body[:, 0, 0], user_body[:, 0, 1], label="normalized")
    # plt.legend()
    # plt.show()
    username = "User"
    exercise = "Exercise"
    if len(sys.argv) > 3:
        username = sys.argv[2]
        exercise = sys.argv[3]
    score = pose_score(user_body, trainer_body)
    plt.plot(range(np.shape(score)[0]), score)
    plt.title(f"{username}\n{exercise}")
    plt.xlabel("Time")
    plt.ylabel("Score")
    plt.xlim(0, np.shape(score)[0])
    plt.ylim(0, 105)

    path = f"Graphs/{username}"
    if not os.path.exists(path):
        os.makedirs(path)
    path = f"Graphs/{username}/{exercise}.png"
    plt.savefig(path)
    return path


# usage: python scoring_algo.py <user_footage_dir/url> <username> <exercise>
if __name__ == '__main__':
    url1 = ""
    if len(sys.argv) > 3:
        url1 = sys.argv[1]
    url2 = "example/sample_data/BicepRefernce.txt"
    if "bicep" in sys.argv[3]:
        url2 = "example/sample_data/BicepRefernce.txt"
    elif "crunches" in sys.argv[3]:
        url2 = "example/sample_data/reference_Skeleton.txt"
    print(run(url1, url2))
