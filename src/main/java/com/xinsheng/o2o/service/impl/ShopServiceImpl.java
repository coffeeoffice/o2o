package com.xinsheng.o2o.service.impl;


import java.io.InputStream;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.xinsheng.o2o.dao.ShopDao;
import com.xinsheng.o2o.dto.ShopExecution;
import com.xinsheng.o2o.entity.Shop;
import com.xinsheng.o2o.enums.ShopStateEnum;
import com.xinsheng.o2o.exceptions.ShopOperationException;
import com.xinsheng.o2o.service.ShopService;
import com.xinsheng.o2o.util.ImageUtil;
import com.xinsheng.o2o.util.PathUtil;


//这一层就是实现service层未实现的方法
@Service
public class ShopServiceImpl implements ShopService{
	@Autowired
	private ShopDao shopDao;
	
	@Transactional
	public ShopExecution addShop(Shop shop, InputStream shopImgInputStream,String fileName) throws ShopOperationException {
		if(shop == null) {//如果店铺信息为空，则返回枚举类型的null_shop
			return new ShopExecution(ShopStateEnum.NULL_SHOP);	
		}
		
		try {//初始化一些店铺信息必要的参数,外面不能改变的值
			shop.setEnableStatus(0);
			shop.setCreateTime(new Date());
			shop.setLastEditTime(new Date());
			//添加店铺信息
			int effectedNum = shopDao.insertShop(shop);
			if(effectedNum <= 0) {
				throw new ShopOperationException("店铺创建失败");
			}else {
				if(shopImgInputStream!=null) {
					//存储图片,传入shop实体对象和shopImg图片
					try {
						addShopImg(shop, shopImgInputStream,fileName);
					}catch(Exception e) {
						throw new ShopOperationException("addShopImg error:" + e.getMessage());
					}
					//更新店铺的图片地址
					effectedNum = shopDao.updateShop(shop);
					if(effectedNum<=0) {
						throw new ShopOperationException("更新图片地址失败");
					}
				    
					
				}
				
			}
			
		}catch(Exception e) {
			throw new ShopOperationException("addShop error:" + e.getMessage());
	
		}
		
		
		return new ShopExecution(ShopStateEnum.CHECK,shop);//
	}

//	private void addShopImg(Shop shop, File shopImg) {
//		// TODO Auto-generated method stub
//		
//	}

	private void addShopImg(Shop shop, InputStream shopImgInputStream ,String fileName) {
		//获取shop图片目录的相对值路径
		String dest = PathUtil.getShopImagePath(shop.getShopId());
		String shopImgAddr = ImageUtil.generateThumbnail(shopImgInputStream, fileName,dest);
		shop.setShopImg(shopImgAddr);
		
	}

	@Override
	public Shop getByShopId(long shopId) {
		
		return shopDao.queryByShopId(shopId);
	}

	@Override
	public ShopExecution modifyShop(Shop shop, InputStream shopImgInputStream, String fileName)
			throws ShopOperationException {
		if (shop ==null||shop.getShopId()==null) {
			return new ShopExecution(ShopStateEnum.NULL_SHOP);
		} else {
			//1.判断是否需要处理图片
			try {
			if (shopImgInputStream!=null&&fileName!=null&&!"".equals(fileName)) {
				Shop tempShop = shopDao.queryByShopId(shop.getShopId());
				if (tempShop.getShopImg()!=null) {
					ImageUtil.deleteFileOrPath(tempShop.getShopImg());
				}
				addShopImg(shop, shopImgInputStream, fileName);
			}
			//2.跟新店铺信息
			shop.setLastEditTime(new Date());
			int effectedNum = shopDao.updateShop(shop);
			if (effectedNum<=0) {
				return new ShopExecution(ShopStateEnum.INNER_ERROR);
			} else {
				shop = shopDao.queryByShopId(shop.getShopId());
				return new ShopExecution(ShopStateEnum.SUCCESS,shop);
			}}catch(Exception e) {
				throw new ShopOperationException("modifyShop error:"+e.getMessage());
			}
			
			
		}
		
		
	}
	
	
	
	
}
