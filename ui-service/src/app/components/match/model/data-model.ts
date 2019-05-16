
export enum ResultType {
  NORMAL = 'NORMAL',
  UNKNOWN = 'UNKNOWN',
  POSTPONED = 'POSTPONED',
  CANCELLED = 'CANCELLED',
  NOT_FOUND = 'NOT_FOUND'
}

export class Team {
  id: number;
  name: string;
  alternativeNames?: string[];
  falseNames?: string[];
}


export class Match {
  id: number;
  sourceSystemName: string;
  sourceSystemId: string;
  homeTeam: Team;
  awayTeam: Team;
  startTime: Date;
  homeScore?: number;
  awayScore?: number;
  resultType: ResultType;
  markedAsFinishedTime?: Date;
}

export class MatchOdd {
  id: number;
  matchId: number;
  bookmakerId: number;
  bookmakerName: string;
  updatedOn: Date;
  odd1: number;
  odd2: number;
  oddX: number;
  odd1X?: number;
  odd12?: number;
  oddX2?: number;
  oddBTSY?: number;
  oddBTSN?: number;
  oddO05?: number;
  oddO15?: number;
  oddO25?: number;
  oddO35?: number;
  oddO45?: number;
  oddO55?: number;
  oddO65?: number;
  oddU05?: number;
  oddU15?: number;
  oddU25?: number;
  oddU35?: number;
  oddU45?: number;
  oddU55?: number;
  oddU65?: number;
}
