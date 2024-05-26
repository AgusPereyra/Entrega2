package com.example.entrega1

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import org.json.JSONArray
import java.util.Timer
import kotlin.concurrent.schedule

class GameActivity : AppCompatActivity() {

    var category: String? = ""
    private lateinit var questionList: ArrayList<Question>
    private var totalQuestions = 0
    private var correctQuestions = 0
    private var currentQuestion = -1
    private var comodin = false// necesario para la segunda parte
    private var total = -1 // preguntas que pasaron - comodin(en caso de que se uso)

    private lateinit var categoryView: TextView
    private lateinit var questionView: TextView
    private lateinit var currentView: TextView
    private lateinit var optionsArray: Array<Button>
    private lateinit var btnComodin: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val extras = intent.extras
        this.category = extras?.getString("category")
        loadQuestionsFromJSON(category)

        this.category

        categoryView = findViewById(R.id.categoryView)
        categoryView.text = category?.uppercase()

        questionView = findViewById(R.id.question)
        val option1 = findViewById<Button>(R.id.option1)
        val option2 = findViewById<Button>(R.id.option2)
        val option3 = findViewById<Button>(R.id.option3)
        val option4 = findViewById<Button>(R.id.option4)
        optionsArray = arrayOf(option1, option2, option3, option4)
        btnComodin = findViewById(R.id.buttonComodin)
        currentView = findViewById(R.id.currentView)

        setNextQuestion()

        for (i in optionsArray.indices){
            optionsArray[i].setOnClickListener{ answer(i, optionsArray[i])}
        }

        btnComodin.setOnClickListener { comodin().also { btnComodin.text = "X"
                                                         btnComodin.isClickable = false } }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val mi: MenuInflater = menuInflater;
        mi.inflate(R.menu.menu_game, menu);
        return true; // true indica que debe visualizarse
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId== R.id.menuHome){
            this.finish()
        }else if(item.itemId== R.id.menuAyuda) {
            val i = Intent(this, HelpActivity::class.java)
            startActivity(i)
        }
        return true;
    }

    private fun loadQuestionsFromJSON(category: String?) {
        val fileName = "$category.json"
        val inputStream = assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val text = String(buffer, Charsets.UTF_8)
        // Convertir la cadena JSON en un JSONArray
        val jsonArray = JSONArray(text)
        totalQuestions = jsonArray.length()
        questionList = ArrayList<Question>(totalQuestions)
        for (i in 0..<totalQuestions) {
            val jsonObject = jsonArray.getJSONObject(i)
            val question = jsonObject.getString("question")
            val optionsArray = jsonObject.getJSONArray("options")
            val options = ArrayList<String>()
            for (j in 0..<optionsArray.length())
                options.add(optionsArray.getString(j))
            val correctAnswerIndex = jsonObject.getInt("correctAnswerIndex")
            val q = Question(question, options, correctAnswerIndex)
            questionList.add(q)
        }
    }





    private fun setNextQuestion(){
        currentQuestion++
        total ++
        if(currentQuestion<totalQuestions) {
            setViewTexts()
        }else{
            currentQuestion--
            showResults()
        }
    }

    private fun answer(ans: Int, b: Button){
        for (i in optionsArray.indices){
            optionsArray[i].isClickable = false
            btnComodin.isClickable = false
        }
        val correct = questionList[currentQuestion].getCorrectAnswerIndex()
        if ( correct == ans ){
            correctQuestions++
            b.setBackgroundColor( getColor( R.color.green ) )//verde
        } else {
            b.setBackgroundColor( getColor( R.color.red ) ) //rojo
            optionsArray[correct].setBackgroundColor( getColor( R.color.gray ) )
        }
        Timer().schedule(2000) {
           runOnUiThread {
               setNextQuestion()
               for (i in optionsArray.indices){
                   optionsArray[i].isClickable = true
                   if( !comodin) btnComodin.isClickable = true
               }}
        }
    }

    private fun comodin(){
        this.comodin = true
        total --
        btnComodin.setBackgroundColor(getColor(R.color.gray))
        this.setNextQuestion()
    }

    private fun showResults(){
        if(comodin) totalQuestions --
        val alert = EndgameDialogFragment(totalQuestions, correctQuestions, comodin, category)
        alert.show(supportFragmentManager, category)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("category", category)
        outState.putInt("correctQuestions", correctQuestions)
        outState.putInt("currentQuestion", currentQuestion)
        outState.putInt("total", total)
        outState.putBoolean("comodin", comodin)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        category = savedInstanceState.getString("category")
        correctQuestions = savedInstanceState.getInt("correctQuestions")
        currentQuestion = savedInstanceState.getInt("currentQuestion")
        total = savedInstanceState.getInt("total")
        setViewTexts()
        comodin = savedInstanceState.getBoolean("comodin")
        if (comodin){
            btnComodin.isClickable = false
            btnComodin.text = "X"
            btnComodin.setBackgroundColor(getColor(R.color.gray))
        }
    }

    private fun setViewTexts(){
        questionView.text = questionList[currentQuestion].getQuestion()
        currentView.text = "${this.correctQuestions}/${total}"
        for (i in optionsArray.indices){
            optionsArray[i].setBackgroundColor(getColor(R.color.original))
            optionsArray[i].text = questionList[currentQuestion].getOptions()[i]
        }
    }

}