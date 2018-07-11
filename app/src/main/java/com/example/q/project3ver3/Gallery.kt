package com.example.q.project3ver3

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.Base64
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import org.json.JSONArray
import java.io.ByteArrayOutputStream


var imgList = ArrayList<String>()

class Gallery : Fragment() {

    val userId = LoginActivity().myGetUserId()
    val URL = LoginActivity().url
    lateinit var requstQueue : RequestQueue
    lateinit var recyclerView : RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.gallery,container,false)

        recyclerView = layout.findViewById<RecyclerView>(R.id.galleryView)

        loadImage(recyclerView)

        val layout_manager : RecyclerView.LayoutManager = GridLayoutManager(this.context, 3)
        recyclerView.setLayoutManager(layout_manager)

        return layout
    }


    fun loadImage( view : RecyclerView){
        val data = HashMap<String, String>()
        imgList.clear()
        data.put("userId",userId)
        data.put("tag", "image_load")

        val jsonobj = object : JsonObjectRequest(Request.Method.POST, URL, JSONObject(data),
                Response.Listener { response ->
                    val base64List : JSONArray = response.getJSONArray("imgList")

                    for(i in 0 until base64List.length()){
                        val item =  base64List.getJSONObject(i).getString("name")
                        imgList.add(item)

                    }
                    val gallery_adapter = Gallery.ImageAdapter(imgList, this.context)
                    recyclerView.setAdapter(gallery_adapter)
                },
                Response.ErrorListener { error ->
                    error.toString()
                    print("error")
                }
        ) {

            //here I want to post data to server
        }


        MySingleton.getInstance(this.requireContext()).addToRequestQueue(jsonobj)
    }




    class ImageAdapter(val items : ArrayList<String>, val context : Context?) : RecyclerView.Adapter<ViewHolder>() {

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = ViewHolder(LayoutInflater.from(context).inflate(R.layout.image_item, parent, false))
            return view
        }
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val img = Base64.getDecoder().decode(items[position])
            val bitmap = BitmapFactory.decodeByteArray(img, 0, img.size)
            holder.image.setImageBitmap(bitmap)
        }
    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val image = view.findViewById<ImageView>(R.id.GalleryImage)
    }
}