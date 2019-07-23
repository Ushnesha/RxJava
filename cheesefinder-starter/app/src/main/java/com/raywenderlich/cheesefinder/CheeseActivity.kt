/*
 * Copyright (c) 2017 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.cheesefinder

import android.text.Editable
import android.text.TextWatcher
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_cheeses.*
import java.util.concurrent.TimeUnit


class CheeseActivity : BaseSearchActivity(){

    //declare a function that returns an observable that will emit strings.
    private fun createButtonClickObservable(): Observable<String> {
        return Observable.create{ emitter->
            searchButton.setOnClickListener{
                emitter.onNext(queryEditText.text.toString())
            }
            //called when the Observable is disposed,
            emitter.setCancellable{
                searchButton.setOnClickListener(null)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val buttonClickStream = createButtonClickObservable()
                //Converting the button click stream into a flowable using LATEST BackpressureStrategy.
                .toFlowable(BackpressureStrategy.LATEST)
        val textChangeStream = createTextChangeObservable()
                //Converting the button click stream into a flowable using BUFFER BackpressureStrategy.
                .toFlowable(BackpressureStrategy.BUFFER)
        //create an observable
        val searchTextFLowable = Flowable.merge<String>(buttonClickStream, textChangeStream)
        //val searchTextFLowable = createButtonClickObservable()
        //Subscribe to the observable and supply a simple Consumer
        searchTextFLowable
                //the chain should start on the main thread instead of on the I/O thread. In Android, all code that works with View should execute on the main thread.
                .subscribeOn(AndroidSchedulers.mainThread())
                //Add the doOnNext operator so that showProgress() will be called every time a new item is emitted.
                .doOnNext{ showProgress() }
                //Specify that the next operator should be called on the I/O thread.
                .observeOn(Schedulers.io())
                //For each search query, you return a list of results.
                .map{ cheeseSearchEngine.search(it) }
                //make sure that the results are passed to the list on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    hideProgress()
                    showResult(it)
                }
    }

    //a function that will return an observable for text changes
    private fun createTextChangeObservable(): Observable<String>{
        val textChangeObservable = Observable.create<String> { emitter->
            val textWatcher= object: TextWatcher{
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int){
                    p0?.toString()?.let{
                        emitter.onNext(it)
                    }
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

                override fun afterTextChanged(p0: Editable?) = Unit

            }
            queryEditText.addTextChangedListener(textWatcher)
            emitter.setCancellable{
                queryEditText.removeTextChangedListener(textWatcher)
            }
        }
        return textChangeObservable
                .filter{ it.length >= 2}
                .debounce(1000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
    }

}