package io.renren.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2021-07-24 20:09:59
 */
@Data
@TableName("baidu_res")
public class BaiduResEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 
	 */
	private Integer fileId;
	/**
	 * 
	 */
	private Integer seq;
	/**
	 * 
	 */
	private String name;
	/**
	 * 
	 */
	private String sex;
	/**
	 * 
	 */
	private String idNo;
	/**
	 * 
	 */
	private String mobile;
	/**
	 * 
	 */
	private String extInfo;

}
