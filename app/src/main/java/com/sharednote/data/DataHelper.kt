package com.sharednote.data

import android.annotation.SuppressLint
import android.util.Log
import java.time.LocalDate
import kotlin.Int
import kotlin.collections.iterator

class DataHelper {
//    fun generateMedicinesByCategory(
//        medicines: List<MedicineWithCategory>
//    ): List<GroupedMedicineInfo> {
//        val result = mutableListOf<GroupedMedicineInfo>()
//
//        val grouped = medicines.groupBy { it.categoryName }
//
//        for ((category, meds) in grouped) {
//            result.add(GroupedMedicineInfo.Category(category))
//            meds.forEach {
//                result.add(
//                    GroupedMedicineInfo.Medicine(
//                        it.name,
//                        it.photoInt,
//                        it.photoUri,
//                        it.description
//                    )
//                )
//            }
//        }
//
//        return result
//    }
//
//    @SuppressLint("DefaultLocale")
//    fun generateChildInfo(
//        childList: List<ChildEntity>,
//        sicknessesMap: Map<Int, SicknessEntity>,
//        factList: List<FactEntity>,
//        currentDate: LocalDate
//    ): List<ChildInfo> {
//        val result = mutableListOf<ChildInfo>()
//        childList.forEach {
//            val childId = it.id
//            val currentSicknesses = sicknessesMap.get(childId)
//            var healthy = true
//            var sickDate = ""
//            var state = ""
//            var sicknessId: Int? = null
//            if (currentSicknesses != null) {
//                healthy = false
//                sicknessId = currentSicknesses.id
//                sickDate = StringUtil.convertDateToShortNameString(currentSicknesses.dateStart)
//                state =
//                    "${StringUtil.calculateDays(currentSicknesses.dateStart, currentDate) + 1} days"
//            }
//            val birthDay = StringUtil.convertDateToFullString(it.birthDate)
//            //
//            val sortedFactList =
//                factList.filter { it1 -> it1.temperatureMode && it1.childId == childId }
//                    .sortedByDescending { it.date }
//                    .sortedByDescending { it.time }
//            factList.forEach { Log.d("MyTag", "generateChildInfo, factList: $it") }
//            sortedFactList.forEach { Log.d("MyTag", "generateChildInfo, sortedFactList: $it") }
//            var temperature: Double =
//                if (sortedFactList.isEmpty()) 0.0 else sortedFactList[0].temperature
//            //val temp = String.format("%.1f", temperature)
//            result.add(
//                ChildInfo(
//                    it.id, it.name, it.age, it.weight, birthDay, it.photoInt, it.photoUri,
//                    healthy, sickDate, sicknessId, temperature,
//                    state
//                )
//            )
//        }
//        return ArrayList<ChildInfo>(result)
//    }
//
//    /*
//    FactEntity(
//    @PrimaryKey(autoGenerate = true) val id: Int = 0,
//    val temperature: Double,
//    val moreThanUsual: Boolean,
//    val medicineId: Int?,
//    val sicknessId: Int?,
//    val date: LocalDate,
//    val time: LocalTime
//)
//    * */
//    @SuppressLint("DefaultLocale")
//    fun generateFactByTemperature(
//        activeFactList: List<FactEntity>,
//        childMap: Map<Int, ChildEntity>,
//        medicineMap: Map<Int, MedicineEntity>
//    ): List<DailyTemperatureListInfo> {//0
//        val facts = ArrayList(activeFactList)
//
//        return facts.filter { it.temperatureMode }.groupBy { it.childId }
//            .mapNotNull { (childId, factsByChild) ->
//                val child: ChildEntity = childMap[childId] ?: return@mapNotNull null
//                val daysList: List<DailyTemperatureByDayInfo> = factsByChild.groupBy { it.date }
//                    .map {/*3*/ (date, factsByDate) ->
//                        val countDays = 0
//                        val oneDaysList: List<DailyTemperatureByOneInfo> =
//                            factsByDate.map {/*5*/ fact ->
//                                val temp = StringUtil.convertTemperatureToString(fact.temperature)
//                                DailyTemperatureByOneInfo(
//                                    time = fact.time,
//                                    temperature = temp,
//                                    moreThanUsual = fact.moreThanUsual,
//                                    medicine = medicineMap.get(fact.medicineId)?.name ?: ""
//                                )
//                            }/*5*/.sortedBy { it.time }
//
//                        DailyTemperatureByDayInfo(
//                            date = date,
//                            countDays = countDays,
//                            list = oneDaysList
//                        )
//                    }.sortedBy { it.date }
//                var countDays = 0
//                daysList.forEach { it.countDays = ++countDays }
//                DailyTemperatureListInfo(
//                    child = child,
//                    list = daysList
//                )
//            }//1
//    }//0
//
//    fun generateStatisticInfo(
//        childMap: Map<Int, ChildEntity>,
//        medicineMap: Map<Int, MedicineEntity>,
//        facts: List<FactEntity>,
//        sicknesses: List<SicknessEntity>
//    ): List<StatisticListInfo> {//0
//        // ?
//        val sicknessList = ArrayList(sicknesses)
//        Log.d("MyTag", "generateStatistic()_1, childMap ${childMap.size}, medicineMap ${medicineMap.size}")
//        Log.d("MyTag", "generateStatistic()_1, facts ${facts.size}, sicknesses ${sicknesses.size}")
//        facts.forEach {  Log.d("MyTag", "generateStatistic()_1, factId: ${it.id}, medicineId ${it.medicineId}")}
//
//        val result = sicknessList.groupBy { it.childId }
//            //return sicknessList.groupBy { it.childId }
//            .mapNotNull { (childId, sicknessesByChild) ->
//                val child: ChildEntity = childMap.get(childId) ?: return@mapNotNull null
//
//                Log.d("MyTag", "generateStatistic(),groupBy_2.1, childId=${childId} child=${child} ")
//                Log.d("MyTag", "generateStatistic(),groupBy_2.2, sicknessesByChild ${sicknessesByChild.size} ")
//
//                val list: List<StatisticByOneInfo> = sicknessesByChild.map {
//                    var maxTemperature = 36.6
//                    val sicknessId = it.id
//                    val medicinesSet = mutableSetOf<String>()
//                    facts.filter { it.sicknessId == sicknessId }.forEach {
//                        if (it.temperature > maxTemperature) maxTemperature = it.temperature
//                        Log.d("MyTag", "generateStatistic(),groupBy_3.1, facts: fact.Id ${it.id}, fact.medicineId ${it.medicineId}")
//                        if (it.medicineId != null) {
//                            val medicine = medicineMap[it.medicineId]
//                            Log.d("MyTag", "generateStatistic(),groupBy_3.2, medicine ${medicine}")
//                            val name = medicine?.name ?: ""
//                            Log.d("MyTag", "generateStatistic(),groupBy_3.3, name ${name}")
//                            medicinesSet.add(name)
//                        }
//                    }
//
//                    val countDays = StringUtil.calculateDays(it.dateStart, it.dateEnd)+1
//                    val medicinesList =ArrayList(medicinesSet)
//                    Log.d("MyTag", "generateStatistic(),groupBy_4.1, medicinesList $medicinesList")
//                    medicinesList.forEach { Log.d("MyTag", "groupBy_4.2, it $it") }
//                    StatisticByOneInfo(
//                        it.dateStart,
//                        maxTemperature,
//                        countDays,
//                        medicinesList
//                    )
//                }.sortedBy { it.date }
//                Log.d("MyTag", "generateStatistic(),groupBy_5.1, list ${list.size} ")
//                list.forEach { Log.d("MyTag", "generateStatistic(),groupBy_5.2, list: $it ")}
////                val list: List<StatisticByOneInfo> = factsByChild.groupBy { it.date }
////                    .map {/*3*/ (date, factsByDate) ->
////                        val countDays = 0
////                        val oneDaysList: List<DailyTemperatureByOneInfo> =
////                            factsByDate.map {/*5*/ fact ->
////                                DailyTemperatureByOneInfo(
////                                    time = fact.time,
////                                    temperature = fact.temperature.toString(),
////                                    moreThanUsual = fact.moreThanUsual,
////                                    medicine = medicineMap.get(fact.medicineId)?.name ?: ""
////                                )
////                                StatisticByOneInfo(
////
////
////                                )
////                            }/*5*/.sortedBy { it.time }
////
////                        DailyTemperatureByDayInfo(
////                            date = date,
////                            countDays = countDays,
////                            list = oneDaysList
////                        )
////                    }.sortedBy { it.date }
////                var countDays = 0
////                daysList.forEach { it.countDays = ++countDays }
//                StatisticListInfo(
//                    child = child,
//                    list = list
//                )
//            }//1
//
//        return result
//    }//0
//
//
//    suspend fun initDefaultDatabase(db: AppDatabase) {
//        if (!db.medicineDao().getAll().isEmpty()) {
//            Log.d("MyTag", "skipped initDefaultDatabase()")
//            return
//        }
//        Log.d("MyTag", "initDefaultDatabase()")
//        val childDao = db.childDao()
//        val unitDao = db.unitDao()
//        val medicineDao = db.medicineDao()
//        val categoryDao = db.categoryDao()
//
//        val photo1 = R.drawable.ic_boy
//        val photo2 = R.drawable.ic_girl
//        val photo3 = R.drawable.ic_drug
//
//        val unitEntity1 = UnitEntity(0, "kol.")
//        val unitEntity2 = UnitEntity(0, "спрей")
//        val unitEntity3 = UnitEntity(0, "капли")
//        unitDao.insert(unitEntity1)
//        unitDao.insert(unitEntity2)
//        unitDao.insert(unitEntity3)
//
//        val unitMap = unitDao.getAll().associateBy { it.name }
//
//        val unitId1: Int = unitMap[unitEntity1.name]?.id ?: 0
//        val unitId2: Int = unitMap[unitEntity2.name]?.id ?: 0
//        val unitId3: Int = unitMap[unitEntity3.name]?.id ?: 0
//        //
//        val category1 = CategoryEntity(0, "Antipyretic", true)
//        val category2 = CategoryEntity(0, "Painkiller", false)
//        val category3 = CategoryEntity(0, "Others", false)
//        categoryDao.insert(category1)
//        categoryDao.insert(category2)
//        categoryDao.insert(category3)
//        val categoryMap = categoryDao.getAll().associateBy { it.name }
//
//        val categoryId1: Int = categoryMap[category1.name]?.id ?: 0
//        val categoryId2: Int = categoryMap[category2.name]?.id ?: 0
//        val categoryId3: Int = categoryMap[category3.name]?.id ?: 0
//
//        //
//        val medicineEntity1 =
//            MedicineEntity(0, "Paracetamol", categoryId1, unitId1, photo3, null)
//        val medicineEntity2 =
//            MedicineEntity(0, "Panadol", categoryId1, unitId1, photo3, null)
//        val medicineEntity3 =
//            MedicineEntity(0, "Ibuprofen", categoryId1, unitId1, photo3, null)
//        val medicineEntity4 =
//            MedicineEntity(0, "Sofiest", categoryId2, unitId1, photo3, null)
//        val medicineEntity5 =
//            MedicineEntity(0, "Izofret", categoryId3, unitId1, photo3, null)
//        val medicineEntity6 =
//            MedicineEntity(0, "Sofrenit", categoryId3, unitId1, photo3, null)
//
//        medicineDao.insert(medicineEntity1)
//        medicineDao.insert(medicineEntity2)
//        medicineDao.insert(medicineEntity3)
//        medicineDao.insert(medicineEntity4)
//        medicineDao.insert(medicineEntity5)
//        medicineDao.insert(medicineEntity6)
//        //
//        val d1 = LocalDate.of(2020, 7, 4)
//
//        val child1 = ChildEntity(0, "John", 5, 21, d1, photo1, null)
//
//        childDao.insert(child1)
//        //
//        val unitSize = unitDao.getAll().size
//        val medSize = medicineDao.getAll().size
//        val catSize = categoryDao.getAll().size
//        val childSize = childDao.getAll().size
//        Log.d(
//            "MyTag",
//            "initDefaultDatabase(): units $unitSize, cats $catSize, medicines $medSize, children $childSize"
//        )
//    }

}