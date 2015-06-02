package ut.com.bccriskadvisory;

import org.junit.Test;
import com.bccriskadvisory.MyPluginComponent;
import com.bccriskadvisory.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}