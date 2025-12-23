package com.bps.publikasistatistik.presentation.upload

import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bps.publikasistatistik.presentation.theme.BpsPrimary
import com.bps.publikasistatistik.presentation.upload.category.UpdateCategoryScreen
import com.bps.publikasistatistik.presentation.upload.components.TabSwitcher
import com.bps.publikasistatistik.util.Resource
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadPublicationScreen(
    navController: NavController,
    viewModel: UploadViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    
    val uploadState by viewModel.uploadState.collectAsState()

    // Handle upload result
    LaunchedEffect(uploadState) {
        when (val result = uploadState) {
            is Resource.Success -> {
                Toast.makeText(context, result.data, Toast.LENGTH_SHORT).show()
                viewModel.resetUploadState()
                // Stay on the screen after success, form is already cleared
            }
            is Resource.Error -> {
                Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                viewModel.resetUploadState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (selectedTab == 0) "Upload Publikasi" else "Update Kategori",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BpsPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Tab Switcher
            TabSwitcher(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            // Content based on selected tab
            when (selectedTab) {
                0 -> UploadPublicationContent(viewModel = viewModel)
                1 -> UpdateCategoryScreen(navController = navController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UploadPublicationContent(viewModel: UploadViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val subCategories by viewModel.subCategories.collectAsState()

    // File Picker Launcher
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.updateFileUri(uri)
    }

    // Date Picker
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val calendar = Calendar.getInstance().apply { timeInMillis = millis }
                        val date = String.format(
                            "%04d-%02d-%02d",
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH) + 1,
                            calendar.get(Calendar.DAY_OF_MONTH)
                        )
                        viewModel.updatePublishDate(date)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Judul Publikasi
        OutlinedTextField(
            value = uiState.title,
            onValueChange = { viewModel.updateTitle(it) },
            label = { Text("Judul publikasi") },
            placeholder = { Text("Contoh: Statistik Daerah 2024") },
            leadingIcon = { Icon(Icons.Default.Title, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            isError = uiState.errors.containsKey("title"),
            supportingText = {
                if (uiState.errors.containsKey("title")) {
                    Text(uiState.errors["title"]!!, color = MaterialTheme.colorScheme.error)
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        // 2. Abstraksi
        OutlinedTextField(
            value = uiState.description,
            onValueChange = { viewModel.updateDescription(it) },
            label = { Text("Abstraksi") },
            placeholder = { Text("Deskripsi singkat publikasi") },
            leadingIcon = { Icon(Icons.Outlined.Description, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            minLines = 4,
            maxLines = 5,
            isError = uiState.errors.containsKey("description"),
            supportingText = {
                if (uiState.errors.containsKey("description")) {
                    Text(uiState.errors["description"]!!, color = MaterialTheme.colorScheme.error)
                }
            }
        )

        // 3. Penulis
        OutlinedTextField(
            value = uiState.author,
            onValueChange = { viewModel.updateAuthor(it) },
            label = { Text("Penulis") },
            placeholder = { Text("Nama penulis") },
            leadingIcon = { Icon(Icons.Default.Person, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            isError = uiState.errors.containsKey("author"),
            supportingText = {
                if (uiState.errors.containsKey("author")) {
                    Text(uiState.errors["author"]!!, color = MaterialTheme.colorScheme.error)
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        // 4. Kategori Dropdown
        var expandedCategory by remember { mutableStateOf(false) }
        val selectedCategory = categories.find { it.id == uiState.categoryId }
        
        ExposedDropdownMenuBox(
            expanded = expandedCategory,
            onExpandedChange = { expandedCategory = it }
        ) {
            OutlinedTextField(
                value = selectedCategory?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Kategori") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                leadingIcon = { Icon(Icons.Default.Category, null) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                isError = uiState.errors.containsKey("category"),
                supportingText = {
                    if (uiState.errors.containsKey("category")) {
                        Text(uiState.errors["category"]!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )
            ExposedDropdownMenu(
                expanded = expandedCategory,
                onDismissRequest = { expandedCategory = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            viewModel.updateCategory(category.id)
                            expandedCategory = false
                        }
                    )
                }
            }
        }

        // 5. Sub Kategori Dropdown
        var expandedSubCategory by remember { mutableStateOf(false) }
        val selectedSubCategory = subCategories.find { it.id == uiState.subCategoryId }
        
        ExposedDropdownMenuBox(
            expanded = expandedSubCategory,
            onExpandedChange = { expandedSubCategory = it && uiState.categoryId != null }
        ) {
            OutlinedTextField(
                value = selectedSubCategory?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Sub Kategori") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubCategory) },
                leadingIcon = { Icon(Icons.Default.Category, null) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                enabled = uiState.categoryId != null,
                isError = uiState.errors.containsKey("subCategory"),
                supportingText = {
                    if (uiState.errors.containsKey("subCategory")) {
                        Text(uiState.errors["subCategory"]!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )
            ExposedDropdownMenu(
                expanded = expandedSubCategory,
                onDismissRequest = { expandedSubCategory = false }
            ) {
                subCategories.forEach { subCategory ->
                    DropdownMenuItem(
                        text = { Text(subCategory.name) },
                        onClick = {
                            viewModel.updateSubCategory(subCategory.id)
                            expandedSubCategory = false
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 6. Tahun Terbit Dropdown
            var expandedYear by remember { mutableStateOf(false) }
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val years = (currentYear downTo 1945).toList()
            
            ExposedDropdownMenuBox(
                expanded = expandedYear,
                onExpandedChange = { expandedYear = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = uiState.year?.toString() ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tahun Terbit") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedYear) },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, null) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    isError = uiState.errors.containsKey("year"),
                    supportingText = {
                        if (uiState.errors.containsKey("year")) {
                            Text(uiState.errors["year"]!!, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )
                ExposedDropdownMenu(
                    expanded = expandedYear,
                    onDismissRequest = { expandedYear = false },
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    years.forEach { year ->
                        DropdownMenuItem(
                            text = { Text(year.toString()) },
                            onClick = {
                                viewModel.updateYear(year)
                                expandedYear = false
                            }
                        )
                    }
                }
            }

            // 7. Tanggal Terbit
            OutlinedTextField(
                value = uiState.publishDate,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tanggal Terbit") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, null)
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                isError = uiState.errors.containsKey("publishDate"),
                supportingText = {
                    if (uiState.errors.containsKey("publishDate")) {
                        Text(uiState.errors["publishDate"]!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }

        // 8. Nomor Katalog
        OutlinedTextField(
            value = uiState.catalogNumber,
            onValueChange = { viewModel.updateCatalogNumber(it) },
            label = { Text("Nomor Katalog") },
            placeholder = { Text("Contoh: 1102001.5201") },
            leadingIcon = { Icon(Icons.Default.Numbers, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            isError = uiState.errors.containsKey("catalogNumber"),
            supportingText = {
                if (uiState.errors.containsKey("catalogNumber")) {
                    Text(uiState.errors["catalogNumber"]!!, color = MaterialTheme.colorScheme.error)
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        // 9. Nomor Publikasi
        OutlinedTextField(
            value = uiState.publicationNumber,
            onValueChange = { viewModel.updatePublicationNumber(it) },
            label = { Text("Nomor Publikasi") },
            placeholder = { Text("Contoh: 52010.2301") },
            leadingIcon = { Icon(Icons.Default.Numbers, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            isError = uiState.errors.containsKey("publicationNumber"),
            supportingText = {
                if (uiState.errors.containsKey("publicationNumber")) {
                    Text(uiState.errors["publicationNumber"]!!, color = MaterialTheme.colorScheme.error)
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        // 10. ISSN/ISBN
        OutlinedTextField(
            value = uiState.issn,
            onValueChange = { viewModel.updateIssn(it) },
            label = { Text("ISSN/ISBN") },
            placeholder = { Text("Contoh: 978-602-438-301-0") },
            leadingIcon = { Icon(Icons.Default.BookmarkBorder, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            isError = uiState.errors.containsKey("issn"),
            supportingText = {
                if (uiState.errors.containsKey("issn")) {
                    Text(uiState.errors["issn"]!!, color = MaterialTheme.colorScheme.error)
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        // 11. Frekuensi Rilis
        OutlinedTextField(
            value = uiState.frequency,
            onValueChange = { viewModel.updateFrequency(it) },
            label = { Text("Frekuensi Rilis") },
            placeholder = { Text("Contoh: Tahunan, Bulanan, Triwulan") },
            leadingIcon = { Icon(Icons.Default.Schedule, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            isError = uiState.errors.containsKey("frequency"),
            supportingText = {
                if (uiState.errors.containsKey("frequency")) {
                    Text(uiState.errors["frequency"]!!, color = MaterialTheme.colorScheme.error)
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        // 12. Bahasa
        OutlinedTextField(
            value = uiState.language,
            onValueChange = { viewModel.updateLanguage(it) },
            label = { Text("Bahasa") },
            placeholder = { Text("Contoh: Indonesia, English, Bilingual") },
            leadingIcon = { Icon(Icons.Default.Language, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            isError = uiState.errors.containsKey("language"),
            supportingText = {
                if (uiState.errors.containsKey("language")) {
                    Text(uiState.errors["language"]!!, color = MaterialTheme.colorScheme.error)
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        // 13. Upload File Area
        Text(
            text = "Upload File",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = BpsPrimary
        )

        val isFileSelected = uiState.fileUri != null
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (isFileSelected) Color(0xFFE8F5E9) else Color(0xFFF5F5F5))
                .border(
                    width = 2.dp,
                    color = if (isFileSelected) Color(0xFF4CAF50) else if (uiState.errors.containsKey("file")) MaterialTheme.colorScheme.error else Color.LightGray,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { pdfPickerLauncher.launch("application/pdf") },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                if (isFileSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = getFileNameFromUri(context, uiState.fileUri!!),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ketuk untuk ganti file",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.CloudUpload,
                        contentDescription = null,
                        tint = BpsPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pilih file atau tarik dan taruh disini.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = BpsPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "pdf - up to 25MB",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { pdfPickerLauncher.launch("application/pdf") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE8F5E9),
                            contentColor = Color(0xFF2E7D32)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Telusuri file")
                    }
                }
            }
        }

        if (uiState.errors.containsKey("file")) {
            Text(
                text = uiState.errors["file"]!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 14. Publikasi Unggulan Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Publikasi Unggulan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Publikasi akan ditampilkan di carousel beranda",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Switch(
                checked = uiState.isFeatured,
                onCheckedChange = { viewModel.updateIsFeatured(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = BpsPrimary
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 15. Simpan Button
        Button(
            onClick = { viewModel.uploadPublication() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E3A5F),
                disabledContainerColor = Color.Gray
            ),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Mengupload...", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            } else {
                Text(
                    text = "Simpan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

private fun getFileNameFromUri(context: android.content.Context, uri: Uri): String {
    var result = ""
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
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