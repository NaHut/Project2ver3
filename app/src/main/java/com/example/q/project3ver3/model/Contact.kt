package com.example.q.project3ver3.model

class Contact {

    var id: Int = 0
    var name: String? = null
    var phone_number: String? = null
    var image_path: String? = null

    constructor() {}

    constructor(id: Int, name: String, phone_number: String, image_path: String) {
        this.id = id
        this.name = name
        this.phone_number = phone_number
        this.image_path = image_path
    }

}