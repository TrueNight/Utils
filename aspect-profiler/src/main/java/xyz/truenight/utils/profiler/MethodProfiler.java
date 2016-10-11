/**
 * Copyright (C) 2016 Mikhail Frolov
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.truenight.utils.profiler;

import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Aspect
public class MethodProfiler {
    private static final AtomicInteger msExecMethodNumber = new AtomicInteger();
    private static volatile boolean enabled = true;
    private static volatile boolean showDeclaration;
    private static volatile int depth;
    private static volatile int offset;

    private MethodProfiler() {
    }

    @Pointcut("within(@xyz.truenight.utils.profiler.ProfileMethod *)")
    public void withinAnnotatedClass() {
    }

    @Pointcut("execution(!synthetic * *(..)) && withinAnnotatedClass()")
    public void methodInsideAnnotatedType() {
    }

    @Pointcut("execution(!synthetic *.new(..)) && withinAnnotatedClass()")
    public void constructorInsideAnnotatedType() {
    }

    @Pointcut("execution(@xyz.truenight.utils.profiler.ProfileMethod * *(..)) || methodInsideAnnotatedType()")
    public void method() {
    }

    @Pointcut("execution(@xyz.truenight.utils.profiler.ProfileMethod *.new(..)) || constructorInsideAnnotatedType()")
    public void constructor() {
    }

    public static void setEnabled(boolean enabled) {
        MethodProfiler.enabled = enabled;
    }

    public static void init(boolean showDeclaration, int callerDepth) {
        if (callerDepth < 0) {
            callerDepth = 0;
        }

        int offset = showDeclaration ? 0 : 1;
        int depth = showDeclaration ? callerDepth + 1 : callerDepth;

        MethodProfiler.showDeclaration = showDeclaration;
        MethodProfiler.depth = depth;
        MethodProfiler.offset = offset + 3;
    }

    @Around("method() || constructor()")
    public Object logAndExecute(ProceedingJoinPoint joinPoint) throws Throwable {
        int number = msExecMethodNumber.incrementAndGet();
        enterMethod(joinPoint, number);

        long startNanos = System.nanoTime();
        long threadTime = SystemClock.currentThreadTimeMillis();
        Object result = joinPoint.proceed();
        long stopNanos = System.nanoTime();
        long lengthMillis = TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos);
        long threadLengthMillis = SystemClock.currentThreadTimeMillis() - threadTime;

        exitMethod(joinPoint, result, lengthMillis, threadLengthMillis, number);

        return result;
    }

    private static void enterMethod(JoinPoint joinPoint, int number) {
        if (!enabled) return;

        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();

        Class<?> cls = codeSignature.getDeclaringType();
        String methodName = codeSignature.getName();
        String[] parameterNames = codeSignature.getParameterNames();
        Object[] parameterValues = joinPoint.getArgs();
        SourceLocation sourceLocation = joinPoint.getSourceLocation();

        StringBuilder builder = new StringBuilder("<- ").append(number).append(" ");
        builder.append(methodName).append('(');
        for (int i = 0; i < parameterValues.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(parameterNames[i]).append('=');
            builder.append(Strings.toString(parameterValues[i]));
        }
        builder.append(')');

        if (Looper.myLooper() != Looper.getMainLooper()) {
            builder.append(" [Thread:\"").append(Thread.currentThread().getName()).append("\"]");
        }

        builder.append("(").append(sourceLocation.getFileName()).append(":").append(sourceLocation.getLine()).append(")");

        Log.d(asTag(cls), builder.toString());

//            if (showDeclaration || depth > 0) {
//                Tracer.print("" + sourceLocation.getWithinType() + " " + " " + sourceLocation.getFileName() + " " + sourceLocation.getLine());
//                Tracer.print("" + sourceLocation.getWithinType().getName() + "(" + sourceLocation.getFileName() + ":" + sourceLocation.getLine() + ")");
//                Tracer.print("(" + sourceLocation.getFileName() + ":" + sourceLocation.getLine() + ")");
//                Tracer.config().depth(depth).depthOffset(offset).print();
//            }
    }

    private static void exitMethod(JoinPoint joinPoint, Object result, long lengthMillis, long threadLengthMillis, int number) {
        if (!enabled) return;

        Signature signature = joinPoint.getSignature();
        SourceLocation sourceLocation = joinPoint.getSourceLocation();

        Class<?> cls = signature.getDeclaringType();
        String methodName = signature.getName();
        boolean hasReturnType = signature instanceof MethodSignature
                && ((MethodSignature) signature).getReturnType() != void.class;

        //-1 to fix threadLengthMillis accuracy(no nano time for this measurement)
        boolean showThreadTime = threadLengthMillis == lengthMillis || threadLengthMillis - 1 == lengthMillis;
        showThreadTime = !showThreadTime;

        StringBuilder builder = new StringBuilder("-> ").append(number).append(" ")
                .append(methodName)
                .append(" [");

        if (showThreadTime) {
            builder.append(threadLengthMillis).append("/");
        }

        builder.append(lengthMillis)
                .append("ms]");

        if (hasReturnType) {
            builder.append(" = ");
            builder.append(Strings.toString(result));
        }

        builder.append("(").append(sourceLocation.getFileName()).append(":").append(sourceLocation.getLine()).append(")");

        Log.d(asTag(cls), builder.toString());
    }

    private static String asTag(Class<?> cls) {
        if (cls.isAnonymousClass()) {
            return asTag(cls.getEnclosingClass());
        }
        return cls.getSimpleName();
    }
}