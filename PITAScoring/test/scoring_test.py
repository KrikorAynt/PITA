import unittest
import PITAScoring.scoring_algo
import PITAScoring.body_parts
import numpy as np


class TestScoring(unittest.TestCase):
    def test_body_parts(self):
        self.assertEqual(PITAScoring.scoring_algo.length(np.array([[2, 0], [3, 0]]), np.array([[0, 0], [0, 0]])), np.array([[2],[3]]))
