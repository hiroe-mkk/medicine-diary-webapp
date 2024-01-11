import Croppie from 'croppie/croppie.js';

export class ImageTrimmingManager {
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
      viewport: { width: 250, height: 250 },
      boundary: { width: 255, height: 255 },
      enableOrientation: true,
    };
    this._croppie = new Croppie(container, croppieOption);
  }

  async result() {
    if (!this.isTrimming) return;

    try {
      const blob = await this._croppie.result({
        type: 'blob',
        size: { width: 400, height: 400 },
        format: 'jpeg',
      });
      return blob;
    } catch (error) {
      throw error;
    }
  }

  destroy() {
    if (this._croppie !== null) {
      this._croppie.destroy();
      this._croppie = null;
    }
  }
}
