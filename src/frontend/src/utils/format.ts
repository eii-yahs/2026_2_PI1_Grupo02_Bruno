export function formatNumber(value: number | undefined, digits = 2) {
  if (value == null || !Number.isFinite(value)) return '—'

  return value.toFixed(digits)
}

export function formatElapsedTime(seconds: number, started: boolean) {
  if (!started) return '--:--'

  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60

  return `${String(minutes).padStart(2, '0')}:${String(remainingSeconds).padStart(2, '0')}`
}
