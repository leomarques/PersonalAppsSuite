package com.personalapps.suite.cannabis.feature.sessions.presentation

import com.personalapps.suite.shared.testing.MainDispatcherRule
import com.personalapps.suite.cannabis.feature.api.model.CannabisLog
import com.personalapps.suite.cannabis.feature.api.model.CannabisSession
import com.personalapps.suite.cannabis.feature.api.repository.SessionsRepository
import java.time.Instant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FakeSessionsRepository : SessionsRepository {
    private val sessions = MutableStateFlow<List<CannabisSession>>(emptyList())
    private val logs = MutableStateFlow<List<CannabisLog>>(emptyList())

    override fun getAllSessions(): Flow<List<CannabisSession>> = sessions
    override fun getAllLogs(): Flow<List<CannabisLog>> = logs

    override fun getLogsForSession(sessionId: Long): Flow<List<CannabisLog>> {
        return MutableStateFlow(logs.value.filter { it.sessionId == sessionId })
    }

    override suspend fun insertSession(session: CannabisSession): Long {
        val list = sessions.value.toMutableList()
        val existingIndex = list.indexOfFirst { it.id == session.id && session.id != 0L }
        val idToReturn: Long
        if (existingIndex != -1) {
            list[existingIndex] = session
            idToReturn = session.id
        } else {
            val newSession = session.copy(id = (list.size + 1).toLong())
            list.add(newSession)
            idToReturn = newSession.id
        }
        sessions.value = list
        return idToReturn
    }

    override suspend fun deleteSession(session: CannabisSession) {
        val list = sessions.value.toMutableList()
        list.remove(session)
        sessions.value = list
        // Cascade delete logs
        val logsList = logs.value.toMutableList()
        logsList.removeAll { it.sessionId == session.id }
        logs.value = logsList
    }

    override suspend fun insertLog(log: CannabisLog): Long {
        val list = logs.value.toMutableList()
        val existingIndex = list.indexOfFirst { it.id == log.id && log.id != 0L }
        val idToReturn: Long
        if (existingIndex != -1) {
            list[existingIndex] = log
            idToReturn = log.id
        } else {
            val newLog = log.copy(id = (list.size + 1).toLong())
            list.add(newLog)
            idToReturn = newLog.id
        }
        logs.value = list
        return idToReturn
    }

    override suspend fun deleteLog(log: CannabisLog) {
        val list = logs.value.toMutableList()
        list.remove(log)
        logs.value = list
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class SessionsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeSessionsRepository()
    private lateinit var viewModel: SessionsViewModel

    @Before
    fun setUp() {
        viewModel = SessionsViewModel(repository)
    }

    @Test
    fun startSession_createsActiveSession() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.sessionsState.collect {}
        }
        backgroundScope.launch {
            viewModel.activeSessionState.collect {}
        }
        runCurrent()

        assertNull(viewModel.activeSessionState.value)

        viewModel.startSession("Evening Relax")
        runCurrent()

        val active = viewModel.activeSessionState.value
        assertNotNull(active)
        assertEquals("Evening Relax", active?.title)
        assertNull(active?.endTime)
    }

    @Test
    fun endActiveSession_setsEndTime() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.sessionsState.collect {}
        }
        backgroundScope.launch {
            viewModel.activeSessionState.collect {}
        }
        runCurrent()

        viewModel.startSession("Chill")
        runCurrent()

        val active = viewModel.activeSessionState.value!!
        viewModel.endActiveSession(active)
        runCurrent()

        assertNull(viewModel.activeSessionState.value)

        val allSessions = repository.getAllSessions().first()
        assertEquals(1, allSessions.size)
        assertNotNull(allSessions.first().endTime)
    }

    @Test
    fun logUsage_addsLogEntry() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.logsState.collect {}
        }

        viewModel.logUsage(null, "Blue Dream", "Vape", 0.2f, "feeling creative")
        runCurrent()

        val logs = repository.getAllLogs().first()
        assertEquals(1, logs.size)
        assertEquals("Blue Dream", logs.first().strainName)
        assertEquals("Vape", logs.first().method)
        assertEquals(0.2f, logs.first().amountGrams)
    }
}
