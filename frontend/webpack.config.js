const Webpack = require('webpack');
const path = require('path');
const { VueLoaderPlugin } = require('vue-loader');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');

module.exports = {
  mode: 'production',
  entry: {
    vendor: ['vue', './src/main/css/style.css', './src/main/js/siteHeader.js'],
    home: './src/main/js/home.js',
    calendar: './src/main/js/calendar.js',
    setting: './src/main/js/setting.js',
    user: './src/main/js/user.js',
    sharedGroupManagement: './src/main/js/sharedGroupManagement.js',
    medicineDetail: './src/main/js/medicineDetail.js',
    medicineOverviews: './src/main/js/medicineOverviews.js',
    medicineForm: './src/main/js/medicineForm.js',
    medicationRecords: './src/main/js/medicationRecords.js',
    medicationRecordForm: './src/main/js/medicationRecordForm.js',
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
      'process.env.NODE_ENV': JSON.stringify('production'), // Vue.js を本番モードに設定する
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
      vue$: 'vue/dist/vue.esm-browser.prod.js',
      '@main': path.resolve(__dirname, 'src/main/'),
    },
  },
};
