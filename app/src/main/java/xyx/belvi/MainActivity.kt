package xyx.belvi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fields.view.*
import xyz.belvi.blurhash.BlurHash
import xyz.belvi.blurhash.blurPlaceHolder

class MainActivity : AppCompatActivity() {

    val blurHash: BlurHash = BlurHash(this, lruSize = 20)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerview.adapter = SampleRecyclerAdapter(sampleResponse())
    }

    private fun sampleResponse(): List<SampleResponse> {
        val response = resources.openRawResource(R.raw.sample).bufferedReader()
            .use { it.readText() }
        return Gson().fromJson(response, object : TypeToken<List<SampleResponse>>() {}.type)
    }

    inner class SampleRecyclerAdapter(private val response: List<SampleResponse>) :
        RecyclerView.Adapter<SampleRecyclerHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleRecyclerHolder {
            return SampleRecyclerHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.fields, parent, false)
            )
        }

        override fun onBindViewHolder(holder: SampleRecyclerHolder, position: Int) {
            holder.bindView(response[position])
        }

        override fun getItemCount(): Int {
            return response.size
        }
    }

    inner class SampleRecyclerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(sampleResponse: SampleResponse) {
            with(itemView) {
                val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
                textView.text = sampleResponse.blur
                Glide.with(this).load(sampleResponse.img)
                    .transition(DrawableTransitionOptions.withCrossFade(factory))
                    .blurPlaceHolder(sampleResponse.blur, imageView, blurHash) { requestBuilder ->
                        requestBuilder.into(imageView)
                    }

            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        blurHash.clean()
    }
}