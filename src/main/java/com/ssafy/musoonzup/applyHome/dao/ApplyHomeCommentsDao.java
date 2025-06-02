package com.ssafy.musoonzup.applyHome.dao;

import org.apache.ibatis.annotations.Mapper;
import com.ssafy.musoonzup.applyHome.dto.*;

@Mapper
public interface ApplyHomeCommentsDao {
	ApplyHomeComments selectByApplyIdx(Long applyIdx);
	int insert(ApplyHomeComments applyHomeComments);
}
