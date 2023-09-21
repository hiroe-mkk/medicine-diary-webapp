const Webpack = require('webpack');
const path = require('path');

module.exports = {
  mode: 'development',
  devtool: 'eval-cheap-module-source-map',
  output: {
    // ビルド成果物はルートプロジェクトの src/main/resources/static/dist に出力する
    path: path.resolve(__dirname, '../src/main/resources/static/dist'),
    filename: 'js/[name].bundle.js',
    // ビルド成果物を出力する前に、出力ディレクトリをクリーンアップする
    clean: true,
  },
  resolve: {
    // モジュールを解決する際は node_modules から検索する
    modules: ['node_modules'],
    alias: {
      '@main': path.resolve(__dirname, 'src/main/'),
    },
  },
};