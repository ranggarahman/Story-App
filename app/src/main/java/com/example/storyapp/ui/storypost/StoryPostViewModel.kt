package com.example.storyapp.ui.storypost

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.api.ApiConfig
import com.example.storyapp.ui.auth.validator.PostResult
import com.example.storyapp.utils.Event
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class StoryPostViewModel: ViewModel() {

    private val apiService = ApiConfig.getApiService()

    private val _uploadResult = MutableLiveData<Event<PostResult>>()
    val uploadResult: LiveData<Event<PostResult>> = _uploadResult

    private val _galleryPreviewImage = MutableLiveData<Uri>()
    val galleryPreviewImage: LiveData<Uri> = _galleryPreviewImage

    private val _cameraPreviewImage = MutableLiveData<Bitmap>()
    val cameraPreviewImage: LiveData<Bitmap> = _cameraPreviewImage

    private val _getImage  = MutableLiveData<File>()
    val getImage: LiveData<File> = _getImage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun uploadStory(picture: MultipartBody.Part,
                    description: RequestBody,
                    lat: RequestBody?,
                    lon: RequestBody?,
                    token: String) {
        Log.d(TAG, "$lat, $lon")
        _isLoading.value = true
        viewModelScope.launch {
            try {
                Log.d(TAG, "$lat, $lon")
                val response = apiService.postStory(description, picture, lat, lon, token)
                if(response.isSuccessful){
                    val successResult = PostResult(successPost = PostedStoryUserView(response.body()?.message))
                    _uploadResult.value = Event(successResult)
                    Log.d(TAG, "SUCCESS RESPONSE : ${response.body()}")
                    _isLoading.value = false
                } else {
                    val errorResult = PostResult(failedPost = PostedStoryUserView(response.body()?.message))
                    _uploadResult.value = Event(errorResult)
                    Log.e(TAG, "FAILED RESPONSE : ${response.errorBody()}. ERROR CODE : ${response.code()}")
                    Log.e(TAG, "BODY : $description, $picture, $token")
                    _isLoading.value = false
                }

            } catch (e: Exception){
                Log.e(TAG, "ERROR POSTING: ${e.message}")
                _isLoading.value = false
            }
        }
    }

    fun galleryPreview(selectedImage : Uri){
        _galleryPreviewImage.value = selectedImage
    }

    fun cameraPreview(selectedImage : Bitmap){
        _cameraPreviewImage.value = selectedImage
    }

    fun setImage(image : File){
        _getImage.value = image
        Log.d(TAG, "image ${_getImage.value}")
    }

    companion object {
        private const val TAG = "StoryPostViewModel"
    }

}