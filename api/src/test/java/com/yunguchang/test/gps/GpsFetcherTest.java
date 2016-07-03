package com.yunguchang.test.gps;

import com.yunguchang.gps.GpsCorrelate;
import com.yunguchang.gps.GpsFetcher;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by 禕 on 2015/9/8.
 */
@RunWith(MockitoJUnitRunner.class)
public class GpsFetcherTest {

    @Mock
    GpsCorrelate gpsCorrelate;


    @InjectMocks
    GpsFetcher gpsFetch;

    @Test
    public void fetchGps() throws Exception {
        gpsFetch.fetchGps();
        verify(gpsCorrelate,times(1)).putGpsPoints(any(List.class),any(DateTime.class));
    }
}
