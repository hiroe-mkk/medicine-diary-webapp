import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.run.*

plugins {
	id("org.springframework.boot") version "3.1.3"
	id("io.spring.dependency-management") version "1.1.3"
	kotlin("jvm") version "1.8.22"
	kotlin("plugin.spring") version "1.8.22"
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

sourceSets {
    getByName("main").resources.srcDirs("src/main/kotlin") // mybatis SQL map XML ファイルを検知させる
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	implementation("org.springframework.boot:spring-boot-starter-web")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// テンプレートエンジン
	implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:3.2.1")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

	// セキュリティ
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
	testImplementation("org.springframework.security:spring-security-test")

    // データベース
    runtimeOnly("com.mysql:mysql-connector-j")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.2")
    testImplementation("org.mybatis.spring.boot:mybatis-spring-boot-starter-test:3.0.2")
    implementation("io.minio:minio:8.5.4")

	// バリデータ
	implementation("org.springframework.boot:spring-boot-starter-validation")
}

tasks.withType<ProcessResources> {
	dependsOn(":frontend:npm_run_build") // リソースを処理する前に frontend サブモジュールのビルドを実行する
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<BootRun> {
	mainClass.set("example.Application")
	sourceResources(sourceSets["main"]) // 静的リソースを Automatic Restart の対象とする

	if (findProperty("profiles") == "dev") {
		jvmArgs("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005") // デバッグが可能な状態でアプリケーションを起動する
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}