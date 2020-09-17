package br.com.marcottc.weatherpeek.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.com.marcottc.weatherpeek.databinding.ActivityStartLayoutBinding
import br.com.marcottc.weatherpeek.util.NetworkUtil
import com.google.android.material.snackbar.Snackbar

class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartLayoutBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
    }

    fun goToMainActivity(view: View) {
        if (!NetworkUtil.hasConnectivity(this)) {
            Snackbar.make(binding.coordinatorLayout, "No internet connectivity!", Snackbar.LENGTH_LONG)
                .show()
            return
        }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}