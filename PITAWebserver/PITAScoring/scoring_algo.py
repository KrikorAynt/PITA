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
    sampling_rate = 5
    scores = np.array([])
    acceptable_error = 0

    window_weight = 100 / (np.shape(user_)[1] / sampling_rate)
    user_[BodyParts.head, :, :] = np.ones([np.shape(trainer_)[1], 2])
    trainer_[BodyParts.head, :, :] = np.ones([np.shape(trainer_)[1], 2])
    user_ = user_[np.r_[4:12], :, :]
    trainer_ = trainer_[np.r_[4:12], :, :]
    search_range = 120
    for i in range(0, np.shape(user_)[1], sampling_rate):
        # 4 to 12 are the indexes of shoulders, elbows, wrists and hands
        diff = np.array([mean_squared_error(trainer_[:, i, :], user_[:, j, :], squared=False) for j in
                         range(min(i + search_range, np.shape(user_)[1]))])
        for j, val in enumerate(diff):
            if val == 0:
                diff[j] = 0.1
        score = 100 - window_weight * np.sort(diff)[0] * (1 - acceptable_error)
        scores = np.append(scores, score)

    scores = (scores - np.min(scores)) / (np.max(scores) - np.min(scores)) * 100
    for i, val in enumerate(scores):
        if val == 0:
            scores = np.delete(scores, i)
    return scores, np.mean(scores)


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
    # user_body = normalize(user_body, trainer_body)
    username = "User"
    exercise = "Exercise"
    if len(sys.argv) > 3:
        username = sys.argv[2]
        exercise = sys.argv[3]
    score, final_score = pose_score(user_body, trainer_body)
    plt.scatter(range(np.shape(score)[0]), score)
    plt.title(f"{username} {exercise}\nFinal Score: {max(0, final_score):.1f}")
    plt.xlabel("Time")
    plt.ylabel("Score")
    plt.xlim(0, np.shape(score)[0])
    plt.ylim(0, 105)

    file_path = sys.argv[1]
    file_name_with_extension = os.path.basename(file_path)
    file_name, file_extension = os.path.splitext(file_name_with_extension)
    path = f"Graphs/{username}"
    if not os.path.exists(path):
        os.makedirs(path)
    path = f"Graphs/{username}/{file_name}.png"
    plt.savefig(path)
    return path


# usage: python scoring_algo.py <user_footage_dir/url> <username> <exercise>
if __name__ == '__main__':
    if len(sys.argv) < 4:
        raise ValueError("Invalid arguments")
    url1 = sys.argv[1]
    if "bicep_curl" in sys.argv[3]:
        url2 = "PITAScoring/example/sample_data/bicep_curl_ref.txt"
    elif "lat_raise" in sys.argv[3]:
        url2 = "PITAScoring/example/sample_data/lat_raise_ref.txt"
    elif "shoulder_press" in sys.argv[3]:
        url2 = "PITAScoring/example/sample_data/shoulder_press_ref.txt"
    else:
        raise ValueError("Invalid exercise")
    print(run(url1, url2))
