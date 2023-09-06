package com.quizGame.quizgame

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.quizgame.databinding.ActivityQuizBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class QuizActivity : AppCompatActivity() {

    lateinit var quizBinding: ActivityQuizBinding
    //next 2 lines are to access in the database
    private val database = FirebaseDatabase.getInstance()
    private val databaseReference = database.reference.child("questions")

    var question = ""
    var answerA = ""
    var answerB = ""
    var answerC = ""
    var answerD = ""
    var correctAnswer = ""
    var questionCount = 0   //this is for the number of questions in the database, it'll change in the future
    var questionNumber = 0

    private var userAnswer = ""
    private var userCorrect = 0
    private var userWrong = 0
    //creating the timer
    private lateinit var timer : CountDownTimer
    private val totalTime =  30000L //time has to be defined in milliseconds. L means long datatype
    var timerContinue = false
    var leftTime = totalTime
    private val scoreRef = database.reference
    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser

    val questions = HashSet<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizBinding = ActivityQuizBinding.inflate(layoutInflater)
        val view = quizBinding.root
        setContentView(view)

        do{
            val number = kotlin.random.Random.nextInt(1, 11)
            Log.d("number", number.toString())
            questions.add(number)
        }while (questions.size < 5)
        Log.d("numberOfQuestions", questions.toString())

        gameLogic()

        quizBinding.buttonNext.setOnClickListener {
            resetTimer()
            gameLogic()
        }

        quizBinding.buttonFinish.setOnClickListener {
            sendScore()
        }
        quizBinding.textViewA.setOnClickListener {
            pauseTimer()
            userAnswer = "a"
            if (correctAnswer == userAnswer) {
                quizBinding.textViewA.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.textViewCorrect.text = userCorrect.toString()
            }else {
                quizBinding.textViewA.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.textViewWrong.text = userWrong.toString()
                findAnswer()
            }
            disableClickableOfOptions()
        }
        quizBinding.textViewB.setOnClickListener {
            pauseTimer()
            userAnswer = "b"
            if (correctAnswer == userAnswer) {
                quizBinding.textViewB.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.textViewCorrect.text = userCorrect.toString()
            }else {
                quizBinding.textViewB.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.textViewWrong.text = userWrong.toString()
                findAnswer()
            }
            disableClickableOfOptions()
        }
        quizBinding.textViewC.setOnClickListener {
            pauseTimer()
            userAnswer = "c"
            if (correctAnswer == userAnswer) {
                quizBinding.textViewC.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.textViewCorrect.text = userCorrect.toString()
            }else {
                quizBinding.textViewC.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.textViewWrong.text = userWrong.toString()
                findAnswer()
            }
            disableClickableOfOptions()
        }
        quizBinding.textViewD.setOnClickListener {
            pauseTimer()
            userAnswer = "d"
            if (correctAnswer == userAnswer) {
                quizBinding.textViewD.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.textViewCorrect.text = userCorrect.toString()
            }else {
                quizBinding.textViewD.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.textViewWrong.text = userWrong.toString()
                findAnswer()
            }
            disableClickableOfOptions()
        }

    }
    private fun gameLogic() {
        restoreOptions()

        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) { //this is for data retrieving

                questionCount = snapshot.childrenCount.toInt()

                if (questionNumber < questions.size) {
                    question = snapshot.child(questions.elementAt(questionNumber).toString()).child("q").value.toString()
                    answerA = snapshot.child(questions.elementAt(questionNumber).toString()).child("a").value.toString()
                    answerB = snapshot.child(questions.elementAt(questionNumber).toString()).child("b").value.toString()
                    answerC = snapshot.child(questions.elementAt(questionNumber).toString()).child("c").value.toString()
                    answerD = snapshot.child(questions.elementAt(questionNumber).toString()).child("d").value.toString()
                    correctAnswer = snapshot.child(questions.elementAt(questionNumber).toString()).child("answer").value.toString()

                    quizBinding.textViewQuestion.text = question
                    quizBinding.textViewA.text = answerA
                    quizBinding.textViewB.text = answerB
                    quizBinding.textViewC.text = answerC
                    quizBinding.textViewD.text = answerD

                    quizBinding.progressBarQuiz.visibility = View.INVISIBLE
                    quizBinding.linearLayoutInfo.visibility = View.VISIBLE
                    quizBinding.linearLayoutQuestions.visibility = View.VISIBLE
                    quizBinding.linearLayoutButtons.visibility = View.VISIBLE
                    startTimer()
                } else {
                    val dialogMessage = AlertDialog.Builder(this@QuizActivity)
                    dialogMessage.setTitle("Quiz Game")
                    dialogMessage.setMessage("Congratulations!!!\n you answered all the questions. Do you want to see the results?")
                    dialogMessage.setCancelable(false)
                    dialogMessage.setPositiveButton("See Result"){dialogWindow, position ->
                        sendScore()
                    }
                    dialogMessage.setNegativeButton("Play Again"){ dialogWindow, position ->
                        val intent = Intent(this@QuizActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    dialogMessage.create().show()
                }

                questionNumber++

            }

            //this next says what happens when theres an error or the database can't be reached
            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun findAnswer() {
        when(correctAnswer) {
            "a" -> quizBinding.textViewA.setBackgroundColor(Color.GREEN)
            "b" -> quizBinding.textViewB.setBackgroundColor(Color.GREEN)
            "c" -> quizBinding.textViewC.setBackgroundColor(Color.GREEN)
            "d" -> quizBinding.textViewD.setBackgroundColor(Color.GREEN)
        }
    }
    fun disableClickableOfOptions() {
        quizBinding.textViewA.isClickable = false
        quizBinding.textViewB.isClickable = false
        quizBinding.textViewC.isClickable = false
        quizBinding.textViewD.isClickable = false
    }
    private fun restoreOptions() {
        quizBinding.textViewA.setBackgroundColor(Color.WHITE)
        quizBinding.textViewB.setBackgroundColor(Color.WHITE)
        quizBinding.textViewC.setBackgroundColor(Color.WHITE)
        quizBinding.textViewD.setBackgroundColor(Color.WHITE)

        quizBinding.textViewA.isClickable = true
        quizBinding.textViewB.isClickable = true
        quizBinding.textViewC.isClickable = true
        quizBinding.textViewD.isClickable = true
    }
    private fun startTimer() {
        //Countdown is an abstract class, so to be able to run it we write object : before it
        timer = object : CountDownTimer(leftTime, 1000){
            override fun onTick(millisUntilFinished: Long) {
                leftTime = millisUntilFinished
                updateCountDownText()

            }

            override fun onFinish() {
                disableClickableOfOptions()
                resetTimer()
                updateCountDownText()
                quizBinding.textViewQuestion.text = "Sorry, you ran out of time. Please continue with the next question"
                timerContinue = false
            }

        }.start()
        timerContinue = true
    }
    fun updateCountDownText() {
       val remainingTime : Int = (leftTime/1000).toInt()
        quizBinding.textViewTime.text = remainingTime.toString()
    }
    private fun pauseTimer() {
        timer.cancel()
        timerContinue = false

    }
    fun resetTimer() {
        pauseTimer()
        leftTime = totalTime
        updateCountDownText()

    }
    fun sendScore() {
        user?.let {
            val userUID =it.uid //it represent the non null user object
            scoreRef.child("scores").child(userUID)

                .child("correct").setValue(userCorrect)

            scoreRef.child("scores").child(userUID)
                .child("wrong").setValue(userWrong).addOnSuccessListener {
                    Toast.makeText(applicationContext, "Score sent to database successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@QuizActivity,  ResultActivity::class.java)
                    startActivity(intent)
                    finish()

                }
        }
    }
}