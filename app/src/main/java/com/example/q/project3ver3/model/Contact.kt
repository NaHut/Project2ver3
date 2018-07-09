package com.example.q.project3ver3.model

class Contact {

    var id: Int = 0
    var name: String? = null
    var phone_number: String? = null
    var timestamp: String? = null

    constructor() {}

    constructor(id: Int, name: String, phone_number: String, timestamp: String) {
        this.id = id
        this.name = name
        this.phone_number = phone_number
        this.timestamp = timestamp
    }

}