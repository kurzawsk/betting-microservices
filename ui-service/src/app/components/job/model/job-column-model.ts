export enum JobColumn {
  ID = 'id',
  CODE = 'code',
  DESCRIPTION = 'description',
  SERVICE_NAME = 'serviceName',
  URL_SUFFIX = 'urlSuffix',
  LAST_EXECUTION_START_TIME = 'lastExecutionStartTime',
  LAST_EXECUTION_FINISH_TIME = 'lastExecutionFinishTime',
  LAST_EXECUTION_JOB_STATUS = 'lastExecutionJobStatus',
  LAST_EXECUTION_ERROR_MESSAGE = 'lastExecutionErrorMessage',
  ENABLED = 'enabled',
  RUN_BTN = 'runBtn'
}

export enum JobExecutionColumn {
  ID = 'id',
  START_TIME = 'startTime',
  FINISH_TIME = 'finishTime',
  JOB_EXECUTION_STATUS = 'jobExecutionStatus',
  ERROR_MESSAGE = 'errorMessage'
}
