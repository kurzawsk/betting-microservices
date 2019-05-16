import {BrowserModule} from '@angular/platform-browser';
import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';

import {
  MatCardModule, MatCheckboxModule, MatDatepickerModule, MatDialogModule, MatFormFieldModule, MatIconModule,
  MatInputModule,
  MatListModule, MatNativeDateModule, MatPaginatorModule, MatProgressSpinnerModule, MatSelectModule, MatSortModule,
  MatTableModule,
  MatTabsModule,
  MatToolbarModule, MatTooltipModule
} from '@angular/material';

import {AppComponent} from './app.component';
import {HomeComponent} from './components/home/home.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {RouterModule, Routes} from '@angular/router';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CookieService} from 'ngx-cookie-service';

import {LoginComponent} from './components/login/login.component';
import {AuthService} from './services/auth.service';
import {RoleGuard} from './services/role-guard.service';
import {MainComponent} from './components/main/main.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {EnvHostService} from './services/env-host.service';
import {
  CellComponent, CellContainerComponent, CheckboxCellComponent, ExpandableTextCellComponent, HtmlCellComponent,
  MultiColorCellComponent, RouterLinkCellComponent, SimpleTextDialogComponent, TextCellComponent
} from './components/common/ui-grid/cell';
import {VariedTypeCellComponent} from './components/common/ui-grid/cells/varied-type-cell/varied-type-cell.component';
import {StyledCellComponent} from './components/common/ui-grid/cells/styled-cell/styled-cell.component';
import {SplitCellComponent} from './components/common/ui-grid/cells/split-cell/split-cell.component';
import {CheckboxInputCellComponent} from './components/common/ui-grid/cells/checkbox-input-cell/checkbox-input-cell.component';
import {DateCellComponent} from './components/common/ui-grid/cells/date-cell/date-cell.component';
import {ButtonCellComponent} from './components/common/ui-grid/cells/button-cell/button-cell.component';
import {ActionLinkCellComponent} from './components/common/ui-grid/cells/action-link-cell/action-link-cell.component';
import {GridComponent} from './components/common/ui-grid/grid.component';
import {StorageService} from './services/storage.service';
import {I18nService} from './services/i18n.service';
import {AuthInterceptor} from './services/auth-interceptor.service';
import {JobComponent} from './components/job/job.component';
import {JobDataSource} from './components/job/job-data-source';
import {JobService} from './components/job/job.service';
import {JobExecutionDataSource} from './components/job/job-execution-data-source';
import {SpinnerContainerComponent} from './components/common/progress-spinner/container/spinner-container.component';
import {SpinnerComponent} from './components/common/progress-spinner/progress-spinner.component';
import {MatchComponent} from './components/match/match.component';
import {MatchService} from './components/match/match.service';
import {MatchDataSource} from './components/match/match-data-source.service';
import {MatchOddDataSource} from './components/match/match-odd-data-source.service';
import {SimpleSearchComponent} from './components/common/simple-search/simple-search.component';
import {InlineListEntryComponent} from './components/common/list-entry/inline-list-entry/inline-list-entry.component';
import {TeamComponent} from './components/match/team.component';
import {TeamDataSource} from './components/match/team-data-source.service';
import {TeamMatchesDataSource} from './components/match/team-matches-data-source.service';
import {AcceptRejectComponent, MappingCaseComponent} from './components/mapping-case/mapping-case.component';
import {MappingCaseDataSource} from './components/mapping-case/mapping-case-datasource.service';
import {MappingCaseService} from './components/mapping-case/mapping-case.service';
import {ExecuteReportsDialogComponent, ReportingComponent} from './components/reporting/reporting.component';
import {ReportingService} from './components/reporting/reporting.service';
import {ReportingDataSource} from './components/reporting/reporting-data-source';


const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: 'main', component: MainComponent, canActivate: [RoleGuard], data: {expectedRole: 'USER'}},
  {path: 'match', component: MatchComponent, canActivate: [RoleGuard], data: {expectedRole: 'USER'}},
  {path: 'match/:id', component: MatchComponent, canActivate: [RoleGuard], data: {expectedRole: 'USER'}},
  {path: 'team', component: TeamComponent, canActivate: [RoleGuard], data: {expectedRole: 'USER'}},
  {path: 'team/:id', component: TeamComponent, canActivate: [RoleGuard], data: {expectedRole: 'USER'}},
  {path: 'mapping-case', component: MappingCaseComponent, canActivate: [RoleGuard], data: {expectedRole: 'USER'}},
  {path: 'mapping-case/:id', component: MappingCaseComponent, canActivate: [RoleGuard], data: {expectedRole: 'USER'}},
  {
    path: 'reporting',
    component: ReportingComponent,
    canActivate: [RoleGuard],
    data: {expectedRole: 'ADMIN'}
  },
  {
    path: 'job',
    component: JobComponent,
    canActivate: [RoleGuard],
    data: {expectedRole: 'ADMIN'}
  },

  {path: '**', redirectTo: 'main', pathMatch: 'full'}
];

@NgModule({
  declarations: [
    CellContainerComponent,
    CellComponent,
    TextCellComponent,
    ExpandableTextCellComponent,
    HtmlCellComponent,
    CheckboxCellComponent,
    RouterLinkCellComponent,
    MultiColorCellComponent,
    VariedTypeCellComponent,
    StyledCellComponent,
    SplitCellComponent,
    CheckboxInputCellComponent,
    DateCellComponent,
    ButtonCellComponent,
    ActionLinkCellComponent,
    GridComponent,
    AppComponent,
    HomeComponent,
    LoginComponent,
    MainComponent,
    JobComponent,
    SpinnerComponent,
    SpinnerContainerComponent,
    MatchComponent,
    SimpleSearchComponent,
    InlineListEntryComponent,
    TeamComponent,
    MappingCaseComponent,
    AcceptRejectComponent,
    ExpandableTextCellComponent,
    SimpleTextDialogComponent,
    ReportingComponent,
    ExecuteReportsDialogComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    BrowserAnimationsModule,
    MatCardModule,
    MatListModule,
    MatTabsModule,
    MatToolbarModule,
    MatDialogModule,
    FormsModule,
    MatDatepickerModule,
    MatNativeDateModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    MatPaginatorModule,
    MatSortModule,
    MatProgressSpinnerModule,
    MatTableModule,
    MatTooltipModule,
    MatCheckboxModule,
    MatDialogModule,
    RouterModule.forRoot(routes, {onSameUrlNavigation: 'reload'})
  ],
  entryComponents: [
    CellComponent,
    TextCellComponent,
    ExpandableTextCellComponent,
    HtmlCellComponent,
    RouterLinkCellComponent,
    ButtonCellComponent,
    CheckboxCellComponent,
    CellContainerComponent,
    StyledCellComponent,
    SplitCellComponent,
    ActionLinkCellComponent,
    VariedTypeCellComponent,
    CheckboxInputCellComponent,
    MultiColorCellComponent,
    DateCellComponent,
    SpinnerContainerComponent,
    SpinnerComponent,
    InlineListEntryComponent,
    AcceptRejectComponent,
    SimpleTextDialogComponent,
    ExecuteReportsDialogComponent

  ],
  providers: [AuthService, CookieService, TeamMatchesDataSource,
    JobDataSource, JobExecutionDataSource, JobService, MatchService, MatchDataSource, MatchOddDataSource, TeamDataSource,
    MappingCaseDataSource, MappingCaseService, ReportingDataSource, ReportingService, StorageService, I18nService,
    EnvHostService, RoleGuard
    , {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AppModule {
}
