package com.bps.publikasistatistik.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bps.publikasistatistik.presentation.theme.BackgroundCard
import com.bps.publikasistatistik.presentation.theme.FeaturedLabelEndColor
import com.bps.publikasistatistik.presentation.theme.FeaturedLabelStartColor
import com.bps.publikasistatistik.presentation.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeaturedPublicationCard(
    title: String,
    coverUrl: String?,
    subCategoryName: String,
    publishDate: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val featuredLabelGradient = Brush.horizontalGradient(
        colors = listOf(
            FeaturedLabelStartColor, // Light orange
            FeaturedLabelEndColor     // Dark orange
        )
    )

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Cover Image with Gradient Label
            Box {
                AsyncImage(
                    model = coverUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                
                // Orange Gradient Label (Top)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    GradientLabel(
                        text = subCategoryName,
                        gradient = featuredLabelGradient
                    )
                }
            }
            
            // Content
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Date
                Text(
                    text = formatPublishDate(publishDate),
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun formatPublishDate(publishDate: String?): String {
    if (publishDate.isNullOrEmpty()) return "Tanggal tidak tersedia"
    
    return try {
        // Assuming format from backend is "yyyy-MM-dd"
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val date = inputFormat.parse(publishDate)
        outputFormat.format(date ?: return publishDate)
    } catch (e: Exception) {
        publishDate
    }
}
