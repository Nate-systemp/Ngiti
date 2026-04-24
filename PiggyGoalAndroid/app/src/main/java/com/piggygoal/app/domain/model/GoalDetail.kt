package com.piggygoal.app.domain.model

data class GoalDetail(
    val goal: Goal,
    val deposits: List<Deposit>,
)
