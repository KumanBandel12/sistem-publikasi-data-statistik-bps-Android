package com.bps.publikasistatistik.presentation.upload

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bps.publikasistatistik.data.remote.api.PublicationApi
import com.bps.publikasistatistik.data.remote.dto.request.PublicationRequestDto
import com.bps.publikasistatistik.domain.model.Category
import com.bps.publikasistatistik.domain.usecase.category.GetCategoriesUseCase
import com.bps.publikasistatistik.domain.usecase.category.GetSubCategoriesUseCase
import com.bps.publikasistatistik.util.Resource
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val api: PublicationApi,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getSubCategoriesUseCase: GetSubCategoriesUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(UploadPublicationUiState())
    val uiState = _uiState.asStateFlow()

    private val _uploadState = MutableStateFlow<Resource<String>?>(null)
    val uploadState = _uploadState.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _subCategories = MutableStateFlow<List<Category>>(emptyList())
    val subCategories = _subCategories.asStateFlow()

    init {
        // Set default publish date to today
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        _uiState.update { it.copy(publishDate = today) }
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _categories.value = resource.data ?: emptyList()
                    }
                    is Resource.Error -> {
                        // Handle error if needed
                    }
                    is Resource.Loading -> {
                        // Handle loading if needed
                    }
                }
            }
        }
    }

    fun loadSubCategories(categoryId: Long) {
        viewModelScope.launch {
            getSubCategoriesUseCase(categoryId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _subCategories.value = resource.data ?: emptyList()
                    }
                    is Resource.Error -> {
                        _subCategories.value = emptyList()
                    }
                    is Resource.Loading -> {
                        // Handle loading if needed
                    }
                }
            }
        }
    }

    fun updateTitle(value: String) {
        _uiState.update { it.copy(title = value) }
        clearError("title")
    }

    fun updateDescription(value: String) {
        _uiState.update { it.copy(description = value) }
        clearError("description")
    }

    fun updateAuthor(value: String) {
        _uiState.update { it.copy(author = value) }
        clearError("author")
    }

    fun updateCategory(categoryId: Long) {
        _uiState.update { it.copy(categoryId = categoryId, subCategoryId = null) }
        clearError("category")
        loadSubCategories(categoryId)
    }

    fun updateSubCategory(subCategoryId: Long) {
        _uiState.update { it.copy(subCategoryId = subCategoryId) }
        clearError("subCategory")
    }

    fun updateYear(year: Int) {
        _uiState.update { it.copy(year = year) }
        clearError("year")
    }

    fun updatePublishDate(date: String) {
        _uiState.update { it.copy(publishDate = date) }
        clearError("publishDate")
    }

    fun updateCatalogNumber(value: String) {
        _uiState.update { it.copy(catalogNumber = value) }
        clearError("catalogNumber")
    }

    fun updatePublicationNumber(value: String) {
        _uiState.update { it.copy(publicationNumber = value) }
        clearError("publicationNumber")
    }

    fun updateIssn(value: String) {
        _uiState.update { it.copy(issn = value) }
        clearError("issn")
    }

    fun updateFrequency(value: String) {
        _uiState.update { it.copy(frequency = value) }
        clearError("frequency")
    }

    fun updateLanguage(value: String) {
        _uiState.update { it.copy(language = value) }
        clearError("language")
    }

    fun updateFileUri(uri: Uri?) {
        if (uri != null) {
            val error = validateFile(uri)
            if (error != null) {
                _uiState.update { it.copy(errors = it.errors + ("file" to error)) }
            } else {
                _uiState.update { it.copy(fileUri = uri) }
                clearError("file")
            }
        } else {
            _uiState.update { it.copy(fileUri = null) }
        }
    }

    fun updateIsFeatured(value: Boolean) {
        _uiState.update { it.copy(isFeatured = value) }
    }

    private fun validateFile(uri: Uri): String? {
        try {
            val contentResolver = context.contentResolver
            val fileSize = contentResolver.openInputStream(uri)?.use { it.available() } ?: 0
            val fileName = getFileNameFromUri(uri)

            return when {
                fileSize > 25 * 1024 * 1024 -> "File terlalu besar. Maksimal 25MB"
                !fileName.endsWith(".pdf", ignoreCase = true) -> "Format file tidak didukung. Gunakan PDF"
                else -> null
            }
        } catch (e: Exception) {
            return "Gagal memvalidasi file"
        }
    }

    private fun getFileNameFromUri(uri: Uri): String {
        var result = ""
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (index != -1) result = it.getString(index)
                }
            }
        }
        if (result.isEmpty()) {
            result = uri.path ?: ""
            val cut = result.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    private fun clearError(field: String) {
        _uiState.update { 
            it.copy(errors = it.errors - field) 
        }
    }

    fun validateForm(): Boolean {
        val state = _uiState.value
        val errors = mutableMapOf<String, String>()

        if (state.title.isBlank()) errors["title"] = "Judul publikasi wajib diisi"
        if (state.description.isBlank()) errors["description"] = "Abstraksi wajib diisi"
        if (state.author.isBlank()) errors["author"] = "Penulis wajib diisi"
        if (state.categoryId == null) errors["category"] = "Kategori wajib dipilih"
        if (state.subCategoryId == null) errors["subCategory"] = "Sub Kategori wajib dipilih"
        if (state.year == null) errors["year"] = "Tahun Terbit wajib dipilih"
        if (state.publishDate.isBlank()) errors["publishDate"] = "Tanggal Terbit wajib diisi"
        if (state.catalogNumber.isBlank()) errors["catalogNumber"] = "Nomor Katalog wajib diisi"
        if (state.publicationNumber.isBlank()) errors["publicationNumber"] = "Nomor Publikasi wajib diisi"
        if (state.issn.isBlank()) errors["issn"] = "ISSN/ISBN wajib diisi"
        if (state.frequency.isBlank()) errors["frequency"] = "Frekuensi Rilis wajib diisi"
        if (state.language.isBlank()) errors["language"] = "Bahasa wajib diisi"
        if (state.fileUri == null) errors["file"] = "File PDF wajib diunggah"

        _uiState.update { it.copy(errors = errors) }
        return errors.isEmpty()
    }

    fun uploadPublication() {
        if (!validateForm()) {
            return
        }

        val state = _uiState.value
        val uri = state.fileUri ?: return

        viewModelScope.launch {
            _uploadState.value = Resource.Loading()
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Prepare file part
                val file = getFileFromUri(uri)
                val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

                // Prepare request part
                val requestDto = PublicationRequestDto(
                    title = state.title,
                    description = state.description,
                    author = state.author,
                    categoryId = state.categoryId!!,
                    subCategoryId = state.subCategoryId,
                    year = state.year!!,
                    releaseDate = state.publishDate,
                    catalogNumber = state.catalogNumber,
                    publicationNumber = state.publicationNumber,
                    issnIsbn = state.issn,
                    releaseFrequency = state.frequency,
                    language = state.language,
                    isFeatured = state.isFeatured
                )

                val jsonString = Gson().toJson(requestDto)
                val requestPart = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

                // Send to API
                val response = api.uploadPublication(filePart, requestPart)

                if (response.isSuccessful && response.body()?.success == true) {
                    _uploadState.value = Resource.Success("Upload Berhasil!")
                    clearForm()
                } else {
                    _uploadState.value = Resource.Error(response.body()?.message ?: "Upload Gagal")
                }
            } catch (e: Exception) {
                _uploadState.value = Resource.Error("Error: ${e.localizedMessage}")
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

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

    private fun clearForm() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        _uiState.value = UploadPublicationUiState(publishDate = today)
        _subCategories.value = emptyList()
    }

    fun resetUploadState() {
        _uploadState.value = null
    }
}