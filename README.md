# お薬服用記録サポートアプリ 「 お薬日記 」

![home](https://github.com/hiroe-mkk/medicine-diary-webapp/assets/145527696/0bd7a76b-c023-40a7-a431-a5577c763f53)

服用したお薬の記録をサポートする Web アプリケーションです。

URL：[https://okusuri-nikki-kk.link](https://okusuri-nikki-kk.link)

## 機能一覧

### メイン機能

- **服用記録追加機能**
  - 服用記録の一覧表示 (薬・日付・ユーザーなどによるフィルタリング)
  - 服用記録のカレンダー表示
- **薬情報登録機能**
  - 薬の画像のトリミングとアップロード
  - 在庫情報の設定 (服用記録追加に伴う在庫数の自動更新)
  - 薬の一覧表示 (症状によるフィルタリング)
- **共有機能**
  - ユーザー検索
  - 共有グループへの招待
  - 薬情報並びに服用記録の公開・非公開設定

### その他

- **認証機能**
  - ソーシャルログイン (初回ログイン時にアカウント自動作成)
- **プロフィール設定機能**
  - プロフィール画像のトリミングとアップロード
- **お問い合わせ機能**
  - お問い合わせ内容の確認メールを送信

# 使用技術

## バックエンド

- **Kotlin 1.8.2**
- **Spring Boot 3.1.2**
- **Spring Security (spring-boot-starter-security) 3.0.6**
- **Spring Boot Actuator (spring-boot-starter-actuator) 3.1.0**
- **Flyway 9.21.0**
- **MyBatis (mybatis-spring-boot-starter) 3.0.0**
- **Thymeleaf (spring-boot-starter-thymeleaf) 3.0.6**
- **JUnit (AssertJ) 3.23.1**
- **SpringMockK 4.0.2**
- **Gradle 7.6.1**

### 設計

- ソフトウェア開発手法：**ドメイン駆動設計**
- モデリング手法：**ICONIX プロセス (ユースケース駆動開発より)**
- アーキテクチャ：オニオンアーキテクチャ

#### パッケージ構成

![architecture](https://github.com/hiroe-mkk/medicine-diary-webapp/assets/145527696/994ac768-92e2-4cb3-8908-8e24e8166d1e)

```
├── application
│   ├── query (クエリサービス)
│   └── service (アプリケーションサービス)
├── domain
│   └── model (ドメインオブジェクト)
├── infrastructure
|   ├── emailsender (メールセンダーの実装)
|   ├── query (クエリサービスの実装)
|   ├── repository (リポジトリの実装)
|   └── storage (オブジェクトストレージの実装)
└── presentation
    └── controller
        ├── api (JSON を返すコントローラ)
        └── page (html を返すコントローラ)
```

## フロントエンド

- **Vue.js 3.2.47**
- **Node.js 18.14.1**
- **npm 9.6.7**
- **Jest 29.5.0**
- **Webpack 5.82.0**
- **Babel 7.21.8**
- **Bulma 0.9.4**

## インフラ

![infrastructure](https://github.com/hiroe-mkk/medicine-diary-webapp/assets/145527696/68720d26-58b6-484f-8156-a9ee8d7a992d)

- **AWS (ECR, ECS (Fargate), RDS (MySQL), S3, SES, CloudFront, ALB, Route53, Systems Manager, CloudWatch)**
- **Terraform**
- **CircleCI**
- **Docker / docker-compose**
