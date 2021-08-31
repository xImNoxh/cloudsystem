package de.polocloud.api.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

/*
  An annotation that marks that the given
  class wants to be auto registered

  But only if the class is supported

  For example Packets or EventListeners
 */
public @interface AutoRegistry {

}
