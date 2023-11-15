package example.testhelper.springframework.autoconfigure

/**
 * ドメイン層のクラスをテストする場合に利用可能なアノテーション
 */
@MyBatisRepositoryTest
@UseMockObjectStorageClient
@EnableObjectStorageAutoConfiguration
@EnableDomainServiceAutoConfiguration
annotation class DomainLayerTest