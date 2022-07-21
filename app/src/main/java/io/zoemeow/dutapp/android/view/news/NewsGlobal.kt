package io.zoemeow.dutapp.android.view.news

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.zoemeow.dutapi.objects.NewsGlobalItem
import io.zoemeow.dutapp.android.model.ProcessState
import io.zoemeow.dutapp.android.utils.DateToString
import io.zoemeow.dutapp.android.utils.LazyList_EndOfListHandler

@Composable
fun NewsGlobal(
    newsGlobalList: SnapshotStateList<NewsGlobalItem>,
    isLoading: MutableState<ProcessState>,
    reloadRequested: (Boolean) -> Unit,
    itemClicked: (NewsGlobalItem) -> Unit
) {
    val swipeRefreshState = rememberSwipeRefreshState(true)
    val lazyColumnState = rememberLazyListState()
    swipeRefreshState.isRefreshing = isLoading.value == ProcessState.Running

    LazyList_EndOfListHandler(
        listState = lazyColumnState,
        onLoadMore = { reloadRequested(false) }
    )

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            swipeRefreshState.isRefreshing = true
            reloadRequested(true)
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp),
            state = lazyColumnState,
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
        ) {
            items(newsGlobalList) { item ->
                NewsGlobalItem(
                    date = if (item.date != null) DateToString(item.date, "dd/MM/yyyy") else "",
                    title = item.title ?: "",
                    summary = item.contentString ?: "",
                    clickable = {
                        itemClicked(item)
                    }
                )
            }
        }
    }
}