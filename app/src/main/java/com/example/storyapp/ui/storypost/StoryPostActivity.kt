package com.example.storyapp.ui.storypost

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.EXTRA_OUTPUT
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.example.storyapp.R
import com.example.storyapp.data.repository.AuthRepository
import com.example.storyapp.databinding.ActivityStoryPostBinding
import com.example.storyapp.ui.storylist.StoryListActivity.Companion.UPLOAD_STORY_RESULT
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryPostBinding
    private lateinit var currentPhotoPath: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val storyPostViewModel by viewModels<StoryPostViewModel>()

    private var getImage: File? = null
    private var lat: Double? = null
    private var lon: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferences = getSharedPreferences(AuthRepository.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val token = preferences.getString(AuthRepository.TOKEN_KEY, null)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.cameraButton.setOnClickListener { startTakePhoto() }
        binding.galleryButton.setOnClickListener { startGallery() }

        binding.uploadButton.setOnClickListener { uploadStory(token) }

        storyPostViewModel.isLoading.observe(this){
            showLoading(it)
        }

        storyPostViewModel.getImage.observe(this){
            getImage = it
            Log.d(TAG, "IMAGE $getImage")
        }

        storyPostViewModel.galleryPreviewImage.observe(this){ selectedImage ->
            binding.previewImageView.setImageURI(selectedImage)
        }

        storyPostViewModel.cameraPreviewImage.observe(this){selectedImage ->
            binding.previewImageView.setImageBitmap(selectedImage)
        }

        storyPostViewModel.uploadResult.observe(this, Observer {event ->
            val uploadResult = event.getContentIfNotHandled() ?: return@Observer

            if(uploadResult.failedPost != null){
                Toast.makeText(
                    this@StoryPostActivity,
                    uploadResult.failedPost.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (uploadResult.successPost != null){

                val intent = Intent()

                Toast.makeText(
                    this@StoryPostActivity,
                    uploadResult.successPost.message,
                    Toast.LENGTH_SHORT
                ).show()

                setResult(UPLOAD_STORY_RESULT, intent)
                finish()
            }
        })
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude

                    Log.d(TAG, "COORD : $lat, $lon")
                } else {
                    Log.d(TAG, "ERROR LOCATION NULL")
                }
            }

        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == RESULT_OK){
            val selectedImage: Uri = it.data?.data as Uri

            val myImage = uriToFile(selectedImage, this@StoryPostActivity)
            //getImage = myImage
            storyPostViewModel.setImage(myImage)

            Log.d(TAG, "$getImage")

            storyPostViewModel.galleryPreview(selectedImage)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ activityResult ->

        Log.d(TAG, "RESULT CODE : ${activityResult.resultCode}")

        if (activityResult.resultCode == RESULT_OK) {
            val myImage = File(currentPhotoPath)

            Log.d(TAG, " path : $currentPhotoPath")
            //getImage = myImage
            storyPostViewModel.setImage(myImage)

            Log.d(TAG, "$getImage")

            storyPostViewModel.getImage.observe(this){
                getImage = it
                Log.d(TAG, "IMAGE $getImage")

                val result = BitmapFactory.decodeFile(getImage?.path)

                //This seems to be bad practice, but for now it works.
                //Need input on how to decouple.
                storyPostViewModel.cameraPreview(result)
            }
        }
    }

    private fun uploadStory(token: String?) {
        val description = binding.descriptionTextview.text

        if (getImage != null && description.isNotEmpty()) {

            val file = reduceFileImage(getImage as File)

            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val image: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            Log.d(TAG,"$image, $description" )
            Log.d(TAG, "$lat, $lon")

            //Kenapa saya lakukan seperti dibawah ini?
            //saya juga bingung kenapa tidak bisa langsung pakai
//            lat.toString().toRequestBody(),
//            lon.toString().toRequestBody(),
            //Tapi kalau tidak seperti dibawah, story tanpa lokasi tidak bisa dipost
            //Tapi jika seperti ini bisa.
            //Saya kurang paham mengapa.
            //Any help would be appreciated.

            if (lat != null && lon != null) {
                token?.let {
                    storyPostViewModel.uploadStory(
                        image,
                        description.toString().toRequestBody("text/plain".toMediaType()),
                        lat.toString().toRequestBody(),
                        lon.toString().toRequestBody(),
                        token = "Bearer $it"
                    )
                }
            } else {
                token?.let {
                    storyPostViewModel.uploadStory(
                        image,
                        description.toString().toRequestBody("text/plain".toMediaType()),
                        null,
                        null,
                        token = "Bearer $it"
                    )
                }
            }
        } else {
            Toast.makeText(this@StoryPostActivity, getString(R.string.action_insert_image), Toast.LENGTH_SHORT).show()
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose an Image")
        launcherIntentGallery.launch(chooser)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@StoryPostActivity,
                "com.example.storyapp",
                it
            )

            currentPhotoPath = it.absolutePath

            Log.d(TAG, "path : $currentPhotoPath")

            intent.putExtra(EXTRA_OUTPUT, photoURI)

            Log.d(TAG, "URI : $photoURI")

            launcherIntentCamera.launch(intent)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(!allPermissionsGranted()){
                Toast.makeText(
                    this,
                    "Permission Denied",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu_storypost, menu)
        val toggle = menu?.findItem(R.id.menu_item_toggle)
        val switchView = toggle?.actionView as SwitchCompat

        switchView.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                getMyLocation()
            } else {
                lat = null
                lon = null
            }
        }

        return true
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val TAG = "StoryPostActivity"
    }
}