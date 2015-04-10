package com.example.unmcampuslocatorapp;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.os.Process;
import android.content.Context;
import android.content.Intent;

public class MyExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
private final Context myContext;
private final Class<?> myActivityClass;

public MyExceptionHandler(Context context, Class<?> c) {

myContext = context;
myActivityClass = c;
}

@Override
public void uncaughtException(Thread thread, Throwable exception) {

StringWriter stackTrace = new StringWriter();
exception.printStackTrace(new PrintWriter(stackTrace));
System.err.println(stackTrace);// You can use LogCat too
Intent intent = new Intent(myContext, myActivityClass);
String s = stackTrace.toString();
//you can use this String to know what caused the exception and in which Activity
intent.putExtra("uncaughtException",
        "Exception is: " + stackTrace.toString());
intent.putExtra("stacktrace", s);
myContext.startActivity(intent);
//for restarting the Activity
Process.killProcess(Process.myPid());
System.exit(0);
}
}