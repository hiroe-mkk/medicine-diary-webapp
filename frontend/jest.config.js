module.exports = {
  verbose: true, // テストの実行結果を詳細に表示する
  testMatch: ['**/test/**/*.test.js'], // テストファイルは、 test ディレクトリ以下に配置し、ファイル名を *.test.js とする
  moduleNameMapper: {
    // main 以下のパスを @main/ で import できるようにする
    '^@main(.*)$': '<rootDir>/src/main/$1',
  },
};
