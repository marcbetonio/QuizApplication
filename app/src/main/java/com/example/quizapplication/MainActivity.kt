package com.example.quizapplication

import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.quizapplication.ui.theme.QuizApplicationTheme

data class Question(val text: String, val options: List<String>, val correctAnswer: String)

class QuizViewModel : ViewModel() {
    val questions = listOf(
        Question(
            "Is it possible to have an activity without UI to perform action/actions?",
            listOf("Not Possible", "Wrong Question", "Yes, it is possible", "None of the above"),
            "Yes, it is possible"
        ),
        Question(
            "Which folder do you copy and paste an image into?",
            listOf("Layout", "Resources", "Drawable", "Java"), "Drawable"
        ),
        Question(
            "Which component property should be changed to a name that is specific of the components use?",
            listOf("Text", "ID", "Editable", "Content Description"), "ID"
        ),
        Question(
            "Which listener is called for the device to register the enter key press?",
            listOf("OnClickListener", "OnKeyListener", "OnContextClickListener", "OnHoverListener"),
            "OnKeyListener"
        )
    )
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizScreen(QuizViewModel())
        }
    }
}


@Composable
fun QuizScreen(viewModel: QuizViewModel) {
    val currentQuestionIndex = remember { mutableStateOf(0) }
    val questions = viewModel.questions
    val selectedAnswers = remember {
        mutableStateOf<List<String?>>(List(questions.size) { null })
    }
//    val quizSubmitted = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val correctAnswers = questions.map { it.correctAnswer }
    val userAnswers = selectedAnswers.value.filterNotNull()
    val score = userAnswers.count { userAnswer ->
        correctAnswers.any { it == userAnswer }
    }
    val currentQuestion = questions[currentQuestionIndex.value]
    val isCorrect =
        selectedAnswers.value[currentQuestionIndex.value] == currentQuestion.correctAnswer
    var showScore by remember { mutableStateOf(false) }
    var showRestartButton by remember { mutableStateOf(false) }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {


            val currentQuestion = questions[currentQuestionIndex.value]

            QuestionCard(question = currentQuestion,
                selectedAnswer = selectedAnswers.value[currentQuestionIndex.value],
                onAnswerSelected = { answer ->
                    val updatedAnswers = selectedAnswers.value.toMutableList()
                    updatedAnswers[currentQuestionIndex.value] = answer
                    selectedAnswers.value = updatedAnswers.toList()
                }
            )
        }

        Box() {
            Button(
                onClick = {
                    if (currentQuestionIndex.value < questions.lastIndex) {
                        currentQuestionIndex.value++
                        Toast.makeText(
                            context,
                            if (isCorrect) "Correct answer." else "Wrong answer.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        showScore = true
                        showRestartButton = true
                    }
                }
            ) {
                Text(text = if (currentQuestionIndex.value < questions.lastIndex) "Next Question" else "Submit and View Score")
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Box() {
                if (showScore) {
                    Text(
                        text = "Total Score: $score/${questions.size}",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            Box() {
                if (showRestartButton) {
                    Button(
                        onClick = {
                            selectedAnswers.value = List(questions.size) { null }
                            currentQuestionIndex.value = 0
                            showScore = false
                            showRestartButton = false
                            //quizSubmitted.value = false
                            Toast.makeText(context, "Quiz Restarted", Toast.LENGTH_SHORT).show()
                        },
                        enabled = showRestartButton
                    ) {
                        Text(text = "Restart Quiz")
                    }
                }
            }
        }
    }
}


@Composable
fun QuestionCard(question: Question, selectedAnswer: String?, onAnswerSelected: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = question.text)
            Spacer(modifier = Modifier.height(8.dp))

            question.options.forEach { option ->
                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    RadioButton(
                        selected = selectedAnswer == option,
                        onClick = {
                            onAnswerSelected(option)
                        })

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        option,
                        modifier = Modifier
                            .padding(10.dp)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    QuizScreen(QuizViewModel())
}