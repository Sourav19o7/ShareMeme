package com.example.sharememe

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    var url: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadMeme()
    }

    private fun loadMeme()
    {
        findViewById<ProgressBar>(R.id.progressbar).visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(this)
        val curl = "https://meme-api.herokuapp.com/gimme"

// Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, curl, null,
            Response.Listener { response ->
                 url = response.getString("url")
                //Log.d("success Request", url)
                val imageView = findViewById<ImageView>(R.id.memeimageView)

                // Declaring executor to parse the URL
                val executor = Executors.newSingleThreadExecutor()

                // Once the executor parses the URL
                // and receives the image, handler will load it
                // in the ImageView
                val handler = Handler(Looper.getMainLooper())

                // Initializing the image
                var image: Bitmap? = null

                // Only for Background process (can take time depending on the Internet speed)
                executor.execute {

                    // Image URL
//                    val imageURL = "https://media.geeksforgeeks.org/wp-content/cdn-uploads/gfg_200x200-min.png"

                    // Tries to get the image and post it in the ImageView
                    // with the help of Handler
                    try {
                        val `in` = java.net.URL(url).openStream()
                        image = BitmapFactory.decodeStream(`in`)

                        // Only for making changes in UI
                        handler.post {
                            findViewById<ProgressBar>(R.id.progressbar).visibility = View.GONE
                            imageView.setImageBitmap(image)
                        }
                    }

                    // If the URL doesnot point to
                    // image or any other kind of failure
                    catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            },
            Response.ErrorListener {

            })

// Add the request to the RequestQueue.
        queue.add(jsonObjectRequest)
    }

    fun nextMeme(view: View) {
        loadMeme()
    }

    fun shareMeme(view: View) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text /plain"
        intent.putExtra(Intent.EXTRA_TEXT, "Meme Hai : $url")
        val chooser = Intent.createChooser(intent, "Share this meme using ...")
        startActivity(chooser)
    }
}