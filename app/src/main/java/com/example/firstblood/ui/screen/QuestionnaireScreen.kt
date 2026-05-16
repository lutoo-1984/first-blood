package com.example.firstblood.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstblood.viewmodel.Step

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnaireScreen(
    currentStep: Step,
    onBack: () -> Unit,
    onSocialInsurance: (Int) -> Unit,
    onHousingType: (String) -> Unit,
    onOwnHouseYears: (Int) -> Unit,
    onRentYears: (Int) -> Unit,
    onEducation: (String) -> Unit,
    onAge: (Int) -> Unit,
    onResidenceLocation: (Boolean) -> Unit,
    onWorkLocation: (Boolean) -> Unit,
    onTax: (Boolean) -> Unit,
    onHonors: (Boolean) -> Unit,
    onPenalty: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "北京积分落户",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (currentStep != Step.WELCOME) {
                        TextButton(onClick = onBack) {
                            Text("< 上一步")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress indicator
            val stepList = Step.entries.filter { it != Step.WELCOME }
            val currentIndex = stepList.indexOf(currentStep)
            if (currentIndex >= 0) {
                LinearProgressIndicator(
                    progress = { (currentIndex + 1).toFloat() / stepList.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer
                )

                Text(
                    text = "第 ${currentIndex + 1} / ${stepList.size} 步",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(bottom = 16.dp)
                )
            }

            // Question content
            when (currentStep) {
                Step.WELCOME -> { /* handled by HomeScreen */ }

                Step.SOCIAL_INSURANCE -> NumberQuestion(
                    title = "社保缴纳年限",
                    description = "您在北京连续缴纳社会保险有多少年？",
                    unit = "年",
                    range = 0..50,
                    onConfirm = onSocialInsurance
                )

                Step.HOUSING_TYPE -> SingleChoiceQuestion(
                    title = "住房情况",
                    description = "您在北京的住房属于哪种类型？",
                    options = listOf(
                        "own" to "自有住房",
                        "rent" to "租赁住房",
                        "other" to "其他（如单位宿舍等）"
                    ),
                    onConfirm = onHousingType
                )

                Step.OWN_HOUSE_YEARS -> NumberQuestion(
                    title = "自有住房年限",
                    description = "您在北京自有住房连续居住了多少年？",
                    unit = "年",
                    range = 0..50,
                    onConfirm = onOwnHouseYears
                )

                Step.RENT_YEARS -> NumberQuestion(
                    title = "租赁住房年限",
                    description = "您在北京租赁住房连续居住了多少年？",
                    unit = "年",
                    range = 0..50,
                    onConfirm = onRentYears
                )

                Step.EDUCATION -> SingleChoiceQuestion(
                    title = "最高学历",
                    description = "您的最高学历是什么？（取最高学历计分，不累计）",
                    options = listOf(
                        "doctor" to "博士研究生（37分）",
                        "master" to "硕士研究生（26分）",
                        "bachelor" to "大学本科（15分）",
                        "juniorCollege" to "大学专科（10.5分）",
                        "other" to "其他（0分）"
                    ),
                    onConfirm = onEducation
                )

                Step.AGE -> NumberQuestion(
                    title = "年龄",
                    description = "您的年龄是多少岁？（45周岁及以下加20分）",
                    unit = "岁",
                    range = 18..80,
                    onConfirm = onAge
                )

                Step.LOCATION_RESIDENCE -> YesNoQuestion(
                    title = "居住区域",
                    description = "您的居住地是否在城六区（东城、西城、朝阳、海淀、丰台、石景山）之外？",
                    onConfirm = onResidenceLocation
                )

                Step.LOCATION_WORK -> YesNoQuestion(
                    title = "就业区域",
                    description = "您的就业地是否在城六区（东城、西城、朝阳、海淀、丰台、石景山）之外？",
                    onConfirm = onWorkLocation
                )

                Step.TAX -> YesNoQuestion(
                    title = "纳税情况",
                    description = "近3年是否连续纳税且每年个人所得税达到10万元以上？",
                    onConfirm = onTax
                )

                Step.HONORS -> YesNoQuestion(
                    title = "荣誉表彰",
                    description = "是否获得过省部级以上劳动模范、全国道德模范等荣誉？",
                    onConfirm = onHonors
                )

                Step.PENALTY -> NumberQuestion(
                    title = "守法记录",
                    description = "近5年是否有行政拘留处罚记录？如有，请填写次数（每次扣30分）",
                    unit = "次",
                    range = 0..20,
                    onConfirm = onPenalty
                )

                Step.RESULT -> { /* handled by ResultScreen */ }
            }
        }
    }
}

@Composable
private fun NumberQuestion(
    title: String,
    description: String,
    unit: String,
    range: IntRange,
    onConfirm: (Int) -> Unit
) {
    var input by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = description,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = input,
            onValueChange = {
                input = it.filter { c -> c.isDigit() }
                error = false
            },
            label = { Text(unit) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = error,
            singleLine = true,
            modifier = Modifier.width(200.dp),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 28.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        )

        if (error) {
            Text(
                text = "请输入 ${range.first}~${range.last} 之间的数字",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val value = input.toIntOrNull()
                if (value != null && value in range) {
                    onConfirm(value)
                } else {
                    error = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = input.isNotBlank()
        ) {
            Text("下一步", fontSize = 16.sp)
        }
    }
}

@Composable
private fun SingleChoiceQuestion(
    title: String,
    description: String,
    options: List<Pair<String, String>>,
    onConfirm: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = description,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        options.forEach { (value, label) ->
            Card(
                onClick = { onConfirm(value) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun YesNoQuestion(
    title: String,
    description: String,
    onConfirm: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = description,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { onConfirm(true) },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("是", fontSize = 16.sp)
            }

            OutlinedButton(
                onClick = { onConfirm(false) },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
            ) {
                Text("否", fontSize = 16.sp)
            }
        }
    }
}
