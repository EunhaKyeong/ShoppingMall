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
import org.zerock.service.ProductServiceImpl;

import lombok.extern.log4j.Log4j;
import net.sf.json.JSONArray;

@Controller
@Log4j
public class MainController {
	@Resource
	private ProductServiceImpl pm;

	@RequestMapping("/")
	public String toMainPage() {
		
		return "/mainPage";
	}
	
	//ī�װ��� ��ǰ ����Ʈ ������
	@RequestMapping("/ProductList/{categoryCode}")
	public String productByCategory(@PathVariable("categoryCode") int categoryCode, Model model) {
		//ī�װ� �׸�
		List<CategoryVO> categoryVOList = pm.getCategory();
		model.addAttribute("categories", categoryVOList);
		
		//��ǰ ����
		int pageNum = pm.getCount(categoryCode)/6+1;
		model.addAttribute("pageNum", pageNum);
		
		//getListByCategory ���������� �ؽ���
		HashMap parameterHm = new HashMap();
		parameterHm.put("categoryCode", categoryCode);
		parameterHm.put("startIdx", 0);
		
		//��ǰ����Ʈ-1������
		List<ProductVO> productVOList = pm.getListByCategory(parameterHm);
		model.addAttribute("products", productVOList);
		
		return "/ProductList";
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
		category = pm.getCategory();
		model.addAttribute("category", JSONArray.fromObject(category));
	}
	
	@PostMapping("/ProductUpload")
	public String toUploadPage(ProductVO p) throws Exception{
		
		pm.register(p);
		
		return "redirect:/ProductList";
	}
}
