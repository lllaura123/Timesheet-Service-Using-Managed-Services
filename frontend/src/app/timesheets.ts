import { Student } from './students';

export interface Timesheet {
  student: Student;
  year: number;
  month: number;
  fileExists: boolean;
}
