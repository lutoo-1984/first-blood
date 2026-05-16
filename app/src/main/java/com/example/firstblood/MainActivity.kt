package com.example.firstblood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.firstblood.ui.screen.HomeScreen
import com.example.firstblood.ui.screen.QuestionnaireScreen
import com.example.firstblood.ui.screen.ResultScreen
import com.example.firstblood.ui.theme.FirstBloodTheme
import com.example.firstblood.viewmodel.CalculatorViewModel
import com.example.firstblood.viewmodel.Step

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FirstBloodTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: CalculatorViewModel = viewModel()
                    val currentStep by viewModel::currentStep
                    val result by viewModel::result

                    when (currentStep) {
                        Step.WELCOME -> HomeScreen(
                            onStart = viewModel::startCalculation
                        )

                        Step.RESULT -> {
                            result?.let { res ->
                                ResultScreen(
                                    result = res,
                                    onRestart = viewModel::restart
                                )
                            }
                        }

                        else -> QuestionnaireScreen(
                            currentStep = currentStep,
                            onBack = viewModel::goBack,
                            onSocialInsurance = viewModel::setSocialInsurance,
                            onHousingType = viewModel::setHousingType,
                            onOwnHouseYears = viewModel::setOwnHouseYears,
                            onRentYears = viewModel::setRentYears,
                            onEducation = viewModel::setEducation,
                            onAge = viewModel::setAge,
                            onResidenceLocation = viewModel::setResidenceLocation,
                            onWorkLocation = viewModel::setWorkLocation,
                            onTax = viewModel::setTax,
                            onHonors = viewModel::setHonors,
                            onPenalty = viewModel::setPenalty
                        )
                    }
                }
            }
        }
    }
}
