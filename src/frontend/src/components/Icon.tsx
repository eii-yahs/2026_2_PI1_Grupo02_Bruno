import {
  AlertTriangle,
  Layers,
  Play,
  RefreshCw,
  Route,
  Square,
  Gauge,
  Clock3,
  type LucideIcon,
} from 'lucide-react'

type IconName = 'layers' | 'play' | 'stop' | 'refresh' | 'route' | 'speed' | 'time' | 'alert'

const icons: Record<IconName, LucideIcon> = {
  layers: Layers,
  play: Play,
  stop: Square,
  refresh: RefreshCw,
  route: Route,
  speed: Gauge,
  time: Clock3,
  alert: AlertTriangle,
}

export function Icon({ name }: { name: IconName }) {
  const IconComponent = icons[name]
  return <IconComponent className="block size-[15px] shrink-0" aria-hidden="true" />
}
