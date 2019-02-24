package io.breezil.queryfier.engine.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface QEntity {
    Class<? extends Object> name();
	String alias();
}
