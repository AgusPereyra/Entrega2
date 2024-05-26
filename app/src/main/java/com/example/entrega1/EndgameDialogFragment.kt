package com.example.entrega1

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class EndgameDialogFragment( private val totalQuestions: Int,
                             private val correctAnswers: Int,
                             private val comodin: Boolean,
                             private val category: String?): DialogFragment() {



        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            var comodinUsado = getString(R.string.comodinUsado)
            if(!comodin) comodinUsado = getString(R.string.comodinNoUsado)
            return AlertDialog.Builder(context)
                .setMessage( getString(R.string.puntaje)+": ${correctAnswers}/${totalQuestions}"+"\n"
                            +getString(R.string.comodin)+": "+comodinUsado)
                .setPositiveButton(getString(R.string.reiniciar)) { dialog,which ->
                    val i = Intent(activity, GameActivity::class.java)
                    i.putExtra("category", category)
                    activity?.finish()
                    startActivity(i)
                }
                .setNegativeButton(getString(R.string.aceptar)) { dialog, which ->
                    activity?.finish()
                }
                .create()
        }


}