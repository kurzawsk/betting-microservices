import {Inject, Injectable, LOCALE_ID} from '@angular/core';
import {Subject} from 'rxjs/index';

@Injectable()
export class I18nService {

  public changeEmitter = new Subject();
  public readonly LANGUAGE;
  private _lang: {} = {};

  constructor(@Inject(LOCALE_ID) public localeId: string) {
    this.LANGUAGE = localeId ? localeId.substr(0, 2) : '';
  }

  public getMessage = (messageKey: string, values: any[]) => values.reduce((msg, value) =>
    msg.replace(new RegExp('\\[' + values.indexOf(value).toString() + '\\]', 'g'), value.toString()), this.lang[messageKey])



  get lang(): {} {
    return this._lang;
  }
}
