package com.yyw.copyingioscalculator.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yyw.copyingioscalculator.exts.numFormatForUS
import com.yyw.copyingioscalculator.ui.theme.CopyingIosCalculatorTheme

/**
 * 上部输出框，显示结果
 * 支持自适应文字大小
 * @param showNum 显示的输入内容或计算结果
 * @param modifier 从外部设置组件的大小
 */
@Composable
fun LandScapeOutputView(showNum: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterEnd
    ) {
        SelectionContainer {
            Text(
                text = showNum.numFormatForUS(),
                fontSize = 40.sp,
                color = MaterialTheme.colors.secondary,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Visible,
                modifier = Modifier/*.background(color = Color.Green)*/
                    .padding(horizontal = 14.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreOutputView() {
    CopyingIosCalculatorTheme {
        LandScapeOutputView("1234")
    }
}