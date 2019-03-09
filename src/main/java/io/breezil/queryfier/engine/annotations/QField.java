package io.breezil.queryfier.engine.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.breezil.queryfier.engine.enums.JoinType;

@Target({ ElementType.FIELD, ElementType.METHOD} )
@Retention(RetentionPolicy.RUNTIME)
public @interface QField {
	String name();
	String comparator() default "=";
	String valueWrapper() default "";
	JoinType join() default JoinType.INNER_JOIN;
}
