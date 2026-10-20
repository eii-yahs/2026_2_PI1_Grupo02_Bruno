export type MazeSize = 'GRID_4X4' | 'GRID_8X4' | 'GRID_12X4'

export type MazeDimensions = {
  label: string
  rows: number
  columns: number
}

export type Position = {
  row: number
  column: number
  heading?: string
}

export const mazeOptions: Record<MazeSize, MazeDimensions> = {
  GRID_4X4: { label: '4 × 4', rows: 4, columns: 4 },
  GRID_8X4: { label: '8 × 4', rows: 4, columns: 8 },
  GRID_12X4: { label: '12 × 4', rows: 4, columns: 12 },
}
