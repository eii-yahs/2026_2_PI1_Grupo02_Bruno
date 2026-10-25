import type { Position } from './maze'

export type Telemetry = {
  position?: Position
  distanceTravelledMeters?: number
  averageSpeedMetersPerSecond?: number
  batteryVoltageVolts?: number
  batteryStatus?: string
  chargeConsumedMilliampHours?: number
  energyConsumedWattHours?: number
}

export type RunResult =
  | 'Em andamento'
  | 'Sucesso'
  | 'Interrompido'
  | 'Aguardando'
  | 'Não concluído'
  | 'Resultado indisponível'
  | 'Início solicitado'
  | 'Interrupção solicitada'
  | 'Falhou'
