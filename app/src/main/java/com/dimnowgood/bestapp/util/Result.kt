package com.dimnowgood.bestapp.util

data class Result<out T>(val status:Status, val data:T?, val message:String) {

    companion object{

        fun<T> success(data:T?):Result<T>{
            return Result(Status.SUCCESS,data,"")
        }

        fun<T> error(msg:String, data:T?):Result<T>{
            return Result(Status.ERROR, data, msg)
        }

        fun<T> loading(msg:String, data:T?):Result<T>{
            return Result(Status.LOADING, data, "")
        }

    }
}