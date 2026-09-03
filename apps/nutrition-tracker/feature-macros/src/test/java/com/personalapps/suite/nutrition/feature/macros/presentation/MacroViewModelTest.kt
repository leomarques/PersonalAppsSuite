package com.personalapps.suite.nutrition.feature.macros.presentation

import com.personalapps.suite.nutrition.feature.api.model.MacroGoal
import com.personalapps.suite.nutrition.feature.api.repository.MacroGoalRepository
import com.personalapps.suite.nutrition.feature.macros.domain.usecase.SaveMacroGoalUseCase
import com.personalapps.suite.shared.common.Result
import com.personalapps.suite.shared.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FakeMacroGoalRepository : MacroGoalRepository {
    private val goal = MutableStateFlow<MacroGoal?>(null)
    override fun getMacroGoal(): Flow<MacroGoal?> = goal
    override suspend fun insertMacroGoal(goal: MacroGoal): Result<Long> {
        this.goal.value = goal
        return Result.Success(1L)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MacroViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeMacroGoalRepository()
    private lateinit var viewModel: MacroViewModel

    @Before
    fun setUp() {
        val saveMacroGoalUseCase = SaveMacroGoalUseCase(repository)
        viewModel = MacroViewModel(repository, saveMacroGoalUseCase)
    }

    @Test
    fun saveMacroGoal_updatesRepositoryAndState() = runTest(mainDispatcherRule.testDispatcher) {
        backgroundScope.launch {
            viewModel.uiState.collect {}
        }
        runCurrent()

        viewModel.saveMacroGoal("2500", "150", "250", "80")
        runCurrent()

        val savedGoal = viewModel.uiState.value.goal
        assertNotNull(savedGoal)
        assertEquals(2500, savedGoal?.calories)
        assertEquals(150, savedGoal?.protein)
        assertEquals(250, savedGoal?.carbs)
        assertEquals(80, savedGoal?.fat)
    }
}
