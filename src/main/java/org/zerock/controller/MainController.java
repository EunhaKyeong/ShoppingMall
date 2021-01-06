package org.zerock.controller;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zerock.domain.CategoryVO;
import org.zerock.domain.ProductVO;
import org.zerock.service.ProductService;
import org.zerock.service.ProductServiceImpl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import net.sf.json.JSONArray;

@Controller
@Log4j
public class MainController {

	@Resource
	private ProductService ps;

	@RequestMapping("/")
	public String toMainPage() {
		
		return "/mainPage";
	}
	
	@GetMapping("/ProductList")
	public void toProductList(Model model) {
		
		List<CategoryVO> categoryVOList = ps.getCategory();

	//ī�װ��� ��ǰ ����Ʈ ������
	@RequestMapping("/ProductList/{categoryCode}")
	public String productByCategory(@PathVariable("categoryCode") int categoryCode, Model model) {
		//ī�װ� �׸�
		List<CategoryVO> categoryVOList = ps.getCategory();
		model.addAttribute("categories", categoryVOList);
		
		List<ProductVO> productVOList = ps.getList();
		System.out.println(productVOList.get(0).getProduct_price());

		//��ǰ ����
		int pageNum = pm.getCount(categoryCode)/6+1;
		model.addAttribute("pageNum", pageNum);
		
		//getListByCategory ���������� �ؽ���
		HashMap parameterHm = new HashMap();
		parameterHm.put("categoryCode", categoryCode);
		parameterHm.put("startIdx", 0);
		
		//��ǰ����Ʈ-1������
		List<ProductVO> productVOList = ps.getListByCategory(parameterHm);
		model.addAttribute("products", productVOList);

	}
	
	@RequestMapping(value="/ProductList/paging", method=RequestMethod.POST)
	@ResponseBody
	public List<ProductVO> productPaging(@RequestBody HashMap<String, Object> dataTransfer) {
		List<ProductVO> productVOList = pm.getListByCategory(dataTransfer);
		
		return productVOList;
	}
	
	@GetMapping("/ProductUpload")
	public void toUploadPage(Model model) {
		
		List<CategoryVO> category = null;
		category = ps.getCategory();
		model.addAttribute("category", JSONArray.fromObject(category));
	}
	
	@PostMapping("/ProductUpload")
	public String toUploadPage(ProductVO p) throws Exception{
		
		ps.register(p);
		
		return "redirect:/ProductList";
	}
}
