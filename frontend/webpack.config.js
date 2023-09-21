const Webpack = require('webpack');
const path = require('path');
const { VueLoaderPlugin } = require('vue-loader');

module.exports = {
  mode: 'development',
  devtool: 'eval-cheap-module-source-map',
  entry: {
    vendor: ['vue'],
  },
  output: {
    // ビルド成果物はルートプロジェクトの src/main/resources/static/dist に出力する
    path: path.resolve(__dirname, '../src/main/resources/static/dist'),
    filename: 'js/[name].bundle.js',
    // ビルド成果物を出力する前に、出力ディレクトリをクリーンアップする
    clean: true,
  },
  plugins: [
    new Webpack.DefinePlugin({
      __VUE_OPTIONS_API__: true, // オプションAPIサポートの有効化
      __VUE_PROD_DEVTOOLS__: false, //　本番環境での開発ツールのサポートを無効化
    }),
    // .vue ファイルをビルドしてバンドルする
    new VueLoaderPlugin(),
  ],
  module: {
    rules: [
      {
        // .js ファイルを Babel でトランスパイルする
        test: /\.js$/,
        loader: 'babel-loader',
        // node_modules を対象外にする
        exclude: /node_modules/,
      },
      {
        // .vue ファイルをビルドしてバンドルする
        test: /\.vue$/,
        loader: 'vue-loader',
      },
      {
        // .vue ファイルの style をバンドルして style 要素として埋め込む
        test: /\.vue\.css$/,
        use: ['vue-style-loader', 'css-loader'],
      },
    ],
  },
  resolve: {
    // モジュールを解決する際は node_modules から検索する
    modules: ['node_modules'],
    alias: {
      vue$: 'vue/dist/vue.esm-bundler.js',
      '@main': path.resolve(__dirname, 'src/main/'),
    },
  },
};