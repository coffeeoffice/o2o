package com.xinsheng.o2o.dao;
import com.xinsheng.o2o.BaseTest;
import com.xinsheng.o2o.entity.Area;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AreaDaoTest extends BaseTest {
 
	@Autowired
	private AreaDao areaDao;
	
	@Test
	public void testQueryArea() {
		List<Area>areaList = areaDao.queryArea();
		assertEquals(2,areaList.size());
		
	}
	
	
	
}
