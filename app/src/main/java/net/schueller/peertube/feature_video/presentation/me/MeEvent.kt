package net.schueller.peertube.feature_video.presentation.me

import net.schueller.peertube.feature_video.domain.model.Video

sealed class MeEvent {
//    data class Logout(val video: Video): MeEvent()
    object Logout: MeEvent()

}