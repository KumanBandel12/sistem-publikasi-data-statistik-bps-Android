package com.bps.publikasistatistik.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bps.publikasistatistik.domain.model.Publication

@Composable
fun FeaturedPublicationsSection(
    publications: List<Publication>,
    onPublicationClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (publications.isEmpty()) {
        return
    }
    
    val pagerState = rememberPagerState(pageCount = { publications.size })
    
    Column(modifier = modifier.padding(top = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Publikasi Unggulan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            DotsIndicator(
                totalDots = publications.size,
                currentIndex = pagerState.currentPage
            )
        }
        FeaturedPublicationCarousel(
            publications = publications,
            pagerState = pagerState,
            onPublicationClick = onPublicationClick
        )
    }
}
