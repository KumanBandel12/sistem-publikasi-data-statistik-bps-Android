package com.bps.publikasistatistik.presentation.home.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bps.publikasistatistik.domain.model.Publication
import kotlinx.coroutines.delay

@Composable
fun FeaturedPublicationCarousel(
    publications: List<Publication>,
    pagerState: PagerState,
    onPublicationClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (publications.isEmpty()) {
        // Hide entire section - return null/empty
        return
    }
    
    // Auto-scroll logic
    LaunchedEffect(pagerState.currentPage) {
        delay(5000) // 5 seconds
        val nextPage = (pagerState.currentPage + 1) % publications.size
        pagerState.animateScrollToPage(nextPage)
    }
    
    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 40.dp),
        pageSpacing = 12.dp,
        modifier = modifier
    ) { page ->
        FeaturedPublicationCard(
            title = publications[page].title,
            coverUrl = publications[page].coverUrl,
            subCategoryName = publications[page].subCategoryName,
            publishDate = publications[page].publishDate,
            onClick = { onPublicationClick(publications[page].id) }
        )
    }
}
