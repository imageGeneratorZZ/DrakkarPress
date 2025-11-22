package com.drakkarpress.platform.rate;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RateLimit {
    String key();            // logical key per action
    int limit();              // max executions per period
    Period period() default Period.DAY;
    enum Period { DAY }
}
