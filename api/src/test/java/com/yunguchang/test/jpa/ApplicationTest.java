package com.yunguchang.test.jpa;

import com.yunguchang.data.ApplicationRepository;
import com.yunguchang.data.BusBusinessRelationRepository;
import com.yunguchang.data.UserRepository;
import com.yunguchang.model.persistence.TSysUserEntity;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by gongy on 2015/11/25.
 */
@RunWith(Arquillian.class)
public class ApplicationTest extends AbstractJpaTest {
    @Inject
    private BusBusinessRelationRepository busBusinessRelationRepository;

    @Test
    public void testGetDispatchOfApplication(){
        List<TSysUserEntity> result = busBusinessRelationRepository.getDispatcherOfPassenger("000b50b3abe14a35b47f9b55b835e0c5");
        assertTrue(result != null);
    }


}
