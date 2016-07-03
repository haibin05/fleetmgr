package com.yunguchang.test.jpa;

import com.yunguchang.data.DepotRepository;
import com.yunguchang.model.persistence.TBusReturningDepotEntity;
import com.yunguchang.model.persistence.TEmbeddedDepot;
import org.jboss.arquillian.junit.Arquillian;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by 禕 on 2015/10/11.
 */
@RunWith(Arquillian.class)
public class DepotTest extends AbstractJpaTest {
    @Inject
    private DepotRepository depotRepository;



    @Test
    public void testListReturningDepot() {
        List<TBusReturningDepotEntity> results = depotRepository.listReturningDepotByStartAndEnd(new DateTime(2015, 10, 1, 0, 0), new DateTime(2015, 10, 31, 0, 0), null);
        assertTrue(results.size()>0);
    }
    @Test
    public void testListDepots(){
        List<TEmbeddedDepot> results = depotRepository.listDepots(null);
        assertTrue(results.size()>0);

    }

}
