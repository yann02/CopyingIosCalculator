package com.yyw.copyingioscalculator.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yyw.copyingioscalculator.ui.theme.CopyingIosCalculatorTheme

@Composable
fun MyTopAppBar(onToggleTheme: () -> Unit = {}) {
    TopAppBar(
        title = {
            Text(text = "")
        },
        backgroundColor = MaterialTheme.colors.background,
        actions = {
            Text(
                text = "切换主题",
                color = MaterialTheme.colors.secondary,
                modifier = Modifier
                    .clickable(onClick = onToggleTheme)
                    .padding(10.dp)
            )
        })
}

@Preview
@Composable
fun MyTopAppBarPreviewDark() {
    CopyingIosCalculatorTheme(true) {
        MyTopAppBar()
    }
}

@Preview
@Composable
fun MyTopAppBarPreview() {
    CopyingIosCalculatorTheme(false) {
        MyTopAppBar()
    }
}