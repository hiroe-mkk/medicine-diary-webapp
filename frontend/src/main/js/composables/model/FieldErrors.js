export class FieldErrors {
  constructor() {
    this._fieldNameToMessages = {};
  }

  isEmpty() {
    return this._fieldNameToMessages === {};
  }

  contains(fieldName) {
    if (this.isEmpty()) return false;

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
