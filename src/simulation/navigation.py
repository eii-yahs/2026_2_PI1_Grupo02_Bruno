"""HU24: identify the opposite corner from the fixed start (3, 0).

Coordinates are zero-based (row, column). Physical cells measure 0.18 m.
This module does not stop a run or publish MQTT results (HU32).
"""

from dataclasses import dataclass

CELL_LENGTH_METERS = 0.18


def goal_cell(rows: int, columns: int) -> tuple[int, int]:
    """Return the upper-right target for each supported maze."""
    if type(rows) is not int or type(columns) is not int:
        raise ValueError("Maze dimensions must be integers")
    if rows != 4 or columns not in (4, 8, 12):
        raise ValueError("Supported dimensions are 4x4, 4x8 and 4x12 (rows x columns)")
    return 0, columns - 1


def is_goal_reached(rows: int, columns: int, position: tuple[int, int]) -> bool:
    """Reject invalid positions; compare valid positions with the target."""
    target = goal_cell(rows, columns)
    if not isinstance(position, (tuple, list)) or len(position) != 2:
        raise ValueError("Position must contain row and column")
    row, column = position
    if type(row) is not int or type(column) is not int:
        raise ValueError("Position coordinates must be integers")
    if not (0 <= row < rows and 0 <= column < columns):
        raise ValueError("Position outside maze dimensions")
    return (row, column) == target


@dataclass
class GoalDetector:
    """One instance per run: update returns True only on first arrival.

    reached remains True after arrival, even if later positions leave the target.
    HU32 can consume this state or the first-arrival return value.
    """

    rows: int
    columns: int
    reached: bool = False

    def __post_init__(self) -> None:
        goal_cell(self.rows, self.columns)

    def update(self, position: tuple[int, int]) -> bool:
        at_goal = is_goal_reached(self.rows, self.columns, position)
        if at_goal and not self.reached:
            self.reached = True
            return True
        return False
