package net.schueller.peertube.feature_server_address.data.data_source.remote.dto

import net.schueller.peertube.feature_server_address.domain.model.Category

data class CategoryDto(
    var id: Int,
    var label: String
)

fun CategoryDto.toCategory(): Category {
    return Category(
        id = id,
        label = label
    )
}