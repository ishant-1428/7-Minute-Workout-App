package com.example.a7minuteworkout

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minuteworkout.databinding.ActivityExerciseBinding
import java.util.*

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener{

    lateinit var bindinging: ActivityExerciseBinding

    private var restTimer: CountDownTimer? = null
    private var exerciseTimer: CountDownTimer?= null

    private var exerciseProgress: Int =0
    private var restProgress: Int = 0

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    private var tts : TextToSpeech ?= null
    private var media : MediaPlayer ?= null
    private var exerciseAdapter : ExerciseStatusAdapter ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindinging = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(bindinging.root)

        setSupportActionBar(bindinging.toolbarExerciseActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        bindinging.toolbarExerciseActivity.setNavigationOnClickListener {
            setUpCustomDialogBox()
        }
        exerciseList = Constants.defaultExerciseList()
        setUpRestView()

        tts = TextToSpeech(this,this)

        setUpExerciseStatusRecyclerView()
    }

    override fun onDestroy() {
        if(restTimer != null){
            restTimer!!.cancel()
            restProgress =0
        }
        if(exerciseTimer != null){
            exerciseTimer!!.cancel()
            exerciseProgress =0
        }
        if(tts!=null){
            tts!!.stop()
            tts!!.shutdown()
        }

        if(media !=null){
            media!!.stop()
        }
        super.onDestroy()
    }

    @SuppressLint("SetTextI18n")
    private fun setRestProgressBar(){
        bindinging.progressBar.progress = restProgress
        bindinging.exerciseName.text = exerciseList!![currentExercisePosition +1].getName()
        restTimer = object: CountDownTimer(10000,1000){
            override fun onTick(millisUntilFinished: Long) {
                restProgress +=1
                bindinging.progressBar.progress = 10 - restProgress
                bindinging.tvProgress.text = (10 - restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition +=1
                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()
                setUpExerciseOne()
            }
        }.start()

    }

    private fun setUpRestView(){

        try {
            media = MediaPlayer.create(applicationContext,R.raw.press_start)
            media!!.isLooping = false
            media!!.start()
        }catch (e:Exception){
            e.printStackTrace()
        }

        bindinging.llRestView.visibility = View.VISIBLE
        bindinging.llExercise1.visibility = View.GONE
        if(restTimer != null){
            restTimer!!.cancel()
            restProgress =0
        }
        setRestProgressBar()
    }

    private fun setExerciseOne(){
        bindinging.progressBar1.progress = exerciseProgress
        exerciseTimer = object: CountDownTimer(30000,1000){
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress +=1
                bindinging.progressBar1.progress = 30 - exerciseProgress
                bindinging.tvProgress1.text = (30 - exerciseProgress).toString()
            }

            override fun onFinish() {
                if(currentExercisePosition<(exerciseList?.size!! -1)){
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseAdapter!!.notifyDataSetChanged()
                    setUpRestView()
                }else{
                    finish()
                    val intent= Intent(this@ExerciseActivity,FinishActivity::class.java)
                    startActivity(intent)
                }
            }
        }.start()
    }
    private fun setUpExerciseOne(){


        bindinging.llRestView.visibility = View.GONE
        bindinging.llExercise1.visibility = View.VISIBLE
        if(exerciseTimer != null){
            exerciseTimer!!.cancel()
            exerciseProgress =0
        }

        speakOut(exerciseList!![currentExercisePosition].getName())
        bindinging.ivExercise.setImageResource(exerciseList!![currentExercisePosition].getImage())
        bindinging.tvExerciseName.text = exerciseList!![currentExercisePosition].getName()
        setExerciseOne()

    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(Locale.US)
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS","Language is not supported")
            }else{
                Log.e("TTS","Initialization failed")
            }
        }
    }

    private fun speakOut(text : String){
        tts!!.speak(text,TextToSpeech.QUEUE_FLUSH, null,"")
    }

    private fun setUpExerciseStatusRecyclerView(){
        bindinging.rvExerciseStatus.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,
            false)
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!,this)

        bindinging.rvExerciseStatus.adapter = exerciseAdapter
    }

    private fun setUpCustomDialogBox(){
        val customDialog = Dialog(this)

        customDialog.setContentView(R.layout.dialog_custom_box)
        customDialog.show()
        customDialog.findViewById<View>(R.id.yes).setOnClickListener {
            finish()
            customDialog.dismiss()
        }
        customDialog.findViewById<View>(R.id.no).setOnClickListener {
            customDialog.dismiss()
        }
    }
}