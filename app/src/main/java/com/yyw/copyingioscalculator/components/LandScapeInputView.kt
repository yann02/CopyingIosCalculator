package com.yyw.copyingioscalculator.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yyw.copyingioscalculator.ButtonProperty


/**
 * 下部控制（输入）面板
 * @param actionData 输入面板的数据，含输入按钮文本和相关配色数据
 * @param modifier 从外部设置组件的样式
 * @param onClick 回调用户点击的事件
 */
@Composable
fun LandScapeControlPanel(
    actionData: MutableMap<Int, List<ButtonProperty>>,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {
        actionData.mapKeys { rowArr ->
            Row(
                Modifier.weight(1f).padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowArr.value.map {
                    LandScapeInputButton(
                        it.text,
                        Modifier
                            .weight(if (it.text == "0") 2f else 1f)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(color = it.backgroundColor),
                        it.textColor
                    ) {
                        onClick(it.text)
                    }
                }
            }
        }
    }
}

/**
 * 输入按钮
 * @param text 按钮文本
 * @param modifier 传入样式
 * @param textColor 设置文本字体颜色
 * @param onClick 点击事件的回调
 */
@Composable
fun LandScapeInputButton(text: String, modifier: Modifier, textColor: Color = Color.Unspecified, onClick: () -> Unit = {}) {
    Box(modifier = modifier.then(Modifier.clickable { onClick() }), contentAlignment = Alignment.Center) {
        if (text == "0") {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = text,
                    color = textColor,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        } else {
            Text(text = text, color = textColor, fontSize = 20.sp)
        }
    }
}