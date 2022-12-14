package com.xinsheng.o2o.web.shopadmin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sun.imageio.plugins.common.ImageUtil;
import com.xinsheng.o2o.dto.ShopExecution;
import com.xinsheng.o2o.entity.Area;
import com.xinsheng.o2o.entity.PersonInfo;
import com.xinsheng.o2o.entity.Shop;
import com.xinsheng.o2o.entity.ShopCategory;
import com.xinsheng.o2o.enums.ShopStateEnum;
import com.xinsheng.o2o.service.AreaService;
import com.xinsheng.o2o.service.ShopCategoryService;
import com.xinsheng.o2o.service.ShopService;
import com.xinsheng.o2o.util.CodeUtil;
import com.xinsheng.o2o.util.HttpServletRequestUtil;
import com.xinsheng.o2o.util.PathUtil;
import com.xinsheng.o2o.util.ImageUtil;
@Controller
@RequestMapping("/shopadmin")
public class ShopManagementController {
	@Autowired
	private ShopService shopService;
	@Autowired
	private ShopCategoryService shopCategoryService;
	@Autowired
	private AreaService areaService;
	
	@RequestMapping(value="/getshopinitinfo",method=RequestMethod.GET)
	@ResponseBody
	private Map<String,Object> getshopinitinfo(){
		Map<String,Object>  modelMap = new HashMap<String,Object>();
		List<ShopCategory> shopCategoryList =new ArrayList<ShopCategory>();
		List<Area> areaList =new ArrayList<Area>();
		try {
			shopCategoryList = shopCategoryService.getShopCategoryList( new ShopCategory());
			areaList =areaService.getAreaList();
			modelMap.put("shopCategoryList", shopCategoryList);
			modelMap.put("areaList", areaList);
			modelMap.put("success", true);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
		}
		
		
		
		return modelMap;
		 
		
	}
	@RequestMapping(value="/registershop",method=RequestMethod.POST)
	@ResponseBody
	private Map<String,Object> registerShop(HttpServletRequest request){
		//????????????????????????????????request??????????????map??????????????????????????
		// ??????????????Map<String,Object>??????????????@ResponseBody??????????json????
		
		//1.????????????????????????????????????????????????????
		Map<String,Object> modelMap=new HashMap<String,Object>();
		if (!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "?????????????? ");
			return modelMap;
		}
		String shopStr =  HttpServletRequestUtil.getString(request,"shopStr");
		ObjectMapper mapper = new ObjectMapper();
		Shop shop = null;
		try {
			shop = mapper.readValue(shopStr, Shop.class);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
			return modelMap;
		}
		
		CommonsMultipartFile shopImg = null;
		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		if (commonsMultipartResolver.isMultipart(request)) {
			MultipartHttpServletRequest multipartHttpServletRequest  = (MultipartHttpServletRequest) request;
			shopImg = (CommonsMultipartFile) multipartHttpServletRequest
					.getFile("shopImg");
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "????????????????");
			return modelMap;
		}
		try {
			shop = mapper.readValue(shopStr, Shop.class);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}
		
	
		
		//2.????????
		if (shop != null&& shopImg != null) {
			PersonInfo owner =new PersonInfo();
			owner.setUserId(1L);
			shop.setOwner(owner);
//			File shopImgFile = new File(PathUtil.getImgBasePath() + ImageUtil.getRandomFileName());
//			
//			try {
//				shopImgFile.createNewFile();
//			} catch (Exception e) {
//				modelMap.put("success", false);
//				modelMap.put("errMsg", e.toString());
//				return modelMap;
//			}
//			try {
//				inputStreamtoFile(shopImg.getInputStream(), shopImgFile);
//			} catch (Exception e) {
//				modelMap.put("success", false);
//				modelMap.put("errMsg", e.toString());
//				return modelMap;
//			}
			
			ShopExecution se;
			try {
				se = ShopService.addShop(shop,shopImg.getInputStream(),shopImg.getOriginalFilename());
				if (se.getState() == ShopStateEnum.CHECK.getState()) {
					modelMap.put("succcess", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", se.getStateInfo());
				}
			} catch (RuntimeException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());
			} catch (IOException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());
			}
			
			return modelMap;
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "??????????????");
			return modelMap;
		}
		
		
		
		
		
		//3.????????,


	
	}
//	private static void inputStreamtoFile(InputStream ins ,File file) {
//		FileOutputStream os =null;
//		try {
//			os = new FileOutputStream(file);
//			int bytesRead = 0;
//			byte[] buffer =new byte[1024];
//			while ((bytesRead = ins.read(buffer))!=-1) {
//				os.write(buffer,0,bytesRead);
//				
//			}
//		} catch (Exception e) {
//			throw new RuntimeException("????inputStreamtoFile????????"+e.getMessage());
//		}finally{
//			try {
//				if (os!=null) {
//					os.close();
//				}
//				if (ins!=null) {
//					ins.close();
//				}
//				
//			} catch (Exception e) {
//				throw new RuntimeException("????inputStreamtoFile????io????????"+e.getMessage());
//			}
//		}
//		
//	}
	
}
