package com.yunguchang.test.rest;

import com.yunguchang.rest.SyncService;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by gongy on 8/25/2015.
 */
@RunWith(Arquillian.class)
public class SyncServiceTest extends AbstractRestTest {
    @ArquillianResource
    private URL baseURL;

    @Test
    public void testGetAllDrivers(@ArquillianResteasyResource("api") SyncService syncService) throws Exception {

        /*
        RYBG#0010701#仇智伟#1065720# #15068367920@$2cc9a1dcbd354001849c491dbd89a70d
        CLBG#fb28cf8d66b74a488105b544f855f846#浙F20648#内部移交#1065691#yes@$ae8639da32ca46c6b1c8c08bbce9c9d6
        CDBG#001070902#路灯公司#0010709@$f511016c04cb433386a8637c0d8882f0
        CLWX#浙F996H7#报修#2015-10-11 14:06:38@$e72cad47c5ec4d27a372042122e8fc20
        LXGG#1200518#王丽勇#13857384032@$b2add828eb0747d7b48dd122f964e4f2
        CLDD#20151103093416171#浙F25981#1065495#丁磊明(18767164520)#嘉兴#南湖集控站#2015-11-03 09:33#2015-11-03 10:33@$087300df0a834a4db772d410025a34d2
        CLWX#1061409#浙F13610(37039)#出厂#2015-11-12 10:18:57@$856f5211ecdc44189f4b552c7ee71dbb
        CLWZ#9b87183d5a0b42cd94f1e9d3653b730d#浙F55586#4@$20aa690f48954d2d830d835469065a94
        CLWX#浙F2S376#报修#2015-10-07 10:22:07@$4d43374fbea646cb8d46a942523bfebd
        RYQJ#1209211#病假#2015-09-08@$b9ff49a17d0d49afb41f393cd3b8b8fa
        CLWX#浙F9C569#进厂#2015-11-02 14:45:37@$e3d32db7fe834464b4022b40b84953c6
        CLWX#9b1b238410424f50b5697754bb554d78#浙F37962#报修#2015-10-27 10:25:04@$974ef69d72d041b49455d2b82383afaa
        CLDD#20151112104608250#9a70c0ef6b4a49c79fd8919e4faa2597#浙F37571#1420669#史建立(18767164620)#嘉兴#宁波#2015-10-11 10:45#2015-11-11 10:45@$4954a442e12c4cada647064ddcbff81f
        CLWX#5b8bc8fdea9a4e1589e67c34a7400b43#浙F996H7#进厂#2015-11-12 10:13:06@$c0e2fad8b3fa4a11bb3f318eb5b48fed
        CLBG#fb28cf8d66b74a488105b544f855f846#浙F20648#移交到外部@$ea21596efde34b4a8cb53e970d432741
        CLWX#多功能工器具柜#出厂#2015-11-02 14:14:41@$0c2e9880b6704398be360f7fe62d432d
        RYBG#0010701#小明#3e38a91e680944d39d9ac24788cb7295#none#18777777777@$a49a9f0ba49144e4b0a8d69cd12a0e68
        PWGG#00230399#c4ca4238a0b923820dcc509a6f75849b@$076caa3ec29a4f929c28dd56f45ed7e4
        UIGG#00181652#钱伟杰#00206#13738288320@$74d516811f974882be501a5cf924e3cb
        CLWZ#浙F55586#4@$83e51aa95b79426ea1e6185da08bb337
        CLWZ#浙F55586#3@$8114a1dafe614b72aaf7cb1799250919
         */
        List<Map> requestDataList = new ArrayList<>();
        Map<String, Object> requestData = new HashMap<>();

        requestData.put("doc", "人员信息变更");
        String id = "2cc9a1dcbd354001849c491dbd89a70d";
        requestData.put("key", "RYBG#0010701#仇智伟#1065720# #15068367920@$" + id);
        requestData = new HashMap<>();
        requestData.put("doc", "车辆信息变更(内部移交)");
        id = "ae8639da32ca46c6b1c8c08bbce9c9d6";
        requestData.put("key", "CLBG#fb28cf8d66b74a488105b544f855f846#浙F20648#内部移交#1065691#yes@$" + id);
        requestDataList.add(requestData);
        requestData = new HashMap<>();
        requestData.put("doc", "车队信息变更");
        id = "f511016c04cb433386a8637c0d8882f0";
        requestData.put("key", "CDBG#001070902#路灯公司#0010709@$" + id);
        requestDataList.add(requestData);
        requestData = new HashMap<>();
        requestData.put("doc", "车辆维修(报修)");
        id = "e72cad47c5ec4d27a372042122e8fc20";
        requestData.put("key", "CLWX#浙F996H7#报修#2015-10-11 14:06:38@$" + id);
        requestDataList.add(requestData);
        requestData = new HashMap<>();
        requestData.put("doc", "用车联系人更改");
        id = "b2add828eb0747d7b48dd122f964e4f2";
        requestData.put("key", "LXGG#1200518#王丽勇#13857384032@$" + id);
        requestDataList.add(requestData);
        requestData = new HashMap<>();
        requestData.put("doc", "车辆调度");
        id = "087300df0a834a4db772d410025a34d2";
        requestData.put("key", "CLDD#20151103093416171#浙F25981#1065495#丁磊明(18767164520)#嘉兴#南湖集控站#2015-11-03 09:33#2015-11-03 10:33@$" + id);
        requestDataList.add(requestData);
        requestData = new HashMap<>();
        requestData.put("doc", "车辆维修(出厂)");
        id = "856f5211ecdc44189f4b552c7ee71dbb";
        requestData.put("key", "CLWX#1061409#浙F13610(37039)#出厂#2015-11-12 10:18:57@$" + id);
        requestDataList.add(requestData);
        requestData = new HashMap<>();
        requestData.put("doc", "车辆违章");
        id = "20aa690f48954d2d830d835469065a94";
        requestData.put("key", "CLWZ#9b87183d5a0b42cd94f1e9d3653b730d#浙F55586#4@$" + id);
        requestDataList.add(requestData);
        requestData = new HashMap<>();
        requestData.put("doc", "车辆维修(报修)");
        id = "4d43374fbea646cb8d46a942523bfebd";
        requestData.put("key", "CLWX#浙F2S376#报修#2015-10-07 10:22:07@$" + id);
        requestDataList.add(requestData);
        requestData = new HashMap<>();
        requestData.put("doc", "驾驶员请假");
        id = "b9ff49a17d0d49afb41f393cd3b8b8fa";
        requestData.put("key", "RYQJ#1209211#病假#2015-09-08@$" + id);
        requestDataList.add(requestData);
        requestData = new HashMap<>();
        requestData.put("doc", "车辆维修(进厂)");
        id = "e3d32db7fe834464b4022b40b84953c6";
        requestData.put("key", "CLWX#浙F9C569#进厂#2015-11-02 14:45:37@$" + id);
        requestDataList.add(requestData);
        requestData = new HashMap<>();
        requestData.put("doc", "车辆维修(报修)");
        id = "974ef69d72d041b49455d2b82383afaa";
        requestData.put("key", "CLWX#9b1b238410424f50b5697754bb554d78#浙F37962#报修#2015-10-27 10:25:04@$" + id);
        requestDataList.add(requestData);
        requestData = new HashMap<>();
        requestData.put("doc", "车辆调度");
        id = "4954a442e12c4cada647064ddcbff81f";
        requestData.put("key", "CLDD#20151112104608250#9a70c0ef6b4a49c79fd8919e4faa2597#浙F37571#1420669#史建立(18767164620)#嘉兴#宁波#2015-10-11 10:45#2015-11-11 10:45@$" + id);
        requestDataList.add(requestData);
        requestData = new HashMap<>();
        requestData.put("doc", "车辆维修(进厂)");
        id = "c0e2fad8b3fa4a11bb3f318eb5b48fed";
        requestData.put("key", "CLWX#5b8bc8fdea9a4e1589e67c34a7400b43#浙F996H7#进厂#2015-11-12 10:13:06@$" + id);
        requestDataList.add(requestData);
        requestData = new HashMap<>();
        requestData.put("doc", "车辆信息变更(移交到外部)");
        id = "ea21596efde34b4a8cb53e970d432741";
        requestData.put("key", "CLBG#fb28cf8d66b74a488105b544f855f846#浙F20648#移交到外部@$" + id);
        requestDataList.add(requestData);
        requestData = new HashMap<>();
        requestData.put("doc", "车辆维修(出厂)");
        id = "0c2e9880b6704398be360f7fe62d432d";
        requestData.put("key", "CLWX#多功能工器具柜#出厂#2015-11-02 14:14:41@$" + id);
        requestDataList.add(requestData);
        requestData = new HashMap<>();
        requestData.put("doc", "人员信息变更");
        id = "a49a9f0ba49144e4b0a8d69cd12a0e68";
        requestData.put("key", "RYBG#0010701#小明#3e38a91e680944d39d9ac24788cb7295#none#18777777777@$" + id);
        requestDataList.add(requestData);
        requestData = new HashMap<>();
        requestData.put("doc", "密码修改");
        id = "076caa3ec29a4f929c28dd56f45ed7e4";
        requestData.put("key", "PWGG#00230399#c4ca4238a0b923820dcc509a6f75849b@$" + id);
        requestDataList.add(requestData);
        requestData = new HashMap<>();
        requestData.put("doc", "用户信息修改");
        id = "74d516811f974882be501a5cf924e3cb";
        requestData.put("key", "UIGG#00181652#钱伟杰#00206#13738288320@$" + id);
        requestDataList.add(requestData);
        requestData = new HashMap<>();
        requestData.put("doc", "车辆违章");
        id = "83e51aa95b79426ea1e6185da08bb337";
        requestData.put("key", "CLWZ#浙F55586#4@$" + id);
        requestDataList.add(requestData);
        requestData = new HashMap<>();
        requestData.put("doc", "车辆违章");
        id = "8114a1dafe614b72aaf7cb1799250919";
        requestData.put("key", "CLWZ#浙F55586#3@$" + id);
        requestDataList.add(requestData);


        for (Map map : requestDataList) {

            Response response = syncService.requestDispatcher(map, null);
            Map content = response.readEntity(Map.class);
            assertNotNull(content);
            assertEquals(200, response.getStatus());
        }

    }
}
