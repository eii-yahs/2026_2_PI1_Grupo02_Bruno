import { Bot } from 'lucide-react'

const headingDegrees: Record<string, number> = { NORTH: 0, EAST: 90, SOUTH: 180, WEST: 270 }

export function MicromouseIcon({ heading = 'NORTH' }: { heading?: string }) {
  const angle = headingDegrees[heading] ?? 0

  return (
    <Bot
      className="block size-full overflow-visible stroke-2 text-amber transition-transform duration-100"
      style={{ transform: `rotate(${angle}deg)` }}
      aria-hidden="true"
    />
  )
}
