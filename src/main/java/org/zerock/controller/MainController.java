package org.zerock.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.domain.CategoryVO;
import org.zerock.domain.DetailVO;
import org.zerock.domain.PageDTO;
import org.zerock.domain.ProductVO;
import org.zerock.service.CustomerServiceImpl;
import org.zerock.service.OrderServiceImpl;
import org.zerock.service.ProductServiceImpl;
import org.zerock.service.ReplyService;
import org.zerock.service.basketService;
import org.zerock.utils.ReviewListUtils;
import org.zerock.utils.UploadFileUtils;

import lombok.extern.log4j.Log4j;
import net.sf.json.JSONArray;

@Controller
@Log4j
public class MainController {
	@Resource
	private ProductServiceImpl pm;
	@Resource
	private basketService bm;
	@Resource
	private ReplyService rm;
	@Resource(name = "uploadPath")
	private String uploadPath; // servlet-context占쏙옙占쏙옙占쏙옙 占쌍억옙占�
	@Inject
	private CustomerServiceImpl customerService;
	@Resource
	private OrderServiceImpl orderService;
	private PageDTO page;
	
	@RequestMapping("/")
	public String toMainPage(HttpSession session, Model model) {
		String customerName = "";
		Long customerCode = (Long) session.getAttribute("customerCode");
		
		if (customerCode!=null) {
			customerName = customerService.getCustomerName(customerCode);
		}
		model.addAttribute("customerName", customerName);
		
		return "mainPage";
	}
	
	//카테고리별 상품 리스트 페이지
	@RequestMapping("/ProductList/{categoryCode}")
	public String productByCategory(@PathVariable("categoryCode") int categoryCode, Model model, 
			@CookieValue(value="productCnt", required=false) Cookie cookieCnt, HttpServletResponse response) {

		//카테고리 항목
	    List<CategoryVO> categoryVOList = pm.getCategory();
	    model.addAttribute("categories", categoryVOList);
	      
	    //해당 카테고리의 상품 총 갯수 쿠키에 저장
	    String productCntByCategory = Integer.toString(pm.getCount(categoryCode));
	    if (cookieCnt==null) {
	    	cookieCnt = new Cookie("productCnt", productCntByCategory);
	    } else if (!cookieCnt.getValue().equals(productCntByCategory)) {
	    	cookieCnt.setValue(productCntByCategory);
	    }
	    response.addCookie(cookieCnt);
	      
	    return "/ProductList";
	}
	
	//ProductList 페이징처리
	@RequestMapping(value="/ProductList/paging/{categoryCode}", method=RequestMethod.POST)
	@ResponseBody
	public List<ProductVO> productPaging(@PathVariable("categoryCode") int categoryCode, @RequestBody HashMap<String, Object> reqHm) {
		log.info(reqHm);
		page = new PageDTO(Integer.parseInt(reqHm.get("page").toString()), 6);
		List<ProductVO> productVOList = pm.getListByCategory(page.getOffset(), categoryCode);
		
		return productVOList;
	}
	
	@GetMapping("/ProductUpload")
	public void toUploadPage(Model model, HttpSession session) {
		
		String CompanyName = "";
		String customerName = "";
		Long customerCode = (Long) session.getAttribute("customerCode");
		
		if (customerCode!=null) {
			CompanyName = customerService.getCompanyName(customerCode);
			customerName = customerService.getCustomerName(customerCode);
		}
		
		List<CategoryVO> category = null;
		category = pm.getCategory();
		model.addAttribute("category", JSONArray.fromObject(category));
		model.addAttribute("CompanyName", CompanyName);
		model.addAttribute("customerName", customerName);
	}
	
	@PostMapping("/ProductUpload")
	public String toUploadPage(ProductVO p, MultipartFile file) throws Exception{
		
		//이미지 등록 메소드
		String imgUploadPath = uploadPath + File.separator + "imgUpload";
		String ymdPath = UploadFileUtils.calcPath(imgUploadPath); //년월일 폴더경로
		String fileName = null;

		if(file.getOriginalFilename() != null && file.getOriginalFilename() != "") {
		 fileName =  UploadFileUtils.fileUpload(imgUploadPath, file.getOriginalFilename(), file.getBytes(), ymdPath); 
		} else {
		 fileName = uploadPath + File.separator + "images" + File.separator + "none.png";
		}
		
		p.setImage_url(File.separator + "imgUpload" + ymdPath + File.separator + fileName);
		p.setThumbnail_url(File.separator + "imgUpload" + ymdPath + File.separator + "s" + File.separator + "s_" + fileName);
		//이미지 등록 메소드 end
		
		pm.register(p);
		
		return "redirect:/ProductList/1";
	}
	
	@GetMapping("/ProductDetail/{product_code}")
	public String toDetail(@PathVariable("product_code") int product_code ,Model model, HttpSession session) {
		
		String CompanyName = "";
		String customerName = "";
		Long customerCode = (Long) session.getAttribute("customerCode");

		Map map = new HashMap<>();
		if (customerCode!=null) {
			List<HashMap<String, Integer>> rl = rm.getReviewList(customerCode, product_code);
			CompanyName = customerService.getCompanyName(customerCode);
			customerName = customerService.getCustomerName(customerCode);
			map.put("product_code", product_code);
			map.put("customer_code", customerCode);
			
			model.addAttribute("CompanyName", CompanyName);
			model.addAttribute("customerName", customerName);
			model.addAttribute("getOrderDetailCode", rm.getOrderDetailCode(customerCode, product_code));
			model.addAttribute("getReviewList", ReviewListUtils.getReviewList(rl));
		} else if (customerCode == null) {
			ArrayList<Integer> sample = new ArrayList<>();
			model.addAttribute("getReviewList", sample);
		}
		
		DetailVO d = pm.getById(product_code);
		model.addAttribute("ProductById", d);
		model.addAttribute("score", rm.getScore(product_code));
		return "/ProductDetail";
	}
	
	@GetMapping("/ProductModify/{product_code}")
	public String toModifyPage(@PathVariable("product_code") int product_code, Model model) {
		
		//占쏙옙占쏙옙占쏙옙 占쌔븝옙
		DetailVO d = pm.getById(product_code);
		model.addAttribute("ModifyProduct", d);
		
		List<CategoryVO> category = null;
		category = pm.getCategory();
		model.addAttribute("category", JSONArray.fromObject(category));
		
		return "/ProductModify";
	}
	
	@PostMapping("/ProductModify/{product_code}")
	public String toModifyPage(@PathVariable("product_code") int product_code, ProductVO p) {
		
		//占쏙옙占쏙옙占쏙옙占쏙옙
		pm.ProductModify(p);
		
		return "redirect:/ProductList/1";
	}
	
	@PostMapping("/Delete")
	public String toDelete(int product_code) {
		
		pm.ProductDelete(product_code);
		
		return "redirect:/ProductList/1"; //占쏙옙占쏙옙占쏙옙占쏙옙占쏙옙占쌔억옙占쏙옙
	}
	
	//마이페이지-마이페이지 초기화면은 주문목록
	@RequestMapping("/myPage/order/list")
	public String orderList(HttpSession session, Model model, 
			@CookieValue(value="orderDoneCnt", required=false) Cookie cookieCnt, HttpServletResponse response) {
		log.info("\n====================여기는 마이페이지 주문목록 페이지=======================");
		
		String cnt = Integer.toString(orderService.getOrderCnt(Integer.parseInt(session.getAttribute("customerCode").toString()), "done"));
		//주문 완료된 총 개수를 쿠키로 저장.
		if (cookieCnt==null) {
			log.info("쿠키가 존재하지 않음.");
			cookieCnt = new Cookie("orderDoneCnt", cnt);
		} else if (cookieCnt.getValue()!=cnt) {
			log.info("쿠키가 동일하지 않음.");
			cookieCnt.setValue(cnt);
		}
		response.addCookie(cookieCnt);
//		Integer customerCode = Integer.parseInt(session.getAttribute("customerCode").toString());
//		page = new PageVO(0, 5, orderService.getOrderCnt(Integer.parseInt(session.getAttribute("customerCode").toString()), "done"));
//		List<HashMap<String, Object>> orDoneInfo = orderService.getOrderListLimit(customerCode, "done", page);
//		//List<HashMap<String, Object>> orDoneInfo = orderService.getOrderList(customerCode, "done");
//		log.info(orDoneInfo.toString());
//		
//		model.addAttribute("orderInfo", orDoneInfo);
		
		return "myPage/orderList";
	}
	
	@RequestMapping("/myPage/basket")
	public String basket() {
		
		return "myPage/basket";
	}
	
	//접근 불가능 오류 페이지
	@RequestMapping("/error/accessDenied")
	public String accessDenied() {
		
		return "accessDenied";
	}
}