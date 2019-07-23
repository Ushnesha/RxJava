package com.example.rxtut

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.lang.Exception
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

fun main(args : Array<String>) {
    Observable.just("Hello Reactive World")
        .subscribe { value -> println(value) }

    println()

    Observable.just("Apple", "Orange", "Banana")
        .map({input -> throw RuntimeException()})
        .subscribe(
            //onNext
            { value -> println("Recieved : $value") },
            //onError
            { error -> println("Error : $error") },
            //onComplete used in lambda expression
            { println("Completed") }
        )

    println()

    Observable.fromArray("Apple", "Orange", "Banana")
        .subscribe{println(it)}

    println()

    Observable.fromIterable(listOf("Apple", "Orange", "Banana"))
        .subscribe(
            {value -> println("Recieved : $value")}, //onNext
            { error -> println("Error : $error") },  //onError
            { println("Completed") }   //Completed
        )

    println()

    getObservableFromList(listOf("Yellow", "Orange", "Green"))
        .subscribe{println("Received : $it")}

    println()

    getObservableFromList(listOf("Ice", "", "Rock"))
        .subscribe({value -> println("Received : $value")},
            {error -> println("Error : $error")}
        )

    println()

    //INTERVAL
    Observable.intervalRange(10L,
        2L,
        0L,
        1L,
        TimeUnit.SECONDS)
        .subscribe{println("We just received : $it")}

    println()

    //BACKPRESSURE: OBSERVABLE vs FLOWABLE

//    //using observable got outOfMemopryException
//    val observable = PublishSubject.create<Int>()
//    observable.observeOn(Schedulers.computation())
//        .subscribe({
//            println("The Number is: $it")
//        },
//            {t ->
//                print(t.message)
//            })
//        for( i in 0..1000000){
//            observable.onNext(i)
//        }
//
//    println()

    //observable with the help of flowable
    val observable1 = PublishSubject.create<Int>()
    observable1
        //Backpressure strategy DROP will drop some of the items in order to preserve memory capabilities.
        .toFlowable(BackpressureStrategy.DROP)
        .observeOn(Schedulers.computation())
        .subscribe({
            println("The Number is: $it")
        },
            {t ->
                print(t.message)
            })
    for( i in 0..1000){
        observable1.onNext(i)
    }

    //TYPES OF EMITTERS

    //1.Flowable
    Flowable.just("This is a flowable")
        .subscribe(
            {value -> println("Received : $value")},
            {error -> println("Error : $error")},
            {println("Completed")}
        )

    println()

    //2.Maybe
    Maybe.just("This is a Maybe")
        .subscribe(
            {value -> println("Received : $value")},
            {error -> println("Error : $error")},
            {println("Completed")}
        )

    println()

    //3.Single
    Single.just("This is a Single")
        .subscribe(
            {value -> println("Received : $value")},
            {error -> println("Error : $error")}
        )

    println()

    //4.Completable
    Completable.create{emitter ->
        emitter.onComplete()
        emitter.onError(Exception())
    }

    println()

    //methods of observables
    Observable.just("Hello")
        .doOnSubscribe { println("subscribed") }
        .doOnNext { s -> println("Received : $s") }
        .doAfterNext{println("After Receiving")}
        .doOnError{e -> println("Error : $e")}
        .doOnComplete{println("Completed")}
        .doFinally{println("Do finally!")}
        .doOnDispose{ println("Do on dispose")}
        .subscribe{ println("subscribe")}

    println()




}


//CREATE OBSERVABLE

//function to convert a list to observable
fun getObservableFromList(myList : List<String>) =
    Observable.create<String>{emitter ->
        myList.forEach { kind ->
            if(kind == ""){
                emitter.onError(Exception("There's no any value to show"))
            }
            emitter.onNext(kind)
        }
        emitter.onComplete()
    }







