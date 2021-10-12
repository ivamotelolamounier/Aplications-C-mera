/**
 * Documentação:
 * https://developer.android.com/guide/topics/media/camera?hl=pt-Br
 * Neste exemplo, além da solicitação para acessar a galeria de fotos, o
 * aplicativo, solicita a autorização para o uso da Câmera, e também para
 * que possam ser gravadas no disco interno do aparelho, tanto imagens, como
 * sons (vídeos), através de 'requestPermissions'
 */
package com.ivamotelo.applicationcamera

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var imageUri : Uri? = null

    companion object {
        private val PERMISSION_CODE_IMAGE_PICK = 1000
        private val IMAGE_PICK_CODE = 1001
        private val PERMISSION_CAMERA_CAPTURE = 2000
        private val OPEN_CAMERA_CODE = 2001
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // val btn_pick_Image: Button = findViewById<Button>(R.id.btn_pick_Image)
        //val btn_open_camera: Button = findViewById<Button>(R.id.btn_open_camera)

        btn_pick_Image.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED
                ) {
                    val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permission, PERMISSION_CODE_IMAGE_PICK)
                } else {
                    pickImageFromGalery()
                }
            } else {
                pickImageFromGalery()
            }
        }
        btn_open_camera.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                        val permissions = arrayOf(Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        requestPermissions(permissions, PERMISSION_CAMERA_CAPTURE)
                } else {
                    openCamera()
                }
            } else {
                openCamera()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE_IMAGE_PICK -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //Permissão Leitura
                    pickImageFromGalery()
                } else {
                    Toast.makeText(this, "Permissão negada", Toast.LENGTH_LONG).show()
                }
            }
            PERMISSION_CAMERA_CAPTURE -> {
                if (grantResults.size > 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&  //Permissão de Leitura
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {  //Permissão de gravação e câmera
                    openCamera()
                } else {
                    Toast.makeText(this, "Permissão negada", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun pickImageFromGalery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun openCamera() {
        // Estrutura de captura de imagens
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Nova foto")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image capturada pela câmera")
        // variável que armazena a foto tirada pelo usuário
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, OPEN_CAMERA_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

       // val imageView: ImageView = findViewById(R.id.image_view)


        if (resultCode == Activity.RESULT_OK && resultCode == IMAGE_PICK_CODE) {
            image_view.setImageURI(data?.data)
        }
        if (resultCode == Activity.RESULT_OK && resultCode == OPEN_CAMERA_CODE) {
            image_view.setImageURI(imageUri)
        }
    }
}