import argparse
import threading
import unittest
from unittest.mock import patch

from navigation import GoalDetector
from simulator import MAZES, MicromouseSimulator, serpentine_path


class FakeClock:
    def __init__(self):
        self.now = 0.0

    def monotonic(self):
        return self.now

    def wait(self, delay):
        self.now += max(0, delay)
        return False

    def is_set(self):
        return False


class GoalDetectionTests(unittest.TestCase):
    def simulator_without_broker(self):
        simulator = MicromouseSimulator.__new__(MicromouseSimulator)
        simulator.args = argparse.Namespace(sample_interval=0.1, cell_length=0.18, speed=0.35)
        simulator.prefix = 'ratocego'
        simulator.lock = threading.Lock()
        simulator.event_id = 1
        return simulator

    def test_route_signals_target_once_for_each_maze(self):
        simulator = self.simulator_without_broker()
        for maze, (rows, columns) in MAZES.items():
            run = {'runId': 42, 'mazeType': maze, 'goalDetector': GoalDetector(rows, columns)}
            with patch('builtins.print') as output:
                signals = [simulator.identify_goal(run, position)
                           for position in serpentine_path(rows, columns)]
                self.assertEqual(1, sum(signals))
                output.assert_called_once()
                self.assertIn(f'posicao=(0, {columns - 1})', output.call_args.args[0])
            self.assertTrue(run['goalDetector'].reached)

    def test_detection_does_not_publish_or_stop_run(self):
        simulator = self.simulator_without_broker()
        run = {'runId': 42, 'mazeType': 'GRID_4X4',
               'goalDetector': GoalDetector(4, 4), 'stop': threading.Event()}
        with patch.object(simulator, 'publish') as publish, patch('builtins.print'):
            self.assertTrue(simulator.identify_goal(run, (0, 3)))
            self.assertFalse(simulator.identify_goal(run, (0, 3)))
            publish.assert_not_called()
        self.assertFalse(run['stop'].is_set())

    def test_production_loop_identifies_and_resets_state_for_new_runs(self):
        simulator = self.simulator_without_broker()
        for run_id, maze in enumerate(MAZES, start=1):
            clock = FakeClock()
            rows, columns = MAZES[maze]
            stale = GoalDetector(rows, columns)
            stale.update((0, columns - 1))
            run = {'runId': run_id, 'mazeType': maze, 'stop': clock, 'goalDetector': stale}
            simulator.active_run = run
            with patch('simulator.time.monotonic', clock.monotonic), \
                    patch.object(simulator, 'publish', return_value=True), \
                    patch('builtins.print') as output:
                simulator.run_maze(run)
            self.assertIsNot(run['goalDetector'], stale)
            self.assertTrue(run['goalDetector'].reached)
            arrivals = [call for call in output.call_args_list
                        if 'objetivo identificado' in call.args[0]]
            self.assertEqual(1, len(arrivals))
            self.assertIsNone(simulator.active_run)


if __name__ == '__main__':
    unittest.main()
