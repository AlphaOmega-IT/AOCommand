package de.alphaomega.it.cmdhandlerapi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
	
	String name();
	
	String permission() default "";

	String noPermsDE() default "<red>Du hast keine Rechte diesen Befehl ausführen zu können.</red>";
	String noPermsEN() default "<red>You do not have the right permission to execute this command.</red>";

	String[] aliases() default {};
	
	String description() default "";
	
	String usage() default "";
	
	boolean inGameOnly() default true;
	
}