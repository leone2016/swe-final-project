import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { CommonModule } from '@angular/common';
import { Store } from '@ngrx/store';
import { ngrxFormsQuery } from '../+state/forms.selectors';
import { formsActions } from '../..';

@UntilDestroy()
@Component({
  selector: 'cdt-list-errors',
  standalone: true,
  templateUrl: './list-errors.component.html',
  styleUrls: ['./list-errors.component.css'],
  imports: [CommonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListErrorsComponent implements OnInit, OnDestroy {
  errors: string[] = [];

  constructor(private readonly store: Store, private readonly changeDetectorRef: ChangeDetectorRef) {}

  ngOnInit() {
    this.store
      .select(ngrxFormsQuery.selectErrors)
      .pipe(untilDestroyed(this))
      .subscribe((e) => {
        // Intentamos parsear si es un string
        let errorsArray: any[] = [];

        try {
          const parsed = typeof e === 'string' ? JSON.parse(e) : e;
          errorsArray = Array.isArray(parsed) ? parsed : [];
        } catch (err) {
          console.error('Error parsing error response', err);
        }

        this.errors = errorsArray.map((err: any) => `${err.code} ${err.message}`);
        this.changeDetectorRef.markForCheck();
      });
  }

  ngOnDestroy() {
    this.store.dispatch(formsActions.initializeErrors());
  }
}
