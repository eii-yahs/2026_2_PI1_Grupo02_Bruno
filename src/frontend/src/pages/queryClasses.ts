export const fieldClass = 'min-h-10 rounded-(--radius-control) border border-[#30445a] bg-[#0c1520] px-2.5 py-2 font-(family-name:--mono)! text-(length:--text-small)! leading-[normal]! font-normal! text-(--text)'
export const tableClass = [
  'w-full border-collapse whitespace-nowrap text-(length:--text-body)',
  '[&_th]:border-b [&_th]:border-(--line) [&_th]:p-3 [&_th]:text-left [&_th]:font-(family-name:--mono) [&_th]:text-(length:--text-caption) [&_th]:leading-[normal] [&_th]:font-normal [&_th]:tracking-[.12em] [&_th]:text-(--text-muted) [&_th]:uppercase',
  '[&_td]:border-b [&_td]:border-[#1c2b3b] [&_td]:p-3 [&_tbody_tr:last-child_td]:border-b-0 [&_tbody_tr:hover]:bg-(--surface-raised)',
  '[&_a]:font-semibold [&_a]:text-(--cyan) [&_a]:no-underline [&_a:hover]:underline',
].join(' ')
export const filtersClass = 'mb-3.5 flex flex-wrap items-center gap-2.5 rounded-(--radius-card) border border-(--line) bg-(--surface) p-3'
export const countClass = 'ml-auto font-(family-name:--mono) text-(length:--text-caption) leading-[normal] whitespace-nowrap text-(--text-muted) [@media(max-width:700px)]:ml-0'
export const tableWrapClass = 'relative overflow-x-auto rounded-(--radius-card) border border-(--line) bg-(--surface)'
export const viewLinkClass = 'inline-block rounded-(--radius-control) border border-[#1e6a74] px-[9px] py-[3px] font-(family-name:--mono) text-(length:--text-caption) leading-[normal]'
export const chartsClass = 'mt-3.5 grid grid-cols-2 gap-3.5 [@media(max-width:960px)]:grid-cols-1'
export const breadcrumbClass = 'mb-3.5 flex flex-wrap gap-2 font-(family-name:--mono) text-(length:--text-small) leading-[normal] text-(--text-secondary) [&_a]:text-(--cyan) [&_a]:no-underline [&_a:hover]:underline [&_b]:font-semibold [&_b]:text-(--text)'
