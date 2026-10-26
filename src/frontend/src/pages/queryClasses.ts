export const pageShellClass = 'min-h-screen bg-page text-text'
export const pageContentClass = 'mx-auto w-full max-w-[1440px] px-[5vw] pb-8 pt-[38px] max-[960px]:px-6 max-[700px]:px-4 max-[700px]:pb-5 max-[700px]:pt-7'
export const eyebrowClass = 'mb-[7px] font-mono text-caption leading-[1.4] tracking-[.04em] text-muted'
export const monoClass = 'font-mono tabular-nums'
export const panelClass = 'rounded-[4px] border border-line bg-surface'
export const fieldClass = 'min-h-10 rounded-[3px] border border-[#4a4e48] bg-surface-low px-2.5 py-2 font-mono! text-small! leading-[normal]! font-normal! text-text'
export const tableClass = [
  'w-full border-collapse whitespace-nowrap text-(length:--text-body)',
  '[&_th]:border-b [&_th]:border-line [&_th]:p-3 [&_th]:text-left [&_th]:font-mono [&_th]:text-caption [&_th]:leading-[normal] [&_th]:font-normal [&_th]:tracking-[.08em] [&_th]:text-muted [&_th]:uppercase',
  '[&_td]:border-b [&_td]:border-[#3c403b] [&_td]:p-3 [&_tbody_tr:last-child_td]:border-b-0 [&_tbody_tr:hover]:bg-surface-raised',
  '[&_a]:font-semibold [&_a]:text-cyan [&_a]:no-underline [&_a:hover]:underline',
].join(' ')
export const filtersClass = 'mb-3.5 flex flex-wrap items-center gap-2.5 rounded-[4px] border border-line bg-surface p-3'
export const countClass = 'ml-auto whitespace-nowrap font-mono text-caption leading-[normal] text-muted max-[700px]:ml-0'
export const tableWrapClass = 'relative overflow-x-auto rounded-[4px] border border-line bg-surface'
export const viewLinkClass = 'inline-flex items-center gap-1 rounded-[3px] border border-[#555b50] px-[9px] py-[3px] font-mono text-caption leading-[normal]'
export const chartsClass = 'mt-3.5 grid grid-cols-2 gap-3.5 [@media(max-width:960px)]:grid-cols-1'
export const breadcrumbClass = 'mb-3.5 flex flex-wrap gap-2 font-mono text-small leading-[normal] text-secondary [&_a]:text-cyan [&_a]:no-underline [&_a:hover]:underline [&_b]:font-semibold [&_b]:text-text'
