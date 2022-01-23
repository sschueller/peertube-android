package net.schueller.peertube.feature_video.data.remote.dto

import net.schueller.peertube.feature_video.domain.model.Language


data class LanguageDto(
    var id: String?,
    var label: String
)

fun LanguageDto.toLanguage(): Language {
    return Language(
        id = id,
        label = label
    )
}