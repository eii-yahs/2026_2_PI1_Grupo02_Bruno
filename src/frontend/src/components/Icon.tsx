type IconName = 'layers' | 'play' | 'stop' | 'refresh' | 'route' | 'speed' | 'time' | 'alert'

const paths: Record<IconName, string> = {
  layers: 'M12 3 3 8l9 5 9-5-9-5Zm-9 9 9 5 9-5M3 16l9 5 9-5',
  play: 'm8 5 12 7-12 7V5Z',
  stop: 'M7 7h10v10H7z',
  refresh: 'M20 7v5h-5M4 17v-5h5m10-2a7 7 0 0 0-12-3L4 12m16 0-3 5a7 7 0 0 1-12-3',
  route: 'M6 19a2 2 0 1 0 0-4 2 2 0 0 0 0 4Zm12-10a2 2 0 1 0 0-4 2 2 0 0 0 0 4ZM8 17h4a3 3 0 0 0 3-3V9',
  speed: 'M3 14a9 9 0 1 1 18 0M12 14l4-5M5 18h14',
  time: 'M12 7v5l3 2m6-2a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z',
  alert: 'M12 9v4m0 4h.01M10 3h4l8 16H2l8-16Z',
}

export function Icon({ name }: { name: IconName }) {
  return <svg className="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true"><path d={paths[name]}/></svg>
}
