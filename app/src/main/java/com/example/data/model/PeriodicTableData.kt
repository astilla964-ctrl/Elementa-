package com.example.data.model

object PeriodicTableData {
    val ALL_118_ELEMENTS: List<Chemical> = PeriodicTableData1.ELEMENTS_1_TO_56 + PeriodicTableData2.ELEMENTS_57_TO_118

    fun getElementByZ(z: Int): Chemical? = ALL_118_ELEMENTS.find { it.atomicNumber == z }

    fun getElementBySymbol(sym: String): Chemical? = ALL_118_ELEMENTS.find { it.symbol.equals(sym, ignoreCase = true) }
}
