export class ArrayConverter {
  // 渡された [値1, 値2, 値3] 形式の文字列をカンマ区切りで分解し、文字列の配列に変換する
  static fromString(str) {
    if (str.length === 2) return [];

    const ignoreBracketStr = str.substring(1, str.length - 1);
    return ignoreBracketStr.split(', ');
  }
}