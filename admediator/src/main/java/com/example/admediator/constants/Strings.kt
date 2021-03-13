package com.example.admediator.constants


internal object Strings {
    // We use a strings object instead of string resources, because we need
    // context to access string resources, but we don't have access to context everywhere

    const val sharepreferences_name = "ad_mediator_shared_pref"

    // TAPSELL

    const val tapsell_ad_not_available = "Tapsell ad not available."

    // CHARTBOOST

    const val chartboost_ad_not_available = "Chartboost ad not available."

    // UNITY

    const val unity_ad_not_available = "Unity ad not available."

    const val unity_ad_show_error = "Unity faced an error while showing the ad."

    const val unity_ads_service_error = "Unity-ads service error."
}