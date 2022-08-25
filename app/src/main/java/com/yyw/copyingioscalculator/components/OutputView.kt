package com.yyw.copyingioscalculator.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yyw.copyingioscalculator.exts.numFormatForUS
import com.yyw.copyingioscalculator.ui.theme.CopyingIosCalculatorTheme


/**
 * 上部输入框
 * 支持自适应文字大小
 * @param showNum 显示的输入内容或计算结果
 * @param textSize 设置文本字体大小
 * @param modifier 从外部设置组件的大小
 * @param changeFontSize 从外部设置字体大小
 */
@Composable
fun ResizeOutputView(showNum: String, textSize: Int, modifier: Modifier = Modifier, changeFontSize: () -> Unit = {}) {
    var readyToDraw by remember { mutableStateOf(false) }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        SelectionContainer {
            Text(
                text = showNum.numFormatForUS(),
                fontSize = textSize.sp,
                color = MaterialTheme.colors.secondary,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Visible,
                modifier = Modifier/*.background(color = Color.Green)*/
                    .padding(horizontal = 14.dp)
                    .drawWithContent {
                        if (readyToDraw) {
                            drawContent()
                        }
                    },
                onTextLayout = { textLayoutResult ->
                    if (textLayoutResult.didOverflowWidth) {
                        changeFontSize()
                    } else {
                        readyToDraw = true
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun PreResizeOutputView() {
    CopyingIosCalculatorTheme {
        ResizeOutputView("1234", 100)
    }
}