import unittest

from navigation import CELL_LENGTH_METERS, GoalDetector, goal_cell, is_goal_reached


class NavigationTests(unittest.TestCase):
    def test_targets_for_all_supported_mazes(self):
        for columns in (4, 8, 12):
            with self.subTest(columns=columns):
                self.assertEqual((0, columns - 1), goal_cell(4, columns))
        self.assertEqual(0.18, CELL_LENGTH_METERS)

    def test_every_cell_in_every_maze(self):
        for columns in (4, 8, 12):
            for row in range(4):
                for column in range(columns):
                    with self.subTest(columns=columns, position=(row, column)):
                        self.assertEqual(row == 0 and column == columns - 1,
                                         is_goal_reached(4, columns, (row, column)))

    def test_rejects_unsupported_and_non_integer_dimensions(self):
        for rows, columns in ((8, 8), (4, 5), (0, 4), (4, 0), (4.0, 4), (True, 4)):
            with self.subTest(dimensions=(rows, columns)):
                with self.assertRaises(ValueError):
                    goal_cell(rows, columns)
                with self.assertRaises(ValueError):
                    GoalDetector(rows, columns)

    def test_rejects_invalid_positions(self):
        for position in ((-1, 0), (4, 0), (0, -1), (0, 4), (0.0, 3),
                         (False, 3), (), (0,), (0, 3, 1), None, '03'):
            with self.subTest(position=position):
                with self.assertRaises(ValueError):
                    is_goal_reached(4, 4, position)

    def test_signals_only_first_arrival_and_keeps_state(self):
        detector = GoalDetector(4, 4)
        self.assertFalse(detector.update((3, 0)))
        self.assertFalse(detector.reached)
        self.assertTrue(detector.update((0, 3)))
        self.assertTrue(detector.reached)
        self.assertFalse(detector.update((0, 3)))
        self.assertFalse(detector.update((0, 2)))
        self.assertFalse(detector.update((0, 3)))

    def test_new_attempt_has_independent_state(self):
        first = GoalDetector(4, 4)
        first.update((0, 3))
        second = GoalDetector(4, 4)
        self.assertFalse(second.reached)
        self.assertTrue(second.update((0, 3)))


if __name__ == '__main__':
    unittest.main()
