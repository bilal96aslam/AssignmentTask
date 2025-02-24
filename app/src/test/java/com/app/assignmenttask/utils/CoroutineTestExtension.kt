package com.app.assignmenttask.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * This test class is used to test coroutine-based operations in a controlled environment.
 * We use `TestCoroutineScheduler`, `StandardTestDispatcher`, and `TestScope` to ensure:
 * - **Consistent execution**: Prevents flakiness in coroutine-based tests.
 * - **Controlled timing**: Allows manual advancing of time for delay-related tests.
 * - **Main dispatcher override**: Ensures that coroutines run in a test-friendly dispatcher.
 *
 * This approach is preferred in JUnit 5 as `@Rule` from JUnit 4 is not supported. Instead,
 * we initialize the coroutine test setup manually in `@BeforeEach`, or alternatively,
 * we can use `@ExtendWith(CoroutineTestExtension::class)` for reusability.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineTestExtension : BeforeEachCallback, AfterEachCallback {
    val testScheduler = TestCoroutineScheduler()
    val testDispatcher = StandardTestDispatcher(testScheduler)
    val testScope = TestScope(testDispatcher)

    override fun beforeEach(context: ExtensionContext?) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun afterEach(context: ExtensionContext?) {
        Dispatchers.resetMain()
    }
}