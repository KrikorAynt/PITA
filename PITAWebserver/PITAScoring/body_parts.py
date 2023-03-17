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
