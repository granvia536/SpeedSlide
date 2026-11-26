export const MI_TO_KM = 1.609344;
export const KM_TO_MI = 1 / MI_TO_KM; // 0.621371192237334

export function kmToMiles(km: number): number {
  return km * KM_TO_MI;
}

export function milesToKm(miles: number): number {
  return miles * MI_TO_KM;
}

export function formatOneDecimal(value: number): string {
  if (!Number.isFinite(value)) return '0.0';
  const rounded = Math.round(value * 10) / 10;
  if (Object.is(rounded, -0) || rounded === 0) return '0.0';
  return rounded.toFixed(1);
}
