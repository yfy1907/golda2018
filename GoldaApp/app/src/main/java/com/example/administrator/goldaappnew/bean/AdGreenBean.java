package com.example.administrator.goldaappnew.bean;

import java.io.Serializable;

public class AdGreenBean implements Serializable{

	// 'board_id': '13', 
	// 'gis_x': '118.79054260253906', 
	// 'gis_y': '32.056941986083984',
	// 'icon': 'big_led', 
	// 'intro': '宏图地产', 
	// 'bd_sn': '2', 
	// 'company': '南京市金桥广告有限公司',
	// 'shortname': '金桥', 
	// 'address': '江苏省南京市鼓楼区中山路226号(所在商圈:南师大,南大)', 
	// 'status':
	// '1', 'belongto': ''

	private String board_id;
	private String gis_x;
	private String gis_y;
	private String icon;
	private String intro;
	private String bd_sn;
	private String company;
	private String shortname;
	private String address;
	private String status;
	private String belongto;
	private String province;
	private String dateline;
	private String city;
	private String area;
	private String process_contact;		//联系人
	private String process_tel;			//联系电话
	private String gis_pic;		// 巡查图片
	private String b_attach_2; //建档图片
	private String icon_type;
	private String icon_class;
	private String icon_cnname;

//	private String lid = "";
//	private String xid = "";
	private String uid = "";
	private String people_find = "";
	private String question = "";

	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getB_attach_2() {
		return b_attach_2;
	}

	public void setB_attach_2(String b_attach_2) {
		this.b_attach_2 = b_attach_2;
	}

	public String getGis_pic() {
		return gis_pic;
	}

	public void setGis_pic(String gis_pic) {
		this.gis_pic = gis_pic;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	private String level = "";

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getPeople_find() {
		return people_find;
	}

	public void setPeople_find(String people_find) {
		this.people_find = people_find;
	}

	public String getIcon_type() {
		return icon_type;
	}

	public void setIcon_type(String icon_type) {
		this.icon_type = icon_type;
	}

	public String getIcon_class() {
		return icon_class;
	}

	public void setIcon_class(String icon_class) {
		this.icon_class = icon_class;
	}

	public String getIcon_cnname() {
		return icon_cnname;
	}

	public void setIcon_cnname(String icon_cnname) {
		this.icon_cnname = icon_cnname;
	}



	public String getProcess_tel() {
		return process_tel;
	}

	public void setProcess_tel(String process_tel) {
		this.process_tel = process_tel;
	}

	public String getDateline() {
		return dateline;
	}

	public void setDateline(String dateline) {
		this.dateline = dateline;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getBoard_id() {
		return board_id;
	}

	public void setBoard_id(String board_id) {
		this.board_id = board_id;
	}

	public String getGis_x() {
		return gis_x;
	}

	public void setGis_x(String gis_x) {
		this.gis_x = gis_x;
	}

	public String getGis_y() {
		return gis_y;
	}

	public void setGis_y(String gis_y) {
		this.gis_y = gis_y;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getBd_sn() {
		return bd_sn;
	}

	public String getProcess_contact() {
		return process_contact;
	}

	public void setProcess_contact(String process_contact) {
		this.process_contact = process_contact;
	}


	public void setBd_sn(String bd_sn) {
		this.bd_sn = bd_sn;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBelongto() {
		return belongto;
	}

	public void setBelongto(String belongto) {
		this.belongto = belongto;
	}

	@Override
	public String toString() {
		return "AdGreenBean{" +
				"board_id='" + board_id + '\'' +
				", gis_x='" + gis_x + '\'' +
				", gis_y='" + gis_y + '\'' +
				", icon='" + icon + '\'' +
				", intro='" + intro + '\'' +
				", bd_sn='" + bd_sn + '\'' +
				", company='" + company + '\'' +
				", shortname='" + shortname + '\'' +
				", address='" + address + '\'' +
				", status='" + status + '\'' +
				", belongto='" + belongto + '\'' +
				", province='" + province + '\'' +
				", dateline='" + dateline + '\'' +
				", city='" + city + '\'' +
				", area='" + area + '\'' +
				", uid='" + uid + '\'' +
				", process_contact='" + process_contact + '\'' +
				", process_tel='" + process_tel + '\'' +
				", gis_pic='" + gis_pic + '\'' +
				'}';
	}
}
