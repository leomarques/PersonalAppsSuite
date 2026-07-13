package com.personalapps.suite.cannabis.feature.history.presentation

import com.personalapps.suite.cannabis.feature.api.model.CannabisLog
import com.personalapps.suite.cannabis.feature.api.model.CannabisSession
import com.personalapps.suite.cannabis.feature.api.repository.SessionsRepository
import com.personalapps.suite.shared.testing.MainDispatcherRule
import java.time.Instant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FakeSessionsRepository : SessionsRepository {
    private val sessions = MutableStateFlow<List<CannabisSession>>(emptyList())
    private val logs = MutableStateFlow<List<CannabisLog>>(emptyList())

    override fun getAllSessions(): Flow<List<CannabisSession>> = sessions
    override fun getAllLogs(): Flow<List<CannabisLog>> = logs
    override fun getLogsForSession(sessionId: Long): Flow<List<CannabisLog>> = logs

    override suspend fun insertSession(session: CannabisSession): Long = 0L
    override suspend fun deleteSession(session: CannabisSession) {}
    override suspend fun insertLog(log: CannabisLog): Long {
        val list = logs.value.toMutableList()
        val newLog = log.copy(id = (list.size + 1).toLong())
        list.add(newLog)
        logs.value = list
        return newLog.id
    }
    override suspend fun deleteLog(log: CannabisLog) {
        val list = logs.value.toMutableList()
        list.remove(log)
        logs.value = list
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeSessionsRepository()
    private lateinit var viewModel: HistoryViewModel

    @Before
    fun setUp() {
        viewModel = HistoryViewModel(repository)
    }

    @Test
    fun deleteLog_removesLogFromRepository() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.uiState.collect {}
        }
        runCurrent()

        repository.insertLog(
            CannabisLog(
                id = 1,
                sessionId = null,
                strainName = "Pineapple Express",
                method = "Joint",
                amountGrams = 0.5f,
                timestamp = Instant.now(),
                notes = ""
            )
        )
        runCurrent()

        val log = repository.getAllLogs().first().first()
        viewModel.deleteLog(log)
        runCurrent()

        val logs = viewModel.uiState.value.logs
        assertEquals(0, logs.size)
    }
}
