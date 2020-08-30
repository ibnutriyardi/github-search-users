package com.github.search.util

class LinkHeader(rawLinkHeader: String? = "") {

    companion object {
        private const val LINK_DELIMITER = ";"
        private const val LINK_SEPARATOR = ","
        private const val REL_NEXT = "next"
        private const val REL_LAST = "last"
    }

    private val regexUrlLink = "[<](https?://.*)[>]".toRegex()

    val nextUrl
        get() = getUrl(REL_NEXT)

    val lastUrl
        get() = getUrl(REL_LAST)

    private val listRawLink = rawLinkHeader?.split(LINK_SEPARATOR) ?: emptyList()
    var listLinkModel: List<LinkModel> = emptyList()

    init {
        listLinkModel = mapRawToLinkModel(listRawLink)
    }

    private fun mapRawToLinkModel(listRawLink: List<String>): List<LinkModel> {
        if (listRawLink.isEmpty()) return emptyList()
        return listRawLink.map {
            LinkModel(
                url = urlLink(it),
                rel = valueParamType(it)
            )
        }
    }

    private fun valueParamType(rawLink: String): String {
        val value = removeUrlLink(rawLink).split(LINK_DELIMITER).find {
            it.trim().substringBefore("=").equals(Params.REL.name, true)
        }?.substringAfter("=") ?: ""

        return value.replace("\"", "").trim()
    }

    private fun removeUrlLink(raw: String) = raw.replace("<${urlLink(raw)}>", "")

    private fun urlLink(rawLink: String): String {
        val value = rawLink.split(LINK_DELIMITER).firstOrNull {
            it.trim().matches(regexUrlLink)
        } ?: ""

        return regexUrlLink.find(value)?.groups?.get(1)?.value?.trim() ?: ""
    }

    private fun getUrl(relValue: String) = listLinkModel.firstOrNull {
        it.rel.equals(relValue, true)
    }?.url ?: ""

    data class LinkModel(
        val url: String = "",
        val rel: String = ""
    )

    enum class Params {
        REL
    }
}