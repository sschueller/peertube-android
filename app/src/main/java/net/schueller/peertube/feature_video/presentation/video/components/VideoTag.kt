package net.schueller.peertube.feature_video.presentation.video.components;

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun VideoTag(
        tag: String
) {
        Text(text = tag)
}