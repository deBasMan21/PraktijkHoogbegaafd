package nl.avans.praktijkhoogbegaafd.domain

import nl.avans.praktijkhoogbegaafd.dal.FeelingEntity
import java.util.ArrayList

class DayFeeling {
    var feelingsForDay = ArrayList<FeelingEntity>()
    var date = ""

    constructor(date: String) {
        this.date = date
    }

    constructor(date: String?, feelings: ArrayList<FeelingEntity>) {
        feelingsForDay = feelings

    }

    fun addFeeling(fe: FeelingEntity) {
        feelingsForDay.add(fe)
    }
}