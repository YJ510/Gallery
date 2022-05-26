package com.yj510.gallery

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

abstract class BaseActivity:AppCompatActivity() {
    abstract fun permissionGranted(requestCode: Int)
    abstract fun permissionDenied(requestCode: Int)

    fun requirePermissions(permissions:Array<String>, requestCode:Int){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
        //api 버전 마시멜로우 이하이면 권한처리 x
            permissionGranted(requestCode)
        }
        else{
            //권한이 없을 떄 요청청
           ActivityCompat.requestPermissions(this,permissions,requestCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(grantResults.all{it == PackageManager.PERMISSION_GRANTED}){
            permissionGranted(requestCode)
        }
        else{
            permissionDenied(requestCode)
        }
    }

}