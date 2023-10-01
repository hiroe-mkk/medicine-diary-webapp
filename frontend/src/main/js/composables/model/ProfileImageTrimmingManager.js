import Croppie from 'croppie/croppie.js';

export class ProfileImageTrimmingManager {
  constructor() {
    this._croppie = null;
  }

  get isTrimming() {
    return this._croppie !== null;
  }

  setFile(file, container) {
    this.destroy();

    const objectURL = URL.createObjectURL(file);
    const croppieOption = {
      url: objectURL,
      viewport: { width: 300, height: 300 },
      boundary: { width: 305, height: 305 },
      enableOrientation: true,
    };
    this._croppie = new Croppie(container, croppieOption);
  }

  result() {
    if (!this.isTrimming()) return;

    return this._croppie
      .result({
        type: 'blob',
        size: { width: 400, height: 400 },
        format: 'jpeg',
      })
      .then((blob) => blob);
  }

  destroy() {
    if (this._croppie !== null) {
      this._croppie.destroy();
      this._croppie = null;
    }
  }
}
