package com.yyw.copyingioscalculator

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yyw.copyingioscalculator.ActionEnum.*
import com.yyw.copyingioscalculator.exprk.Expressions
import com.yyw.copyingioscalculator.ui.theme.*
import java.math.BigDecimal
import java.math.RoundingMode

const val TAG = "wyy"
const val MAX_LENGTH_OF_SHOW = 9

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
        ShowResizeView(appState.showNum, appState.fontSizeOfShowNum) {
            val textSize = (appState.fontSizeOfShowNum * 0.9).toInt()
            appState = appState.copy(fontSizeOfShowNum = textSize)
        }
        Column(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(color = Color.Green)
        ) {
            appState.actionData.mapKeys { rowArr ->
                Row(
                    Modifier
                        .weight(1f)
                        .background(color = if (rowArr.key % 2 == 0) Color.Cyan else Color.Red),
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
                            appState = calculate(appState, it.text)
                        }
                    }
                }
            }
        }
    }
}

/**
 * 输入框
 * 支持自适应文字大小
 */
@Composable
fun ShowResizeView(showNum: String, textSize: Int, changeFontSize: () -> Unit) {
    var readyToDraw by remember { mutableStateOf(false) }
    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(.3f)
            .background(color = Color.Blue),
        contentAlignment = Alignment.BottomEnd
    ) {
        Surface(shape = RoundedCornerShape(8.dp), color = Color.Red) {
            SelectionContainer {
                Text(
                    text = showNum,
                    fontSize = textSize.sp,
                    color = Color.White,
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
                curState.copy(showNum = showNum, fontSizeOfShowNum = 100)
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
    val preResStr = preRes.toString()
    val res = if (preResStr.contains(".")) {
        val preResArrByDot = preResStr.split(".")
        preRes.setScale(preResStr.length - preResArrByDot[0].replace("-", "").length - 1, RoundingMode.UP)
    } else {
        preRes
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
                    modifier = Modifier
                        .weight(1f)
                        .background(color = Color.Red),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        } else {
            Text(text = text, color = textColor, fontSize = 40.sp, modifier = Modifier.background(color = Color.Red))
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
