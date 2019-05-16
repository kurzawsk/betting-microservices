export class MappingCase {
  id: string;
  homeTeamName: string;
  awayTeamName: string;
  matchId: number;
  matchTeams: string;
  awaySimilarityFactor: number;
  homeSimilarityFactor: number;
  sourceSystemName: string;
  status: MappingCaseStatus;
  createdOn: Date;
  updatedOn: string;
  createdBy: Date;
  updatedBy: string;
}

export enum MappingCaseStatus {
  NEW = 'NEW',
  ACCEPTED = 'ACCEPTED',
  REJECTED = 'REJECTED'
}
