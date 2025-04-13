package org.company.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppContainer(modifier: Modifier = Modifier , content: @Composable () -> Unit) {
    Box {
        content()
    }
}