package net.schueller.peertube.feature_video.presentation.video_list.components;


import androidx.compose.material.Text
import androidx.compose.runtime.Composable;
import net.schueller.peertube.feature_video.domain.model.Category

@Composable
fun VideoCategory(
        category: Category
) {
        Text(text = category.label)

}