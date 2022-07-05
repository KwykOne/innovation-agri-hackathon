import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AppLoadComponent } from './app-load.component';

describe('AppLoadComponent', () => {
  let component: AppLoadComponent;
  let fixture: ComponentFixture<AppLoadComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AppLoadComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AppLoadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
