const headingDegrees: Record<string, number> = { NORTH: 0, EAST: 90, SOUTH: 180, WEST: 270 }

export function MicromouseIcon({ heading = 'NORTH' }: { heading?: string }) {
  const angle = headingDegrees[heading] ?? 0

  return <svg className="robot-icon" viewBox="0 0 40 40" style={{ transform: `rotate(${angle}deg)` }} aria-hidden="true">
    <circle className="robot-halo" cx="20" cy="20" r="18.5"/>
    <g className="robot-wheels">
      <rect x="5.8" y="13" width="5" height="15" rx="2.5"/>
      <rect x="29.2" y="13" width="5" height="15" rx="2.5"/>
      <path d="M7.7 16v2m0 5v2m24.6-9v2m0 5v2"/>
    </g>
    <path className="robot-chassis" d="M14.7 7.6 20 4.8l5.3 2.8a5.3 5.3 0 0 1 2.8 4.7v15.2a6 6 0 0 1-6 6h-4.2a6 6 0 0 1-6-6V12.3a5.3 5.3 0 0 1 2.8-4.7Z"/>
    <path className="robot-front" d="m20 7.2 3.1 4.6h-6.2L20 7.2Z"/>
    <g className="robot-sensors">
      <circle cx="15.8" cy="14.6" r="1.6"/>
      <circle cx="24.2" cy="14.6" r="1.6"/>
    </g>
    <path className="robot-deck" d="M16 19h8v7.2a1.8 1.8 0 0 1-1.8 1.8h-4.4a1.8 1.8 0 0 1-1.8-1.8V19Z"/>
    <circle className="robot-led" cx="20" cy="22.4" r="1.2"/>
    <path className="robot-screws" d="M14.8 18.5h.1m10.2 0h.1m-10.3 9h.1m10.2 0h.1"/>
  </svg>
}
