package io.zoemeow.dutschedule.ui.view.main

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.GlobalVariables
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.activity.AccountActivity
import io.zoemeow.dutschedule.activity.NewsActivity
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.model.CustomClock
import io.zoemeow.dutschedule.model.ProcessState
import io.zoemeow.dutschedule.ui.component.main.AffectedLessonsSummaryItem
import io.zoemeow.dutschedule.ui.component.main.DateAndTimeSummaryItem
import io.zoemeow.dutschedule.ui.component.main.LessonTodaySummaryItem
import io.zoemeow.dutschedule.ui.component.main.SchoolNewsSummaryItem
import io.zoemeow.dutschedule.ui.component.main.UpdateAvailableSummaryItem
import io.zoemeow.dutschedule.utils.CustomDateUtil
import io.zoemeow.dutschedule.utils.openLink
import io.zoemeow.dutschedule.viewmodel.MainViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days

// This for MainView - Tab View
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activity_MainView_Dashboard(
    modifier: Modifier = Modifier,
    context: Context,
    appearanceState: AppearanceState,
    mainViewModel: MainViewModel,
    onNewsOpened: (() -> Unit)? = null,
    onLoginRequested: (() -> Unit)? = null
) {
    Scaffold(
        modifier = modifier.fillMaxWidth(),
        containerColor = appearanceState.containerColor,
        contentColor = appearanceState.contentColor,
        topBar = {
            TopAppBar(
                title = { Text(text = context.getString(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
    ) { paddingValues ->
        Activity_MainView_Dashboard_Body(
            modifier = Modifier.padding(paddingValues),
            context = context,
            appearanceState = appearanceState,
            mainViewModel = mainViewModel,
            onNewsOpened = onNewsOpened,
            onLoginRequested = onLoginRequested
        )
    }
}

@Composable
fun Activity_MainView_Dashboard_Body(
    modifier: Modifier = Modifier,
    context: Context,
    appearanceState: AppearanceState,
    mainViewModel: MainViewModel,
    onNewsOpened: (() -> Unit)? = null,
    onLoginRequested: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            DateAndTimeSummaryItem(
                padding = PaddingValues(bottom = 10.dp, start = 15.dp, end = 15.dp),
                isLoading = mainViewModel.currentSchoolYearWeek.processState.value == ProcessState.Running,
                currentSchoolWeek = mainViewModel.currentSchoolYearWeek.data.value,
                opacity = appearanceState.componentOpacity
            )
            LessonTodaySummaryItem(
                padding = PaddingValues(bottom = 10.dp, start = 15.dp, end = 15.dp),
                hasLoggedIn = mainViewModel.accountSession.accountSession.processState.value == ProcessState.Successful,
                isLoading = mainViewModel.accountSession.accountSession.processState.value == ProcessState.Running || mainViewModel.accountSession.subjectSchedule.processState.value == ProcessState.Running,
                clicked = {
                    if (mainViewModel.accountSession.accountSession.processState.value == ProcessState.Successful) {
                        val intent = Intent(context, AccountActivity::class.java)
                        intent.action = AccountActivity.INTENT_SUBJECTINFORMATION
                        context.startActivity(intent)
                    } else {
                        onLoginRequested?.let { it() }
                    }
                },
                affectedList = mainViewModel.accountSession.subjectSchedule.data.filter { subSch ->
                    subSch.subjectStudy.scheduleList.any { schItem -> schItem.dayOfWeek + 1 == CustomDateUtil.getCurrentDayOfWeek() } &&
                            subSch.subjectStudy.scheduleList.any { schItem ->
                                schItem.lesson.end >= CustomClock.getCurrent().toDUTLesson2().lesson
                            }
                }.toList(),
                opacity = appearanceState.componentOpacity
            )
            AffectedLessonsSummaryItem(
                padding = PaddingValues(bottom = 10.dp, start = 15.dp, end = 15.dp),
                hasLoggedIn = mainViewModel.accountSession.accountSession.processState.value == ProcessState.Successful,
                isLoading = mainViewModel.accountSession.accountSession.processState.value == ProcessState.Running || mainViewModel.accountSession.subjectSchedule.processState.value == ProcessState.Running,
                clicked = {
                    if (mainViewModel.accountSession.accountSession.processState.value != ProcessState.Successful) {
                        onLoginRequested?.let { it() }
                    }
                },
                affectedList = arrayListOf(
                    "ie1i0921d - i029di12",
                    "ie1i0921d - i029di12",
                    "ie1i0921d - i029di12",
                    "ie1i0921d - i029di12",
                    "ie1i0921d - i029di12"
                ),
                opacity = appearanceState.componentOpacity
            )
            SchoolNewsSummaryItem(
                padding = PaddingValues(bottom = 10.dp, start = 15.dp, end = 15.dp),
                newsToday = run {
                    val today = LocalDateTime(
                        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                        LocalTime(0, 0, 0)
                    ).toInstant(TimeZone.UTC)

                    // https://stackoverflow.com/questions/77368433/how-to-get-current-date-with-reset-time-0000-with-kotlinx-localdatetime
                    return@run mainViewModel.newsInstance.newsGlobal.data.filter { it.date == today.toEpochMilliseconds() }.size
                },
                newsThisWeek = run {
                    val today = LocalDateTime(
                        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                        LocalTime(0, 0, 0)
                    ).toInstant(TimeZone.UTC)
                    val before7Days = today.minus(7.days)

                    // https://stackoverflow.com/questions/77368433/how-to-get-current-date-with-reset-time-0000-with-kotlinx-localdatetime
                    return@run mainViewModel.newsInstance.newsGlobal.data.filter { it.date <= today.toEpochMilliseconds() && it.date >= before7Days.toEpochMilliseconds() }.size
                },
                clicked = {
                    onNewsOpened?.let { it() }
                },
                isLoading = mainViewModel.newsInstance.newsGlobal.processState.value == ProcessState.Running,
                opacity = appearanceState.componentOpacity
            )
            UpdateAvailableSummaryItem(
                padding = PaddingValues(bottom = 10.dp, start = 15.dp, end = 15.dp),
                isLoading = false,
                updateAvailable = false,
                latestVersionString = "",
                clicked = {
                    context.openLink(
                        url = GlobalVariables.LINK_REPOSITORY_RELEASE,
                        customTab = false,
                    )
                },
                opacity = appearanceState.componentOpacity
            )
        }
    }
}