package net.schueller.peertube.feature_settings.settings.presentation

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.jamal.composeprefs.ui.PrefsScreen
import com.jamal.composeprefs.ui.prefs.*
import net.schueller.peertube.presentation.dataStore
import net.schueller.peertube.R
import net.schueller.peertube.common.Constants.COLOR_PREF_AMBER
import net.schueller.peertube.common.Constants.COLOR_PREF_BLUE
import net.schueller.peertube.common.Constants.COLOR_PREF_BLUEGRAY
import net.schueller.peertube.common.Constants.COLOR_PREF_BROWN
import net.schueller.peertube.common.Constants.COLOR_PREF_CYAN
import net.schueller.peertube.common.Constants.COLOR_PREF_DEEPORANGE
import net.schueller.peertube.common.Constants.COLOR_PREF_DEEPPURPLE
import net.schueller.peertube.common.Constants.COLOR_PREF_GRAY
import net.schueller.peertube.common.Constants.COLOR_PREF_GREEN
import net.schueller.peertube.common.Constants.COLOR_PREF_INDIGO
import net.schueller.peertube.common.Constants.COLOR_PREF_LIGHTBLUE
import net.schueller.peertube.common.Constants.COLOR_PREF_LIGHTGREEN
import net.schueller.peertube.common.Constants.COLOR_PREF_LIME
import net.schueller.peertube.common.Constants.COLOR_PREF_ORANGE
import net.schueller.peertube.common.Constants.COLOR_PREF_PINK
import net.schueller.peertube.common.Constants.COLOR_PREF_PURPLE
import net.schueller.peertube.common.Constants.COLOR_PREF_RED
import net.schueller.peertube.common.Constants.COLOR_PREF_TEAL
import net.schueller.peertube.common.Constants.COLOR_PREF_YELLOW
import net.schueller.peertube.common.Constants.PREF_ACCEPT_INSECURE_KEY
import net.schueller.peertube.common.Constants.PREF_BACKGROUND_AUDIO_KEY
import net.schueller.peertube.common.Constants.PREF_BACKGROUND_BEHAVIOR_KEY
import net.schueller.peertube.common.Constants.PREF_BACKGROUND_FLOAT_KEY
import net.schueller.peertube.common.Constants.PREF_BACKGROUND_STOP_KEY
import net.schueller.peertube.common.Constants.PREF_BACK_PAUSE_KEY
import net.schueller.peertube.common.Constants.PREF_DARK_MODE_KEY
import net.schueller.peertube.common.Constants.PREF_SHOW_NSFW_KEY
import net.schueller.peertube.common.Constants.PREF_THEME_KEY
import net.schueller.peertube.common.Constants.PREF_VIDEO_SPEED_KEY

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun SettingsScreen() {
    Scaffold(topBar = { SettingsTopBar() }) {

        PrefsScreen(dataStore = LocalContext.current.dataStore) {

            prefsGroup({
//                GroupHeader(
//                    title = stringResource(R.string.settings_activity_look_and_feel_category_title),
//                    color = MaterialTheme.colors.secondary
//                )
            }) {
                prefsItem {
                    ListPref(
                        key = "pref_language_app_key",
                        title = stringResource(R.string.pref_language_app),
                        useSelectedAsSummary = true,
                        entries = mapOf(
                            "ab" to stringResource(R.string.iso_lang_ab),
                            "aa" to stringResource(R.string.iso_lang_aa),
                            "af" to stringResource(R.string.iso_lang_af),
                            "ak" to stringResource(R.string.iso_lang_ak),
                            "sq" to stringResource(R.string.iso_lang_sq),
                            "ase" to stringResource(R.string.iso_lang_ase),
                            "am" to stringResource(R.string.iso_lang_am),
                            "ar" to stringResource(R.string.iso_lang_ar),
                            "an" to stringResource(R.string.iso_lang_an),
                            "hy" to stringResource(R.string.iso_lang_hy),
                            "as" to stringResource(R.string.iso_lang_as),
                            "av" to stringResource(R.string.iso_lang_av),
                            "ay" to stringResource(R.string.iso_lang_ay),
                            "az" to stringResource(R.string.iso_lang_az),
                            "bm" to stringResource(R.string.iso_lang_bm),
                            "ba" to stringResource(R.string.iso_lang_ba),
                            "eu" to stringResource(R.string.iso_lang_eu),
                            "be" to stringResource(R.string.iso_lang_be),
                            "bn" to stringResource(R.string.iso_lang_bn),
                            "bn_rBD" to stringResource(R.string.iso_lang_bn_rBD),
                            "bi" to stringResource(R.string.iso_lang_bi),
                            "bs" to stringResource(R.string.iso_lang_bs),
                            "bzs" to stringResource(R.string.iso_lang_bzs),
                            "br" to stringResource(R.string.iso_lang_br),
                            "bfi" to stringResource(R.string.iso_lang_bfi),
                            "bg" to stringResource(R.string.iso_lang_bg),
                            "my" to stringResource(R.string.iso_lang_my),
                            "ca" to stringResource(R.string.iso_lang_ca),
                            "ch" to stringResource(R.string.iso_lang_ch),
                            "ce" to stringResource(R.string.iso_lang_ce),
                            "zh" to stringResource(R.string.iso_lang_zh),
                            "csl" to stringResource(R.string.iso_lang_csl),
                            "cv" to stringResource(R.string.iso_lang_cv),
                            "kw" to stringResource(R.string.iso_lang_kw),
                            "co" to stringResource(R.string.iso_lang_co),
                            "cr" to stringResource(R.string.iso_lang_cr),
                            "hr" to stringResource(R.string.iso_lang_hr),
                            "cs" to stringResource(R.string.iso_lang_cs),
                            "cse" to stringResource(R.string.iso_lang_cse),
                            "da" to stringResource(R.string.iso_lang_da),
                            "dsl" to stringResource(R.string.iso_lang_dsl),
                            "dv" to stringResource(R.string.iso_lang_dv),
                            "nl" to stringResource(R.string.iso_lang_nl),
                            "dz" to stringResource(R.string.iso_lang_dz),
                            "en" to stringResource(R.string.iso_lang_en),
                            "eo" to stringResource(R.string.iso_lang_eo),
                            "et" to stringResource(R.string.iso_lang_et),
                            "ee" to stringResource(R.string.iso_lang_ee),
                            "fo" to stringResource(R.string.iso_lang_fo),
                            "fj" to stringResource(R.string.iso_lang_fj),
                            "fi" to stringResource(R.string.iso_lang_fi),
                            "fr" to stringResource(R.string.iso_lang_fr),
                            "fsl" to stringResource(R.string.iso_lang_fsl),
                            "ff" to stringResource(R.string.iso_lang_ff),
                            "gl" to stringResource(R.string.iso_lang_gl),
                            "lg" to stringResource(R.string.iso_lang_lg),
                            "ka" to stringResource(R.string.iso_lang_ka),
                            "de" to stringResource(R.string.iso_lang_de),
                            "gsg" to stringResource(R.string.iso_lang_gsg),
                            "gn" to stringResource(R.string.iso_lang_gn),
                            "gu" to stringResource(R.string.iso_lang_gu),
                            "ht" to stringResource(R.string.iso_lang_ht),
                            "ha" to stringResource(R.string.iso_lang_ha),
                            "he" to stringResource(R.string.iso_lang_he),
                            "hz" to stringResource(R.string.iso_lang_hz),
                            "hi" to stringResource(R.string.iso_lang_hi),
                            "ho" to stringResource(R.string.iso_lang_ho),
                            "hu" to stringResource(R.string.iso_lang_hu),
                            "is" to stringResource(R.string.iso_lang_is),
                            "ig" to stringResource(R.string.iso_lang_ig),
                            "id" to stringResource(R.string.iso_lang_id),
                            "iu" to stringResource(R.string.iso_lang_iu),
                            "ik" to stringResource(R.string.iso_lang_ik),
                            "ga" to stringResource(R.string.iso_lang_ga),
                            "it" to stringResource(R.string.iso_lang_it),
                            "ja" to stringResource(R.string.iso_lang_ja),
                            "jsl" to stringResource(R.string.iso_lang_jsl),
                            "jv" to stringResource(R.string.iso_lang_jv),
                            "kl" to stringResource(R.string.iso_lang_kl),
                            "kn" to stringResource(R.string.iso_lang_kn),
                            "kr" to stringResource(R.string.iso_lang_kr),
                            "ks" to stringResource(R.string.iso_lang_ks),
                            "kk" to stringResource(R.string.iso_lang_kk),
                            "km" to stringResource(R.string.iso_lang_km),
                            "ki" to stringResource(R.string.iso_lang_ki),
                            "rw" to stringResource(R.string.iso_lang_rw),
                            "ky" to stringResource(R.string.iso_lang_ky),
                            "tlh" to stringResource(R.string.iso_lang_tlh),
                            "kv" to stringResource(R.string.iso_lang_kv),
                            "kg" to stringResource(R.string.iso_lang_kg),
                            "ko" to stringResource(R.string.iso_lang_ko),
                            "avk" to stringResource(R.string.iso_lang_avk),
                            "kj" to stringResource(R.string.iso_lang_kj),
                            "ku" to stringResource(R.string.iso_lang_ku),
                            "lo" to stringResource(R.string.iso_lang_lo),
                            "lv" to stringResource(R.string.iso_lang_lv),
                            "li" to stringResource(R.string.iso_lang_li),
                            "ln" to stringResource(R.string.iso_lang_ln),
                            "lt" to stringResource(R.string.iso_lang_lt),
                            "jbo" to stringResource(R.string.iso_lang_jbo),
                            "lu" to stringResource(R.string.iso_lang_lu),
                            "lb" to stringResource(R.string.iso_lang_lb),
                            "mk" to stringResource(R.string.iso_lang_mk),
                            "mg" to stringResource(R.string.iso_lang_mg),
                            "ms" to stringResource(R.string.iso_lang_ms),
                            "ml" to stringResource(R.string.iso_lang_ml),
                            "mt" to stringResource(R.string.iso_lang_mt),
                            "gv" to stringResource(R.string.iso_lang_gv),
                            "mi" to stringResource(R.string.iso_lang_mi),
                            "mr" to stringResource(R.string.iso_lang_mr),
                            "mh" to stringResource(R.string.iso_lang_mh),
                            "el" to stringResource(R.string.iso_lang_el),
                            "mn" to stringResource(R.string.iso_lang_mn),
                            "na" to stringResource(R.string.iso_lang_na),
                            "nv" to stringResource(R.string.iso_lang_nv),
                            "ng" to stringResource(R.string.iso_lang_ng),
                            "ne" to stringResource(R.string.iso_lang_ne),
                            "nd" to stringResource(R.string.iso_lang_nd),
                            "se" to stringResource(R.string.iso_lang_se),
                            "no" to stringResource(R.string.iso_lang_no),
                            "nb" to stringResource(R.string.iso_lang_nb),
                            "nn" to stringResource(R.string.iso_lang_nn),
                            "ny" to stringResource(R.string.iso_lang_ny),
                            "oc" to stringResource(R.string.iso_lang_oc),
                            "oj" to stringResource(R.string.iso_lang_oj),
                            "or" to stringResource(R.string.iso_lang_or),
                            "om" to stringResource(R.string.iso_lang_om),
                            "os" to stringResource(R.string.iso_lang_os),
                            "pks" to stringResource(R.string.iso_lang_pks),
                            "pa" to stringResource(R.string.iso_lang_pa),
                            "fa" to stringResource(R.string.iso_lang_fa),
                            "pl" to stringResource(R.string.iso_lang_pl),
                            "pt" to stringResource(R.string.iso_lang_pt),
                            "ps" to stringResource(R.string.iso_lang_ps),
                            "qu" to stringResource(R.string.iso_lang_qu),
                            "ro" to stringResource(R.string.iso_lang_ro),
                            "rm" to stringResource(R.string.iso_lang_rm),
                            "rn" to stringResource(R.string.iso_lang_rn),
                            "ru" to stringResource(R.string.iso_lang_ru),
                            "rsl" to stringResource(R.string.iso_lang_rsl),
                            "sm" to stringResource(R.string.iso_lang_sm),
                            "sg" to stringResource(R.string.iso_lang_sg),
                            "sc" to stringResource(R.string.iso_lang_sc),
                            "sdl" to stringResource(R.string.iso_lang_sdl),
                            "gd" to stringResource(R.string.iso_lang_gd),
                            "sr" to stringResource(R.string.iso_lang_sr),
                            "sh" to stringResource(R.string.iso_lang_sh),
                            "sn" to stringResource(R.string.iso_lang_sn),
                            "ii" to stringResource(R.string.iso_lang_ii),
                            "sd" to stringResource(R.string.iso_lang_sd),
                            "si" to stringResource(R.string.iso_lang_si),
                            "sk" to stringResource(R.string.iso_lang_sk),
                            "sl" to stringResource(R.string.iso_lang_sl),
                            "so" to stringResource(R.string.iso_lang_so),
                            "sfs" to stringResource(R.string.iso_lang_sfs),
                            "nr" to stringResource(R.string.iso_lang_nr),
                            "st" to stringResource(R.string.iso_lang_st),
                            "es" to stringResource(R.string.iso_lang_es),
                            "su" to stringResource(R.string.iso_lang_su),
                            "sw" to stringResource(R.string.iso_lang_sw),
                            "ss" to stringResource(R.string.iso_lang_ss),
                            "sv" to stringResource(R.string.iso_lang_sv),
                            "swl" to stringResource(R.string.iso_lang_swl),
                            "tl" to stringResource(R.string.iso_lang_tl),
                            "ty" to stringResource(R.string.iso_lang_ty),
                            "tg" to stringResource(R.string.iso_lang_tg),
                            "ta" to stringResource(R.string.iso_lang_ta),
                            "tt" to stringResource(R.string.iso_lang_tt),
                            "te" to stringResource(R.string.iso_lang_te),
                            "th" to stringResource(R.string.iso_lang_th),
                            "bo" to stringResource(R.string.iso_lang_bo),
                            "ti" to stringResource(R.string.iso_lang_ti),
                            "to" to stringResource(R.string.iso_lang_to),
                            "ts" to stringResource(R.string.iso_lang_ts),
                            "tn" to stringResource(R.string.iso_lang_tn),
                            "tr" to stringResource(R.string.iso_lang_tr),
                            "tk" to stringResource(R.string.iso_lang_tk),
                            "tw" to stringResource(R.string.iso_lang_tw),
                            "ug" to stringResource(R.string.iso_lang_ug),
                            "uk" to stringResource(R.string.iso_lang_uk),
                            "ur" to stringResource(R.string.iso_lang_ur),
                            "uz" to stringResource(R.string.iso_lang_uz),
                            "ve" to stringResource(R.string.iso_lang_ve),
                            "vi" to stringResource(R.string.iso_lang_vi),
                            "wa" to stringResource(R.string.iso_lang_wa),
                            "cy" to stringResource(R.string.iso_lang_cy),
                            "fy" to stringResource(R.string.iso_lang_fy),
                            "wo" to stringResource(R.string.iso_lang_wo),
                            "xh" to stringResource(R.string.iso_lang_xh),
                            "yi" to stringResource(R.string.iso_lang_yi),
                            "yo" to stringResource(R.string.iso_lang_yo),
                            "za" to stringResource(R.string.iso_lang_za),
                            "zu" to stringResource(R.string.iso_lang_zu)
                        )
                    )
                }
                prefsItem {
                    ListPref(
                        key = PREF_THEME_KEY,
                        title = stringResource(R.string.pref_title_app_theme),
                        useSelectedAsSummary = true,
                        entries = mapOf(
                           COLOR_PREF_RED to stringResource(R.string.red),
                           COLOR_PREF_PINK to stringResource(R.string.pink),
                           COLOR_PREF_PURPLE to stringResource(R.string.purple),
                           COLOR_PREF_DEEPPURPLE to stringResource(R.string.deeppurple),
                           COLOR_PREF_INDIGO to stringResource(R.string.indigo),
                           COLOR_PREF_BLUE to stringResource(R.string.blue),
                           COLOR_PREF_LIGHTBLUE to stringResource(R.string.lightblue),
                           COLOR_PREF_CYAN to stringResource(R.string.cyan),
                           COLOR_PREF_TEAL to stringResource(R.string.teal),
                           COLOR_PREF_GREEN to stringResource(R.string.green),
                           COLOR_PREF_LIGHTGREEN to stringResource(R.string.lightgreen),
                           COLOR_PREF_LIME to stringResource(R.string.lime),
                           COLOR_PREF_YELLOW to stringResource(R.string.yellow),
                           COLOR_PREF_AMBER to stringResource(R.string.amber),
                           COLOR_PREF_ORANGE to stringResource(R.string.orange),
                           COLOR_PREF_DEEPORANGE to stringResource(R.string.deeporange),
                           COLOR_PREF_BROWN to stringResource(R.string.brown),
                           COLOR_PREF_GRAY to stringResource(R.string.gray),
                           COLOR_PREF_BLUEGRAY to stringResource(R.string.bluegray)
                        )
                    )
                }
                prefsItem {
                    SwitchPref(
                        key = PREF_DARK_MODE_KEY,
                        title = stringResource(R.string.pref_title_dark_mode),
                        summary = stringResource(R.string.pref_description_dark_mode)
                    )
                }
            }


            prefsGroup({
//                GroupHeader(
//                    title = stringResource(R.string.settings_activity_video_list_category_title),
//                    color = MaterialTheme.colors.secondary
//                )
            }) {
                prefsItem {
                    SwitchPref(
                        key = PREF_SHOW_NSFW_KEY,
                        title = stringResource(R.string.pref_title_show_nsfw),
                        summary = stringResource(R.string.pref_description_show_nsfw)
                    )
                }
                prefsItem {
                    MultiSelectListPref(
                        key = "pref_video_language_key",
                        title = stringResource(R.string.pref_language),
                        summary = stringResource(R.string.pref_description_language),
                        entries = mapOf(
                            "ar" to stringResource(R.string.iso_lang_ar),
                            "bn" to stringResource(R.string.iso_lang_bn),
                            "bn_rBD" to stringResource(R.string.iso_lang_bn_rBD),
                            "cs" to stringResource(R.string.iso_lang_cs),
                            "de" to stringResource(R.string.iso_lang_de),
                            "el" to stringResource(R.string.iso_lang_el),
                            "en" to stringResource(R.string.iso_lang_en),
                            "es" to stringResource(R.string.iso_lang_es),
                            "fa" to stringResource(R.string.iso_lang_fa),
                            "fi" to stringResource(R.string.iso_lang_fi),
                            "fr" to stringResource(R.string.iso_lang_fr),
                            "gd" to stringResource(R.string.iso_lang_gd),
                            "hi" to stringResource(R.string.iso_lang_hi),
                            "it" to stringResource(R.string.iso_lang_it),
                            "ja" to stringResource(R.string.iso_lang_ja),
                            "no" to stringResource(R.string.iso_lang_no),
                            "nl" to stringResource(R.string.iso_lang_nl),
                            "pl" to stringResource(R.string.iso_lang_pl),
                            "ru" to stringResource(R.string.iso_lang_ru),
                            "sv" to stringResource(R.string.iso_lang_sv),
                            "tr" to stringResource(R.string.iso_lang_tr),
                            "uk" to stringResource(R.string.iso_lang_uk),
//                            "zh-rCN" to stringResource(R.string.iso_lang_zh_rCN),
//                            "zh-rTW" to stringResource(R.string.iso_lang_zh_rTW)
                        )
                    )
                }
            }

            prefsGroup({
//                GroupHeader(
//                    title = stringResource(R.string.settings_activity_video_playback_category_title),
//                    color = MaterialTheme.colors.secondary
//                )
            }) {
                prefsItem {
                    ListPref(
                        key = PREF_VIDEO_SPEED_KEY,
                        title = stringResource(R.string.pref_title_video_speed),
                        useSelectedAsSummary = true,
                        entries = mapOf(
                            "0.5" to "0.5x",
                            "0.75" to "0.75x",
                            "1.0" to "Normal",
                            "1.25" to "1.25x",
                            "1.5" to "1.5x",
                            "2" to "2x",
                        )
                    )
                }
                prefsItem {
                    SwitchPref(
                        key = PREF_BACK_PAUSE_KEY,
                        title = stringResource(R.string.pref_title_back_pause),
                        summary = stringResource(R.string.pref_description_back_pause)
                    )
                }
                prefsItem {
                    ListPref(
                        key = PREF_BACKGROUND_BEHAVIOR_KEY,
                        title = stringResource(R.string.pref_background_behavior),
                        useSelectedAsSummary = true,
                        entries = mapOf(
                            PREF_BACKGROUND_AUDIO_KEY to stringResource(R.string.pref_background_audio),
                            PREF_BACKGROUND_STOP_KEY to stringResource(R.string.pref_background_stop),
                            PREF_BACKGROUND_FLOAT_KEY to stringResource(R.string.pref_background_float),
                        )
                    )
                }
            }

            prefsGroup({
//                GroupHeader(
//                    title = stringResource(R.string.settings_activity_advanced_category_title),
//                    color = MaterialTheme.colors.secondary
//                )
            }) {
                prefsItem {
                    SwitchPref(
                        key = PREF_ACCEPT_INSECURE_KEY,
                        title = stringResource(R.string.pref_title_accept_insecure),
                        summary = stringResource(R.string.pref_description_accept_insecure)
                    )
                }
                prefsItem {
                    prefsItem {
                        TextPref(
                            title = stringResource(R.string.clear_search_history),
                            summary = stringResource(R.string.clear_search_history_prompt),
                            enabled = true
                        )
                    }
                }
            }

            prefsGroup({
//                GroupHeader(
//                    title = stringResource(R.string.settings_activity_about_category_title),
//                    color = MaterialTheme.colors.secondary
//                )
            }) {
                prefsItem {
                    TextPref(
                        title = stringResource(R.string.pref_title_version),
                        summary = stringResource(R.string.versionName),
                        enabled = false
                    )
                }
                prefsItem {
                    TextPref(
                        title = stringResource(R.string.pref_title_buildtime),
                        summary = "pref_buildtime",
                        enabled = false
                    )
                }
                prefsItem {
                    TextPref(
                        title = stringResource(R.string.pref_title_license),
                        summary = stringResource(R.string.pref_description_license),
                        enabled = false
                    )
                }

            }
        }
    }
}

@Composable
fun SettingsTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Settings",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    )
}