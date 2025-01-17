package io.zoemeow.dutschedule.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DialogRadioButton(
    modifier: Modifier = Modifier,
    title: String,
    isSelected: Boolean,
    isEnabled: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .run {
                if (onClick != null && isEnabled) return@run this.clickable { onClick() }
                else this
            },
        color = Color.Transparent,
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = {
                    RadioButton(
                        selected = isSelected,
                        enabled = isEnabled,
                        onClick = { onClick?.let { it() } }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(title)
                    }
                }
            )
        }
    )
}