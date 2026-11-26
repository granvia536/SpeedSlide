import {useEffect, useState} from 'react';
import {Minus, Moon, Plus, Sun} from 'lucide-react';
import {EnterValueModal} from './components/EnterValueModal';
import {MiniTickRuler} from './components/MiniTickRuler';
import {formatOneDecimal, kmToMiles} from './constants/conversion';

export default function App() {
  const [km, setKm] = useState<number>(10);
  const [darkMode, setDarkMode] = useState<boolean>(true);
  const [editingUnit, setEditingUnit] = useState<'km' | 'mi' | null>(null);

  useEffect(() => {
    const root = document.documentElement;
    if (darkMode) {
      root.classList.add('dark');
    } else {
      root.classList.remove('dark');
    }
  }, [darkMode]);

  const miles = kmToMiles(km);

  const handleStep = (deltaKm: number) => {
    setKm((prev) => Math.max(0, Math.round((prev + deltaKm) * 10) / 10));
  };

  return (
    <main className="min-h-screen w-full bg-[#F1F5F9] dark:bg-black flex items-center justify-center p-4 transition-colors">
      {/* Compact FarenSlide-style Kilometers <-> Miles Converter Bar */}
      <div
        className="w-full max-w-[440px] rounded-[26px] bg-white dark:bg-[#0F141F] border border-slate-200/80 dark:border-[#1A2130] shadow-lg dark:shadow-none px-3 py-3.5 sm:px-4 sm:py-4 flex items-center justify-between gap-1.5 sm:gap-2"
        aria-label="Kilometers to Miles Converter"
      >
        {/* Decrement button */}
        <button
          type="button"
          onClick={() => handleStep(-1)}
          disabled={km <= 0}
          aria-label="Decrease kilometers by 1"
          className="min-w-[36px] min-h-[44px] inline-flex items-center justify-center rounded-xl text-slate-400 hover:text-slate-800 dark:text-[#667085] dark:hover:text-[#D5DAE3] disabled:opacity-30 active:scale-90 transition-all focus:outline-none focus-visible:ring-2 focus-visible:ring-sky-400"
        >
          <Minus className="w-4 h-4 stroke-[1.75]" aria-hidden="true" />
        </button>

        {/* Left Value: Kilometers (tap to enter exact value) */}
        <button
          type="button"
          onClick={() => setEditingUnit('km')}
          aria-label={`Kilometers: ${formatOneDecimal(km)}. Tap to edit`}
          className="min-h-[44px] px-1.5 rounded-xl inline-flex items-baseline justify-center hover:bg-slate-100 dark:hover:bg-[#171F2E] transition-colors focus:outline-none focus-visible:ring-2 focus-visible:ring-sky-400 shrink-0"
        >
          <span className="text-[26px] sm:text-[28px] leading-none font-normal tabular-nums tracking-tight text-slate-800 dark:text-[#D7DCE5]">
            {formatOneDecimal(km)}
          </span>
          <span className="ml-1 text-[15px] leading-none text-slate-400 dark:text-[#667085]">
            km
          </span>
        </button>

        {/* Center Mini Slide Ruler */}
        <MiniTickRuler valueKm={km} onChangeKm={setKm} />

        {/* Right Value: Miles (tap to enter exact value) */}
        <button
          type="button"
          onClick={() => setEditingUnit('mi')}
          aria-label={`Miles: ${formatOneDecimal(miles)}. Tap to edit`}
          className="min-h-[44px] px-1.5 rounded-xl inline-flex items-baseline justify-center hover:bg-slate-100 dark:hover:bg-[#171F2E] transition-colors focus:outline-none focus-visible:ring-2 focus-visible:ring-sky-400 shrink-0"
        >
          <span className="text-[26px] sm:text-[28px] leading-none font-normal tabular-nums tracking-tight text-[#0284C7] dark:text-[#159FE0]">
            {formatOneDecimal(miles)}
          </span>
          <span className="ml-1 text-[15px] leading-none text-slate-400 dark:text-[#667085]">
            mi
          </span>
        </button>

        {/* Increment button */}
        <button
          type="button"
          onClick={() => handleStep(1)}
          aria-label="Increase kilometers by 1"
          className="min-w-[36px] min-h-[44px] inline-flex items-center justify-center rounded-xl text-slate-400 hover:text-slate-800 dark:text-[#667085] dark:hover:text-[#D5DAE3] active:scale-90 transition-all focus:outline-none focus-visible:ring-2 focus-visible:ring-sky-400"
        >
          <Plus className="w-4 h-4 stroke-[1.75]" aria-hidden="true" />
        </button>

        {/* Theme toggle button (matches Sun icon on far right) */}
        <button
          type="button"
          onClick={() => setDarkMode((prev) => !prev)}
          aria-label={darkMode ? 'Switch to light mode' : 'Switch to dark mode'}
          title={darkMode ? 'Switch to light mode' : 'Switch to dark mode'}
          className="min-w-[40px] min-h-[44px] inline-flex items-center justify-center rounded-xl hover:bg-slate-100 dark:hover:bg-[#171F2E] active:scale-90 transition-all focus:outline-none focus-visible:ring-2 focus-visible:ring-amber-400 shrink-0"
        >
          {darkMode ? (
            <Sun className="w-6 h-6 text-[#D89A0B] fill-[#D89A0B]" aria-hidden="true" />
          ) : (
            <Moon className="w-5 h-5 text-slate-600 fill-slate-600" aria-hidden="true" />
          )}
        </button>
      </div>

      {/* Numeric Entry Modal (matches Enter Celsius dialog) */}
      {editingUnit && (
        <EnterValueModal
          unit={editingUnit}
          initialKm={km}
          onCancel={() => setEditingUnit(null)}
          onSetKm={(nextKm) => {
            setKm(nextKm);
            setEditingUnit(null);
          }}
        />
      )}
    </main>
  );
}
