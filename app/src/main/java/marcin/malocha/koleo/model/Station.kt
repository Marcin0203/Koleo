package marcin.malocha.koleo.model

data class Station (
    val id: Int,
    val name: String,
    val nameSlug: String,
    val latitude: String,
    val longitude: String,
    val hits: Int,
    val ibnr: Int? = null
)