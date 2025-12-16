package com.bps.publikasistatistik.presentation.upload

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bps.publikasistatistik.data.remote.api.PublicationApi
import com.bps.publikasistatistik.data.remote.dto.request.PublicationRequestDto
import com.bps.publikasistatistik.util.Resource
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val api: PublicationApi,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // State Form
    var title = mutableStateOf("")
    var description = mutableStateOf("")
    var year = mutableStateOf("")
    var categoryId = mutableStateOf("")

    // State File
    var selectedFileUri = mutableStateOf<Uri?>(null)

    // State Upload
    private val _uploadState = MutableStateFlow<Resource<String>?>(null)
    val uploadState = _uploadState.asStateFlow()

    fun uploadPublication() {
        val uri = selectedFileUri.value
        if (uri == null) {
            _uploadState.value = Resource.Error("Pilih file PDF terlebih dahulu")
            return
        }

        viewModelScope.launch {
            _uploadState.value = Resource.Loading()
            try {
                // 1. Siapkan File Part (Binary)
                val file = getFileFromUri(uri)
                val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

                // 2. Siapkan Request Part (JSON Object)
                val requestDto = PublicationRequestDto(
                    title = title.value,
                    description = description.value,
                    year = year.value.toIntOrNull() ?: 2024,
                    categoryId = categoryId.value.toLongOrNull() ?: 1,
                    // Field wajib lain sesuai openapi.yaml
                    author = "Admin",
                    catalogNumber = "",
                    publicationNumber = "",
                    issnIsbn = "",
                    releaseFrequency = "",
                    releaseDate = "2024-01-01",
                    language = "Indonesia"
                )

                // Convert Object -> JSON String -> RequestBody
                val jsonString = Gson().toJson(requestDto)
                val requestPart = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

                // 3. Kirim ke API
                val response = api.uploadPublication(filePart, requestPart)

                if (response.isSuccessful && response.body()?.success == true) {
                    _uploadState.value = Resource.Success("Upload Berhasil!")
                } else {
                    _uploadState.value = Resource.Error(response.body()?.message ?: "Upload Gagal")
                }
            } catch (e: Exception) {
                _uploadState.value = Resource.Error("Error: ${e.localizedMessage}")
            }
        }
    }

    // Helper: Buat File Temporary dari URI
    private fun getFileFromUri(uri: Uri): File {
        val contentResolver = context.contentResolver
        val tempFile = File(context.cacheDir, "temp_upload.pdf")

        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return tempFile
    }
}