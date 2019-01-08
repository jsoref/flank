package ftl.test.util

import com.google.auth.oauth2.ComputeEngineCredentials
import com.google.cloud.storage.BlobInfo
import ftl.gc.GcStorage
import org.junit.Assert
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.logging.Level
import java.util.logging.Logger

object LocalGcs {

    private const val TEST_BUCKET = "tmp_bucket_2"

    private fun uploadToMockGcs(path: Path) {
        if (!path.toFile().exists()) {
            throw RuntimeException("File doesn't exist at path $path")
        }

        val fileName = path.fileName.toString()
        val testApkBytes = Files.readAllBytes(path)
        val testApkBlobInfo = BlobInfo.newBuilder(TEST_BUCKET, fileName).build()
        GcStorage.storage.create(testApkBlobInfo, testApkBytes)

        Assert.assertTrue(
            GcStorage.storage.list(TEST_BUCKET).values
                .map { it.name }
                .any { fileName == it }
        )
    }

    private fun silenceComputeEngine() {
        // Set log level then access the storage var to lazy load it.
        // Silence info log about "Failed to detect whether we are running on Google Compute Engine."
        Logger.getLogger(ComputeEngineCredentials::class.java.name).level = Level.OFF
        GcStorage.storage
    }

    fun uploadFiles() {
        val appApk = "../test_app/apks/app-debug.apk"
        val testApk = "../test_app/apks/app-debug-androidTest.apk"
        val ipaZip = "./src/test/kotlin/ftl/fixtures/tmp/EarlGreyExample.zip"
        val xctestrun = "./src/test/kotlin/ftl/fixtures/tmp/EarlGreyExampleSwiftTests_iphoneos12.1-arm64e.xctestrun"

        listOf(appApk, testApk, ipaZip, xctestrun).forEach { file ->
            uploadToMockGcs(Paths.get(file))
        }
    }
}
