import React, {useEffect, useRef, useState} from 'react';
import {
  formatOneDecimal,
  kmToMiles,
  milesToKm,
} from '../constants/conversion';

interface EnterValueModalProps {
  unit: 'km' | 'mi';
  initialKm: number;
  onCancel: () => void;
  onSetKm: (nextKm: number) => void;
}

export const EnterValueModal: React.FC<EnterValueModalProps> = ({
  unit,
  initialKm,
  onCancel,
  onSetKm,
}) => {
  const initialDisplay =
    unit === 'km'
      ? formatOneDecimal(initialKm)
      : formatOneDecimal(kmToMiles(initialKm));

  const [textValue, setTextValue] = useState<string>(initialDisplay);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    const timer = window.setTimeout(() => {
      inputRef.current?.focus();
      inputRef.current?.select();
    }, 10);
    return () => window.clearTimeout(timer);
  }, []);

  const parsed = parseFloat(textValue.replace(',', '.'));
  const valid = Number.isFinite(parsed) && parsed >= 0;

  const previewConverted = valid
    ? unit === 'km'
      ? `${formatOneDecimal(kmToMiles(parsed))} mi`
      : `${formatOneDecimal(milesToKm(parsed))} km`
    : unit === 'km'
      ? '— mi'
      : '— km';

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!valid) return;
    const nextKm = unit === 'km' ? parsed : milesToKm(parsed);
    onSetKm(Math.max(0, nextKm));
  };

  useEffect(() => {
    const onKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        onCancel();
      }
    };
    window.addEventListener('keydown', onKeyDown);
    return () => window.removeEventListener('keydown', onKeyDown);
  }, [onCancel]);

  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-labelledby="enter-value-title"
      onClick={onCancel}
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/65 px-4"
    >
      <form
        onSubmit={handleSubmit}
        onClick={(e) => e.stopPropagation()}
        className="w-full max-w-[320px] rounded-[24px] bg-white dark:bg-[#131B29] p-6 shadow-2xl border border-slate-200 dark:border-[#1E2738]"
      >
        <h2
          id="enter-value-title"
          className="text-lg font-semibold text-slate-900 dark:text-white"
        >
          {unit === 'km' ? 'Enter Kilometers (km)' : 'Enter Miles (mi)'}
        </h2>

        <div className="mt-5 flex items-baseline justify-between gap-3">
          <input
            ref={inputRef}
            type="text"
            inputMode="decimal"
            aria-label={unit === 'km' ? 'Kilometers value' : 'Miles value'}
            value={textValue}
            onChange={(e) => setTextValue(e.target.value)}
            className="w-full min-w-0 bg-transparent text-[28px] leading-tight font-normal tabular-nums text-slate-900 dark:text-white caret-[#2EA8D9] focus:outline-none"
          />
          <span className="text-lg text-slate-500 dark:text-[#7B8494] shrink-0">
            {unit}
          </span>
        </div>

        <div className="mt-2.5 text-[15px] tabular-nums text-[#0EA5E9] dark:text-[#17A2E0]">
          = {previewConverted}
        </div>

        <div className="mt-7 flex items-center justify-end gap-5">
          <button
            type="button"
            onClick={onCancel}
            className="min-h-[40px] px-2 text-sm font-medium text-slate-500 hover:text-slate-800 dark:text-[#8892A4] dark:hover:text-white transition-colors focus:outline-none focus-visible:ring-2 focus-visible:ring-sky-400 rounded-lg"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={!valid}
            className="min-h-[40px] px-3 text-sm font-semibold text-[#0284C7] dark:text-[#17A2E0] hover:text-sky-400 disabled:opacity-40 transition-colors focus:outline-none focus-visible:ring-2 focus-visible:ring-sky-400 rounded-lg"
          >
            Set
          </button>
        </div>
      </form>
    </div>
  );
};
