package com.yyw.copyingioscalculator

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yyw.copyingioscalculator.ui.theme.*

const val TAG = "wyy"

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
            appState.actionData.mapKeys { rowArr ->
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    rowArr.value.map {
                        IosInputButton(
                            it.text,
                            Modifier
                                .weight(if (it.text == "0") 2f else 1f)
                                .aspectRatio(if (it.text == "0") 2f else 1f)
                                .clip(CircleShape)
                                .background(color = it.backgroundColor),
                            it.textColor
                        ) {
                            appState = calculate(appState, it.text)
                        }
                    }
                }
            }
        }
    }
}

fun calculate(curState: AppStateUI, input: String): AppStateUI {
    return when (input) {
//        in "0".."9" -> {
        in arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".") -> {
            if (curState.action == ActionEnum.IDLE) {
                val firstNum = if (input == ".") {
                    if (curState.inputFirst.isEmpty()) {
                        "0$input"
                    } else {
                        if (curState.inputFirst.contains(".")) {
                            curState.inputFirst
                        } else {
                            curState.inputFirst + input
                        }
                    }
                } else {
                    curState.inputFirst + input
                }
                curState.copy(inputFirst = firstNum, showNum = firstNum)
            } else {
                val act = mActionData
                val secondNum = if (input == ".") {
                    if (curState.inputSecond.isEmpty()) {
                        "0$input"
                    } else {
                        if (curState.inputSecond.contains(".")) {
                            curState.inputSecond
                        } else {
                            curState.inputSecond + input
                        }
                    }
                } else {
                    curState.inputSecond + input
                }
                when (curState.opt) {
                    "÷" -> {
                        act[0]?.get(3)?.let {
                            it.textColor = Color.White
                            it.backgroundColor = Orange
                        }
                        curState.copy(inputSecond = secondNum, showNum = secondNum, actionData = act)
                    }
                    "×" -> {
                        act[1]?.get(3)?.let {
                            it.textColor = Color.White
                            it.backgroundColor = Orange
                        }
                        curState.copy(inputSecond = secondNum, showNum = secondNum, actionData = act)
                    }
                    "-" -> {
                        act[2]?.get(3)?.let {
                            it.textColor = Color.White
                            it.backgroundColor = Orange
                        }
                        curState.copy(inputSecond = secondNum, showNum = secondNum, actionData = act)
                    }
                    "+" -> {
                        act[3]?.get(3)?.let {
                            it.textColor = Color.White
                            it.backgroundColor = Orange
                        }
                        curState.copy(inputSecond = secondNum, showNum = secondNum, actionData = act)
                    }
                    else -> curState.copy(inputSecond = secondNum, showNum = secondNum)
                }
            }
        }
        "÷" -> {
            val act = mActionData
            act[0]?.get(3)?.let {
                it.backgroundColor = Color.White
                it.textColor = Orange
            }
            if (curState.opt != null) {
                when (curState.opt) {
                    "×" -> {
                        act[1]?.get(3)?.let {
                            it.backgroundColor = Orange
                            it.textColor = Color.White
                        }
                    }
                    "-" -> {
                        act[2]?.get(3)?.let {
                            it.backgroundColor = Orange
                            it.textColor = Color.White
                        }
                    }
                    "+" -> {
                        act[3]?.get(3)?.let {
                            it.backgroundColor = Orange
                            it.textColor = Color.White
                        }
                    }
                }
            }
            if (curState.inputFirst.isEmpty()) {
                curState.copy(inputFirst = curState.showNum, opt = input, action = ActionEnum.OPERATOR, actionData = act)
            } else {
                curState.copy(opt = input, action = ActionEnum.OPERATOR, actionData = act)
            }
        }
        "×" -> {
            val act = mActionData
            act[1]?.get(3)?.let {
                it.backgroundColor = Color.White
                it.textColor = Orange
            }
            if (curState.opt != null) {
                when (curState.opt) {
                    "÷" -> {
                        act[0]?.get(3)?.let {
                            it.backgroundColor = Orange
                            it.textColor = Color.White
                        }
                    }
                    "-" -> {
                        act[2]?.get(3)?.let {
                            it.backgroundColor = Orange
                            it.textColor = Color.White
                        }
                    }
                    "+" -> {
                        act[3]?.get(3)?.let {
                            it.backgroundColor = Orange
                            it.textColor = Color.White
                        }
                    }
                }
            }
            if (curState.inputFirst.isEmpty()) {
                curState.copy(inputFirst = curState.showNum, opt = input, action = ActionEnum.OPERATOR, actionData = act)
            } else {
                curState.copy(opt = input, action = ActionEnum.OPERATOR, actionData = act)
            }
        }
        "-" -> {
            val act = mActionData
            act[2]?.get(3)?.let {
                it.backgroundColor = Color.White
                it.textColor = Orange
            }
            if (curState.opt != null) {
                when (curState.opt) {
                    "÷" -> {
                        act[0]?.get(3)?.let {
                            it.backgroundColor = Orange
                            it.textColor = Color.White
                        }
                    }
                    "×" -> {
                        act[1]?.get(3)?.let {
                            it.backgroundColor = Orange
                            it.textColor = Color.White
                        }
                    }
                    "+" -> {
                        act[3]?.get(3)?.let {
                            it.backgroundColor = Orange
                            it.textColor = Color.White
                        }
                    }
                }
            }
            if (curState.inputFirst.isEmpty()) {
                curState.copy(inputFirst = curState.showNum, opt = input, action = ActionEnum.OPERATOR, actionData = act)
            } else {
                curState.copy(opt = input, action = ActionEnum.OPERATOR, actionData = act)
            }
        }
        "+" -> {
            val act = mActionData
            act[3]?.get(3)?.let {
                it.backgroundColor = Color.White
                it.textColor = Orange
            }
            if (curState.opt != null) {
                when (curState.opt) {
                    "÷" -> {
                        act[0]?.get(3)?.let {
                            it.backgroundColor = Orange
                            it.textColor = Color.White
                        }
                    }
                    "×" -> {
                        act[1]?.get(3)?.let {
                            it.backgroundColor = Orange
                            it.textColor = Color.White
                        }
                    }
                    "-" -> {
                        act[2]?.get(3)?.let {
                            it.backgroundColor = Orange
                            it.textColor = Color.White
                        }
                    }
                }
            }
            if (curState.inputFirst.isEmpty()) {
                curState.copy(inputFirst = curState.showNum, opt = input, action = ActionEnum.OPERATOR, actionData = act)
            } else {
                curState.copy(opt = input, action = ActionEnum.OPERATOR, actionData = act)
            }
        }
        "=" -> {
            if (curState.opt == null || curState.inputFirst.isEmpty() || curState.inputSecond.isEmpty()) {
                curState
            } else {
                if (curState.inputFirst.endsWith(".")) {
                    curState.copy(inputFirst = curState.inputFirst.replace(".", ""))
                }
                if (curState.inputSecond.endsWith(".")) {
                    curState.copy(inputSecond = curState.inputSecond.replace(".", ""))
                }
                when (curState.opt) {
                    "÷" -> curState.copy(
                        showNum = getShowNum((curState.inputFirst.toFloat() / curState.inputSecond.toFloat()).toString()),
                        action = ActionEnum.IDLE,
                        inputFirst = "",
                        opt = null,
                        inputSecond = ""
                    )
                    "×" -> curState.copy(
                        showNum = getShowNum((curState.inputFirst.toFloat() * curState.inputSecond.toFloat()).toString()),
                        action = ActionEnum.IDLE,
                        inputFirst = "",
                        opt = null,
                        inputSecond = ""
                    )
                    "-" -> curState.copy(
                        showNum = getShowNum((curState.inputFirst.toFloat() - curState.inputSecond.toFloat()).toString()),
                        action = ActionEnum.IDLE,
                        inputFirst = "",
                        opt = null,
                        inputSecond = ""
                    )
                    "+" -> curState.copy(
                        showNum = getShowNum((curState.inputFirst.toFloat() + curState.inputSecond.toFloat()).toString()),
                        action = ActionEnum.IDLE,
                        inputFirst = "",
                        opt = null,
                        inputSecond = ""
                    )
                    else -> curState
                }
            }
        }
        else -> curState
    }
}

fun getShowNum(primitiveNum: String): String {
    var res = primitiveNum
    if (res.endsWith(".0")) {
        Log.d(TAG, "匹配到.0")
        res = res.replace(".0", "")
    }
    if (res.matches(Regex(".[1-9]+0+"))) {
        Log.d(TAG, "匹配到.0尾缀")
        res = res.replace(Regex("0+$"), "")
    }
    Log.d(TAG, "res:$res")
    return res
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
    arrayOf("4" to DarkGray, "5" to DarkGray, "6" to DarkGray, "-" to Orange),
    arrayOf("1" to DarkGray, "2" to DarkGray, "3" to DarkGray, "+" to Orange),
    arrayOf("0" to DarkGray, "." to DarkGray, "=" to Orange)
)

val mActionData = mutableMapOf(
    0 to listOf(
        ButtonProperty(text = "AC", textColor = Color.Black, backgroundColor = LightGray),
        ButtonProperty(text = "+/-", textColor = Color.Black, backgroundColor = LightGray),
        ButtonProperty(text = "%", textColor = Color.Black, backgroundColor = LightGray),
        ButtonProperty(text = "÷", textColor = Color.White, backgroundColor = Orange),
    ),
    1 to listOf(
        ButtonProperty(text = "7", textColor = Color.White, backgroundColor = DarkGray),
        ButtonProperty(text = "8", textColor = Color.White, backgroundColor = DarkGray),
        ButtonProperty(text = "9", textColor = Color.White, backgroundColor = DarkGray),
        ButtonProperty(text = "×", textColor = Color.White, backgroundColor = Orange)
    ),
    2 to listOf(
        ButtonProperty(text = "4", textColor = Color.White, backgroundColor = DarkGray),
        ButtonProperty(text = "5", textColor = Color.White, backgroundColor = DarkGray),
        ButtonProperty(text = "6", textColor = Color.White, backgroundColor = DarkGray),
        ButtonProperty(text = "-", textColor = Color.White, backgroundColor = Orange),
    ),
    3 to listOf(
        ButtonProperty(text = "1", textColor = Color.White, backgroundColor = DarkGray),
        ButtonProperty(text = "2", textColor = Color.White, backgroundColor = DarkGray),
        ButtonProperty(text = "3", textColor = Color.White, backgroundColor = DarkGray),
        ButtonProperty(text = "+", textColor = Color.White, backgroundColor = Orange)
    ),
    4 to listOf(
        ButtonProperty(text = "0", textColor = Color.White, backgroundColor = DarkGray),
        ButtonProperty(text = ".", textColor = Color.White, backgroundColor = DarkGray),
        ButtonProperty(text = "=", textColor = Color.White, backgroundColor = Orange)
    )
)

data class AppStateUI(
    var inputFirst: String = "",
    var opt: String? = null,
    var inputSecond: String = "",
    var action: ActionEnum = ActionEnum.IDLE,
    var showNum: String = "0",
    var actionData: MutableMap<Int, List<ButtonProperty>> = mActionData
)

data class ButtonProperty(
    var text: String = "0",
    var textColor: Color = Color.Unspecified,
    var backgroundColor: Color = Color.Unspecified
)

/**
 * @property IDLE 默认状态
 * @property OPERATOR 用户点击了运算符
 * @property END 点击了等号
 */
enum class ActionEnum {
    IDLE, OPERATOR, END
}
