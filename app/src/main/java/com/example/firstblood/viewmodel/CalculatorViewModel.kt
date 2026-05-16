package com.example.firstblood.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.firstblood.model.CalculationResult
import com.example.firstblood.model.PointCalculator
import com.example.firstblood.model.UserAnswers

/* 问卷步骤定义 */
enum class Step(val displayName: String) {
    WELCOME("开始"),
    SOCIAL_INSURANCE("社保缴纳"),
    HOUSING_TYPE("住房情况"),
    OWN_HOUSE_YEARS("自有住房"),
    RENT_YEARS("租赁住房"),
    EDUCATION("教育背景"),
    AGE("年龄"),
    LOCATION_RESIDENCE("居住区域"),
    LOCATION_WORK("就业区域"),
    TAX("纳税情况"),
    HONORS("荣誉表彰"),
    PENALTY("守法记录"),
    RESULT("计算结果")
}

class CalculatorViewModel : ViewModel() {

    var currentStep by mutableStateOf(Step.WELCOME)
        private set

    var answers by mutableStateOf(UserAnswers())
        private set

    var result by mutableStateOf<CalculationResult?>(null)
        private set

    fun startCalculation() {
        currentStep = Step.SOCIAL_INSURANCE
    }

    fun goBack() {
        currentStep = when (currentStep) {
            Step.WELCOME -> Step.WELCOME
            Step.SOCIAL_INSURANCE -> Step.WELCOME
            Step.HOUSING_TYPE -> Step.SOCIAL_INSURANCE
            Step.OWN_HOUSE_YEARS -> Step.HOUSING_TYPE
            Step.RENT_YEARS -> Step.HOUSING_TYPE
            Step.EDUCATION -> {
                if (answers.housingType == "own") Step.OWN_HOUSE_YEARS
                else if (answers.housingType == "rent") Step.RENT_YEARS
                else Step.HOUSING_TYPE
            }
            Step.AGE -> Step.EDUCATION
            Step.LOCATION_RESIDENCE -> Step.AGE
            Step.LOCATION_WORK -> Step.LOCATION_RESIDENCE
            Step.TAX -> Step.LOCATION_WORK
            Step.HONORS -> Step.TAX
            Step.PENALTY -> Step.HONORS
            Step.RESULT -> Step.PENALTY
        }
    }

    fun setSocialInsurance(years: Int) {
        answers = answers.copy(socialInsuranceYears = years)
        currentStep = Step.HOUSING_TYPE
    }

    fun setHousingType(type: String) {
        answers = answers.copy(housingType = type)
        currentStep = when (type) {
            "own" -> Step.OWN_HOUSE_YEARS
            "rent" -> Step.RENT_YEARS
            else -> Step.EDUCATION
        }
    }

    fun setOwnHouseYears(years: Int) {
        answers = answers.copy(ownHouseYears = years)
        currentStep = Step.EDUCATION
    }

    fun setRentYears(years: Int) {
        answers = answers.copy(rentYears = years)
        currentStep = Step.EDUCATION
    }

    fun setEducation(edu: String) {
        answers = answers.copy(education = edu)
        currentStep = Step.AGE
    }

    fun setAge(age: Int) {
        answers = answers.copy(age = age)
        currentStep = Step.LOCATION_RESIDENCE
    }

    fun setResidenceLocation(years: Int) {
        answers = answers.copy(outsideUrbanResidenceYears = years)
        currentStep = Step.LOCATION_WORK
    }

    fun setWorkLocation(years: Int) {
        answers = answers.copy(outsideUrbanWorkYears = years)
        currentStep = Step.TAX
    }

    fun setTax(high: Boolean) {
        answers = answers.copy(highTax = high)
        currentStep = Step.HONORS
    }

    fun setHonors(has: Boolean) {
        answers = answers.copy(hasHonors = has)
        currentStep = Step.PENALTY
    }

    fun setPenalty(count: Int) {
        answers = answers.copy(detentionCount = count)
        calculate()
    }

    private fun calculate() {
        result = PointCalculator.calculate(answers)
        currentStep = Step.RESULT
    }

    fun restart() {
        currentStep = Step.WELCOME
        answers = UserAnswers()
        result = null
    }
}
