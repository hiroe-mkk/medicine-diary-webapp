export class FieldErrors {
  constructor() {
    this._fieldNameToMessages = {};
  }

  contains(fieldName) {
    return this._fieldNameToMessages[fieldName] !== undefined;
  }

  get(fieldName) {
    if (!this.contains(fieldName)) return [];

    return this._fieldNameToMessages[fieldName];
  }

  set(fieldErrors) {
    this._fieldNameToMessages = fieldErrors;
  }

  clear() {
    this._fieldNameToMessages = {};
  }
}