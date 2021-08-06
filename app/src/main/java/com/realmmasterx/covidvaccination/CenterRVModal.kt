package com.realmmasterx.covidvaccination

class CenterRVModal {
    constructor(centerName: String, centerAddress: String, centerFromTime: String, centerToTime: String, feeType: String, ageLimit: Int, vaccineName: String, availabilityCapacity: Int) {
        this.centerName = centerName
        this.centerAddress = centerAddress
        this.centerFromTime = centerFromTime
        this.centerToTime = centerToTime
        this.feeType = feeType
        this.ageLimit = ageLimit
        this.vaccineName = vaccineName
        this.availabilityCapacity = availabilityCapacity
    }

    val centerName: String
    val centerAddress: String
    val centerFromTime: String
    val centerToTime: String
    val feeType: String
    val ageLimit: Int
    var vaccineName: String
    var availabilityCapacity: Int


}