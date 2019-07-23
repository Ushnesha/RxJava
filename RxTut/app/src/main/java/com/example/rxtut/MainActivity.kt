package com.example.rxtut

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //TRANSFORMERS
        fun <T> applyObservableAsync() : ObservableTransformer<T, T> {
            return ObservableTransformer { value ->
                value.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            }
        }

        Observable.just("Apple", "Orange", "Banana")
            .compose(applyObservableAsync())
            .subscribe{ v-> println("First Observable Received : $v")}

        Observable.just("Yellow", "Blue", "Green")
            .compose(applyObservableAsync())
            .subscribe{ v-> println("Second Observable Received : $v")}


        //    //OPERATORS
    //1. map()println("map operator")
    Observable.just("Water", "Fire", "Wood")
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map {
                m -> m+" 2"
        }
        .subscribe{
                v-> println("Received : $v")
        }

        //2. flatmap()

        Observable.just("Water", "Fire", "Wood")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                m -> Observable.just(m+" 2")
                .subscribeOn(Schedulers.io())
            }
            .subscribe{
                    v -> println("Received : $v")
            }

        //3. zip
        Observable.zip(
            Observable.just("Roses","Sunflower","Leaves", "Trunk"),
            Observable.just("Red","Yellow","Green","Brown"),
            //the value of first and second observables are string and the output is also string
            BiFunction<String, String, String> {
                type, color -> "$type are $color"
            }
        ).subscribe{
            v -> println("Received : $v")
        }

    }




}
