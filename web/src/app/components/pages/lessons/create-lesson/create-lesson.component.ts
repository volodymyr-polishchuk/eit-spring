import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Subject} from '../../../shared/models/subject';
import {DataSourceService} from '../../../shared/repository/data-source.service';
import {Topic} from '../../../shared/models/topic';
import {MatSnackBar} from '@angular/material';
import {Lesson} from '../../../shared/models/lesson';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Observable} from 'rxjs';
import {distinctUntilChanged, map, startWith} from 'rxjs/operators';

@Component({
  selector: 'app-create-lesson',
  templateUrl: './create-lesson.component.html',
  styleUrls: ['./create-lesson.component.css']
})
export class CreateLessonComponent implements OnInit {

  subjects: Array<Subject> = [];
  topics: Array<Topic> = [];
  filteredTopics: Observable<Array<Topic>>;
  lastSubjectKey: string;
  formGroup: FormGroup;

  @Output() onCreateLesson: EventEmitter<Lesson> = new EventEmitter<Lesson>();
  offline = false;

  constructor(private dataSource: DataSourceService,
              private snackBar: MatSnackBar,
              private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.createFormGroup();
    this.subjects = JSON.parse(localStorage.getItem('subjects'));
    this.dataSource.getAllSubject().subscribe(
      (next: Array<{k: string; name: string}>) => {
        this.subjects = next.map((item) => ({key: item.k, name: item.name}));
        localStorage.setItem('subjects', JSON.stringify(this.subjects));
        if (this.subjects) {
          this.formGroup.controls.subject.setValue(this.subjects[0].key);
          this.refreshTopicsList(this.subjects[0].key);
        }
      });
  }

  startLesson(): void {
    let topicControl = this.formGroup.controls.topic;
    if (topicControl.invalid) {
      this.snackBar.open('Тема заняття не може бути пустою', 'Закрити', { duration: 2000 });
      return;
    }
    const topic: Topic = this.topics
        .find(t => t.name === topicControl.value) || {key: null, name: topicControl.value};
    const subject: Subject = this.subjects
        .find(subjectValue => subjectValue.key === this.formGroup.value.subject);
    this.onCreateLesson.emit({
      name: subject.name,
      timeToNowDifference: new Date().getTime(),
      topic,
      subject
    });
  }

  refreshTopicsList(key: string): void {
    if (key === '-1') {
      key = this.lastSubjectKey;
    }
    this.lastSubjectKey = key;
    this.dataSource.getTopicsForSubject(key)
      .subscribe((value: Array<{k: string, name: string}>) => {
        this.topics = value.map((it) => ({key: it.k, name: it.name}));
        this.filteredTopics = this.formGroup.controls.topic.valueChanges
          .pipe(
            startWith(''),
            map(value => this._filterTopic(value))
          );
        (document.getElementById('topicInput') as HTMLInputElement).value = '';
      });
  }

  private _filterTopic(value: string): Array<Topic> {
    const topicName = value.toLowerCase();
    return this.topics.filter(option => option.name.toLowerCase().includes(topicName));
  }

  removeTopic(topic: Topic): void {
    if (confirm(`Ви точно бажаєте видалити тему [${topic.name}]`)) {
      this.dataSource.deleteTopic(topic)
        .subscribe(value => {
          this.refreshTopicsList('-1');
          this.snackBar.open('Тема успішно видалена', 'Закрити', { duration: 2000 });
        }, (error) => {
          this.snackBar.open(error.error.message, 'Закрити', { duration: 2000 });
        });
    }
  }

  private createFormGroup() {
    let date = new Date();
    this.formGroup = this.formBuilder.group({
      subject: [''],
      topic: ['', [Validators.required]],
      datetimeStart: [date.toISOString().replace(/:\d{2}.\d{3}Z/, '')],
      time: [],
    });
    this.formGroup.controls.subject.valueChanges
        .pipe(distinctUntilChanged())
        .subscribe(subjectKey => {
          this.refreshTopicsList(subjectKey);
        });
  }
}
