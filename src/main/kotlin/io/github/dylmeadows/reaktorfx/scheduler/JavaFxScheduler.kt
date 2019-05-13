package io.github.dylmeadows.reaktorfx.scheduler

import javafx.application.Platform
import reactor.core.scheduler.Schedulers

object JavaFxScheduler {
    val platform = Schedulers.fromExecutor(Platform::runLater)
}