declare module 'frappe-gantt' {
  export interface Task {
    id: string
    name: string
    start: string
    end: string
    progress: number
    dependencies?: string
    custom_class?: string
  }

  export interface GanttOptions {
    view_mode?: 'Quarter Day' | 'Half Day' | 'Day' | 'Week' | 'Month' | 'Quarter' | 'Year'
    language?: string
    bar_height?: number
    bar_corner_radius?: number
    arrow_curve?: number
    padding?: number
    date_format?: string
    custom_popup_html?: (task: Task) => string
    on_click?: (task: Task) => void
    on_date_change?: (task: Task, start: Date, end: Date) => void
    on_progress_change?: (task: Task, progress: number) => void
    on_view_change?: (mode: string) => void
  }

  export default class Gantt {
    constructor(element: HTMLElement | string, tasks: Task[], options?: GanttOptions)
    change_view_mode(mode: 'Quarter Day' | 'Half Day' | 'Day' | 'Week' | 'Month' | 'Quarter' | 'Year'): void
    refresh(tasks: Task[]): void
  }
}

