package com.yyw.copyingioscalculator

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.yyw.copyingioscalculator.ActionEnum.*
import com.yyw.copyingioscalculator.components.ControlPanel
import com.yyw.copyingioscalculator.components.ResizeOutputView
import com.yyw.copyingioscalculator.exprk.Expressions
import com.yyw.copyingioscalculator.ui.theme.CopyingIosCalculatorTheme
import com.yyw.copyingioscalculator.ui.theme.DarkGray
import com.yyw.copyingioscalculator.ui.theme.LightGray
import com.yyw.copyingioscalculator.ui.theme.Orange
import java.math.BigDecimal

const val TAG = "wyy"
const val MAX_LENGTH_OF_SHOW = 9

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(ComposeView(this).apply {
            consumeWindowInsets = false
            setContent {
                CopyingIosCalculatorTheme {
                    CalculatorView()
                }
            }
        })
    }
}

@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreCalculatorViewWithTopAppBar() {
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
            .background(color = MaterialTheme.colors.background)
            .padding(10.dp)
    ) {
        ResizeOutputView(
            appState.showNum, appState.fontSizeOfShowNum, Modifier
                .fillMaxWidth()
                .fillMaxHeight(.3f)
        ) {
            val textSize = (appState.fontSizeOfShowNum * 0.9).toInt()
            appState = appState.copy(fontSizeOfShowNum = textSize)
        }
        ControlPanel(
            appState.actionData, Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .systemBarsPadding()
        ) {
            appState = calculate(appState, it)
        }
    }
}


fun calculate(curState: AppStateUI, input: String): AppStateUI {
    return when (input) {
        in arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".") -> {
            when (curState.action) {
                IDLE -> {
                    val firstNum = if (canInput(curState.showNum)) {
                        getInputNum(curState.showNum, input)
                    } else {
                        curState.showNum
                    }
                    curState.copy(showNum = firstNum)
                }
                COMPLETED -> {
                    val firstNum = getInputNum("0", input)
                    curState.copy(showNum = firstNum, action = IDLE)
                }
                else -> {
                    val secondNum = if (curState.action == OPERATOR) {
                        getInputNum("0", input)
                    } else {
                        if (canInput(curState.showNum)) {
                            getInputNum(curState.showNum, input)
                        } else {
                            curState.showNum
                        }
                    }
                    if (curState.action == OPERATOR) {
                        setActionDataToDefault(curState.opt!!)
                    }
                    curState.copy(showNum = secondNum, action = INPUT_SECOND_NUM)
                }
            }
        }
        in arrayOf("÷", "×", "-", "+") -> {
            when (curState.action) {
                IDLE, COMPLETED -> {
                    setActionDataToSelected(input)
                    curState.copy(
                        inputFirst = curState.showNum,
                        opt = input,
                        action = OPERATOR
                    )
                }
                OPERATOR -> {
                    setActionDataToDefault(curState.opt!!)
                    setActionDataToSelected(input)
                    curState.copy(opt = input)
                }
                INPUT_SECOND_NUM -> {
                    setActionDataToSelected(input)
                    val inputNum: String =
                        getFirstNum(curState.inputFirst, curState.showNum, curState.opt!!).cutShowNumForMaxLength()
                    curState.copy(
                        inputFirst = inputNum,
                        showNum = inputNum,
                        action = OPERATOR,
                        opt = input,
                        fontSizeOfShowNum = 100
                    )
                }
            }
        }
        "=" -> {
            when (curState.action) {
                OPERATOR -> {
                    setActionDataToDefault(curState.opt!!)
                    curState.copy(inputFirst = "", action = COMPLETED)
                }
                INPUT_SECOND_NUM -> {
                    val firstNum = curState.inputFirst.dropEndDot()
                    val secondNum = curState.showNum.dropEndDot()
                    val showNum = getFirstNum(firstNum, secondNum, curState.opt!!).cutShowNumForMaxLength()
                    curState.copy(
                        showNum = showNum,
                        action = COMPLETED,
                        inputFirst = "",
                        opt = null,
                        inputSecond = "",
                        fontSizeOfShowNum = 100
                    )
                }
                else -> {
                    curState.copy(inputFirst = "", action = COMPLETED)
                }
            }
        }
        "AC" -> {
            when (curState.action) {
                IDLE, COMPLETED -> {
                    curState.copy(inputFirst = "", showNum = "0", fontSizeOfShowNum = 100)
                }
                OPERATOR -> {
                    setActionDataToDefault(curState.opt!!)
                    curState.copy(
                        inputFirst = "",
                        showNum = "0",
                        action = IDLE,
                        opt = null,
                        fontSizeOfShowNum = 100
                    )
                }
                INPUT_SECOND_NUM -> {
                    curState.copy(
                        inputFirst = "",
                        showNum = "0",
                        action = IDLE,
                        opt = null,
                        inputSecond = "",
                        fontSizeOfShowNum = 100
                    )
                }
            }
        }
        in arrayOf("+/-", "%") -> {
            val showNum = if (input == "+/-") {
                if (curState.action == OPERATOR) "-0" else curState.showNum.togglePreNum()
            } else {
                curState.showNum.toOnePercent().cutShowNumForMaxLength()
            }
            if (curState.action == OPERATOR) {
                setActionDataToDefault(curState.opt!!)
                curState.copy(action = INPUT_SECOND_NUM, showNum = showNum, fontSizeOfShowNum = 100)
            } else {
                curState.copy(showNum = showNum)
            }
        }
        else -> curState
    }
}

fun canInput(showNum: String): Boolean {
    val numString = showNum.replace("-", "").replace(".", "")
    return numString.length < MAX_LENGTH_OF_SHOW
}

fun String.dropEndDot() = run {
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

fun String.cutShowNumForMaxLength() = run {
    var symbolNum = 0
    if (this.contains("-")) {
        symbolNum++
    }
    if (this.contains(".")) {
        symbolNum++
    }
    if (this.length > MAX_LENGTH_OF_SHOW + symbolNum) {
        this.substring(0, MAX_LENGTH_OF_SHOW + symbolNum)
    } else {
        this
    }
}

fun getFirstNum(inputFirst: String, showNum: String, opt: String): String {
    val preRes = when (opt) {
        "÷" -> {
            Expressions().eval("$inputFirst/$showNum")
        }
        "×" -> {
            Expressions().eval("$inputFirst*$showNum")
        }
        "-" -> {
            Expressions().eval("$inputFirst-$showNum")
        }
        "+" -> {
            Expressions().eval("$inputFirst+$showNum")
        }
        else -> BigDecimal(0)
    }
    return getShowNum(preRes.toString())
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
    var action: ActionEnum = IDLE,
    var showNum: String = "0",
    var fontSizeOfShowNum: Int = 100,
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
