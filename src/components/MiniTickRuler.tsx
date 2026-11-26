import React, {useRef, useState} from 'react';

interface MiniTickRulerProps {
  /** Value tracked along the center mini-ruler (in kilometers) */
  valueKm: number;
  onChangeKm: (nextKm: number) => void;
}

const PX_PER_KM = 5; // 50px between major 10-km ticks, matching the compact widget ruler

export const MiniTickRuler: React.FC<MiniTickRulerProps> = ({
  valueKm,
  onChangeKm,
}) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const [dragging, setDragging] = useState(false);

  const dragRef = useRef<{
    active: boolean;
    pointerId: number | null;
    startX: number;
    startKm: number;
  }>({
    active: false,
    pointerId: null,
    startX: 0,
    startKm: 0,
  });

  const handlePointerDown = (e: React.PointerEvent<HTMLDivElement>) => {
    if (e.button !== 0 && e.pointerType === 'mouse') return;
    e.currentTarget.setPointerCapture(e.pointerId);
    e.currentTarget.focus();
    dragRef.current = {
      active: true,
      pointerId: e.pointerId,
      startX: e.clientX,
      startKm: valueKm,
    };
    setDragging(true);
  };

  const handlePointerMove = (e: React.PointerEvent<HTMLDivElement>) => {
    const state = dragRef.current;
    if (!state.active || state.pointerId !== e.pointerId) return;
    const dx = e.clientX - state.startX;
    const next = Math.max(0, Math.round((state.startKm - dx / PX_PER_KM) * 10) / 10);
    onChangeKm(next);
  };

  const handlePointerUp = (e: React.PointerEvent<HTMLDivElement>) => {
    const state = dragRef.current;
    if (!state.active || state.pointerId !== e.pointerId) return;
    state.active = false;
    state.pointerId = null;
    setDragging(false);
    if (e.currentTarget.hasPointerCapture(e.pointerId)) {
      e.currentTarget.releasePointerCapture(e.pointerId);
    }
  };

  const handleWheel = (e: React.WheelEvent<HTMLDivElement>) => {
    e.preventDefault();
    const delta = Math.abs(e.deltaX) > Math.abs(e.deltaY) ? e.deltaX : e.deltaY;
    const step = delta > 0 ? 0.5 : -0.5;
    onChangeKm(Math.max(0, Math.round((valueKm + step) * 10) / 10));
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLDivElement>) => {
    const step = e.shiftKey ? 5 : 0.5;
    if (e.key === 'ArrowRight' || e.key === 'ArrowUp') {
      e.preventDefault();
      onChangeKm(Math.round((valueKm + step) * 10) / 10);
    } else if (e.key === 'ArrowLeft' || e.key === 'ArrowDown') {
      e.preventDefault();
      onChangeKm(Math.max(0, Math.round((valueKm - step) * 10) / 10));
    }
  };

  // Render integer ticks around the current km value
  const minTick = Math.max(0, Math.floor(valueKm) - 14);
  const maxTick = Math.floor(valueKm) + 14;
  const ticks: number[] = [];
  for (let t = minTick; t <= maxTick; t++) {
    ticks.push(t);
  }

  return (
    <div
      ref={containerRef}
      role="slider"
      tabIndex={0}
      aria-label="Slide to adjust kilometers"
      aria-valuemin={0}
      aria-valuenow={Number(valueKm.toFixed(1))}
      onPointerDown={handlePointerDown}
      onPointerMove={handlePointerMove}
      onPointerUp={handlePointerUp}
      onPointerCancel={handlePointerUp}
      onWheel={handleWheel}
      onKeyDown={handleKeyDown}
      className={`relative h-11 w-20 sm:w-28 shrink-0 overflow-hidden select-none touch-none rounded-lg focus:outline-none focus-visible:ring-2 focus-visible:ring-sky-400/70 ${
        dragging ? 'cursor-grabbing' : 'cursor-grab'
      }`}
      style={{
        maskImage:
          'linear-gradient(to right, transparent 0%, black 22%, black 78%, transparent 100%)',
        WebkitMaskImage:
          'linear-gradient(to right, transparent 0%, black 22%, black 78%, transparent 100%)',
      }}
    >
      {/* Ticks strip centered at 50% */}
      <div className="pointer-events-none absolute inset-0 left-1/2">
        {ticks.map((tick) => {
          const offsetPx = (tick - valueKm) * PX_PER_KM;
          const isMajor = tick % 10 === 0;
          const isMedium = !isMajor && tick % 5 === 0;

          return (
            <div
              key={tick}
              style={{transform: `translate3d(${offsetPx}px, 0, 0)`}}
              className="absolute bottom-1 left-0 -translate-x-1/2 flex flex-col items-center"
            >
              {isMajor && (
                <span className="mb-0.5 text-[9px] leading-none tabular-nums text-slate-400 dark:text-[#6B768A]">
                  {tick}
                </span>
              )}
              <div
                className={
                  isMajor
                    ? 'w-[1px] h-4 bg-slate-500 dark:bg-[#5E687B]'
                    : isMedium
                      ? 'w-[1px] h-2.5 bg-slate-400 dark:bg-[#414A5C]'
                      : 'w-[1px] h-1.5 bg-slate-300 dark:bg-[#2A3140]'
                }
              />
            </div>
          );
        })}
      </div>

      {/* Subtle center hairline needle */}
      <div
        aria-hidden="true"
        className="pointer-events-none absolute top-1 bottom-1 left-1/2 -translate-x-1/2 w-[1.5px] bg-sky-500/80 dark:bg-[#5A6A82]/90"
      />
    </div>
  );
};
