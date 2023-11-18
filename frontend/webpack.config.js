const Webpack = require('webpack');
const path = require('path');
const { VueLoaderPlugin } = require('vue-loader');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');

module.exports = {
  mode: 'development',
  devtool: 'eval-cheap-module-source-map',
  entry: {
    vendor: ['vue'],
    style: './src/main/css/style.css',
    siteHeader: './src/main/js/siteHeader.js',
    mypage: './src/main/js/mypage.js',
    setting: './src/main/js/setting.js',
    sharedGroupManagement: './src/main/js/sharedGroupManagement.js',
    medicineForm: './src/main/js/medicineForm.js',
    medicationRecords: './src/main/js/medicationRecords.js',
    medicationRecordForm: './src/main/js/medicationRecordForm.js',
    medicineDetail: './src/main/js/medicineDetail.js',
    medicineOverviews: './src/main/js/medicineOverviews.js',
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
    // .css ファイルを .js ファイルとバンドルせずに、.css ファイルとして出力する
    new MiniCssExtractPlugin({
      filename: 'css/[name].bundle.css',
    }),
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
      {
        // .css ファイルを .js ファイルとバンドルせずに、ファイルとして出力する
        test: /(?<!\.vue)\.css$/,
        use: [MiniCssExtractPlugin.loader, 'css-loader'],
      },
      {
        // .png ファイルと .jpg ファイルを DataUrl 化する
        test: /\.(png|jpg)$/,
        type: 'asset/inline',
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