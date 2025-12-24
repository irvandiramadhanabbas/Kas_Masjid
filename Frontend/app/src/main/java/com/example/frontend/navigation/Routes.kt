package com.example.frontend.navigation

object Routes {

    const val SPLASH = "splash"
    const val LOGIN = "login"

    const val HOME_KETUA = "home_ketua"
    const val HOME_BENDAHARA = "home_bendahara"
    const val HOME_JAMAAH = "home_jamaah"

    const val TAB_DASHBOARD = "tab_dashboard"
    const val TAB_TRANSAKSI = "tab_transaksi"
    const val TAB_PROFIL = "tab_profil"
    const val TAB_LAPORAN = "tab_laporan"

    const val KATEGORI_LIST = "kategori_list"
    const val TRANSAKSI_FORM = "transaksi_form"
}

fun roleToHomeRoute(role: String): String {
    return when (role.uppercase()) {
        "KETUA" -> Routes.HOME_KETUA
        "BENDAHARA" -> Routes.HOME_BENDAHARA
        "JAMAAH" -> Routes.HOME_JAMAAH
        else -> Routes.LOGIN
    }
}

