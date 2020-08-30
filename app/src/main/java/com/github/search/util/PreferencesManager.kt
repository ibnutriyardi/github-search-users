package com.github.search.util

import com.orhanobut.hawk.Hawk

object PreferencesManager {

    private const val KEY_NEXT_HREF = "NEXT_HREF"
    private const val KEY_LAST_HREF = "LAST_HREF"

    var nextHref: String
        set(value) {
            Hawk.put(KEY_NEXT_HREF, value)
        }
        get() = Hawk.get(KEY_NEXT_HREF, "")

    var lastHref: String
        set(value) {
            Hawk.put(KEY_LAST_HREF, value)
        }
        get() = Hawk.get(KEY_LAST_HREF, "")
}