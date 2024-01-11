export class NotificationMessage {
  constructor() {
    this._type = undefined;
    this._message = undefined;
    this._details = undefined;
  }

  get type() {
    return this._type;
  }

  get message() {
    return this._message;
  }

  get details() {
    return this._details;
  }

  get color() {
    if (this._type === 'ERROR' || this._type === 'WARNING') {
      return 'is-danger';
    } else {
      return 'is-link';
    }
  }

  get hasMessage() {
    return this._message !== undefined;
  }

  get hasDetails() {
    if (!this.hasMessage) return false;

    return this._details !== undefined;
  }

  set(type = '', message = '', details = '') {
    this._type = type !== '' ? type : undefined;
    this._message = message !== '' ? message : undefined;
    this._details = details !== '' ? details : undefined;
  }
}
