package com.dimnowgood.bestapp.util

data class Result<T>(val status:Status, val data:T?, val message:String) {

    companion object{

        fun<T> success(data:T?, msg:String):Result<T>{
            return Result(Status.SUCCESS,data,msg)
        }

        fun<T> error(data:T?, msg:String):Result<T>{
            return Result(Status.ERROR, data, msg)
        }

        fun<T> loading(data:T?, msg:String):Result<T>{
            return Result(Status.LOADING, data, msg)
        }

    }
}