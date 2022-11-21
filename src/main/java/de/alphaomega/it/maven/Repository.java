package de.alphaomega.it.maven;

import javax.annotation.Nonnull;
import java.lang.annotation.*;

@Documented
@Target(ElementType.LOCAL_VARIABLE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Repository {

    @Nonnull
    String url();
}
