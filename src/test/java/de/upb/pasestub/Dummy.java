package de.upb.pasestub;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class Dummy {
    @Test
    public void dummyTest1() {
        int a = 6;
        assertEquals(6, a);
    }
    @Test
    public void dummyTest2() {
        int a = 5;
        assertEquals(5, a);
    }
    @Test
    public void dummyTest3(){
        assert(Stub.testMethod());
    }
}