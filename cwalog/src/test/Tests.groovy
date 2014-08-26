package test

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import groovy.util.GroovyTestSuite;
import junit.framework.TestSuite;


@RunWith(Suite.class)
@Suite.SuiteClasses([
   LineTest.class,
   GroupTest.class
])
public class Tests {
}