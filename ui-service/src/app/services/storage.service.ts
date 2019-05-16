import {Injectable} from '@angular/core';
import {I18nService} from './i18n.service';

@Injectable()
export class StorageService {

  private static readonly appName = 'test';

  private prefix;

  constructor(private i18nService: I18nService) {
    this.prefix = StorageService.appName + '_' + this.i18nService.LANGUAGE + '_';
  }

  public setItem = (key: string, data: string) => localStorage.setItem(this.prefix + key, data);

  public getItem = (key: string) => localStorage.getItem(this.prefix + key);

  public removeItem = (key: string) => localStorage.removeItem(this.prefix + key);
}
