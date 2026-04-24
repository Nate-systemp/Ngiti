package com.piggygoal.app.ui.navigation

sealed class PiggyGoalDestination(val route: String) {
    data object Home : PiggyGoalDestination("home")
    data object Settings : PiggyGoalDestination("settings")
    data object GoalEditor : PiggyGoalDestination("goal_editor/{goalId}") {
        fun createRoute(goalId: Long? = null): String = "goal_editor/${goalId ?: -1L}"
    }

    data object GoalDetail : PiggyGoalDestination("goal_detail/{goalId}") {
        fun createRoute(goalId: Long): String = "goal_detail/$goalId"
    }
}
