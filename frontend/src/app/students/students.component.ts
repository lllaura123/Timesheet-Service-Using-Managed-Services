import { Component, OnInit } from '@angular/core';
import { Student } from '../students';
import { StudentService } from '../student.service';

@Component({
  selector: 'app-students',
  templateUrl: './students.component.html',
  styleUrls: ['./students.component.css']
})
export class StudentsComponent implements OnInit {
  students: Student[];
  date: Date = new Date();
  monthName: string;

  constructor(private studentService: StudentService) { }

  ngOnInit(): void {
    this.date.setMonth(this.date.getMonth()-1);
    this.getStudents(this.dateToString(this.date));
    this.monthName= this.displayMonth();
  }

  getLastMonth() {
    this.date.setMonth(this.date.getMonth()-1);
    this.getStudents(this.dateToString(this.date));
    this.monthName= this.displayMonth();
    }

  getNextMonth() {
    this.date.setMonth(this.date.getMonth()+1);
    this.getStudents(this.dateToString(this.date));
    this.monthName= this.displayMonth();
  }

  getStudents(date: string): void {
    this.studentService.getStudents(date).subscribe(students=> this.students= students);
  }

  dateToString(date: Date): string {
    let year: number = date.getFullYear();
    let month: number = date.getMonth()+1;
    return year+"-"+((month>9)?month: "0"+(month))+"-01";
  }
  displayMonth(): string {
    let monthNames= ["Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August","September", "Oktober", "November", "Dezember"];
    return monthNames[this.date.getMonth()]+" "+ this.date.getFullYear();
  }

}
