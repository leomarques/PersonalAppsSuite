package com.personalapps.suite.cannabis.feature.stats.presentation

import com.personalapps.suite.cannabis.feature.api.model.CannabisLog
import com.personalapps.suite.cannabis.feature.api.model.CannabisSession
import com.personalapps.suite.cannabis.feature.api.repository.SessionsRepository
import com.personalapps.suite.shared.testing.MainDispatcherRule
import java.time.Instant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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

    override suspend fun insertSession(session: CannabisSession): Long {
        val list = sessions.value.toMutableList()
        val newSess = session.copy(id = (list.size + 1).toLong())
        list.add(newSess)
        sessions.value = list
        return newSess.id
    }
    override suspend fun deleteSession(session: CannabisSession) {}
    override suspend fun insertLog(log: CannabisLog): Long {
        val list = logs.value.toMutableList()
        val newLog = log.copy(id = (list.size + 1).toLong())
        list.add(newLog)
        logs.value = list
        return newLog.id
    }
    override suspend fun deleteLog(log: CannabisLog) {}
}

@OptIn(ExperimentalCoroutinesApi::class)
class StatsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeSessionsRepository()
    private lateinit var viewModel: StatsViewModel

    @Before
    fun setUp() {
        viewModel = StatsViewModel(repository)
    }

    @Test
    fun init_loadsLogsAndSessions() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.uiState.collect {}
        }
        runCurrent()

        repository.insertSession(
            CannabisSession(id = 1, title = "Chill", startTime = Instant.now(), endTime = null)
        )
        repository.insertLog(
            CannabisLog(
                id = 1,
                sessionId = 1,
                strainName = "Jack Herer",
                method = "Vape",
                amountGrams = 0.2f,
                timestamp = Instant.now(),
                notes = ""
            )
        )
        runCurrent()

        assertEquals(1, viewModel.uiState.value.sessions.size)
        assertEquals(1, viewModel.uiState.value.logs.size)
    }
}
