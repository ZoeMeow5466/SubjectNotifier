package io.zoemeow.dutschedule.ui.view.news.controls

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.dutwrapper.dutwrapper.News
import io.dutwrapper.dutwrapper.News.NewsItem
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.activity.NewsActivity
import io.zoemeow.dutschedule.model.AppearanceState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsPopup(
    context: Context,
    snackBarHostState: SnackbarHostState? = null,
    appearanceState: AppearanceState,
    isVisible: Boolean = false,
    newsType: String? = null,
    newsData: String? = null,
    onLinkClicked: ((String) -> Unit)? = null,
    onMessageReceived: (String, Boolean, String?, (() -> Unit)?) -> Unit, // (msg, forceDismissBefore, actionText, action)
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = { onDismiss() },
            sheetState = sheetState,
            content = {
                Box(
                    modifier = Modifier,
                    contentAlignment = Alignment.BottomEnd,
                    content = {
                        Column() {
                            // Title bar
//                            Row(
//                                modifier = Modifier.fillMaxWidth()
//                                    .padding(horizontal = 10.dp),
//                                horizontalArrangement = Arrangement.SpaceBetween,
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Text(
//                                    context.getString(R.string.news_detail_title),
//                                    style = MaterialTheme.typography.titleLarge
//                                )
//                                IconButton(
//                                    onClick = {
//                                        scope.launch {
//                                            sheetState.hide()
//                                        }.invokeOnCompletion {
//                                            onDismiss()
//                                        }
//                                    },
//                                    content = {
//                                        Icon(
//                                            Icons.Default.Clear,
//                                            context.getString(R.string.action_close),
//                                            modifier = Modifier.size(25.dp)
//                                        )
//                                    }
//                                )
//                            }
                            // Content
                            when (newsType) {
                                NewsActivity.NEWSTYPE_NEWSGLOBAL -> {
                                    NewsDetailScreen(
                                        context = context,
                                        newsItem = Gson().fromJson(newsData, object : TypeToken<NewsItem>() {}.type),
                                        newsType = News.NewsType.Global,
                                        linkClicked = { link ->
                                            onLinkClicked?.let { it(link) }
                                        }
                                    )
                                }
                                NewsActivity.NEWSTYPE_NEWSSUBJECT -> {
                                    NewsDetailScreen(
                                        context = context,
                                        newsItem = Gson().fromJson(newsData, object : TypeToken<News.NewsSubjectItem>() {}.type) as NewsItem,
                                        newsType = News.NewsType.Subject,
                                        linkClicked = { link ->
                                            onLinkClicked?.let { it(link) }
                                        }
                                    )
                                }
                                else -> { }
                            }
                            Spacer(modifier = Modifier.size(80.dp))
                        }
                    }
                )
//                Scaffold(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(horizontal = 5.dp),
//                    contentWindowInsets = WindowInsets(0.dp, 15.dp,0.dp,0.dp),
//                    snackbarHost = { snackBarHostState?.let { SnackbarHost(hostState = it) } },
//                    containerColor = Color.Transparent,
//                    contentColor = appearanceState.contentColor,
//                    topBar = {
//                        TopAppBar(
//                            title = { Text(context.getString(R.string.news_detail_title)) },
//                            colors = TopAppBarDefaults.topAppBarColors(
//                                containerColor = Color.Transparent,
//                                scrolledContainerColor = Color.Transparent
//                            ),
//                            actions = {
//                                IconButton(
//                                    onClick = {
//                                        scope.launch {
//                                            sheetState.hide()
//                                        }.invokeOnCompletion {
//                                            onDismiss()
//                                        }
//                                    },
//                                    content = {
//                                        Icon(
//                                            Icons.Default.Clear,
//                                            context.getString(R.string.action_close),
//                                            modifier = Modifier.size(25.dp)
//                                        )
//                                    }
//                                )
//                            }
//                        )
//                    },
//                    floatingActionButton = {
//                        if (newsType == NewsActivity.NEWSTYPE_NEWSSUBJECT) {
//                            ExtendedFloatingActionButton(
//                                content = {
//                                    Row(verticalAlignment = Alignment.CenterVertically) {
//                                        Icon(Icons.Default.Add, context.getString(R.string.news_detail_addtofilter_fab))
//                                        Spacer(modifier = Modifier.size(5.dp))
//                                        Text(context.getString(R.string.news_detail_addtofilter_fab))
//                                    }
//                                },
//                                onClick = {
//                                    try {
//                                        // TODO: Develop a add news filter function for news subject detail.
//                                        onMessageReceived(context.getString(R.string.feature_not_ready), true, null, null)
//                                    } catch (ex: Exception) {
//                                        ex.printStackTrace()
//                                        onMessageReceived(context.getString(R.string.news_detail_addtofilter_failed), true, null, null)
//                                    }
//                                }
//                            )
//                        }
//                    }
//                ) { paddingValues ->
//                    when (newsType) {
//                        NewsActivity.NEWSTYPE_NEWSGLOBAL -> {
//                            NewsDetailScreen(
//                                context = context,
//                                padding = paddingValues,
//                                newsItem = Gson().fromJson(newsData, object : TypeToken<NewsItem>() {}.type),
//                                newsType = News.NewsType.Global,
//                                linkClicked = { link ->
//                                    onLinkClicked?.let { it(link) }
//                                }
//                            )
//                        }
//                        NewsActivity.NEWSTYPE_NEWSSUBJECT -> {
//                            NewsDetailScreen(
//                                context = context,
//                                padding = paddingValues,
//                                newsItem = Gson().fromJson(newsData, object : TypeToken<News.NewsSubjectItem>() {}.type) as NewsItem,
//                                newsType = News.NewsType.Subject,
//                                linkClicked = { link ->
//                                    onLinkClicked?.let { it(link) }
//                                }
//                            )
//                        }
//                        else -> { }
//                    }
//                }
            }
        )
    }
}