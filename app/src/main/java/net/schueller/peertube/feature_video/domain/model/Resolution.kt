package net.schueller.peertube.feature_video.domain.model

// video resolutions
const val CLASS_96_PIXEL_WIDTH = 128
const val CLASS_96_PIXEL_HEIGHT = 96

const val CLASS_120_PIXEL_WIDTH = 160
const val CLASS_120_PIXEL_HEIGHT = 120

const val CLASS_144_PIXEL_WIDTH = 256
const val CLASS_144_PIXEL_HEIGHT = 144

const val CLASS_180_PIXEL_WIDTH = 320
const val CLASS_180_PIXEL_HEIGHT = 180

const val CLASS_240_PIXEL_WIDTH = 320
const val CLASS_240_PIXEL_HEIGHT = 240

const val CLASS_288_PIXEL_WIDTH = 384
const val CLASS_288_PIXEL_HEIGHT = 288

const val CLASS_360_PIXEL_WIDTH = 640
const val CLASS_360_PIXEL_HEIGHT = 360

data class Resolution(
    val id: Int,
    val label: String
)