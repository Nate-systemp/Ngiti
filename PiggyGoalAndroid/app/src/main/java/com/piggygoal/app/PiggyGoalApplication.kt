package com.piggygoal.app

import android.app.Application
import com.piggygoal.app.di.AppContainer

class PiggyGoalApplication : Application() {
    val appContainer: AppContainer by lazy { AppContainer(this) }
}
