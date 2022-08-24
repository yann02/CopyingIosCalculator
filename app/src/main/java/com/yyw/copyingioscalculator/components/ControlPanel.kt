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
 * 下部控制面板
 */
@Composable
fun ControlPanel(actionData: MutableMap<Int, List<ButtonProperty>>, onClick: (String) -> Unit = {}) {
    Column(
        Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .systemBarsPadding()
    ) {
        actionData.mapKeys { rowArr ->
            Row(
                Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowArr.value.map {
                    InputButton(
                        it.text,
                        Modifier
                            .weight(if (it.text == "0") 2f else 1f)
                            .aspectRatio(if (it.text == "0") 2f else 1f)
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


@Composable
fun InputButton(text: String, modifier: Modifier, textColor: Color = Color.Unspecified, onClick: () -> Unit = {}) {
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
                    fontSize = 40.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        } else {
            Text(text = text, color = textColor, fontSize = 40.sp)
        }
    }
}