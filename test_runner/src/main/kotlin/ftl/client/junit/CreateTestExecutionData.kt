package ftl.client.junit

import com.google.api.services.toolresults.model.Step
import com.google.api.services.toolresults.model.TestCase
import com.google.api.services.toolresults.model.Timestamp
import com.google.testing.model.TestExecution
import com.google.testing.model.ToolResultsStep
import ftl.gc.GcToolResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

internal fun List<TestExecution>.createTestExecutionDataListAsync(): List<TestExecutionData> = runBlocking {
    map { testExecution ->
        async(Dispatchers.IO) {
            // calling this function in async block, speeds up execution because
            // it is making api calls under the hood
            testExecution.createTestExecutionData()
        }
    }.awaitAll()
}

private suspend fun TestExecution.createTestExecutionData(): TestExecutionData {
    val (
        testCases: List<TestCase>,
        step: Step
    ) = getAsync(toolResultsStep)

    return TestExecutionData(
        testExecution = this@createTestExecutionData,
        testCases = testCases,
        step = step,
        timestamp = testCases.getStartTimestamp()
    )
}

private suspend fun getAsync(toolResultsStep: ToolResultsStep) = coroutineScope {
    val response = async { GcToolResults.listAllTestCases(toolResultsStep) }
    val step = async { GcToolResults.getStepResult(toolResultsStep) }
    response.await() to step.await()
}

// Unfortunately is not possible to obtain from api exact the same timestamp as is in autogenerated test_result_1.xml from google cloud.
// This one is a little bit lower but close as possible. The difference is around ~3 seconds.
private fun List<TestCase>.getStartTimestamp(): Timestamp = this
    .mapNotNull { it.startTime }
    .minByOrNull { it.asUnixTimestamp() }
    ?: Timestamp()