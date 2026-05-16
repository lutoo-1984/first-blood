package com.example.firstblood.model

/* 北京积分落户指标体系 */
enum class PointCategory(val title: String, val maxPoints: Double) {
    EMPLOYMENT("合法稳定就业", 999.0),
    RESIDENCE("合法稳定住所", 999.0),
    EDUCATION("教育背景", 37.0),
    AGE("年龄", 20.0),
    LOCATION_TRANSFER("职住区域", 9.0),
    TAX("纳税", 6.0),
    HONORS("荣誉表彰", 20.0),
    PENALTY("守法记录", -999.0)
}

data class UserAnswers(
    val socialInsuranceYears: Int = 0,        // 社保年限
    val housingType: String = "",             // own / rent / other
    val ownHouseYears: Int = 0,               // 自有住房年限
    val rentYears: Int = 0,                   // 租房年限
    val education: String = "",               // juniorCollege / bachelor / master / doctor / other
    val age: Int = 0,                         // 年龄
    val outsideUrbanResidence: Boolean = false, // 居住地在城六区外
    val outsideUrbanWork: Boolean = false,      // 就业地在城六区外
    val highTax: Boolean = false,              // 近3年年纳税>=10万
    val hasHonors: Boolean = false,            // 省部级以上荣誉
    val detentionCount: Int = 0                // 行政拘留次数
)

data class PointBreakdown(
    val category: PointCategory,
    val points: Double,
    val detail: String
)

data class CalculationResult(
    val totalPoints: Double,
    val breakdowns: List<PointBreakdown>
)

object PointCalculator {

    fun calculate(answers: UserAnswers): CalculationResult {
        val breakdowns = mutableListOf<PointBreakdown>()

        // 1. 合法稳定就业：每连续缴纳社保满1年积3分
        val employmentPoints = minOf(answers.socialInsuranceYears * 3.0, 3.0 * 50.0) // 上限约
        breakdowns.add(
            PointBreakdown(
                PointCategory.EMPLOYMENT, employmentPoints,
                "社保缴纳 ${answers.socialInsuranceYears} 年 × 3分 = ${employmentPoints}分"
            )
        )

        // 2. 合法稳定住所
        var residencePoints = 0.0
        val residenceDetail = when (answers.housingType) {
            "own" -> {
                val pts = minOf(answers.ownHouseYears * 1.0, 50.0)
                residencePoints = pts
                "自有住房 ${answers.ownHouseYears} 年 × 1分 = ${pts}分"
            }
            "rent" -> {
                val pts = minOf(answers.rentYears * 0.5, 25.0)
                residencePoints = pts
                "租赁住房 ${answers.rentYears} 年 × 0.5分 = ${pts}分"
            }
            else -> {
                residencePoints = 0.0
                "其他情况，不计分"
            }
        }
        breakdowns.add(PointBreakdown(PointCategory.RESIDENCE, residencePoints, residenceDetail))

        // 3. 教育背景
        val (eduPoints, eduLabel) = when (answers.education) {
            "doctor" -> 37.0 to "博士"
            "master" -> 26.0 to "硕士"
            "bachelor" -> 15.0 to "本科"
            "juniorCollege" -> 10.5 to "大专"
            else -> 0.0 to "其他"
        }
        breakdowns.add(
            PointBreakdown(
                PointCategory.EDUCATION, eduPoints,
                "学历：$eduLabel，积 ${eduPoints}分"
            )
        )

        // 4. 年龄
        val (agePoints, ageDetail) = if (answers.age <= 45) {
            20.0 to "年龄 ${answers.age} 岁（≤45岁），积 20分"
        } else {
            0.0 to "年龄 ${answers.age} 岁（>45岁），不计分"
        }
        breakdowns.add(PointBreakdown(PointCategory.AGE, agePoints, ageDetail))

        // 5. 职住区域
        var locationPoints = 0.0
        val locationParts = mutableListOf<String>()
        if (answers.outsideUrbanResidence) {
            val pts = minOf(2.0 * 3.0, 6.0) // max 6, 满3年
            locationPoints += pts
            locationParts.add("居住转移 6分")
        }
        if (answers.outsideUrbanWork) {
            val pts = minOf(1.0 * 3.0, 3.0) // max 3, 满3年
            locationPoints += pts
            locationParts.add("就业转移 3分")
        }
        breakdowns.add(
            PointBreakdown(
                PointCategory.LOCATION_TRANSFER, locationPoints,
                if (locationParts.isEmpty()) "未转移，不计分" else locationParts.joinToString(" + ") + " = ${locationPoints}分"
            )
        )

        // 6. 纳税
        val taxPoints = if (answers.highTax) 6.0 else 0.0
        breakdowns.add(
            PointBreakdown(
                PointCategory.TAX, taxPoints,
                if (taxPoints > 0) "近3年连续纳税且年纳税≥10万，积 6分" else "未达到纳税加分条件"
            )
        )

        // 7. 荣誉表彰
        val honorPoints = if (answers.hasHonors) 20.0 else 0.0
        breakdowns.add(
            PointBreakdown(
                PointCategory.HONORS, honorPoints,
                if (honorPoints > 0) "省部级以上劳动模范/道德模范等，积 20分" else "无荣誉表彰加分"
            )
        )

        // 8. 守法记录（扣分）
        val penaltyPoints = -(answers.detentionCount * 30.0)
        if (penaltyPoints < 0) {
            breakdowns.add(
                PointBreakdown(
                    PointCategory.PENALTY, penaltyPoints,
                    "行政拘留 ${answers.detentionCount} 次 × (-30分) = ${penaltyPoints}分"
                )
            )
        }

        val total = breakdowns.sumOf { it.points }
        return CalculationResult(total, breakdowns)
    }
}
