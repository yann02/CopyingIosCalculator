package com.yyw.copyingioscalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yyw.copyingioscalculator.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorView()
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun CalculatorView() {
    var appState by remember {
        mutableStateOf(AppStateUI())
    }
    Column(
        Modifier
            .background(color = Background)
            .padding(10.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(.3f),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = appState.showNum, color = Color.White, fontSize = 100.sp
            )
        }
        Column(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            menuArr.map { rowArr ->
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    rowArr.map {
                        IosInputButton(
                            it.first,
                            Modifier
                                .weight(if (it.first == "0") 2f else 1f)
                                .aspectRatio(if (it.first == "0") 2f else 1f)
                                .clip(CircleShape)
                                .background(color = it.second),
                            Color.White
                        ) {
                            appState = calculate(appState, it.first)
                        }
                    }
                }
            }
        }
    }
}

fun calculate(curState: AppStateUI, input: String): AppStateUI {
    return when (input) {
        in "0".."9" -> {
            if (curState.action == ActionEnum.IDLE) {
                val firstNum = curState.inputFirst + input
                curState.copy(inputFirst = firstNum, showNum = firstNum)
            } else {
                val secondNum = curState.inputSecond + input
                curState.copy(inputSecond = secondNum, showNum = secondNum)
            }
        }
        in arrayOf("÷", "×", "—", "+") -> curState.copy(opt = input, action = ActionEnum.OPERATOR)
        "=" -> when (curState.opt) {
            "÷" -> curState.copy(
                showNum = (curState.inputFirst.toInt() / curState.inputSecond.toInt()).toString(),
                action = ActionEnum.IDLE,
                inputFirst = "",
                inputSecond = ""
            )
            "×" -> curState.copy(
                showNum = (curState.inputFirst.toInt() * curState.inputSecond.toInt()).toString(),
                action = ActionEnum.IDLE,
                inputFirst = "",
                inputSecond = ""
            )
            "—" -> curState.copy(
                showNum = (curState.inputFirst.toInt() - curState.inputSecond.toInt()).toString(),
                action = ActionEnum.IDLE,
                inputFirst = "",
                inputSecond = ""
            )
            "+" -> curState.copy(
                showNum = (curState.inputFirst.toInt() + curState.inputSecond.toInt()).toString(),
                action = ActionEnum.IDLE,
                inputFirst = "",
                inputSecond = ""
            )
            else -> curState
        }
        else -> curState
    }
}

@Composable
fun IosInputButton(text: String, modifier: Modifier, textColor: Color = Color.Unspecified, onClick: () -> Unit = {}) {
    Box(modifier = modifier.then(Modifier.clickable { onClick() }), contentAlignment = Alignment.Center) {
        Text(text = text, color = textColor, fontSize = 40.sp)
    }
}

val menuArr = arrayOf(
    arrayOf("C" to LightGray, "+/-" to LightGray, "%" to LightGray, "÷" to Orange),
    arrayOf("7" to DarkGray, "8" to DarkGray, "9" to DarkGray, "×" to Orange),
    arrayOf("4" to DarkGray, "5" to DarkGray, "6" to DarkGray, "—" to Orange),
    arrayOf("1" to DarkGray, "2" to DarkGray, "3" to DarkGray, "+" to Orange),
    arrayOf("0" to DarkGray, "." to DarkGray, "=" to Orange)
)

data class AppStateUI(
    var inputFirst: String = "",
    var opt: String? = null,
    var inputSecond: String = "",
    var action: ActionEnum = ActionEnum.IDLE,
    var showNum: String = "0"
)

/**
 * @property IDLE 默认状态
 * @property OPERATOR 用户点击了运算符
 * @property END 点击了等号
 */
enum class ActionEnum {
    IDLE, OPERATOR, END
}
