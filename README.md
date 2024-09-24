# お薬服用記録サポートアプリ 「 お薬日記 」

![home](https://github.com/hiroe-mkk/medicine-diary-webapp/assets/145527696/0bd7a76b-c023-40a7-a431-a5577c763f53)

服用したお薬の記録をサポートする Web アプリケーションです。

アプリケーションのコンセプトや技術選定理由などについては、[こちらの記事](https://qiita.com/hiroe-mkk/items/fb49783b346944961b04) をご参照ください。

## 機能一覧

### メイン機能

- **薬情報登録機能**
    - 薬情報の登録・更新・削除
    - 薬の画像のトリミングとアップロード
    - 在庫情報の設定 (服用記録追加に伴う在庫数の自動更新)
    - 薬の一覧表示 (症状やユーザーによる検索)
- **服用記録追加機能**
    - 服用記録の追加・修正・削除
    - 服用記録の一覧表示 (薬・日付・ユーザーなどによる検索)
    - 服用記録のカレンダー表示
- **共有機能**
    - 共有グループへの招待メール送信
    - 共有グループへの参加・脱退
    - 共有グループ内におけるアクセス権限管理 (薬の公開設定に基づく)

### その他

- **認証機能**
    - ソーシャルログイン (初回ログイン時にアカウント自動作成)
- **プロフィール設定機能**
    - プロフィール情報の設定
    - プロフィール画像のトリミングとアップロード
- **お問い合わせ機能**
    - お問い合わせ内容の確認メール送信

# 使用技術

## バックエンド

- **Kotlin 1.9.25**
- **Gradle 7.6.1**
- **Spring Boot 3.3.3**
- **Spring Security (spring-boot-starter-security)**
- **MyBatis (mybatis-spring-boot-starter)**
- **Thymeleaf (spring-boot-starter-thymeleaf)**
- **Flyway 9.21.0**
- **JUnit (AssertJ) 3.23.1**
- **SpringMockK 4.0.2**

### 設計

- ソフトウェア開発手法：**ドメイン駆動設計**
- モデリング手法：**ICONIX プロセス (ユースケース駆動開発より)**
- アーキテクチャ：オニオンアーキテクチャ

#### パッケージ構成

```
├── application
│   ├── query (クエリサービス: 読み取り専用のデータ取得ロジックを提供する)
│   └── service (アプリケーションサービス: ユースケースを実装し、ドメインモデルを調整・連携する)
├── domain
│   └── model (ドメインモデル: エンティティ、値オブジェクト、ドメインサービス、リポジトリインターフェースなどを含む)
├── infrastructure
|   ├── db (データベース実装: リポジトリインターフェースの実装やデータベースへのアクセスを管理する)
|   ├── email (メール実装: メール送信に関する実装を行う)
|   └── objectstorage (オブジェクトストレージ実装: オブジェクトストレージサービスへのアクセスを管理する)
└── presentation
    └── controller
        ├── api (APIコントローラ: APIエンドポイントを提供する)
        └── page (Pageコントローラ: HTMLページのレンダリングを行う)
```

![architecture](https://github.com/hiroe-mkk/medicine-diary-webapp/assets/145527696/994ac768-92e2-4cb3-8908-8e24e8166d1e)

#### モデリング成果物

- [エンドポイント一覧](https://github.com/hiroe-mkk/medicine-diary-webapp/blob/main/docs/endpoints.svg)
- [画面遷移図](https://github.com/hiroe-mkk/medicine-diary-webapp/blob/main/docs/screen-transition-map.svg)
- [ドメインモデル図](https://github.com/hiroe-mkk/medicine-diary-webapp/blob/main/docs/domain-model.svg)
- [ロバストネス図](https://github.com/hiroe-mkk/medicine-diary-webapp/blob/main/docs/robustness.svg)
- [E-R図](https://github.com/hiroe-mkk/medicine-diary-webapp/blob/main/docs/er.svg)

## フロントエンド

- **Node.js 18.18.0**
- **Webpack 5.82.0**
- **Babel 7.21.8**
- **Vue.js 3.2.47**
- **Jest 29.5.0**
- **Bulma 0.9.4**

## インフラ

![infrastructure](https://github.com/hiroe-mkk/medicine-diary-webapp/assets/145527696/68720d26-58b6-484f-8156-a9ee8d7a992d)

- **AWS (ECR, ECS (Fargate), RDS (MySQL), S3, SES, CloudFront, ALB, Route53, Systems Manager, CloudWatch)**
- **Terraform**
- **CircleCI**
- **Docker / docker-compose**
