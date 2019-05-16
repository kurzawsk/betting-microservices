import {Injectable} from '@angular/core';

@Injectable()
export class EnvHostService {

  public static readonly GATEWAY_URL = window.location.origin + '/';

  constructor() {
  }

  public getGatewayUrl = () => EnvHostService.GATEWAY_URL;
}
