package org.zerock.service;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.domain.Criteria;
import org.zerock.domain.ReplyPageVO;
import org.zerock.domain.ReplyVO;
import org.zerock.mapper.ReplyMapper;

import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class ReplyServiceImpl implements ReplyService{
	
	@Setter(onMethod_ = @Autowired)
	private ReplyMapper rm;
	
	@Override
	public int insert(ReplyVO r) {
		
		return rm.insert(r);
	}
	
	@Override
	public ReplyVO read(int review_code) {
		
		return rm.read(review_code);
	}
	
	@Override
	public int update(ReplyVO r) {
		
		return rm.update(r);
	}
	
	@Override
	public int delete(int review_code) {
		
		return rm.delete(review_code);
	}
	
	@Override
	public List<ReplyVO> getListWithPaging(Criteria cri, int product_code) {
		
		return rm.getListWIthPaging(cri, product_code);
	}
	
	@Override
	public ReplyPageVO getListPage(Criteria cri, int product_code) {
		
		return new ReplyPageVO(rm.getCountByProductCode(product_code), rm.getListWIthPaging(cri, product_code));
	}

	@Override
	public int CustomerReply(Map map) {
		
		return rm.CustomerReply(map);
	}
	
	@Override
	public int OrderStatusIsDone(Map map) {
		
		return rm.OrderStatusIsDone(map);
	}
	
	@Override
	public int getOrderCode(Map map) {
		
		return rm.getOrderCode(map);
	}
}
