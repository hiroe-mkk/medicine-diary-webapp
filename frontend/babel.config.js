module.exports = {
  presets: [
    [
      '@babel/preset-env',
      {
        modules: false, // import と export を Webpack に処理させるために、変換を無効化する
        useBuiltIns: 'entry',
        corejs: '3.22',
      },
    ],
  ],
};