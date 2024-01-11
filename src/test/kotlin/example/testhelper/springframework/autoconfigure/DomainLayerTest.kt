package example.testhelper.springframework.autoconfigure

/**
 * ドメイン層のクラスをテストする場合に利用可能なアノテーション
 */
@MyBatisRepositoryTest
@UseMockObjectStorageClient
@UseMockEmailSenderClient
@EnableObjectStorageAutoConfiguration
@EnableDomainServiceAutoConfiguration
annotation class DomainLayerTest