package io.zoemeow.dutschedule.ui.component.news

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import io.dutwrapperlib.dutwrapper.objects.enums.LessonStatus
import io.dutwrapperlib.dutwrapper.objects.enums.NewsType
import io.dutwrapperlib.dutwrapper.objects.news.NewsGlobalItem
import io.dutwrapperlib.dutwrapper.objects.news.NewsSubjectItem
import io.zoemeow.dutschedule.util.CustomDateUtils

@Composable
fun NewsDetailScreen(
    newsItem: NewsGlobalItem,
    newsType: NewsType,
    darkMode: Boolean = false,
    padding: PaddingValues = PaddingValues(0.dp),
    linkClicked: ((String) -> Unit)? = null
) {
    when (newsType) {
        NewsType.Global -> {
            NewsDetailBody_NewsGlobal(
                darkMode = darkMode,
                padding = padding,
                newsItem = newsItem,
                linkClicked = linkClicked
            )
        }
        NewsType.Subject -> {
            NewsDetailBody_NewsSubject(
                darkMode = darkMode,
                padding = padding,
                newsItem = newsItem as NewsSubjectItem,
                linkClicked = linkClicked
            )
        }
    }
}

@Composable
private fun NewsDetailBody_NewsGlobal(
    darkMode: Boolean = false,
    padding: PaddingValues,
    newsItem: NewsGlobalItem,
    linkClicked: ((String) -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = newsItem.title,
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = "⏱ ${
                    CustomDateUtils.dateToString(
                        newsItem.date,
                        "dd/MM/yyyy",
                        "UTC"
                    )
                } (${CustomDateUtils.unixToDuration(newsItem.date)})",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 7.dp)
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            val annotatedString = buildAnnotatedString {
                if (newsItem.contentString != null) {
                    // Parse all string to annotated string.
                    append(newsItem.contentString)
                    // Adjust color for annotated string to follow system mode.
                    addStyle(
                        style = SpanStyle(color = MaterialTheme.colorScheme.inverseSurface),
                        start = 0,
                        end = newsItem.contentString.length
                    )
                    // Adjust for detected link.
                    newsItem.links?.forEach {
                        addStringAnnotation(
                            tag = it.position!!.toString(),
                            annotation = it.url!!,
                            start = it.position,
                            end = it.position + it.text!!.length
                        )
                        addStyle(
                            style = SpanStyle(color = Color(0xff64B5F6)),
                            start = it.position,
                            end = it.position + it.text.length
                        )
                    }
                }
            }
            SelectionContainer {
                ClickableText(
                    text = annotatedString,
                    style = MaterialTheme.typography.bodyLarge,
                    onClick = {
                        try {
                            newsItem.links?.forEach { item ->
                                annotatedString
                                    .getStringAnnotations(item.position!!.toString(), it, it)
                                    .firstOrNull()
                                    ?.let { url ->
                                        var urlTemp = url.item
                                        urlTemp =
                                            urlTemp.replace(
                                                "http://",
                                                "http://",
                                                ignoreCase = true
                                            )
                                        urlTemp = urlTemp.replace(
                                            "https://",
                                            "https://",
                                            ignoreCase = true
                                        )
                                        linkClicked?.let { it(urlTemp) }
                                    }
                            }
                        } catch (_: Exception) {
                            // TODO: Exception for can't open link here!
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun NewsDetailBody_NewsSubject(
    darkMode: Boolean = false,
    padding: PaddingValues,
    newsItem: NewsSubjectItem,
    linkClicked: ((String) -> Unit)? = null
) {
    val optionsScrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(optionsScrollState)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = String.format(
                    "Subject news from %s",
                    newsItem.lecturerName
                ),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "⏱ ${
                    CustomDateUtils.dateToString(
                        newsItem.date,
                        "dd/MM/yyyy",
                        "UTC"
                    )
                } (${CustomDateUtils.unixToDuration(newsItem.date)})",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 7.dp)
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            // Affecting classrooms.
            var affectedClassrooms = ""
            newsItem.affectedClass.forEach { className ->
                if (affectedClassrooms.isEmpty()) {
                    affectedClassrooms = "\n- ${className.subjectName}"
                } else {
                    affectedClassrooms += "\n- ${className.subjectName}"
                }
                var first = true
                for (item in className.codeList) {
                    if (first) {
                        affectedClassrooms += " ["
                        first = false
                    } else {
                        affectedClassrooms += ", "
                    }
                    affectedClassrooms += "${item.studentYearId.lowercase()}.${item.classId.uppercase()}"
                }
                affectedClassrooms += "]"
            }
            Text(
                text = String.format(
                    "Applied to: %s",
                    affectedClassrooms
                ),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            // Affecting lessons, hour, room.
            if (arrayListOf(
                    LessonStatus.Leaving,
                    LessonStatus.MakeUp
                ).contains(newsItem.lessonStatus)
            ) {
                Text(
                    text =
                    String.format(
                        "Status: %s",
                        when (newsItem.lessonStatus) {
                            LessonStatus.Leaving -> "Leaving"
                            LessonStatus.MakeUp -> "Make up"
                            else -> "(unknown)"
                        }
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.size(5.dp))
                Text(
                    text = String.format(
                        when (newsItem.lessonStatus) {
                            LessonStatus.Leaving -> "Lesson will leave: %s"
                            LessonStatus.MakeUp -> "Lesson will make up: %s"
                            else -> "Lesson will leave: %s"
                        },
                        newsItem.affectedLesson
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.size(5.dp))
                Text(
                    text = String.format(
                        "Date: %s",
                        CustomDateUtils.dateToString(newsItem.affectedDate, "dd/MM/yyyy", "UTC")
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                )
                if (newsItem.lessonStatus == LessonStatus.MakeUp) {
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(
                        text =
                        String.format(
                            "Make up in room: %s",
                            newsItem.affectedRoom
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            } else {
                val annotatedString = buildAnnotatedString {
                    if (newsItem.contentString != null) {
                        // Parse all string to annotated string.
                        append(newsItem.contentString)
                        // Adjust color for annotated string to follow system mode.
                        addStyle(
                            style = SpanStyle(color = MaterialTheme.colorScheme.inverseSurface),
                            start = 0,
                            end = newsItem.contentString.length
                        )
                        // Adjust for detected link.
                        newsItem.links?.forEach {
                            addStringAnnotation(
                                tag = it.position!!.toString(),
                                annotation = it.url!!,
                                start = it.position,
                                end = it.position + it.text!!.length
                            )
                            addStyle(
                                style = SpanStyle(color = Color(0xff64B5F6)),
                                start = it.position,
                                end = it.position + it.text.length
                            )
                        }
                    }
                }
                SelectionContainer {
                    ClickableText(
                        text = annotatedString,
                        style = MaterialTheme.typography.bodyLarge,
                        onClick = {
                            try {
                                newsItem.links?.forEach { item ->
                                    annotatedString
                                        .getStringAnnotations(
                                            item.position!!.toString(),
                                            it,
                                            it
                                        )
                                        .firstOrNull()
                                        ?.let { url ->
                                            var urlTemp = url.toString()
                                            urlTemp =
                                                urlTemp.replace(
                                                    "http://",
                                                    "http://",
                                                    ignoreCase = true
                                                )
                                            urlTemp = urlTemp.replace(
                                                "https://",
                                                "https://",
                                                ignoreCase = true
                                            )
                                            linkClicked?.let { it(urlTemp) }
                                        }
                                }
                            } catch (_: Exception) {
                                // TODO: Exception for can't open link here!
                            }
                        }
                    )
                }
            }
        }
    }
}