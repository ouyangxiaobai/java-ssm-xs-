package com.cms.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.cms.entity.Teacher;
import com.cms.service.TeacherService;
import com.cms.utils.MD5Util;
import com.cms.utils.StrUtil;
import com.cms.utils.page.Pagination;

@Controller
@RequestMapping(value="/teacher")
public class TeacherController {
	@Autowired
	private TeacherService teacherService;
	
	@ResponseBody
	@RequestMapping(value="/list")
	public String getTeacherList(@RequestParam(defaultValue="0")int curr,@RequestParam(defaultValue="10")int nums,
			@RequestParam(defaultValue="")String searchKey) {
		Pagination<Teacher> page = new Pagination<Teacher>();
		
		page.setTotalItemsCount(teacherService.getTotalItemsCount(searchKey));
		page.setPageSize(nums);
		page.setPageNum(curr);
		List<Teacher> list = teacherService.getTeacher(page, searchKey);
		
		
		String jsonStr = StrUtil.RETURN_JONS_PRE_STR + page.getTotalItemsCount() 
				+ StrUtil.RETURN_JONS_MID_STR
				+ JSON.toJSONString(list) + StrUtil.RETURN_JONS_END_STR;
		return jsonStr;
	}
	
	@ResponseBody
	@RequestMapping(value="/listForSelect")
	public String getTeacherListForSelect(@RequestParam(defaultValue="") String searchKey) {
		List<Teacher> list = teacherService.getTeacherForSelect(searchKey);
		String jsonStr = StrUtil.RETURN_JONS_PRE_STR + list.size() 
				+ StrUtil.RETURN_JONS_MID_STR
				+ JSON.toJSONString(list) + StrUtil.RETURN_JONS_END_STR;
		return jsonStr;
	}
    
	@RequestMapping(value="/addPage")
	public ModelAndView toAddPage() {
		return new ModelAndView("/teacherAdd");
	}
	
	/**
	 * ?????????????????????teacher
	 * @param opType
	 * @param teacher
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/add")
	public String addTeacher(@RequestParam(defaultValue="2") int opType, Teacher teacher) {
		int res = 0;
		if (opType == 0) {
			try {
				teacher.setPassword(teacher.getPassword().toUpperCase());
				res = teacherService.addTeacher(teacher);
			} catch (Exception e) {
				System.out.println("??????????????????????????????");
				return "??????????????????????????????";
			}
			if (res > 0)
				return StrUtil.RESULT_TRUE;
			return "????????????";
		} else if (opType == 1) {
			teacher.setPassword(null);
			res = teacherService.updateTeacher(teacher);
			if (res > 0) return StrUtil.RESULT_TRUE;
			return "???????????????";
		}
		return "error";
	}
	
	/**
	 * ????????????
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/resetPswd")
	public String resetPasswrd(String id) {
		Teacher teacher = new Teacher();
		teacher.setId(id);
		teacher.setPassword(MD5Util.MD5("123456"));
		if (teacherService.updateTeacher(teacher) > 0) return StrUtil.RESULT_TRUE;
		return "???????????????";
	}
	
	@ResponseBody
	@RequestMapping(value="/delete")
	public String deleteStudnet(Teacher t) {
		if (teacherService.deleteTeacher(t) > 0) return StrUtil.RESULT_TRUE;
		return "???????????????";
	}
	
	/**
	 * ????????????
	 * @param tIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/deleteList")
	public String deleteStudnetList(String tIds) {
		List<String> list = new ArrayList<String>();
		try {
			String[] ids = tIds.split(",");
			for (String id: ids) {
				list.add(id);
			}
			if (teacherService.deleteTeacher(list) > 0) {
				return StrUtil.RESULT_TRUE;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "??????????????????????????????";//
		}
		return "???????????????";
	}
	
	@ResponseBody
	@RequestMapping("/import")  
	public String impotr(HttpServletRequest request, MultipartFile file) {  
	     //?????????????????????  
	     InputStream in = null;
		try {
			in = file.getInputStream();
			//????????????  
			int res = teacherService.importExcelInfo(in,file);
			if (res > 0) {
				return StrUtil.RETURN_JONS_PRE_STR+"0"
						+StrUtil.RETURN_JONS_MID_STR+"true"
						+StrUtil.RETURN_JONS_END_STR;
			} else {
				return StrUtil.RETURN_JONS_PRE_STR+"0"
						+StrUtil.RETURN_JONS_MID_STR+"false"
						+StrUtil.RETURN_JONS_END_STR;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return StrUtil.RETURN_JONS_PRE_STR+"0"
			+StrUtil.RETURN_JONS_MID_STR+"error"
			+StrUtil.RETURN_JONS_END_STR;
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
}
