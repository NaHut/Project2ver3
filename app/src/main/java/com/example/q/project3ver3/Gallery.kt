package com.example.q.project3ver3

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray
import org.json.JSONObject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import android.graphics.Bitmap
import android.support.v4.content.FileProvider
import com.android.volley.toolbox.Volley
import com.example.q.project3ver3.utils.RecyclerTouchListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Base64

var imgList = ArrayList<String>()


const val CAMERA_CODE = 1111
const val GALLERY_CODE = 1112

class Gallery : Fragment() {

    val userId = LoginActivity().myGetUserId()
    val URL = LoginActivity().url
    lateinit var requstQueue : RequestQueue
    lateinit var recyclerView : RecyclerView

    var photoUri : Uri? = null
    var currentPhotoPath : String? = null
    lateinit var mImageCaptureName : String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.gallery,container,false)

        recyclerView = layout.findViewById<RecyclerView>(R.id.galleryView)

        loadImage(recyclerView)

        val layout_manager : RecyclerView.LayoutManager = GridLayoutManager(this.context, 3)
        recyclerView.setLayoutManager(layout_manager)


        val fab = layout.findViewById<View>(R.id.fab_add_image) as FloatingActionButton
        fab.setOnClickListener { view ->
            showImageDialog()
        }

        recyclerView.addOnItemTouchListener(RecyclerTouchListener(this.requireContext(),
                recyclerView, object : RecyclerTouchListener.ClickListener {

            override fun onClick(view: View, position: Int) {}

            override fun onLongClick(view: View?, position: Int) {
                showActionsDialog(position);
            }
        }))

        return layout
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CAMERA_CODE){
            insertImage2DB()
        }

        else if (requestCode == GALLERY_CODE){
            if (data != null) {
                sendPicture(data.data)
            }
            System.out.println("For Debugs")
        }
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

    fun showImageDialog(){
        val layoutInflater = LayoutInflater.from(this.requireContext())
        val view = layoutInflater.inflate(R.layout.add_image_dialog, null)

        val userInput = AlertDialog.Builder(this.requireContext())
        userInput.setView(view)

        val btCamera :Button = view.findViewById(R.id.btCamera)
        val btGallery : Button = view.findViewById(R.id.btGallery)

        val neutralButton = userInput.setNeutralButton("Cancel") { _, _ ->
            Toast.makeText(this.requireContext(), "Cancel", Toast.LENGTH_SHORT).show()
        }

        val dialog = userInput.create()

        btCamera.setOnClickListener(object : View.OnClickListener{
            override fun onClick(view: View?) {
                doTakePhoto()
                Toast.makeText(context, "Camera", Toast.LENGTH_SHORT).show()
                dialog.cancel()
            }
        })
        btGallery.setOnClickListener(object : View.OnClickListener{
            override fun onClick(view: View?) {
                doTakeGallery()
                Toast.makeText(context, "Gallery", Toast.LENGTH_SHORT).show()
                dialog.cancel()
            }
        })



        dialog.show()
    }

    fun doTakePhoto(){
        val state = Environment.getExternalStorageState()
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            val myIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (myIntent.resolveActivity(this.requireContext().packageManager) != null) {
                lateinit var photoFile : File

                try {
                    photoFile = createImageFile()
                } catch (ex: IOException) {

                }

                if(photoFile != null){
                    photoUri = FileProvider.getUriForFile(this.requireContext(), this.requireContext().packageName,photoFile)
                    myIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(myIntent, CAMERA_CODE)

                }
            }
        }

    }


    fun doTakeGallery(){
        val myIntent = Intent(Intent.ACTION_PICK)
        myIntent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        myIntent.setType("image/*")
        startActivityForResult(myIntent, GALLERY_CODE)
    }

    fun createImageFile() : File {

        val dir = File(Environment.getExternalStorageDirectory().toString() + "/path/")

        if(!dir.exists()){
            dir.mkdirs()
        }

        val timeStamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        mImageCaptureName = timeStamp + ".png"

        val storageDir : File = File(Environment.getExternalStorageDirectory().toString() + "/path/" + mImageCaptureName)
        currentPhotoPath = storageDir.absolutePath

        return storageDir
    }

    fun insertImage2DB(){

        val bitmap = readImageWithSampling(currentPhotoPath,300,300)
        val base64String = encodeToBase64(bitmap)

        readyPost(base64String, "add_image")

    }

    fun encodeToBase64(image : Bitmap) : String{
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 50, baos)
        val byteArray = baos.toByteArray()

        val encoded : String = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
//        val result = String(byteArray, charset("UTF-8") )
//        return byteArray.toString()

        return encoded
    }

    fun readImageWithSampling(imagePath: String?, targetWidth: Int, targetHeight: Int): Bitmap {
        var targetHeight = targetHeight
        // Get the dimensions of the bitmap
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imagePath, bmOptions)

        val photoWidth = bmOptions.outWidth
        val photoHeight = bmOptions.outHeight

        if (targetHeight <= 0) {
            targetHeight = targetWidth * photoHeight / photoWidth
        }

        // Determine how much to scale down the image
        var scaleFactor = 1
        if (photoWidth > targetWidth) {
            scaleFactor = Math.min(photoWidth / targetWidth, photoHeight / targetHeight)
        }

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor

        return BitmapFactory.decodeFile(imagePath, bmOptions)
    }

    fun readyPost(imageString : String, tag : String){
        val data = HashMap<String,String>()

        data.put("tag", tag)
        data.put("userId", userId)
        data.put("img", imageString)
        data.put("filename", mImageCaptureName)

        postImage(LoginActivity().url,data)
    }


    fun postImage(url: String, data: HashMap<String, String>) {

        var result : String

        val jsonRequest = object : JsonObjectRequest(Request.Method.POST, url, JSONObject(data),
                Response.Listener { response ->
                    result = response.getString("result")

                    loadImage(recyclerView)
                    System.out.println("For Debug")
                },
                Response.ErrorListener { error ->
                    print("error")
                }
        ) {

            //here I want to post data to sever
        }
        MySingleton.getInstance(this.requireContext()).addToRequestQueue(jsonRequest)

    }

    fun sendPicture(imgUri : Uri?){
        val imagePath = getRealPathFromURI(imgUri)
        val imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1)
        currentPhotoPath = imagePath
        mImageCaptureName = imageName

        val bitmap = readImageWithSampling(currentPhotoPath,300,300)
        val base64String = encodeToBase64(bitmap)

        readyPost(base64String, "add_image" )

        System.out.println("FOr debug")
    }

    fun getRealPathFromURI(contentUri : Uri?) : String {

        var column_index = 0
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = this.requireContext().contentResolver.query(contentUri,proj,null,null,null)
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }

        return cursor.getString(column_index)
    }
    private fun showActionsDialog(position: Int) {
        val colors = arrayOf<CharSequence>("Delete")
        var result : Int? = null
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle("Choose option")
        builder.setItems(colors) { dialog, which ->
            imageDelete(position, "delete_image")
            result = DEL_IMAGE
        }
        builder.show()

        if(result == DEL_IMAGE){
            Toast.makeText(this.requireContext(),"Deleted ", Toast.LENGTH_SHORT).show()
        }
    }

    fun imageDelete(position : Int, tag : String){
        val data = HashMap<String,String>()

        data.put("tag", tag)
        data.put("userId", userId)
        data.put("index", ""+position)

        postImage(LoginActivity().url,data)

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