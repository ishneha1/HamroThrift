package com.example.hamrothrift.model

data class ProductModel(
    var productID : String ="",
    var productName : String ="",
    var price : Double =0.0,
    var quantity : Int =0,
    var description : String ="",
    var productImage : String ="",
)
