package com.yyw.copyingioscalculator

import android.os.Bundle
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
            CopyingIosCalculatorTheme {
                CalculatorView()
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreCalculatorView() {
    CopyingIosCalculatorTheme {
        CalculatorView()
    }
}

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
                        InputButton(
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
        in arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".") -> {
            when (curState.action) {
                ActionEnum.IDLE -> {
                    val firstNum = getInputNum(curState.showNum, input)
                    curState.copy(showNum = firstNum)
                }
                ActionEnum.COMPLETED -> {
                    val firstNum = getInputNum("0", input)
                    curState.copy(showNum = firstNum, action = ActionEnum.IDLE)
                }
                else -> {
                    val secondNum = getInputNum(if (curState.action == ActionEnum.OPERATOR) "0" else curState.showNum, input)
                    if (curState.action == ActionEnum.OPERATOR) {
                        setActionDataToDefault(curState.opt!!)
                        curState.copy(actionData = mActionData)
                    }
                    curState.copy(showNum = secondNum, action = ActionEnum.INPUT_SECOND_NUM)
                }
            }
        }
        in arrayOf("÷", "×", "-", "+") -> {
            when (curState.action) {
                ActionEnum.IDLE, ActionEnum.COMPLETED -> {
                    setActionDataToSelected(input)
                    curState.copy(
                        inputFirst = curState.showNum,
                        opt = input,
                        action = ActionEnum.OPERATOR,
                        actionData = mActionData
                    )
                }
                ActionEnum.OPERATOR -> {
                    setActionDataToDefault(curState.opt!!)
                    setActionDataToSelected(input)
                    curState.copy(opt = input, actionData = mActionData)
                }
                ActionEnum.INPUT_SECOND_NUM -> {
                    setActionDataToSelected(input)
                    val inputNum: String = getFirstNum(curState.inputFirst, curState.showNum, curState.opt!!)
                    curState.copy(
                        inputFirst = inputNum,
                        showNum = inputNum,
                        action = ActionEnum.OPERATOR,
                        actionData = mActionData,
                        opt = input
                    )
                }
            }
        }
        "=" -> {
            when (curState.action) {
                ActionEnum.OPERATOR -> {
                    setActionDataToDefault(curState.opt!!)
                    curState.copy(inputFirst = "", actionData = mActionData, action = ActionEnum.COMPLETED)
                }
                ActionEnum.INPUT_SECOND_NUM -> {
                    val firstNum = curState.inputFirst.toSubStringWithDot()
                    val secondNum = curState.showNum.toSubStringWithDot()
                    val showNum = getFirstNum(firstNum, secondNum, curState.opt!!)
                    curState.copy(
                        showNum = showNum,
                        action = ActionEnum.COMPLETED,
                        inputFirst = "",
                        opt = null,
                        inputSecond = ""
                    )
                }
                else -> {
                    curState.copy(inputFirst = "", action = ActionEnum.COMPLETED)
                }
            }
        }
        "AC" -> {
            when (curState.action) {
                ActionEnum.IDLE, ActionEnum.COMPLETED -> {
                    curState.copy(inputFirst = "", showNum = "0")
                }
                ActionEnum.OPERATOR -> {
                    setActionDataToDefault(curState.opt!!)
                    curState.copy(inputFirst = "", showNum = "0", actionData = mActionData, action = ActionEnum.IDLE, opt = null)
                }
                ActionEnum.INPUT_SECOND_NUM -> {
                    curState.copy(inputFirst = "", showNum = "0", action = ActionEnum.IDLE, opt = null, inputSecond = "")
                }
            }
        }
        in arrayOf("+/-", "%") -> {
            if (curState.action == ActionEnum.OPERATOR) {
                setActionDataToDefault(curState.opt!!)
                curState.copy(action = ActionEnum.INPUT_SECOND_NUM, actionData = mActionData)
            }
            val showNum = if (input == "+/-") {
                if (curState.action == ActionEnum.OPERATOR) "-0" else curState.showNum.togglePreNum()
            } else {
                curState.showNum.toOnePercent()
            }
            curState.copy(showNum = showNum)
        }
        else -> curState
    }
}

fun String.toSubStringWithDot() = run {
    if (endsWith(".")) {
        substring(0, length - 1)
    } else {
        this
    }
}

fun String.togglePreNum() = run {
    if (startsWith("-")) {
        replace("-", "")
    } else {
        "-$this"
    }
}

fun String.toOnePercent() = run {
    if (this == "0" || this == "-0") "0" else getFirstNum(this, "100", "÷")
}

fun getFirstNum(inputFirst: String, showNum: String, opt: String): String {
    val res = when (opt) {
        "÷" -> {
            inputFirst.toFloat() / showNum.toFloat()
        }
        "×" -> {
            inputFirst.toFloat() * showNum.toFloat()
        }
        "-" -> {
            inputFirst.toFloat() - showNum.toFloat()
        }
        "+" -> {
            inputFirst.toFloat() + showNum.toFloat()
        }
        else -> 1.0
    }
    return getShowNum(res.toString())
}

fun setActionDataToSelected(opt: String) {
    when (opt) {
        "÷" -> {
            mActionData[0]?.get(3)?.let {
                it.textColor = Orange
                it.backgroundColor = Color.White
            }
        }
        "×" -> {
            mActionData[1]?.get(3)?.let {
                it.textColor = Orange
                it.backgroundColor = Color.White
            }
        }
        "-" -> {
            mActionData[2]?.get(3)?.let {
                it.textColor = Orange
                it.backgroundColor = Color.White
            }
        }
        "+" -> {
            mActionData[3]?.get(3)?.let {
                it.textColor = Orange
                it.backgroundColor = Color.White
            }
        }
    }
}

fun setActionDataToDefault(opt: String) {
    when (opt) {
        "÷" -> {
            mActionData[0]?.get(3)?.let {
                it.textColor = Color.White
                it.backgroundColor = Orange
            }
        }
        "×" -> {
            mActionData[1]?.get(3)?.let {
                it.textColor = Color.White
                it.backgroundColor = Orange
            }
        }
        "-" -> {
            mActionData[2]?.get(3)?.let {
                it.textColor = Color.White
                it.backgroundColor = Orange
            }
        }
        "+" -> {
            mActionData[3]?.get(3)?.let {
                it.textColor = Color.White
                it.backgroundColor = Orange
            }
        }
    }
}

fun getInputNum(showNum: String, input: String): String {
    return if (input == ".") {
        if (showNum == "0") {
            "0$input"
        } else {
            if (showNum.contains(".")) {
                showNum
            } else {
                showNum + input
            }
        }
    } else if (input == "0") {
        if (showNum == "-0" || showNum == "0") {
            showNum
        } else {
            showNum + input
        }
    } else {
        when (showNum) {
            "-0" -> "-$input"
            "0" -> input
            else -> showNum + input
        }
    }
}

fun getShowNum(primitiveNum: String): String {
    var res = primitiveNum
    if (res.endsWith(".0")) {
        res = res.replace(".0", "")
    }
    if (res.matches(Regex("\\.[1-9]+0+"))) {
        res = res.replace(Regex("0+$"), "")
    }
    return res
}

@Composable
fun InputButton(text: String, modifier: Modifier, textColor: Color = Color.Unspecified, onClick: () -> Unit = {}) {
    Box(modifier = modifier.then(Modifier.clickable { onClick() }), contentAlignment = Alignment.Center) {
        Text(text = text, color = textColor, fontSize = 40.sp)
    }
}

//val menuArr = arrayOf(
//    arrayOf("C" to LightGray, "+/-" to LightGray, "%" to LightGray, "÷" to Orange),
//    arrayOf("7" to DarkGray, "8" to DarkGray, "9" to DarkGray, "×" to Orange),
//    arrayOf("4" to DarkGray, "5" to DarkGray, "6" to DarkGray, "-" to Orange),
//    arrayOf("1" to DarkGray, "2" to DarkGray, "3" to DarkGray, "+" to Orange),
//    arrayOf("0" to DarkGray, "." to DarkGray, "=" to Orange)
//)
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
 * @property INPUT_SECOND_NUM 用户输入了第二个操作数
 * @property COMPLETED 用户点击了“=”号，完成计算
 */
enum class ActionEnum {
    IDLE, OPERATOR, INPUT_SECOND_NUM, COMPLETED
}
