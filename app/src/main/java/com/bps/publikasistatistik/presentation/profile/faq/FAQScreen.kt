package com.bps.publikasistatistik.presentation.profile.faq

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Model Data Sederhana
data class FaqItem(
    val question: String,
    val answer: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAQScreen(
    navController: NavController
) {
    // Data Dummy FAQ (Sesuaikan dengan kebutuhan BPS)
    val faqList = listOf(
        FaqItem(
            "Bagaimana cara mendownload publikasi?",
            "Pilih publikasi yang Anda inginkan dari Halaman Beranda atau Pencarian, masuk ke halaman detail, lalu klik tombol 'Download PDF' di bagian bawah."
        ),
        FaqItem(
            "Apakah layanan ini berbayar?",
            "Tidak. Seluruh data publikasi statistik yang disediakan oleh BPS dalam aplikasi ini dapat diakses dan diunduh secara GRATIS."
        ),
        FaqItem(
            "Saya lupa password akun saya, apa yang harus dilakukan?",
            "Anda bisa menghubungi administrator BPS terkait atau menggunakan fitur 'Lupa Password' di halaman Login (jika tersedia)."
        ),
        FaqItem(
            "Apakah saya bisa mengubah data profil?",
            "Ya. Masuk ke menu Profil, lalu pilih 'Edit Profil' untuk mengubah Nama, No HP, atau Alamat Anda."
        ),
        FaqItem(
            "Format file apa yang disediakan?",
            "Saat ini sebagian besar publikasi tersedia dalam format PDF (.pdf) yang bisa dibuka langsung di aplikasi atau PDF Reader eksternal."
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bantuan & FAQ") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(faqList) { faq ->
                FaqCard(faq)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun FaqCard(faq: FaqItem) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = faq.question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = faq.answer,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}