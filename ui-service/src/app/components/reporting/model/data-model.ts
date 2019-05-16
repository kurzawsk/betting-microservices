export class Report {
  id: number;
  code: string;
  title: string;
  description?: string;
  urlSuffix: string;
  serviceName: string;
  enabled: boolean;
  lastExecutionParameters?: string;
  deafultParameters?: string;
  lastExecutionStartTime?: Date;
  lastExecutionFinishTime: Date;
  lastExecutionResultData?: any;
}

export class CustomReportParameters {
  fromDate?: Date;
  toDate?: Date;
  predefinedPeriod?: PredefinedPeriod;
}

export enum PredefinedPeriod {
  YESTERDAY = 'YESTERDAY',
  LAST_7_DAYS = 'LAST_7_DAYS',
  LAST_MONTH = 'LAST_MONTH'
}
