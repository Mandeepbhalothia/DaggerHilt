package com.mandeep.daggerhilt

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private companion object {
        val TAG = "MainActivity"
    }

    // fieldInjection
    @Inject
    lateinit var someClass: SomeClass

    //constructor injection
    @Inject
    lateinit var someOtherClass: SomeOtherClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate: Hello fieldInjection what is you message, ${someClass.doSomething()}")
        Log.d(TAG, "onCreate: constructor injection, ${someOtherClass.getSimpleMsg()}")
        Log.d(TAG, "onCreate: constructor injection, ${someOtherClass.getSomething()}")
    }

    class SomeClass
    @Inject
    constructor() {
        fun doSomething(): String {
            return "I am doing something"
        }
    }

    // constructor injection
    class SomeOtherClass
    @Inject
    constructor(
        private var classC: ClassC
    ) {
        fun getSomething(): String {
            return "Constructor Injection example"
        }

        fun getSimpleMsg(): String{
            classC.callInterfaceMethod()
            return classC.simpleMsg()
        }
    }

    class ClassC
    @Inject
    constructor(
        @Impl1 private val classD: SomeInterface
    ) {
        fun simpleMsg(): String {
            return "Simple String"
        }

        fun callInterfaceMethod() {
            Log.d(TAG, "callInterfaceMethod: ${classD.getSomeThing()}")
        }
    }

    // constructor injection if we use some dependencies that doesn't use di like Gson or if we use interface

    class ClassD
    @Inject
    constructor() : SomeInterface {
        override fun getSomeThing(): String {
            return "From Class D"
        }

    }

    interface SomeInterface {
        fun getSomeThing(): String
    }

    @InstallIn(ApplicationComponent::class) // level of use and scope
    @dagger.Module
    class Module{

        @Impl1 // this is not required now. But we have to use when multiple classes are using same interface
        @Singleton // scope should not be upper level (eg If we use Activity::class and scope of singleton)
        @Provides
        fun provideSomeInterface(): SomeInterface{
            return ClassD()
        }
    }

    // Retention is used for same interface but methods are different
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Impl1 // now we can annotate methods in module and in class constructor as this Impl1

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class Impl2
}

