plugins {
    id("com.github.node-gradle.node") version "7.0.0"
}

// プロジェクトローカルに OS に応じた Node.js を自動ダウンロードする
node {
    download.set(true)
    version.set("18.18.0")
}

// npm run build の前に npm install を実行する
tasks.getByName("npm_run_build") {
    dependsOn("npm_install")
}