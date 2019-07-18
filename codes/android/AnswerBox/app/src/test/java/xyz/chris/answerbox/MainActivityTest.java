package xyz.cathal.answerbox;

import android.support.design.internal.BottomNavigationItemView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Created by chrisdurning on 12/03/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class MainActivityTest {

    @Mock
    private BottomNavigationItemView bottomNavigationItemView;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(MainActivity.class);
    }

    @Test
    public void testBottomBar() throws Exception {

    }

}
