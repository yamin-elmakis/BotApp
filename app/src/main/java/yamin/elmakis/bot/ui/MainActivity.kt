package yamin.elmakis.bot.ui

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import yamin.elmakis.bot.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            // Tell the Window that our app is going to responsible for fitting for any system windows.
            // This is similar to the now deprecated:
            // view.setSystemUiVisibility(LAYOUT_STABLE | LAYOUT_FULLSCREEN | LAYOUT_FULLSCREEN)
            window.setDecorFitsSystemWindows(false)
        }
    }
}