package com.yj510.gallery

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.DatabaseUtils
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.yj510.gallery.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.URI
import java.text.SimpleDateFormat

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    val list = ArrayList<Uri>()

    // Permisisons
    val PERM_CAMERA = arrayOf(Manifest.permission.CAMERA)
    val PERM_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    val REQ_STORAGE =99
    val REQ_CAMERA =100

    val TAKE_CAMERA=100
    val TAKE_STORAGE=99


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        //binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnCamera.setOnClickListener {
            requirePermissions(PERM_CAMERA,REQ_CAMERA)
        }
        binding.btnUpload.setOnClickListener {
            requirePermissions(PERM_STORAGE,REQ_STORAGE)
        }
    }

    override fun permissionGranted(requestCode: Int){
        //Toast.makeText(this, "권한이 승인되었습니다.",Toast.LENGTH_SHORT).show()
        when(requestCode){
            REQ_STORAGE ->{
                Toast.makeText(this, "외부 저장소 권한이 승인되었습니다.",Toast.LENGTH_SHORT).show()
                openGallery()
            }
            REQ_CAMERA ->{
                Toast.makeText(this, "외부 저장소 권한이 승인되었습니다.",Toast.LENGTH_SHORT).show()
                openCamera()
            }
        }
    }
    override fun permissionDenied(requestCode: Int){
        when(requestCode){
            REQ_STORAGE ->{
                Toast.makeText(this, "외부 저장소 권한이 승인되지 않으면 \n 기능을 실행할 수 없습니다.",Toast.LENGTH_SHORT).show()
                finish()
            }

            REQ_CAMERA ->{
                Toast.makeText(this, "카메라 권한이 승인되지 않으면 \n 기능을 실행할 수 없습니다.",Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun openCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent,TAKE_CAMERA)
    }

    private fun openGallery(){
        var intent = Intent(Intent.ACTION_PICK)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent,TAKE_STORAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== RESULT_OK){
            when(requestCode){
                TAKE_CAMERA->{ //카메라 촬영 결과를 처리
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    saveBitmapAsJPGFile(imageBitmap)
                    //binding.imageView.setImageBitmap(imageBitmap)
                }
                TAKE_STORAGE->{
                    list.clear()
                    if(data?.clipData!=null){
                        val count = data.clipData!!.itemCount
                        if(count>5){
                            val string =count.toString()+"이하로 선택해주세요"
                            Toast.makeText(applicationContext, string, Toast.LENGTH_LONG)
                            return
                        }
                        else{
                            for( i in 0 until count){
                                val imageUri = data.clipData!!.getItemAt(i).uri
                                list.add(imageUri)
                            }
                        }
                    }
                    else{
                        data?.data?.let{uri ->
                            val imageUri : Uri? = data?.data
                            if(imageUri != null){
                                list.add(imageUri)
                            }
                        }
                    }
                    Toast.makeText(this, "사진수: "+list.count().toString(),Toast.LENGTH_SHORT).show()
                }

            }
            }
        }

    private fun newJpgFileName() : String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())
        return "${filename}.jpg"}

    private fun saveBitmapAsJPGFile(bitmap: Bitmap) {
        val path = File(filesDir, "image")
        if(!path.exists()){
            path.mkdirs()
        }

        val file = File(path, newJpgFileName())
        var imageFile: OutputStream? = null
        try{
            file.createNewFile()
            imageFile = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageFile)
            imageFile.close()
            Toast.makeText(this, file.absolutePath, Toast.LENGTH_LONG).show()    }
        catch (e: Exception){
            null    }
    }
}
