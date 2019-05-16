export class PagedSearchResult {
  items: any[] = [];
  totalItemsCount = 0;
}

export class PagedSearchParams {
  pageIndex = 0;
  pageSize = 10;
  sortOrderAsc = false;
  sortProperty = 'id';
  filter: { [key: string]: any } = {};
}

export class Organization {
  id: number;
  name: string;
  address: string;
  createdDate: Date;
  updatedDate: Date;
}

export class Department {
  id: number;
  name: string;
  createdDate: Date;
  updatedDate: Date;
}

export class Job {
  id: number;
  code: string;
  urlSuffix: string;
  serviceName: string;
  enabled: boolean;
  lastExecutionStartTime: Date;
  lastExecutionFinishTime: Date;
  lastExecutionJobStatus: JobExecutionStatus;
  lastExecutionErrorMessage: string;
  description: string;
}

export class JobExecution {
  id: number;
  startTime: Date;
  finishTime: Date;
  jobExecutionStatus: JobExecutionStatus;
  errorMessage: string;
}

export enum JobExecutionStatus {
  RUNNING = 'RUNNING',
  SUCCESS = 'SUCCESS',
  FAILED = 'FAILED'
}

export enum JobOperation {
  RUN_JOB = 'RUN_JOB',
  ENABLE_JOB = 'ENABLE_JOB',
  DISABLE_JOB = 'DISABLE_JOB'
}
